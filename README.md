# Grigoriev Yanis — Student API (DevOps Midterm)

Student: **Grigoriev Yanis** | Group: **IT22303** | ID: **37038**

Project status: Tested Spring Boot CRUD API, with complete project documentation and PostgreSQL. 

## Technologies
Spring Boot 3.5.x, Java 21, Gradle 8.14.3, Spring Data JPA, PostgreSQL, Docker Compose, Jenkins.

## Run
```bash
./scripts/create_env.sh
docker compose up -d --build
curl http://localhost:18080/api/about
```

The local `.env` contains a randomly generated database password. It must not be committed.

## Endpoints
- `GET /api/about` — project/student identity
- `POST /api/students` — create
- `GET /api/students` — list
- `GET /api/students/{id}` — one
- `PUT /api/students/{id}` — validated full update (CRUD feature)
- `DELETE /api/students/{id}` — delete

## Local Java build
```bash
./gradlew clean test bootJar
```
First run downloads the verified official Gradle Wrapper JAR and Gradle distribution.

For every midterm requirement, see `RUNBOOK_RU.md`.
