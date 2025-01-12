package com.crypto.kraken.bot.model;

public record Candle(Float open,
                     Float high,
                     Float low,
                     Float close,
                     Float volume) {
}
