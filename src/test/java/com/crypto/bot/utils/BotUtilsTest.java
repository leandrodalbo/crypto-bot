package com.crypto.bot.utils;

import com.crypto.bot.model.Candle;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BotUtilsTest {

    @Test
    public void shouldFormatDoubleValues() {
        assertThat(3.57451).isEqualTo(BotUtils.botFormatDouble(3.574510685746));
        assertThat(3.57).isEqualTo(BotUtils.botFormatDouble(3.57));
        assertThat(3.576).isEqualTo(BotUtils.botFormatDouble(3.5760));
    }

    @Test
    public void willListOfCandlesToArray() {
        Candle candle = new Candle(23.0, 28.0, 20.0, 22.0, 34343.0);

        assertThat(new Candle[]{candle}).isEqualTo(BotUtils.toCandlesArray(List.of(candle)));
    }

    @Test
    public void shouldFormatBalanceKey() {
        assertThat("TRX").isEqualTo(BotUtils.formatBalanceKey("TRX.F"));
    }

}