package com.crypto.kraken.bot.service;

import com.crypto.kraken.bot.component.KrakenClient;
import com.crypto.kraken.bot.component.TradeWrapper;
import com.crypto.kraken.bot.props.OperationProps;
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
    OperationProps conf;

    @Mock
    TradeWrapper tradeWrapper;

    @Test
    public void shouldFetchAllTradingPairsCandles() {
        when(conf.pairs()).thenReturn(Map.of("BTC", "USD"));
        when(krakenClient.ohlcData(any())).thenReturn(List.of(new Candle(10.0, 11.0, 9.0, 8.5, 1234.0)));

        Map<String, List<Candle>> tradingData = underTest.fetchCandles();

        assertThat(tradingData.get("BTCUSD")).isNotNull();
        assertThat(tradingData.get("BTCUSD").size()).isEqualTo(1);
    }

    @Test
    void shouldReturnTradingAccountBalance() throws NoSuchAlgorithmException, InvalidKeyException {
        when(krakenClient.balance()).thenReturn(new Balance(Map.of("USD", 343.44578)));

        Balance result = underTest.getBalance();

        assertThat(result.formattedValuesMap().get("USD")).isEqualTo(343.44578);
    }

    @Test
    void shouldReturnAssetsPrice() {
        when(conf.pairs()).thenReturn(Map.of("XXLM", "ZUSD"));
        when(krakenClient.assetPrice(any())).thenReturn(new AssetPrice("XXLM", 34.48));

        List<AssetPrice> result = underTest.assetsPrice();

        assertThat(34.48).isEqualTo(result.get(0).formattedUSD());
    }

    @Test
    void shouldOpenATrade() throws NoSuchAlgorithmException, InvalidKeyException {
        when(conf.currency()).thenReturn("ZUSD");
        when(conf.formattedStop()).thenReturn(0.006);
        when(conf.formattedProfit()).thenReturn(0.018);
        when(conf.formattedNotBelow()).thenReturn(50.0);

        when(krakenClient.assetPrice(any())).thenReturn(new AssetPrice("XXLM", 34.4));
        when(krakenClient.balance()).thenReturn(new Balance(Map.of("ZUSD", 80.0)));
        when(krakenClient.postOrder(any(), anyDouble(), any())).thenReturn(true);
        when(tradeWrapper.canTrade()).thenReturn(true);
        when(tradeWrapper.getTrade()).thenReturn(Optional.of(new Trade(true, new TradingPair("XXLM", "ZUSD"), 0, 0, Instant.now().toEpochMilli())));

        underTest.openTrade("XXLM");

        verify(tradeWrapper, times(1)).setTrade(any());
        verify(krakenClient, times(1)).postOrder(any(), anyDouble(), any());
    }

    @Test
    void shouldCloseATrade() throws NoSuchAlgorithmException, InvalidKeyException {
        when(tradeWrapper.canTrade()).thenReturn(false);
        when(tradeWrapper.getTrade()).thenReturn(Optional.of(new Trade(true, new TradingPair("XXLM", "ZUSD"), 34.1F, 35.1F, Instant.now().toEpochMilli())));
        when(krakenClient.balance()).thenReturn(new Balance(Map.of("XXLM", 2.3433)));
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
        when(krakenClient.balance()).thenReturn(new Balance(Map.of("XXLM", 2.3433)));
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

        when(krakenClient.assetPrice(any())).thenReturn(new AssetPrice("XXLM", 34.099));
        when(krakenClient.balance()).thenReturn(new Balance(Map.of("XXLM", 2.3433)));
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

        when(krakenClient.assetPrice(any())).thenReturn(new AssetPrice("XXLM", 35.1099));
        when(krakenClient.balance()).thenReturn(new Balance(Map.of("XXLM", 2.3433)));
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
