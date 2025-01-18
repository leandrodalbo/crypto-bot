package com.crypto.bot.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

import static com.crypto.bot.utils.BotUtils.botFormatDouble;

@ConfigurationProperties("operation")
public record OperationProps(Map<String, String> pairs, double stop, double profit, String currency, double notBelow,
                             int minutesLimit) {
    public double formattedStop() {
        return botFormatDouble(stop);
    }

    public double formattedProfit() {
        return botFormatDouble(profit);
    }

    public double formattedNotBelow() {
        return botFormatDouble(notBelow);
    }
}
