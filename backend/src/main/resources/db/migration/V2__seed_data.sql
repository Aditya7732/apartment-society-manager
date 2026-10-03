-- =============================================================================
-- Migration: V2__seed_data.sql
-- Description: Seed initial demo & development data
-- Default Passwords:
-- admin / manager / accountant / security / resident1 / resident2:
-- Password is: "Admin@123" for admin, "Manager@123" for manager, "Accountant@123" for accountant,
-- "Security@123" for security, "Resident@123" for residents.
-- Standard BCrypt Hash for "Password@123" (used universally for ease):
-- $2a$10$e0MYzXyjpJS7Pd0RVvHwHe1z4J5l5eF/uCq.W9E0k0v8x/0C1D0i6
-- =============================================================================

-- 1. Insert Roles
INSERT INTO roles (id, name) VALUES 
(1, 'ROLE_SUPER_ADMIN'),
(2, 'ROLE_SOCIETY_ADMIN'),
(3, 'ROLE_ACCOUNTANT'),
(4, 'ROLE_RESIDENT'),
(5, 'ROLE_SECURITY')
ON CONFLICT DO NOTHING;

-- Hash below is BCrypt for: Password@123
-- $2a$10$e0MYzXyjpJS7Pd0RVvHwHe1z4J5l5eF/uCq.W9E0k0v8x/0C1D0i6

-- 2. Insert Base Users
INSERT INTO users (id, username, email, password, full_name, phone_number, active) VALUES
('11111111-1111-1111-1111-111111111111', 'admin', 'admin@society.com', '$2a$10$xrKTGHdSu8eOtKufIc4sQOQyIXpPeGKQNDSLxnCWLkWqeMzCjQYrS', 'System Administrator', '+91 9876543210', true),
('22222222-2222-2222-2222-222222222222', 'manager', 'manager@society.com', '$2a$10$xrKTGHdSu8eOtKufIc4sQOQyIXpPeGKQNDSLxnCWLkWqeMzCjQYrS', 'Rajesh Sharma (Society Manager)', '+91 9876543211', true),
('33333333-3333-3333-3333-333333333333', 'accountant', 'accountant@society.com', '$2a$10$xrKTGHdSu8eOtKufIc4sQOQyIXpPeGKQNDSLxnCWLkWqeMzCjQYrS', 'Sunita Mehta (Accountant)', '+91 9876543212', true),
('44444444-4444-4444-4444-444444444444', 'security', 'security@society.com', '$2a$10$xrKTGHdSu8eOtKufIc4sQOQyIXpPeGKQNDSLxnCWLkWqeMzCjQYrS', 'Ramesh Kumar (Chief Security)', '+91 9876543213', true),
('55555555-5555-5555-5555-555555555555', 'resident1', 'vikram.verma@gmail.com', '$2a$10$xrKTGHdSu8eOtKufIc4sQOQyIXpPeGKQNDSLxnCWLkWqeMzCjQYrS', 'Vikram Verma', '+91 9876543214', true),
('66666666-6666-6666-6666-666666666666', 'resident2', 'priya.singh@gmail.com', '$2a$10$xrKTGHdSu8eOtKufIc4sQOQyIXpPeGKQNDSLxnCWLkWqeMzCjQYrS', 'Priya Singh', '+91 9876543215', true)
ON CONFLICT DO NOTHING;

-- 3. Assign Roles
INSERT INTO user_roles (user_id, role_id) VALUES
('11111111-1111-1111-1111-111111111111', 1), -- SUPER_ADMIN
('22222222-2222-2222-2222-222222222222', 2), -- SOCIETY_ADMIN
('33333333-3333-3333-3333-333333333333', 3), -- ACCOUNTANT
('44444444-4444-4444-4444-444444444444', 5), -- SECURITY
('55555555-5555-5555-5555-555555555555', 4), -- RESIDENT
('66666666-6666-6666-6666-666666666666', 4)  -- RESIDENT
ON CONFLICT DO NOTHING;

-- 4. Insert Buildings
INSERT INTO buildings (id, name, code, total_floors, total_flats, description) VALUES
('b1111111-1111-1111-1111-111111111111', 'Harmony Tower (Block A)', 'BLK-A', 10, 40, 'Premium residential tower with garden view'),
('b2222222-2222-2222-2222-222222222222', 'Sunrise Crest (Block B)', 'BLK-B', 8, 32, 'Modern tower facing main boulevard'),
('b3333333-3333-3333-3333-333333333333', 'Emerald Heights (Block C)', 'BLK-C', 12, 48, 'Luxury high-rise with clubhouse access')
ON CONFLICT DO NOTHING;

-- 5. Insert Flats
INSERT INTO flats (id, building_id, flat_number, floor_number, flat_type, area_sqft, occupancy_status, owner_name, owner_phone, owner_email, parking_slot) VALUES
('f1111111-1111-1111-1111-111111111111', 'b1111111-1111-1111-1111-111111111111', 'A-101', 1, '2BHK', 1250.0, 'OWNER_OCCUPIED', 'Vikram Verma', '+91 9876543214', 'vikram.verma@gmail.com', 'P-A101'),
('f2222222-2222-2222-2222-222222222222', 'b1111111-1111-1111-1111-111111111111', 'A-102', 1, '3BHK', 1650.0, 'TENANT_OCCUPIED', 'Amitabh Patel', '+91 9876543299', 'amitabh.patel@gmail.com', 'P-A102'),
('f3333333-3333-3333-3333-333333333333', 'b2222222-2222-2222-2222-222222222222', 'B-201', 2, '2BHK', 1300.0, 'OWNER_OCCUPIED', 'Priya Singh', '+91 9876543215', 'priya.singh@gmail.com', 'P-B201'),
('f4444444-4444-4444-4444-444444444444', 'b2222222-2222-2222-2222-222222222222', 'B-202', 2, '3BHK', 1700.0, 'VACANT', 'Sanjay Kapoor', '+91 9876543288', 'sanjay.kapoor@gmail.com', 'P-B202'),
('f5555555-5555-5555-5555-555555555555', 'b3333333-3333-3333-3333-333333333333', 'C-501', 5, '4BHK', 2200.0, 'UNDER_MAINTENANCE', 'Rohan Gupta', '+91 9876543277', 'rohan.gupta@gmail.com', 'P-C501')
ON CONFLICT DO NOTHING;

-- 6. Insert Residents
INSERT INTO residents (id, user_id, flat_id, first_name, last_name, email, phone, emergency_contact_name, emergency_contact_phone, move_in_date, is_owner, active) VALUES
('a1111111-1111-1111-1111-111111111111', '55555555-5555-5555-5555-555555555555', 'f1111111-1111-1111-1111-111111111111', 'Vikram', 'Verma', 'vikram.verma@gmail.com', '+91 9876543214', 'Sunil Verma', '+91 9811122233', '2022-01-15', true, true),
('a2222222-2222-2222-2222-222222222222', '66666666-6666-6666-6666-666666666666', 'f3333333-3333-3333-3333-333333333333', 'Priya', 'Singh', 'priya.singh@gmail.com', '+91 9876543215', 'Anil Singh', '+91 9822233344', '2023-04-10', true, true)
ON CONFLICT DO NOTHING;

-- 7. Insert Family Members
INSERT INTO family_members (id, resident_id, full_name, relation, age, phone_number) VALUES
('af111111-1111-1111-1111-111111111111', 'a1111111-1111-1111-1111-111111111111', 'Neha Verma', 'SPOUSE', 32, '+91 9876549911'),
('af222222-2222-2222-2222-222222222222', 'a1111111-1111-1111-1111-111111111111', 'Aarav Verma', 'CHILD', 6, NULL),
('af333333-3333-3333-3333-333333333333', 'a2222222-2222-2222-2222-222222222222', 'Karan Singh', 'SPOUSE', 35, '+91 9876549922')
ON CONFLICT DO NOTHING;

-- 8. Insert Vehicles
INSERT INTO vehicles (id, resident_id, flat_id, vehicle_number, vehicle_type, parking_slot) VALUES
('a1111111-9999-1111-1111-111111111111', 'a1111111-1111-1111-1111-111111111111', 'f1111111-1111-1111-1111-111111111111', 'MH-02-CB-1234', 'FOUR_WHEELER', 'P-A101'),
('a2222222-9999-2222-2222-222222222222', 'a2222222-2222-2222-2222-222222222222', 'f3333333-3333-3333-3333-333333333333', 'MH-02-XY-9876', 'TWO_WHEELER', 'P-B201')
ON CONFLICT DO NOTHING;

-- 9. Insert Staff
INSERT INTO staff (id, user_id, full_name, phone, role, joining_date, salary, active, emergency_contact) VALUES
('c1111111-1111-1111-1111-111111111111', '44444444-4444-4444-4444-444444444444', 'Ramesh Kumar', '+91 9876543213', 'SECURITY', '2021-06-01', 18000.00, true, '+91 9999911111'),
('c2222222-2222-2222-2222-222222222222', NULL, 'Suresh Yadav', '+91 9876500001', 'ELECTRICIAN', '2022-02-15', 22000.00, true, '+91 9999922222'),
('c3333333-3333-3333-3333-333333333333', NULL, 'Mahesh Shinde', '+91 9876500002', 'PLUMBER', '2022-03-01', 20000.00, true, '+91 9999933333')
ON CONFLICT DO NOTHING;

-- 10. Insert Maintenance Bills
INSERT INTO maintenance_bills (id, flat_id, bill_number, billing_period, bill_date, due_date, base_amount, parking_charges, water_charges, late_fee, other_charges, discount, tax_amount, total_amount, paid_amount, status, notes) VALUES
('da111111-1111-1111-1111-111111111111', 'f1111111-1111-1111-1111-111111111111', 'BILL-202603-A101', '2026-03', '2026-03-01', '2026-03-15', 3500.00, 500.00, 300.00, 0.00, 200.00, 0.00, 0.00, 4500.00, 4500.00, 'PAID', 'Monthly society maintenance fee'),
('da222222-2222-2222-2222-222222222222', 'f3333333-3333-3333-3333-333333333333', 'BILL-202603-B201', '2026-03', '2026-03-01', '2026-03-15', 3500.00, 500.00, 300.00, 0.00, 0.00, 0.00, 0.00, 4300.00, 0.00, 'OVERDUE', 'Monthly society maintenance fee'),
('da333333-3333-3333-3333-333333333333', 'f2222222-2222-2222-2222-222222222222', 'BILL-202603-A102', '2026-03', '2026-03-01', '2026-03-15', 4200.00, 500.00, 350.00, 0.00, 0.00, 0.00, 0.00, 5050.00, 2500.00, 'PARTIALLY_PAID', 'Partial payment received'),
('da444444-4444-4444-4444-444444444444', 'f1111111-1111-1111-1111-111111111111', 'BILL-202604-A101', '2026-04', '2026-04-01', '2026-04-15', 3500.00, 500.00, 300.00, 0.00, 0.00, 0.00, 0.00, 4300.00, 0.00, 'PENDING', 'Monthly society maintenance fee')
ON CONFLICT DO NOTHING;

-- 11. Insert Payments
INSERT INTO payments (id, bill_id, resident_id, receipt_number, amount, payment_date, payment_method, transaction_id, status, notes, created_by_id) VALUES
('e1111111-1111-1111-1111-111111111111', 'da111111-1111-1111-1111-111111111111', 'a1111111-1111-1111-1111-111111111111', 'REC-202603-0001', 4500.00, '2026-03-05 10:30:00+05:30', 'UPI', 'UPI/9876541230/SUCCESS', 'SUCCESS', 'Full bill payment paid online', '55555555-5555-5555-5555-555555555555'),
('e2222222-2222-2222-2222-222222222222', 'da333333-3333-3333-3333-333333333333', NULL, 'REC-202603-0002', 2500.00, '2026-03-10 14:15:00+05:30', 'CASH', 'CASH-REF-9081', 'SUCCESS', 'Partial cash payment made at society office', '33333333-3333-3333-3333-333333333333')
ON CONFLICT DO NOTHING;

-- 12. Insert Complaints
INSERT INTO complaints (id, resident_id, flat_id, assigned_staff_id, title, description, category, priority, status, resolution) VALUES
('c1111111-1111-1111-1111-111111111111', 'a1111111-1111-1111-1111-111111111111', 'f1111111-1111-1111-1111-111111111111', 'c3333333-3333-3333-3333-333333333333', 'Water leakage in master bathroom', 'Water is leaking continuously from the sink pipe underneath.', 'PLUMBING', 'HIGH', 'IN_PROGRESS', NULL),
('c2222222-2222-2222-2222-222222222222', 'a2222222-2222-2222-2222-222222222222', 'f3333333-3333-3333-3333-333333333333', 'c2222222-2222-2222-2222-222222222222', 'Corridor light flickering near B-201', 'The LED tubelight in the second-floor corridor keeps flickering.', 'ELECTRICAL', 'LOW', 'RESOLVED', 'Replaced faulty LED driver and tube.'),
('c3333333-3333-3333-3333-333333333333', 'a1111111-1111-1111-1111-111111111111', 'f1111111-1111-1111-1111-111111111111', NULL, 'Elevator B making noise', 'Lift B in Harmony tower makes a grinding noise on 4th floor.', 'LIFT', 'CRITICAL', 'OPEN', NULL)
ON CONFLICT DO NOTHING;

-- 13. Insert Complaint Comments
INSERT INTO complaint_comments (id, complaint_id, user_id, comment) VALUES
('cb111111-1111-1111-1111-111111111111', 'c1111111-1111-1111-1111-111111111111', '55555555-5555-5555-5555-555555555555', 'Please send plumber before 5 PM if possible.'),
('cb222222-2222-2222-2222-222222222222', 'c1111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 'Assigned to Mahesh (Plumber). He will visit around 3:30 PM.')
ON CONFLICT DO NOTHING;

-- 14. Insert Society Expenses
INSERT INTO society_expenses (id, category, amount, expense_date, vendor_name, description, payment_method, invoice_number, created_by_id) VALUES
('e1111111-1111-1111-1111-111111111111', 'ELECTRICITY', 45000.00, '2026-03-10', 'State Electricity Board', 'Common area power bill for Block A, B & C', 'BANK_TRANSFER', 'INV-SEB-9871', '33333333-3333-3333-3333-333333333333'),
('e2222222-2222-2222-2222-222222222222', 'SECURITY', 60000.00, '2026-03-01', 'Apex Security Agency', 'Monthly 24x7 security guard service contract', 'BANK_TRANSFER', 'INV-APEX-0326', '33333333-3333-3333-3333-333333333333'),
('e3333333-3333-3333-3333-333333333333', 'LIFT_MAINTENANCE', 18500.00, '2026-03-12', 'Otis Elevator Corp', 'Quarterly AMC servicing for Block A & C elevators', 'BANK_TRANSFER', 'INV-OTIS-7712', '33333333-3333-3333-3333-333333333333')
ON CONFLICT DO NOTHING;

-- 15. Insert Notices
INSERT INTO notices (id, title, content, priority, audience, publish_date, expiry_date, created_by_id) VALUES
('ad111111-1111-1111-1111-111111111111', 'Annual General Body Meeting (AGM) Scheduled', 'The AGM for the current financial year will be held on Sunday, April 15th at 10:30 AM in the Clubhouse. All flat owners are requested to attend.', 'HIGH', 'ALL', '2026-03-20 09:00:00+05:30', '2026-04-16 00:00:00+05:30', '22222222-2222-2222-2222-222222222222'),
('ad222222-2222-2222-2222-222222222222', 'Scheduled Water Tank Cleaning', 'Water supply will be interrupted between 10 AM and 4 PM on Thursday, March 30th due to overhead tank chemical cleaning.', 'EMERGENCY', 'RESIDENTS', '2026-03-25 08:00:00+05:30', '2026-03-31 00:00:00+05:30', '22222222-2222-2222-2222-222222222222')
ON CONFLICT DO NOTHING;

-- 16. Insert Visitors
INSERT INTO visitors (id, flat_id, resident_id, visitor_name, phone_number, purpose, expected_arrival, actual_arrival, actual_departure, vehicle_number, status, checked_in_by_id) VALUES
('ae111111-1111-1111-1111-111111111111', 'f1111111-1111-1111-1111-111111111111', 'a1111111-1111-1111-1111-111111111111', 'Rahul Saxena', '+91 9811199988', 'Personal Visit', '2026-03-27 14:00:00+05:30', '2026-03-27 14:10:00+05:30', NULL, 'KA-01-AB-4321', 'CHECKED_IN', '44444444-4444-4444-4444-444444444444'),
('ae222222-2222-2222-2222-222222222222', 'f3333333-3333-3333-3333-333333333333', 'a2222222-2222-2222-2222-222222222222', 'Swiggy Delivery', '+91 9811199977', 'Food Delivery', '2026-03-27 18:00:00+05:30', NULL, NULL, NULL, 'EXPECTED', NULL)
ON CONFLICT DO NOTHING;

-- 17. Insert Notifications
INSERT INTO notifications (id, user_id, title, message, type, is_read, reference_id) VALUES
('af111111-9999-1111-1111-111111111111', '55555555-5555-5555-5555-555555555555', 'Maintenance Bill Generated', 'Your maintenance bill for April 2026 (₹ 4,300) has been generated.', 'BILL', false, 'da444444-4444-4444-4444-444444444444'),
('af222222-9999-2222-2222-222222222222', '55555555-5555-5555-5555-555555555555', 'Complaint In Progress', 'Plumbing complaint #c1111111 has been assigned to Mahesh Shinde.', 'COMPLAINT', true, 'c1111111-1111-1111-1111-111111111111')
ON CONFLICT DO NOTHING;

-- 18. Audit Logs
INSERT INTO audit_logs (id, user_id, action, entity_type, entity_id, ip_address, new_value) VALUES
('ab111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 'CREATE_MAINTENANCE_BILL', 'MAINTENANCE_BILL', 'da444444-4444-4444-4444-444444444444', '127.0.0.1', 'Bill generated for April 2026')
ON CONFLICT DO NOTHING;
