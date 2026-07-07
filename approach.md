# Approach Breakdown

Before starting the implementation, I started by creating this document to document the approach and my thought process along the way.
The goal is to provide a clear understanding of the solution and the decisions made during the implementation to the reviewer/interviewer.

## Functional Requirements

To facilitate the process of implementing the tiny ledger application, I first "translated" the described features into a 
set of functional requirements, which are described below:

- **FR1:** A user should be able to record money movements. Specifies:
  - The type of movement (deposit or withdrawal)
  - The amount of money to be moved
- **FR2:** A user should be able to view the current balance
- **FR3:** A user should be able to view the transaction history
  - As per the fifth assumption in the [Assumptions document](assumptions.md), the transaction history should be returned 
  in reverse chronological order, with the most recent transaction appearing first.

## Domain Model

After understanding the scope of the problem and identifying the functional requirements, I designed a domain model to ensure all requirements could be met.

### Transaction

As per the first assumption in the [Assumptions document](assumptions.md), the ledger will only handle one user account, given
that, and according to the requirements I envisioned the Transaction entity as the following:

- transactionId: Long - Unique identifier for the transaction
- transactionTime: unix timestamp
- transactionType: TransactionTypeEnum (can be either DEPOSIT or WITHDRAWAL)
- amount: BigDecimal
- balanceAfterTransaction: BigDecimal

#### Balance After Transaction Field Decision 
I considered three possible approaches for retrieving the current balance of the user:
1. Keeping track of the current balance after each transaction in the transaction itself
2. Keeping track of the current balance in a separate field in the ledger
3. Going through all the records in the ledger each time the future ```viewCurrentBalance()``` endpoint is called

After deliberating for a bit, I opted for the first approach, as that way to retrieve the balance is a simple O(1)
operation, by just retrieving the last transaction in the ledger and returning its balanceAfterTransaction field.
The second approach would also be O(1), but it would require an additional field and updating it within each transaction.
The last approach would be O(n) so it was discarded. Overall, I think the first approach is elegant and simple enough
for the scope of this project, and it also allows for a more detailed transaction history, as each transaction will
have the balance after it was executed.

#### Transaction ID Decision
I considered two possible approaches for generating the transactionId field:
1. Using a simple counter that increments with each new transaction
2. Using a UUID

Since on creation of a new transaction we already have to retrieve the last transaction in the ledger to get its
balanceAfterTransaction field, I opted for the first approach, as it is simpler and slightly more efficient than generating a UUID.

In a real world scenario, unless there was any specific requirement for the transactionId, I'd probably opt for the common
approach of letting the database generate the transactionId for us, as it would be more robust and scalable than either of the two approaches I considered.

#### Long and BigDecimal Decision

While we're keeping it simple and my first approach was to use int and double for the transactionId and amount fields 
respectively, I ended up using Long and BigDecimal instead, as using these types doesn't require any additional effort 
and they are more appropriate for the use case, as they can handle larger values and BigDecimal is more precise than double.

## Data Persistence

Following the recommendation in the [exercise document](exercise.md), I opted for an in-memory data structure to store
the transactions, which is a simple list of transactions, implemented as a stack, so that the most recent transaction
is always at the top of the stack, which allows for O(1) retrieval of the current balance.

*While not considered for simplicity reasons, a simple way to support multiple users (still disregarding auth and autz),
would be for this List of transactions to be a Map of userId to List of transactions, where each userId would have its own list of transactions.
Additionally, to make the domain a bit more robust we could even wrap the List of transactions in a new object called Ledger*

## API Design

To ensure a good organization of the API, I designed a RESTful API with one endpoint for each functional requirement, as described below:

- **FR1:** POST /transactions - Records a new transaction (deposit or withdrawal)
  - Request body:
    - transactionType: TransactionTypeEnum (can be either DEPOSIT or WITHDRAWAL)
    - amount: BigDecimal
  - Response body:
    - transactionId: Long - Unique identifier for the transaction
    - transactionTime: unix timestamp
    - transactionType: TransactionTypeEnum (can be either DEPOSIT or WITHDRAWAL)
    - amount: BigDecimal
    - balanceAfterTransaction: BigDecimal
  - Response codes:
    - 201 Created - Transaction created successfully
    - 400 Bad Request - Invalid request body. The following validation rules apply:
        - `amount` must be present, non-null, and greater than zero
        - `transactionType` must be present and one of the supported values (`DEPOSIT` or `WITHDRAWAL`)
    - 422 Unprocessable Entity - Insufficient funds for withdrawal
- **FR2:** GET /balance - Returns the current balance
  - Response body:
    - balance: BigDecimal
  - Response codes:
    - 200 OK - Balance retrieved successfully
  - Edge case:
    - If there are no transactions, the balance should be 0.0
- **FR3:** GET /transactions - Returns the transaction history in reverse chronological order
    - Response body:
        - transactions: List of transactions, each with all the fields described in the Transaction entity above
    - Response codes:
        - 200 OK - Transaction history retrieved successfully
    - Edge case:
        - If there are no transactions, the response should be an empty list
    - Implementation note:
        - Since the in-memory store is implemented as a stack (most recent transaction on top), the endpoint simply
        iterates it from top to bottom, yielding reverse chronological order without any additional sorting or copying.

## Implementation Details

Now that the foundations of the application have been laid out, I'll dive into some implementation details that I think are worth mentioning.

### Project Structure
The project is structured as follows:
```
src/
├── main/
│   ├── java/
│   │   └── com/teya/gustavo/interview/tiny_ledger/
│   │       ├── controller/ # Controllers for the API endpoints
│   │       ├── model/ # Domain models (Transaction, TransactionTypeEnum)
│   │       ├── service/ # Services for business logic
│   │       ├── persistence/ # In-memory data persistence
│   │       └── application/ # Main application class
│   └── resources/
│       └── application.properties # Application configuration
└── test/
    └── java/
        └── com/teya/gustavo/interview/tiny_ledger/
            ├── controller/ # Tests for the API endpoints
            ├── service/ # Tests for the services
            └── persistence/ # Tests for the in-memory data persistence
```

This structure is simple enough for the scope of this project, and it allows for a clear separation of concerns between the different layers of the application.

### Coding Patterns

Although the [exercise document](exercise.md) explicitly states "Please try to keep it simple. The objective is to understand your approach to
problems and your thought process rather than a test of your technical knowledge,
even if it means having to make trade-offs.", I will still make use of Dependency Injection and Dependency Inversion Principle
to ensure a clean separation of concerns and to facilitate testing.


