package com.crypto.bot.utils;

import com.crypto.bot.model.Candle;

import java.util.List;

public class BotUtils {
    public static double botFormatDouble(double number) {
        return Double.parseDouble(String.format("%.5f", number));
    }

    public static Candle[] toCandlesArray(List<Candle> candles) {
        Candle[] result = new Candle[candles.size()];

        for (int i = 0; i < candles.size(); i++) {
            result[i] = candles.get(i);
        }
        return result;
    }
}
