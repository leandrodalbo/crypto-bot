package com.crypto.kraken.bot.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@ConfigurationProperties("operation")
public record OperationConf(Set<String> pairs) {}
