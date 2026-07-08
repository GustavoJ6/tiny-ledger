package com.teya.gustavo.interview.tiny_ledger.persistence;

import com.teya.gustavo.interview.tiny_ledger.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryTransactionRepository implements TransactionRepository {

    private final Deque<Transaction> transactions = new ArrayDeque<>();

    @Override
    public void save(Transaction transaction) {
        transactions.push(transaction);
    }

    @Override
    public Optional<Transaction> findLast() {
        return Optional.ofNullable(transactions.peek());
    }

    @Override
    public List<Transaction> findAll() {
        return List.copyOf(transactions);
    }
}
