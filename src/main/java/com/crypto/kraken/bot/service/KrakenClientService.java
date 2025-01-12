package com.crypto.kraken.bot.service;

import com.crypto.kraken.bot.krakenResponse.KrakenOHLCResponse;
import com.crypto.kraken.bot.model.Candle;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class KrakenClientService {
    private static final String OHLC_PATH = "/0/public/OHLC";
    private final RestClient client;

    public KrakenClientService(RestClient client) {
        this.client = client;
    }

    public List ohlcData(String tradingPair, int interval, long since) {
        var data = client.get()
                .uri(uriBuilder -> uriBuilder.path(OHLC_PATH)
                        .queryParam("pair", tradingPair)
                        .queryParam("interval", interval)
                        .queryParam("since", since).build())
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .body(KrakenOHLCResponse.class);
        return toCandlesList(data);
    }

    private List toCandlesList(KrakenOHLCResponse response) {

        List data = (List) (response.result().values().stream().toList().get(0));

        return data.stream().map(it -> {
            List candleData = (List) it;
            return new Candle(Float.parseFloat((String) candleData.get(1)), Float.parseFloat((String) candleData.get(2))
                    , Float.parseFloat((String) candleData.get(3)), Float.parseFloat((String) candleData.get(4)), Float.parseFloat((String) candleData.get(6)));
        }).toList();
    }
}
