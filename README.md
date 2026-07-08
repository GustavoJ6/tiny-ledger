# Tiny Ledger

A simple REST API to power a tiny ledger, built with Java 21 and Spring Boot 4.

For a detailed breakdown of the approach and design decisions, please refer to the [approach document](approach.md).
The assumptions made during implementation are documented in the [assumptions document](assumptions.md).

## Requirements

- Java 21

## Running the application

```bash
./mvnw spring-boot:run
```

The application will start on port 8080 by default.

## Running the tests

```bash
./mvnw test
```

## API Endpoints

### Record a transaction

```bash
# Deposit
curl -X POST http://localhost:8081/transactions \
  -H "Content-Type: application/json" \
  -d '{"type": "DEPOSIT", "amount": 100.00}'

# Withdrawal
curl -X POST http://localhost:8081/transactions \
  -H "Content-Type: application/json" \
  -d '{"type": "WITHDRAWAL", "amount": 40.00}'
```

### View current balance

```bash
curl http://localhost:8081/balance
```

### View transaction history

```bash
curl http://localhost:8081/transactions
```

## Example flow

```bash
# 1. Deposit 100.00
curl -X POST http://localhost:8081/transactions \
  -H "Content-Type: application/json" \
  -d '{"type": "DEPOSIT", "amount": 100.00}'
# Response (201): {"id":1,"timestamp":...,"type":"DEPOSIT","amount":100.00,"balanceAfterTransaction":100.00}

# 2. Deposit another 50.00
curl -X POST http://localhost:8081/transactions \
  -H "Content-Type: application/json" \
  -d '{"type": "DEPOSIT", "amount": 50.00}'
# Response (201): {"id":2,"timestamp":...,"type":"DEPOSIT","amount":50.00,"balanceAfterTransaction":150.00}

# 3. Withdraw 30.00
curl -X POST http://localhost:8081/transactions \
  -H "Content-Type: application/json" \
  -d '{"type": "WITHDRAWAL", "amount": 30.00}'
# Response (201): {"id":3,"timestamp":...,"type":"WITHDRAWAL","amount":30.00,"balanceAfterTransaction":120.00}

# 4. Check balance
curl http://localhost:8081/balance
# Response (200): 120.00

# 5. View transaction history (most recent first)
curl http://localhost:8081/transactions
# Response (200): [{"id":3,...},{"id":2,...},{"id":1,...}]

# 6. Attempt a withdrawal with insufficient funds
curl -X POST http://localhost:8081/transactions \
  -H "Content-Type: application/json" \
  -d '{"type": "WITHDRAWAL", "amount": 999.00}'
# Response (422): insufficient funds
```
