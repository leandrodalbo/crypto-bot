package com.crypto.bot.component;

import com.crypto.bot.props.IndicatorProps;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.crypto.bot.utils.BotUtils.botFormatDouble;

@Component
public class CoreWrapper {
    private final IndicatorProps props;
    private final Core core;

    public CoreWrapper(IndicatorProps props, Core core) {
        this.props = props;
        this.core = core;
    }

    public boolean isBollingerBandsBuy(double assetPrice, double[] data) {
        double[] upperBand = new double[data.length];
        double[] middleBand = new double[data.length];
        double[] lowerBand = new double[data.length];

        core.bbands(0, data.length - 1, data, props.bbPeriod(), props.bbStdDev(), props.bbStdDev(), MAType.Sma, new MInteger(), new MInteger(), upperBand, middleBand, lowerBand);

        lowerBand = this.cleanUp(lowerBand);

        return (botFormatDouble(assetPrice) <= botFormatDouble(lowerBand[lowerBand.length - 1]));
    }

    public boolean isEMABuy(double[] data) {
        double[] shortValues = new double[data.length];
        double[] longValues = new double[data.length];

        core.ema(0, data.length - 1, data, props.shortMA(), new MInteger(), new MInteger(), shortValues);
        core.ema(0, data.length - 1, data, props.longMA(), new MInteger(), new MInteger(), longValues);

        shortValues = this.cleanUp(shortValues);
        longValues = this.cleanUp(longValues);

        return (botFormatDouble(shortValues[shortValues.length - 3]) < botFormatDouble(longValues[longValues.length - 3]) &&
                botFormatDouble(shortValues[shortValues.length - 1]) > botFormatDouble(longValues[longValues.length - 1]));
    }


    public boolean isMACDBuy(double[] data) {

        if (data.length < (props.macdSlow() + props.macdSignal())) {
            return false;
        }

        double[] macd = new double[data.length];
        double[] signal = new double[data.length];
        double[] hist = new double[data.length];

        core.macd(
                0,
                data.length - 1,
                data,
                props.macdFast(),
                props.macdSlow(),
                props.macdSignal(),
                new MInteger(),
                new MInteger(),
                macd,
                signal,
                hist);

        macd = this.cleanUp(macd);
        signal = this.cleanUp(signal);

        return (botFormatDouble(macd[macd.length - 3]) < botFormatDouble(signal[signal.length - 3]) && botFormatDouble(macd[macd.length - 1]) > botFormatDouble(signal[signal.length - 1]));
    }

    public double[] cleanUp(double[] values) {
        return Arrays.copyOf(values, findIndex(values));
    }

    public int[] cleanUp(int[] values) {
        return Arrays.copyOf(values, findIndex(values));
    }

    private int findIndex(double[] values) {
        int i = values.length - 1;

        while (i >= 0 && values[i] == 0.0)
            i--;
        return i + 1;
    }

    private int findIndex(int[] values) {
        int i = values.length - 1;

        while (i >= 0 && values[i] == 0)
            i--;
        return i + 1;
    }
}
