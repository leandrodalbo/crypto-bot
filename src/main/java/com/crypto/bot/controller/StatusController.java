package com.crypto.bot.controller;


import com.crypto.bot.dto.BotStatus;
import com.crypto.bot.service.OperationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/bot")
public class StatusController {

    private final OperationService service;

    public StatusController(OperationService service) {
        this.service = service;
    }

    @GetMapping("/status")
    public BotStatus botStatus() throws NoSuchAlgorithmException, InvalidKeyException {
        return new BotStatus(service.getBalance(), service.tradeStatus(), service.assetsPrice());
    }
}
