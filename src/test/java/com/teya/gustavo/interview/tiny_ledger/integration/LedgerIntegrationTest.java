package com.teya.gustavo.interview.tiny_ledger.integration;

import com.teya.gustavo.interview.tiny_ledger.persistence.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LedgerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.clear();
    }

    @Test
    void shouldRecordTransactionsAndReflectCorrectBalanceAndHistory() throws Exception {
        // Deposit 100.00
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"DEPOSIT\",\"amount\":100.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.balanceAfterTransaction").value(100.00));

        // Deposit another 50.00
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"DEPOSIT\",\"amount\":50.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.balanceAfterTransaction").value(150.00));

        // Check balance at moment A
        mockMvc.perform(get("/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string("150.00"));

        // Withdraw 30.00
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"WITHDRAWAL\",\"amount\":30.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.balanceAfterTransaction").value(120.00));

        // Check balance at moment B - should reflect the withdrawal
        mockMvc.perform(get("/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string("120.00"));

        // Transaction history should be in reverse chronological order
        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].type").value("WITHDRAWAL"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[2].id").value(1));
    }

    @Test
    void shouldReturn422WhenWithdrawalExceedsBalance() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"DEPOSIT\",\"amount\":50.00}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"WITHDRAWAL\",\"amount\":999.00}"))
                .andExpect(status().is(422))
                .andExpect(content().string("insufficient funds"));
    }

    @Test
    void shouldReturnZeroBalanceAndEmptyHistoryWhenLedgerIsEmpty() throws Exception {
        mockMvc.perform(get("/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
