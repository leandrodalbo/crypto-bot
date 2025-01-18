package com.crypto.bot.model;

public record TradingPair(String key, String to) {

    @Override
    public String toString() {
        return key + to;
    }
}
