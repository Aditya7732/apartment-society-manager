# Deployment & Operations Guide

This guide covers deployment instructions for the **Apartment Society Manager** stack using Docker Compose and standard container workflows.

---

## 1. Prerequisites

* **Docker Engine**: version 24.0+
* **Docker Compose**: version 2.20+
* Minimum **2 GB RAM** and **10 GB Disk Space** available on host.

---

## 2. Environment Configuration

1. Copy the configuration template:
   ```bash
   cp .env.example .env
   ```

2. Edit `.env` and set secure values for production:
   - Provide a strong `DATABASE_PASSWORD`.
   - Generate a 256-bit base64 `JWT_SECRET` key:
     ```bash
     openssl rand -base64 32
     ```
   - Set `ALLOWED_ORIGINS` to your production domain (e.g., `https://society.yourdomain.com`).

---

## 3. Docker Compose Orchestration

The project includes a multi-service `docker-compose.yml` that orchestrates:

* `postgres`: PostgreSQL 16 Alpine with internal health checks.
* `backend`: Spring Boot 3.2 application on OpenJDK 21 Temurin.
* `frontend`: React 18 production build served through an Nginx Alpine reverse proxy.

### Starting the Stack

```bash
# Build images and start all containers in detached mode
docker-compose up --build -d
```

### Checking Service Health

```bash
docker-compose ps
```

Verify that all services show `Up` and `(healthy)`:
```
NAME               IMAGE                     COMMAND                  SERVICE    STATUS
society_db         postgres:16-alpine        "docker-entrypoint.s…"   postgres   Up (healthy)
society_backend    society-manager-backend   "java -jar app.jar"      backend    Up
society_frontend   society-manager-frontend  "/docker-entrypoint.…"   frontend   Up
```

### Viewing Logs

```bash
# Stream logs for all containers
docker-compose logs -f

# View backend logs only
docker-compose logs -f backend
```

---

## 4. Reverse Proxy & Networking

The Nginx container acts as the single entry point on port `80` (and `443` when configured with SSL):
* Requests to `/api/` are proxied to `http://backend:8080/api/`.
* All other requests serve the static React single-page application with fallback to `/index.html`.

For public production deployments, place the stack behind an external reverse proxy (e.g., Traefik, Caddy, or Cloudflare) or add Let's Encrypt SSL certificates to Nginx.

---

## 5. Database Backup & Restoration

### Backup
```bash
docker-compose exec postgres pg_dump -U postgres society_db > society_db_backup_$(date +%Y%m%d).sql
```

### Restore
```bash
cat backup_file.sql | docker-compose exec -T postgres psql -U postgres society_db
```

---

## 6. Stopping the Application

```bash
# Gracefully stop containers
docker-compose down

# Stop containers and remove persistent database volumes (Caution: removes data)
docker-compose down -v
```
