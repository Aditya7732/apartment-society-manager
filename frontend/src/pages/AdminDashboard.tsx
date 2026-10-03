import React, { useEffect, useState } from 'react';
import { dashboardApi } from '../api/dashboardApi';
import { DashboardSummary } from '../types';
import { StatCard } from '../components/common/StatCard';
import {
  Building2,
  Users,
  Receipt,
  DollarSign,
  AlertTriangle,
  TrendingUp,
  FileSpreadsheet,
  ShieldAlert,
} from 'lucide-react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  Legend,
} from 'recharts';
import { Link } from 'react-router-dom';

export const AdminDashboard: React.FC = () => {
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboard();
  }, []);

  const fetchDashboard = async () => {
    try {
      const response = await dashboardApi.getAdminSummary();
      if (response && response.data) {
        setSummary(response.data);
      } else if (response) {
        setSummary(response as any);
      }
    } catch (err) {
      console.error('Failed to load dashboard summary', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <div className="w-10 h-10 border-4 border-emerald-200 border-t-emerald-600 rounded-full animate-spin" />
      </div>
    );
  }

  const COLORS = ['#ef4444', '#f59e0b', '#10b981', '#3b82f6'];

  const complaintPieData = summary
    ? [
        { name: 'Pending', value: summary.openComplaintsCount || 0 },
        { name: 'In Progress', value: summary.inProgressComplaintsCount || 0 },
        { name: 'Resolved', value: summary.resolvedComplaintsCount || 0 },
      ]
    : [];

  const occupiedCount = summary?.occupiedFlatsCount ?? summary?.occupiedFlats ?? 0;
  const totalCount = summary?.totalFlatsCount ?? summary?.totalFlats ?? 0;
  const occupancyRate =
    summary?.occupancyRatePercentage ??
    (totalCount > 0 ? Math.round((occupiedCount / totalCount) * 100) : 0);
  const collectedThisMonth = summary?.totalCollectedThisMonth ?? summary?.collectionThisMonth ?? 0;
  const pendingAmount = summary?.totalPendingAmount ?? summary?.pendingMaintenanceAmount ?? 0;
  const residentsCount = summary?.totalResidentsCount ?? summary?.totalResidents ?? 0;

  return (
    <div className="space-y-8 animate-fadeIn">
      {/* Executive Light Banner */}
      <div className="p-6 sm:p-8 rounded-3xl bg-white border border-slate-200 shadow-sm relative overflow-hidden flex flex-col md:flex-row md:items-center justify-between gap-6">
        <div className="relative z-10">
          <div className="flex items-center gap-2 mb-2">
            <span className="w-2 h-2 rounded-full bg-emerald-500 animate-ping" />
            <span className="text-[11px] font-extrabold uppercase tracking-widest text-emerald-700 font-mono">
              Live Operations Dashboard
            </span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-black text-slate-900 tracking-tight">
            Society Executive Command Center
          </h1>
          <p className="text-xs text-slate-500 mt-1 max-w-xl leading-relaxed">
            Real-time telemetry, maintenance dues collections, gate visitor logs, and resident ticketing SLA tracking.
          </p>
        </div>

        {/* Action Shortcuts */}
        <div className="relative z-10 flex flex-wrap items-center gap-3">
          <Link
            to="/bills"
            className="px-4 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-bold rounded-xl text-xs flex items-center gap-2 shadow-md shadow-emerald-600/20 transition"
          >
            <Receipt className="w-4 h-4" /> Batch Billing
          </Link>
          <Link
            to="/expenses"
            className="px-4 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-800 border border-slate-300 rounded-xl text-xs font-bold flex items-center gap-2 transition"
          >
            <DollarSign className="w-4 h-4 text-amber-600" /> Log Expense
          </Link>
          <Link
            to="/reports"
            className="px-4 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-800 border border-slate-300 rounded-xl text-xs font-bold flex items-center gap-2 transition"
          >
            <FileSpreadsheet className="w-4 h-4 text-sky-600" /> Audit CSV
          </Link>
        </div>
      </div>

      {/* Primary Metrics Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <StatCard
          title="Occupancy Capacity"
          value={`${occupiedCount} / ${totalCount}`}
          subtitle={`${occupancyRate}% Occupants Capacity`}
          icon={Building2}
          color="purple"
        />
        <StatCard
          title="Monthly Dues Collected"
          value={`₹${collectedThisMonth.toLocaleString('en-IN')}`}
          subtitle={`Pending Balance: ₹${pendingAmount.toLocaleString('en-IN')}`}
          icon={DollarSign}
          color="emerald"
        />
        <StatCard
          title="Registered Residents"
          value={residentsCount}
          subtitle="Flat Owners & Tenants"
          icon={Users}
          color="sky"
        />
        <StatCard
          title="Open Helpdesk Tickets"
          value={summary?.openComplaintsCount || 0}
          subtitle={`${summary?.inProgressComplaintsCount || 0} Tickets In Progress`}
          icon={AlertTriangle}
          color="rose"
        />
      </div>

      {/* Financial & Complaint Visualizations */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Collection Trend Chart */}
        <div className="lg:col-span-2 bg-white border border-slate-200 rounded-3xl p-6 shadow-sm">
          <div className="flex items-center justify-between mb-6">
            <div>
              <div className="flex items-center gap-2">
                <TrendingUp className="w-5 h-5 text-emerald-600" />
                <h3 className="text-base font-bold text-slate-900">Financial Revenue & Collection Trend</h3>
              </div>
              <p className="text-xs text-slate-500 mt-0.5">6-month total maintenance billing vs collections</p>
            </div>
          </div>

          <div className="h-72 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart
                data={summary?.monthlyFinancials || []}
                margin={{ top: 10, right: 10, left: -10, bottom: 0 }}
              >
                <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" opacity={0.8} />
                <XAxis dataKey="month" stroke="#64748b" fontSize={11} fontStyle="bold" />
                <YAxis
                  stroke="#64748b"
                  fontSize={11}
                  tickFormatter={(val) => `₹${val / 1000}k`}
                />
                <Tooltip
                  contentStyle={{
                    backgroundColor: '#ffffff',
                    borderColor: '#cbd5e1',
                    borderRadius: '1rem',
                    boxShadow: '0 10px 15px -3px rgba(0,0,0,0.1)',
                    color: '#0f172a',
                  }}
                  formatter={(val: any) => [`₹${Number(val).toLocaleString('en-IN')}`, 'Amount']}
                />
                <Bar dataKey="totalBilled" name="Billed Target" fill="#6366f1" radius={[6, 6, 0, 0]} />
                <Bar dataKey="collection" name="Actual Collection" fill="#10b981" radius={[6, 6, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Complaints Breakdown Pie Chart */}
        <div className="bg-white border border-slate-200 rounded-3xl p-6 shadow-sm flex flex-col justify-between">
          <div>
            <div className="flex items-center gap-2">
              <ShieldAlert className="w-5 h-5 text-amber-600" />
              <h3 className="text-base font-bold text-slate-900">Helpdesk Resolution SLA</h3>
            </div>
            <p className="text-xs text-slate-500 mt-0.5">Status distribution of resident complaints</p>
          </div>

          <div className="h-56 w-full my-2">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={complaintPieData}
                  cx="50%"
                  cy="50%"
                  innerRadius={55}
                  outerRadius={80}
                  paddingAngle={6}
                  dataKey="value"
                >
                  {complaintPieData.map((_entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip
                  contentStyle={{
                    backgroundColor: '#ffffff',
                    borderColor: '#cbd5e1',
                    borderRadius: '1rem',
                  }}
                />
                <Legend
                  verticalAlign="bottom"
                  height={36}
                  wrapperStyle={{ color: '#475569', fontSize: '11px', fontWeight: 'bold' }}
                />
              </PieChart>
            </ResponsiveContainer>
          </div>

          <div className="pt-4 border-t border-slate-100 flex items-center justify-between text-xs text-slate-500">
            <span>
              Active Security Staff:{' '}
              <strong className="text-emerald-700 font-mono">
                {summary?.activeStaffCount || 0}
              </strong>
            </span>
            <span>
              Today's Gate Entries:{' '}
              <strong className="text-sky-700 font-mono">
                {summary?.todayVisitorsCount || 0}
              </strong>
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};
