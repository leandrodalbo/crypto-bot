package com.crypto.bot.model;

import java.util.HashMap;
import java.util.Map;

import static com.crypto.bot.utils.BotUtils.botFormatDouble;
import static com.crypto.bot.utils.BotUtils.formatBalanceKey;

public record Balance(Map<String, Double> values) {

    public Map<String, Double> formattedValuesMap() {
        Map<String, Double> result = new HashMap<>();

        this.values()
                .entrySet()
                .forEach(it -> result.put(formatBalanceKey(it.getKey()), botFormatDouble(it.getValue())));

        return result;
    }
}
