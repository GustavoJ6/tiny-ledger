package com.teya.gustavo.interview.tiny_ledger.service;

import com.teya.gustavo.interview.tiny_ledger.model.Transaction;
import com.teya.gustavo.interview.tiny_ledger.model.TransactionType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TransactionService {

    Optional<Transaction> recordTransaction(TransactionType transactionType, BigDecimal amount);

    BigDecimal getCurrentBalance();

    List<Transaction> getAllTransactions();
}
