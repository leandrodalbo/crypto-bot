package com.crypto.kraken.bot.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BotUtilsTest {

    @Test
    public void shouldFormatDoubleValues() {
        assertThat(3.57451).isEqualTo(BotUtils.botFormatDouble(3.574510685746));
        assertThat(3.57).isEqualTo(BotUtils.botFormatDouble(3.57));
        assertThat(3.576).isEqualTo(BotUtils.botFormatDouble(3.5760));
    }
}
