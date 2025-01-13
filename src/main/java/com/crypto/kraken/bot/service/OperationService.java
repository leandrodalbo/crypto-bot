package com.crypto.kraken.bot.service;

import com.crypto.kraken.bot.component.KrakenClient;
import com.crypto.kraken.bot.conf.OperationConf;
import com.crypto.kraken.bot.model.Balance;
import com.crypto.kraken.bot.model.Candle;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OperationService {

    private final OperationConf operationConf;
    private final KrakenClient krakenClient;

    public OperationService(OperationConf operationConf, KrakenClient krakenClient) {
        this.operationConf = operationConf;
        this.krakenClient = krakenClient;
    }

    public Map<String, List<Candle>> fetchCandles() {
        Map<String, List<Candle>> result = new HashMap<String, List<Candle>>();

        this.operationConf
                .pairs()
                .forEach(pair -> result.put(pair, krakenClient.ohlcData(pair, 60, Instant.now().minus(10, ChronoUnit.DAYS).toEpochMilli())));

        return result;

    }

    public Balance getBalance() throws NoSuchAlgorithmException, InvalidKeyException {
        return krakenClient.balance();
    }
}
