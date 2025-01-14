package com.crypto.kraken.bot.component;

import com.crypto.kraken.bot.conf.MainConfProps;
import com.crypto.kraken.bot.krakenResponse.KrakenBalanceResponse;
import com.crypto.kraken.bot.krakenResponse.KrakenOHLCResponse;
import com.crypto.kraken.bot.model.AssetPrice;
import com.crypto.kraken.bot.model.Balance;
import com.crypto.kraken.bot.model.Candle;
import com.crypto.kraken.bot.model.TradingPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
public class KrakenClient {
    private static final String API_KEY_HEADER = "API-Key";
    private static final String API_SIGN_HEADER = "API-Sign";
    private static final String SHA_256 = "SHA-256";
    private static final String HMAC_SHA_512 = "HmacSHA512";

    private static final String OHLC_PATH = "/0/public/OHLC";
    private static final String BALANCE_PATH = "/0/private/Balance";
    private static final String PRICE_PATH = "/0/public/Ticker";

    private final RestClient client;
    private final MainConfProps props;

    private final Logger logger = LoggerFactory.getLogger(KrakenClient.class);

    public KrakenClient(RestClient client, MainConfProps props) {
        this.client = client;
        this.props = props;
    }

    public List ohlcData(TradingPair tradingPair, int interval, long since) {
        var response = client.get()
                .uri(uriBuilder -> uriBuilder.path(OHLC_PATH)
                        .queryParam("pair", tradingPair.toString())
                        .queryParam("interval", interval)
                        .queryParam("since", since).build())
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .body(KrakenOHLCResponse.class);

        if (response.error().length > 0) {
            logger.warn(response.error()[0]);
            return List.of();
        }

        return toCandlesList(response);
    }

    public Balance balance() throws NoSuchAlgorithmException, InvalidKeyException {
        String nonce = String.valueOf(System.currentTimeMillis());
        Map<String, String> params = new HashMap<>();

        params.put("nonce", nonce);
        String data = postingData(params);

        KrakenBalanceResponse response = client.post()
                .uri(uriBuilder -> uriBuilder.path(BALANCE_PATH).build())
                .header(API_KEY_HEADER, this.props.key())
                .header(API_SIGN_HEADER, signature(this.props.secret(), data, nonce, BALANCE_PATH))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .body(data)
                .retrieve()
                .body(KrakenBalanceResponse.class);

        if (response.error().length > 0) {
            logger.warn(response.error()[0]);
            return new Balance(Map.of());
        }

        return toBalance(response);
    }

    public String signature(String privateKey, String encodedPayload, String nonce, String endpointPath) throws NoSuchAlgorithmException, InvalidKeyException {
        final byte[] pathInBytes = endpointPath.getBytes(StandardCharsets.UTF_8);
        final String noncePrependedToPostData = nonce + encodedPayload;
        final MessageDigest md = MessageDigest.getInstance(SHA_256);

        md.update(noncePrependedToPostData.getBytes(StandardCharsets.UTF_8));

        final byte[] messageHash = md.digest();
        final byte[] base64DecodedSecret = Base64.getDecoder().decode(privateKey);
        final SecretKeySpec keyspec = new SecretKeySpec(base64DecodedSecret, HMAC_SHA_512);

        Mac mac = Mac.getInstance(HMAC_SHA_512);
        mac.init(keyspec);

        mac.reset();
        mac.update(pathInBytes);
        mac.update(messageHash);

        return Base64.getEncoder().encodeToString(mac.doFinal());
    }


    public String postingData(Map<String, String> data) {
        final StringBuilder postData = new StringBuilder();
        for (final Map.Entry<String, String> param : data.entrySet()) {
            if (!postData.isEmpty()) {
                postData.append("&");
            }
            postData.append(param.getKey());
            postData.append("=");
            postData.append(URLEncoder.encode(param.getValue(), StandardCharsets.UTF_8));
        }

        return postData.toString();
    }

    private List toCandlesList(KrakenOHLCResponse response) {

        List data = (List) (response.result().values().stream().toList().get(0));

        return data.stream().map(it -> {
            List candleData = (List) it;
            return new Candle(Float.parseFloat((String) candleData.get(1)), Float.parseFloat((String) candleData.get(2))
                    , Float.parseFloat((String) candleData.get(3)), Float.parseFloat((String) candleData.get(4)), Float.parseFloat((String) candleData.get(6)));
        }).toList();
    }

    private Balance toBalance(KrakenBalanceResponse response) {
        Map<String, Float> balanceMap = new HashMap<>();

        response.result().entrySet().forEach(it ->
        {
            float value = Float.parseFloat(it.getValue());
            if(value > 0) balanceMap.put(it.getKey(), value);
        });

        return new Balance(balanceMap);
    }

    public AssetPrice assetPrice(TradingPair pair) {

        var response = client.get()
                .uri(uriBuilder -> uriBuilder.path(PRICE_PATH)
                        .queryParam("pair", pair.toString()).build())
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .body(Map.class);

        if (((List)response.get("error")).size() > 0) {
            logger.warn(((List<String>) response.get("error")).get(0));
            return new AssetPrice("ERROR", -1f);
        }

        return toAsset(pair, (Map<String, List>) response.get("result"));
    }

    private AssetPrice toAsset(TradingPair pair, Map<String, List> result) {
        Map pairMap = (Map) result.get(pair.toString());
        List info = (List) pairMap.get("c");
        return new AssetPrice(pair.key(),
                Float.parseFloat((String) info.get(0)));
    }
}
