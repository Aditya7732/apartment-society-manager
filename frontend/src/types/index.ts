export type RoleType = 
  | 'ROLE_SUPER_ADMIN' 
  | 'ROLE_SOCIETY_ADMIN' 
  | 'ROLE_ACCOUNTANT' 
  | 'ROLE_RESIDENT' 
  | 'ROLE_SECURITY';

export type OccupancyStatus = 'VACANT' | 'OWNER_OCCUPIED' | 'TENANT_OCCUPIED' | 'UNDER_MAINTENANCE';

export type BillStatus = 'GENERATED' | 'PENDING' | 'PARTIALLY_PAID' | 'PAID' | 'OVERDUE' | 'CANCELLED';

export type PaymentMethod = 'CASH' | 'UPI' | 'BANK_TRANSFER' | 'CARD' | 'OTHER' | 'ONLINE';

export type PaymentStatus = 'PENDING' | 'SUCCESS' | 'FAILED' | 'REFUNDED';

export type ComplaintCategory = 
  | 'PLUMBING' 
  | 'ELECTRICAL' 
  | 'SECURITY' 
  | 'CLEANING' 
  | 'LIFT' 
  | 'WATER' 
  | 'PARKING' 
  | 'NOISE' 
  | 'MAINTENANCE' 
  | 'OTHER';

export type ComplaintPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL' | 'URGENT';

export type ComplaintStatus = 'OPEN' | 'ASSIGNED' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED' | 'REJECTED';

export type ExpenseCategory = 
  | 'ELECTRICITY' 
  | 'WATER' 
  | 'SECURITY' 
  | 'CLEANING' 
  | 'REPAIRS' 
  | 'LIFT_MAINTENANCE' 
  | 'GARDENING' 
  | 'STAFF_SALARY' 
  | 'INSURANCE' 
  | 'EVENTS' 
  | 'OTHER';

export type NoticePriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'EMERGENCY' | 'URGENT';

export type AudienceType = 'ALL' | 'RESIDENTS' | 'OWNERS' | 'TENANTS';

export type VisitorStatus = 'EXPECTED' | 'CHECKED_IN' | 'CHECKED_OUT' | 'CANCELLED' | 'INSIDE';

export type StaffRole = 'SECURITY' | 'CLEANER' | 'ELECTRICIAN' | 'PLUMBER' | 'GARDENER' | 'MAINTENANCE';

export type NotificationType = 'BILL' | 'PAYMENT' | 'COMPLAINT' | 'NOTICE' | 'VISITOR' | 'GENERAL';

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
}

export interface User {
  id: string;
  username: string;
  email: string;
  fullName: string;
  phoneNumber?: string;
  phone?: string;
  active: boolean;
  roles: RoleType[];
  residentId?: string;
  flatId?: string;
  flatNumber?: string;
}

export interface UserInfo {
  id: string;
  username: string;
  email: string;
  fullName: string;
  phone?: string;
  phoneNumber?: string;
  roles: RoleType[];
  residentId?: string;
  flatId?: string;
  flatNumber?: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  userId: string;
  username: string;
  email: string;
  fullName: string;
  roles: RoleType[];
  residentId?: string;
  flatId?: string;
  flatNumber?: string;
  success?: boolean;
  message?: string;
  data?: {
    token: string;
    user: User;
  };
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  user: UserInfo;
}

export interface Building {
  id: string;
  name: string;
  code: string;
  totalFloors: number;
  totalFlats: number;
  description?: string;
  createdAt?: string;
}

export interface Flat {
  id: string;
  buildingId: string;
  buildingName?: string;
  buildingCode?: string;
  flatNumber: string;
  floorNumber: number;
  flatType: string;
  bhkType?: string;
  areaSqft: number;
  squareFeet?: number;
  occupancyStatus: OccupancyStatus;
  ownerName?: string;
  ownerPhone?: string;
  ownerEmail?: string;
  parkingSlot?: string;
  currentResidentName?: string;
  currentResidentPhone?: string;
}

export interface FamilyMember {
  id?: string;
  residentId?: string;
  fullName: string;
  relation: string;
  age?: number;
  phoneNumber?: string;
}

export interface Vehicle {
  id?: string;
  residentId?: string;
  flatId?: string;
  flatNumber?: string;
  vehicleNumber: string;
  vehicleType: string;
  parkingSlot?: string;
}

export interface Resident {
  id: string;
  userId?: string;
  username?: string;
  flatId: string;
  flatNumber?: string;
  buildingName?: string;
  firstName: string;
  lastName: string;
  fullName: string;
  email: string;
  phone: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string;
  moveInDate: string;
  moveOutDate?: string;
  isOwner: boolean;
  residentType?: 'OWNER' | 'TENANT';
  active: boolean;
  familyMembers?: FamilyMember[];
  vehicles?: Vehicle[];
}

export interface MaintenanceBill {
  id: string;
  flatId: string;
  flatNumber?: string;
  buildingName?: string;
  residentName?: string;
  billNumber: string;
  billingPeriod: string;
  billingMonth?: string;
  billingYear?: number;
  billDate: string;
  dueDate: string;
  baseAmount: number;
  maintenanceAmount?: number;
  parkingCharges: number;
  waterCharges: number;
  lateFee: number;
  lateFeeAmount?: number;
  otherCharges: number;
  discount: number;
  taxAmount: number;
  totalAmount: number;
  netPayableAmount?: number;
  paidAmount: number;
  outstandingAmount: number;
  status: BillStatus;
  notes?: string;
}

export interface Payment {
  id: string;
  billId: string;
  billNumber?: string;
  billingPeriod?: string;
  residentId?: string;
  residentName?: string;
  flatNumber?: string;
  receiptNumber: string;
  amount: number;
  amountPaid?: number;
  paymentDate: string;
  paymentMethod: PaymentMethod;
  transactionId?: string;
  transactionReference?: string;
  status: PaymentStatus;
  notes?: string;
  createdByName?: string;
}

export interface ComplaintComment {
  id: string;
  complaintId: string;
  userId: string;
  userName: string;
  userFullName?: string;
  userRole: string;
  comment: string;
  createdAt: string;
}

export interface Complaint {
  id: string;
  ticketNumber?: string;
  residentId: string;
  residentName?: string;
  residentPhone?: string;
  flatId: string;
  flatNumber?: string;
  buildingName?: string;
  assignedStaffId?: string;
  assignedStaffName?: string;
  title: string;
  description: string;
  category: ComplaintCategory;
  priority: ComplaintPriority;
  status: ComplaintStatus;
  resolution?: string;
  resolutionNotes?: string;
  closedDate?: string;
  createdAt: string;
  updatedAt?: string;
  comments?: ComplaintComment[];
}

export interface SocietyExpense {
  id: string;
  title?: string;
  category: ExpenseCategory;
  amount: number;
  expenseDate: string;
  vendorName?: string;
  description?: string;
  paymentMethod: PaymentMethod;
  invoiceNumber?: string;
  attachmentPath?: string;
  approvalStatus?: 'APPROVED' | 'PENDING' | 'REJECTED';
  createdByName?: string;
  createdAt?: string;
}

export interface Notice {
  id: string;
  title: string;
  content: string;
  priority: NoticePriority;
  audience: AudienceType;
  targetAudience?: AudienceType;
  publishDate: string;
  postedAt?: string;
  expiryDate?: string;
  attachmentPath?: string;
  createdByName?: string;
  postedByName?: string;
  createdAt?: string;
  active: boolean;
}

export interface Visitor {
  id: string;
  flatId: string;
  flatNumber?: string;
  buildingName?: string;
  residentId?: string;
  residentName?: string;
  visitorName: string;
  phoneNumber: string;
  phone?: string;
  purpose: string;
  expectedArrival: string;
  expectedDeparture?: string;
  actualArrival?: string;
  actualDeparture?: string;
  checkInTime?: string;
  checkOutTime?: string;
  badgeNumber?: string;
  vehicleNumber?: string;
  status: VisitorStatus;
  checkedInByName?: string;
  checkedOutByName?: string;
}

export interface Staff {
  id: string;
  userId?: string;
  fullName: string;
  phone: string;
  role: StaffRole;
  joiningDate: string;
  salary?: number;
  active: boolean;
  emergencyContact?: string;
  shiftTiming?: string;
  agencyName?: string;
}

export interface AuditLog {
  id: string;
  userName: string;
  username?: string;
  userRole: string;
  action: string;
  entityType: string;
  entityName?: string;
  entityId?: string;
  details?: string;
  ipAddress?: string;
  previousValue?: string;
  newValue?: string;
  createdAt: string;
  timestamp?: string;
}

export interface NotificationItem {
  id: string;
  title: string;
  message: string;
  type: NotificationType;
  read: boolean;
  referenceId?: string;
  createdAt: string;
}

export type AppNotification = NotificationItem;

export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface MonthlyFinancialChart {
  month: string;
  collection: number;
  expenses: number;
  totalBilled?: number;
  totalCollected?: number;
}

export interface AdminDashboardData {
  totalFlats: number;
  totalFlatsCount?: number;
  occupiedFlats: number;
  occupiedFlatsCount?: number;
  vacantFlats: number;
  underMaintenanceFlats: number;
  occupancyRatePercentage?: number;
  totalResidents: number;
  totalResidentsCount?: number;
  pendingMaintenanceAmount: number;
  totalPendingAmount?: number;
  collectionThisMonth: number;
  totalCollectedThisMonth?: number;
  expensesThisMonth: number;
  activeStaffCount?: number;
  todayVisitorsCount?: number;
  openComplaintsCount: number;
  inProgressComplaintsCount: number;
  resolvedComplaintsCount: number;
  recentPayments: Payment[];
  recentComplaints: Complaint[];
  monthlyFinancials: MonthlyFinancialChart[];
  complaintsByCategory: Record<string, number>;
  complaintsByStatus: Record<string, number>;
  occupancyStats: Record<string, number>;
}

export type DashboardSummary = AdminDashboardData;
export type ResidentDashboardSummary = ResidentDashboardData;

export interface ResidentDashboardData {
  flatDetails?: Flat;
  flat?: Flat;
  currentBill?: MaintenanceBill;
  totalOutstandingAmount: number;
  totalUnpaidDues?: number;
  unpaidBills?: MaintenanceBill[];
  recentBills: MaintenanceBill[];
  myComplaints: Complaint[];
  myActiveComplaintsCount?: number;
  latestNotices: Notice[];
  upcomingVisitors: Visitor[];
  expectedVisitorsTodayCount?: number;
}
