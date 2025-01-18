package com.crypto.bot.component;

import com.crypto.bot.model.Candle;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CandlePatternFinderTest {
    CandlePatternFinder finder = new CandlePatternFinder();

    @Test
    void shouldFindABullishPatternBetweenTheFirstASecond() {
        Candle[] candles = new Candle[]{
                new Candle(2.0, 2.5, 1.2, 1.5, 232.0),
                new Candle(1.4, 3.0, 1.2, 2.1, 232.0),
                new Candle(0.9, 5.0, 0.8, 2.5, 232.0),
        };
        assertThat(finder.isEngulfingCandleBuy(candles)).isTrue();
    }

    @Test
    void shouldFindABullishPatternBetweenTheSecondAndLast() {
        Candle[] candles = new Candle[]{
                new Candle(1.1, 2.2, 1.0, 1.0, 232.0),
                new Candle(3.1, 3.5, 2.3, 2.4, 232.0),
                new Candle(2.1, 3.4, 1.9, 3.2, 232.0),
        };
        assertThat(finder.isEngulfingCandleBuy(candles)).isTrue();
    }


    @Test
    void shouldBeFalseForNullCandles() {
        assertThat(finder.isEngulfingCandleBuy(new Candle[2])).isFalse();
    }

    @Test
    void shouldBeFalseForAnEmptyArray() {
        assertThat(finder.isEngulfingCandleBuy(new Candle[0])).isFalse();
    }
}
