package com.crypto.kraken.bot.model;

import static com.crypto.kraken.bot.utils.BotUtils.botFormatDouble;

public record Trade(boolean isOpen, TradingPair pair, double stop, double profit, long timestamp) {

    public double formattedStop() {
        return botFormatDouble(stop);
    }

    public double formattedProfit() {
        return botFormatDouble(profit);
    }

    @Override
    public String toString() {
        return String.format("asset: %s, stop:%f, profit:%s", pair.key(), stop, profit);
    }
}
