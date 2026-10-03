import React, { useEffect, useState, useMemo } from 'react';
import { staffApi } from '../api/staffApi';
import { Staff } from '../types';
import { Modal } from '../components/common/Modal';
import { Badge } from '../components/common/Badge';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { 
  ShieldAlert, Plus, Phone, Clock, Search, Filter, 
  Calendar, DollarSign, AlertCircle, Eye, UserCheck, Briefcase
} from 'lucide-react';

export const StaffPage: React.FC = () => {
  const { hasAnyRole } = useAuth();
  const toast = useToast();
  const [staffList, setStaffList] = useState<Staff[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedRole, setSelectedRole] = useState('ALL');

  const canManage = hasAnyRole(['SUPER_ADMIN', 'SOCIETY_ADMIN']);

  // Add Staff Modal
  const [showModal, setShowModal] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [fullName, setFullName] = useState('');
  const [phone, setPhone] = useState('');
  const [role, setRole] = useState('SECURITY');
  const [joiningDate, setJoiningDate] = useState(new Date().toISOString().split('T')[0]);
  const [salary, setSalary] = useState('');
  const [emergencyContact, setEmergencyContact] = useState('');
  const [shiftTiming, setShiftTiming] = useState('Morning (06:00 - 14:00)');
  const [agencyName, setAgencyName] = useState('');

  // View Staff Details Modal
  const [detailStaff, setDetailStaff] = useState<Staff | null>(null);

  useEffect(() => {
    fetchStaff();
  }, []);

  const fetchStaff = async () => {
    try {
      setLoading(true);
      const res = await staffApi.getAllStaff();
      if (res && res.data) {
        setStaffList(res.data.content || []);
      } else if (res && (res as any).content) {
        setStaffList((res as any).content);
      } else if (Array.isArray(res)) {
        setStaffList(res as any);
      }
    } catch (err) {
      console.error('Failed to load staff list', err);
      toast.error('Network Error', 'Unable to retrieve staff roster');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateStaff = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!fullName.trim() || !phone.trim()) {
      toast.error('Validation Error', 'Full Name and Phone are required');
      return;
    }

    try {
      setSubmitting(true);
      await staffApi.createStaff({
        fullName: fullName.trim(),
        phone: phone.trim(),
        role,
        joiningDate: joiningDate || undefined,
        salary: salary ? parseFloat(salary) : undefined,
        emergencyContact: emergencyContact.trim() || undefined,
        agencyName: agencyName.trim() || undefined,
        shiftTiming: shiftTiming.trim() || undefined,
      });
      toast.success('Staff Enrolled', `${fullName} has been registered to society roster.`);
      setShowModal(false);
      setFullName('');
      setPhone('');
      setSalary('');
      setEmergencyContact('');
      setAgencyName('');
      fetchStaff();
    } catch (err: any) {
      const msg = err?.response?.data?.message || 'Failed to register staff member';
      toast.error('Registration Failed', msg);
    } finally {
      setSubmitting(false);
    }
  };

  const filteredStaff = useMemo(() => {
    return staffList.filter((s) => {
      const matchSearch =
        s.fullName.toLowerCase().includes(searchQuery.toLowerCase()) ||
        s.phone.includes(searchQuery);
      const matchRole = selectedRole === 'ALL' || s.role === selectedRole;
      return matchSearch && matchRole;
    });
  }, [staffList, searchQuery, selectedRole]);

  return (
    <div className="space-y-6 animate-fadeIn text-slate-900">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 flex items-center gap-2">
            <ShieldAlert className="w-7 h-7 text-indigo-600" /> Society Staff & Security Roster
          </h1>
          <p className="text-xs text-slate-500 mt-1 font-medium">
            Manage security guards, housekeeping, electricians, and facility maintenance personnel
          </p>
        </div>
        {canManage && (
          <button
            onClick={() => setShowModal(true)}
            className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-bold uppercase tracking-wider flex items-center gap-2 transition shadow-md shadow-indigo-600/20"
          >
            <Plus className="w-4 h-4" /> Register New Staff
          </button>
        )}
      </div>

      {/* Filters and Search Bar */}
      <div className="bg-white border border-slate-200 rounded-2xl p-4 shadow-sm flex flex-col md:flex-row gap-4 items-center justify-between">
        <div className="relative w-full md:w-80">
          <Search className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            type="text"
            placeholder="Search by name or phone..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full bg-slate-50 border border-slate-200 rounded-xl pl-10 pr-4 py-2 text-xs font-medium text-slate-900 focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
          />
        </div>

        <div className="flex items-center gap-3 w-full md:w-auto">
          <div className="flex items-center gap-2 text-xs font-bold text-slate-500 uppercase tracking-wider">
            <Filter className="w-3.5 h-3.5" /> Role:
          </div>
          <select
            value={selectedRole}
            onChange={(e) => setSelectedRole(e.target.value)}
            className="bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 text-xs font-semibold text-slate-700 focus:outline-none"
          >
            <option value="ALL">All Roles</option>
            <option value="SECURITY">Security Guard</option>
            <option value="CLEANER">Housekeeping / Cleaner</option>
            <option value="ELECTRICIAN">Electrician</option>
            <option value="PLUMBER">Plumber</option>
            <option value="GARDENER">Gardener</option>
            <option value="MAINTENANCE">Facility Manager</option>
          </select>
        </div>
      </div>

      {/* Staff Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {loading ? (
          <div className="col-span-3 text-center text-slate-400 py-12">Loading staff roster...</div>
        ) : filteredStaff.length === 0 ? (
          <div className="col-span-3 text-center text-slate-500 py-12 bg-white rounded-2xl border border-slate-200">
            <Briefcase className="w-10 h-10 text-slate-300 mx-auto mb-2" />
            <p className="font-semibold">No staff records match your criteria.</p>
          </div>
        ) : (
          filteredStaff.map((s) => (
            <div key={s.id} className="bg-white border border-slate-200 rounded-2xl p-5 shadow-sm hover:shadow-md transition flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between mb-3">
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-xl bg-indigo-50 border border-indigo-200 text-indigo-700 flex items-center justify-center font-bold text-sm">
                      {(s.fullName || 'S').charAt(0).toUpperCase()}
                    </div>
                    <div>
                      <h3 className="font-extrabold text-slate-900 text-sm">{s.fullName}</h3>
                      <div className="mt-0.5">
                        <Badge variant="info">{s.role}</Badge>
                      </div>
                    </div>
                  </div>
                  <Badge variant={s.active !== false ? 'success' : 'secondary'}>
                    {s.active !== false ? 'Active' : 'Inactive'}
                  </Badge>
                </div>

                <div className="space-y-2 text-xs text-slate-600 pt-3 border-t border-slate-100">
                  <div className="flex items-center gap-2 text-slate-900 font-mono font-medium">
                    <Phone className="w-3.5 h-3.5 text-slate-400" /> {s.phone}
                  </div>
                  {s.shiftTiming && (
                    <div className="flex items-center gap-2">
                      <Clock className="w-3.5 h-3.5 text-slate-400" /> {s.shiftTiming}
                    </div>
                  )}
                  {s.agencyName && (
                    <div className="text-[11px] text-slate-500 font-medium">Agency: {s.agencyName}</div>
                  )}
                </div>
              </div>

              <div className="pt-4 mt-3 border-t border-slate-100 flex justify-end">
                <button
                  onClick={() => setDetailStaff(s)}
                  className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-lg text-xs font-bold transition"
                >
                  <Eye className="w-3.5 h-3.5" /> View Profile
                </button>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Staff Detail Modal */}
      {detailStaff && (
        <Modal isOpen={!!detailStaff} onClose={() => setDetailStaff(null)} title="Staff Member Profile">
          <div className="space-y-4 text-slate-900">
            <div className="flex items-center gap-4 p-4 bg-slate-50 rounded-2xl border border-slate-200">
              <div className="w-14 h-14 rounded-2xl bg-indigo-600 text-white flex items-center justify-center font-bold text-xl">
                {(detailStaff.fullName || 'S').charAt(0).toUpperCase()}
              </div>
              <div>
                <h3 className="text-lg font-bold text-slate-900">{detailStaff.fullName}</h3>
                <div className="flex items-center gap-2 mt-1">
                  <Badge variant="info">{detailStaff.role}</Badge>
                  <Badge variant={detailStaff.active !== false ? 'success' : 'secondary'}>
                    {detailStaff.active !== false ? 'Active Employee' : 'Inactive'}
                  </Badge>
                </div>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3 text-xs">
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                <div className="text-[10px] text-slate-400 font-bold uppercase">Phone Number</div>
                <div className="font-mono font-bold text-slate-800 mt-0.5">{detailStaff.phone}</div>
              </div>
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                <div className="text-[10px] text-slate-400 font-bold uppercase">Emergency Contact</div>
                <div className="font-mono font-bold text-slate-800 mt-0.5">
                  {detailStaff.emergencyContact || 'Not Specified'}
                </div>
              </div>
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                <div className="text-[10px] text-slate-400 font-bold uppercase">Monthly Salary</div>
                <div className="font-bold text-slate-800 mt-0.5">
                  {detailStaff.salary != null ? `₹${detailStaff.salary.toLocaleString()}` : 'Confidential'}
                </div>
              </div>
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                <div className="text-[10px] text-slate-400 font-bold uppercase">Shift Schedule</div>
                <div className="font-medium text-slate-800 mt-0.5">{detailStaff.shiftTiming || 'General Shift'}</div>
              </div>
            </div>

            <div className="flex justify-end pt-2">
              <button
                onClick={() => setDetailStaff(null)}
                className="px-5 py-2 bg-slate-200 hover:bg-slate-300 text-slate-800 font-bold text-xs uppercase tracking-wider rounded-xl transition"
              >
                Close
              </button>
            </div>
          </div>
        </Modal>
      )}

      {/* Add Staff Modal */}
      <Modal isOpen={showModal} onClose={() => setShowModal(false)} title="Register Staff Member">
        <form onSubmit={handleCreateStaff} className="space-y-4 text-slate-900">
          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
              Full Name *
            </label>
            <input
              type="text"
              required
              placeholder="e.g. Bahadur Singh"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Phone Number *
              </label>
              <input
                type="text"
                required
                placeholder="9876543210"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-mono"
              />
            </div>
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Staff Role *
              </label>
              <select
                value={role}
                onChange={(e) => setRole(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
              >
                <option value="SECURITY">Security Guard</option>
                <option value="CLEANER">Housekeeping / Cleaner</option>
                <option value="GARDENER">Gardener</option>
                <option value="ELECTRICIAN">Electrician</option>
                <option value="PLUMBER">Plumber</option>
                <option value="MAINTENANCE">Facility Manager</option>
              </select>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Joining Date
              </label>
              <input
                type="date"
                value={joiningDate}
                onChange={(e) => setJoiningDate(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
              />
            </div>
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Monthly Salary (₹)
              </label>
              <input
                type="number"
                min="0"
                placeholder="e.g. 18000"
                value={salary}
                onChange={(e) => setSalary(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Shift Timing
              </label>
              <input
                type="text"
                placeholder="e.g. Day Shift (08:00 - 20:00)"
                value={shiftTiming}
                onChange={(e) => setShiftTiming(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
              />
            </div>
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Emergency Contact
              </label>
              <input
                type="text"
                placeholder="e.g. 9811223344"
                value={emergencyContact}
                onChange={(e) => setEmergencyContact(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-mono"
              />
            </div>
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
              Agency Name (Optional)
            </label>
            <input
              type="text"
              placeholder="e.g. Security Solutions Pvt Ltd"
              value={agencyName}
              onChange={(e) => setAgencyName(e.target.value)}
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
            />
          </div>

          <button
            type="submit"
            disabled={submitting}
            className="w-full py-3 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 text-white font-extrabold text-xs uppercase tracking-wider rounded-xl transition shadow-md"
          >
            {submitting ? 'Registering...' : 'Save Staff Member'}
          </button>
        </form>
      </Modal>
    </div>
  );
};
