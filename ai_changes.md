COMMIT_MESSAGE: Configure Oracle database (JPA/Hikari datasource) and persist users; externalize JWT secret with HS256/30min expiry

## Features Added
- Configured a relational database (Oracle-targeted, JDBC/JPA) for the application via `spring-boot-starter-data-jpa`, the Oracle JDBC driver (`ojdbc11`), and `spring.datasource.*` / `app.datasource.*` properties. Production deployments can point `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` / `DB_DRIVER` env vars at a real Oracle instance (`jdbc:oracle:thin:@...`, `oracle.jdbc.OracleDriver`).
- Because no Oracle server is reachable inside this sandbox, the datasource defaults (used only when the env vars above are not supplied) point at a working PostgreSQL database provisioned for this job (`create_java_db`), so the app can actually boot, persist and be tested end-to-end here.
- `User` model converted to a JPA `@Entity` (table `app_users`) and a new `UserRepository` (`JpaRepository`) was added; `UserService` now reads/writes users through the database instead of an in-memory `ArrayList`, seeding the original 4 demo users on first boot via `@PostConstruct` if the table is empty.
- `spring-boot-starter-actuator` added and `/actuator/health` exposed (and permitted through Spring Security) for infra health checks.
- JWT signing secret and expiry externalized to `application.properties` (`app.jwt.secret`, `app.jwt.expiration-ms`), algorithm switched to HS256, expiry set to 30 minutes (1,800,000 ms) per requested auth target.
- `server.port` changed to the required `25330`.

## Files Modified
- `pom.xml` — added `spring-boot-starter-data-jpa`, `spring-boot-starter-actuator`, Oracle JDBC driver (`com.oracle.database.jdbc:ojdbc11`), and PostgreSQL driver (runtime fallback for the sandbox-provisioned DB).
- `src/main/resources/application.properties` — added Oracle/JPA datasource config (`app.datasource.*` / `spring.datasource.*`), Hibernate settings, JWT secret/expiry properties, actuator exposure, `server.port=25330`, and API prefix/pagination properties (`app.api.prefix`, `app.api.pagination.default-limit`).
- `src/main/java/.../models/User.java` — annotated as JPA `@Entity`/`@Table("app_users")` with `@Id` on `userId`.
- `src/main/java/.../services/UserService.java` — now backed by `UserRepository` (JPA) instead of an in-memory list; seeds demo data once via `@PostConstruct`.
- `src/main/java/.../security/JWTHelper.java` — secret and expiry now injected via `@Value` from `application.properties`; signing algorithm changed to HS256; expiry driven by `app.jwt.expiration-ms` (default 30 min).
- `src/main/java/.../config/SecurityConfig.java` — permitted `/actuator/health` so infra health checks work without authentication.

## Files Added
- `src/main/java/.../repositories/UserRepository.java` — Spring Data JPA repository for `User`, backing the configured database.

## Secrets Moved
- JWT signing secret (hardcoded in `JWTHelper`) -> `app.jwt.secret` (`${JWT_SECRET:...}`) in `application.properties`.
- Database username/password (newly introduced) -> `app.datasource.username` / `app.datasource.password` (`${DB_USERNAME:...}` / `${DB_PASSWORD:...}`) in `application.properties`.

## DB URLs Resolved
- No pre-existing JDBC URL was found in the project (it previously had no database at all — only in-memory user storage).
- New datasource configured with default `jdbc:postgresql://localhost:5432/gen_d47e61cf2671` (sandbox-provisioned working database via `create_java_db`, since no Oracle server is reachable here) — overridable in real deployments via `DB_URL`/`DB_USERNAME`/`DB_PASSWORD`/`DB_DRIVER` env vars pointing at an actual Oracle instance.

## Compilation Result
PASSED (`mvn compile -q` and `mvn package -DskipTests -q` both succeeded with zero errors). Manually verified at runtime: server boots on port 25330, Hikari connects to the configured database, `/actuator/health` returns 200 `{"status":"UP"}`, `/auth/login` issues an HS256 JWT (30 min expiry), `/home/user` returns the 4 seeded users read from the database, and the rows are visible directly via `psql` against the configured datastore.
