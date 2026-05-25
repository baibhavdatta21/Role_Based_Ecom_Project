# 🛒 E-Commerce Microservices Backend

A production-ready, cloud-native e-commerce REST API built with **Spring Boot** and a full **microservices architecture**. The system covers user management, product catalogue, shopping cart, and order placement — secured with JWT and role-based access control.

---

## 🏗️ Architecture Overview

```
Client → API Gateway → [User Service | Product Service | Order Service]
                ↑               ↑
         Eureka (Discovery)   Config Server
```

| Service | Port | Responsibility |
|---|---|---|
| `config-server` | 8888 | Centralised configuration via Spring Cloud Config |
| `eureka` | 8761 | Service registry and discovery (Netflix Eureka) |
| `gateway` | 8080 | JWT validation, role-based routing, logging |
| `user` | 8081 | Registration, login, profile management |
| `product` | 8082 | Product CRUD, KMP-based search |
| `order` | 8083 | Cart management and order placement |

---

## 🚀 Features

- **JWT Authentication** — Stateless token-based auth issued by the User Service and validated at the Gateway
- **Role-Based Access Control** — Three roles: `ADMIN`, `SELLER`, `CUSTOMER` enforced at the gateway layer
- **KMP Search Algorithm** — Efficient substring product search using Knuth-Morris-Pratt
- **Inter-Service Communication** — Feign clients between Order and User/Product services
- **Centralised Config** — All service configs served from Spring Cloud Config Server
- **Service Discovery** — All services register with Eureka for load-balanced routing (`lb://`)
- **Comprehensive Tests** — Unit tests for all controllers, services, and utilities

---

## 🔐 Security Model

Public routes (`/api/public/**`) require no token. Authenticated routes (`/api/auth/**`) require a valid JWT Bearer token in the `Authorization` header. The Gateway extracts the user ID and propagates it downstream via the `X-User-ID` header.

| Role | Capabilities |
|---|---|
| `ADMIN` | Full access to all endpoints |
| `SELLER` | Create, update, and delete products |
| `CUSTOMER` | Browse products, manage cart, place orders |

---

## 🛠️ Tech Stack

- **Framework:** Spring Boot 3.x, Spring Cloud
- **Security:** Spring Security, JJWT
- **Database:** MySQL (JPA / Hibernate)
- **Service Mesh:** Netflix Eureka, Spring Cloud Gateway
- **Config:** Spring Cloud Config Server
- **Build:** Maven
- **Testing:** JUnit 5, Mockito, Spring Security Test

---

## ▶️ Running Locally

Start services in this order:

```bash
# 1. Config Server
cd config-server && ./mvnw spring-boot:run

# 2. Eureka
cd eureka && ./mvnw spring-boot:run

# 3. Business services (any order)
cd user    && ./mvnw spring-boot:run
cd product && ./mvnw spring-boot:run
cd order   && ./mvnw spring-boot:run

# 4. Gateway (last)
cd gateway && ./mvnw spring-boot:run
```

All requests should go through the Gateway on **port 8080**.

---

## 📋 API Endpoints

| Endpoint | Method | Authorization | Description |
|---|---|---|---|
| `api/public/users/signup` | POST | Public | Register a new customer account |
| `api/public/users/login` | POST | Public | Login and receive a JWT token |
| `api/public/products/search` | GET | Public | Search products by keyword (KMP) |
| `api/auth/users/signup-admin` | POST | Admin | Create a new Admin account |
| `api/auth/users` | GET | Admin | Get all users |
| `api/auth/users/{id}` | GET | Admin | Get a user by ID |
| `api/auth/users/{id}` | PUT | Admin, Customer, Seller | Update a user profile |
| `api/auth/users/{id}` | DELETE | Admin, Customer, Seller | Delete a user account |
| `api/auth/products` | POST | Admin, Seller | Create a new product |
| `api/auth/products/{id}` | PUT | Admin, Seller | Update a product |
| `api/auth/products/{id}` | GET | Admin, Customer, Seller | Get a product by ID |
| `api/auth/products/{id}` | DELETE | Admin, Seller | Delete a product |
| `api/auth/cart` | POST | Admin, Customer | Add a product to cart |
| `api/auth/cart/items/{productId}` | DELETE | Admin, Customer | Remove a product from cart |
| `api/auth/cart` | GET | Admin, Customer | View cart contents |
| `api/auth/orders` | POST | Admin, Customer | Place an order from cart |
