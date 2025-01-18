package com.crypto.bot.krakenResponse;

import java.util.Map;

public record KrakenBalanceResponse(String[] error, Map<String, String> result) {}
