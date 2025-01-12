package com.crypto.kraken.bot.krakenResponse;

import java.util.Map;

public record KrakenOHLCResponse(String[] error, Map<String, Object> result) {}
