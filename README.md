# Notification Service Microservice

Enterprise-grade notification service built with Spring Boot 3.2, Java 21, PostgreSQL, and Kafka.

## Features

- **Multi-Channel Support**: Email, SMS, Push, In-App, and Webhook notifications
- **Template Engine**: Mustache-based templating with placeholder support
- **User Preferences**: Per-user notification channel preferences
- **Retry Mechanism**: Exponential backoff retry strategy for failed notifications
- **Scheduling**: Support for future-dated notifications
- **Audit Trail**: Complete delivery logs and history

- **Observability**: Prometheus metrics, health checks, and logging
- **Scalability**: Stateless design with Kafka for async processing

## Tech Stack

- Java 21
- Spring Boot 3.2
- PostgreSQL 16
- Kafka
- Liquibase
- OpenAPI/Swagger
- Docker & Kubernetes

## Prerequisites

- Java 21
- Maven 3.8+
- Docker & Docker Compose
- PostgreSQL 16
- Kafka

## Quick Start

### Using Docker Compose

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f notification-service

# Stop services
docker-compose down
```

### Local Development

```bash
# Start PostgreSQL and Kafka
docker-compose up -d postgres kafka

# Run the application
mvn spring-boot:run
```

## API Documentation

Once the application is running, access:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI Spec: http://localhost:8080/v3/api-docs

## API Endpoints

### Notifications
- `POST /notifications/send` - Send a notification
- `POST /notifications/bulk-send` - Send bulk notifications
- `POST /notifications/schedule` - Schedule a notification
- `GET /notifications/{id}` - Get notification by ID
- `GET /notifications/user/{userId}` - Get user notifications
- `GET /notifications/search` - Search notifications with filters

### Templates
- `POST /templates` - Create template
- `PUT /templates/{id}` - Update template
- `GET /templates/{id}` - Get template
- `GET /templates` - List templates

### Preferences
- `GET /preferences/{userId}` - Get user preferences
- `PUT /preferences/{userId}` - Update user preferences

### Admin
- `POST /admin/retry/{notificationId}` - Retry failed notification
- `POST /admin/cancel/{notificationId}` - Cancel notification

## Configuration

Key configuration properties in `application.yml`:

```yaml
notification:
  retry:
    max-attempts: 5
    backoff-multiplier: 2
  providers:
    email:
      enabled: true
    sms:
      enabled: true
      provider: twilio
    push:
      enabled: true
      provider: fcm
```

## Environment Variables

- `SPRING_DATASOURCE_URL` - Database URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `SPRING_KAFKA_BOOTSTRAP_SERVERS` - Kafka servers
- `JWT_SECRET` - JWT signing secret
- `TWILIO_ACCOUNT_SID` - Twilio account SID
- `TWILIO_AUTH_TOKEN` - Twilio auth token
- `FCM_SERVER_KEY` - Firebase server key

## Kubernetes Deployment

```bash
# Apply configurations
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/deployment.yaml

# Check status
kubectl get pods
kubectl get services
```

## Monitoring

- Health: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/metrics
- Prometheus: http://localhost:8080/actuator/prometheus

## Database Schema

The service uses Liquibase for database migrations. Schema includes:
- `notifications` - Notification records
- `templates` - Notification templates
- `user_preferences` - User notification preferences
- `webhook_endpoints` - Webhook configurations
- `delivery_logs` - Delivery audit logs



## Retry Strategy

Failed notifications are retried with exponential backoff:
- Attempt 1: 1 minute
- Attempt 2: 2 minutes
- Attempt 3: 5 minutes
- Attempt 4: 10 minutes
- Attempt 5: 20 minutes

## Example Requests

### Send Email Notification

```bash
curl -X POST http://localhost:8080/notifications/send \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "type": "EMAIL",
    "payload": {
      "to": "user@example.com",
      "subject": "Welcome",
      "body": "Welcome to our service!"
    }
  }'
```

### Create Template

```bash
curl -X POST http://localhost:8080/templates \
  -H "Content-Type: application/json" \
  -d '{
    "name": "welcome-email",
    "channel": "EMAIL",
    "subject": "Welcome {{name}}",
    "body": "Hello {{name}}, welcome to our service!",
    "placeholders": ["name"]
  }'
```

## License

Proprietary - Enterprise Use Only
