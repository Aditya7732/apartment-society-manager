import React, { useState } from 'react';
import { reportApi } from '../api/reportApi';
import { useToast } from '../context/ToastContext';
import {
  FileSpreadsheet, Download, AlertTriangle, DollarSign, Receipt,
  FileText, Loader2, Calendar, Users, FileDown
} from 'lucide-react';

interface ReportCard {
  id: string;
  icon: React.ComponentType<{ className?: string }>;
  iconBg: string;
  iconText: string;
  title: string;
  description: string;
  supportsDate: boolean;
}

const REPORT_CARDS: ReportCard[] = [
  {
    id: 'collection',
    icon: Receipt,
    iconBg: 'bg-indigo-50 border-indigo-200',
    iconText: 'text-indigo-700',
    title: 'Financial Maintenance Collections',
    description: 'Statement of collected maintenance dues, payment modes, UTR transaction references, and society income.',
    supportsDate: true,
  },
  {
    id: 'defaulters',
    icon: AlertTriangle,
    iconBg: 'bg-rose-50 border-rose-200',
    iconText: 'text-rose-700',
    title: 'Overdue Dues & Defaulters List',
    description: 'Summary of flats with overdue maintenance charges, days past due, late penalties, and resident phone contacts.',
    supportsDate: false,
  },
  {
    id: 'residents',
    icon: Users,
    iconBg: 'bg-amber-50 border-amber-200',
    iconText: 'text-amber-700',
    title: 'Resident Occupancy Directory',
    description: 'Roster of all society units, owners vs tenants, flat area sqft, contact phone, and occupancy status.',
    supportsDate: false,
  },
  {
    id: 'expenses',
    icon: DollarSign,
    iconBg: 'bg-emerald-50 border-emerald-200',
    iconText: 'text-emerald-700',
    title: 'Society Expenditure Audit Ledger',
    description: 'Itemized accounting ledger of vendor disbursements, utility payments, repair contracts, and staff payroll.',
    supportsDate: true,
  },
  {
    id: 'complaints',
    icon: FileText,
    iconBg: 'bg-sky-50 border-sky-200',
    iconText: 'text-sky-700',
    title: 'Helpdesk & Ticket SLA Performance',
    description: 'Audit log of resident maintenance tickets, priority tiers, assigned technicians, and resolution turnaround times.',
    supportsDate: true,
  },
];

export const ReportsPage: React.FC = () => {
  const toast = useToast();
  const [loading, setLoading] = useState<Record<string, boolean>>({});

  // Global date range for reports
  const today = new Date();
  const firstOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
  const [startDate, setStartDate] = useState(firstOfMonth.toISOString().split('T')[0]);
  const [endDate, setEndDate] = useState(today.toISOString().split('T')[0]);

  const setOpLoading = (key: string, val: boolean) => {
    setLoading((prev) => ({ ...prev, [key]: val }));
  };

  const downloadFile = (blob: Blob, filename: string) => {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
  };

  const handleExport = async (reportId: string, format: 'pdf' | 'csv') => {
    const opKey = `${reportId}-${format}`;
    setOpLoading(opKey, true);
    const dateStr = `${startDate}_to_${endDate}`;
    const ext = format;

    try {
      let blob: Blob;
      let filename = `Report_${reportId}_${dateStr}.${ext}`;

      if (reportId === 'collection') {
        blob = format === 'pdf'
          ? await reportApi.downloadFinancialPdf(startDate, endDate)
          : await reportApi.downloadFinancialCsv(startDate, endDate);
        filename = `Financial_Collections_${dateStr}.${ext}`;
      } else if (reportId === 'defaulters') {
        blob = format === 'pdf'
          ? await reportApi.downloadDefaultersPdf()
          : await reportApi.downloadDefaultersCsv();
        filename = `Overdue_Defaulters_${Date.now()}.${ext}`;
      } else if (reportId === 'residents') {
        blob = format === 'pdf'
          ? await reportApi.downloadResidentsPdf()
          : await reportApi.downloadResidentsCsv();
        filename = `Resident_Occupancy_Directory_${Date.now()}.${ext}`;
      } else if (reportId === 'expenses') {
        blob = format === 'pdf'
          ? await reportApi.downloadExpensesPdf(startDate, endDate)
          : await reportApi.downloadExpensesCsv(startDate, endDate);
        filename = `Society_Expenditure_${dateStr}.${ext}`;
      } else if (reportId === 'complaints') {
        blob = format === 'pdf'
          ? await reportApi.downloadComplaintsPdf(startDate, endDate)
          : await reportApi.downloadComplaintsCsv(startDate, endDate);
        filename = `Helpdesk_SLA_Report_${dateStr}.${ext}`;
      } else {
        return;
      }

      downloadFile(blob, filename);
      toast.success(
        `${format.toUpperCase()} Downloaded`,
        `${filename} has been saved to your downloads.`
      );
    } catch (err: any) {
      const msg = err?.response?.data?.message || `Failed to export ${reportId} as ${format.toUpperCase()}`;
      toast.error('Export Failed', msg);
    } finally {
      setOpLoading(opKey, false);
    }
  };

  return (
    <div className="space-y-6 animate-fadeIn text-slate-900">
      <div>
        <h1 className="text-2xl font-extrabold text-slate-900 flex items-center gap-2">
          <FileSpreadsheet className="w-7 h-7 text-indigo-600" /> Society Reports & Audit Exports
        </h1>
        <p className="text-xs text-slate-500 mt-1 font-medium">
          Generate official PDF documents and CSV spreadsheets for audit compliance, committee reviews, and financial disclosures
        </p>
      </div>

      {/* Date Range Filter */}
      <div className="bg-white border border-slate-200 rounded-2xl p-4 shadow-sm">
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
          <div className="flex items-center gap-2 text-slate-600">
            <Calendar className="w-4 h-4 text-indigo-600" />
            <span className="text-xs font-bold text-slate-800 uppercase tracking-wider">
              Reporting Interval
            </span>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <div className="flex items-center gap-2">
              <label className="text-xs text-slate-500 font-semibold">Start:</label>
              <input
                type="date"
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
                className="bg-slate-50 border border-slate-200 rounded-xl px-3 py-1.5 text-xs text-slate-900 font-medium focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
              />
            </div>
            <div className="flex items-center gap-2">
              <label className="text-xs text-slate-500 font-semibold">End:</label>
              <input
                type="date"
                value={endDate}
                min={startDate}
                onChange={(e) => setEndDate(e.target.value)}
                className="bg-slate-50 border border-slate-200 rounded-xl px-3 py-1.5 text-xs text-slate-900 font-medium focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
              />
            </div>
          </div>
        </div>
      </div>

      {/* Report Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {REPORT_CARDS.map((card) => {
          const Icon = card.icon;
          const isPdfLoading = loading[`${card.id}-pdf`];
          const isCsvLoading = loading[`${card.id}-csv`];

          return (
            <div
              key={card.id}
              className="bg-white border border-slate-200 rounded-2xl p-5 shadow-sm flex flex-col justify-between hover:shadow-md transition"
            >
              <div>
                <div className="flex items-center justify-between mb-3">
                  <div
                    className={`w-11 h-11 rounded-xl ${card.iconBg} border ${card.iconText} flex items-center justify-center`}
                  >
                    <Icon className="w-5 h-5" />
                  </div>
                  {card.supportsDate ? (
                    <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider bg-slate-100 px-2 py-0.5 rounded-md">
                      Filtered by Date
                    </span>
                  ) : (
                    <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider bg-slate-100 px-2 py-0.5 rounded-md">
                      Current Snapshot
                    </span>
                  )}
                </div>

                <h3 className="text-sm font-extrabold text-slate-900">{card.title}</h3>
                <p className="text-xs text-slate-500 mt-1.5 leading-relaxed">{card.description}</p>
              </div>

              <div className="pt-4 mt-4 border-t border-slate-100 grid grid-cols-2 gap-2">
                <button
                  onClick={() => handleExport(card.id, 'pdf')}
                  disabled={isPdfLoading || isCsvLoading}
                  className="py-2.5 px-3 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 text-white font-bold text-xs uppercase tracking-wider rounded-xl flex items-center justify-center gap-1.5 transition shadow-sm"
                >
                  {isPdfLoading ? (
                    <Loader2 className="w-3.5 h-3.5 animate-spin" />
                  ) : (
                    <FileDown className="w-3.5 h-3.5" />
                  )}
                  {isPdfLoading ? 'Exporting...' : 'PDF Doc'}
                </button>

                <button
                  onClick={() => handleExport(card.id, 'csv')}
                  disabled={isPdfLoading || isCsvLoading}
                  className="py-2.5 px-3 bg-slate-100 hover:bg-slate-200 disabled:opacity-50 text-slate-800 font-bold text-xs uppercase tracking-wider rounded-xl flex items-center justify-center gap-1.5 transition border border-slate-200"
                >
                  {isCsvLoading ? (
                    <Loader2 className="w-3.5 h-3.5 animate-spin" />
                  ) : (
                    <Download className="w-3.5 h-3.5 text-slate-600" />
                  )}
                  {isCsvLoading ? 'Exporting...' : 'CSV Sheet'}
                </button>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
