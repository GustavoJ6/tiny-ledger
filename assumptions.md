# Assumptions

This file serves to document the assumptions made while implementing the tiny ledger application.

1. For the sake of simplicity, it is assumed that the ledger will only handle one user account.
2. The ledger will only support the two types of transactions described in the assessment doc: deposits and withdrawals.
3. The ledger will only support a single currency for all transactions.
4. The ledger does NOT handle concurrent requests and does NOT implement any form of transactions or atomic operations.
In a real-world scenario, it would be important to ensure that the ledger is thread-safe and able to handle concurrent
requests while maintaining data integrity.
5. The transaction history should be returned in reverse chronological order, with the most recent transaction appearing first. 
To clarify, this was a decision made by me so that the user can see the most recent transactions first, as it is more intuitive and user-friendly.
6. The timestamp will be returned as a unix timestamp, in seconds, it assumes that the presentation layer would handle the
conversion to a human-readable format if necessary.
7. No DTOs or mappers will be implemented to keep it simple, the domain model will be used directly in the API layer. 
Normally, it would be better to separate the domain model from the API layer.
8. The Transaction Service uses `Optional.empty()` to signal that a transaction could not be recorded (e.g. insufficient funds).
A persistence failure is treated as an exceptional case and throws an `IllegalStateException`, as `Optional.empty()` is
already semantically reserved for business rule violations. In a real-world scenario, this would warrant a dedicated
response code and proper error handling.
9. Transactions that are not successful won't be recorded in the transaction history, and the balance will remain unchanged.
An audit table or log would be a better approach to keep track of failed transactions, but this is not implemented in this tiny ledger application.
