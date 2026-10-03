import React, { useEffect, useState, useMemo } from 'react';
import { visitorApi } from '../api/visitorApi';
import { flatApi } from '../api/flatApi';
import { Visitor, Flat } from '../types';
import { Badge } from '../components/common/Badge';
import { Pagination } from '../components/common/Pagination';
import { Modal } from '../components/common/Modal';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import {
  UserCheck, LogOut, CheckCircle2, Plus, Search, Filter,
  Phone, Clock, Car, ShieldAlert, Loader2, Home, XCircle
} from 'lucide-react';

export const VisitorsPage: React.FC = () => {
  const { user, hasAnyRole } = useAuth();
  const toast = useToast();
  const [visitors, setVisitors] = useState<Visitor[]>([]);
  const [flats, setFlats] = useState<Flat[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [actionId, setActionId] = useState<string | number | null>(null);

  // Filters
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  // Register Visitor Modal
  const [showModal, setShowModal] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [visitorName, setVisitorName] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [purpose, setPurpose] = useState('Guest / Family Visit');
  const [flatId, setFlatId] = useState('');
  const [vehicleNumber, setVehicleNumber] = useState('');
  const [expectedArrival, setExpectedArrival] = useState('');

  const canGateManage = hasAnyRole(['SUPER_ADMIN', 'SOCIETY_ADMIN', 'SECURITY']);

  useEffect(() => {
    fetchVisitors();
    fetchFlats();
  }, [page]);

  const fetchFlats = async () => {
    try {
      const res = await flatApi.getAllFlats({ size: 100 });
      if (res && res.content) {
        setFlats(res.content);
      } else if (res && (res as any).data?.content) {
        setFlats((res as any).data.content);
      } else if (Array.isArray(res)) {
        setFlats(res as any);
      }
    } catch (err) {
      console.error('Failed to load flats', err);
    }
  };

  const fetchVisitors = async () => {
    setLoading(true);
    try {
      const response = await visitorApi.getAllVisitors({ page, size: 12 });
      if (response && response.data) {
        setVisitors(response.data.content || []);
        setTotalPages(response.data.totalPages || 1);
      } else if (response && (response as any).content) {
        setVisitors((response as any).content);
        setTotalPages((response as any).totalPages || 1);
      }
    } catch (err) {
      console.error('Failed to load visitors history', err);
      toast.error('Network Error', 'Unable to retrieve visitor log');
    } finally {
      setLoading(false);
    }
  };

  const handlePreRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    const effectiveFlatId = flatId || user?.flatId;
    if (!effectiveFlatId) {
      toast.error('Validation Error', 'Please select a destination flat');
      return;
    }
    if (!visitorName.trim()) {
      toast.error('Validation Error', 'Visitor name is required');
      return;
    }

    try {
      setSubmitting(true);
      await visitorApi.preRegister({
        flatId: effectiveFlatId,
        visitorName: visitorName.trim(),
        phoneNumber: phoneNumber.trim() || undefined,
        purpose: purpose.trim() || 'Visit',
        vehicleNumber: vehicleNumber.trim().toUpperCase() || undefined,
        expectedArrival: expectedArrival ? `${expectedArrival}:00` : undefined,
      });

      toast.success('Visitor Registered', `Entry pass logged for ${visitorName}.`);
      setShowModal(false);
      setVisitorName('');
      setPhoneNumber('');
      setVehicleNumber('');
      setExpectedArrival('');
      fetchVisitors();
    } catch (err: any) {
      toast.error('Registration Failed', err?.response?.data?.message || 'Failed to register visitor');
    } finally {
      setSubmitting(false);
    }
  };

  const handleCheckIn = async (id: string | number, name: string) => {
    setActionId(id);
    try {
      await visitorApi.checkIn(id);
      toast.success('Visitor Checked In', `${name} has arrived at the gate and entered.`);
      await fetchVisitors();
    } catch (err: any) {
      toast.error('Check-In Failed', err?.response?.data?.message || 'Failed to check in visitor');
    } finally {
      setActionId(null);
    }
  };

  const handleCheckOut = async (id: string | number, name: string) => {
    setActionId(id);
    try {
      await visitorApi.checkOut(id);
      toast.success('Visitor Checked Out', `${name} departure logged.`);
      await fetchVisitors();
    } catch (err: any) {
      toast.error('Check-Out Failed', err?.response?.data?.message || 'Failed to check out visitor');
    } finally {
      setActionId(null);
    }
  };

  const filteredVisitors = useMemo(() => {
    return visitors.filter((v) => {
      const q = searchQuery.toLowerCase();
      const matchSearch =
        v.visitorName.toLowerCase().includes(q) ||
        (v.phoneNumber && v.phoneNumber.includes(q)) ||
        (v.phone && v.phone.includes(q)) ||
        (v.flatNumber && v.flatNumber.toLowerCase().includes(q)) ||
        (v.purpose && v.purpose.toLowerCase().includes(q));

      const matchStatus = statusFilter === 'ALL' || v.status === statusFilter;
      return matchSearch && matchStatus;
    });
  }, [visitors, searchQuery, statusFilter]);

  const activeInsideCount = useMemo(() => {
    return visitors.filter((v) => v.status === 'CHECKED_IN' || v.status === 'INSIDE').length;
  }, [visitors]);

  return (
    <div className="space-y-6 animate-fadeIn text-slate-900">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 flex items-center gap-2">
            <UserCheck className="w-7 h-7 text-emerald-600" /> Visitor Register & Security Gate Log
          </h1>
          <p className="text-xs text-slate-500 mt-1 font-medium">
            Monitor gate entry passes, pre-registered visitors, delivery executives, and exit timestamps
          </p>
        </div>
        <button
          onClick={() => setShowModal(true)}
          className="px-5 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold uppercase tracking-wider flex items-center gap-2 transition shadow-md shadow-emerald-600/20"
        >
          <Plus className="w-4 h-4" /> Pre-Register Visitor
        </button>
      </div>

      {/* KPI Banner */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="bg-white border border-slate-200 rounded-2xl p-4 shadow-sm flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-emerald-50 border border-emerald-200 text-emerald-700 flex items-center justify-center font-bold">
            <UserCheck className="w-6 h-6" />
          </div>
          <div>
            <div className="text-[11px] font-bold text-slate-500 uppercase tracking-wider">Visitors Currently Inside</div>
            <div className="text-xl font-black text-emerald-700">{activeInsideCount} Guests</div>
          </div>
        </div>

        <div className="bg-white border border-slate-200 rounded-2xl p-4 shadow-sm flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-indigo-50 border border-indigo-200 text-indigo-700 flex items-center justify-center font-bold">
            <Clock className="w-6 h-6" />
          </div>
          <div>
            <div className="text-[11px] font-bold text-slate-500 uppercase tracking-wider">Total Recorded in Page</div>
            <div className="text-xl font-black text-slate-900">{filteredVisitors.length} Log Entries</div>
          </div>
        </div>

        <div className="bg-white border border-slate-200 rounded-2xl p-4 shadow-sm flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-amber-50 border border-amber-200 text-amber-700 flex items-center justify-center font-bold">
            <ShieldAlert className="w-6 h-6" />
          </div>
          <div>
            <div className="text-[11px] font-bold text-slate-500 uppercase tracking-wider">Security Gate Protocol</div>
            <div className="text-sm font-extrabold text-slate-800">Pass Verification Active</div>
          </div>
        </div>
      </div>

      {/* Search and Filters Bar */}
      <div className="bg-white border border-slate-200 rounded-2xl p-4 shadow-sm flex flex-col md:flex-row gap-4 items-center justify-between">
        <div className="relative w-full md:w-80">
          <Search className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            type="text"
            placeholder="Search visitor, phone, flat..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full bg-slate-50 border border-slate-200 rounded-xl pl-10 pr-4 py-2 text-xs font-medium text-slate-900 focus:outline-none focus:ring-2 focus:ring-emerald-500/20"
          />
        </div>

        <div className="flex items-center gap-3 w-full md:w-auto">
          <div className="flex items-center gap-2 text-xs font-bold text-slate-500 uppercase tracking-wider">
            <Filter className="w-3.5 h-3.5" /> Status:
          </div>
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            className="bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 text-xs font-semibold text-slate-700 focus:outline-none"
          >
            <option value="ALL">All Statuses</option>
            <option value="EXPECTED">Expected (Pre-registered)</option>
            <option value="CHECKED_IN">Inside Society</option>
            <option value="CHECKED_OUT">Checked Out</option>
            <option value="CANCELLED">Cancelled</option>
          </select>
        </div>
      </div>

      {/* Visitors Table */}
      <div className="bg-white border border-slate-200 rounded-2xl shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-700">
            <thead className="bg-slate-50 text-slate-500 uppercase tracking-wider font-extrabold border-b border-slate-200">
              <tr>
                <th className="px-5 py-3.5">Visitor Name</th>
                <th className="px-5 py-3.5">Destination Unit</th>
                <th className="px-5 py-3.5">Phone Number</th>
                <th className="px-5 py-3.5">Purpose</th>
                <th className="px-5 py-3.5">Vehicle</th>
                <th className="px-5 py-3.5">Arrival Time</th>
                <th className="px-5 py-3.5">Departure Time</th>
                <th className="px-5 py-3.5">Status</th>
                <th className="px-5 py-3.5 text-right">Gate Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan={9} className="px-5 py-12 text-center text-slate-400">
                    <Loader2 className="w-6 h-6 animate-spin mx-auto mb-2 text-emerald-600" />
                    Loading visitor logs...
                  </td>
                </tr>
              ) : filteredVisitors.length === 0 ? (
                <tr>
                  <td colSpan={9} className="px-5 py-12 text-center text-slate-500">
                    <UserCheck className="w-8 h-8 text-slate-300 mx-auto mb-2" />
                    No visitor logs match your search.
                  </td>
                </tr>
              ) : (
                filteredVisitors.map((v) => {
                  const checkIn = v.checkInTime || v.actualArrival || v.expectedArrival;
                  const checkOut = v.checkOutTime || v.actualDeparture;
                  const isInside = v.status === 'CHECKED_IN' || v.status === 'INSIDE';
                  const isExpected = v.status === 'EXPECTED';
                  const isActing = actionId === v.id;

                  return (
                    <tr key={v.id} className="hover:bg-slate-50 transition">
                      <td className="px-5 py-3.5 font-bold text-slate-900">{v.visitorName}</td>
                      <td className="px-5 py-3.5 font-semibold text-slate-800">
                        Flat {v.flatNumber || v.flatId} {v.buildingName ? `(${v.buildingName})` : ''}
                      </td>
                      <td className="px-5 py-3.5 font-mono text-slate-600">{v.phone || v.phoneNumber || 'N/A'}</td>
                      <td className="px-5 py-3.5 font-medium">{v.purpose}</td>
                      <td className="px-5 py-3.5 font-mono text-slate-500">{v.vehicleNumber || '—'}</td>
                      <td className="px-5 py-3.5 text-slate-500 font-mono">
                        {checkIn ? new Date(checkIn).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '—'}
                      </td>
                      <td className="px-5 py-3.5 text-slate-500 font-mono">
                        {checkOut ? new Date(checkOut).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '—'}
                      </td>
                      <td className="px-5 py-3.5">
                        <Badge
                          variant={
                            isInside ? 'success' : isExpected ? 'info' : v.status === 'CANCELLED' ? 'secondary' : 'warning'
                          }
                        >
                          {v.status.replace(/_/g, ' ')}
                        </Badge>
                      </td>
                      <td className="px-5 py-3.5 text-right">
                        {canGateManage && isExpected && (
                          <button
                            onClick={() => handleCheckIn(v.id, v.visitorName)}
                            disabled={isActing}
                            className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-700 disabled:opacity-50 text-white rounded-lg text-xs font-bold transition inline-flex items-center gap-1 shadow-sm"
                          >
                            {isActing ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <CheckCircle2 className="w-3.5 h-3.5" />}
                            Check In
                          </button>
                        )}
                        {canGateManage && isInside && (
                          <button
                            onClick={() => handleCheckOut(v.id, v.visitorName)}
                            disabled={isActing}
                            className="px-3 py-1.5 bg-rose-50 hover:bg-rose-100 text-rose-600 border border-rose-200 rounded-lg text-xs font-bold transition inline-flex items-center gap-1"
                          >
                            {isActing ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <LogOut className="w-3.5 h-3.5" />}
                            Check Out
                          </button>
                        )}
                        {!isExpected && !isInside && (
                          <span className="text-slate-400 text-xs italic">Logged</span>
                        )}
                      </td>
                    </tr>
                  );
                })
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

      {/* Pre-Register Visitor Modal */}
      <Modal isOpen={showModal} onClose={() => setShowModal(false)} title="Pre-Register Expected Visitor">
        <form onSubmit={handlePreRegister} className="space-y-4 text-slate-900">
          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
              Destination Flat *
            </label>
            <select
              required
              value={flatId || user?.flatId || ''}
              onChange={(e) => setFlatId(e.target.value)}
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium focus:outline-none focus:border-emerald-500"
            >
              <option value="">-- Select Destination Unit --</option>
              {flats.map((f) => (
                <option key={f.id} value={f.id}>
                  Flat {f.flatNumber} {f.buildingName && `(Block ${f.buildingName})`}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
              Visitor Full Name *
            </label>
            <input
              type="text"
              required
              placeholder="e.g. Vikram Sharma"
              value={visitorName}
              onChange={(e) => setVisitorName(e.target.value)}
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Contact Phone
              </label>
              <input
                type="text"
                placeholder="e.g. 9811223344"
                value={phoneNumber}
                onChange={(e) => setPhoneNumber(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-mono focus:outline-none focus:border-emerald-500"
              />
            </div>
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Vehicle Number
              </label>
              <input
                type="text"
                placeholder="e.g. MH12AB5678"
                value={vehicleNumber}
                onChange={(e) => setVehicleNumber(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-mono uppercase focus:outline-none focus:border-emerald-500"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Visit Purpose *
              </label>
              <select
                value={purpose}
                onChange={(e) => setPurpose(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium focus:outline-none focus:border-emerald-500"
              >
                <option value="Guest / Family Visit">Guest / Family Visit</option>
                <option value="Delivery / Courier">Delivery / Courier</option>
                <option value="Service / Repair Technician">Service / Repair Technician</option>
                <option value="Housekeeping / Maid">Housekeeping / Maid</option>
                <option value="Cab / Transport Pick-up">Cab / Transport Pick-up</option>
                <option value="Other">Other</option>
              </select>
            </div>
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Expected Arrival
              </label>
              <input
                type="datetime-local"
                value={expectedArrival}
                onChange={(e) => setExpectedArrival(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium focus:outline-none focus:border-emerald-500"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={submitting}
            className="w-full py-3 bg-emerald-600 hover:bg-emerald-700 disabled:opacity-50 text-white font-extrabold text-xs uppercase tracking-wider rounded-xl transition shadow-md flex items-center justify-center gap-2"
          >
            {submitting ? <Loader2 className="w-4 h-4 animate-spin" /> : null}
            {submitting ? 'Registering Entry...' : 'Generate Visitor Pass'}
          </button>
        </form>
      </Modal>
    </div>
  );
};
