package com.crypto.kraken.bot.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kraken")
public record MainConfProps(String url, String key, String secret) {}
