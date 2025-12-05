# Notification Service - Usage Guide

## Quick Start

### 1. Start the Application

```bash
# Using Docker Compose (Recommended)
docker-compose up -d

# Or start dependencies only and run locally
docker-compose up -d postgres kafka
mvn spring-boot:run
```

### 2. Access API Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/prometheus

---

---

## Common Use Cases

### 1. Send an Email Notification

```bash
curl -X POST http://localhost:8080/notifications/send \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "type": "EMAIL",
    "payload": {
      "to": "user@example.com",
      "subject": "Welcome to Our Service",
      "body": "<h1>Welcome!</h1><p>Thanks for joining us.</p>"
    }
  }'
```

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "user123",
  "type": "EMAIL",
  "status": "PENDING",
  "payload": {...},
  "retries": 0,
  "createdAt": "2025-12-01T18:39:06"
}
```

### 2. Send an SMS Notification

```bash
curl -X POST http://localhost:8080/notifications/send \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "type": "SMS",
    "payload": {
      "to": "+1234567890",
      "message": "Your verification code is 123456"
    }
  }'
```

### 3. Send a Push Notification

```bash
curl -X POST http://localhost:8080/notifications/send \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "type": "PUSH",
    "payload": {
      "deviceToken": "fcm-device-token-here",
      "title": "New Message",
      "body": "You have a new message from John"
    }
  }'
```

### 4. Send In-App Notification

```bash
curl -X POST http://localhost:8080/notifications/send \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "type": "IN_APP",
    "payload": {
      "title": "System Update",
      "message": "New features are now available",
      "actionUrl": "/features"
    }
  }'
```

### 5. Send Webhook Notification

```bash
curl -X POST http://localhost:8080/notifications/send \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "type": "WEBHOOK",
    "payload": {
      "event": "order.completed",
      "orderId": "ORD-12345",
      "amount": 99.99
    }
  }'
```

---

## Using Templates

### 1. Create a Template

```bash
curl -X POST http://localhost:8080/templates \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "welcome-email",
    "channel": "EMAIL",
    "subject": "Welcome {{name}}!",
    "body": "<h1>Hello {{name}}</h1><p>Welcome to {{companyName}}!</p>",
    "placeholders": ["name", "companyName"]
  }'
```

### 2. Send Notification Using Template

```bash
curl -X POST http://localhost:8080/notifications/send \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "type": "EMAIL",
    "templateId": 1,
    "payload": {
      "to": "user@example.com",
      "subject": "Welcome",
      "name": "John Doe",
      "companyName": "Acme Corp"
    }
  }'
```

### 3. List All Templates

```bash
curl -X GET "http://localhost:8080/templates?page=0&size=20" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Scheduling Notifications

### Schedule for Future Delivery

```bash
curl -X POST http://localhost:8080/notifications/schedule \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "type": "EMAIL",
    "scheduledAt": "2025-12-02T10:00:00",
    "payload": {
      "to": "user@example.com",
      "subject": "Reminder",
      "body": "This is your scheduled reminder"
    }
  }'
```

---

## Bulk Notifications

### Send Multiple Notifications at Once

```bash
curl -X POST http://localhost:8080/notifications/bulk-send \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "notifications": [
      {
        "userId": "user1",
        "type": "EMAIL",
        "payload": {
          "to": "user1@example.com",
          "subject": "Announcement",
          "body": "Important update for all users"
        }
      },
      {
        "userId": "user2",
        "type": "SMS",
        "payload": {
          "to": "+1234567890",
          "message": "Important update"
        }
      }
    ]
  }'
```

---

## Managing User Preferences

### 1. Get User Preferences

```bash
curl -X GET http://localhost:8080/preferences/user123 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Response:**
```json
{
  "id": 1,
  "userId": "user123",
  "emailEnabled": true,
  "smsEnabled": true,
  "pushEnabled": true,
  "inAppEnabled": true,
  "webhookEnabled": true
}
```

### 2. Update User Preferences

```bash
curl -X PUT http://localhost:8080/preferences/user123 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "emailEnabled": true,
    "smsEnabled": false,
    "pushEnabled": true,
    "inAppEnabled": true,
    "webhookEnabled": false
  }'
```

**Note:** If a user disables a channel, notifications of that type will be rejected.

---

## Querying Notifications

### 1. Get Notification by ID

```bash
curl -X GET http://localhost:8080/notifications/550e8400-e29b-41d4-a716-446655440000 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 2. Get All Notifications for a User

```bash
curl -X GET "http://localhost:8080/notifications/user/user123?page=0&size=20" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 3. Search Notifications with Filters

```bash
# By status
curl -X GET "http://localhost:8080/notifications/search?status=SENT" \
  -H "Authorization: Bearer YOUR_TOKEN"

# By type
curl -X GET "http://localhost:8080/notifications/search?type=EMAIL" \
  -H "Authorization: Bearer YOUR_TOKEN"

# By date range
curl -X GET "http://localhost:8080/notifications/search?startDate=2025-12-01T00:00:00&endDate=2025-12-01T23:59:59" \
  -H "Authorization: Bearer YOUR_TOKEN"

# Combined filters
curl -X GET "http://localhost:8080/notifications/search?userId=user123&status=FAILED&type=EMAIL" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Admin Operations

**Note:** Requires `ROLE_ADMIN` in JWT token.

### 1. Retry Failed Notification

```bash
curl -X POST http://localhost:8080/admin/retry/550e8400-e29b-41d4-a716-446655440000 \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

### 2. Cancel Notification

```bash
curl -X POST http://localhost:8080/admin/cancel/550e8400-e29b-41d4-a716-446655440000 \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
```

---

## Notification Statuses

| Status | Description |
|--------|-------------|
| `PENDING` | Notification created, waiting to be processed |
| `PROCESSING` | Currently being sent |
| `SENT` | Successfully delivered |
| `FAILED` | Delivery failed |
| `RETRYING` | Being retried after failure |
| `CANCELLED` | Cancelled by admin |

---

## Retry Mechanism

Failed notifications are automatically retried with exponential backoff:

| Attempt | Wait Time |
|---------|-----------|
| 1 | 1 minute |
| 2 | 2 minutes |
| 3 | 5 minutes |
| 4 | 10 minutes |
| 5 | 20 minutes |

After 5 failed attempts, notifications remain in `FAILED` status and require manual intervention.

---

## Configuration

### Email Provider (SMTP)

Set in `application.yml` or environment variables:

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
```

### SMS Provider (Twilio)

```yaml
notification:
  providers:
    sms:
      account-sid: ${TWILIO_ACCOUNT_SID}
      auth-token: ${TWILIO_AUTH_TOKEN}
      from-number: ${TWILIO_FROM_NUMBER}
```

### Push Notifications (FCM)

```yaml
notification:
  providers:
    push:
      server-key: ${FCM_SERVER_KEY}
```

---

## Monitoring

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

### Prometheus Metrics

```bash
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/prometheus
```

### Application Logs

```bash
# Docker Compose
docker-compose logs -f notification-service

# Kubernetes
kubectl logs -f deployment/notification-service
```

---

## Testing with Postman

Import the provided `postman_collection.json` file:

1. Open Postman
2. Click **Import**
3. Select `postman_collection.json`
4. Set the `jwt_token` variable in the collection
5. Start testing!

---

## Troubleshooting

### Notification Stuck in PENDING

- Check Kafka is running: `docker-compose ps kafka`
- Check consumer logs: `docker-compose logs notification-service`

### Authentication Failed

- Verify JWT token is valid and not expired
- Ensure token includes required `sub` and `roles` claims

### Email Not Sending

- Verify SMTP credentials in `application.yml`
- Check delivery logs in database: `SELECT * FROM delivery_logs`

### Database Connection Error

- Ensure PostgreSQL is running: `docker-compose ps postgres`
- Verify connection string in environment variables

---

## Production Deployment

### Kubernetes

```bash
# Apply configurations
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/deployment.yaml

# Check status
kubectl get pods
kubectl get services

# View logs
kubectl logs -f deployment/notification-service
```

### Environment Variables

Required for production:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/notification_db
SPRING_DATASOURCE_USERNAME=your-username
SPRING_DATASOURCE_PASSWORD=your-password
SPRING_KAFKA_BOOTSTRAP_SERVERS=your-kafka-host:9092
JWT_SECRET=your-secure-secret-key
MAIL_USERNAME=your-email@example.com
MAIL_PASSWORD=your-email-password
TWILIO_ACCOUNT_SID=your-twilio-sid
TWILIO_AUTH_TOKEN=your-twilio-token
FCM_SERVER_KEY=your-fcm-key
```

---

## Support

For issues or questions:
- Check logs: `docker-compose logs -f`
- Review API docs: http://localhost:8080/swagger-ui.html
- Check health: http://localhost:8080/actuator/health
