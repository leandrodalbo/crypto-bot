package com.crypto.bot.service;

import com.crypto.bot.component.CandlePatternFinder;
import com.crypto.bot.component.CoreWrapper;
import com.crypto.bot.model.Candle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StrategyServiceTest {

    @Mock
    private CoreWrapper wrapper;

    @Mock
    private CandlePatternFinder finder;

    @InjectMocks
    private StrategyService underTest;

    @Test
    public void willBeTrueWithMACDAndEMA() {
        when(wrapper.isMACDBuy(any())).thenReturn(true);
        when(wrapper.isEMABuy(any())).thenReturn(true);
        when(wrapper.isBollingerBandsBuy(anyDouble(), any())).thenReturn(false);
        when(finder.isEngulfingCandleBuy(any())).thenReturn(false);

        assertThat(underTest.isValidForTrade(2.0, new Candle[]{new Candle(2.0, 2.5, 1.9, 2.1, 33443.0)})).isTrue();

    }

    @Test
    public void willBeTrueWithMACDAndBB() {
        when(wrapper.isMACDBuy(any())).thenReturn(true);
        when(wrapper.isEMABuy(any())).thenReturn(false);
        when(wrapper.isBollingerBandsBuy(anyDouble(), any())).thenReturn(true);
        when(finder.isEngulfingCandleBuy(any())).thenReturn(false);

        assertThat(underTest.isValidForTrade(2.0, new Candle[]{new Candle(2.0, 2.5, 1.9, 2.1, 33443.0)})).isTrue();

    }

    @Test
    public void willBeTrueWithMACDAndEngulfing() {
        when(wrapper.isMACDBuy(any())).thenReturn(true);
        when(wrapper.isEMABuy(any())).thenReturn(false);
        when(wrapper.isBollingerBandsBuy(anyDouble(), any())).thenReturn(false);
        when(finder.isEngulfingCandleBuy(any())).thenReturn(true);

        assertThat(underTest.isValidForTrade(2.0, new Candle[]{new Candle(2.0, 2.5, 1.9, 2.1, 33443.0)})).isTrue();

    }

    @Test
    public void willBeTrueWithBBAndEMA() {
        when(wrapper.isMACDBuy(any())).thenReturn(false);
        when(wrapper.isEMABuy(any())).thenReturn(true);
        when(wrapper.isBollingerBandsBuy(anyDouble(), any())).thenReturn(true);
        when(finder.isEngulfingCandleBuy(any())).thenReturn(false);

        assertThat(underTest.isValidForTrade(2.0, new Candle[]{new Candle(2.0, 2.5, 1.9, 2.1, 33443.0)})).isTrue();

    }

    @Test
    public void willBeTrueWithBBAndEngulfing() {
        when(wrapper.isMACDBuy(any())).thenReturn(false);
        when(wrapper.isEMABuy(any())).thenReturn(false);
        when(wrapper.isBollingerBandsBuy(anyDouble(), any())).thenReturn(true);
        when(finder.isEngulfingCandleBuy(any())).thenReturn(true);

        assertThat(underTest.isValidForTrade(2.0, new Candle[]{new Candle(2.0, 2.5, 1.9, 2.1, 33443.0)})).isTrue();

    }

    @Test
    public void willBeTrueWithEMAAndEngulfing() {
        when(wrapper.isMACDBuy(any())).thenReturn(false);
        when(wrapper.isEMABuy(any())).thenReturn(true);
        when(wrapper.isBollingerBandsBuy(anyDouble(), any())).thenReturn(false);
        when(finder.isEngulfingCandleBuy(any())).thenReturn(true);

        assertThat(underTest.isValidForTrade(2.0, new Candle[]{new Candle(2.0, 2.5, 1.9, 2.1, 33443.0)})).isTrue();

    }
}
