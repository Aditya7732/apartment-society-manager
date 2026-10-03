-- =============================================================================
-- Migration: V1__initial_schema.sql
-- Description: Create initial schema for Apartment Society Management System
-- =============================================================================

-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Roles
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 2. Users
CREATE TABLE users (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    failed_login_attempts INT NOT NULL DEFAULT 0,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. User Roles Mapping
CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- 4. Buildings
CREATE TABLE buildings (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(20) NOT NULL UNIQUE,
    total_floors INT NOT NULL DEFAULT 1,
    total_flats INT NOT NULL DEFAULT 1,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Flats
CREATE TABLE flats (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    building_id UUID NOT NULL REFERENCES buildings(id) ON DELETE RESTRICT,
    flat_number VARCHAR(20) NOT NULL,
    floor_number INT NOT NULL,
    flat_type VARCHAR(30) NOT NULL, -- 1BHK, 2BHK, 3BHK, 4BHK, PENTHOUSE
    area_sqft DOUBLE PRECISION NOT NULL,
    occupancy_status VARCHAR(30) NOT NULL DEFAULT 'VACANT', -- VACANT, OWNER_OCCUPIED, TENANT_OCCUPIED, UNDER_MAINTENANCE
    owner_name VARCHAR(100),
    owner_phone VARCHAR(20),
    owner_email VARCHAR(100),
    parking_slot VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_building_flat_number UNIQUE (building_id, flat_number)
);

-- 6. Residents
CREATE TABLE residents (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID UNIQUE REFERENCES users(id) ON DELETE SET NULL,
    flat_id UUID NOT NULL REFERENCES flats(id) ON DELETE RESTRICT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    move_in_date DATE NOT NULL,
    move_out_date DATE,
    is_owner BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 7. Family Members
CREATE TABLE family_members (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    resident_id UUID NOT NULL REFERENCES residents(id) ON DELETE CASCADE,
    full_name VARCHAR(100) NOT NULL,
    relation VARCHAR(30) NOT NULL, -- SPOUSE, CHILD, PARENT, SIBLING, OTHER
    age INT,
    phone_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 8. Vehicles
CREATE TABLE vehicles (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    resident_id UUID NOT NULL REFERENCES residents(id) ON DELETE CASCADE,
    flat_id UUID NOT NULL REFERENCES flats(id) ON DELETE CASCADE,
    vehicle_number VARCHAR(30) NOT NULL UNIQUE,
    vehicle_type VARCHAR(20) NOT NULL, -- TWO_WHEELER, FOUR_WHEELER, OTHER
    parking_slot VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 9. Maintenance Bills
CREATE TABLE maintenance_bills (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    flat_id UUID NOT NULL REFERENCES flats(id) ON DELETE RESTRICT,
    bill_number VARCHAR(50) NOT NULL UNIQUE,
    billing_period VARCHAR(20) NOT NULL, -- YYYY-MM e.g. '2026-03'
    bill_date DATE NOT NULL,
    due_date DATE NOT NULL,
    base_amount NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    parking_charges NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    water_charges NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    late_fee NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    other_charges NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    discount NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    tax_amount NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    total_amount NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    paid_amount NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(30) NOT NULL DEFAULT 'GENERATED', -- GENERATED, PENDING, PARTIALLY_PAID, PAID, OVERDUE, CANCELLED
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_flat_billing_period UNIQUE (flat_id, billing_period)
);

-- 10. Payments
CREATE TABLE payments (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    bill_id UUID NOT NULL REFERENCES maintenance_bills(id) ON DELETE RESTRICT,
    resident_id UUID REFERENCES residents(id) ON DELETE SET NULL,
    receipt_number VARCHAR(50) NOT NULL UNIQUE,
    amount NUMERIC(12,2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_method VARCHAR(30) NOT NULL, -- CASH, UPI, BANK_TRANSFER, CARD, OTHER
    transaction_id VARCHAR(100),
    status VARCHAR(30) NOT NULL DEFAULT 'SUCCESS', -- PENDING, SUCCESS, FAILED, REFUNDED
    notes TEXT,
    created_by_id UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 11. Staff
CREATE TABLE staff (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID UNIQUE REFERENCES users(id) ON DELETE SET NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    role VARCHAR(50) NOT NULL, -- SECURITY, CLEANER, ELECTRICIAN, PLUMBER, GARDENER, MAINTENANCE
    joining_date DATE NOT NULL,
    salary NUMERIC(12,2),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    emergency_contact VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 12. Complaints
CREATE TABLE complaints (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    resident_id UUID NOT NULL REFERENCES residents(id) ON DELETE RESTRICT,
    flat_id UUID NOT NULL REFERENCES flats(id) ON DELETE RESTRICT,
    assigned_staff_id UUID REFERENCES staff(id) ON DELETE SET NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(40) NOT NULL, -- PLUMBING, ELECTRICAL, SECURITY, CLEANING, LIFT, WATER, PARKING, NOISE, MAINTENANCE, OTHER
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM', -- LOW, MEDIUM, HIGH, CRITICAL
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN', -- OPEN, ASSIGNED, IN_PROGRESS, RESOLVED, CLOSED, REJECTED
    resolution TEXT,
    closed_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 13. Complaint Comments
CREATE TABLE complaint_comments (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    complaint_id UUID NOT NULL REFERENCES complaints(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    comment TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 14. Society Expenses
CREATE TABLE society_expenses (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    category VARCHAR(50) NOT NULL, -- ELECTRICITY, WATER, SECURITY, CLEANING, REPAIRS, LIFT_MAINTENANCE, GARDENING, STAFF_SALARY, INSURANCE, EVENTS, OTHER
    amount NUMERIC(12,2) NOT NULL,
    expense_date DATE NOT NULL,
    vendor_name VARCHAR(100),
    description TEXT,
    payment_method VARCHAR(30) NOT NULL,
    invoice_number VARCHAR(100),
    attachment_path VARCHAR(255),
    created_by_id UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 15. Notices
CREATE TABLE notices (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM', -- LOW, MEDIUM, HIGH, EMERGENCY
    audience VARCHAR(30) NOT NULL DEFAULT 'ALL', -- ALL, RESIDENTS, OWNERS, TENANTS
    publish_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expiry_date TIMESTAMP,
    attachment_path VARCHAR(255),
    created_by_id UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 16. Visitors
CREATE TABLE visitors (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    flat_id UUID NOT NULL REFERENCES flats(id) ON DELETE RESTRICT,
    resident_id UUID REFERENCES residents(id) ON DELETE SET NULL,
    visitor_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    purpose VARCHAR(100) NOT NULL,
    expected_arrival TIMESTAMP NOT NULL,
    expected_departure TIMESTAMP,
    actual_arrival TIMESTAMP,
    actual_departure TIMESTAMP,
    vehicle_number VARCHAR(30),
    status VARCHAR(30) NOT NULL DEFAULT 'EXPECTED', -- EXPECTED, CHECKED_IN, CHECKED_OUT, CANCELLED
    checked_in_by_id UUID REFERENCES users(id) ON DELETE SET NULL,
    checked_out_by_id UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 17. Audit Logs
CREATE TABLE audit_logs (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100),
    ip_address VARCHAR(45),
    previous_value TEXT,
    new_value TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 18. Notifications
CREATE TABLE notifications (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL, -- BILL, PAYMENT, COMPLAINT, NOTICE, VISITOR, GENERAL
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    reference_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_flats_building ON flats(building_id);
CREATE INDEX idx_residents_flat ON residents(flat_id);
CREATE INDEX idx_residents_user ON residents(user_id);
CREATE INDEX idx_bills_flat ON maintenance_bills(flat_id);
CREATE INDEX idx_bills_status ON maintenance_bills(status);
CREATE INDEX idx_bills_period ON maintenance_bills(billing_period);
CREATE INDEX idx_payments_bill ON payments(bill_id);
CREATE INDEX idx_payments_resident ON payments(resident_id);
CREATE INDEX idx_complaints_resident ON complaints(resident_id);
CREATE INDEX idx_complaints_flat ON complaints(flat_id);
CREATE INDEX idx_complaints_status ON complaints(status);
CREATE INDEX idx_expenses_category ON society_expenses(category);
CREATE INDEX idx_expenses_date ON society_expenses(expense_date);
CREATE INDEX idx_visitors_flat ON visitors(flat_id);
CREATE INDEX idx_visitors_status ON visitors(status);
CREATE INDEX idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_created ON audit_logs(created_at);
CREATE INDEX idx_notifications_user ON notifications(user_id, is_read);
