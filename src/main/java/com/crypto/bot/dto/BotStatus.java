package com.crypto.bot.dto;

import com.crypto.bot.model.AssetPrice;
import com.crypto.bot.model.Balance;
import com.crypto.bot.model.Trade;

import java.util.List;

public record BotStatus(Balance balance, Trade trade, List<AssetPrice> latestPrice) {
}
