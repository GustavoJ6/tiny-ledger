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
