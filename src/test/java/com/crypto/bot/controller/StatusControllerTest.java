package com.crypto.bot.controller;

import com.crypto.bot.model.Balance;
import com.crypto.bot.service.OperationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(StatusController.class)
public class StatusControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    private OperationService service;

    @Test
    public void shouldFetchStatus() throws Exception {
        when(service.getBalance()).thenReturn(new Balance(Map.of("USD", 343.4)));

        var res = mvc.perform(get("/bot/status").contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(res.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(service, times(1)).getBalance();
    }

}
