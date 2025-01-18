package com.crypto.bot.krakenResponse;

import java.util.Map;

public record KrakenOrderResponse(String[] error, Map<String, Object> result) {}
