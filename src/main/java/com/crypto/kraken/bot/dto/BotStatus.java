package com.crypto.kraken.bot.dto;

import com.crypto.kraken.bot.model.AssetPrice;
import com.crypto.kraken.bot.model.Balance;
import com.crypto.kraken.bot.model.Trade;

import java.util.List;

public record BotStatus(Balance balance, Trade trade, List<AssetPrice> latestPrice) {
}
