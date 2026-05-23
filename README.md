# Recipy

Recipe sharing API built with Spring Boot.

## Demo Accounts

| Role  | Email             | Password   |
|-------|-------------------|------------|
| Admin | admin@recipy.com  | Admin@123  |
| User  | user@recipy.com   | User@123   |

---

## API Endpoints

### Authentication (`/api/auth`)

| Method | Endpoint            | Description          | Auth |
|--------|---------------------|----------------------|------|
| POST   | `/register`         | Register a new user  | No   |
| POST   | `/login`            | Login                | No   |
| GET    | `/verify?token=`    | Verify email         | No   |
| GET    | `/me`               | Get current user     | Yes  |
| POST   | `/logout`           | Logout               | Yes  |
| POST   | `/change-password`  | Change password      | Yes  |
| POST   | `/forgot-password`  | Request reset link   | No   |
| POST   | `/reset-password`   | Reset password       | No   |

### Recipes (`/api/recipes`)

| Method | Endpoint         | Description            | Auth |
|--------|------------------|------------------------|------|
| GET    | `/`              | List (paginated, filterable) | Yes |
| POST   | `/`              | Create                 | Yes  |
| GET    | `/{id}`          | Get by ID              | Yes  |
| PATCH  | `/{id}`          | Update                 | Yes  |
| DELETE | `/{id}`          | Soft delete            | Yes  |
| PUT    | `/{id}/restore`  | Restore                | Yes  |

### Categories (`/api/categories`)

| Method | Endpoint | Description     | Auth |
|--------|----------|-----------------|------|
| GET    | `/`      | List available  | Yes  |

### Ingredients (`/api/ingredients`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST   | `/`      | Create      | Yes  |
| GET    | `/{id}`  | Get by ID   | Yes  |
| PATCH  | `/{id}`  | Update      | Yes  |
| DELETE | `/{id}`  | Delete      | Yes  |

### Users (`/api/users`)

| Method | Endpoint    | Description      | Auth |
|--------|-------------|------------------|------|
| PUT    | `/profile`  | Update profile   | Yes  |

### Admin (`/api/admin/**`) — Requires ADMIN role

| Method | Endpoint                | Description        |
|--------|-------------------------|--------------------|
| GET    | `/recipes`              | List all (paginated) |
| DELETE | `/users/{id}`           | Soft delete user   |
| PUT    | `/users/{id}/restore`   | Restore user       |

---

## Run with Docker

```bash
docker compose up --build
```

## Run Locally (IntelliJ)

1. Copy `.env.example` to `.env` and fill in the values:
```bash
cp .env.example .env
```

2. Make sure you have a local PostgreSQL instance running and your `.env` `DB_URL` points to it:
```
DB_URL=jdbc:postgresql://localhost:5432/recipy
```

3. Open the project in IntelliJ, click the **Run** button