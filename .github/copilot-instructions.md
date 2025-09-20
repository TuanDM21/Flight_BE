# AI Coding Agent Instructions for Airport Management System

## Project Overview

This is a Spring Boot 3.2.4 application for airport management (QuanLyCangHangKhong) with JWT authentication, MySQL database, and Docker deployment. The system manages flights, assignments, users, teams, and notifications for airport operations.

## Architecture & Key Components

### Layered Architecture Pattern

- **Controllers**: REST API endpoints in `controller/` package
- **Services**: Business logic in `service/impl/` (interfaces in `service/`)
- **Repositories**: Data access layer extending JpaRepository
- **Models**: JPA entities in `model/` package
- **DTOs**: Data transfer objects in `dto/` package

### Core Dependencies & Technologies

- Spring Boot 3.2.4 with Java 17
- Spring Security + JWT authentication
- MySQL 8.0 with Flyway migrations
- SpringDoc OpenAPI (Swagger)
- Azure Blob Storage for file uploads
- Docker + Docker Compose for deployment
- Lombok for boilerplate reduction

## Critical Development Workflows

### Environment Setup

```bash
# 1. Copy environment template
cp .env.example .env

# 2. Configure required environment variables
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/airport_db
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
JWT_SECRET=your_jwt_secret
JWT_EXPIRATION=86400000

# 3. Start development environment
make run-dev
```

### Database Migrations

```bash
# Run migrations during development
make db-migrate

# Check migration status
make db-info

# Clean database (CAUTION: destroys data)
make db-clean
```

### Build & Deployment

```bash
# Development build
make mvn-compile

# Production build with Docker
make build
make run-prod

# Full deployment workflow
make build && make run-prod
```

## Project-Specific Patterns & Conventions

### 1. API Response Pattern

**ALWAYS use `ApiResponseCustom<T>` for all API responses:**

```java
// ✅ CORRECT: Use ApiResponseCustom wrapper
@PostMapping("/login")
public ResponseEntity<ApiResponseCustom<LoginDTO>> login(@Valid @RequestBody LoginRequest request) {
    ApiResponseCustom<LoginDTO> response = authService.login(request);
    return ResponseEntity.status(response.getStatusCode()).body(response);
}

// ❌ AVOID: Direct entity/DTO returns
@PostMapping("/login")
public ResponseEntity<LoginDTO> login(@Valid @RequestBody LoginRequest request) {
    // Don't do this
}
```

**Response Structure:**

```java
ApiResponseCustom.success(data)           // 200 OK with data
ApiResponseCustom.success("Message", data) // 200 OK with custom message
ApiResponseCustom.error("Error message")   // Error response
```

### 2. Service Layer Pattern

**ALWAYS create interface/impl pairs:**

```java
// ✅ CORRECT: Interface in service/
public interface UserService {
    ApiResponseCustom<UserDTO> findById(Integer id);
}

// Implementation in service/impl/
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    // Implementation
}
```

### 3. Entity Relationship Patterns

**Use explicit join columns and fetch strategies:**

```java
@Entity
@Table(name = "users")
public class User {
    @ManyToOne(fetch = FetchType.EAGER)  // Explicit fetch strategy
    @JoinColumn(name = "role_id", nullable = false)  // Explicit column name
    private Role role;
}
```

### 4. Validation & Error Handling

**Use Bean Validation on request DTOs:**

```java
public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
```

### 5. Security Configuration

**Public endpoints in SecurityConfig:**

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers(
        "/api/auth/**",           // Authentication endpoints
        "/swagger-ui/**",         // Swagger UI
        "/v3/api-docs/**"         // OpenAPI docs
    ).permitAll()
    .anyRequest().authenticated())
```

### 6. Scheduled Tasks

**Use Spring @Scheduled with cron expressions:**

```java
@Component
public class NotificationCleanupScheduler {
    @Scheduled(cron = "0 0 2 * * ?")  // Daily at 2 AM
    public void cleanupOldNotifications() {
        // Implementation
    }
}
```

## Database & Migration Patterns

### Flyway Migration Naming

**Follow semantic versioning pattern:**

- `V1_0__Create_roles_table.sql`
- `V1_1__Create_teams_units_tables.sql`
- `V2_0__Seerder_data.sql`

### Environment-Specific Configurations

- **Development**: `application-dev.properties` (debug enabled, H2 console)
- **Production**: `application-prod.properties` (optimized, Swagger disabled)

## File Upload & Azure Integration

### Azure Blob Storage Pattern

```java
@Service
public class AzurePreSignedUrlServiceImpl implements AzurePreSignedUrlService {
    @Value("${azure.storage.container-name}")
    private String containerName;

    // Generate pre-signed URLs for secure uploads
    public PreSignedUrlDTO generatePreSignedUrl(String fileName) {
        // Implementation
    }
}
```

## Testing & Quality Assurance

### Unit Test Structure

**Place tests in `src/test/java/` mirroring main package structure:**

```
src/test/java/com/project/quanlycanghangkhong/
├── controller/
├── service/
└── repository/
```

### Integration Testing

**Use `@SpringBootTest` for full context testing:**

```java
@SpringBootTest
@AutoConfigureTestDatabase
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
}
```

## Docker & Deployment

### Multi-Stage Docker Build

**Production Dockerfile uses JRE, development uses JDK:**

```dockerfile
# Production stage
FROM eclipse-temurin:17-jre AS production
COPY --from=build /app/target/*.jar app.jar
USER spring  # Non-root user
```

### Docker Compose Profiles

```yaml
services:
  migration:
    profiles:
      - migration # Only runs when profile is activated
```

## Common Pitfalls to Avoid

### 1. Database Connection Issues

- Always set `spring.jpa.hibernate.ddl-auto=update` in dev
- Use environment variables for database credentials
- Ensure MySQL timezone matches application timezone

### 2. CORS Configuration

- Configure CORS in SecurityConfig, not controller-level
- Allow credentials for authenticated requests
- Set appropriate exposed headers for JWT tokens

### 3. JWT Token Handling

- Store tokens in Authorization header: `Bearer <token>`
- Validate tokens in JwtAuthenticationFilter
- Handle token expiration gracefully

### 4. File Upload Security

- Use pre-signed URLs for Azure blob uploads
- Validate file types and sizes
- Never store files in application container

## Key Files to Reference

### Configuration Files

- `pom.xml` - Dependencies and build configuration
- `application.properties` - Base configuration
- `application-dev.properties` - Development overrides
- `application-prod.properties` - Production overrides

### Core Implementation

- `SecurityConfig.java` - Authentication and authorization
- `ApiResponseCustom.java` - Standardized API responses
- `AuthServiceImpl.java` - Authentication business logic
- `User.java` - Main entity with relationships

### Infrastructure

- `Dockerfile` - Multi-stage production build
- `docker-compose.yml` - Production deployment
- `docker-compose.dev.yml` - Development environment
- `Makefile` - Build and deployment automation

## OpenAPI Schema Definitions

### Response Schema Pattern

**ALWAYS use `ApiResponseCustom<T>` for OpenAPI schemas, NOT custom wrapper classes:**

```java
// ✅ CORRECT: Use ApiResponseCustom with generic type
@PostMapping("/login")
@Operation(summary = "User login", description = "Authenticate user and return JWT token")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Login successful",
        content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
    @ApiResponse(responseCode = "401", description = "Invalid credentials",
        content = @Content(schema = @Schema(implementation = ApiResponseCustom.class))),
    @ApiResponse(responseCode = "404", description = "User not found",
        content = @Content(schema = @Schema(implementation = ApiResponseCustom.class)))
})
public ResponseEntity<ApiResponseCustom<LoginDTO>> login(@Valid @RequestBody LoginRequest request) {
    ApiResponseCustom<LoginDTO> response = authService.login(request);
    return ResponseEntity.status(response.getStatusCode()).body(response);
}

// ❌ AVOID: Custom wrapper classes (duplicates ApiResponseCustom structure)
@ApiResponse(responseCode = "200", description = "Login successful",
    content = @Content(schema = @Schema(implementation = LoginApiResponse.class)))
```

### Generic Type Specification

**For proper OpenAPI documentation, specify the generic type in the schema:**

```java
// For single object responses
@ApiResponse(responseCode = "200", description = "User found",
    content = @Content(schema = @Schema(
        implementation = ApiResponseCustom.class,
        subTypes = {UserDTO.class}  // Specify the actual data type
    )))

// For list responses
@ApiResponse(responseCode = "200", description = "Users list",
    content = @Content(schema = @Schema(
        implementation = ApiResponseCustom.class,
        subTypes = {List.class, UserDTO.class}  // List of UserDTO
    )))
```

### Request Schema Definitions

**Use proper schema annotations on request DTOs:**

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User login request")
public class LoginRequest {

    @Schema(description = "User email", example = "user@example.com", required = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "User password", example = "password123", required = true, minLength = 6)
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
```

### Schema Examples and Validation

**Provide comprehensive examples in schema annotations:**

```java
@Schema(description = "User data transfer object", example = """
{
  "id": 1,
  "name": "Nguyễn Văn A",
  "email": "nguyenvana@example.com",
  "role": {
    "id": 1,
    "roleName": "STAFF"
  },
  "team": {
    "id": 1,
    "teamName": "Ground Operations"
  }
}
""")
public class UserDTO {
    // fields...
}
```

### Controller-Level Schema Configuration

**Configure schemas at controller level for consistency:**

```java
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for managing users")
@Schema(description = "User management operations")
public class UserController {

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseCustom.class))),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponseCustom.class)))
    })
    public ResponseEntity<ApiResponseCustom<UserDTO>> getUserById(@PathVariable Integer id) {
        // implementation
    }
}
```

### Common Schema Patterns

#### Pagination Response

```java
@Schema(description = "Paginated response wrapper")
public class PaginatedResponse<T> {
    @Schema(description = "List of items")
    private List<T> items;

    @Schema(description = "Current page number", example = "0")
    private int currentPage;

    @Schema(description = "Total number of pages", example = "10")
    private int totalPages;

    @Schema(description = "Total number of items", example = "100")
    private long totalItems;
}
```

#### Error Response Schema

```java
@Schema(description = "Error response details")
public class ErrorDetails {
    @Schema(description = "Error timestamp", example = "2024-01-15T10:30:00Z")
    private LocalDateTime timestamp;

    @Schema(description = "Error message", example = "Validation failed")
    private String message;

    @Schema(description = "Error details", example = "Email is required")
    private String details;

    @Schema(description = "HTTP status code", example = "400")
    private int statusCode;
}
```

### Schema Validation Rules

1. **Always use ApiResponseCustom** as the base response wrapper
2. **Specify generic types** using `subTypes` in schema annotations
3. **Provide examples** for complex objects and edge cases
4. **Use consistent naming** across all schemas
5. **Document required fields** with `required = true`
6. **Include validation constraints** in schema descriptions
7. **Avoid custom wrapper classes** - use the standard ApiResponseCustom pattern

### Migration from Custom Wrappers

**Replace existing custom response classes with ApiResponseCustom:**

```java
// OLD: Custom wrapper (AVOID)
@Schema(implementation = UserApiResponse.class)

// NEW: Standard wrapper (USE)
@Schema(implementation = ApiResponseCustom.class)
public ResponseEntity<ApiResponseCustom<UserDTO>> getUser() {
    return ResponseEntity.ok(ApiResponseCustom.success(userDTO));
}
```

This ensures consistent API documentation and reduces code duplication while maintaining proper OpenAPI specification compliance.

## Getting Started Checklist

- [ ] Copy `.env.example` to `.env` and configure variables
- [ ] Run `make run-dev` to start development environment
- [ ] Execute `make db-migrate` to set up database schema
- [ ] Access Swagger UI at `http://localhost:8080/swagger-ui.html`
- [ ] Test authentication endpoints with JWT tokens
- [ ] Review entity relationships in model classes
- [ ] Understand service layer patterns in service/impl/</content>
