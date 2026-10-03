import React, { useEffect, useState, useCallback } from 'react';
import { billApi } from '../api/billApi';
import { paymentApi } from '../api/paymentApi';
import { MaintenanceBill, BillStatus } from '../types';
import { Modal } from '../components/common/Modal';
import { Badge } from '../components/common/Badge';
import { Pagination } from '../components/common/Pagination';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import {
  Sparkles, Receipt, Search, XCircle, Loader2, Filter,
  FileDown, Eye, CreditCard, AlertCircle, CheckCircle2,
  Calendar, Building2, UserCheck
} from 'lucide-react';

const MONTH_NAMES = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December',
];

const BILL_STATUS_OPTIONS: BillStatus[] = ['GENERATED', 'PENDING', 'PARTIALLY_PAID', 'PAID', 'OVERDUE', 'CANCELLED'];

export const MaintenanceBillsPage: React.FC = () => {
  const { hasAnyRole, user } = useAuth();
  const toast = useToast();
  const [bills, setBills] = useState<MaintenanceBill[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  // Filter state
  const [searchText, setSearchText] = useState('');
  const [statusFilter, setStatusFilter] = useState<BillStatus | ''>('');

  // Batch Generation Modal
  const [showBatchModal, setShowBatchModal] = useState(false);
  const [month, setMonth] = useState(new Date().getMonth() + 1);
  const [year, setYear] = useState(new Date().getFullYear());
  const [baseAmount, setBaseAmount] = useState(3500);
  const [waterCharges, setWaterCharges] = useState(300);
  const [parkingCharges, setParkingCharges] = useState(500);
  const [dueDate, setDueDate] = useState('');
  const [generating, setGenerating] = useState(false);

  // Detail Modal
  const [detailBill, setDetailBill] = useState<MaintenanceBill | null>(null);

  // Download PDF Loading State
  const [downloadingId, setDownloadingId] = useState<string | null>(null);

  // Cancel Confirmation Modal
  const [cancelModalBill, setCancelModalBill] = useState<MaintenanceBill | null>(null);
  const [canceling, setCanceling] = useState(false);

  // Quick Payment Modal
  const [payModalBill, setPayModalBill] = useState<MaintenanceBill | null>(null);
  const [payAmount, setPayAmount] = useState<string>('');
  const [payMethod, setPayMethod] = useState<string>('UPI');
  const [payTxnId, setPayTxnId] = useState<string>('');
  const [payNotes, setPayNotes] = useState<string>('');
  const [recordingPay, setRecordingPay] = useState(false);

  const canManageBills = hasAnyRole(['SUPER_ADMIN', 'SOCIETY_ADMIN', 'ACCOUNTANT']);

  const fetchBills = useCallback(async () => {
    setLoading(true);
    try {
      const params: any = { page, size: 15 };
      if (statusFilter) params.status = statusFilter;
      if (searchText.trim()) params.search = searchText.trim();
      const response = await billApi.getAllBills(params);
      if (response && response.content) {
        setBills(response.content);
        setTotalPages(response.totalPages || 1);
      } else if (response && (response as any).data) {
        const d = (response as any).data;
        setBills(d.content || []);
        setTotalPages(d.totalPages || 1);
      }
    } catch (err) {
      toast.error('Failed to load bills', 'Please check backend connection.');
    } finally {
      setLoading(false);
    }
  }, [page, statusFilter, searchText]);

  useEffect(() => {
    const debounce = setTimeout(() => fetchBills(), 300);
    return () => clearTimeout(debounce);
  }, [fetchBills]);

  const handleGenerateBatch = async (e: React.FormEvent) => {
    e.preventDefault();
    setGenerating(true);
    try {
      const results = await billApi.generateBatchBills({
        billingMonth: Number(month),
        billingYear: Number(year),
        baseAmount: Number(baseAmount),
        defaultWaterCharges: Number(waterCharges),
        defaultParkingCharges: Number(parkingCharges),
        dueDate: dueDate || undefined,
      });
      const count = Array.isArray(results) ? results.length : 1;
      toast.success(
        `Batch Bills Generated`,
        `${count} maintenance bill${count !== 1 ? 's' : ''} generated for ${MONTH_NAMES[month - 1]} ${year}.`
      );
      setShowBatchModal(false);
      fetchBills();
    } catch (err: any) {
      const msg = err?.response?.data?.message || 'Failed to generate batch bills';
      toast.error('Generation Failed', msg);
    } finally {
      setGenerating(false);
    }
  };

  const handleDownloadPdf = async (bill: MaintenanceBill) => {
    try {
      setDownloadingId(bill.id);
      const blob = await billApi.downloadPdf(bill.id);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `Maintenance_Bill_${bill.billNumber || bill.id}.pdf`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
      toast.success('Download Complete', `Bill ${bill.billNumber} PDF generated successfully.`);
    } catch (err: any) {
      const msg = err?.response?.data?.message || 'Unable to download maintenance bill PDF.';
      toast.error('Download Failed', msg);
    } finally {
      setDownloadingId(null);
    }
  };

  const confirmCancel = async () => {
    if (!cancelModalBill) return;
    try {
      setCanceling(true);
      await billApi.cancel(cancelModalBill.id);
      toast.success('Bill Cancelled', `Bill ${cancelModalBill.billNumber} has been revoked.`);
      setCancelModalBill(null);
      if (detailBill?.id === cancelModalBill.id) setDetailBill(null);
      fetchBills();
    } catch (err: any) {
      const msg = err?.response?.data?.message || 'Failed to cancel bill';
      toast.error('Cancel Failed', msg);
    } finally {
      setCanceling(false);
    }
  };

  const openPayModal = (bill: MaintenanceBill) => {
    const outstanding = Math.max(0, (bill.netPayableAmount ?? bill.totalAmount ?? 0) - (bill.paidAmount ?? 0));
    setPayModalBill(bill);
    setPayAmount(outstanding.toString());
    setPayTxnId(`TXN-${Date.now().toString().slice(-6)}`);
    setPayNotes(`Payment for ${bill.billingPeriod || 'Maintenance'} bill`);
  };

  const handleRecordPayment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!payModalBill) return;

    const amt = parseFloat(payAmount);
    if (isNaN(amt) || amt <= 0) {
      toast.error('Validation Error', 'Payment amount must be greater than ₹0.');
      return;
    }

    try {
      setRecordingPay(true);
      await paymentApi.recordPayment({
        billId: payModalBill.id,
        amount: amt,
        paymentMethod: payMethod,
        transactionId: payTxnId,
        notes: payNotes,
      });

      toast.success('Payment Recorded', `Successfully recorded ₹${amt.toLocaleString('en-IN')} for bill ${payModalBill.billNumber}.`);
      setPayModalBill(null);
      if (detailBill?.id === payModalBill.id) setDetailBill(null);
      fetchBills();
    } catch (err: any) {
      const msg = err?.response?.data?.message || 'Failed to process payment.';
      toast.error('Payment Failed', msg);
    } finally {
      setRecordingPay(false);
    }
  };

  const getBadgeVariant = (status: BillStatus) => {
    if (status === 'PAID') return 'success';
    if (status === 'OVERDUE') return 'danger';
    if (status === 'CANCELLED') return 'secondary';
    if (status === 'PARTIALLY_PAID') return 'info';
    return 'warning';
  };

  return (
    <div className="space-y-6 animate-fadeIn text-slate-900">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 flex items-center gap-2">
            <Receipt className="w-7 h-7 text-indigo-600" /> Maintenance Invoices & Ledger
          </h1>
          <p className="text-xs text-slate-500 mt-1 font-medium">
            Generate itemized billing cycles, track dues, download official invoices, and reconcile payments
          </p>
        </div>
        {canManageBills && (
          <button
            onClick={() => setShowBatchModal(true)}
            className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-bold uppercase tracking-wider flex items-center gap-2 transition shadow-md shadow-indigo-600/20"
          >
            <Sparkles className="w-4 h-4" /> Generate Monthly Bills
          </button>
        )}
      </div>

      {/* Filters and Search Bar */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center gap-3">
        <div className="relative flex-1 w-full">
          <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
          <input
            type="text"
            value={searchText}
            onChange={(e) => { setSearchText(e.target.value); setPage(0); }}
            placeholder="Search by bill number, flat, or resident name..."
            className="w-full pl-10 pr-4 py-2.5 bg-white border border-slate-200 rounded-xl text-xs text-slate-900 placeholder-slate-400 focus:outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/15"
          />
        </div>
        <div className="flex items-center gap-2 w-full sm:w-auto">
          <Filter className="w-4 h-4 text-slate-400" />
          <select
            value={statusFilter}
            onChange={(e) => { setStatusFilter(e.target.value as BillStatus | ''); setPage(0); }}
            className="bg-white border border-slate-200 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-semibold focus:outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/15"
          >
            <option value="">All Statuses</option>
            {BILL_STATUS_OPTIONS.map((s) => (
              <option key={s} value={s}>{s.replace('_', ' ')}</option>
            ))}
          </select>
          {(searchText || statusFilter) && (
            <button
              onClick={() => { setSearchText(''); setStatusFilter(''); setPage(0); }}
              className="p-2 text-slate-500 hover:text-slate-700 hover:bg-slate-100 rounded-xl transition"
              title="Clear filters"
            >
              <XCircle className="w-4 h-4" />
            </button>
          )}
        </div>
      </div>

      {/* Bill Table */}
      <div className="bg-white border border-slate-200 rounded-2xl shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-700">
            <thead className="bg-slate-50 text-slate-500 uppercase tracking-wider font-extrabold border-b border-slate-200">
              <tr>
                <th className="px-5 py-3.5">Bill Number</th>
                <th className="px-5 py-3.5">Flat Unit</th>
                <th className="px-5 py-3.5">Resident</th>
                <th className="px-5 py-3.5">Billing Period</th>
                <th className="px-5 py-3.5">Total Dues</th>
                <th className="px-5 py-3.5">Paid</th>
                <th className="px-5 py-3.5">Due Date</th>
                <th className="px-5 py-3.5">Status</th>
                <th className="px-5 py-3.5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan={9} className="px-5 py-12 text-center text-slate-400">
                    <Loader2 className="w-6 h-6 animate-spin mx-auto mb-2 text-indigo-600" />
                    Loading maintenance bills...
                  </td>
                </tr>
              ) : bills.length === 0 ? (
                <tr>
                  <td colSpan={9} className="px-5 py-12 text-center text-slate-400">
                    <Receipt className="w-8 h-8 mx-auto mb-2 opacity-40 text-slate-300" />
                    <p className="font-semibold text-slate-600">No maintenance bills found.</p>
                    {canManageBills && !searchText && !statusFilter && (
                      <p className="text-xs text-slate-400 mt-1">Use "Generate Monthly Bills" to create scheduled bills for all residents.</p>
                    )}
                  </td>
                </tr>
              ) : (
                bills.map((bill) => {
                  const payable = bill.netPayableAmount ?? bill.totalAmount ?? 0;
                  const paid = bill.paidAmount ?? 0;
                  const outstanding = Math.max(0, payable - paid);
                  const isCancellable = bill.status !== 'PAID' && bill.status !== 'CANCELLED';
                  const isPayable = bill.status !== 'PAID' && bill.status !== 'CANCELLED';
                  const isDownloading = downloadingId === bill.id;

                  return (
                    <tr key={bill.id} className="hover:bg-slate-50 transition">
                      <td className="px-5 py-3.5 font-mono font-bold text-slate-900">{bill.billNumber}</td>
                      <td className="px-5 py-3.5 font-semibold text-slate-900">
                        Flat {bill.flatNumber || bill.flatId}
                        {bill.buildingName && <span className="text-slate-500 font-normal"> ({bill.buildingName})</span>}
                      </td>
                      <td className="px-5 py-3.5 text-slate-600 font-medium">{bill.residentName || '—'}</td>
                      <td className="px-5 py-3.5 font-medium">{bill.billingPeriod || 'N/A'}</td>
                      <td className="px-5 py-3.5 font-bold text-slate-900">
                        ₹{Number(payable).toLocaleString('en-IN')}
                      </td>
                      <td className="px-5 py-3.5 font-semibold text-emerald-700">
                        ₹{Number(paid).toLocaleString('en-IN')}
                      </td>
                      <td className="px-5 py-3.5 text-slate-500 font-mono">{bill.dueDate || '—'}</td>
                      <td className="px-5 py-3.5">
                        <Badge variant={getBadgeVariant(bill.status)}>{bill.status.replace('_', ' ')}</Badge>
                      </td>
                      <td className="px-5 py-3.5 text-right">
                        <div className="flex items-center justify-end gap-1.5">
                          {/* View details */}
                          <button
                            onClick={() => setDetailBill(bill)}
                            className="p-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-lg transition"
                            title="View Invoice Details"
                          >
                            <Eye className="w-4 h-4" />
                          </button>

                          {/* Download PDF */}
                          <button
                            onClick={() => handleDownloadPdf(bill)}
                            disabled={isDownloading}
                            className="p-1.5 bg-indigo-50 hover:bg-indigo-100 text-indigo-700 border border-indigo-200 rounded-lg transition disabled:opacity-50"
                            title="Download Official Bill PDF"
                          >
                            {isDownloading ? <Loader2 className="w-4 h-4 animate-spin" /> : <FileDown className="w-4 h-4" />}
                          </button>

                          {/* Quick Pay */}
                          {isPayable && (
                            <button
                              onClick={() => openPayModal(bill)}
                              className="px-2.5 py-1 bg-emerald-50 hover:bg-emerald-100 text-emerald-700 border border-emerald-200 rounded-lg text-xs font-bold transition inline-flex items-center gap-1"
                              title="Record Payment"
                            >
                              <CreditCard className="w-3.5 h-3.5" /> Pay
                            </button>
                          )}

                          {/* Cancel Bill */}
                          {canManageBills && isCancellable && (
                            <button
                              onClick={() => setCancelModalBill(bill)}
                              className="p-1.5 text-rose-600 bg-rose-50 hover:bg-rose-100 border border-rose-200 rounded-lg transition"
                              title="Cancel Bill"
                            >
                              <XCircle className="w-4 h-4" />
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
        <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
      </div>

      {/* Bill Detail Modal */}
      {detailBill && (
        <Modal isOpen={!!detailBill} onClose={() => setDetailBill(null)} title="Maintenance Invoice Statement">
          <div className="space-y-5 text-slate-900">
            {/* Header info */}
            <div className="p-4 bg-slate-50 border border-slate-200 rounded-2xl flex items-center justify-between">
              <div>
                <div className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Invoice Reference</div>
                <div className="text-base font-extrabold text-slate-900 font-mono mt-0.5">{detailBill.billNumber}</div>
                <div className="text-xs text-slate-500 mt-1">
                  Flat <strong>{detailBill.flatNumber}</strong> {detailBill.buildingName && `(${detailBill.buildingName})`} • Resident: <strong>{detailBill.residentName || 'N/A'}</strong>
                </div>
              </div>
              <div className="text-right">
                <Badge variant={getBadgeVariant(detailBill.status)}>{detailBill.status.replace('_', ' ')}</Badge>
                <div className="text-xs text-slate-500 font-mono mt-1.5">Period: {detailBill.billingPeriod}</div>
              </div>
            </div>

            {/* Itemized charges table */}
            <div className="border border-slate-200 rounded-2xl overflow-hidden">
              <div className="bg-slate-50 px-4 py-2 text-[11px] font-extrabold uppercase tracking-wider text-slate-500 border-b border-slate-200">
                Itemized Charges
              </div>
              <div className="divide-y divide-slate-100 text-xs">
                <div className="flex justify-between px-4 py-2.5">
                  <span className="text-slate-600">Base Maintenance Assessment</span>
                  <span className="font-semibold text-slate-900 font-mono">₹{detailBill.baseAmount?.toLocaleString('en-IN') || '0.00'}</span>
                </div>
                <div className="flex justify-between px-4 py-2.5">
                  <span className="text-slate-600">Water Utility Charges</span>
                  <span className="font-semibold text-slate-900 font-mono">₹{detailBill.waterCharges?.toLocaleString('en-IN') || '0.00'}</span>
                </div>
                <div className="flex justify-between px-4 py-2.5">
                  <span className="text-slate-600">Dedicated Parking Assessment</span>
                  <span className="font-semibold text-slate-900 font-mono">₹{detailBill.parkingCharges?.toLocaleString('en-IN') || '0.00'}</span>
                </div>
                {Number(detailBill.otherCharges) > 0 && (
                  <div className="flex justify-between px-4 py-2.5">
                    <span className="text-slate-600">Other Miscellaneous Charges</span>
                    <span className="font-semibold text-slate-900 font-mono">₹{detailBill.otherCharges?.toLocaleString('en-IN')}</span>
                  </div>
                )}
                {Number(detailBill.lateFee) > 0 && (
                  <div className="flex justify-between px-4 py-2.5 bg-rose-50/50">
                    <span className="text-rose-700 font-medium">Late Payment Penalty</span>
                    <span className="font-bold text-rose-700 font-mono">+₹{detailBill.lateFee?.toLocaleString('en-IN')}</span>
                  </div>
                )}
                {Number(detailBill.discount) > 0 && (
                  <div className="flex justify-between px-4 py-2.5 bg-emerald-50/50">
                    <span className="text-emerald-700 font-medium">Early Payment Concession</span>
                    <span className="font-bold text-emerald-700 font-mono">-₹{detailBill.discount?.toLocaleString('en-IN')}</span>
                  </div>
                )}
                <div className="flex justify-between px-4 py-3 bg-slate-50 font-bold">
                  <span className="text-slate-800">Total Net Amount Payable</span>
                  <span className="text-indigo-700 text-sm font-mono">
                    ₹{(detailBill.netPayableAmount ?? detailBill.totalAmount ?? 0).toLocaleString('en-IN')}
                  </span>
                </div>
                <div className="flex justify-between px-4 py-2.5">
                  <span className="text-slate-500 font-medium">Amount Credited / Paid</span>
                  <span className="font-semibold text-emerald-600 font-mono">₹{(detailBill.paidAmount || 0).toLocaleString('en-IN')}</span>
                </div>
                <div className="flex justify-between px-4 py-3 bg-slate-100 font-extrabold text-sm">
                  <span className="text-slate-900">Remaining Balance Due</span>
                  <span className="text-rose-600 font-mono">
                    ₹{Math.max(0, (detailBill.netPayableAmount ?? detailBill.totalAmount ?? 0) - (detailBill.paidAmount || 0)).toLocaleString('en-IN')}
                  </span>
                </div>
              </div>
            </div>

            {/* Dates */}
            <div className="grid grid-cols-2 gap-3 text-xs">
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                <span className="text-[10px] text-slate-400 font-bold uppercase">Bill Generation Date</span>
                <div className="font-mono text-slate-700 mt-0.5">{detailBill.billDate || 'N/A'}</div>
              </div>
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                <span className="text-[10px] text-slate-400 font-bold uppercase">Payment Due Date</span>
                <div className="font-mono text-slate-700 mt-0.5">{detailBill.dueDate || 'N/A'}</div>
              </div>
            </div>

            {/* Actions */}
            <div className="flex items-center justify-between pt-3 border-t border-slate-200">
              <button
                onClick={() => handleDownloadPdf(detailBill)}
                disabled={downloadingId === detailBill.id}
                className="px-4 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-bold uppercase tracking-wider flex items-center gap-1.5 transition shadow-sm"
              >
                {downloadingId === detailBill.id ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <FileDown className="w-3.5 h-3.5" />}
                Download PDF
              </button>

              <div className="flex items-center gap-2">
                {detailBill.status !== 'PAID' && detailBill.status !== 'CANCELLED' && (
                  <button
                    onClick={() => {
                      const b = detailBill;
                      setDetailBill(null);
                      openPayModal(b);
                    }}
                    className="px-4 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold uppercase tracking-wider flex items-center gap-1.5 transition shadow-sm"
                  >
                    <CreditCard className="w-3.5 h-3.5" /> Record Payment
                  </button>
                )}
                <button
                  onClick={() => setDetailBill(null)}
                  className="px-4 py-2.5 bg-slate-200 hover:bg-slate-300 text-slate-800 rounded-xl text-xs font-bold uppercase tracking-wider transition"
                >
                  Close
                </button>
              </div>
            </div>
          </div>
        </Modal>
      )}

      {/* Record Payment Modal */}
      {payModalBill && (
        <Modal isOpen={!!payModalBill} onClose={() => setPayModalBill(null)} title="Record Maintenance Payment">
          <form onSubmit={handleRecordPayment} className="space-y-4 text-slate-900">
            <div className="p-3.5 bg-indigo-50 border border-indigo-200 rounded-xl text-xs">
              <div className="font-bold text-indigo-900">Bill #{payModalBill.billNumber}</div>
              <div className="text-indigo-700 mt-0.5">
                Flat {payModalBill.flatNumber} • Net Due: ₹{(payModalBill.netPayableAmount ?? payModalBill.totalAmount ?? 0).toLocaleString('en-IN')}
              </div>
            </div>

            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Payment Amount (₹) *
              </label>
              <input
                type="number"
                required
                min="1"
                step="any"
                value={payAmount}
                onChange={(e) => setPayAmount(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-mono font-bold"
              />
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Payment Mode *
                </label>
                <select
                  value={payMethod}
                  onChange={(e) => setPayMethod(e.target.value)}
                  className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2.5 text-xs text-slate-900 font-medium"
                >
                  <option value="UPI">UPI / QR</option>
                  <option value="NET_BANKING">Net Banking / NEFT</option>
                  <option value="CARD">Credit / Debit Card</option>
                  <option value="CHEQUE">Bank Cheque</option>
                  <option value="CASH">Cash Collection</option>
                </select>
              </div>
              <div>
                <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Transaction / UTR Reference
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. UTR-98234"
                  value={payTxnId}
                  onChange={(e) => setPayTxnId(e.target.value)}
                  className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-mono"
                />
              </div>
            </div>

            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Receipt Remarks (Optional)
              </label>
              <input
                type="text"
                placeholder="Notes or cheque number..."
                value={payNotes}
                onChange={(e) => setPayNotes(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
              />
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <button
                type="button"
                onClick={() => setPayModalBill(null)}
                className="px-4 py-2.5 bg-slate-200 hover:bg-slate-300 text-slate-700 font-bold text-xs rounded-xl transition"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={recordingPay}
                className="px-6 py-2.5 bg-emerald-600 hover:bg-emerald-700 disabled:opacity-50 text-white font-extrabold text-xs uppercase tracking-wider rounded-xl transition shadow-md flex items-center gap-1.5"
              >
                {recordingPay && <Loader2 className="w-3.5 h-3.5 animate-spin" />}
                {recordingPay ? 'Processing...' : 'Submit Payment'}
              </button>
            </div>
          </form>
        </Modal>
      )}

      {/* Cancel Bill Confirmation Modal */}
      {cancelModalBill && (
        <Modal isOpen={!!cancelModalBill} onClose={() => setCancelModalBill(null)} title="Confirm Bill Revocation">
          <div className="space-y-4 text-slate-900">
            <p className="text-xs text-slate-600">
              Are you sure you want to cancel maintenance bill <strong>{cancelModalBill.billNumber}</strong> for Flat <strong>{cancelModalBill.flatNumber}</strong>?
              This action cannot be undone.
            </p>
            <div className="flex justify-end gap-3 pt-2">
              <button
                onClick={() => setCancelModalBill(null)}
                className="px-4 py-2 bg-slate-200 hover:bg-slate-300 text-slate-700 font-bold text-xs rounded-xl transition"
              >
                Keep Bill
              </button>
              <button
                onClick={confirmCancel}
                disabled={canceling}
                className="px-5 py-2 bg-rose-600 hover:bg-rose-700 disabled:opacity-50 text-white font-bold text-xs rounded-xl transition"
              >
                {canceling ? 'Cancelling...' : 'Cancel Bill'}
              </button>
            </div>
          </div>
        </Modal>
      )}

      {/* Batch Generation Modal */}
      <Modal isOpen={showBatchModal} onClose={() => setShowBatchModal(false)} title="Generate Society Monthly Bills">
        <form onSubmit={handleGenerateBatch} className="space-y-4 text-slate-900">
          <p className="text-xs text-slate-500 bg-indigo-50 border border-indigo-200 rounded-xl px-4 py-3">
            This will generate maintenance bills for <strong>all occupied flats</strong>. Existing bills for the selected billing period will automatically be skipped to prevent duplicate charges.
          </p>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">Billing Month</label>
              <select
                value={month}
                onChange={(e) => setMonth(Number(e.target.value))}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium focus:outline-none focus:border-indigo-500"
              >
                {MONTH_NAMES.map((name, i) => (
                  <option key={i + 1} value={i + 1}>{name}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">Billing Year</label>
              <input
                type="number"
                value={year}
                min={2020}
                max={2030}
                onChange={(e) => setYear(Number(e.target.value))}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium focus:outline-none focus:border-indigo-500"
              />
            </div>
          </div>

          <div className="grid grid-cols-3 gap-3">
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">Base Amount (₹)</label>
              <input
                type="number"
                required
                min={100}
                value={baseAmount}
                onChange={(e) => setBaseAmount(Number(e.target.value))}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-mono focus:outline-none focus:border-indigo-500"
              />
            </div>
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">Water Charges (₹)</label>
              <input
                type="number"
                min={0}
                value={waterCharges}
                onChange={(e) => setWaterCharges(Number(e.target.value))}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-mono focus:outline-none focus:border-indigo-500"
              />
            </div>
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">Parking Charges (₹)</label>
              <input
                type="number"
                min={0}
                value={parkingCharges}
                onChange={(e) => setParkingCharges(Number(e.target.value))}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-mono focus:outline-none focus:border-indigo-500"
              />
            </div>
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
              Bill Due Date (Optional)
            </label>
            <input
              type="date"
              value={dueDate}
              onChange={(e) => setDueDate(e.target.value)}
              min={new Date().toISOString().split('T')[0]}
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium focus:outline-none focus:border-indigo-500"
            />
          </div>

          <button
            type="submit"
            disabled={generating}
            className="w-full py-3 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 text-white font-bold text-xs uppercase tracking-wider rounded-xl transition shadow-md flex items-center justify-center gap-2"
          >
            {generating ? <Loader2 className="w-4 h-4 animate-spin" /> : <Sparkles className="w-4 h-4" />}
            {generating ? 'Generating Bills...' : `Generate Bills for ${MONTH_NAMES[month - 1]} ${year}`}
          </button>
        </form>
      </Modal>
    </div>
  );
};
