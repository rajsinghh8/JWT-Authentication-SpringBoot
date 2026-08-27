COMMIT_MESSAGE: Fix datasource startup and apply the API v1 prefix

## Features Added
- Resolved the configured datasource to the working PostgreSQL fallback returned by JDBC resolution.
- Applied the /api/v1 prefix consistently to controller routes and Spring Security route rules.
- Configured the required server port 26248.
- Added an integration test for API v1 registration, login, and JWT authentication.

## Files Modified
- pom.xml — added the PostgreSQL JDBC runtime driver required by the resolved datasource.
- src/main/resources/application.properties — set PostgreSQL URL, driver, resolved credentials and dialect; set port 26248.
- src/main/java/com/example/JWTAuthenticationSpringboot/config/AuthController.java — mapped authentication routes under API v1.
- src/main/java/com/example/JWTAuthenticationSpringboot/config/SecurityConfig.java — aligned public and protected route matchers with API v1.
- src/main/java/com/example/JWTAuthenticationSpringboot/controllers/AdminController.java — mapped admin routes under API v1.
- src/main/java/com/example/JWTAuthenticationSpringboot/controllers/HomeController.java — mapped home routes under API v1.

## Files Added
- src/test/java/com/example/JWTAuthenticationSpringboot/controller/ApiPrefixIntegrationTest.java — validates API v1 registration, login, JWT authentication, and temporary test-user cleanup.

## Secrets Moved
- None. JWT and datasource credentials were already externalized through application.properties environment placeholders.

## DB URLs Resolved
- jdbc:oracle:thin:@localhost:1521/XEPDB1 -> jdbc:postgresql://localhost:5432/gen_d47e61cf2671

## Test Results Summary
- 2 PASSED, 0 FAILED, 0 SKIPPED (mvn test -q)
