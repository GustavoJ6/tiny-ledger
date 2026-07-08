package com.teya.gustavo.interview.tiny_ledger.controller;

import com.teya.gustavo.interview.tiny_ledger.model.TransactionType;

import java.math.BigDecimal;

public record TransactionRequest(TransactionType type, BigDecimal amount) {
}
