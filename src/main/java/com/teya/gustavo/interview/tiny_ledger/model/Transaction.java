package com.teya.gustavo.interview.tiny_ledger.model;

import java.math.BigDecimal;

public record Transaction(Long id, Long timestamp, TransactionType type, BigDecimal amount,
                          BigDecimal balanceAfterTransaction) {

    public Transaction {
        if (id == null) throw new IllegalArgumentException("id must not be null");
        if (timestamp == null) throw new IllegalArgumentException("timestamp must not be null");
        if (type == null) throw new IllegalArgumentException("type must not be null");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("amount must be greater than zero");
        if (balanceAfterTransaction == null)
            throw new IllegalArgumentException("balanceAfterTransaction must not be null");
        if (balanceAfterTransaction.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("balanceAfterTransaction must not be negative");
    }
}
