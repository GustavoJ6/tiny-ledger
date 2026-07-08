package com.teya.gustavo.interview.tiny_ledger.persistence;

import com.teya.gustavo.interview.tiny_ledger.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    void save(Transaction transaction);

    Optional<Transaction> findLast();

    List<Transaction> findAll();
}
