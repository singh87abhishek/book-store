# BookStore API

**Prerequisites**
- Java 17 (or later) installed and `JAVA_HOME` configured
- Maven (optional if you use the included wrapper) or use the provided `mvnw`/`mvnw.cmd`

**Build & Run (development)**
- From the `bookstore-api` directory, start the app with the Maven wrapper:

```bash
# macOS / Linux
./mvnw spring-boot:run

# Windows (PowerShell)
./mvnw.cmd spring-boot:run
```

**Package and run the JAR**

```bash
./mvnw package
java -jar target/*.jar
```

**Run tests**

```bash
./mvnw test
```

**Important project files**
- Security configuration: [src/main/java/com/bookstore/app/configuration/SecurityConfig.java](src/main/java/com/bookstore/app/configuration/SecurityConfig.java#L1-L200)
- Application entry point: [src/main/java/com/bookstore/app/BookStoreApplication.java](src/main/java/com/bookstore/app/BookStoreApplication.java#L1-L200)
- Application properties: [src/main/resources/application.properties](src/main/resources/application.properties#L1-L200)

**Main API endpoints (overview)**
- `/bookstore-api/auth/**` — Authentication and registration endpoints
- `/bookstore-api/books/**` — Book listing and management endpoints
- `/bookstore-api/cart/**` — Cart operations (add/update/remove/get)

Note: endpoints may be protected by roles according to `SecurityConfig`. Adjust users/roles or security settings in the source if you need open endpoints for development.

**Registering a user (example)**
A minimal example using `curl` (adjust hostname/port if different):

```bash
curl -X POST -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password","email":"test@example.com"}' \
  http://localhost:8080/api/auth/register
```

**Troubleshooting**
- Check logs for stack traces and ensure database configuration in `application.properties` is correct (JDBC URL, username, password).
