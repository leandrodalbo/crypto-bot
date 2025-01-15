package com.crypto.kraken.bot.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties("operation")
public record OperationProps(Map<String, String> pairs, float stop, float profit, String currency, float notBelow, int minutesLimit) {}
