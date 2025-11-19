📬 Notification Delivery & Retry Service

A Spring Boot–based Email/SMS notification system with retry mechanism, background processing, scheduler recovery, validation, and JWT security.
<img width="10000" height="2" alt="image" src="https://github.com/user-attachments/assets/c980cd7e-17ac-4de1-9b6d-d689fc4eb2c8" />

🚀 Features
✅ 1. Submit Notifications

Accept Email/SMS requests

Save with QUEUED status

✅ 2. Background Worker

Processes queued messages

Updates status → SENT / FAILED

✅ 3. Retry Logic (Exponential Backoff)
Attempt	Delay
1	60 sec
2	120 sec
3	240 sec
✅ 4. Stuck Message Scheduler

Detects messages stuck in PROCESSING

Resets them to QUEUED after timeout

✅ 5. JWT Security

All endpoints protected

Requires Bearer Token

Includes token generation utility

✅ 6. Input Validation

Valid email format

Valid phone format (E.164 or 10-digit local)

Required field checks

✅ 7. Pagination Support

GET /api/notifications?page=&size=

Max page size restricted

<img width="10000" height="2" alt="image" src="https://github.com/user-attachments/assets/c980cd7e-17ac-4de1-9b6d-d689fc4eb2c8" />



🧱 Architecture

notification-Project
notification-api/
├── config/
├── controller/
├── dto/
├── entity/
├── exception/
├── repository/
├── service/
├── security/
├── util/
└── resources/
notification-worker/
├── adapter/
├── entity/
├── repository/
└── service/

<img width="10000" height="2" alt="image" src="https://github.com/user-attachments/assets/c980cd7e-17ac-4de1-9b6d-d689fc4eb2c8" />

📡 API Endpoints
🔐 Authentication

Generate JWT token by running:

GenerateJwt.main()
<img width="10000" height="2" alt="image" src="https://github.com/user-attachments/assets/c980cd7e-17ac-4de1-9b6d-d689fc4eb2c8" />

📬 Submit Notification
POST /api/notifications
Authorization: Bearer <token>

Request Body

{
  "channel": "EMAIL",
  "recipient": "user@example.com",
  "subject": "Hello",
  "body": "Test message",
  "maxRetries": 3
}


Response

{
  "id": 12,
  "status": "QUEUED"
}

<img width="10000" height="2" alt="image" src="https://github.com/user-attachments/assets/c980cd7e-17ac-4de1-9b6d-d689fc4eb2c8" />

📄 Get All Notifications
GET /api/notifications?page=0&size=20
Authorization: Bearer <token>

<img width="10000" height="2" alt="image" src="https://github.com/user-attachments/assets/c980cd7e-17ac-4de1-9b6d-d689fc4eb2c8" />


🔍 Get Notification by ID
GET /api/notifications/{id}
Authorization: Bearer <token>
<img width="10000" height="2" alt="image" src="https://github.com/user-attachments/assets/c980cd7e-17ac-4de1-9b6d-d689fc4eb2c8" />

🛢 Database Schema (MySQL)

id

channel

recipient

status

retry_count

max_retries

last_error

next_attempt_at

processing_started_at

created_at

updated_at

<img width="10000" height="2" alt="image" src="https://github.com/user-attachments/assets/c980cd7e-17ac-4de1-9b6d-d689fc4eb2c8" />


🔁 Retry Mechanism

Uses exponential backoff

Stores last error

Marks message FAILED after max retries

🕒 Stuck Message Recovery

Scheduler resets any notification stuck in PROCESSING for over 120 seconds.
<img width="10000" height="2" alt="image" src="https://github.com/user-attachments/assets/c980cd7e-17ac-4de1-9b6d-d689fc4eb2c8" />

🔒 Security (JWT)

Implemented using:
Spring Security
JWT Authentication
Bearer Token Filter
application.yml:

security:
  jwt:
    secret: verylong256bitkeyhere...
    issuer: notification-api

Use Bearer Token in Postman.
<img width="10000" height="2" alt="image" src="https://github.com/user-attachments/assets/c980cd7e-17ac-4de1-9b6d-d689fc4eb2c8" />

🧪 Validation Rules
Email

✔ test@example.com
✖ invalid@

SMS

✔ +919876543210
✔ 9876543210
✖ 12345

<img width="10000" height="2" alt="image" src="https://github.com/user-attachments/assets/c980cd7e-17ac-4de1-9b6d-d689fc4eb2c8" />

▶️ Running the Project
Build
mvn clean package

Run
mvn spring-boot:run


or

java -jar target/notification-api-0.0.1-SNAPSHOT.jar

Generate JWT Token

Run:

GenerateJwt.java

<img width="10000" height="2" alt="image" src="https://github.com/user-attachments/assets/c980cd7e-17ac-4de1-9b6d-d689fc4eb2c8" />

🧰 Tech Stack
Component	Technology
Backend	Spring Boot 3
Security	Spring Security + JWT
Database	MySQL
ORM	JPA / Hibernate
Build Tool	Maven
Validation	Jakarta Validation
<img width="10000" height="2" alt="image" src="https://github.com/user-attachments/assets/c980cd7e-17ac-4de1-9b6d-d689fc4eb2c8" />

## 🧪 Postman Collection
You can test all APIs using our Postman collection:

🔗 **Postman Collection:** https://web.postman.co/workspace/My-Workspace~d5ed3689-0f0c-43c0-8219-f160521ec1d4/collection/24607951-8fc66572-3178-4951-b1bd-54b9fad22596?action=share&source=copy-link&creator=24607951
<img width="10000" height="2" alt="image" src="https://github.com/user-attachments/assets/c980cd7e-17ac-4de1-9b6d-d689fc4eb2c8" />

📝 Author

Balaji
Java Full-Stack Developer
GitHub: [your-link](https://github.com/Balaji-Rishi)

<img width="10000" height="2" alt="image" src="https://github.com/user-attachments/assets/c980cd7e-17ac-4de1-9b6d-d689fc4eb2c8" />

⭐ Support

If you found this project helpful, please ⭐ the repository.


