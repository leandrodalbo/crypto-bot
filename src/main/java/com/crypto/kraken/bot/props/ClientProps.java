package com.crypto.kraken.bot.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kraken")
public record ClientProps(String url, String key, String secret, int candlesInterval, int sinceDays) {}
