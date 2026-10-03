# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-10-03

### Added
- Initial public release of **Apartment Society Manager**.
- Full-stack architecture featuring Spring Boot 3.2.5 REST backend and React 18 / TypeScript frontend.
- JWT-based authentication and Role-Based Access Control (RBAC) with 5 roles: `SUPER_ADMIN`, `SOCIETY_ADMIN`, `ACCOUNTANT`, `RESIDENT`, and `SECURITY`.
- Building and Flat management with floor indexing, occupancy tracking, and owner details.
- Resident registry tracking owners, tenants, family members, and registered vehicles.
- Maintenance billing engine supporting flat area-based calculation, water charges, parking fees, and batch bill generation.
- Payment processing module with transaction tracking and PDF receipt download via OpenPDF.
- Complaint helpdesk management with priority tracking, staff assignment, status lifecycle, and comment threads.
- Visitor management gate log with badge numbers, vehicle details, entry/exit timestamps, and resident pre-registration.
- Society expense tracker with category management, vendor logging, and payment method records.
- Notice broadcast board with audience targeting and priority flags.
- Comprehensive financial and operational reports with PDF and CSV export capabilities.
- Immutable audit logging tracking user actions and IP addresses.
- Multi-container Docker Compose configuration for PostgreSQL, Spring Boot backend, and Nginx-proxied React frontend.
- Flyway database migration scripts (`V1__initial_schema.sql` and `V2__seed_data.sql`).
- GitHub Actions CI pipeline for automated testing and builds.
