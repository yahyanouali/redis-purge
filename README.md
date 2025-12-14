# redis-purge

Sample Quarkus 3 application demonstrating several ways to work with Redis for a simple "Group → Users" use case. It exposes REST endpoints that create, read, and delete `User` entries stored in a Redis Hash per group key. The same features are implemented with different Redis client styles:

- Low-level Redis (Vert.x)
- Low-level Redis (Mutiny for Quarkus)
- Low-level RedisAPI (Vert.x)
- Low-level RedisAPI (Mutiny)
- High-level Quarkus Redis DataSource (Imperative)
- High-level Quarkus Reactive Redis DataSource (Reactive)

The goal is to compare approaches and provide ready-to-run examples and cURL commands.


## Prerequisites

- Java 21+
- Maven 3.9+ (or use the included Maven Wrapper `mvnw`/`mvnw.cmd`)
- Docker (for running Redis via Docker Compose)


## Quick start

1) Start Redis locally using the provided Compose file:

```bash
docker compose -f compose.yml up -d
```

This launches a Redis 7.4 container exposing `localhost:6379` and persists data in a local Docker volume.

2) Run the app in dev mode:

```bash
./mvnw quarkus:dev
```

Quarkus dev console: http://localhost:8080/q/dev

OpenAPI: http://localhost:8080/q/openapi

Swagger UI: http://localhost:8080/q/swagger-ui


## Redis configuration

Configured in `src/main/resources/application.properties`:

```
quarkus.redis.hosts=redis://localhost:6379
```

You can override at runtime with environment variables, e.g.:

```bash
QUARKUS_REDIS_HOSTS=redis://my-redis:6379 ./mvnw quarkus:dev
```


## Data model

`User` is a simple record:

```java
public record User(String id, String name, String email) {}
```

Users are stored in a Redis Hash under the group key `groups:{groupId}`. A TTL of 36,000 seconds (10 hours) is set on the group key when the first user is created via the provided APIs.

Example JSON payload:

```json
{
  "id": "u1",
  "name": "Jane Doe",
  "email": "jane@example.com"
}
```


## REST endpoints

All endpoints are unauthenticated and accept/return JSON. They are grouped by implementation style.

Common semantics per group `groups:{groupId}`:
- Create user: `HSET groups:{groupId} {user.id} {User JSON}` and set TTL to 36,000s
- Get user: `HGET`
- Get all users: `HGETALL`
- Delete user: `HDEL`
- Delete all users of a group (delete key): `DEL`
- Get group TTL: `TTL`

Note: null/empty results map to HTTP 200 with empty bodies in these samples (no error mapping is implemented).

### 1) Low-level Redis (Vert.x)

Base path: `/redis/vertx`

- `POST /create/{groupId}` → `CompletionStage<Boolean>`
- `GET /get/{groupId}/{userId}` → `CompletionStage<User>`
- `GET /get-all/{groupId}` → `CompletionStage<Map<String, User>>`
- `DELETE /delete/{groupId}/{userId}` → `CompletionStage<Integer>`
- `DELETE /delete-all/{groupId}` → `CompletionStage<Integer>`
- `GET /get-ttl/{groupId}` → `CompletionStage<Long>`

Java: `com.monapp.resource.lowlevel.redis.GroupUserResourceRedisVertx`

### 1.2) Low-level Redis (Mutiny)

Base path: `/redis/mutiny`

- `POST /create/{groupId}` → `Uni<Boolean>`
- `GET /get/{groupId}/{userId}` → `Uni<User>`
- `GET /get-all/{groupId}` → `Uni<Map<String, User>>`
- `DELETE /delete/{groupId}/{userId}` → `Uni<Integer>`
- `DELETE /delete-all/{groupId}` → `Uni<Integer>`
- `GET /get-ttl/{groupId}` → `Uni<Long>`

Java: `com.monapp.resource.lowlevel.redis.GroupUserResourceRedisMutiny`

### 2) Low-level RedisAPI (Vert.x)

Base path: `/redisapi/vertx`

- `POST /create/{groupId}` → `CompletionStage<Boolean>`
- `GET /get/{groupId}/{userId}` → `CompletionStage<User>`
- `GET /get-all/{groupId}` → `CompletionStage<Map<String, User>>`
- `DELETE /delete/{groupId}/{userId}` → `CompletionStage<Integer>`
- `DELETE /delete-all/{groupId}` → `CompletionStage<Integer>`
- `GET /get-ttl/{groupId}` → `CompletionStage<Long>`

Java: `com.monapp.resource.lowlevel.redisapi.GroupUserResourceRedisAPIVertx`

### 2.2) Low-level RedisAPI (Mutiny)

Base path: `/redisapi/mutiny`

- `POST /create/{groupId}` → `Uni<Boolean>`
- `GET /get/{groupId}/{userId}` → `Uni<User>`
- `GET /get-all/{groupId}` → `Uni<Map<String, User>>`
- `DELETE /delete/{groupId}/{userId}` → `Uni<Integer>`
- `DELETE /delete-all/{groupId}` → `Uni<Integer>`
- `GET /get-ttl/{groupId}` → `Uni<Long>`

Java: `com.monapp.resource.lowlevel.redisapi.GroupUserResourceRedisAPIMutiny`

### 3) High-level Quarkus Redis DataSource (Imperative)

Base path: `/datasource/imperative`

- `POST /create/{groupId}` → `boolean`
- `GET /get/{groupId}/{userId}` → `User`
- `GET /get-all/{groupId}` → `Map<String, User>`
- `DELETE /delete/{groupId}/{userId}` → `int`
- `DELETE /delete-all/{groupId}` → `int`
- `GET /get-ttl/{groupId}` → `Long`

Java: `com.monapp.resource.highlevel.GroupUserResourceDataSourceImperative`

### 3.2) High-level Quarkus Reactive Redis DataSource

Base path: `/datasource/reactive`

- `POST /create/{groupId}` → `Uni<Boolean>`
- `GET /get/{groupId}/{userId}` → `Uni<User>`
- `GET /get-all/{groupId}` → `Uni<Map<String, User>>`
- `DELETE /delete/{groupId}/{userId}` → `Uni<Integer>`
- `DELETE /delete-all/{groupId}` → `Uni<Integer>`
- `GET /get-ttl/{groupId}` → `Uni<Long>`

Java: `com.monapp.resource.highlevel.GroupUserResourceDataSourceReactive`


## cURL examples

Assuming the app runs on `http://localhost:8080`.

Create user (imperative datasource example):

```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"id":"u1","name":"Jane Doe","email":"jane@example.com"}' \
  http://localhost:8080/datasource/imperative/create/my-group
```

Get one user:

```bash
curl http://localhost:8080/datasource/imperative/get/my-group/u1
```

Get all users:

```bash
curl http://localhost:8080/datasource/imperative/get-all/my-group
```

Delete one user:

```bash
curl -X DELETE http://localhost:8080/datasource/imperative/delete/my-group/u1
```

Delete all users of a group:

```bash
curl -X DELETE http://localhost:8080/datasource/imperative/delete-all/my-group
```

Get TTL of group key:

```bash
curl http://localhost:8080/datasource/imperative/get-ttl/my-group
```

The same routes exist under the other base paths listed above.


## Build and run

Package the application (JVM mode):

```bash
./mvnw clean package
```

Run the JAR:

```bash
java -jar target/redis-purge-dev.jar
```

Containerize with the provided Dockerfiles (optional):

```bash
# Build JVM image (example)
docker build -f src/main/docker/Dockerfile.jvm -t redis-purge:jvm .
```

Build a native executable (GraalVM, optional):

```bash
./mvnw clean package -Dnative
```

Resulting binaries and images depend on your platform and GraalVM setup.


## Project layout

- `src/main/java/com/monapp/model/User.java` — data model
- `src/main/java/com/monapp/redis/lowlevel/*` — low-level Redis and RedisAPI managers (Vert.x / Mutiny)
- `src/main/java/com/monapp/resource/lowlevel/*` — REST resources for low-level APIs
- `src/main/java/com/monapp/redis/highlevel/*` — Quarkus Redis DataSource managers (imperative/reactive)
- `src/main/java/com/monapp/resource/highlevel/*` — REST resources for high-level DataSource
- `compose.yml` — Redis service for local development
- `src/main/resources/application.properties` — configuration


## Troubleshooting

- Connection refused: ensure `docker compose up -d` has started Redis on port 6379, or update `quarkus.redis.hosts` accordingly.
- Empty responses: when a key or user does not exist, endpoints may return `null` or empty maps without an error status.
- TTL is negative: Redis returns `-2` if key does not exist and `-1` if the key exists but has no associated expire.


## License

This project is provided as-is for demonstration purposes.
