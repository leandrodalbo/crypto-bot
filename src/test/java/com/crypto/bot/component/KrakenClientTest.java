package com.crypto.bot.component;

import com.crypto.bot.model.Balance;
import com.crypto.bot.model.TradingPair;
import com.crypto.bot.model.Candle;
import com.crypto.bot.model.AssetPrice;
import com.crypto.bot.model.BuySell;
import com.crypto.bot.model.CandlesSinceUnit;
import com.crypto.bot.props.ClientProps;
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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class KrakenClientTest {

    KrakenClient underTest;

    ObjectMapper mapper = new ObjectMapper();
    ClientProps confProps = new ClientProps("http://localhost:%s", "aB1zKa2jKRo+wVcE2XzIv5Y9CrIT1aB2cdU00weENSTapquQcLo8aRz4", "kQH5HW/8p1uGOVjbgWA7FunAmGO8lsSUXNsu3eow76sz84Q18fWxnyRzBHCd3pd5nE9qa99HAZtuZuj6F1huXg==", 60, 5, CandlesSinceUnit.day);
    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();

        var restClient = RestClient.builder()
                .baseUrl(String.format(confProps.url(),
                        mockWebServer.getPort()))
                .build();

        this.underTest = new KrakenClient(restClient, confProps);
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

        List data = underTest.ohlcData(new TradingPair("BTC", "USD"));

        assertThat(data).isNotNull();
        assertThat(data).isEqualTo(List.of(new Candle(30306.1, 30306.2, 30305.7, 30305.7, 3.39243896)));
    }

    @Test
    public void shouldHandleFailingResponseOnFetchOHLC() throws JsonProcessingException {
        var mockResponse = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(mapper.writeValueAsString(Map.of(
                        "error", new String[]{"failed"},
                        "result", Map.of()
                )));

        mockWebServer.enqueue(mockResponse);

        List result = underTest.ohlcData(new TradingPair("BTC", "USD"));

        assertThat(result).isEmpty();
    }

    @Test
    public void shouldHandleFailingResponseOnFetchBalance() throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeyException {
        var mockResponse = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(mapper.writeValueAsString(Map.of(
                        "error", new String[]{"failed"},
                        "result", Map.of()
                )));

        mockWebServer.enqueue(mockResponse);

        Balance result = underTest.balance();

        assertThat(result.values().entrySet()).isEmpty();
    }

    @Test
    public void shouldFetchBalance() throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeyException {
        var mockResponse = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(mapper.writeValueAsString(Map.of(
                        "error", new String[]{},
                        "result", Map.of("USD", "500.", "DOT", "35.3")
                )));

        mockWebServer.enqueue(mockResponse);

        Balance result = underTest.balance();

        assertThat(result.formattedValuesMap().get("USD")).isEqualTo(500.0);
        assertThat((result.formattedValuesMap().get("DOT"))).isEqualTo(35.3);
    }

    @Test
    public void shouldFetchLastClosedPrice() throws JsonProcessingException {
        var mockResponse = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(mapper.writeValueAsString(Map.of(
                        "error", new String[]{},
                        "result", Map.of("XXRPZUSD", Map.of("c", List.of("30303.20000",
                                "0.00067643")))
                )));

        mockWebServer.enqueue(mockResponse);

        AssetPrice result = underTest.assetPrice(new TradingPair("XXRP", "ZUSD"));

        assertThat("XXRP").isEqualTo(result.name());
        assertThat(30303.2).isEqualTo(result.formattedUSD());
    }

    @Test
    public void shouldHandleFailingResponseOnFetchPrice() throws JsonProcessingException {
        var mockResponse = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(mapper.writeValueAsString(Map.of(
                        "error", new String[]{"failed"},
                        "result", Map.of()
                )));

        mockWebServer.enqueue(mockResponse);

        AssetPrice result = underTest.assetPrice(new TradingPair("BTC", "USD"));

        assertThat(-1.0).isEqualTo(result.formattedUSD());
    }

    @Test
    public void shouldPostAMarketOrder() throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeyException {
        var mockResponse = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(mapper.writeValueAsString(Map.of(
                        "error", new String[]{},
                        "result", Map.of()
                )));

        mockWebServer.enqueue(mockResponse);


        assertThat(underTest.postOrder(new TradingPair("TRX", "USD"), 34.4d, BuySell.buy)).isTrue();

    }

    @Test
    public void shouldBeFalseWithAFailingMarketOrder() throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeyException {
        var mockResponse = new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(mapper.writeValueAsString(Map.of(
                        "error", new String[]{"failed"},
                        "result", Map.of()
                )));

        mockWebServer.enqueue(mockResponse);

        assertThat(underTest.postOrder(new TradingPair("TRX", "USD"), 34.4d, BuySell.buy)).isFalse();

    }

    @Test
    void WillGenerateSignature() throws NoSuchAlgorithmException, InvalidKeyException {
        String pk = "kQH5HW/8p1uGOVjbgWA7FunAmGO8lsSUXNsu3eow76sz84Q18fWxnyRzBHCd3pd5nE9qa99HAZtuZuj6F1huXg==";
        String nonce = "1616492376594";
        String data = "nonce=1616492376594&ordertype=limit&pair=XBTUSD&price=37500&type=buy&volume=1.25";
        String path = "/0/private/AddOrder";

        assertThat(underTest.signature(pk, data, nonce, path)).isEqualTo("4/dpxb3iT4tp/ZCVEwSnEsLxx0bqyhLpdfOpc6fn7OR8+UClSV5n9E6aSS8MPtnRfp32bAb0nmbRn6H8ndwLUQ==");
    }

    @Test
    void shouldEncodeData() {
        Map<String, String> params = new HashMap<>();

        params.put("nonce", "13242424243744");
        params.put("ordertype", "market");
        params.put("pair", "BTCUSDT");
        params.put("type", "buy");
        params.put("volume", "0.01");

        String encoded = "volume=0.01&type=buy&nonce=13242424243744&pair=BTCUSDT&ordertype=market";

        assertThat(underTest.postingData(params)).isEqualTo(encoded);
    }

}
