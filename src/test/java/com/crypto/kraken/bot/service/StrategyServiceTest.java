package com.crypto.kraken.bot.service;

import com.crypto.kraken.bot.component.CandlePatternFinder;
import com.crypto.kraken.bot.component.CoreWrapper;
import com.crypto.kraken.bot.model.Candle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StrategyServiceTest {

    @Mock
    private CoreWrapper wrapper;

    @Mock
    private CandlePatternFinder finder;

    @InjectMocks
    private StrategyService underTest;

    public void willBeTrueWithMACDAndEMA() {
        when(wrapper.isMACDBuy(any())).thenReturn(true);
        when(wrapper.isEMABuy(any())).thenReturn(true);

        assertThat(underTest.isValidForTrade(2.0, new Candle[]{new Candle(2.0, 2.5, 1.9, 2.1, 33443.0)})).isFalse();

    }
}
