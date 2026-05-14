# Mini Digital Banking System

Production-style backend banking system built using Spring Boot.

Features include:
- JWT Authentication
- Account Management
- Customer Management
- Secure Transfers
- Ledger History
- Audit Logging
- Role-based Authorization
- Flyway Database Migration
- Concurrency-safe transfers

## Tech Stack

- Java 21
- Spring Boot
- Spring Security
- JWT
- PostgreSQL
- Flyway
- Maven
- Hibernate / JPA

## Architecture

Modular architecture:

- Auth Module
- Account Module
- Transfer Module
- Ledger Module
- Audit Module
- Customer Module

- ## Features

### Authentication
- User Registration
- JWT Login
- Password Change
- Role-based Authorization

### Accounts
- Create Account
- Deposit
- Withdraw
- Balance Inquiry
- Account Status Update

### Transfers
- Account-to-Account Transfers
- Pessimistic Locking
- Reference ID generation

### Ledger
- Transaction History
- Ordered Ledger Entries

### Audit
- Success/Failure Audit Logs
- IP Tracking

## Security Features

- JWT Authentication
- BCrypt Password Encryption
- Role-based Access Control
- Ownership Validation
- Concurrency-safe Transfers
- Pessimistic Row Locking

## APIs

| Method | Endpoint | Description | Module |
|---|---|---|---|
| POST | /api/auth/register-user | Register | Auth |
| POST | /api/auth/login-user | Login | Auth |
| PUT | api/customers/update | Update | Customer |
| GET | /api/customers/myself | Get Myself | Customer |
| POST | /api/accounts | Create Account | Account |
| GET | /api/accounts/my-accounts | Get My Accounts | Account |
| GET | /api/accounts/number/{accountNumber} | Get By Account Number | Account |
| POST | /api/accounts/deposit | Deposit | Account |
| POST | /api/accounts/withdraw | Withdraw | Account |
| GET | /api/accounts/balance/{accountId} | Balance Inquiry | Account |
| POST | /api/transfers | Account to Account Transfer | Transfer |
| GET | api/transfers/{txnId} | Get Transfer Details | Transfer |
| GET | /api/ledger/mini-statement/{accountId} | Mini Statement | Ledger |

## Swagger API Documentation

### Swagger Home
![Swagger Home](screenshots/swagger-home.png)

### JWT Token
![JWT Token](screenshots/jwt-auth.png)

### User Login
![User Login](screenshots/user-login.png)

### Get Customer
![Get Customer](screenshots/get-customer.png)

### Get Accounts
![Get Accounts](screenshots/get-accounts.png)

### Account Withdraw
![Account Withdraw](screenshots/account-withdraw.png)

### Get Account Balance
![Get Account Balance](screenshots/get-balance.png)

### Account to Account Transfer
![Account Transfer](screenshots/transfer-api.png)

### Ledger History
![Account Statement](screenshots/ledger-history.png)


## Run Project

```bash
git clone <repo>
cd mini-bank
mvn clean install
mvn spring-boot:run
