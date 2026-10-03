import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

interface ProtectedRouteProps {
  allowedRoles?: string[];
  children?: React.ReactNode;
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ allowedRoles, children }) => {
  const { isAuthenticated, user, hasAnyRole } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && allowedRoles.length > 0 && !hasAnyRole(allowedRoles)) {
    if (user?.roles.some(r => r === 'ROLE_RESIDENT' || (r as string) === 'RESIDENT')) {
      return <Navigate to="/resident/dashboard" replace />;
    }
    if (user?.roles.some(r => r === 'ROLE_SECURITY' || (r as string) === 'SECURITY')) {
      return <Navigate to="/security/dashboard" replace />;
    }
    return <Navigate to="/admin/dashboard" replace />;
  }

  return children ? <>{children}</> : <Outlet />;
};
