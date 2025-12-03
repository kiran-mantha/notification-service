# PRD / Prompt for Agent — Notification Service Microservice (Spring Boot, Backend Only)

**Title:** Notification Service Microservice  
**Domain:** Enterprise Backend (Java 21, Spring Boot, PostgreSQL, Kafka/RabbitMQ optional)

---

## 1. Overview

Build a fully production-ready **Notification Service Microservice** in **Spring Boot**, responsible for generating, managing, storing, delivering, retrying, and auditing system notifications across the platform.

The system must support **Email**, **SMS**, **Push Notifications**, **In-App Notifications**, and **Webhook-based notifications**.

Provide **clean architecture**, robust **observability**, **security**, and **scalability**.  
This microservice will operate independently and expose REST endpoints to other services.

---

## 2. Core Responsibilities

1. Receive notification requests from external services through REST or message queues.  
2. Validate request payload based on notification type.  
3. Persist notifications to database.  
4. Dispatch notifications via appropriate channel (Email/SMS/Push/Webhook/In-App).  
5. Maintain status lifecycle: `PENDING`, `PROCESSING`, `SENT`, `FAILED`, `RETRYING`, `CANCELLED`.  
6. Retry mechanism for failed notifications.  
7. Provide history, logs, analytics, and audit trails.  
8. Maintain user notification preferences.  
9. Support templating for dynamic notification bodies.  
10. Provide scheduled dispatching (future-dated notifications).  

---

## 3. Notification Types

### 3.1 Email
- SMTP or external provider (SendGrid, SES, Mailgun)
- HTML/Plain text templates
- Attachments support  

### 3.2 SMS
- Twilio (default), Plivo, MSG91
- Short/long messages  

### 3.3 Push Notifications
- Firebase Cloud Messaging (default)
- APNs/GCM (optional)

### 3.4 In-App Notifications
- Stored and delivered through API  
- Each notification linked to a user  

### 3.5 Webhook Notifications
- POST requests with configurable headers  
- Signature-based verification  

---

## 4. Functional Requirements

### 4.1 API Endpoints

#### Notification Requests
- `POST /notifications/send`  
- `POST /notifications/bulk-send`  
- `POST /notifications/schedule`  

#### Template Management
- `POST /templates`  
- `PUT /templates/{id}`  
- `GET /templates/{id}`  
- `GET /templates`  

#### User Preferences
- `GET /preferences/{userId}`  
- `PUT /preferences/{userId}`  

#### Notification History
- `GET /notifications/{id}`  
- `GET /notifications/user/{userId}`  
- `GET /notifications/search` (filters: date-range, status, type)

#### Admin
- `POST /admin/retry/{notificationId}`  
- `POST /admin/cancel/{notificationId}`  

---

## 5. Database Schema (PostgreSQL)

### Tables

#### `notifications`
- id (UUID)  
- user_id  
- type (EMAIL, SMS, PUSH, IN_APP, WEBHOOK)  
- status  
- template_id  
- payload (JSONB)  
- scheduled_at  
- sent_at  
- retries  
- created_at  
- updated_at  

#### `templates`
- id  
- name  
- channel  
- subject  
- body  
- placeholders (JSONB)  
- created_at  
- updated_at  

#### `user_preferences`
- id  
- user_id  
- email_enabled  
- sms_enabled  
- push_enabled  
- in_app_enabled  
- webhook_enabled  
- created_at  
- updated_at  

#### `webhook_endpoints`
- id  
- user_id  
- url  
- secret_key  
- is_active  

#### `delivery_logs`
- id  
- notification_id  
- request_payload  
- response_payload  
- status_code  
- attempt  
- created_at  

---

## 6. Architecture Requirements

### 6.1 Layered Architecture
- Controller  
- Service  
- Repository  
- Domain Model  
- Adapters (Email/SMS/Push/Webhook)  

### 6.2 Key Modules
- Notification Dispatcher  
- Template Engine  
- Retry Scheduler  
- Preferences Manager  
- Delivery Provider Integrations  
- Observability & Monitoring  

### 6.3 Messaging Integration
Support asynchronous notifications via:
- Kafka topics (`notifications.request`, `notifications.retry`)  
OR  
- RabbitMQ queues  

(Agent may choose either but must implement abstraction.)

---

## 7. Non-Functional Requirements

### 7.1 Security
- JWT authentication  
- Role-based access (ROLE_USER, ROLE_ADMIN)  
- Webhook signed HMAC SHA-256  
- Sensitive properties encrypted  

### 7.2 Scalability
- Stateless services  
- Horizontal scaling  
- Async dispatch + queues  

### 7.3 Observability
- Spring Actuator  
- Prometheus metrics  
- ELK/logging setup  
- Request tracing (OpenTelemetry)  

### 7.4 Performance
- Handle > 10,000 notifications/min  
- Bulk send with batching  

### 7.5 Reliability
- Retry with exponential backoff  
- Dead-letter queue for permanently failed notifications  

---

## 8. Retry & Scheduling

### Retry Strategy
- Max retries = configurable (default 5)  
- Exponential backoff: 1min → 2min → 5min → 10min…  

### Scheduling
- Future-dated notifications using:
  - Spring Scheduler  
  - Or message queue delayed messages  

---

## 9. Template Engine Requirements
- Support Mustache/Freemarker/Thymeleaf  
- Placeholder replacement  
- Versioning of templates  
- Preview API for developers  

---

## 10. User Preferences Logic
Notification must be blocked if:
- User has disabled that channel  
- User is temporarily muted  
- User has DND settings (optional future extension)  

---

## 11. Testing Requirements

### Mandatory Tests
- Unit tests (80%+ coverage)  
- Integration tests  
- Contract tests  
- Load tests for 10k/min throughput  

---

## 12. Deployment Requirements

### Deliverables
- Dockerfile  
- Kubernetes Deployment + Service YAML  
- ConfigMap + Secret management  
- CI/CD pipeline recommendations  

---

## 13. Output Expected From the Agent

The agent should generate:

1. Complete backend project structure  
2. Java code for all layers  
3. Entities, repositories, services, controllers  
4. Retry logic + scheduler  
5. Integration with Email/SMS/Push/Webhook  
6. Templating system  
7. User preferences management  
8. API documentation (OpenAPI/Swagger)  
9. Database migrations (Liquibase/Flyway)  
10. Docker + Kubernetes files  
11. Monitoring integration code  
12. Postman collection  

Everything must be ready for production deployment.

