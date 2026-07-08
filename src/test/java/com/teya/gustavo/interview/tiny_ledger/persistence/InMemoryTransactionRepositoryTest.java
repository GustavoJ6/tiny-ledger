package com.teya.gustavo.interview.tiny_ledger.persistence;

import com.teya.gustavo.interview.tiny_ledger.model.Transaction;
import com.teya.gustavo.interview.tiny_ledger.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTransactionRepositoryTest {

    private InMemoryTransactionRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransactionRepository();
    }

    @Test
    void shouldReturnEmptyFindingLastWhenNoTransactionsExist() {
        Optional<Transaction> latest = repository.findLast();
        assertTrue(latest.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenNoTransactionsExist() {
        List<Transaction> all = repository.findAll();
        assertTrue(all.isEmpty());
    }

    @Test
    void shouldReturnLatestTransactionAfterSave() {
        Transaction transaction = new Transaction(1L, 1000L, TransactionType.DEPOSIT,
                new BigDecimal("100.00"), new BigDecimal("100.00"));

        repository.save(transaction);

        Optional<Transaction> latest = repository.findLast();
        assertTrue(latest.isPresent());
        assertEquals(transaction, latest.get());
    }

    @Test
    void shouldReturnMostRecentTransactionAsLatest() {
        Transaction first = new Transaction(1L, 1000L, TransactionType.DEPOSIT,
                new BigDecimal("100.00"), new BigDecimal("100.00"));
        Transaction second = new Transaction(2L, 2000L, TransactionType.DEPOSIT,
                new BigDecimal("50.00"), new BigDecimal("150.00"));

        repository.save(first);
        repository.save(second);

        Optional<Transaction> latest = repository.findLast();
        assertTrue(latest.isPresent());
        assertEquals(second, latest.get());
    }

    @Test
    void shouldReturnAllTransactionsInReverseChronologicalOrder() {
        Transaction first = new Transaction(1L, 1000L, TransactionType.DEPOSIT,
                new BigDecimal("100.00"), new BigDecimal("100.00"));
        Transaction second = new Transaction(2L, 2000L, TransactionType.DEPOSIT,
                new BigDecimal("50.00"), new BigDecimal("150.00"));
        Transaction third = new Transaction(3L, 3000L, TransactionType.WITHDRAWAL,
                new BigDecimal("30.00"), new BigDecimal("120.00"));

        repository.save(first);
        repository.save(second);
        repository.save(third);

        List<Transaction> all = repository.findAll();
        assertEquals(3, all.size());
        assertEquals(third, all.get(0));
        assertEquals(second, all.get(1));
        assertEquals(first, all.get(2));
    }

    @Test
    void shouldNotAffectRepositoryStateWhenModifyingReturnedList() {
        Transaction transaction = new Transaction(1L, 1000L, TransactionType.DEPOSIT,
                new BigDecimal("100.00"), new BigDecimal("100.00"));
        repository.save(transaction);

        List<Transaction> all = repository.findAll();
        assertThrows(UnsupportedOperationException.class, () -> all.add(transaction));
    }
}
