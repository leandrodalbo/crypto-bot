package com.crypto.kraken.bot.service;

import com.crypto.kraken.bot.conf.OperationConf;
import com.crypto.kraken.bot.model.Candle;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OperationService {

    private final OperationConf operationConf;
    private final KrakenClientService clientService;

    public OperationService(OperationConf conf, KrakenClientService krakenClientService) {
        this.operationConf = conf;
        this.clientService = krakenClientService;
    }

    public Map<String, List<Candle>> fetchCandles() {
        Map<String, List<Candle>> result = new HashMap<String, List<Candle>>();

        this.operationConf
                .pairs()
                .forEach(pair -> result.put(pair, clientService.ohlcData(pair, 60, Instant.now().minus(10, ChronoUnit.DAYS).toEpochMilli())));

        return result;

    }
}
