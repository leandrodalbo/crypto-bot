package com.crypto.kraken.bot.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("indicator")
public record IndicatorProps(int bbPeriod,
                             double bbStdDev,
                             int shortMA,
                             int longMA,
                             int macdFast,
                             int macdSlow,
                             int macdSignal,
                             int obvPeriod) {

}
