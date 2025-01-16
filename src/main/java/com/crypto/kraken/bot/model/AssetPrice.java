package com.crypto.kraken.bot.model;

import static com.crypto.kraken.bot.utils.BotUtils.botFormatDouble;

public record AssetPrice(String name, double usd) {
    public double formattedUSD(){
        return botFormatDouble(usd());
    }
}
