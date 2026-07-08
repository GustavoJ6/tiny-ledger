package com.teya.gustavo.interview.tiny_ledger.service;

import com.teya.gustavo.interview.tiny_ledger.model.Transaction;
import com.teya.gustavo.interview.tiny_ledger.model.TransactionType;
import com.teya.gustavo.interview.tiny_ledger.persistence.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl service;

    private Transaction existingTransaction;

    @BeforeEach
    void setUp() {
        existingTransaction = new Transaction(1L, 1000L, TransactionType.DEPOSIT, new BigDecimal("100.00"), new BigDecimal("100.00"));
    }

    @Test
    void shouldRecordDepositWhenLedgerIsEmpty() {
        when(transactionRepository.findLast()).thenReturn(Optional.empty());
        when(transactionRepository.save(any())).thenAnswer(inv -> Optional.of(inv.getArgument(0)));

        Optional<Transaction> result = service.recordTransaction(TransactionType.DEPOSIT, new BigDecimal("50.00"));

        assertTrue(result.isPresent());
        assertEquals(TransactionType.DEPOSIT, result.get().type());
        assertEquals(new BigDecimal("50.00"), result.get().amount());
        assertEquals(new BigDecimal("50.00"), result.get().balanceAfterTransaction());
        assertEquals(1L, result.get().id());
    }

    @Test
    void shouldRecordDepositAndAccumulateBalance() {
        when(transactionRepository.findLast()).thenReturn(Optional.of(existingTransaction));
        when(transactionRepository.save(any())).thenAnswer(inv -> Optional.of(inv.getArgument(0)));

        Optional<Transaction> result = service.recordTransaction(TransactionType.DEPOSIT, new BigDecimal("50.00"));

        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("150.00"), result.get().balanceAfterTransaction());
        assertEquals(2L, result.get().id());
    }

    @Test
    void shouldRecordWithdrawalWhenSufficientFunds() {
        when(transactionRepository.findLast()).thenReturn(Optional.of(existingTransaction));
        when(transactionRepository.save(any())).thenAnswer(inv -> Optional.of(inv.getArgument(0)));

        Optional<Transaction> result = service.recordTransaction(TransactionType.WITHDRAWAL, new BigDecimal("40.00"));

        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("60.00"), result.get().balanceAfterTransaction());
    }

    @Test
    void shouldAllowWithdrawalThatDrainsBalanceToZero() {
        when(transactionRepository.findLast()).thenReturn(Optional.of(existingTransaction));
        when(transactionRepository.save(any())).thenAnswer(inv -> Optional.of(inv.getArgument(0)));

        Optional<Transaction> result = service.recordTransaction(TransactionType.WITHDRAWAL, new BigDecimal("100.00"));

        assertTrue(result.isPresent());
        assertEquals(BigDecimal.ZERO, result.get().balanceAfterTransaction().stripTrailingZeros());
    }

    @Test
    void shouldReturnEmptyWhenWithdrawalExceedsBalance() {
        when(transactionRepository.findLast()).thenReturn(Optional.of(existingTransaction));

        Optional<Transaction> result = service.recordTransaction(TransactionType.WITHDRAWAL, new BigDecimal("200.00"));

        assertTrue(result.isEmpty());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldReturnEmptyWhenWithdrawalOnEmptyLedger() {
        when(transactionRepository.findLast()).thenReturn(Optional.empty());

        Optional<Transaction> result = service.recordTransaction(TransactionType.WITHDRAWAL, new BigDecimal("50.00"));

        assertTrue(result.isEmpty());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenRepositoryFailsToSave() {
        when(transactionRepository.findLast()).thenReturn(Optional.empty());
        when(transactionRepository.save(any())).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> service.recordTransaction(TransactionType.DEPOSIT, new BigDecimal("50.00")));
    }


    @Test
    void shouldReturnZeroBalanceWhenLedgerIsEmpty() {
        when(transactionRepository.findLast()).thenReturn(Optional.empty());

        BigDecimal balance = service.getCurrentBalance();

        assertEquals(BigDecimal.ZERO, balance);
    }

    @Test
    void shouldReturnBalanceFromLatestTransaction() {
        when(transactionRepository.findLast()).thenReturn(Optional.of(existingTransaction));

        BigDecimal balance = service.getCurrentBalance();

        assertEquals(new BigDecimal("100.00"), balance);
    }


    @Test
    void shouldReturnEmptyListWhenNoTransactionsExist() {
        when(transactionRepository.findAll()).thenReturn(List.of());

        List<Transaction> result = service.getAllTransactions();

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnAllTransactions() {
        Transaction second = new Transaction(2L, 2000L, TransactionType.WITHDRAWAL, new BigDecimal("30.00"), new BigDecimal("70.00"));
        when(transactionRepository.findAll()).thenReturn(List.of(second, existingTransaction));

        List<Transaction> result = service.getAllTransactions();

        assertEquals(2, result.size());
        assertEquals(second, result.get(0));
        assertEquals(existingTransaction, result.get(1));
    }
}
