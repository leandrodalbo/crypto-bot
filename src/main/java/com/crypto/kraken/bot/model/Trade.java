package com.crypto.kraken.bot.model;

public record Trade(boolean isOpen, TradingPair pair, float stop, float profit, long timestamp) {
    @Override
    public String toString() {
        return String.format("asset: %s, stop:%f, profit:%s", pair.key(), stop, profit);
    }
}
