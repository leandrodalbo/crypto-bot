package com.crypto.kraken.bot.props;

import com.crypto.kraken.bot.model.CandlesSinceUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kraken")
public record ClientProps(String url, String key, String secret, int candlesInterval, int candlesSince, CandlesSinceUnit candlesSinceUnit) {}
