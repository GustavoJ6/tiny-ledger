package com.teya.gustavo.interview.tiny_ledger.persistence;

import com.teya.gustavo.interview.tiny_ledger.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    /**
     * Saves a transaction to the repository.
     *
     * @param transaction the transaction to save
     * @return the saved transaction if successful, or empty if the transaction could not be saved
     *
     * <p>
     * Note for the reviewer: In this case we only have an in-memory implementation, so the save operation will always succeed.
     * But it makes sense for the interface to return an Optional so that "future" implementations can flag a failure to save a transaction.
     * </p>
     */
    Optional<Transaction> save(Transaction transaction);

    Optional<Transaction> findLast();

    List<Transaction> findAll();

    void clear();
}
