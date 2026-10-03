import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import { ProtectedRoute } from './components/auth/ProtectedRoute';
import { MainLayout } from './layouts/MainLayout';
import { Login } from './pages/Login';
import { AdminDashboard } from './pages/AdminDashboard';
import { ResidentDashboard } from './pages/ResidentDashboard';
import { SecurityDashboard } from './pages/SecurityDashboard';
import { BuildingsFlatsPage } from './pages/BuildingsFlatsPage';
import { ResidentsPage } from './pages/ResidentsPage';
import { MaintenanceBillsPage } from './pages/MaintenanceBillsPage';
import { PaymentsPage } from './pages/PaymentsPage';
import { ComplaintsPage } from './pages/ComplaintsPage';
import { VisitorsPage } from './pages/VisitorsPage';
import { NoticesPage } from './pages/NoticesPage';
import { StaffPage } from './pages/StaffPage';
import { ExpensesPage } from './pages/ExpensesPage';
import { ReportsPage } from './pages/ReportsPage';
import { AuditLogsPage } from './pages/AuditLogsPage';
import { ProfilePage } from './pages/ProfilePage';

const RootRedirect: React.FC = () => {
  const { user, isAuthenticated } = useAuth();
  if (!isAuthenticated || !user) {
    return <Navigate to="/login" replace />;
  }

  if (user.roles?.includes('ROLE_RESIDENT')) {
    return <Navigate to="/resident/dashboard" replace />;
  }
  if (user.roles?.includes('ROLE_SECURITY')) {
    return <Navigate to="/security/dashboard" replace />;
  }
  return <Navigate to="/admin/dashboard" replace />;
};

export const App: React.FC = () => {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />

      <Route element={<ProtectedRoute />}>
        <Route element={<MainLayout />}>
          <Route path="/" element={<RootRedirect />} />
          <Route path="/admin/dashboard" element={<AdminDashboard />} />
          <Route path="/resident/dashboard" element={<ResidentDashboard />} />
          <Route path="/security/dashboard" element={<SecurityDashboard />} />
          <Route path="/buildings-flats" element={<BuildingsFlatsPage />} />
          <Route path="/residents" element={<ResidentsPage />} />
          <Route path="/bills" element={<MaintenanceBillsPage />} />
          <Route path="/payments" element={<PaymentsPage />} />
          <Route path="/complaints" element={<ComplaintsPage />} />
          <Route path="/visitors" element={<VisitorsPage />} />
          <Route path="/notices" element={<NoticesPage />} />
          <Route path="/staff" element={<StaffPage />} />
          <Route path="/expenses" element={<ExpensesPage />} />
          <Route path="/reports" element={<ReportsPage />} />
          <Route path="/audit-logs" element={<AuditLogsPage />} />
          <Route path="/profile" element={<ProfilePage />} />
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};

export default App;
