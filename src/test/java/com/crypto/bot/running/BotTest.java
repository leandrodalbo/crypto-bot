package com.crypto.bot.running;

import com.crypto.bot.model.AssetPrice;
import com.crypto.bot.model.Candle;
import com.crypto.bot.service.OperationService;
import com.crypto.bot.service.StrategyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class BotTest {

    @Mock
    private StrategyService strategyService;

    @Mock
    private OperationService operationService;

    @InjectMocks
    private Bot bot;


    @Test
    public void willTriggerTradeValidation() throws NoSuchAlgorithmException, InvalidKeyException {
        when(operationService.canOperate()).thenReturn(false);
        doNothing().when(operationService).validateTrade();

        bot.checkOpenTrade();

        verify(operationService, times(1)).canOperate();
        verify(operationService, times(1)).validateTrade();

    }

    @Test
    public void willTriggerAnewTrade() throws NoSuchAlgorithmException, InvalidKeyException {
        when(operationService.canOperate()).thenReturn(true);
        when(operationService.fetchCandles()).thenReturn(Map.of("BTC", List.of(new Candle(99000.0, 99100.0, 99050.0, 99090.0, 3343.0))));
        when(operationService.assetsPrice()).thenReturn(List.of(new AssetPrice("BTC", 99069.0)));
        when(strategyService.isValidForTrade(anyDouble(), any())).thenReturn(true);

        bot.newTrade();

        verify(operationService, times(1)).canOperate();
        verify(operationService, times(1)).fetchCandles();
        verify(operationService, times(1)).assetsPrice();
        verify(strategyService, times(1)).isValidForTrade(anyDouble(), any());

    }


}
