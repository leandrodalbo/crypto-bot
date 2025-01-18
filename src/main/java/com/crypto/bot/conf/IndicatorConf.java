package com.crypto.bot.conf;

import com.tictactec.ta.lib.Core;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IndicatorConf {

    @Bean
    public Core indicatorCore() {
        return new Core();
    }
}
