package com.crypto.kraken.bot.service;

import com.crypto.kraken.bot.component.KrakenClient;
import com.crypto.kraken.bot.component.TradeWrapper;
import com.crypto.kraken.bot.props.OperationProps;
import com.crypto.kraken.bot.model.Trade;
import com.crypto.kraken.bot.model.Candle;
import com.crypto.kraken.bot.model.Balance;
import com.crypto.kraken.bot.model.AssetPrice;
import com.crypto.kraken.bot.model.TradingPair;
import com.crypto.kraken.bot.model.BuySell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.crypto.kraken.bot.utils.BotUtils.botFormatDouble;

@Service
public class OperationService {
    private final Logger logger = LoggerFactory.getLogger(OperationService.class);

    private final OperationProps operationConf;
    private final KrakenClient krakenClient;
    private final TradeWrapper tradeWrapper;

    public OperationService(OperationProps operationConf, KrakenClient krakenClient, TradeWrapper tradeWrapper) {
        this.operationConf = operationConf;
        this.krakenClient = krakenClient;
        this.tradeWrapper = tradeWrapper;
    }

    public Map<String, List<Candle>> fetchCandles() {
        Map<String, List<Candle>> result = new HashMap<String, List<Candle>>();

        this.operationConf
                .pairs()
                .entrySet()
                .stream().map(it -> new TradingPair(it.getKey(), it.getValue()))
                .forEach(pair -> result.put(pair.key(), krakenClient.ohlcData(pair)));

        return result;
    }

    public Balance getBalance() throws NoSuchAlgorithmException, InvalidKeyException {
        return krakenClient.balance();
    }

    public List<AssetPrice> assetsPrice() {
        return this.operationConf
                .pairs()
                .entrySet()
                .stream().map(it -> krakenClient.assetPrice(new TradingPair(it.getKey(), it.getValue()))).toList();
    }

    public void openTrade(String pairKey) throws NoSuchAlgorithmException, InvalidKeyException {
        Balance balance = krakenClient.balance();

        Double usdBalance = balance.formattedValuesMap().get(this.operationConf.currency());
        Double notBelow = this.operationConf.formattedNotBelow();

        if(this.operationConf.pairs().get(pairKey) == null){
            logger.info("Pair Configuration missing");
        }

        if(usdBalance == null || notBelow == null){
            logger.info("Currency or notBelow Parameters missing");
        }

        TradingPair pair = new TradingPair(pairKey, this.operationConf.pairs().get(pairKey));

        if (tradeWrapper.canTrade() && usdBalance.doubleValue() > notBelow.doubleValue()) {
            AssetPrice assetPrice = krakenClient.assetPrice(pair);
            double volume = botFormatDouble(usdBalance / assetPrice.formattedUSD());

            if (krakenClient.postOrder(pair, volume, BuySell.buy)) {

                double stopLoss = (assetPrice.formattedUSD() * (1 - this.operationConf.formattedStop()));
                double takeProfit = (assetPrice.formattedUSD() * (1 + this.operationConf.formattedProfit()));

                tradeWrapper.setTrade(Optional.of(new Trade(true, pair, stopLoss, takeProfit, Instant.now().toEpochMilli())));

                logger.info(String.format("Open Trade: %s", tradeWrapper.getTrade()));

            } else {
                logger.warn(String.format("Failed to open trade for: %s", pair));
            }
        }
    }

    public void closeTrade() throws NoSuchAlgorithmException, InvalidKeyException {
        if (!tradeWrapper.canTrade()) {
            Balance balance = krakenClient.balance();
            Trade trade = tradeWrapper.getTrade().get();

            double volume = balance.formattedValuesMap().get(trade.pair().key());

            logger.info(String.format("Closing Trade: %s", trade.pair().key()));

            if (krakenClient.postOrder(trade.pair(), volume, BuySell.sell)) {
                tradeWrapper.setTrade(Optional.empty());
            } else {
                logger.warn(String.format("Failed to close trade for: %s", trade.pair()));
            }
        }
    }

    public void validateTrade() throws NoSuchAlgorithmException, InvalidKeyException {
        if (!tradeWrapper.canTrade()) {
            Trade trade = tradeWrapper.getTrade().get();
            long limit = Instant.now().minus(this.operationConf.minutesLimit(), ChronoUnit.MINUTES).toEpochMilli();
            AssetPrice assetPrice = krakenClient.assetPrice(trade.pair());

            if ((trade.timestamp() < limit) || (assetPrice.formattedUSD() <= trade.formattedStop()) || (assetPrice.formattedUSD() >= trade.formattedProfit())) {
                closeTrade();
                logger.info(String.format("Closed Trade: %s", trade.pair().key()));
            }
        }
    }

    public Trade tradeStatus() {
        return tradeWrapper.getTrade().orElse(new Trade(false, null, 0, 0, Instant.now().toEpochMilli()));
    }

    public boolean canOperate() {
        return tradeWrapper.canTrade();
    }

}
