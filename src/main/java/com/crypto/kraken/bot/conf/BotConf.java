package com.crypto.kraken.bot.conf;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@ConfigurationPropertiesScan
public class BotConf {

    @Bean
    public RestClient restClient(RestClient.Builder builder, MainConfProps props) {
        return builder.baseUrl(props.url()).build();
    }
}
