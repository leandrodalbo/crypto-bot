package com.crypto.kraken.bot.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties("operation")
public record OperationConf(Map<String, String> pairs, float stop, float profit, String currency, float notBelow, int minutesLimit) {}
