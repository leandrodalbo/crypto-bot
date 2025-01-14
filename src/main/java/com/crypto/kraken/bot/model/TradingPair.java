package com.crypto.kraken.bot.model;

public record TradingPair(String key, String to) {

    @Override
    public String toString() {
        return key + to;
    }
}
