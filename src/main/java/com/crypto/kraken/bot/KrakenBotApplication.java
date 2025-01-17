package com.crypto.kraken.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class KrakenBotApplication{

    public static void main(String[] args) {
        SpringApplication.run(KrakenBotApplication.class, args);
    }

}
