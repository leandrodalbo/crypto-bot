package com.crypto.kraken.bot;

import com.crypto.kraken.bot.service.OperationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    private final OperationService service;

    public TestController(OperationService service) {
        this.service = service;
    }

    @GetMapping("/candles")
    public Map testCandles() {
        return service.fetchCandles();
    }
}
