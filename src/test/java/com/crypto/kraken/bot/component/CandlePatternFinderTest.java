package com.crypto.kraken.bot.component;

import com.crypto.kraken.bot.model.Candle;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CandlePatternFinderTest {
    CandlePatternFinder finder = new CandlePatternFinder();

    @Test
    void shouldFindABullishPatternBetweenTheFirstASecond() {
        Candle[] candles = new Candle[]{
                new Candle(2f, 2.5f, 1.2f, 1.5f, 232.0f),
                new Candle(1.4f, 3.0f, 1.2f, 2.1f, 232.0f),
                new Candle(0.9f, 5.0f, 0.8f, 2.5f, 232.0f),
        };
        assertThat(finder.engulfingCandleBuy(candles)).isTrue();
    }

    @Test
    void shouldFindABullishPatternBetweenTheSecondAndLast() {
        Candle[] candles = new Candle[]{
                new Candle(1.1f, 2.2f, 1f, 1.0f, 232.0f),
                new Candle(3.1f, 3.5f, 2.3f, 2.4f, 232.0f),
                new Candle(2.1f, 3.4f, 1.9f, 3.2f, 232.0f),
        };
        assertThat(finder.engulfingCandleBuy(candles)).isTrue();
    }


    @Test
    void shouldBeFalseForNullCandles() {
        assertThat(finder.engulfingCandleBuy(new Candle[2])).isFalse();
    }

    @Test
    void shouldBeFalseForAnEmptyArray() {
        assertThat(finder.engulfingCandleBuy(new Candle[0])).isFalse();
    }
}
