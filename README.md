# Retailer Rewards Program - Reward Calculator

A Spring Boot application that calculates customer reward points based on purchase transactions.

## Reward Points Rule
- **2 points** for every dollar spent over $100 in each transaction.
- **1 point** for every dollar spent between $50 and $100 in each transaction.
- **0 point** for every dollar spent less than $50 in each transaction.

*Example: A $120 purchase = 2x$20 + 1x$50 = 90 points.*

## Production Features
- **Layered Architecture**: Controller -> Service -> Repository.
- **Async API**: REST endpoints use `CompletableFuture` for non-blocking execution.
- **Strict Validation**: Date range validation (max 3 months), null checks, and format validation.
- **Multi-Customer Support**: Calculates rewards for all customers in a single request.
- **Global Exception Handling**: Standardized error responses (e.g., 400 for invalid input, 404 for no data).
- **In-Memory Database**: H2 with automatic data seeding on startup.

## API Documentation

### Calculate Rewards
Calculates the rewards for all customers within a specified date range.

- **Endpoint**: `GET /api/v1/rewards/calculate`
- **Parameters**: 
  - `startDate` (Optional): Start date in `yyyy-MM-dd` format.
  - `endDate` (Optional): End date in `yyyy-MM-dd` format.
  - `numberOfMonths` (Optional): Number of months to calculate rewards for (defaults to 3 if no dates provided).
- **Default Behavior**:
  - If no parameters are provided, it calculates rewards for the last **3 months** from today.
  - If `numberOfMonths` is provided, it calculates rewards for that many months from today.
  - If both `startDate` and `endDate` are provided, it uses that specific range.
- **Validation**:
  - `startDate` cannot be after `endDate`.
- **Success Response (200 OK)**:
```json
[
  {
    "customerId": 1,
    "monthlyRewards": {
      "February": 90,
      "January": 30
    },
    "totalRewards": 120
  },
  {
    "customerId": 2,
    "monthlyRewards": {
      "January": 45
    },
    "totalRewards": 45
  }
]
```

## Running the Project

### Prerequisites
- Java 17 or higher.

### Command Line
Using the included Maven Wrapper:
```bash
./mvnw clean spring-boot:run
```

### Running Tests
```bash
./mvnw test
```
