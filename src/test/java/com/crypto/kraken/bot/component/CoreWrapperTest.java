package com.crypto.kraken.bot.component;

import com.crypto.kraken.bot.props.IndicatorProps;
import com.tictactec.ta.lib.Core;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CoreWrapperTest {

    private final Core core = new Core();
    private final IndicatorProps props = new IndicatorProps(20, 2.0, 9, 21, 12, 26, 9, 14);

    private final CoreWrapper underTest = new CoreWrapper(props, core);

    @Test
    void willFindABuySignalUsingBollingerBands() {
        float[] values = new float[]{31.4f, 25.1f, 35.2f, 33.4f, 28.1f, 24.2f, 25.1f, 28.3f, 32.9f, 73.4f,
                11.1f, 44.2f, 55.1f, 31.3f, 33.4f, 61.1f, 44.2f, 45.1f, 21.3f, 22.9f,
                33.4f, 31.1f, 44.2f, 55.1f, 61.3f, 22.9f, 23.1f, 71.2f, 6.1f, 43.4f,
                21.1f, 44.2f, 25.1f, 21.3f, 23.4f, 21.1f, 44.2f, 45.1f, 71.3f};

        boolean result = underTest.isBollingerBandsBuy(1.365f, values);

        assertThat(result).isTrue();
    }

    @Test
    void willFindABuySignalUsingEMAs() {
        float[] values = new float[]{31.4f, 25.1f, 35.2f, 33.4f, 28.1f, 24.2f, 25.1f, 28.3f, 32.9f, 73.4f,
                11.1f, 44.2f, 55.1f, 31.3f, 33.4f, 61.1f, 44.2f, 45.1f, 21.3f, 22.9f,
                33.4f, 31.1f, 44.2f, 55.1f, 61.3f, 22.9f, 23.1f, 71.2f, 6.1f, 43.4f,
                21.1f, 44.2f, 25.1f, 21.3f, 23.4f, 21.1f, 44.2f, 45.1f, 71.3f};

        boolean result = underTest.isEMABuy(values);

        assertThat(result).isTrue();
    }

    @Test
    void willFindABuySignalUsingMACD() {
        float[] values = new float[]{31.4f, 25.1f, 35.2f, 33.4f, 28.1f, 24.2f, 25.1f, 28.3f, 32.9f, 73.4f,
                11.1f, 44.2f, 55.1f, 31.3f, 33.4f, 61.1f, 44.2f, 45.1f, 21.3f, 22.9f,
                33.4f, 31.1f, 44.2f, 55.1f, 61.3f, 22.9f, 23.1f, 71.2f, 6.1f, 43.4f,
                21.1f, 44.2f, 25.1f, 21.3f, 23.4f, 21.1f, 44.2f, 45.1f, 71.3f};

        boolean result = underTest.isMACDBuy(values);

        assertThat(result).isTrue();
    }

    @Test
    void shouldRemoveZerosForArrayOfDouble() {
        assertThat(underTest.cleanUp(new double[]{0.0})).isEqualTo(new double[0]);
        assertThat(underTest.cleanUp(new double[]{1.0f, 0.0f})).isEqualTo(new double[]{1.0f});
        assertThat(underTest.cleanUp(new double[]{1.0f, 2.0f, 0.0f, 0.0f})).isEqualTo(new double[]{1.0f, 2.0f});
    }

    @Test
    void shouldRemoveZerosForArraysOfInteger() {
        assertThat(underTest.cleanUp(new int[]{0})).isEqualTo(new int[0]);
        assertThat(underTest.cleanUp(new int[]{1, 0})).isEqualTo(new int[]{1});
        assertThat(underTest.cleanUp(new int[]{1, 2, 0, 0})).isEqualTo(new int[]{1, 2});
    }
}
