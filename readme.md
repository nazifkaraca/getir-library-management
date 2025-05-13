# 📚 Library Management System

A full-featured **Library Management System** developed as the capstone project for the **Patika.dev & Getir Java Spring Boot Bootcamp**. This application empowers librarians to manage books, users, and borrowing operations with a secure, testable, and scalable Spring Boot backend.

---

## 🧭 Project Overview

This project is built with **Spring Boot 3** and **Java 21**, leveraging modern development practices such as JWT-based security, RESTful APIs, containerization with Docker, reactive streams, and CI-friendly testing. Users are authenticated and authorized based on their roles (`ROLE_USER` or `ROLE_LIBRARIAN`) to ensure controlled access to features.

---

## 🎯 Key Features

### 📕 Book Management
- ✅ Add, update, delete, and view books
- 🔍 Search functionality with filters: `title`, `author`, `ISBN`, `genre`
- 📄 Pagination and sorting support
- 🔄 Real-time book availability via **Spring WebFlux**

### 👤 User Management
- 👥 Register and manage users with different roles
- 🔐 Role-based access (`USER`, `LIBRARIAN`)
- 🧾 View and update user details (by librarians)
- ❌ Soft delete users

### 🔄 Borrowing System
- 📚 Borrow & return operations with availability check
- 🗓️ Track borrowing, due, and return dates
- 📋 Personal and global borrowing history
- ⚠️ Overdue detection & reporting

---

## 🛠️ Technology Stack

| Layer              | Technology                       |
|-------------------|-----------------------------------|
| Language           | Java 21 (Amazon Corretto 21.0.7)                         |
| Framework          | Spring Boot 3                    |
| Persistence        | Spring Data JPA + Hibernate      |
| Security           | Spring Security + JWT            |
| Reactive Updates   | Spring WebFlux                   |
| Database           | PostgreSQL (prod), H2 (test)     |
| API Documentation  | Swagger / OpenAPI 3              |
| Containerization   | Docker & Docker Compose          |
| Testing            | JUnit 5, Spring Boot Test         |
| Logging            | SLF4J + Logback                  |
| Build Tool         | Maven                            |

---

## Before Running

Run this command to build .jar file:
```bash
mvn clean package -DskipTests 
```


## 🧪 Testing Strategy

- ✔️ **Unit Tests** for services and utility layers
- ✔️ **Integration Tests** for API endpoints and database interactions
- ✔️ **In-memory Testing** with H2 Database
- ✔️ **All tests pass** prior to final build

Run all tests:
```bash
  mvn test # or
  mvn clean verify
```
---

## 🔐 Security

- JWT-based authentication and authorization
- Role-based access control for endpoints
- Password encryption

---

## 📦 Docker Setup

```bash
# Build the app and run with Docker Compose
docker-compose up --build
````

> Make sure Docker and Docker Compose are installed on your system.

---

## 📡 API Documentation

Swagger UI is available at:

```
http://localhost:8070/swagger-ui/index.html
```

You can explore and test all endpoints interactively.

---

## 🗂️ Project Structure

```
src/
├── controller/          # REST endpoints
├── service/             # Business logic
├── repository/          # Data access layer
├── dto/                 # Request/response objects
├── entity/              # JPA entities
├── config/              # Security & Swagger configs
├── exception/           # Global error handling
└── test/                # Unit & integration tests
```

---

## 📑 API Documentation

The Swagger UI provides interactive API documentation:
```
http://localhost:8080/swagger-ui/index.html
````

---

## 📬 Postman Collection

A complete Postman Collection is included to test all features: <h3><a href="https://postman.co/workspace/Personal-Workspace~df22195b-a6bf-4bb0-8f17-2502150396c2/collection/35081217-2e7edf5a-ccfd-4666-8b56-a3d826bb14bd?action=share&creator=35081217&active-environment=35081217-fc0abc9e-3263-4c57-9621-4ff61e38a93e">Go Testing</a></h3>

* Grouped by: Auth, User, Book, Borrowing
* Includes example requests and responses
* Ready-to-use JWT tokens for testing protected routes

---

## 🗃️ Database Schema (ERD)

![Entity Relationship Diagram](https://github.com/user-attachments/assets/55a993a1-ce77-4d19-8ecc-1aedab0a03c1)

---

## ✅ Development & Deployment Notes

* Follows **Clean Code** and **SOLID** principles
* All sensitive data secured via **JWT**
* Logging via **SLF4J** and **Logback**
* Docker-ready for deployment
* Uses **Git** for version control

---

## 🧹 Clean Code Practices

* ✅ Follows **SOLID** and **Clean Architecture**
* ✅ Uses **Lombok** to reduce boilerplate
* ✅ Well-documented code and API
* ✅ Logs key actions and exceptions with structured logging

---

## 🤝 Contribution & Licensing

This project was developed as a part of the **Patika.dev & Getir Java Spring Boot Bootcamp**
It is not intended for commercial use and does not include a license.

---

## 👨‍💻 Author

**Nazif Karaca**
Backend Developer
🌐 [LinkedIn](https://www.linkedin.com/in/nazifkaraca/) | 📨 [Email](mailto:nazif808@gmail.com)


