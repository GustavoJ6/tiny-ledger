package com.teya.gustavo.interview.tiny_ledger.service;

import com.teya.gustavo.interview.tiny_ledger.model.Transaction;
import com.teya.gustavo.interview.tiny_ledger.model.TransactionType;
import com.teya.gustavo.interview.tiny_ledger.persistence.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService{
    
    private static final BigDecimal INITIAL_BALANCE = BigDecimal.ZERO;
    private static final Long INITIAL_TRANSACTION_ID = 1L;
    private static final Long TRANSACTION_ID_INCREMENT = 1L;
    private static final Long MILLIS_IN_SECOND = 1000L;

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Optional<Transaction> recordTransaction(TransactionType transactionType, BigDecimal amount) {
        Optional<Transaction> lastTransaction = transactionRepository.findLast();
        
        BigDecimal previousBalance = lastTransaction.map(Transaction::balanceAfterTransaction).orElse(INITIAL_BALANCE);
        Long newTransactionId = lastTransaction.map(t -> t.id() + TRANSACTION_ID_INCREMENT).orElse(INITIAL_TRANSACTION_ID);
        
        if (transactionType == TransactionType.WITHDRAWAL && !hasSufficientFunds(previousBalance, amount)) {
            return Optional.empty();
        }
        
        BigDecimal updatedBalance = calculateUpdatedBalance(transactionType, previousBalance, amount);
        Transaction newTransaction = buildTransaction(newTransactionId, transactionType, amount, updatedBalance);
        
        return transactionRepository.save(newTransaction)
                .or(() -> {
                    // This check is purely performative, as the in-memory implementation will always succeed. Since
                    // Empty is already being used for Insufficient funds, this is a way to differentiate between the two cases.
                    // In a real-world scenario, this would be handled properly (probably a Error log + a dedicated response code,
                    // involving changing the return type of the service operation).
                    throw new IllegalStateException("Failed to save transaction: " + newTransaction);
                });
    }

    private boolean hasSufficientFunds(BigDecimal balance, BigDecimal amount) {
        return balance.compareTo(amount) >= 0;
    }

    private BigDecimal calculateUpdatedBalance(TransactionType type, BigDecimal previousBalance, BigDecimal amount) {
        return type == TransactionType.WITHDRAWAL
                ? previousBalance.subtract(amount)
                : previousBalance.add(amount);
    }

    private Transaction buildTransaction(Long transactionId, TransactionType type, BigDecimal amount, BigDecimal balance) {
        Long unixTimestampInSeconds = System.currentTimeMillis() / MILLIS_IN_SECOND;
        return new Transaction(transactionId, unixTimestampInSeconds, type, amount, balance);
    }


    @Override
    public BigDecimal getCurrentBalance() {
        return transactionRepository.findLast()
                .map(Transaction::balanceAfterTransaction)
                .orElse(INITIAL_BALANCE);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
