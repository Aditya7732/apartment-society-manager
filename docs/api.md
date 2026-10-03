# REST API Reference

The **Apartment Society Manager** backend exposes a RESTful API over HTTP/JSON with standard status codes and JWT Bearer authentication.

Interactive Swagger documentation is available locally at:
* **Swagger UI**: `http://localhost:8080/swagger-ui.html`
* **OpenAPI v3 JSON**: `http://localhost:8080/v3/api-docs`

---

## Authentication & Headers

Protected endpoints require a valid JWT access token provided in the HTTP request header:

```http
Authorization: Bearer <your_jwt_access_token>
Content-Type: application/json
```

---

## API Modules Overview

### 1. Authentication (`/api/auth`)
* `POST /api/auth/login`: Authenticate with username and password. Returns JWT token, refresh token, user ID, role, and profile info.
* `POST /api/auth/refresh`: Refresh an access token using a valid refresh token.
* `POST /api/auth/change-password`: Update the current authenticated user's password.
* `GET /api/auth/current-user`: Fetch current authenticated user's details and active role.

### 2. User Management (`/api/users`)
* `GET /api/users`: List registered users (filtered by role, active status).
* `GET /api/users/{id}`: Get user profile by UUID.
* `PUT /api/users/{id}`: Update user profile details.
* `PUT /api/users/{id}/status`: Activate or deactivate a user account.
* `PUT /api/users/{id}/roles`: Assign or update user roles (`ROLE_SUPER_ADMIN`, etc.).

### 3. Buildings & Towers (`/api/buildings`)
* `GET /api/buildings`: Retrieve all buildings in the society.
* `POST /api/buildings`: Create a new building (name, code, total floors, total flats).
* `GET /api/buildings/{id}`: Get building details and associated flat count.
* `PUT /api/buildings/{id}`: Update building metadata.
* `DELETE /api/buildings/{id}`: Remove building (restricted if flats exist).

### 4. Flats (`/api/flats`)
* `GET /api/flats`: List flats with optional filtering by building ID, floor number, or occupancy status.
* `POST /api/flats`: Register a new flat unit (area sqft, flat type, owner details, parking slot).
* `GET /api/flats/{id}`: Get detailed flat profile.
* `PUT /api/flats/{id}`: Update flat information and occupancy state (`VACANT`, `OWNER_OCCUPIED`, `TENANT_OCCUPIED`).
* `DELETE /api/flats/{id}`: Delete flat record.

### 5. Residents (`/api/residents`)
* `GET /api/residents`: List society residents (filterable by building, flat, owner vs tenant).
* `POST /api/residents`: Register a resident profile and link to flat.
* `GET /api/residents/{id}`: Get resident details, emergency contacts, and lease info.
* `PUT /api/residents/{id}`: Update resident profile.
* `POST /api/residents/{id}/family-members`: Add family member to resident profile.
* `POST /api/residents/{id}/vehicles`: Register a vehicle for parking permit.

### 6. Maintenance Billing (`/api/maintenance-bills`)
* `GET /api/maintenance-bills`: Paginated list of maintenance bills (filtered by period, status, flat).
* `POST /api/maintenance-bills/generate`: Batch bill generation for a specific month across all flats.
* `GET /api/maintenance-bills/{id}`: Retrieve bill breakdown (base amount, parking, water, late fees).
* `GET /api/maintenance-bills/{id}/pdf`: Download printable PDF maintenance bill.
* `PUT /api/maintenance-bills/{id}/status`: Update bill status (`PAID`, `OVERDUE`, `CANCELLED`).

### 7. Payments (`/api/payments`)
* `GET /api/payments`: Transaction history with filters by date, bill, and resident.
* `POST /api/payments`: Record a payment against an open maintenance bill (UPI, Card, Bank Transfer, Cash).
* `GET /api/payments/{id}`: Get payment confirmation details.
* `GET /api/payments/{id}/receipt`: Download official PDF payment receipt.

### 8. Complaints & Helpdesk (`/api/complaints`)
* `GET /api/complaints`: List tickets (filtered by category, status, priority, resident).
* `POST /api/complaints`: Raise a new helpdesk ticket (Plumbing, Electrical, Lift, Security, etc.).
* `GET /api/complaints/{id}`: Ticket details, assigned staff, and comment history.
* `PUT /api/complaints/{id}/status`: Update ticket status (`OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`).
* `PUT /api/complaints/{id}/assign`: Assign ticket to internal maintenance staff member.
* `POST /api/complaints/{id}/comments`: Post a comment to the ticket thread.

### 9. Staff Management (`/api/staff`)
* `GET /api/staff`: List society staff members (Security, Electricians, Cleaners, Plumbers).
* `POST /api/staff`: Onboard a new staff member.
* `GET /api/staff/{id}`: Get staff profile, assigned complaints, and contact.
* `PUT /api/staff/{id}`: Update staff information or active status.

### 10. Society Expenses (`/api/expenses`)
* `GET /api/expenses`: List expense vouchers with date range and category filters.
* `POST /api/expenses`: Record an operational expenditure (vendor, invoice #, amount, category).
* `GET /api/expenses/{id}`: Retrieve voucher details.
* `DELETE /api/expenses/{id}`: Remove or void expense record.

### 11. Notice Board (`/api/notices`)
* `GET /api/notices`: Fetch active announcements (filtered by audience: `ALL`, `RESIDENTS`, `OWNERS`).
* `POST /api/notices`: Publish a new announcement with priority level (`HIGH`, `EMERGENCY`, `MEDIUM`).
* `GET /api/notices/{id}`: View full notice body.
* `DELETE /api/notices/{id}`: Archive or delete an announcement.

### 12. Visitor Management (`/api/visitors`)
* `GET /api/visitors`: View gate visitors log (filter by date, status, flat).
* `POST /api/visitors/pre-register`: Resident pre-registers expected guest or delivery.
* `POST /api/visitors/check-in`: Security logs guest arrival, vehicle number, and issues badge.
* `POST /api/visitors/{id}/check-out`: Record visitor departure timestamp.

### 13. Dashboard Metrics (`/api/dashboard`)
* `GET /api/dashboard/admin`: High-level KPI metrics (total flats, occupancy %, dues collected, pending tickets, monthly collections trend).
* `GET /api/dashboard/resident`: Resident-specific overview (outstanding balance, active tickets, pending visitors).
* `GET /api/dashboard/security`: Real-time gate statistics (visitors inside, expected today).

### 14. Reports & Exports (`/api/reports`)
* `GET /api/reports/defaulters`: List flats with outstanding overdue balances.
* `GET /api/reports/defaulters/pdf`: Download PDF defaulters report.
* `GET /api/reports/financial/pdf`: Download comprehensive monthly financial balance report.
* `GET /api/reports/financial/csv`: Export collection transactions to CSV.
* `GET /api/reports/expenses/pdf`: Download expenses audit PDF.
* `GET /api/reports/expenses/csv`: Export expense records to CSV.
* `GET /api/reports/residents/pdf`: Download society resident directory PDF.

### 15. Notifications (`/api/notifications`)
* `GET /api/notifications`: Retrieve current user's in-app alerts.
* `PUT /api/notifications/{id}/read`: Mark notification as read.
* `PUT /api/notifications/read-all`: Mark all notifications as read.

### 16. Audit Logs (`/api/audit-logs`)
* `GET /api/audit-logs`: System audit trail with user ID, IP address, action timestamp, and change diff.

---

## Standard Error Response Format

Errors return standard HTTP status codes accompanied by a structured JSON payload:

```json
{
  "timestamp": "2026-10-03T12:00:00.000Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Flat with number A-101 already exists in Harmony Tower",
  "path": "/api/flats"
}
```
