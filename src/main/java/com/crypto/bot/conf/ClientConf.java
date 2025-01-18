package com.crypto.bot.conf;

import com.crypto.bot.props.ClientProps;
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
