package com.crypto.kraken.bot.component;

import com.crypto.kraken.bot.model.Trade;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TradeWrapper {
    private Optional<Trade> trade;

    public TradeWrapper() {
        this.trade = Optional.empty();
    }

    public Optional<Trade> getTrade() {
        return trade;
    }

    public void setTrade(Optional<Trade> trade) {
        this.trade = trade;
    }

    public boolean canTrade() {
        return (this.trade.isEmpty());
    }
}
