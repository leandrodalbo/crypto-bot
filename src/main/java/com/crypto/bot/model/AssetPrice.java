package com.crypto.bot.model;

import static com.crypto.bot.utils.BotUtils.botFormatDouble;

public record AssetPrice(String name, double usd) {
    public double formattedUSD(){
        return botFormatDouble(usd());
    }
}
