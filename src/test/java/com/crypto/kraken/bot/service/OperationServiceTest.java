package com.crypto.kraken.bot.service;

import com.crypto.kraken.bot.component.KrakenClient;
import com.crypto.kraken.bot.conf.OperationConf;
import com.crypto.kraken.bot.model.AssetPrice;
import com.crypto.kraken.bot.model.Balance;
import com.crypto.kraken.bot.model.Candle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OperationServiceTest {

    @InjectMocks
    OperationService underTest;

    @Mock
    KrakenClient krakenClient;

    @Mock
    OperationConf conf;

    @Test
    public void shouldFetchAllTradingPairsCandles() {
        when(conf.pairs()).thenReturn(Map.of("BTC", "USD"));
        when(krakenClient.ohlcData(any(), anyInt(), anyLong())).thenReturn(List.of(new Candle(10f, 11f, 9f, 8.5f, 1234f)));

        Map<String, List<Candle>> tradingData = underTest.fetchCandles();

        assertThat(tradingData.get("BTCUSD")).isNotNull();
        assertThat(tradingData.get("BTCUSD").size()).isEqualTo(1);
    }

    @Test
    void shouldReturnTradingAccountBalance() throws NoSuchAlgorithmException, InvalidKeyException {
        when(krakenClient.balance()).thenReturn(new Balance(Map.of("USD", 343.4F)));

        Balance result = underTest.getBalance();

        assertThat(result.balance().get("USD")).isEqualTo(343.4F);
    }

    @Test
    void shouldReturnAssetsPrice() {
        when(conf.pairs()).thenReturn(Map.of("XXLM", "ZUSD"));
        when(krakenClient.assetPrice(any())).thenReturn(new AssetPrice("XXLM", 34.4F));

        List<AssetPrice> result = underTest.assetsPrice();

        assertThat(34.4f).isEqualTo(result.get(0).usd());
    }
}
