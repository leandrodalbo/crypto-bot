package com.crypto.kraken.bot.service;

import com.crypto.kraken.bot.model.Candle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class KrakenClientServiceTest {

    KrakenClientService underTest;

    ObjectMapper mapper = new ObjectMapper();

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();

        var restClient = RestClient.builder()
                .baseUrl(String.format("http://localhost:%s",
                        mockWebServer.getPort()))
                .build();

        this.underTest = new KrakenClientService(restClient);
    }

    @AfterEach
    void cleanUp() throws IOException {
        this.mockWebServer.shutdown();
    }

    @Test
    public void shouldFetchOHLCData() throws JsonProcessingException {
        var mockResponse = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(mapper.writeValueAsString(Map.of(
                        "error", new String[]{},
                        "result", Map.of("BTCUSD", new Object[]{new Object[]{
                                1688671200,
                                "30306.1",
                                "30306.2",
                                "30305.7",
                                "30305.7",
                                "30306.1",
                                "3.39243896",
                                23}})
                )));

        mockWebServer.enqueue(mockResponse);

        List data = underTest.ohlcData("BTCUSD", 60, Instant.now().minus(3, ChronoUnit.DAYS).toEpochMilli());

        assertThat(data).isNotNull();
        assertThat(data).isEqualTo(List.of(new Candle(30306.1F, 30306.2F, 30305.7F, 30305.7F, 3.39243896F)));
    }

}
