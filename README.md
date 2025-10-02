# Store Management API

This is a simple REST API for managing store products. It's built with Spring Boot 3.5.6 and Java 17. The API lets you add, update, delete, and view products. It uses JWT for authentication and has three user roles: USER, MANAGER, and ADMIN.

## Features

- Add, update, delete, and view products
- Change product prices
- Track and update stock
- Search and filter products
- Pagination for product lists
- JWT authentication
- Role-based access (USER, MANAGER, ADMIN)
- Passwords are hashed
- Uses Java 17 features
- Basic error handling
- Health check endpoints
- Data validation

## Project Structure

```
src/
  main/java/com/store/management/api/
    StoreManagementApplication.java
    config/
      DataInitializer.java
    controller/
      AuthController.java
      ProductController.java
    dto/
      AuthResponse.java
      CreateProductRequest.java
      LoginRequest.java
      ProductResponse.java
      UpdatePriceRequest.java
    exception/
      ErrorResponse.java
      GlobalExceptionHandler.java
    model/
      Product.java
      Role.java
      User.java
    repository/
      ProductRepository.java
      UserRepository.java
    security/
      JwtAuthenticationEntryPoint.java
      JwtAuthenticationFilter.java
      SecurityConfig.java
    service/
      AuthenticationService.java
      CustomUserDetailsService.java
      ProductService.java
  test/java/com/store/management/api/service/
    ProductServiceTest.java
```

## Requirements

- Java 17 or newer
- Maven 3.6 or newer

## How to Run

1. Clone the repo:
   ```
   git clone https://github.com/royalpotato/store-management.git
   cd store-management
   ```
2. Build the app:
   ```
   mvn clean compile
   ```
3. Run tests:
   ```
   mvn test
   ```
4. Start the app:
   ```
   mvn spring-boot:run
   ```
   The app will be at http://localhost:8080

5. H2 Database Console (for dev):
   - Go to http://localhost:8080/h2-console
   - JDBC URL: jdbc:h2:mem:storedb
   - Username: sa
   - Password:

## Authentication

There are three default users:

| Username | Password   | Role    |
|----------|------------|---------|
| admin    | admin123   | ADMIN   |
| manager  | manager123 | MANAGER |
| user     | user123    | USER    |

To get a JWT token, send a POST to `/api/auth/login` with JSON like:
```
{
  "username": "admin",
  "password": "admin123"
}
```
Use the token in the `Authorization` header for other requests:
```
Authorization: Bearer YOUR_JWT_TOKEN
```

## API Endpoints

- POST /api/auth/login - Login, get JWT
- POST /api/auth/logout - Logout (just discard token)
- GET /api/products - List products (paginated)
- GET /api/products/{id} - Get product by ID
- GET /api/products/search?name={name} - Search by name
- GET /api/products/category/{category} - Get by category
- GET /api/products/price-range?minPrice={min}&maxPrice={max} - Get by price range
- POST /api/products - Add product (MANAGER, ADMIN)
- PUT /api/products/{id}/price - Change price (MANAGER, ADMIN)
- PUT /api/products/{id}/stock?quantity={qty} - Update stock (MANAGER, ADMIN)
- GET /api/products/low-stock?threshold={threshold} - Low stock (MANAGER, ADMIN)
- DELETE /api/products/{id} - Delete product (ADMIN)
- GET /actuator/health - Health check
- GET /actuator/info - Info
- GET /actuator/metrics - Metrics

## Example Usage

You can use the `test-requests.http` file in this project to try out the API endpoints if you have the REST Client extension in VS Code. Or use curl, for example:

Login and get a token:
```
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "manager", "password": "manager123"}'
```

Add a product:
```
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"name": "Wireless Mouse", "description": "Ergonomic wireless mouse", "price": 45.99, "category": "Electronics", "stockQuantity": 50}'
```

## Tests and Coverage

- Run all tests:
  ```
  mvn test
  ```
- Run a specific test:
  ```
  mvn test -Dtest=ProductServiceTest
  ```
- Generate code coverage report:
  ```
  mvn jacoco:report
  ```
  Open `target/site/jacoco/index.html` in your browser to see the report.


