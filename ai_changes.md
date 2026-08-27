COMMIT_MESSAGE: Fix deployment port and externalize seed credentials

## Features Added
- Configured the application to listen on the required port 24061.
- Externalized demo seed-account passwords so they can be overridden with environment variables instead of being hardcoded in Java.
- Added Mockito unit coverage for user registration and refresh-token expiration behavior.

## Files Modified
- src/main/resources/application.properties — set server.port to 24061 and added environment-backed demo seed password properties.
- src/main/java/com/example/JWTAuthenticationSpringboot/services/UserService.java — inject seed passwords from configuration before BCrypt encoding.

## Files Added
- src/test/java/com/example/JWTAuthenticationSpringboot/service/UserServiceTest.java — unit tests for normalized registration, encoded passwords, default role, and duplicate email rejection.
- src/test/java/com/example/JWTAuthenticationSpringboot/service/RefreshTokenServiceTest.java — unit tests for valid and expired refresh-token handling.

## Secrets Moved
- UserService seed admin password -> app.seed.admin-password
- UserService seed first user password -> app.seed.user-one-password
- UserService seed shared user password -> app.seed.user-two-password

## DB URLs Resolved
- jdbc:postgresql://localhost:5432/gen_d47e61cf2671 -> jdbc:postgresql://localhost:5432/gen_d47e61cf2671

## Test Results Summary
- 6 PASSED, 0 FAILED, 0 SKIPPED (mvn test -q)
