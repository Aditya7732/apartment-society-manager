import React, { useEffect, useState, useMemo } from 'react';
import { expenseApi } from '../api/expenseApi';
import { SocietyExpense } from '../types';
import { Modal } from '../components/common/Modal';
import { Badge } from '../components/common/Badge';
import { Pagination } from '../components/common/Pagination';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import {
  DollarSign, Plus, Filter, Trash2, Eye, Calendar,
  Receipt, Building2, TrendingDown, FileText, Search
} from 'lucide-react';

const CATEGORIES = [
  'ELECTRICITY', 'WATER', 'SECURITY', 'CLEANING', 'REPAIRS',
  'LIFT_MAINTENANCE', 'GARDENING', 'STAFF_SALARY', 'INSURANCE', 'EVENTS', 'OTHER'
];

export const ExpensesPage: React.FC = () => {
  const { hasAnyRole } = useAuth();
  const toast = useToast();
  const [expenses, setExpenses] = useState<SocietyExpense[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  // Filters
  const [categoryFilter, setCategoryFilter] = useState('ALL');
  const [searchQuery, setSearchQuery] = useState('');

  const canManage = hasAnyRole(['SUPER_ADMIN', 'SOCIETY_ADMIN', 'ACCOUNTANT']);

  // Add Expense Modal
  const [showModal, setShowModal] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [title, setTitle] = useState('');
  const [category, setCategory] = useState('ELECTRICITY');
  const [amount, setAmount] = useState('15000');
  const [expenseDate, setExpenseDate] = useState(new Date().toISOString().split('T')[0]);
  const [vendorName, setVendorName] = useState('');
  const [invoiceNumber, setInvoiceNumber] = useState('');
  const [description, setDescription] = useState('');

  // View Voucher Modal
  const [detailExpense, setDetailExpense] = useState<SocietyExpense | null>(null);

  // Delete Confirm Modal
  const [deleteId, setDeleteId] = useState<string | number | null>(null);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    fetchExpenses();
  }, [page, categoryFilter]);

  const fetchExpenses = async () => {
    setLoading(true);
    try {
      const params: any = { page, size: 10 };
      if (categoryFilter !== 'ALL') {
        params.category = categoryFilter;
      }
      const response = await expenseApi.getAllExpenses(params);
      if (response && response.content) {
        setExpenses(response.content);
        setTotalPages(response.totalPages || 1);
      } else if (response && (response as any).data?.content) {
        setExpenses((response as any).data.content);
        setTotalPages((response as any).data.totalPages || 1);
      } else if (Array.isArray(response)) {
        setExpenses(response);
        setTotalPages(1);
      }
    } catch (err) {
      console.error('Failed to load expenses', err);
      toast.error('Network Error', 'Unable to retrieve society expense ledger');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateExpense = async (e: React.FormEvent) => {
    e.preventDefault();
    const parsedAmount = parseFloat(amount);
    if (isNaN(parsedAmount) || parsedAmount <= 0) {
      toast.error('Validation Error', 'Expense amount must be greater than ₹0');
      return;
    }
    if (!title.trim()) {
      toast.error('Validation Error', 'Expense Title / Description is required');
      return;
    }

    try {
      setSubmitting(true);
      await expenseApi.createExpense({
        title: title.trim(),
        category,
        amount: parsedAmount,
        expenseDate,
        vendorName: vendorName.trim() || undefined,
        invoiceNumber: invoiceNumber.trim() || undefined,
        description: description.trim() || undefined,
      });
      toast.success('Expense Recorded', `Disbursement of ₹${parsedAmount.toLocaleString('en-IN')} added to society accounts.`);
      setShowModal(false);
      setTitle('');
      setAmount('15000');
      setVendorName('');
      setInvoiceNumber('');
      setDescription('');
      fetchExpenses();
    } catch (err: any) {
      const msg = err?.response?.data?.message || 'Failed to record society expense voucher';
      toast.error('Save Failed', msg);
    } finally {
      setSubmitting(false);
    }
  };

  const confirmDeleteExpense = async () => {
    if (!deleteId) return;
    try {
      setDeleting(true);
      await expenseApi.deleteExpense(deleteId);
      toast.success('Expense Removed', 'The voucher record has been permanently deleted.');
      setDeleteId(null);
      fetchExpenses();
    } catch (err: any) {
      toast.error('Delete Failed', err?.response?.data?.message || 'Failed to delete expense record');
    } finally {
      setDeleting(false);
    }
  };

  // Filtered by search query locally
  const displayedExpenses = useMemo(() => {
    if (!searchQuery.trim()) return expenses;
    const q = searchQuery.toLowerCase();
    return expenses.filter(
      (e) =>
        (e.title && e.title.toLowerCase().includes(q)) ||
        (e.vendorName && e.vendorName.toLowerCase().includes(q)) ||
        (e.invoiceNumber && e.invoiceNumber.toLowerCase().includes(q)) ||
        e.category.toLowerCase().includes(q)
    );
  }, [expenses, searchQuery]);

  const totalPageOutflow = useMemo(() => {
    return displayedExpenses.reduce((acc, curr) => acc + (curr.amount || 0), 0);
  }, [displayedExpenses]);

  return (
    <div className="space-y-6 animate-fadeIn text-slate-900">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 flex items-center gap-2">
            <DollarSign className="w-7 h-7 text-indigo-600" /> Society Expense & Utility Ledger
          </h1>
          <p className="text-xs text-slate-500 mt-1 font-medium">
            Monitor society maintenance expenditure, vendor payouts, utility invoices, and staff payroll
          </p>
        </div>
        {canManage && (
          <button
            onClick={() => setShowModal(true)}
            className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-bold uppercase tracking-wider flex items-center gap-2 transition shadow-md shadow-indigo-600/20"
          >
            <Plus className="w-4 h-4" /> Record New Expense
          </button>
        )}
      </div>

      {/* KPI Overview Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="bg-white border border-slate-200 rounded-2xl p-4 shadow-sm flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-rose-50 border border-rose-200 text-rose-600 flex items-center justify-center">
            <TrendingDown className="w-6 h-6" />
          </div>
          <div>
            <div className="text-[11px] font-bold text-slate-500 uppercase tracking-wider">Page Total Outflow</div>
            <div className="text-xl font-black text-rose-600">₹{totalPageOutflow.toLocaleString('en-IN')}</div>
          </div>
        </div>

        <div className="bg-white border border-slate-200 rounded-2xl p-4 shadow-sm flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-indigo-50 border border-indigo-200 text-indigo-600 flex items-center justify-center">
            <Receipt className="w-6 h-6" />
          </div>
          <div>
            <div className="text-[11px] font-bold text-slate-500 uppercase tracking-wider">Vouchers on Page</div>
            <div className="text-xl font-black text-slate-900">{displayedExpenses.length}</div>
          </div>
        </div>

        <div className="bg-white border border-slate-200 rounded-2xl p-4 shadow-sm flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-amber-50 border border-amber-200 text-amber-600 flex items-center justify-center">
            <Building2 className="w-6 h-6" />
          </div>
          <div>
            <div className="text-[11px] font-bold text-slate-500 uppercase tracking-wider">Active Category</div>
            <div className="text-base font-extrabold text-slate-800">{categoryFilter === 'ALL' ? 'All Expenditures' : categoryFilter}</div>
          </div>
        </div>
      </div>

      {/* Search and Filters Bar */}
      <div className="bg-white border border-slate-200 rounded-2xl p-4 shadow-sm flex flex-col md:flex-row gap-4 items-center justify-between">
        <div className="relative w-full md:w-80">
          <Search className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            type="text"
            placeholder="Search vendor, invoice, title..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full bg-slate-50 border border-slate-200 rounded-xl pl-10 pr-4 py-2 text-xs font-medium text-slate-900 focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
          />
        </div>

        <div className="flex items-center gap-3 w-full md:w-auto">
          <div className="flex items-center gap-2 text-xs font-bold text-slate-500 uppercase tracking-wider">
            <Filter className="w-3.5 h-3.5" /> Category:
          </div>
          <select
            value={categoryFilter}
            onChange={(e) => {
              setCategoryFilter(e.target.value);
              setPage(0);
            }}
            className="bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 text-xs font-semibold text-slate-700 focus:outline-none"
          >
            <option value="ALL">All Categories</option>
            {CATEGORIES.map((cat) => (
              <option key={cat} value={cat}>
                {cat.replace(/_/g, ' ')}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Expense Table */}
      <div className="bg-white border border-slate-200 rounded-2xl shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-700">
            <thead className="bg-slate-50 text-slate-500 uppercase tracking-wider font-extrabold border-b border-slate-200">
              <tr>
                <th className="px-5 py-3.5">Expense / Voucher Title</th>
                <th className="px-5 py-3.5">Category</th>
                <th className="px-5 py-3.5">Vendor / Payee</th>
                <th className="px-5 py-3.5">Invoice #</th>
                <th className="px-5 py-3.5">Disbursement Date</th>
                <th className="px-5 py-3.5 text-right">Amount</th>
                <th className="px-5 py-3.5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan={7} className="px-5 py-12 text-center text-slate-400">
                    Loading expense vouchers...
                  </td>
                </tr>
              ) : displayedExpenses.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-5 py-12 text-center text-slate-500">
                    <Receipt className="w-8 h-8 text-slate-300 mx-auto mb-2" />
                    No expense records found.
                  </td>
                </tr>
              ) : (
                displayedExpenses.map((ex) => (
                  <tr key={ex.id} className="hover:bg-slate-50 transition">
                    <td className="px-5 py-3.5">
                      <div className="font-extrabold text-slate-900">{ex.title || `Voucher #${ex.id}`}</div>
                      {ex.description && (
                        <div className="text-[11px] text-slate-500 line-clamp-1">{ex.description}</div>
                      )}
                    </td>
                    <td className="px-5 py-3.5">
                      <Badge variant="info">{ex.category.replace(/_/g, ' ')}</Badge>
                    </td>
                    <td className="px-5 py-3.5 font-medium text-slate-800">
                      {ex.vendorName || <span className="text-slate-400 italic">Self / Internal</span>}
                    </td>
                    <td className="px-5 py-3.5 font-mono text-slate-600">
                      {ex.invoiceNumber || '—'}
                    </td>
                    <td className="px-5 py-3.5 font-mono text-slate-500">
                      {ex.expenseDate}
                    </td>
                    <td className="px-5 py-3.5 text-right font-black text-rose-600 text-sm">
                      ₹{ex.amount.toLocaleString('en-IN')}
                    </td>
                    <td className="px-5 py-3.5 text-right">
                      <div className="flex items-center justify-end gap-2">
                        <button
                          onClick={() => setDetailExpense(ex)}
                          className="p-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-lg transition"
                          title="View Voucher"
                        >
                          <Eye className="w-4 h-4" />
                        </button>
                        {canManage && (
                          <button
                            onClick={() => setDeleteId(ex.id)}
                            className="p-1.5 bg-rose-50 hover:bg-rose-100 text-rose-600 rounded-lg transition"
                            title="Delete Expense"
                          >
                            <Trash2 className="w-4 h-4" />
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
        <Pagination
          currentPage={page}
          totalPages={totalPages}
          onPageChange={setPage}
        />
      </div>

      {/* View Voucher Modal */}
      {detailExpense && (
        <Modal isOpen={!!detailExpense} onClose={() => setDetailExpense(null)} title="Expense Voucher Details">
          <div className="space-y-4 text-slate-900">
            <div className="p-4 bg-slate-50 rounded-2xl border border-slate-200">
              <div className="flex items-center justify-between mb-2">
                <Badge variant="info">{detailExpense.category.replace(/_/g, ' ')}</Badge>
                <div className="text-xs font-mono text-slate-500">{detailExpense.expenseDate}</div>
              </div>
              <h3 className="text-base font-extrabold text-slate-900">{detailExpense.title}</h3>
              {detailExpense.description && (
                <p className="text-xs text-slate-600 mt-2">{detailExpense.description}</p>
              )}
            </div>

            <div className="grid grid-cols-2 gap-3 text-xs">
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                <div className="text-[10px] text-slate-400 font-bold uppercase">Vendor / Service Provider</div>
                <div className="font-bold text-slate-800 mt-0.5">{detailExpense.vendorName || 'Direct Payment'}</div>
              </div>
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                <div className="text-[10px] text-slate-400 font-bold uppercase">Invoice / Bill Reference</div>
                <div className="font-mono font-bold text-slate-800 mt-0.5">{detailExpense.invoiceNumber || 'N/A'}</div>
              </div>
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200 col-span-2">
                <div className="text-[10px] text-slate-400 font-bold uppercase">Disbursed Amount</div>
                <div className="text-xl font-black text-rose-600 mt-0.5">₹{detailExpense.amount.toLocaleString('en-IN')}</div>
              </div>
            </div>

            <div className="flex justify-end pt-2">
              <button
                onClick={() => setDetailExpense(null)}
                className="px-5 py-2 bg-slate-200 hover:bg-slate-300 text-slate-800 font-bold text-xs uppercase tracking-wider rounded-xl transition"
              >
                Close
              </button>
            </div>
          </div>
        </Modal>
      )}

      {/* Delete Confirmation Modal */}
      {deleteId && (
        <Modal isOpen={!!deleteId} onClose={() => setDeleteId(null)} title="Confirm Voucher Deletion">
          <div className="space-y-4 text-slate-900">
            <p className="text-xs text-slate-600">
              Are you sure you want to permanently remove this expense voucher? This will remove the voucher from financial audit records.
            </p>
            <div className="flex justify-end gap-3 pt-2">
              <button
                onClick={() => setDeleteId(null)}
                className="px-4 py-2 bg-slate-200 hover:bg-slate-300 text-slate-700 font-bold text-xs rounded-xl transition"
              >
                Cancel
              </button>
              <button
                onClick={confirmDeleteExpense}
                disabled={deleting}
                className="px-5 py-2 bg-rose-600 hover:bg-rose-700 disabled:opacity-50 text-white font-bold text-xs rounded-xl transition"
              >
                {deleting ? 'Deleting...' : 'Delete Voucher'}
              </button>
            </div>
          </div>
        </Modal>
      )}

      {/* Add Expense Modal */}
      <Modal isOpen={showModal} onClose={() => setShowModal(false)} title="Record Society Expense">
        <form onSubmit={handleCreateExpense} className="space-y-4 text-slate-900">
          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
              Expense / Voucher Title *
            </label>
            <input
              type="text"
              required
              placeholder="e.g. Common Area Electricity Bill - Block A & B"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Category *
              </label>
              <select
                value={category}
                onChange={(e) => setCategory(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
              >
                {CATEGORIES.map((cat) => (
                  <option key={cat} value={cat}>
                    {cat.replace(/_/g, ' ')}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Disbursed Amount (₹) *
              </label>
              <input
                type="number"
                required
                min="1"
                step="any"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Vendor / Payee Name
              </label>
              <input
                type="text"
                placeholder="e.g. Tata Power / Schindler Lifts"
                value={vendorName}
                onChange={(e) => setVendorName(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
              />
            </div>
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Invoice / Reference Number
              </label>
              <input
                type="text"
                placeholder="e.g. INV-2026-908"
                value={invoiceNumber}
                onChange={(e) => setInvoiceNumber(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-mono"
              />
            </div>
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
              Expense Date *
            </label>
            <input
              type="date"
              required
              value={expenseDate}
              onChange={(e) => setExpenseDate(e.target.value)}
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
            />
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
              Description / Notes
            </label>
            <textarea
              rows={3}
              placeholder="Additional details regarding invoice approval or work done..."
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
            />
          </div>

          <button
            type="submit"
            disabled={submitting}
            className="w-full py-3 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 text-white font-extrabold text-xs uppercase tracking-wider rounded-xl transition shadow-md"
          >
            {submitting ? 'Recording Expense...' : 'Save Expense Voucher'}
          </button>
        </form>
      </Modal>
    </div>
  );
};
