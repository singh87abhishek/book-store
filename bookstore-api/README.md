# BookStore API

**Prerequisites**
- Java 17 (or later) installed and `JAVA_HOME` configured
- Maven

**Build & Run (development)**
- From the `bookstore-api` directory, start the app with the Maven wrapper:

```bash
# macOS / Linux
mvn spring-boot:run

# Windows (PowerShell)
mvn spring-boot:run
```

**Package and run the JAR**

```bash
mvn package
java -jar target/*.jar
```

**Run tests**

```bash
mvn test
```

**Important project files**
- Security configuration: [src/main/java/com/bookstore/app/configuration/SecurityConfig.java](src/main/java/com/bookstore/app/configuration/SecurityConfig.java#L1-L200)
- Application entry point: [src/main/java/com/bookstore/app/BookStoreApplication.java](src/main/java/com/bookstore/app/BookStoreApplication.java#L1-L200)
- Application properties: [src/main/resources/application.properties](src/main/resources/application.properties#L1-L200)

**Main API endpoints (overview)**
- `/bookstore-api/auth/**` — Authentication and registration endpoints
- `/bookstore-api/books/**` — Book listing and management endpoints
- `/bookstore-api/cart/**` — Cart operations (add/update/remove/get)
- `/bookstore-api/order/**` — Get and checkout

Note: endpoints may be protected by roles according to `SecurityConfig`. Adjust users/roles or security settings in the source if you need open endpoints for development.

**Data already feeded**
On the first startup (empty DB), application will automatically seed default users and books.
This ensures your app always has baseline data (admin account + sample books) available for testing or demo purposes. (See DataSeeder.java)
ADMIN user - admin/admin123
USER user - Abhi/abhi123


**Registering a user (example)**
A minimal example using `curl` (adjust hostname/port if different):

```bash
curl -X POST -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password","email":"test@example.com"}' \
  http://localhost:8080/api/auth/register
```


