package com.crypto.kraken.bot.service;

import com.crypto.kraken.bot.component.KrakenClient;
import com.crypto.kraken.bot.component.TradeWrapper;
import com.crypto.kraken.bot.conf.OperationConf;
import com.crypto.kraken.bot.model.Trade;
import com.crypto.kraken.bot.model.Candle;
import com.crypto.kraken.bot.model.Balance;
import com.crypto.kraken.bot.model.AssetPrice;
import com.crypto.kraken.bot.model.TradingPair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class OperationServiceTest {

    @InjectMocks
    OperationService underTest;

    @Mock
    KrakenClient krakenClient;

    @Mock
    OperationConf conf;

    @Mock
    TradeWrapper tradeWrapper;

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

        assertThat(result.values().get("USD")).isEqualTo(343.4F);
    }

    @Test
    void shouldReturnAssetsPrice() {
        when(conf.pairs()).thenReturn(Map.of("XXLM", "ZUSD"));
        when(krakenClient.assetPrice(any())).thenReturn(new AssetPrice("XXLM", 34.4F));

        List<AssetPrice> result = underTest.assetsPrice();

        assertThat(34.4f).isEqualTo(result.get(0).usd());
    }

    @Test
    void shouldOpenATrade() throws NoSuchAlgorithmException, InvalidKeyException {
        when(conf.currency()).thenReturn("ZUSD");
        when(conf.stop()).thenReturn(0.006f);
        when(conf.profit()).thenReturn(0.018f);
        when(conf.notBelow()).thenReturn(50f);

        when(krakenClient.assetPrice(any())).thenReturn(new AssetPrice("XXLM", 34.4F));
        when(krakenClient.balance()).thenReturn(new Balance(Map.of("ZUSD", 80F)));
        when(krakenClient.postOrder(any(), anyDouble(), any())).thenReturn(true);
        when(tradeWrapper.canTrade()).thenReturn(true);
        when(tradeWrapper.getTrade()).thenReturn(Optional.of(new Trade(true, new TradingPair("XXLM", "ZUSD"), 0, 0, Instant.now().toEpochMilli())));

        underTest.openTrade(new TradingPair("XXLM", "ZUSD"));

        verify(tradeWrapper, times(1)).setTrade(any());
        verify(krakenClient, times(1)).postOrder(any(), anyDouble(), any());
    }

    @Test
    void shouldCloseATrade() throws NoSuchAlgorithmException, InvalidKeyException {
        when(tradeWrapper.canTrade()).thenReturn(false);
        when(tradeWrapper.getTrade()).thenReturn(Optional.of(new Trade(true, new TradingPair("XXLM", "ZUSD"), 34.1F, 35.1F, Instant.now().toEpochMilli())));
        when(krakenClient.balance()).thenReturn(new Balance(Map.of("XXLM", 2.3433F)));
        when(krakenClient.postOrder(any(), anyDouble(), any())).thenReturn(true);

        underTest.closeTrade();

        verify(tradeWrapper, times(1)).setTrade(any());
        verify(tradeWrapper, times(1)).getTrade();
        verify(krakenClient, times(1)).postOrder(any(), anyDouble(), any());

    }

    @Test
    void shouldValidateAndCloseAnOldTrade() throws NoSuchAlgorithmException, InvalidKeyException {
        when(conf.minutesLimit()).thenReturn(60);
        when(tradeWrapper.canTrade()).thenReturn(false);
        when(tradeWrapper.getTrade()).thenReturn(Optional.of(new Trade(true, new TradingPair("XXLM", "ZUSD"), 34.1F, 35.1F, Instant.now().minus(91, ChronoUnit.MINUTES).toEpochMilli())));

        when(krakenClient.assetPrice(any())).thenReturn(new AssetPrice("XXLM", 34.4F));
        when(krakenClient.balance()).thenReturn(new Balance(Map.of("XXLM", 2.3433F)));
        when(krakenClient.postOrder(any(), anyDouble(), any())).thenReturn(true);


        underTest.validateTrade();

        verify(tradeWrapper, times(2)).canTrade();
        verify(tradeWrapper, times(2)).getTrade();
        verify(tradeWrapper, times(1)).setTrade(any());
        verify(krakenClient, times(1)).assetPrice(any());
        verify(krakenClient, times(1)).balance();
        verify(krakenClient, times(1)).postOrder(any(), anyDouble(), any());

    }

    @Test
    void shouldCloseIfTouchedStopLoss() throws NoSuchAlgorithmException, InvalidKeyException {
        when(conf.minutesLimit()).thenReturn(60);
        when(tradeWrapper.canTrade()).thenReturn(false);
        when(tradeWrapper.getTrade()).thenReturn(Optional.of(new Trade(true, new TradingPair("XXLM", "ZUSD"), 34.1F, 35.1F, Instant.now().toEpochMilli())));

        when(krakenClient.assetPrice(any())).thenReturn(new AssetPrice("XXLM", 34.099F));
        when(krakenClient.balance()).thenReturn(new Balance(Map.of("XXLM", 2.3433F)));
        when(krakenClient.postOrder(any(), anyDouble(), any())).thenReturn(true);


        underTest.validateTrade();

        verify(tradeWrapper, times(2)).canTrade();
        verify(tradeWrapper, times(2)).getTrade();
        verify(tradeWrapper, times(1)).setTrade(any());
        verify(krakenClient, times(1)).assetPrice(any());
        verify(krakenClient, times(1)).balance();
        verify(krakenClient, times(1)).postOrder(any(), anyDouble(), any());

    }

    @Test
    void shouldTakeProfit() throws NoSuchAlgorithmException, InvalidKeyException {
        when(conf.minutesLimit()).thenReturn(60);
        when(tradeWrapper.canTrade()).thenReturn(false);
        when(tradeWrapper.getTrade()).thenReturn(Optional.of(new Trade(true, new TradingPair("XXLM", "ZUSD"), 34.1F, 35.1F, Instant.now().toEpochMilli())));

        when(krakenClient.assetPrice(any())).thenReturn(new AssetPrice("XXLM", 35.1099F));
        when(krakenClient.balance()).thenReturn(new Balance(Map.of("XXLM", 2.3433F)));
        when(krakenClient.postOrder(any(), anyDouble(), any())).thenReturn(true);


        underTest.validateTrade();

        verify(tradeWrapper, times(2)).canTrade();
        verify(tradeWrapper, times(2)).getTrade();
        verify(tradeWrapper, times(1)).setTrade(any());
        verify(krakenClient, times(1)).assetPrice(any());
        verify(krakenClient, times(1)).balance();
        verify(krakenClient, times(1)).postOrder(any(), anyDouble(), any());

    }

    @Test
    public void shouldValidateICanOpenNewTrades() {
        when(tradeWrapper.canTrade()).thenReturn(true);
        assertThat(underTest.canOperate()).isTrue();
    }

    @Test
    public void shouldGetTradeStatus() {
        when(tradeWrapper.getTrade()).thenReturn(Optional.of(new Trade(true, new TradingPair("XXLM", "ZUSD"), 34.1F, 35.1F, Instant.now().toEpochMilli())));
        assertThat(underTest.tradeStatus().isOpen()).isTrue();
    }
}
