package com.crypto.kraken.bot.service;

import com.crypto.kraken.bot.conf.OperationConf;
import com.crypto.kraken.bot.model.Candle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OperationServiceTest {

    @InjectMocks
    OperationService underTest;

    @Mock
    KrakenClientService krakenClient;

    @Mock
    OperationConf conf;

    @Test
    public void shouldFetchAllTradingPairsCandles() {
        when(conf.pairs()).thenReturn(Set.of("BTCUSD"));
        when(krakenClient.ohlcData(anyString(), anyInt(), anyLong())).thenReturn(List.of(new Candle(10f, 11f, 9f, 8.5f, 1234f)));

        Map<String, List<Candle>> tradingData = underTest.fetchCandles();

        assertThat(tradingData.get("BTCUSD")).isNotNull();
        assertThat(tradingData.get("BTCUSD").size()).isEqualTo(1);
    }
}
