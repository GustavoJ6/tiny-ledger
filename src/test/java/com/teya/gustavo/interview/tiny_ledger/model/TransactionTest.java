package com.teya.gustavo.interview.tiny_ledger.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void shouldCreateTransactionWhenAllFieldsAreValid() {
        Transaction transaction = new Transaction(1L, 1000L, TransactionType.DEPOSIT, new BigDecimal("100.00"), new BigDecimal("100.00"));

        assertEquals(1L, transaction.id());
        assertEquals(1000L, transaction.timestamp());
        assertEquals(TransactionType.DEPOSIT, transaction.type());
        assertEquals(new BigDecimal("100.00"), transaction.amount());
        assertEquals(new BigDecimal("100.00"), transaction.balanceAfterTransaction());
    }

    @Test
    void shouldThrowWhenIdIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Transaction(null, 1000L, TransactionType.DEPOSIT, new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals("id must not be null", ex.getMessage());
    }

    @Test
    void shouldThrowWhenTimestampIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Transaction(1L, null, TransactionType.DEPOSIT, new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals("timestamp must not be null", ex.getMessage());
    }

    @Test
    void shouldThrowWhenTypeIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Transaction(1L, 1000L, null, new BigDecimal("100.00"), new BigDecimal("100.00")));
        assertEquals("type must not be null", ex.getMessage());
    }

    @Test
    void shouldThrowWhenAmountIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Transaction(1L, 1000L, TransactionType.DEPOSIT, null, new BigDecimal("100.00")));
        assertEquals("amount must be greater than zero", ex.getMessage());
    }

    @Test
    void shouldThrowWhenAmountIsZero() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Transaction(1L, 1000L, TransactionType.DEPOSIT, BigDecimal.ZERO, new BigDecimal("100.00")));
        assertEquals("amount must be greater than zero", ex.getMessage());
    }

    @Test
    void shouldThrowWhenAmountIsNegative() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Transaction(1L, 1000L, TransactionType.DEPOSIT, new BigDecimal("-50.00"), new BigDecimal("100.00")));
        assertEquals("amount must be greater than zero", ex.getMessage());
    }

    @Test
    void shouldThrowWhenBalanceAfterTransactionIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Transaction(1L, 1000L, TransactionType.DEPOSIT, new BigDecimal("100.00"), null));
        assertEquals("balanceAfterTransaction must not be null", ex.getMessage());
    }

    @Test
    void shouldThrowWhenBalanceAfterTransactionIsNegative() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Transaction(1L, 1000L, TransactionType.WITHDRAWAL, new BigDecimal("150.00"), new BigDecimal("-50.00")));
        assertEquals("balanceAfterTransaction must not be negative", ex.getMessage());
    }

    @Test
    void shouldAllowZeroBalanceAfterTransaction() {
        assertDoesNotThrow(() ->
                new Transaction(1L, 1000L, TransactionType.WITHDRAWAL, new BigDecimal("100.00"), BigDecimal.ZERO));
    }
}
