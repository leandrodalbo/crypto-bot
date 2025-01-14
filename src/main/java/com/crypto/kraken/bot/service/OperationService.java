package com.crypto.kraken.bot.service;

import com.crypto.kraken.bot.component.KrakenClient;
import com.crypto.kraken.bot.component.TradeWrapper;
import com.crypto.kraken.bot.conf.OperationConf;
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

@Service
public class OperationService {
    private final Logger logger = LoggerFactory.getLogger(OperationService.class);

    private final OperationConf operationConf;
    private final KrakenClient krakenClient;
    private final TradeWrapper tradeWrapper;

    public OperationService(OperationConf operationConf, KrakenClient krakenClient, TradeWrapper tradeWrapper) {
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
                .forEach(pair -> result.put(pair.toString(), krakenClient.ohlcData(pair)));

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

    public void openTrade(TradingPair pair) throws NoSuchAlgorithmException, InvalidKeyException {
        Balance balance = krakenClient.balance();

        float usdBalance = formatFloat(balance.values().get(this.operationConf.currency()));

        if (tradeWrapper.canTrade() && usdBalance > formatFloat(this.operationConf.notBelow())) {
            AssetPrice assetPrice = krakenClient.assetPrice(pair);
            double volume = formatDouble(usdBalance / assetPrice.usd());

            if (krakenClient.postOrder(pair, volume, BuySell.buy)) {
                float sl = formatFloat(assetPrice.usd() * (1 - this.operationConf.stop()));
                float tp = formatFloat(assetPrice.usd() * (1 + this.operationConf.profit()));

                tradeWrapper.setTrade(Optional.of(new Trade(true, pair, sl, tp, Instant.now().toEpochMilli())));

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

            double volume = formatDouble(balance.values().get(trade.pair().key()));

            if (krakenClient.postOrder(trade.pair(), volume, BuySell.sell)) {
                tradeWrapper.setTrade(Optional.empty());
                logger.info(String.format("Closed Trade: %s", trade));
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

            float price = formatFloat(assetPrice.usd());

            if (trade.timestamp() < limit) {
                closeTrade();
            }

            if(price <= trade.stop()){
                closeTrade();
            }

            if(price >= trade.profit()){
                closeTrade();
            }
        }
    }

    public Trade tradeStatus(){
        return tradeWrapper.getTrade().orElse(new Trade(false, null, 0,0, Instant.now().toEpochMilli()));
    }

    public boolean canOperate() {
        return tradeWrapper.canTrade();
    }

    private float formatFloat(float number) {
        return Float.parseFloat(String.format("%.3f", number));
    }

    private double formatDouble(double number) {
        return Double.parseDouble(String.format("%.3f", number));
    }
}
