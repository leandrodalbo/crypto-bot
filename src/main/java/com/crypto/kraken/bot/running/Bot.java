package com.crypto.kraken.bot.running;

import com.crypto.kraken.bot.model.AssetPrice;
import com.crypto.kraken.bot.model.Candle;
import com.crypto.kraken.bot.service.OperationService;
import com.crypto.kraken.bot.service.StrategyService;
import com.crypto.kraken.bot.utils.BotUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

@Component
public class Bot {
    private final Logger logger = LoggerFactory.getLogger(Bot.class);

    private final StrategyService strategyService;
    private final OperationService operationService;

    public Bot(StrategyService strategyService, OperationService operationService) {
        this.strategyService = strategyService;
        this.operationService = operationService;
    }

    public void checkOpenTrade() {
        if (!operationService.canOperate()) {
            try {
                logger.info("Trade Validation...");
                operationService.validateTrade();
            } catch (Exception e) {
                logger.warn(String.format("Trade Validation Failed: %s", e.getMessage()));
            }

        } else {
            logger.info("No Trade  to Validate...");
        }
    }

    public void newTrade() {
        if (operationService.canOperate()) {
            try {
                List<String> approvedToTrade = tradeApproved();

                if (!approvedToTrade.isEmpty()) {
                    Random random = new Random();
                   String pairKey = approvedToTrade.get(random.nextInt(approvedToTrade.size()));

                    operationService.openTrade(pairKey);

                } else {
                    logger.info("No assets found to trade...");
                }

            } catch (Exception e) {
                logger.warn(String.format("Trade Opening Failed: %s", e.getMessage()));
            }

        }
    }

    private List<String> tradeApproved() {
        List<String> result = new ArrayList<>();

        Map<String, List<Candle>> assetData = operationService.fetchCandles();
        List<AssetPrice> prices = operationService.assetsPrice();

        assetData.entrySet().forEach(it -> {
            AssetPrice assetPrice = prices
                    .stream()
                    .filter(price -> price.name().equals(it.getKey()))
                    .findFirst().get();

            Candle[] candles = BotUtils.toCandlesArray(it.getValue());

            if (strategyService.isValidForTrade(assetPrice.formattedUSD(), candles)) {
                logger.info(String.format("Analysis for %s approved", it.getKey()));
                result.add(it.getKey());
            } else {
                logger.info(String.format("Analysis for %s not approved", it.getKey()));
            }
        });
        return result;
    }
}


