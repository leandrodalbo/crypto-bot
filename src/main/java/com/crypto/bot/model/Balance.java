package com.crypto.bot.model;

import java.util.HashMap;
import java.util.Map;

import static com.crypto.bot.utils.BotUtils.botFormatDouble;

public record Balance(Map<String, Double> values) {

    public Map<String, Double> formattedValuesMap() {
        Map<String, Double> result = new HashMap<>();

        this.values()
                .entrySet()
                .forEach(it -> result.put(it.getKey(), botFormatDouble(it.getValue())));

        return result;
    }
}
