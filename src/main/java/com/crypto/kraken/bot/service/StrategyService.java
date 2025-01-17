package com.crypto.kraken.bot.service;

import com.crypto.kraken.bot.component.CandlePatternFinder;
import com.crypto.kraken.bot.component.CoreWrapper;
import com.crypto.kraken.bot.model.Candle;

import java.util.Arrays;

public class StrategyService {

    private final CoreWrapper coreWrapper;
    private final CandlePatternFinder candlePatterns;

    public StrategyService(CoreWrapper coreWrapper, CandlePatternFinder candlePatterns) {
        this.coreWrapper = coreWrapper;
        this.candlePatterns = candlePatterns;
    }

    public boolean isValidForTrade(double price, Candle[] candles) {
        double[] closedPrices = closedPrices(candles);
        boolean bb = coreWrapper.isBollingerBandsBuy(price, closedPrices);
        boolean macd = coreWrapper.isMACDBuy(closedPrices);
        boolean ema = coreWrapper.isEMABuy(closedPrices);
        boolean engulfing = candlePatterns.isEngulfingCandleBuy(candles);

        return ((macd && ema) || (macd && bb) || (macd && engulfing) || (bb && ema) || (bb && engulfing) || (ema && engulfing));
    }

    private double[] closedPrices(Candle[] candles) {
        return Arrays.stream(candles).mapToDouble(it -> it.formattedClose()).toArray();
    }

}
