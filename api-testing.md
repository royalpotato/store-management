# Store Management API - Testing Guide

## 🚀 Application Status
✅ **Running on**: http://localhost:8080  
✅ **Database**: H2 Console at http://localhost:8080/h2-console  
✅ **API Documentation**: Available via OpenAPI/Swagger

## 🔐 Test Users
| Username | Password | Role | Permissions |
|----------|----------|------|-------------|
| admin | admin123 | ADMIN | Full access to all endpoints |
| manager | manager123 | MANAGER | Product management + viewing |
| user | user123 | USER | Read-only access |

## 📝 API Endpoints

### Authentication
```bash
# Login to get JWT token
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

### Product Management
```bash
# Get all products (requires authentication)
GET http://localhost:8080/api/products
Authorization: Bearer <your-jwt-token>

# Create new product (ADMIN/MANAGER only)
POST http://localhost:8080/api/products
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "name": "New Product",
  "description": "Product description",
  "price": 29.99,
  "category": "Electronics",
  "brand": "TechBrand",
  "stockQuantity": 100
}

# Update product price (ADMIN/MANAGER only)
PUT http://localhost:8080/api/products/{id}/price
Authorization: Bearer <your-jwt-token>
Content-Type: application/json

{
  "price": 39.99
}

# Get product by ID
GET http://localhost:8080/api/products/{id}
Authorization: Bearer <your-jwt-token>

# Delete product (ADMIN only)
DELETE http://localhost:8080/api/products/{id}
Authorization: Bearer <your-jwt-token>
```

## 🛠 Development Tools

### H2 Database Console
- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:storedb`
- **Username**: `sa`
- **Password**: (leave empty)

### Sample Products Available
The application initializes with 10 sample products in categories:
- Electronics (laptops, smartphones, tablets)
- Books (programming, science fiction)
- Clothing (t-shirts, jeans)
- Home & Garden (coffee makers, desk lamps)

## 🧪 Testing Workflow

1. **Start Application**: `mvn spring-boot:run`
2. **Login**: POST to `/api/auth/login` with test credentials
3. **Copy JWT Token**: From the login response
4. **Test Endpoints**: Use the token in Authorization header
5. **Database Access**: Check H2 console for data verification

## 📊 Monitoring & Actuator
- **Health Check**: http://localhost:8080/actuator/health
- **Info**: http://localhost:8080/actuator/info
- **Metrics**: http://localhost:8080/actuator/metrics

## 🔄 Development Commands
```bash
# Run application
mvn spring-boot:run

# Run tests
mvn test

# Build JAR
mvn clean package

# Run with different profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Happy coding! 🎯