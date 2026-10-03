import React, { useEffect, useState } from 'react';
import { dashboardApi } from '../api/dashboardApi';
import { paymentApi } from '../api/paymentApi';
import { complaintApi } from '../api/complaintApi';
import { visitorApi } from '../api/visitorApi';
import { ResidentDashboardSummary, MaintenanceBill } from '../types';
import { StatCard } from '../components/common/StatCard';
import { Modal } from '../components/common/Modal';
import { Badge } from '../components/common/Badge';
import { useToast } from '../context/ToastContext';
import {
  Home,
  Receipt,
  AlertTriangle,
  UserCheck,
  CreditCard,
  Plus,
  CheckCircle,
} from 'lucide-react';

export const ResidentDashboard: React.FC = () => {
  const toast = useToast();
  const [summary, setSummary] = useState<ResidentDashboardSummary | null>(null);
  const [loading, setLoading] = useState(true);

  // Modal States
  const [payModalBill, setPayModalBill] = useState<MaintenanceBill | null>(null);
  const [paymentMethod, setPaymentMethod] = useState('UPI');
  const [txnRef, setTxnRef] = useState('');
  const [payLoading, setPayLoading] = useState(false);

  // New Complaint Modal
  const [showComplaintModal, setShowComplaintModal] = useState(false);
  const [complaintTitle, setComplaintTitle] = useState('');
  const [complaintDesc, setComplaintDesc] = useState('');
  const [complaintCategory, setComplaintCategory] = useState('PLUMBING');
  const [complaintPriority, setComplaintPriority] = useState('MEDIUM');

  // Pre-register Visitor Modal
  const [showVisitorModal, setShowVisitorModal] = useState(false);
  const [vName, setVName] = useState('');
  const [vPhone, setVPhone] = useState('');
  const [vPurpose, setVPurpose] = useState('');

  useEffect(() => {
    fetchSummary();
  }, []);

  const fetchSummary = async () => {
    try {
      const response = await dashboardApi.getResidentSummary();
      if (response && response.data) {
        setSummary(response.data);
      } else if (response) {
        setSummary(response as any);
      }
    } catch (err) {
      console.error('Failed to fetch resident dashboard', err);
    } finally {
      setLoading(false);
    }
  };

  const handlePayBill = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!payModalBill) return;
    setPayLoading(true);

    try {
      const payable = payModalBill.netPayableAmount ?? payModalBill.totalAmount ?? 0;
      await paymentApi.recordPayment({
        billId: payModalBill.id,
        amountPaid: payable,
        paymentMethod,
        transactionReference: txnRef || `TXN-${Date.now()}`,
        remarks: 'Online Resident Portal Payment',
      });
      toast.success('Payment Successful', `Settled ₹${payable.toLocaleString('en-IN')} for Bill ${payModalBill.billNumber}.`);
      setPayModalBill(null);
      setTxnRef('');
      fetchSummary();
    } catch (err: any) {
      toast.error('Payment Failed', err?.response?.data?.message || 'Failed to process payment');
    } finally {
      setPayLoading(false);
    }
  };

  const handleCreateComplaint = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await complaintApi.createComplaint({
        title: complaintTitle,
        description: complaintDesc,
        category: complaintCategory,
        priority: complaintPriority,
      });
      toast.success('Request Submitted', 'Your maintenance ticket has been assigned to helpdesk.');
      setShowComplaintModal(false);
      setComplaintTitle('');
      setComplaintDesc('');
      fetchSummary();
    } catch (err: any) {
      toast.error('Submission Failed', err?.response?.data?.message || 'Failed to submit complaint');
    }
  };

  const handlePreRegisterVisitor = async (e: React.FormEvent) => {
    e.preventDefault();
    const flatObj = summary?.flatDetails || summary?.flat;
    if (!flatObj) return;
    try {
      await visitorApi.preRegister({
        flatId: flatObj.id,
        visitorName: vName,
        phone: vPhone,
        purpose: vPurpose,
      });
      toast.success('Visitor Pass Issued', `Pre-registered guest pass issued for ${vName}.`);
      setShowVisitorModal(false);
      setVName('');
      setVPhone('');
      setVPurpose('');
      fetchSummary();
    } catch (err: any) {
      toast.error('Registration Failed', err?.response?.data?.message || 'Failed to pre-register visitor');
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <div className="w-10 h-10 border-4 border-emerald-200 border-t-emerald-600 rounded-full animate-spin" />
      </div>
    );
  }

  const flatObj = summary?.flatDetails || summary?.flat;
  const unpaidList =
    summary?.unpaidBills || summary?.recentBills?.filter((b) => b.status !== 'PAID') || [];
  const unpaidDues = summary?.totalUnpaidDues ?? summary?.totalOutstandingAmount ?? 0;
  const activeComplaintsCount =
    summary?.myActiveComplaintsCount ??
    summary?.myComplaints?.filter((c) => c.status !== 'CLOSED' && c.status !== 'RESOLVED').length ??
    0;
  const visitorsTodayCount =
    summary?.expectedVisitorsTodayCount ?? summary?.upcomingVisitors?.length ?? 0;

  return (
    <div className="space-y-8 animate-fadeIn">
      {/* Header Light Banner */}
      <div className="p-6 sm:p-8 rounded-3xl bg-white border border-slate-200 shadow-sm relative overflow-hidden flex flex-col sm:flex-row sm:items-center justify-between gap-6">
        <div className="relative z-10">
          <div className="flex items-center gap-2 mb-2">
            <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
            <span className="text-[11px] font-extrabold uppercase tracking-widest text-emerald-700 font-mono">
              Resident Self-Service Hub
            </span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-black text-slate-900 tracking-tight">
            Welcome Home, Flat {flatObj?.flatNumber || 'Resident'}
          </h1>
          <p className="text-xs text-slate-500 mt-1 max-w-xl">
            Pre-approve guest gate passes, pay maintenance dues, and track service requests.
          </p>
        </div>

        <div className="relative z-10 flex items-center gap-3">
          <button
            onClick={() => setShowVisitorModal(true)}
            className="px-4 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-800 border border-slate-300 rounded-xl text-xs font-bold flex items-center gap-2 transition"
          >
            <UserCheck className="w-4 h-4 text-emerald-600" /> Pre-Register Guest
          </button>
          <button
            onClick={() => setShowComplaintModal(true)}
            className="px-4 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-extrabold rounded-xl text-xs flex items-center gap-2 shadow-md shadow-emerald-600/20 transition"
          >
            <Plus className="w-4 h-4" /> Raise Request
          </button>
        </div>
      </div>

      {/* Metrics Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <StatCard
          title="My Flat Residence"
          value={flatObj?.flatNumber || 'A-101'}
          subtitle={`${flatObj?.squareFeet || flatObj?.areaSqft || 1200} Sq. Ft. • (${
            flatObj?.occupancyStatus || 'OWNED'
          })`}
          icon={Home}
          color="purple"
        />
        <StatCard
          title="Outstanding Dues"
          value={`₹${unpaidDues.toLocaleString('en-IN')}`}
          subtitle={`${unpaidList.length} Pending Invoice(s)`}
          icon={Receipt}
          color={unpaidDues > 0 ? 'rose' : 'emerald'}
        />
        <StatCard
          title="Active Service Tickets"
          value={activeComplaintsCount}
          subtitle="Open Maintenance Requests"
          icon={AlertTriangle}
          color="amber"
        />
        <StatCard
          title="Pre-Approved Guests"
          value={visitorsTodayCount}
          subtitle="Expected Gate Visitors Today"
          icon={UserCheck}
          color="cyan"
        />
      </div>

      {/* Unpaid Bills Section */}
      <div className="bg-white border border-slate-200 rounded-3xl p-6 shadow-sm">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2">
            <Receipt className="w-5 h-5 text-emerald-600" />
            <h3 className="text-base font-bold text-slate-900">Maintenance Bills & Dues Ledger</h3>
          </div>
        </div>

        {unpaidList.length === 0 ? (
          <div className="p-8 text-center bg-slate-50 rounded-2xl border border-slate-200">
            <CheckCircle className="w-10 h-10 text-emerald-600 mx-auto mb-2" />
            <p className="text-sm font-extrabold text-slate-900">No Outstanding Dues!</p>
            <p className="text-xs text-slate-500 mt-1">
              All monthly maintenance charges have been paid.
            </p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs text-slate-700">
              <thead className="bg-slate-50 text-slate-500 uppercase tracking-wider font-extrabold border-b border-slate-200">
                <tr>
                  <th className="px-4 py-3.5">Bill Number</th>
                  <th className="px-4 py-3.5">Billing Period</th>
                  <th className="px-4 py-3.5">Due Date</th>
                  <th className="px-4 py-3.5">Net Payable</th>
                  <th className="px-4 py-3.5">Status</th>
                  <th className="px-4 py-3.5 text-right">Payment</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {unpaidList.map((bill) => {
                  const payable = bill.netPayableAmount ?? bill.totalAmount ?? 0;
                  return (
                    <tr key={bill.id} className="hover:bg-slate-50 transition">
                      <td className="px-4 py-3.5 font-bold text-slate-900 font-mono">
                        {bill.billNumber}
                      </td>
                      <td className="px-4 py-3.5 font-medium">
                        {bill.billingMonth || bill.billingPeriod || 'Current Month'}
                      </td>
                      <td className="px-4 py-3.5 text-rose-600 font-semibold font-mono">
                        {bill.dueDate}
                      </td>
                      <td className="px-4 py-3.5 font-black text-emerald-700 text-sm font-mono">
                        ₹{payable.toLocaleString('en-IN')}
                      </td>
                      <td className="px-4 py-3.5">
                        <Badge variant={bill.status === 'OVERDUE' ? 'danger' : 'warning'}>
                          {bill.status}
                        </Badge>
                      </td>
                      <td className="px-4 py-3.5 text-right">
                        <button
                          onClick={() => setPayModalBill(bill)}
                          className="px-3.5 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white font-extrabold rounded-xl flex items-center gap-1.5 text-xs transition ml-auto shadow-sm"
                        >
                          <CreditCard className="w-3.5 h-3.5" /> Pay Online
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Pay Modal */}
      <Modal
        isOpen={Boolean(payModalBill)}
        onClose={() => setPayModalBill(null)}
        title={`Pay Maintenance Bill: ${payModalBill?.billNumber}`}
      >
        <form onSubmit={handlePayBill} className="space-y-4 text-slate-900">
          <div className="p-4 bg-slate-50 border border-slate-200 rounded-2xl space-y-2">
            <div className="flex justify-between text-xs text-slate-500">
              <span>Billing Month:</span>
              <span className="text-slate-900 font-bold">
                {payModalBill?.billingMonth || 'N/A'}
              </span>
            </div>
            <div className="flex justify-between text-xs text-slate-500">
              <span>Base Maintenance Dues:</span>
              <span className="text-slate-900 font-mono">
                ₹{payModalBill?.maintenanceAmount ?? payModalBill?.baseAmount ?? 0}
              </span>
            </div>
            {payModalBill?.lateFeeAmount ? (
              <div className="flex justify-between text-xs text-rose-600">
                <span>Overdue Penalty:</span>
                <span className="font-mono">+ ₹{payModalBill.lateFeeAmount}</span>
              </div>
            ) : null}
            <div className="pt-2 border-t border-slate-200 flex justify-between text-sm font-black text-emerald-700">
              <span>Total Net Payable:</span>
              <span className="font-mono">
                ₹
                {(
                  payModalBill?.netPayableAmount ??
                  payModalBill?.totalAmount ??
                  0
                ).toLocaleString('en-IN')}
              </span>
            </div>
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-2">
              Payment Gateway Method
            </label>
            <select
              value={paymentMethod}
              onChange={(e) => setPaymentMethod(e.target.value)}
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
            >
              <option value="UPI">UPI Instant (GPay, PhonePe, Paytm QR)</option>
              <option value="CARD">Credit / Debit Card</option>
              <option value="BANK_TRANSFER">Net Banking / NEFT Transfer</option>
            </select>
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
              Transaction Reference / UTR Number
            </label>
            <input
              type="text"
              required
              placeholder="e.g. UTR-9923841029"
              value={txnRef}
              onChange={(e) => setTxnRef(e.target.value)}
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-mono"
            />
          </div>

          <button
            type="submit"
            disabled={payLoading}
            className="w-full py-3 bg-emerald-600 hover:bg-emerald-700 text-white font-extrabold text-xs uppercase tracking-wider rounded-xl transition shadow-md shadow-emerald-600/20"
          >
            {payLoading ? 'Processing Payment...' : 'Confirm & Record Payment'}
          </button>
        </form>
      </Modal>

      {/* Pre-Register Visitor Modal */}
      <Modal
        isOpen={showVisitorModal}
        onClose={() => setShowVisitorModal(false)}
        title="Pre-Register Expected Gate Visitor"
      >
        <form onSubmit={handlePreRegisterVisitor} className="space-y-4 text-slate-900">
          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
              Guest Full Name
            </label>
            <input
              type="text"
              required
              value={vName}
              onChange={(e) => setVName(e.target.value)}
              placeholder="Guest Name"
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
            />
          </div>
          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
              Guest Phone Number
            </label>
            <input
              type="tel"
              required
              value={vPhone}
              onChange={(e) => setVPhone(e.target.value)}
              placeholder="+91 9876543210"
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-mono"
            />
          </div>
          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
              Visit Purpose
            </label>
            <input
              type="text"
              required
              value={vPurpose}
              onChange={(e) => setVPurpose(e.target.value)}
              placeholder="Family Visit / Delivery / Service Technician"
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
            />
          </div>
          <button
            type="submit"
            className="w-full py-3 bg-emerald-600 hover:bg-emerald-700 text-white font-extrabold text-xs uppercase tracking-wider rounded-xl transition shadow-md"
          >
            Generate Gate Verification Pass
          </button>
        </form>
      </Modal>

      {/* Raise Complaint Modal */}
      <Modal
        isOpen={showComplaintModal}
        onClose={() => setShowComplaintModal(false)}
        title="Raise Helpdesk Maintenance Request"
      >
        <form onSubmit={handleCreateComplaint} className="space-y-4 text-slate-900">
          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
              Issue Title
            </label>
            <input
              type="text"
              required
              value={complaintTitle}
              onChange={(e) => setComplaintTitle(e.target.value)}
              placeholder="e.g. Water tap leaking in master bathroom"
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
            />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Category
              </label>
              <select
                value={complaintCategory}
                onChange={(e) => setComplaintCategory(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
              >
                <option value="PLUMBING">Plumbing</option>
                <option value="ELECTRICAL">Electrical</option>
                <option value="SECURITY">Security</option>
                <option value="CLEANING">Housekeeping</option>
                <option value="LIFT">Elevator / Lift</option>
                <option value="WATER">Water Supply</option>
                <option value="PARKING">Parking Slot</option>
                <option value="OTHER">Other</option>
              </select>
            </div>
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Urgency Level
              </label>
              <select
                value={complaintPriority}
                onChange={(e) => setComplaintPriority(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
              >
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
                <option value="URGENT">Urgent / Critical</option>
              </select>
            </div>
          </div>
          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
              Issue Details
            </label>
            <textarea
              required
              rows={3}
              value={complaintDesc}
              onChange={(e) => setComplaintDesc(e.target.value)}
              placeholder="Provide exact details for staff..."
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2 text-xs text-slate-900 font-medium"
            />
          </div>
          <button
            type="submit"
            className="w-full py-3 bg-emerald-600 hover:bg-emerald-700 text-white font-extrabold text-xs uppercase tracking-wider rounded-xl transition shadow-md"
          >
            Submit Helpdesk Ticket
          </button>
        </form>
      </Modal>
    </div>
  );
};
