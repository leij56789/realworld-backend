# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

RealWorld backend (Conduit) — a Spring Boot 4.0.6 REST API for a Medium-like blogging platform. Java 17, Maven (wrapper included), MySQL + MyBatis-Plus, Redis, JWT auth.

## Build & Run Commands

```bash
# Build
./mvnw clean compile

# Run tests
./mvnw test

# Run a single test
./mvnw test -Dtest=RealworldBackendApplicationTests

# Run the app
./mvnw spring-boot:run

# Package as JAR
./mvnw clean package -DskipTests
```

The app starts on port 8081 (commented-out config in `application.properties`; defaults to 8080 if not uncommented).

## Database

MySQL database `realworld` on `localhost:3306` (credentials: `root/123456`). Schema at `sql/schema.sql` and seed data at `sql/data.sql`.

## Architecture

Standard layered Spring Boot architecture with MyBatis-Plus as the ORM:

### Layer Stack

```
Controller → Service (interface) → ServiceImpl (extends ServiceImpl<M,T>) → Mapper (extends BaseMapper<T>) → XML mapper / MyBatis-Plus
```

### Package Layout (`com.realworld.blog`)

| Package | Role |
|---|---|
| `controller/` | REST endpoints: `UserController`, `ArticleController`, `ProfileController` |
| `service/` + `service/impl/` | Business logic. Interfaces extend `IService<T>` from MyBatis-Plus; impls extend `ServiceImpl<M, T>`. Key: `UserService`, `ArticleService`, `UserFollowsService`, `TagService`, `CommentService`, `ArticleTagsService`, `UserFavoritesService` |
| `mapper/` | MyBatis-Plus `BaseMapper<T>` interfaces. Custom SQL lives in XML files under `src/main/resources/com/realworld/blog/mapper/` |
| `entity/` | DB-mapped POJOs: `User`, `Article`, `Comment`, `Tag`, `UserFollows`, `UserFavorites`, `ArticleTags` |
| `dto/request/` | Inbound request bodies (e.g. `UsersLoginRequest`, `ArticlesPostRequest`) |
| `dto/response/` | Outbound response structures. Often use nested `UserBean`/`ArticleBean` classes mirroring the RealWorld API spec |
| `common/` | `Result<T>` (unified response wrapper), `BusinessException` (extends `RuntimeException`), `GlobalExceptionHandler` (`@RestControllerAdvice`) |
| `annotation/` | Custom annotations: `@Log` (method logging), `@RateLimit` (Guava rate limiter), `@Auth` (require JWT) |
| `aspect/` | AOP: `LogAspect` (`@Around` on `@Log` methods), `RateLimitAspect` (`@Around` on `@RateLimit` methods) |
| `interceptor/` | `JwtInterceptor` — parses `Authorization: Token <jwt>` header, stores username/token in `ThreadLocal`, checks `@Auth` annotation |
| `config/` | `MyBatisPlusConfig`, `RedisConfig`, `ThreadPoolConfig`, `PasswordEncoderConfig`, `WebConfig` (registers JWT interceptor) |
| `utils/` | `JwtUtil` (HS256 JWT, 7-day expiry), `RedisUtil` (wraps `RedisTemplate`), `SnowflakeIdWorker` (distributed ID generator, available but not in active use) |

### Authentication

- Custom JWT-based auth — **not** Spring Security. Only `spring-security-crypto` is used for BCrypt password hashing.
- JWT tokens use the prefix `Token ` (not `Bearer `) in the `Authorization` header.
- `JwtInterceptor` runs on every request via `WebConfig.addInterceptors()`. It parses the token if present and stores the current username + token in `ThreadLocal` (accessible via `JwtInterceptor.getCurrentUser()` / `getCurrentToken()`).
- Controllers that require authentication are annotated with `@Auth` — the interceptor returns 401 if the user is not logged in. Controllers without `@Auth` accept optional authentication.
- Service methods read the current user from `JwtInterceptor.getCurrentUser()` and `JwtInterceptor.getCurrentToken()` — never from method parameters.

### API Response Format

All endpoints follow the [RealWorld API spec](https://realworld-docs.netlify.app/docs/specifications/backend-specs/endpoints). Response bodies nest data under a key (e.g. `{"user": {...}}`, `{"article": {...}}`). The full endpoint list is documented in `endpoints.md`.

### Database Schema (7 tables)

`user` — `article` — `comment` (article_id → article, author_id → user) — `tag` — `user_favorites` (user_id, article_id composite PK) — `user_follows` (follower_id, followee_id composite PK) — `article_tags` (article_id, tag_id composite PK). All tables use auto-increment PKs and proper foreign keys with CASCADE deletes. See `sql/schema.sql` for the full DDL.

### Key Implementation Details

- **Slug generation**: `ArticleServiceImpl.generateSlug()` derives a URL-safe slug from the title (lowercase, strip non-alphanumeric, collapse whitespace to hyphens, append `-N` if duplicate).
- **Article listing**: `GET /api/articles` supports filtering by `tag`, `author`, `favorited` query params with pagination (`limit`/`offset`). Implemented via custom XML SQL in `ArticleMapper.xml` (dynamic `<where>` with EXISTS subqueries).
- **Rate limiting**: Apply `@RateLimit(permitsPerSecond=2.0, timeout=5)` on any controller method. Uses Guava `RateLimiter` with a `ConcurrentHashMap` keyed by the annotation's `key` value (or the method signature as fallback).
- **Logging**: Apply `@Log("operation description")` on controller methods. `LogAspect` logs request URL, IP, parameters, response, and elapsed time via SLF4J.
- **Transaction boundaries**: `ArticleServiceImpl.articlesPost()`, `articlesSlugPut()`, and `articlesSlugDelete()` are annotated with `@Transactional`.
- **Debug logging**: The login method in `UserServiceImpl` and article listing method in `ArticleServiceImpl` contain `System.out.println` debug statements.
- **Unimplemented endpoints**: Feed articles, favorites, and tags are commented out in `ArticleController` — the service methods exist but aren't wired up.

### Tech Stack Summary

Spring Boot 4.0.6, MyBatis-Plus 3.5.16, MySQL, Redis (Lettuce), JWT (jjwt 0.11.5), Guava 33.4.0, Hutool 5.8.40, SpringDoc OpenAPI 2.8.6, BCrypt via `spring-security-crypto`, Lombok, Actuator, Jackson.
