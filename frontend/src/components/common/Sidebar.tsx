import React, { useState } from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import {
  LayoutDashboard,
  Building2,
  Users,
  Receipt,
  CreditCard,
  AlertTriangle,
  DollarSign,
  Megaphone,
  UserCheck,
  ShieldAlert,
  FileSpreadsheet,
  Activity,
  ChevronRight,
  ShieldCheck,
  Zap,
  Layers,
  ChevronLeft,
} from 'lucide-react';

interface NavItem {
  label: string;
  path: string;
  icon: React.ElementType;
  roles?: string[];
  badge?: string;
  badgeColor?: string;
}

interface NavSection {
  title: string;
  items: NavItem[];
}

export const Sidebar: React.FC = () => {
  const { hasAnyRole } = useAuth();
  const [collapsed, setCollapsed] = useState(false);

  const navSections: NavSection[] = [
    {
      title: 'DASHBOARDS',
      items: [
        {
          label: 'Admin Portal',
          path: '/admin/dashboard',
          icon: LayoutDashboard,
          roles: ['SUPER_ADMIN', 'SOCIETY_ADMIN', 'ACCOUNTANT'],
        },
        {
          label: 'Resident Portal',
          path: '/resident/dashboard',
          icon: LayoutDashboard,
          roles: ['RESIDENT'],
        },
        {
          label: 'Gate Security Desk',
          path: '/security/dashboard',
          icon: ShieldCheck,
          roles: ['SECURITY', 'SUPER_ADMIN', 'SOCIETY_ADMIN'],
          badge: 'Live',
          badgeColor: 'bg-emerald-100 text-emerald-800 border-emerald-300',
        },
      ],
    },
    {
      title: 'PROPERTY & RESIDENTS',
      items: [
        {
          label: 'Buildings & Towers',
          path: '/buildings-flats',
          icon: Building2,
          roles: ['SUPER_ADMIN', 'SOCIETY_ADMIN', 'ACCOUNTANT'],
        },
        {
          label: 'Residents & Occupants',
          path: '/residents',
          icon: Users,
          roles: ['SUPER_ADMIN', 'SOCIETY_ADMIN', 'ACCOUNTANT', 'RESIDENT'],
        },
        {
          label: 'Staff & Security Team',
          path: '/staff',
          icon: ShieldAlert,
          roles: ['SUPER_ADMIN', 'SOCIETY_ADMIN'],
        },
      ],
    },
    {
      title: 'BILLING & FINANCIALS',
      items: [
        {
          label: 'Maintenance Bills',
          path: '/bills',
          icon: Receipt,
          roles: ['SUPER_ADMIN', 'SOCIETY_ADMIN', 'ACCOUNTANT', 'RESIDENT'],
        },
        {
          label: 'Payments & Receipts',
          path: '/payments',
          icon: CreditCard,
          roles: ['SUPER_ADMIN', 'SOCIETY_ADMIN', 'ACCOUNTANT', 'RESIDENT'],
        },
        {
          label: 'Society Expense Ledger',
          path: '/expenses',
          icon: DollarSign,
          roles: ['SUPER_ADMIN', 'SOCIETY_ADMIN', 'ACCOUNTANT'],
        },
      ],
    },
    {
      title: 'OPERATIONS & GATEWAY',
      items: [
        {
          label: 'Helpdesk & SLA Tickets',
          path: '/complaints',
          icon: AlertTriangle,
        },
        {
          label: 'Visitor Gate Pass Log',
          path: '/visitors',
          icon: UserCheck,
        },
        {
          label: 'Announcements Board',
          path: '/notices',
          icon: Megaphone,
        },
      ],
    },
    {
      title: 'REPORTS & AUDIT',
      items: [
        {
          label: 'CSV Reports & Financials',
          path: '/reports',
          icon: FileSpreadsheet,
          roles: ['SUPER_ADMIN', 'SOCIETY_ADMIN', 'ACCOUNTANT'],
        },
        {
          label: 'System Audit Logs',
          path: '/audit-logs',
          icon: Activity,
          roles: ['SUPER_ADMIN'],
        },
      ],
    },
  ];

  return (
    <aside
      className={`bg-white border-r border-slate-200 flex flex-col h-[calc(100vh-4rem)] sticky top-16 z-30 select-none transition-all duration-300 ${
        collapsed ? 'w-20' : 'w-64'
      }`}
    >
      {/* Collapse Header */}
      <div className="p-3 border-b border-slate-100 flex items-center justify-between">
        {!collapsed && (
          <div className="flex items-center gap-2 px-2">
            <Layers className="w-3.5 h-3.5 text-emerald-600" />
            <span className="text-[11px] font-extrabold uppercase tracking-widest text-slate-400">
              Operations Menu
            </span>
          </div>
        )}
        <button
          onClick={() => setCollapsed(!collapsed)}
          className="p-1.5 rounded-lg text-slate-400 hover:text-slate-700 hover:bg-slate-100 transition mx-auto"
          title={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}
        >
          {collapsed ? <ChevronRight className="w-4 h-4" /> : <ChevronLeft className="w-4 h-4" />}
        </button>
      </div>

      {/* Navigation Links */}
      <div className="p-3 space-y-5 overflow-y-auto flex-1">
        {navSections.map((section) => {
          const visibleItems = section.items.filter(
            (item) => !item.roles || hasAnyRole(item.roles)
          );

          if (visibleItems.length === 0) return null;

          return (
            <div key={section.title}>
              {!collapsed && (
                <p className="px-3 text-[10px] font-black uppercase tracking-widest text-slate-400 mb-2">
                  {section.title}
                </p>
              )}
              <div className="space-y-1">
                {visibleItems.map((item) => {
                  const Icon = item.icon;
                  return (
                    <NavLink
                      key={item.path}
                      to={item.path}
                      title={collapsed ? item.label : undefined}
                      className={({ isActive }) =>
                        `flex items-center justify-between px-3 py-2.5 rounded-xl text-xs font-semibold transition-all duration-200 ${
                          isActive
                            ? 'bg-emerald-50 text-emerald-700 border border-emerald-200 shadow-sm font-bold'
                            : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
                        }`
                      }
                    >
                      <div className="flex items-center gap-3">
                        <Icon className="w-4 h-4 flex-shrink-0" />
                        {!collapsed && <span>{item.label}</span>}
                      </div>
                      {!collapsed && (
                        <div className="flex items-center gap-1.5">
                          {item.badge && (
                            <span
                              className={`text-[9px] font-extrabold uppercase px-1.5 py-0.2 rounded border ${item.badgeColor}`}
                            >
                              {item.badge}
                            </span>
                          )}
                          <ChevronRight className="w-3 h-3 opacity-30" />
                        </div>
                      )}
                    </NavLink>
                  );
                })}
              </div>
            </div>
          );
        })}
      </div>

      {/* Footer Info */}
      {!collapsed && (
        <div className="p-3 border-t border-slate-100 bg-slate-50 m-2 rounded-xl border border-slate-200">
          <div className="flex items-center justify-between text-[10px]">
            <div className="flex items-center gap-1.5 text-slate-500 font-medium">
              <Zap className="w-3 h-3 text-emerald-600" />
              <span>Society DB v1.0</span>
            </div>
            <span className="font-bold text-emerald-700 font-mono">ONLINE</span>
          </div>
        </div>
      )}
    </aside>
  );
};
