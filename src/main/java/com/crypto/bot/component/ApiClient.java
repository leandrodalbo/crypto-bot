package com.crypto.bot.component;

import com.crypto.bot.model.AssetPrice;
import com.crypto.bot.model.Balance;
import com.crypto.bot.model.BuySell;
import com.crypto.bot.model.TradingPair;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface ApiClient {
    Balance balance() throws NoSuchAlgorithmException, InvalidKeyException;
    AssetPrice assetPrice(TradingPair pair);
    boolean postOrder(TradingPair pair, double volume, BuySell buySell) throws NoSuchAlgorithmException, InvalidKeyException;
    public List ohlcData(TradingPair tradingPair);
}
