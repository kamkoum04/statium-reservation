# Stadium Reservation Backend

Spring Boot 3.3 REST API for managing stadiums, football matches, seats and ticket reservations, secured with JWT (HS256) and BCrypt over a MySQL 8 database.

This guide covers running the project from a fresh machine on **Linux/macOS** and **Windows**, plus a full set of cURL examples in `stadium-reservation-backend/ENDPOINT-TESTS.md`.

---

## 1. Tech stack

| Layer        | Technology                                        |
| ------------ | ------------------------------------------------- |
| Runtime      | Java 21 (LTS)                                     |
| Build        | Maven Wrapper (`mvnw`) 3.9.x                      |
| Framework    | Spring Boot 3.3.4 (Web, Security, Data JPA, Mail) |
| Persistence  | Hibernate 6.5 + MySQL 8                           |
| Auth         | Spring Security + `jjwt 0.12.6` (24 h tokens)     |
| Docs         | springdoc-openapi 2.5 (Swagger UI)                |
| Packaging    | WAR (deployable; also runnable via `spring-boot:run`) |
| Other        | Lombok, iText (PDF tickets), Jakarta Mail         |

---

## 2. Prerequisites

| Tool                | Version           | Linux / macOS                 | Windows                                       |
| ------------------- | ----------------- | ----------------------------- | --------------------------------------------- |
| **JDK 21**          | 21.x              | `sudo apt install openjdk-21-jdk` / `brew install openjdk@21` | Adoptium MSI ([adoptium.net](https://adoptium.net/temurin/releases/?version=21)) |
| **Docker**          | 24+               | `sudo apt install docker.io` / Docker Desktop | Docker Desktop                                |
| **Git**             | any               | `sudo apt install git`        | [git-scm.com](https://git-scm.com/)           |
| **curl** (optional) | for testing       | usually preinstalled          | preinstalled on Win10+                        |

> Maven is **not** required — the project ships with `mvnw` / `mvnw.cmd`.

Verify:

```bash
java -version   # should print 21.x
docker --version
git --version
```

---

## 3. Clone the repository

```bash
git clone https://github.com/kamkoum04/statium-reservation.git
cd statium-reservation/stadium-reservation-backend
```

---

## 4. Start MySQL (Docker)

The app expects a database called `Stadium` with user `main` / password `passwd`.

### Linux / macOS

```bash
docker run -d --name statium-mysql \
  -e MYSQL_ROOT_PASSWORD=rootpass \
  -e MYSQL_DATABASE=Stadium \
  -e MYSQL_USER=main \
  -e MYSQL_PASSWORD=passwd \
  -p 3306:3306 \
  mysql:8.0
```

### Windows (PowerShell)

```powershell
docker run -d --name statium-mysql `
  -e MYSQL_ROOT_PASSWORD=rootpass `
  -e MYSQL_DATABASE=Stadium `
  -e MYSQL_USER=main `
  -e MYSQL_PASSWORD=passwd `
  -p 3306:3306 `
  mysql:8.0
```

Wait ~10 s for MySQL to finish initializing (`docker logs statium-mysql | tail`). Hibernate (`ddl-auto=update`) will create all tables on first boot.

---

## 5. Run the backend

> If port `8080` is free you can omit `SERVER_PORT`. The examples below use **8081** so it never collides with another service.

### Linux / macOS

```bash
chmod +x mvnw
SERVER_PORT=8081 ./mvnw spring-boot:run
```

### Windows (PowerShell)

```powershell
$env:SERVER_PORT="8081"
.\mvnw.cmd spring-boot:run
```

### Windows (cmd.exe)

```cmd
set SERVER_PORT=8081
mvnw.cmd spring-boot:run
```

You should see:

```
Tomcat started on port 8081 (http)
Started Demo1Application in ~6 seconds
```

Open:

* **Swagger UI** — http://localhost:8081/swagger-ui/index.html
* **OpenAPI JSON** — http://localhost:8081/v3/api-docs
* **Health** — http://localhost:8081/actuator/health  → `{"status":"UP"}`

---

## 6. Optional configuration

All settings are externalised through environment variables (defaults shown):

| Variable        | Default                                                                | Purpose                                |
| --------------- | ---------------------------------------------------------------------- | -------------------------------------- |
| `SERVER_PORT`   | `8080`                                                                 | HTTP port                              |
| `DB_URL`        | `jdbc:mysql://localhost:3306/Stadium?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC` | JDBC URL                |
| `DB_USERNAME`   | `main`                                                                 | DB user                                |
| `DB_PASSWORD`   | `passwd`                                                               | DB password                            |
| `MAIL_HOST`     | `smtp.gmail.com`                                                       | SMTP host (for ticket confirmation)    |
| `MAIL_PORT`     | `587`                                                                  | SMTP port                              |
| `MAIL_USERNAME` | `Your-email`                                                           | SMTP username (set to enable e‑mails)  |
| `MAIL_PASSWORD` | `passwd`                                                               | SMTP password (use Gmail app password) |

The mail health probe is **disabled** (`management.health.mail.enabled=false`) so the app stays `UP` even without real SMTP credentials. Reservations still succeed when SMTP is not configured; only the confirmation e‑mail step will return a soft error in the response — the ticket is persisted.

---

## 7. Building a deployable WAR

```bash
./mvnw -DskipTests package          # Linux/macOS
.\mvnw.cmd -DskipTests package      # Windows
```

Artifact: `target/demo1-0.0.1-SNAPSHOT.war` — deployable to any Servlet 6 container.

---

## 8. Endpoint testing

A ready-to-run cURL cookbook lives in **`stadium-reservation-backend/ENDPOINT-TESTS.md`**. It covers register/login/refresh, role-based access, stadium + match creation (multipart), seat listing, reservation, and ticket retrieval.

---

## 9. Troubleshooting

| Symptom                                                         | Fix                                                                                                  |
| --------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------- |
| `Port 8080 was already in use`                                  | Run with `SERVER_PORT=8081` (see §5).                                                                |
| `Communications link failure` on startup                        | MySQL container not ready. `docker logs statium-mysql` then retry.                                   |
| `403 Forbidden` on `/admin/**` or `/adminuser/**`               | You must send a valid `Authorization: Bearer <token>` from a user with the matching role.            |
| Reservation returns `Authentication failed` (mail)              | Expected when `MAIL_USERNAME`/`MAIL_PASSWORD` are dummies — the ticket is still created in the DB.   |
| Java version error during build                                 | Install JDK 21 and ensure `JAVA_HOME` points to it.                                                  |
| `failed to lazily initialize a collection of role: ... blocs`   | Ensure `spring.jpa.open-in-view=true` (default in this repo).                                        |

---

## 10. Tear down

```bash
# stop the app: Ctrl-C in the terminal running spring-boot:run
docker stop statium-mysql && docker rm statium-mysql   # optional, removes DB data
```
