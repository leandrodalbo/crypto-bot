package com.crypto.bot.props;

import com.crypto.bot.model.CandlesSinceUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kraken")
public record ClientProps(String url, String key, String secret, int candlesInterval, int candlesSince, CandlesSinceUnit candlesSinceUnit) {}
