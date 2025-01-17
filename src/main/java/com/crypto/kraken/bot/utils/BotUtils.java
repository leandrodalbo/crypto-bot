package com.crypto.kraken.bot.utils;

public class BotUtils {
    public static double botFormatDouble(double number) {
        return Double.parseDouble(String.format("%.5f", number));
    }
}
