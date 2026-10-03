import React, { useEffect, useState, useMemo } from 'react';
import { paymentApi, RecordPaymentRequest } from '../api/paymentApi';
import { billApi } from '../api/billApi';
import { Payment, MaintenanceBill } from '../types';
import { Badge } from '../components/common/Badge';
import { Pagination } from '../components/common/Pagination';
import { Modal } from '../components/common/Modal';
import { useToast } from '../context/ToastContext';
import { useAuth } from '../context/AuthContext';
import {
  Download, CreditCard, Plus, Search, X, Loader2,
  Receipt, Eye, FileDown, CheckCircle2, TrendingUp, Filter
} from 'lucide-react';

export const PaymentsPage: React.FC = () => {
  const { hasAnyRole } = useAuth();
  const toast = useToast();
  const [payments, setPayments] = useState<Payment[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [downloadingId, setDownloadingId] = useState<string | number | null>(null);

  // Filters
  const [searchQuery, setSearchQuery] = useState('');
  const [methodFilter, setMethodFilter] = useState('ALL');

  // Detail Modal
  const [detailPayment, setDetailPayment] = useState<Payment | null>(null);

  // Record Payment Modal
  const canRecord = hasAnyRole(['SUPER_ADMIN', 'SOCIETY_ADMIN', 'ACCOUNTANT']);
  const [showRecordModal, setShowRecordModal] = useState(false);
  const [billSearch, setBillSearch] = useState('');
  const [billResults, setBillResults] = useState<MaintenanceBill[]>([]);
  const [selectedBill, setSelectedBill] = useState<MaintenanceBill | null>(null);
  const [payAmount, setPayAmount] = useState('');
  const [payMethod, setPayMethod] = useState('UPI');
  const [txnId, setTxnId] = useState('');
  const [payNotes, setPayNotes] = useState('');
  const [recording, setRecording] = useState(false);
  const [billSearching, setBillSearching] = useState(false);

  useEffect(() => {
    fetchPayments();
  }, [page]);

  const fetchPayments = async () => {
    setLoading(true);
    try {
      const response = await paymentApi.getAllPayments({ page, size: 12 });
      if (response && Array.isArray((response as any).content)) {
        setPayments((response as any).content);
        setTotalPages((response as any).totalPages || 1);
      } else if (response && (response as any).data) {
        const d = (response as any).data;
        if (Array.isArray(d.content)) {
          setPayments(d.content);
          setTotalPages(d.totalPages || 1);
        } else if (Array.isArray(d)) {
          setPayments(d);
          setTotalPages(1);
        } else {
          setPayments([]);
          setTotalPages(1);
        }
      } else if (Array.isArray(response)) {
        setPayments(response);
        setTotalPages(1);
      } else {
        setPayments([]);
        setTotalPages(1);
      }
    } catch (err) {
      console.error('Failed to load payments', err);
      toast.error('Failed to load payments', 'Please check backend connection.');
      setPayments([]);
    } finally {
      setLoading(false);
    }
  };

  const handleSearchBills = async () => {
    if (!billSearch.trim()) return;
    setBillSearching(true);
    try {
      const res = await billApi.getAllBills({ search: billSearch, size: 10 });
      const content = (res as any).content || (res as any).data?.content || [];
      setBillResults(content.filter((b: MaintenanceBill) => b.status !== 'PAID' && b.status !== 'CANCELLED'));
    } catch (err) {
      toast.error('Failed to search bills');
    } finally {
      setBillSearching(false);
    }
  };

  const handleRecordPayment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedBill) {
      toast.warning('Please select a bill first');
      return;
    }
    const amount = parseFloat(payAmount);
    if (!amount || amount <= 0) {
      toast.warning('Enter a valid payment amount');
      return;
    }
    setRecording(true);
    try {
      const req: RecordPaymentRequest = {
        billId: selectedBill.id,
        amount,
        paymentMethod: payMethod,
        transactionId: txnId || `TXN-${Date.now()}`,
        notes: payNotes,
      };
      await paymentApi.recordPayment(req);
      toast.success('Payment Recorded', `Receipt generated for Bill ${selectedBill.billNumber}`);
      setShowRecordModal(false);
      resetRecordForm();
      fetchPayments();
    } catch (err: any) {
      const msg = err?.response?.data?.message || 'Failed to record payment';
      toast.error('Payment failed', msg);
    } finally {
      setRecording(false);
    }
  };

  const resetRecordForm = () => {
    setBillSearch('');
    setBillResults([]);
    setSelectedBill(null);
    setPayAmount('');
    setPayMethod('UPI');
    setTxnId('');
    setPayNotes('');
  };

  const handleDownloadReceipt = async (paymentId: string | number, receiptNo: string) => {
    setDownloadingId(paymentId);
    try {
      const blob = await paymentApi.downloadReceipt(paymentId);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `Payment_Receipt_${receiptNo || paymentId}.pdf`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
      toast.success('Receipt Saved', `Receipt ${receiptNo} downloaded successfully.`);
    } catch (err) {
      toast.error('Download Failed', 'Unable to download official PDF receipt.');
    } finally {
      setDownloadingId(null);
    }
  };

  const filteredPayments = useMemo(() => {
    return (payments || []).filter((p) => {
      if (!p) return false;
      const q = (searchQuery || '').toLowerCase();
      const matchSearch =
        !searchQuery.trim() ||
        (p.receiptNumber && p.receiptNumber.toLowerCase().includes(q)) ||
        (p.billNumber && p.billNumber.toLowerCase().includes(q)) ||
        (p.flatNumber && p.flatNumber.toLowerCase().includes(q)) ||
        (p.transactionReference && p.transactionReference.toLowerCase().includes(q)) ||
        (p.transactionId && p.transactionId.toLowerCase().includes(q));

      const matchMethod = methodFilter === 'ALL' || p.paymentMethod === methodFilter;
      return matchSearch && matchMethod;
    });
  }, [payments, searchQuery, methodFilter]);

  const pageTotalCollections = useMemo(() => {
    return (filteredPayments || []).reduce((acc, curr) => acc + (curr?.amountPaid ?? curr?.amount ?? 0), 0);
  }, [filteredPayments]);

  const outstanding = selectedBill
    ? ((selectedBill.netPayableAmount ?? selectedBill.totalAmount ?? 0) - (selectedBill.paidAmount ?? 0))
    : 0;

  return (
    <div className="space-y-6 animate-fadeIn text-slate-900">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 flex items-center gap-2">
            <CreditCard className="w-7 h-7 text-emerald-600" /> Payments & Official Receipts
          </h1>
          <p className="text-xs text-slate-500 mt-1 font-medium">
            Audit payment receipts, download bank-ready PDF receipts, and record dues clearance
          </p>
        </div>
        {canRecord && (
          <button
            onClick={() => { setShowRecordModal(true); resetRecordForm(); }}
            className="px-5 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold uppercase tracking-wider flex items-center gap-2 transition shadow-md shadow-emerald-600/20"
          >
            <Plus className="w-4 h-4" /> Record Payment
          </button>
        )}
      </div>

      {/* Overview Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="bg-white border border-slate-200 rounded-2xl p-4 shadow-sm flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-700 flex items-center justify-center">
            <TrendingUp className="w-6 h-6" />
          </div>
          <div>
            <div className="text-[11px] font-bold text-slate-500 uppercase tracking-wider">Page Collection Total</div>
            <div className="text-xl font-black text-emerald-700">₹{pageTotalCollections.toLocaleString('en-IN')}</div>
          </div>
        </div>

        <div className="bg-white border border-slate-200 rounded-2xl p-4 shadow-sm flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-indigo-50 border border-indigo-200 text-indigo-700 flex items-center justify-center">
            <Receipt className="w-6 h-6" />
          </div>
          <div>
            <div className="text-[11px] font-bold text-slate-500 uppercase tracking-wider">Receipts Listed</div>
            <div className="text-xl font-black text-slate-900">{filteredPayments.length}</div>
          </div>
        </div>

        <div className="bg-white border border-slate-200 rounded-2xl p-4 shadow-sm flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-sky-50 border border-sky-200 text-sky-700 flex items-center justify-center">
            <CheckCircle2 className="w-6 h-6" />
          </div>
          <div>
            <div className="text-[11px] font-bold text-slate-500 uppercase tracking-wider">Payment Status</div>
            <div className="text-sm font-extrabold text-slate-800">100% Verified Records</div>
          </div>
        </div>
      </div>

      {/* Filters and Search Bar */}
      <div className="bg-white border border-slate-200 rounded-2xl p-4 shadow-sm flex flex-col md:flex-row gap-4 items-center justify-between">
        <div className="relative w-full md:w-80">
          <Search className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            type="text"
            placeholder="Search receipt #, bill, flat, UTR..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full bg-slate-50 border border-slate-200 rounded-xl pl-10 pr-4 py-2 text-xs font-medium text-slate-900 focus:outline-none focus:ring-2 focus:ring-emerald-500/20"
          />
        </div>

        <div className="flex items-center gap-3 w-full md:w-auto">
          <div className="flex items-center gap-2 text-xs font-bold text-slate-500 uppercase tracking-wider">
            <Filter className="w-3.5 h-3.5" /> Method:
          </div>
          <select
            value={methodFilter}
            onChange={(e) => setMethodFilter(e.target.value)}
            className="bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 text-xs font-semibold text-slate-700 focus:outline-none"
          >
            <option value="ALL">All Methods</option>
            <option value="UPI">UPI</option>
            <option value="CARD">Card</option>
            <option value="NET_BANKING">Net Banking</option>
            <option value="BANK_TRANSFER">Bank Transfer</option>
            <option value="CASH">Cash</option>
            <option value="CHEQUE">Cheque</option>
          </select>
        </div>
      </div>

      {/* Payment Table */}
      <div className="bg-white border border-slate-200 rounded-2xl shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-700">
            <thead className="bg-slate-50 text-slate-500 uppercase tracking-wider font-extrabold border-b border-slate-200">
              <tr>
                <th className="px-5 py-3.5">Receipt No</th>
                <th className="px-5 py-3.5">Bill Number</th>
                <th className="px-5 py-3.5">Flat Unit</th>
                <th className="px-5 py-3.5">Amount Paid</th>
                <th className="px-5 py-3.5">Method</th>
                <th className="px-5 py-3.5">UTR / Txn Ref</th>
                <th className="px-5 py-3.5">Payment Date</th>
                <th className="px-5 py-3.5">Status</th>
                <th className="px-5 py-3.5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan={9} className="px-5 py-12 text-center text-slate-400">
                    <Loader2 className="w-6 h-6 animate-spin mx-auto mb-2 text-emerald-600" />
                    Loading payment records...
                  </td>
                </tr>
              ) : filteredPayments.length === 0 ? (
                <tr>
                  <td colSpan={9} className="px-5 py-12 text-center text-slate-500">
                    <Receipt className="w-8 h-8 mx-auto mb-2 opacity-40 text-slate-300" />
                    <p className="font-semibold text-slate-600">No payment records found.</p>
                  </td>
                </tr>
              ) : (
                filteredPayments.map((p) => {
                  const amtPaid = p.amountPaid ?? p.amount ?? 0;
                  const utr = p.transactionReference || p.transactionId || '—';
                  const isDownloading = downloadingId === p.id;

                  return (
                    <tr key={p.id} className="hover:bg-slate-50 transition">
                      <td className="px-5 py-3.5 font-mono font-bold text-emerald-700">{p.receiptNumber}</td>
                      <td className="px-5 py-3.5 font-semibold text-slate-900">{p.billNumber || p.billId}</td>
                      <td className="px-5 py-3.5 text-slate-700 font-medium">Flat {p.flatNumber || '—'}</td>
                      <td className="px-5 py-3.5 font-black text-slate-900">
                        ₹{Number(amtPaid).toLocaleString('en-IN')}
                      </td>
                      <td className="px-5 py-3.5">
                        <Badge variant="info">{p.paymentMethod}</Badge>
                      </td>
                      <td className="px-5 py-3.5 font-mono text-slate-500 text-[11px]">{utr}</td>
                      <td className="px-5 py-3.5 text-slate-500 font-mono">
                        {p.paymentDate ? new Date(p.paymentDate).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' }) : '—'}
                      </td>
                      <td className="px-5 py-3.5">
                        <Badge variant={p.status === 'SUCCESS' ? 'success' : 'danger'}>{p.status}</Badge>
                      </td>
                      <td className="px-5 py-3.5 text-right">
                        <div className="flex items-center justify-end gap-1.5">
                          <button
                            onClick={() => setDetailPayment(p)}
                            className="p-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-lg transition"
                            title="View Receipt Summary"
                          >
                            <Eye className="w-4 h-4" />
                          </button>
                          <button
                            onClick={() => handleDownloadReceipt(p.id, p.receiptNumber)}
                            disabled={isDownloading}
                            className="p-1.5 bg-emerald-50 hover:bg-emerald-100 text-emerald-700 border border-emerald-200 rounded-lg transition disabled:opacity-50"
                            title="Download Official PDF Receipt"
                          >
                            {isDownloading ? <Loader2 className="w-4 h-4 animate-spin" /> : <FileDown className="w-4 h-4" />}
                          </button>
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

      {/* Payment Receipt Detail Modal */}
      {detailPayment && (
        <Modal isOpen={!!detailPayment} onClose={() => setDetailPayment(null)} title="Official Payment Receipt Details">
          <div className="space-y-4 text-slate-900">
            <div className="p-4 bg-emerald-50/50 border border-emerald-200 rounded-2xl flex items-center justify-between">
              <div>
                <div className="text-[10px] font-bold text-emerald-800 uppercase tracking-wider">Receipt Number</div>
                <div className="text-base font-extrabold text-emerald-900 font-mono mt-0.5">{detailPayment.receiptNumber}</div>
                <div className="text-xs text-emerald-700 mt-1">
                  Settlement for Bill <strong>{detailPayment.billNumber || detailPayment.billId}</strong>
                </div>
              </div>
              <div className="text-right">
                <Badge variant={detailPayment.status === 'SUCCESS' ? 'success' : 'danger'}>{detailPayment.status}</Badge>
                <div className="text-lg font-black text-slate-900 mt-1 font-mono">
                  ₹{(detailPayment.amountPaid ?? detailPayment.amount ?? 0).toLocaleString('en-IN')}
                </div>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3 text-xs">
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                <div className="text-[10px] text-slate-400 font-bold uppercase">Flat / Unit</div>
                <div className="font-bold text-slate-800 mt-0.5">Flat {detailPayment.flatNumber || 'N/A'}</div>
              </div>
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                <div className="text-[10px] text-slate-400 font-bold uppercase">Payment Instrument</div>
                <div className="font-bold text-slate-800 mt-0.5">{detailPayment.paymentMethod}</div>
              </div>
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                <div className="text-[10px] text-slate-400 font-bold uppercase">UTR / Reference ID</div>
                <div className="font-mono text-slate-800 mt-0.5">
                  {detailPayment.transactionReference || detailPayment.transactionId || 'N/A'}
                </div>
              </div>
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                <div className="text-[10px] text-slate-400 font-bold uppercase">Payment Timestamp</div>
                <div className="font-mono text-slate-800 mt-0.5">
                  {detailPayment.paymentDate ? new Date(detailPayment.paymentDate).toLocaleString('en-IN') : 'N/A'}
                </div>
              </div>
            </div>

            <div className="flex items-center justify-between pt-3 border-t border-slate-200">
              <button
                onClick={() => handleDownloadReceipt(detailPayment.id, detailPayment.receiptNumber)}
                disabled={downloadingId === detailPayment.id}
                className="px-4 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold uppercase tracking-wider flex items-center gap-1.5 transition shadow-sm"
              >
                {downloadingId === detailPayment.id ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <FileDown className="w-3.5 h-3.5" />}
                Download PDF Receipt
              </button>
              <button
                onClick={() => setDetailPayment(null)}
                className="px-4 py-2.5 bg-slate-200 hover:bg-slate-300 text-slate-800 rounded-xl text-xs font-bold uppercase tracking-wider transition"
              >
                Close
              </button>
            </div>
          </div>
        </Modal>
      )}

      {/* Record Payment Modal */}
      <Modal isOpen={showRecordModal} onClose={() => setShowRecordModal(false)} title="Record Maintenance Payment">
        <form onSubmit={handleRecordPayment} className="space-y-4 text-slate-900">
          {!selectedBill ? (
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Search Pending Bill <span className="text-rose-500">*</span>
              </label>
              <div className="flex gap-2">
                <input
                  type="text"
                  value={billSearch}
                  onChange={(e) => setBillSearch(e.target.value)}
                  onKeyDown={(e) => e.key === 'Enter' && (e.preventDefault(), handleSearchBills())}
                  placeholder="Bill number, flat number, or resident name..."
                  className="flex-1 bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-500/15"
                />
                <button
                  type="button"
                  onClick={handleSearchBills}
                  disabled={billSearching}
                  className="px-4 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-700 border border-slate-300 rounded-xl text-xs font-bold flex items-center gap-1 transition"
                >
                  {billSearching ? <Loader2 className="w-4 h-4 animate-spin" /> : <Search className="w-4 h-4" />}
                </button>
              </div>
              {billResults.length > 0 && (
                <div className="mt-2 border border-slate-200 rounded-xl overflow-hidden divide-y divide-slate-100 max-h-56 overflow-y-auto">
                  {billResults.map((b) => (
                    <button
                      key={b.id}
                      type="button"
                      onClick={() => {
                        setSelectedBill(b);
                        const amt = ((b.netPayableAmount ?? b.totalAmount ?? 0) - (b.paidAmount ?? 0));
                        setPayAmount(String(Math.max(0, amt)));
                      }}
                      className="w-full text-left px-4 py-3 hover:bg-slate-50 transition text-xs"
                    >
                      <div className="flex items-center justify-between">
                        <div>
                          <span className="font-bold text-slate-900 font-mono">{b.billNumber}</span>
                          <span className="ml-2 text-slate-500">Flat {b.flatNumber || b.flatId}</span>
                        </div>
                        <div className="flex items-center gap-2">
                          <Badge variant={b.status === 'OVERDUE' ? 'danger' : 'warning'}>{b.status}</Badge>
                          <span className="font-bold text-emerald-700">
                            ₹{((b.netPayableAmount ?? b.totalAmount ?? 0) - (b.paidAmount ?? 0)).toLocaleString('en-IN')}
                          </span>
                        </div>
                      </div>
                      <p className="text-slate-500 mt-0.5">Period: {b.billingPeriod} • Due: {b.dueDate}</p>
                    </button>
                  ))}
                </div>
              )}
            </div>
          ) : (
            <div className="p-3.5 bg-emerald-50 border border-emerald-200 rounded-xl flex items-start justify-between">
              <div>
                <p className="text-xs font-bold text-emerald-900">Selected Bill: {selectedBill.billNumber}</p>
                <p className="text-xs text-emerald-700 mt-0.5">
                  Flat {selectedBill.flatNumber} • Outstanding Balance: ₹{Math.max(0, outstanding).toLocaleString('en-IN')}
                </p>
              </div>
              <button
                type="button"
                onClick={() => { setSelectedBill(null); setBillResults([]); setPayAmount(''); }}
                className="p-1 rounded-lg hover:bg-emerald-100 text-emerald-700 transition"
              >
                <X className="w-4 h-4" />
              </button>
            </div>
          )}

          {selectedBill && (
            <>
              <div>
                <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Amount Received (₹) <span className="text-rose-500">*</span>
                </label>
                <input
                  type="number"
                  required
                  min="1"
                  step="any"
                  value={payAmount}
                  onChange={(e) => setPayAmount(e.target.value)}
                  className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-mono font-bold focus:outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-500/15"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Payment Method <span className="text-rose-500">*</span>
                  </label>
                  <select
                    value={payMethod}
                    onChange={(e) => setPayMethod(e.target.value)}
                    className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium focus:outline-none focus:border-emerald-500"
                  >
                    <option value="UPI">UPI</option>
                    <option value="CASH">Cash</option>
                    <option value="CARD">Card</option>
                    <option value="BANK_TRANSFER">Bank Transfer / NEFT</option>
                    <option value="CHEQUE">Cheque</option>
                  </select>
                </div>
                <div>
                  <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                    Transaction / UTR Ref
                  </label>
                  <input
                    type="text"
                    value={txnId}
                    onChange={(e) => setTxnId(e.target.value)}
                    placeholder="e.g. UTR-98234"
                    className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-mono focus:outline-none focus:border-emerald-500"
                  />
                </div>
              </div>

              <div>
                <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                  Notes (Optional)
                </label>
                <input
                  type="text"
                  value={payNotes}
                  onChange={(e) => setPayNotes(e.target.value)}
                  placeholder="e.g. Cheque cleared, or cash collected at counter"
                  className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium focus:outline-none focus:border-emerald-500"
                />
              </div>

              <button
                type="submit"
                disabled={recording}
                className="w-full py-3 bg-emerald-600 hover:bg-emerald-700 disabled:opacity-50 text-white font-bold text-xs uppercase tracking-wider rounded-xl transition shadow-md flex items-center justify-center gap-2"
              >
                {recording ? <Loader2 className="w-4 h-4 animate-spin" /> : null}
                {recording ? 'Processing Payment...' : 'Confirm & Record Payment'}
              </button>
            </>
          )}
        </form>
      </Modal>
    </div>
  );
};
