package com.crypto.kraken.bot.conf;

import com.crypto.kraken.bot.props.ClientProps;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConf {

    @Bean
    public RestClient restClient(RestClient.Builder builder, ClientProps props) {
        return builder.baseUrl(props.url()).build();
    }
}
