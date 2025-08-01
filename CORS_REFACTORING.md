# CORS Configuration Refactoring

## Issues Identified

1. **Multiple conflicting CORS configurations**: CORS was configured in both API Gateway and individual services
2. **Inconsistent CORS settings**: Some services used `@CrossOrigin("*")` while others had specific configurations
3. **Duplicate configurations**: Auth-service had both a `CorsConfig` class and CORS configuration in `SecurityConfig`
4. **Syntax error**: Missing line break in API Gateway CORS configuration
5. **Inconsistent allowed origins**: Some used `allowedOrigins` while others used `allowedOriginPatterns`

## Changes Made

### 1. API Gateway (`api-gateway/src/main/java/api_gateway/api_gateway/config/GatewayConfig.java`)

**Before:**
```java
@Bean
public CorsWebFilter corsWebFilter() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.setAllowedOriginPatterns(List.of("http://localhost:5173"));        config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return new CorsWebFilter(source);
}
```

**After:**
```java
@Bean
public CorsWebFilter corsWebFilter() {
    CorsConfiguration config = new CorsConfiguration();
    
    // Allow credentials
    config.setAllowCredentials(true);
    
    // Allow specific origins (you can add more as needed)
    config.setAllowedOriginPatterns(List.of(
        "http://localhost:3000",
        "http://localhost:5173", 
        "http://localhost:8080",
        "http://127.0.0.1:3000",
        "http://127.0.0.1:5173",
        "http://127.0.0.1:8080"
    ));
    
    // Allow all headers
    config.addAllowedHeader("*");
    
    // Allow all methods
    config.addAllowedMethod("*");
    
    // Expose headers for JWT tokens
    config.setExposedHeaders(List.of("Authorization", "Content-Type"));
    
    // Set max age for preflight requests
    config.setMaxAge(3600L);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return new CorsWebFilter(source);
}
```

### 2. Application Properties (`api-gateway/src/main/resources/application.yml`)

**Removed duplicate CORS configuration:**
```yaml
# Removed this section:
globalcors:
  corsConfigurations:
    '[/**]':
      allowedOrigins: "http://localhost:5173"
      allowedHeaders: "*"
      allowedMethods:
        - GET
        - POST
        - DELETE
        - PUT
        - OPTIONS
```

### 3. Auth Service (`auth-service/src/main/java/com/example/authservice/config/SecurityConfig.java`)

**Updated CORS configuration:**
```java
.cors(cors -> cors
    .configurationSource(request -> {
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow specific origins
        config.setAllowedOriginPatterns(List.of(
            "http://localhost:3000",
            "http://localhost:5173", 
            "http://localhost:8080",
            "http://127.0.0.1:3000",
            "http://127.0.0.1:5173",
            "http://127.0.0.1:8080"
        ));
        
        // Allow all methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Allow all headers
        config.setAllowedHeaders(List.of("*"));
        
        // Expose headers for JWT tokens
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));
        
        // Allow credentials
        config.setAllowCredentials(true);
        
        // Set max age for preflight requests
        config.setMaxAge(3600L);
        
        return config;
    })
)
```

### 4. Removed Duplicate CORS Configurations

- **Deleted:** `auth-service/src/main/java/com/example/authservice/config/CorsConfig.java`
- **Removed:** `@CrossOrigin("*")` annotations from all controllers:
  - `auth-service/src/main/java/com/example/authservice/controller/AuthController.java`
  - `auth-service/src/main/java/com/example/authservice/controller/OptController.java`
  - `user-service/src/main/java/com/example/userservice/controller/UserController.java`
  - `product-service/src/main/java/com/example/productservice/controller/CategoryController.java`

### 5. Added CORS Configuration to Other Services

**User Service (`user-service/src/main/java/com/example/userservice/config/SecurityConfig.java`):**
Added comprehensive CORS configuration similar to auth-service.

**Product Service (`product-service/src/main/java/com/example/productservice/config/SecurityConfig.java`):**
Added comprehensive CORS configuration similar to auth-service.

## CORS Configuration Best Practices Applied

1. **Centralized Configuration**: CORS is now primarily handled by the API Gateway
2. **Consistent Origins**: All services use the same allowed origins
3. **Proper Headers**: JWT tokens are properly exposed via `Authorization` header
4. **Preflight Optimization**: Set `maxAge` to 3600 seconds to cache preflight requests
5. **Security**: Only specific origins are allowed, not wildcards
6. **Credentials Support**: `allowCredentials` is set to `true` for JWT token support

## Allowed Origins

The following origins are now allowed across all services:
- `http://localhost:3000` (React default)
- `http://localhost:5173` (Vite default)
- `http://localhost:8080` (API Gateway)
- `http://127.0.0.1:3000`
- `http://127.0.0.1:5173`
- `http://127.0.0.1:8080`

## Testing CORS

To test if CORS is working properly:

1. **Start all services:**
   ```bash
   docker-compose up -d
   ```

2. **Test from browser console:**
   ```javascript
   fetch('http://localhost:8080/api/auth/login', {
     method: 'POST',
     headers: {
       'Content-Type': 'application/json'
     },
     body: JSON.stringify({
       username: 'test',
       password: 'test'
     })
   })
   .then(response => response.json())
   .then(data => console.log(data));
   ```

3. **Check browser network tab** for CORS headers in the response.

## Troubleshooting

If you still experience CORS issues:

1. **Check browser console** for specific CORS error messages
2. **Verify service is running** on the correct port
3. **Check if frontend origin** is in the allowed origins list
4. **Ensure JWT tokens** are being sent in the `Authorization` header
5. **Check API Gateway logs** for routing issues

## Security Notes

- Only specific origins are allowed (not wildcards)
- Credentials are allowed for JWT token support
- Preflight requests are cached for 1 hour
- All HTTP methods are allowed for API flexibility