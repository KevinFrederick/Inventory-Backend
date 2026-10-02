# Inventory Management Backend

A high-performance, containerized Ktor backend service built to power the Android inventory application, featuring MinIO cloud storage, rate limiting, and offline-first data synchronization

## Key Features
* **REST & Real-Time Sync:** Robust endpoints and WebSocket management (`SyncSocketManager.kt`) for handling offline-first client synchronization.
* **Cloud Object Storage:** Integrated MinIO service (`core:storage`) for secure multipart image uploads.
* **Production-Ready Middlewares:** Built-in rate limiting, proxy redundancy configurations, and request validation pipelines.
* **Database Management:** Modularized database factory layer (`core:database`) ensuring clean data persistence.

## Tech Stack
* **Language & Framework:** Kotlin, Ktor Server
* **Database:** PostgreSQL (via Exposed ORM)
* **Storage:** MinIO (S3-compatible object storage for images)
* **Architecture:** Clean Architecture (Domain -> Use Case -> Ktor Route)
* **Containerization:** Docker

## Architecture & Flow
This project strictly separates business logic from framework details:
1. **Routing:** Parses incoming JSON and routes it to Use Cases.
2. **Use Cases (Interactors):** Handles business logic, file size limits (5MB), and coordinates with validators.
3. **Domain Validators:** Enforces maximum lengths, barcode formats, and nullability independent of the database.
4. **Repositories:** Maps Domain models to Exposed Database tables. Conflict errors (e.g., duplicate SKUs) are caught here.

## Offline-First Sync Mechanism
The API relies on a bulk push/pull sync engine rather than single-item CRUD operations.

* **Endpoint:** `POST /sync`, `GET /sync/pull`
* **REST Endpoints (`SyncRoute.kt`):** Intercepts bulk payloads (`SyncPayloadDto`) containing batches of created, updated, or deleted `Categories`, `Locations`, `Products`, and `StockBatches`
* **Real-Time Sync:** Manages active socket connections to push live state updates to connected clients.
* **Validation:** It runs validation across all arrays before committing. If any item is malformed, the entire sync batch is rejected.
* **Conflict Handling:** The database uses `UPSERT` mechanisms. If a client pushes a record with an existing ID, the server cleanly overwrites it to match the client's latest state.

## Building & Running Locally
This project is fully containerized and uses Docker Secrets for secure credential management, alongside Cloudflare Tunnels for secure external access.

### Prerequisites
* **Docker & Docker Compose** installed.
* A Cloudflare Tunnel token (if exposing externally).

### Step 1: Set Up Secrets
For security, passwords and tokens are not stored in environment variables. You must create physical text files in a `secrets/` directory.
1. Create a folder named `secrets` in the root of the project:
   ```bash
   mkdir secrets
   ```
2. Create the following files inside the secrets directory and paste your credentials into them (no extra spaces or newlines):
* **secrets/db_pass.txt** (Your PostgreSQL password)
* **secrets/minio_pass.txt** (Your MinIO root password)
* **secrets/jwt_secret.txt** (Your secure JWT signing key)
* **secrets/auth_pepper.txt** (Your secure password hashing pepper)
* **secrets/tunnel_token.txt** *(Optional: Your Cloudflare Tunnel token. If you are not using Cloudflare, just leave this file empty so Docker doesn't throw a missing file error).*

### Step 2: Configure Environment Variables
The application uses a `.env` file to inject non-sensitive configuration into the containers.
1. Copy the `.env.example` file and rename it to `.env`.
2. Ensure the values match your local Docker setup:
```env
# Database Configuration
POSTGRES_DB = inventory_db
POSTGRES_USER = admin
POSTGRES_JDBC_URL = jdbc:postgresql://postgres-db:5432/inventory_db?reWriteBatchedInserts=true

# MinIO Configuration
MINIO_URL = http://minio:9000
MINIO_PUBLIC_URL = https://your-cloudflare-domain
MINIO_ROOT_USER = admin

# JWT Configuration
JWT_ISSUER = your-issuer
JWT_AUDIENCE = your-audience
```

### Step 3: Build and Run the Stack
* **Option A: With Cloudflare**  
Run the entire stack (Database, MinIO, Ktor Backend, and Cloudflare Tunnel) using Docker Compose:  
*(Requires a valid token in `secrets/tunnel_token.txt`)*
    ```bash
    docker-compose --profile tunnel up --build -d
    ```
* **Option B: Without Cloudflare**  
This will start the PostgreSQL database, MinIO storage, and Ktor Backend on localhost.
    ```bash
    docker-compose up --build -d
    ```
  
### Step 4: Verify the Services
Once started, the services are bound to localhost for security:
* **Ktor API:** http://localhost:8081 (Mapped from container port 8080)
* **MinIO Web Console:** http://localhost:9001
* **Cloudflare Tunnel:** Automatically routes external traffic to your Ktor backend based on your tunnel configuration.

To view the logs of your backend to ensure it started correctly:
```bash
docker logs inventory-be -f
```