# Pontera QA Automation Assignment

This project is a QA automation suite that validates both **UI workflows** on the Pontera advisor platform and **RESTful API functionality** using Java-based tools.

## ✨ Overview

- **UI Automation**: End-to-end testing of advisor login and client management using [Microsoft Playwright for Java](https://playwright.dev/java/).
- **API Testing**: Functional testing of the public [Restful Booker API](https://restful-booker.herokuapp.com/) using [RestAssured](https://rest-assured.io/).
- **Framework**: Built on **Spring Boot** for dependency injection and environment management, enabling smooth integration and scalable configuration.

## 🔧 Tech Stack

| Layer       | Tools / Frameworks               |
|-------------|----------------------------------|
| Language    | Java 17+                         |
| UI Testing  | Playwright (Java)                |
| API Testing | RestAssured                      |
| Test Engine | JUnit 5                          |
| Assertions  | AssertJ                          |
| DI & Config | Spring Boot                      |

## 🧪 Test Coverage

### ✅ API Tests
- **Create Booking**: Validates creation of a new booking and presence in the list.
- **Update Booking**: Modifies booking dates and validates the updated values in response.

### ✅ UI Tests
- **Without Session Storage**: Full login flow followed by navigation to "Add New Client".
- **With Session Storage**: Reuses login session via saved state for faster test execution:
  - Navigate to Clients page and click "Add New Client"
  - Directly navigate to "Add New Client" page

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven or Gradle
- Internet connection for live test targets

### Configuration

Set credentials in your `application.properties`:
```properties
auth.email=your-email@example.com
auth.password=your-password
playwright.headless=true
```

### Run Tests

Using Maven:
```bash
mvn clean test
```

## ✅ Notes

- `storageState.json` is used to persist Playwright session state for faster UI tests.
- Tests assume live systems (`pontera.com` and `restful-booker.herokuapp.com`) are available during execution.
