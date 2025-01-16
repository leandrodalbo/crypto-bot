package com.crypto.kraken.bot.model;

import static com.crypto.kraken.bot.utils.BotUtils.botFormatDouble;

public record Candle(Double open,
                     Double high,
                     Double low,
                     Double close,
                     Double volume) {

    public Double formattedOpen() {
        return botFormatDouble(open);
    }

    public Double formattedHigh() {
        return botFormatDouble(high);
    }

    public Double formattedLow() {
        return botFormatDouble(low);
    }

    public Double formattedClose() {
        return botFormatDouble(close);
    }

    public Double formattedVolume() {
        return botFormatDouble(volume);
    }
}
