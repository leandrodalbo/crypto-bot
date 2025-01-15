package com.crypto.kraken.bot.component;

import com.crypto.kraken.bot.model.Candle;

public class CandlePatternFinder {

    public boolean engulfingCandleBuy(Candle[] candles) {

        if (candles.length < 3)
            return false;

        return isBullishEngulfing(candles[candles.length - 2], candles[candles.length - 1]) || isBullishEngulfing(candles[candles.length - 3], candles[candles.length - 2]);
    }

    private boolean isBullishEngulfing(Candle previousCandle, Candle currentCandle) {
        return previousCandle.close() < previousCandle.open() &&
                currentCandle.close() > currentCandle.open() &&
                currentCandle.open() < previousCandle.close() &&
                currentCandle.close() > previousCandle.open();
    }
}
