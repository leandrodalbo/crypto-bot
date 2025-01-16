package com.crypto.kraken.bot.service;

import com.crypto.kraken.bot.component.CandlePatternFinder;
import com.crypto.kraken.bot.component.CoreWrapper;
import com.crypto.kraken.bot.model.Candle;
import com.crypto.kraken.bot.model.TradingPair;

public class StrategyService {

    private final CoreWrapper coreWrapper;
    private final CandlePatternFinder candlePatterns;

    public StrategyService(CoreWrapper coreWrapper, CandlePatternFinder candlePatterns) {
        this.coreWrapper = coreWrapper;
        this.candlePatterns = candlePatterns;
    }

    public boolean isValidForTrade(double price ,Candle[] candles){
        return false;
    }

}
