COMMIT_MESSAGE: Configure the application to run on port 24342

## Features Added
- Corrected the application server port to the required local deployment port, 24342.

## Files Modified
- src/main/resources/application.properties — changed server.port from 26248 to 24342; existing JWT HS256, 30-minute expiry, API v1 prefix, PostgreSQL datasource, and health exposure settings remain intact.

## Files Added
- None.

## Secrets Moved
- None. JWT and datasource credentials were already externalized through application.properties environment placeholders.

## DB URLs Resolved
- jdbc:postgresql://localhost:5432/gen_d47e61cf2671 -> jdbc:postgresql://localhost:5432/gen_d47e61cf2671 (pre-resolved configured PostgreSQL URL retained).

## Test Results Summary
- 2 PASSED, 0 FAILED, 0 SKIPPED (mvn test -q).
