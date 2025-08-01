# 🚚 Delivery Management System

A comprehensive microservices-based delivery management system with real-time tracking, driver management, and performance analytics.

## 🏗️ Architecture Overview

### Microservices Architecture
- **API Gateway**: Centralized routing and authentication
- **Eureka Server**: Service discovery and registration
- **Authentication Service**: JWT-based authentication and user management
- **Order Service**: Order management and processing
- **Tracking Service**: Real-time GPS tracking and delivery monitoring
- **Driver Service**: Driver management and performance tracking
- **User Service**: Customer and user profile management
- **Product Service**: Product catalog and management
- **Inventory Service**: Stock management and warehouse operations
- **Employee Service**: Employee management
- **Notification Service**: Real-time notifications

### Infrastructure
- **PostgreSQL**: Primary database for all services
- **Redis**: Caching and session management
- **Kafka**: Event streaming and real-time messaging
- **Zookeeper**: Kafka coordination

## 🚀 Key Features

### 🔐 Authentication & Authorization
- JWT-based authentication
- Role-based access control (Admin, Warehouse Staff, Driver)
- Secure API endpoints with token validation

### 📦 Order Management
- Create, update, and track orders through multiple statuses
- Order history and delivery status tracking
- Integration with driver and warehouse services

### 🗺️ Real-Time Tracking
- GPS tracking with simulated coordinates
- Real-time location updates via WebSocket
- Order tracking with delivery status
- Redis caching for current driver locations
- Kafka integration for location updates

### 👨‍💼 Driver Management
- Driver profile management
- Vehicle information tracking
- Availability and status management
- Performance metrics and analytics
- Driver assignment to orders
- Location-based driver search

### 📊 Performance Analytics
- Driver performance tracking
- Delivery statistics and metrics
- Revenue reports and analytics
- Real-time dashboard data

### 🏪 Inventory Management
- Product catalog management
- Stock tracking and updates
- Warehouse location management
- Inventory assignment to orders

## 🛠️ Technology Stack

### Backend
- **Spring Boot 3.2.0**: Core framework
- **Spring Cloud**: Microservices orchestration
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Data persistence
- **Spring WebSocket**: Real-time communication
- **Spring Kafka**: Event streaming
- **Eureka**: Service discovery
- **PostgreSQL**: Primary database
- **Redis**: Caching and session storage
- **Kafka**: Message streaming

### Frontend (Planned)
- **ReactJS**: Frontend framework
- **Ant Design**: UI component library
- **Axios**: HTTP client
- **Leaflet.js**: Map visualization
- **WebSocket**: Real-time updates

### Reporting
- **JasperReports**: PDF report generation

## 📁 Project Structure

```
├── api-gateway/           # API Gateway service
├── auth-service/          # Authentication service
├── order-service/         # Order management
├── tracking-service/      # GPS tracking service
├── driver-service/        # Driver management
├── user-service/          # User management
├── product-service/       # Product catalog
├── inventory-service/     # Inventory management
├── employee-service/      # Employee management
├── notification-service/  # Notification service
├── eureka-server/        # Service discovery
├── docker-compose.yml    # Docker orchestration
└── README.md            # Project documentation
```

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose
- Java 17 or higher
- Maven 3.6+

### 1. Clone the Repository
```bash
git clone <repository-url>
cd delivery-management-system
```

### 2. Start All Services
```bash
docker-compose up -d
```

### 3. Access Services
- **API Gateway**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761
- **Swagger UI**: http://localhost:8080/swagger-ui.html

### 4. Service Endpoints

#### Authentication
```bash
# Login
POST http://localhost:8080/api/auth/login
{
  "username": "admin",
  "password": "password"
}

# Register
POST http://localhost:8080/api/auth/register
{
  "username": "newuser",
  "password": "password",
  "email": "user@example.com"
}
```

#### Driver Management
```bash
# Create driver
POST http://localhost:8080/api/drivers
{
  "userId": "user123",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "1234567890",
  "licenseNumber": "LIC123456",
  "vehicleType": "MOTORCYCLE"
}

# Get available drivers
GET http://localhost:8080/api/drivers/available

# Update driver location
PUT http://localhost:8080/api/drivers/{driverId}/location?latitude=10.762622&longitude=106.660172
```

#### Tracking
```bash
# Update location
POST http://localhost:8080/api/tracking/location
{
  "driverId": "DRV-ABC123",
  "orderId": "ORD-123",
  "latitude": 10.762622,
  "longitude": 106.660172,
  "speed": 35.0
}

# Get current location
GET http://localhost:8080/api/tracking/location/{driverId}

# Start GPS simulation
POST http://localhost:8080/api/tracking/simulation/start?driverId=DRV-ABC123&orderId=ORD-123
```

#### Order Management
```bash
# Create order tracking
POST http://localhost:8080/api/tracking/order?orderId=ORD-123&driverId=DRV-ABC123&customerId=CUST-456&pickupLat=10.762622&pickupLng=106.660172&deliveryLat=10.763000&deliveryLng=106.661000

# Update order status
PUT http://localhost:8080/api/tracking/order/{orderId}/status?status=IN_TRANSIT
```

## 🔧 Configuration

### Environment Variables
Each service can be configured using environment variables:

```yaml
# Database
SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/db_name
SPRING_DATASOURCE_USERNAME: postgres
SPRING_DATASOURCE_PASSWORD: 123456

# Redis
SPRING_REDIS_HOST: localhost
SPRING_REDIS_PORT: 6379

# Kafka
SPRING_KAFKA_BOOTSTRAP_SERVERS: localhost:9092

# Eureka
EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE: http://localhost:8761/eureka
```

### Service Ports
- **API Gateway**: 8080
- **Eureka Server**: 8761
- **Auth Service**: 8085
- **Order Service**: 8081
- **Tracking Service**: 8087
- **Driver Service**: 8088
- **User Service**: 8086
- **Product Service**: 8084
- **Inventory Service**: 8083
- **Employee Service**: 8082

## 📊 Monitoring & Analytics

### Real-Time Tracking
- WebSocket connections for live location updates
- Redis caching for current driver locations
- Kafka streaming for location events

### Performance Metrics
- Driver performance tracking
- Delivery success rates
- Average delivery times
- Revenue analytics

### Dashboard Features
- Real-time driver locations on map
- Order status tracking
- Performance analytics
- Revenue reports

## 🔒 Security

### Authentication
- JWT token-based authentication
- Role-based access control
- Secure API endpoints

### Data Protection
- Encrypted database connections
- Secure communication between services
- Input validation and sanitization

## 🧪 Testing

### API Testing
```bash
# Test authentication
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'

# Test driver creation
curl -X POST http://localhost:8080/api/drivers \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"userId":"user123","firstName":"John","lastName":"Doe","phoneNumber":"1234567890","licenseNumber":"LIC123456","vehicleType":"MOTORCYCLE"}'
```

### WebSocket Testing
```javascript
// Connect to WebSocket for real-time updates
const socket = new WebSocket('ws://localhost:8087/ws');
socket.onmessage = function(event) {
    console.log('Location update:', JSON.parse(event.data));
};
```

## 🚀 Deployment

### Docker Deployment
```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### Production Considerations
- Use external databases (AWS RDS, Azure SQL)
- Configure Redis cluster for high availability
- Set up Kafka cluster for scalability
- Implement proper monitoring and logging
- Configure SSL/TLS for secure communication

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Contact the development team
- Check the documentation in `/docs` folder

## 🔄 Roadmap

### Phase 1 (Current)
- ✅ Basic microservices architecture
- ✅ Authentication and authorization
- ✅ Order management
- ✅ Driver management
- ✅ Real-time tracking
- ✅ Performance analytics

### Phase 2 (Planned)
- 🔄 Frontend React application
- 🔄 Advanced route optimization
- 🔄 Mobile driver app
- 🔄 Customer mobile app
- 🔄 Advanced analytics dashboard

### Phase 3 (Future)
- 🔄 AI-powered route optimization
- 🔄 Predictive analytics
- 🔄 IoT integration
- 🔄 Blockchain for transparency
- 🔄 Multi-language support