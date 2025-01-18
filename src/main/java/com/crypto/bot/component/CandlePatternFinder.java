package com.crypto.bot.component;

import com.crypto.bot.model.Candle;
import org.springframework.stereotype.Component;

@Component
public class CandlePatternFinder {

    public boolean isEngulfingCandleBuy(Candle[] candles) {

        if (candles.length < 3)
            return false;

        return isBullishEngulfing(candles[candles.length - 2], candles[candles.length - 1]) || isBullishEngulfing(candles[candles.length - 3], candles[candles.length - 2]);
    }

    private boolean isBullishEngulfing(Candle previousCandle, Candle currentCandle) {
        return previousCandle.formattedClose() < previousCandle.formattedOpen() &&
                currentCandle.formattedClose() > currentCandle.formattedOpen() &&
                currentCandle.formattedOpen() < previousCandle.formattedClose() &&
                currentCandle.formattedClose() > previousCandle.formattedOpen();
    }
}
