package com.teya.gustavo.interview.tiny_ledger.controller;

import tools.jackson.databind.ObjectMapper;
import com.teya.gustavo.interview.tiny_ledger.model.Transaction;
import com.teya.gustavo.interview.tiny_ledger.model.TransactionType;
import com.teya.gustavo.interview.tiny_ledger.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;


    @Test
    void shouldReturn201WhenDepositIsSuccessful() throws Exception {
        Transaction saved = new Transaction(1L, 1000L, TransactionType.DEPOSIT, new BigDecimal("100.00"), new BigDecimal("100.00"));
        when(transactionService.recordTransaction(TransactionType.DEPOSIT, new BigDecimal("100.00")))
                .thenReturn(Optional.of(saved));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TransactionRequest(TransactionType.DEPOSIT, new BigDecimal("100.00")))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.balanceAfterTransaction").value(100.00));
    }

    @Test
    void shouldReturn201WhenWithdrawalIsSuccessful() throws Exception {
        Transaction saved = new Transaction(2L, 2000L, TransactionType.WITHDRAWAL, new BigDecimal("40.00"), new BigDecimal("60.00"));
        when(transactionService.recordTransaction(TransactionType.WITHDRAWAL, new BigDecimal("40.00")))
                .thenReturn(Optional.of(saved));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TransactionRequest(TransactionType.WITHDRAWAL, new BigDecimal("40.00")))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("WITHDRAWAL"))
                .andExpect(jsonPath("$.balanceAfterTransaction").value(60.00));
    }

    @Test
    void shouldReturn422WhenInsufficientFunds() throws Exception {
        when(transactionService.recordTransaction(any(), any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TransactionRequest(TransactionType.WITHDRAWAL, new BigDecimal("999.00")))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("insufficient funds"));
    }

    @Test
    void shouldReturn400WhenAmountIsNull() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TransactionRequest(TransactionType.DEPOSIT, null))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("amount must be greater than zero"));
    }

    @Test
    void shouldReturn400WhenAmountIsZero() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TransactionRequest(TransactionType.DEPOSIT, BigDecimal.ZERO))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("amount must be greater than zero"));
    }

    @Test
    void shouldReturn400WhenAmountIsNegative() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TransactionRequest(TransactionType.DEPOSIT, new BigDecimal("-10.00")))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("amount must be greater than zero"));
    }

    @Test
    void shouldReturn400WhenTypeIsNull() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TransactionRequest(null, new BigDecimal("50.00")))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("transactionType must not be null"));
    }

    @Test
    void shouldReturn400WhenTypeIsInvalid() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"INVALID\",\"amount\":50.00}"))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldReturn200WithZeroBalanceWhenLedgerIsEmpty() throws Exception {
        when(transactionService.getCurrentBalance()).thenReturn(BigDecimal.ZERO);

        mockMvc.perform(get("/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    void shouldReturn200WithCurrentBalance() throws Exception {
        when(transactionService.getCurrentBalance()).thenReturn(new BigDecimal("150.00"));

        mockMvc.perform(get("/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string("150.00"));
    }


    @Test
    void shouldReturn200WithEmptyListWhenNoTransactionsExist() throws Exception {
        when(transactionService.getAllTransactions()).thenReturn(List.of());

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldReturn200WithTransactionsInReverseChronologicalOrder() throws Exception {
        Transaction first = new Transaction(1L, 1000L, TransactionType.DEPOSIT, new BigDecimal("100.00"), new BigDecimal("100.00"));
        Transaction second = new Transaction(2L, 2000L, TransactionType.WITHDRAWAL, new BigDecimal("40.00"), new BigDecimal("60.00"));
        when(transactionService.getAllTransactions()).thenReturn(List.of(second, first));

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[1].id").value(1));
    }
}
