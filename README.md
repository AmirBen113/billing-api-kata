# 🛒 Billing & Tax API - Technical Leader Kata

This project is a high-reliability Java API designed to calculate shopping cart invoices with complex tax rules. Developed with a focus on **financial precision**, **resilience**, and **cloud-native standards**.

---

## 🏗 Architectural Overview

The application follows the **Hexagonal Architecture** (Ports & Adapters) pattern to ensure a strict decoupling between business rules and technical infrastructure:

* **Domain Layer**: Contains pure business logic (Tax strategies, 0.05 rounding rules) and immutable `Records`. Zero dependencies on external frameworks.
* **Infrastructure Layer**: Implements **Adapters** (REST Controllers, WebClient, Resilience4j).
* **Application Layer**: Orchestrates use cases and ensures **security/consistency** by reconciling client cart data with the official product catalog.

---

## 🛡️ Resilience & Stability (Technical Lead Focus)

To meet enterprise-grade requirements, the API includes advanced stability patterns:

* **Circuit Breaker (Resilience4j)**: Protects the system from cascading failures when the external Catalog API is down or slow.
* **Graceful Fallback**: If the catalog service is unreachable, the system enters a degraded mode to ensure business continuity.
* **Global Exception Handling**: Implements **RFC 7807 (Problem Details)** to return structured, professional JSON errors instead of raw stack traces.

---

## 📊 Business Rules (Taxes)

| Product Type | VAT Rate | Import Tax |
| :--- | :--- | :--- |
| **Food & Medicine** | 0% | +5% |
| **Books** | 10% | +5% |
| **Others** | 20% | +5% |

### Rounding Rule
All taxes are rounded up to the nearest **0.05**.

**Formula:**
$$P_{ttc} = P_{ht} + \lceil \frac{P_{ht} \times \text{tva}}{100} \rceil_{0.05} + \lceil \frac{P_{ht} \times \text{ti}}{100} \rceil_{0.05}$$

---

## 🩺 Observability & Cloud Readiness

The API is **Cloud-Native** and ready for Kubernetes deployment:

* **Actuator Endpoints**: `/actuator/health` and `/actuator/info` are exposed.
* **Custom Health Indicator**: A dedicated probe monitors the connectivity with the External Catalog API.
* **Liveness & Readiness**: Fully compatible with K8s probes to handle traffic only when dependencies are healthy.
* **Graceful Shutdown**: Configured to complete pending requests before stopping.

---

## 🚀 Getting Started

### Prerequisites
* **JDK 21**
* **Maven 3.9+**

### Build and Run
```bash
mvn clean install
mvn spring-boot:run
```
## 📡 API Endpoints

| Action | Method | Endpoint | Description |
| :--- | :--- | :--- | :--- |
| **Get Catalog** | `GET` | `/external-api/products` | Mocked data providing 10+ items. |
| **Generate Invoice** | `POST` | `/api/billing/invoice` | Processes a cart and returns full tax details. |
| **Health Check** | `GET` | `/actuator/health` | Deep health check including external API status. |

## 🧪 Testing Strategy

* **Unit Tests**: Coverage of complex tax rounding logic (Strategy pattern).
* **Integration Tests**: End-to-end flow using `@SpringBootTest` with a dedicated **test profile** and dynamic port injection (`${local.server.port}`).
* **Resilience Tests**: Validated fallback behavior when external dependencies are unavailable.

## 💡 Author's Note

As a **Technical Leader (12+ years exp)**, I prioritized **maintainability** and **observability**. By choosing a Hexagonal approach, I ensured that the core business logic remains testable in isolation, while the infrastructure is robust enough to handle real-world distributed system failures (Circuit Breaker, Health Monitoring).