package com.crypto.kraken.bot.conf;

import com.crypto.kraken.bot.running.Bot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;


@Configuration
public class BotConf implements SchedulingConfigurer {
    private final Bot bot;

    @Value("${bot.validationCron}")
    private String validationCron;

    @Value("${bot.newTradeCron}")
    private String newTradeCron;

    public BotConf(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addCronTask(bot::newTrade, newTradeCron);
        taskRegistrar.addCronTask(bot::checkOpenTrade, validationCron);
    }
}
