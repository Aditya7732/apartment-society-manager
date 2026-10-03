import React, { useEffect, useState } from 'react';
import { visitorApi } from '../api/visitorApi';
import { flatApi } from '../api/flatApi';
import { Visitor, Flat } from '../types';
import { StatCard } from '../components/common/StatCard';
import { Modal } from '../components/common/Modal';
import { Badge } from '../components/common/Badge';
import {
  ShieldAlert,
  UserCheck,
  LogOut,
  Plus,
  CheckCircle2,
} from 'lucide-react';

export const SecurityDashboard: React.FC = () => {
  const [activeVisitors, setActiveVisitors] = useState<Visitor[]>([]);
  const [loading, setLoading] = useState(true);

  // Gate Check In Modal
  const [showCheckInModal, setShowCheckInModal] = useState(false);
  const [flats, setFlats] = useState<Flat[]>([]);
  const [selectedFlatId, setSelectedFlatId] = useState<string>('');
  const [visitorName, setVisitorName] = useState('');
  const [phone, setPhone] = useState('');
  const [purpose, setPurpose] = useState('GUEST');
  const [vehicleNo, setVehicleNo] = useState('');
  const [badgeNo, setBadgeNo] = useState('');
  const [actionId, setActionId] = useState<string | number | null>(null);

  useEffect(() => {
    fetchActiveVisitors();
    fetchFlats();
  }, []);

  const fetchActiveVisitors = async () => {
    try {
      const response = await visitorApi.getActiveVisitors();
      if (Array.isArray(response)) {
        setActiveVisitors(response);
      } else if (response && (response as any).content && Array.isArray((response as any).content)) {
        setActiveVisitors((response as any).content);
      } else if (response && (response as any).data) {
        const d = (response as any).data;
        setActiveVisitors(Array.isArray(d) ? d : d.content || []);
      }
    } catch (err) {
      console.error('Failed to load active visitors', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchFlats = async () => {
    try {
      const res = await flatApi.getAllFlats({ size: 100 });
      if (res && res.content) {
        setFlats(res.content);
      } else if (res && (res as any).data && (res as any).data.content) {
        setFlats((res as any).data.content);
      } else if (Array.isArray(res)) {
        setFlats(res as any);
      }
    } catch (err) {
      console.error('Failed to load flats', err);
    }
  };

  const handleCheckIn = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedFlatId) return;

    try {
      await visitorApi.gateCheckIn({
        flatId: String(selectedFlatId),
        visitorName,
        phone,
        purpose,
        vehicleNumber: vehicleNo || undefined,
        badgeNumber: badgeNo || undefined,
      });
      setShowCheckInModal(false);
      setVisitorName('');
      setPhone('');
      setVehicleNo('');
      setBadgeNo('');
      fetchActiveVisitors();
    } catch (err) {
      alert('Failed to check in visitor');
    }
  };

  const handleCheckOut = async (id: string | number) => {
    setActionId(id);
    try {
      await visitorApi.checkOut(id);
      await fetchActiveVisitors();
    } catch (err) {
      alert('Failed to check out visitor');
    } finally {
      setActionId(null);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <div className="w-10 h-10 border-4 border-amber-200 border-t-amber-600 rounded-full animate-spin" />
      </div>
    );
  }

  return (
    <div className="space-y-8 animate-fadeIn text-slate-900">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 flex items-center gap-2">
            <ShieldAlert className="w-7 h-7 text-amber-600" /> Security Gate Desk
          </h1>
          <p className="text-xs text-slate-500 mt-1">Real-time gate check-ins, visitor entry & exit logs</p>
        </div>
        <button
          onClick={() => setShowCheckInModal(true)}
          className="px-5 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold uppercase tracking-wider flex items-center gap-2 transition shadow-md shadow-emerald-600/20"
        >
          <Plus className="w-4 h-4" /> New Gate Check-In
        </button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
        <StatCard
          title="Visitors Inside Premises"
          value={activeVisitors.length}
          subtitle="Currently inside society"
          icon={UserCheck}
          color="emerald"
        />
        <StatCard
          title="Security Desk Status"
          value="ACTIVE"
          subtitle="Gate 1 Operational"
          icon={ShieldAlert}
          color="indigo"
        />
      </div>

      {/* Active Visitors Table */}
      <div className="bg-white border border-slate-200 rounded-3xl p-6 shadow-sm">
        <h3 className="text-base font-bold text-slate-900 mb-4">
          Live Visitor Log (Currently Inside)
        </h3>

        {activeVisitors.length === 0 ? (
          <div className="p-8 text-center text-slate-500 text-xs bg-slate-50 rounded-2xl border border-slate-200">
            No visitors inside the premises at this moment.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs text-slate-700">
              <thead className="bg-slate-50 text-slate-500 uppercase tracking-wider font-extrabold border-b border-slate-200">
                <tr>
                  <th className="px-4 py-3.5">Visitor Name</th>
                  <th className="px-4 py-3.5">Destination Flat</th>
                  <th className="px-4 py-3.5">Phone</th>
                  <th className="px-4 py-3.5">Purpose</th>
                  <th className="px-4 py-3.5">Check-In Time</th>
                  <th className="px-4 py-3.5">Status</th>
                  <th className="px-4 py-3.5 text-right">Gate Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {activeVisitors.map((v) => {
                  const checkIn = v.checkInTime || v.actualArrival || v.expectedArrival;
                  return (
                    <tr key={v.id} className="hover:bg-slate-50 transition">
                      <td className="px-4 py-3.5 font-bold text-slate-900">{v.visitorName}</td>
                      <td className="px-4 py-3.5 font-semibold text-slate-800">
                        Flat {v.flatNumber || v.flatId} {v.buildingName ? `(${v.buildingName})` : ''}
                      </td>
                      <td className="px-4 py-3.5 font-mono text-slate-600">{v.phone || v.phoneNumber || 'N/A'}</td>
                      <td className="px-4 py-3.5"><Badge variant="info">{v.purpose}</Badge></td>
                      <td className="px-4 py-3.5 text-slate-500 font-mono">
                        {checkIn ? new Date(checkIn).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : 'Just Now'}
                      </td>
                      <td className="px-4 py-3.5">
                        <Badge variant="warning">INSIDE PREMISES</Badge>
                      </td>
                      <td className="px-4 py-3.5 text-right">
                        <button
                          onClick={() => handleCheckOut(v.id)}
                          disabled={actionId === v.id}
                          className="px-3 py-1.5 bg-rose-50 hover:bg-rose-100 text-rose-700 border border-rose-200 rounded-xl font-bold text-xs transition inline-flex items-center gap-1 shadow-sm disabled:opacity-50"
                        >
                          <LogOut className="w-3.5 h-3.5" />
                          {actionId === v.id ? 'Checking Out...' : 'Check-Out'}
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

      {/* New Gate Check-In Modal */}
      <Modal
        isOpen={showCheckInModal}
        onClose={() => setShowCheckInModal(false)}
        title="Gate Security Visitor Entry Check-In"
      >
        <form onSubmit={handleCheckIn} className="space-y-4 text-slate-900">
          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
              Destination Flat
            </label>
            <select
              required
              value={selectedFlatId}
              onChange={(e) => setSelectedFlatId(e.target.value)}
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
            >
              <option value="">-- Select Target Flat --</option>
              {flats.map((f) => (
                <option key={f.id} value={f.id}>
                  Flat {f.flatNumber} ({f.buildingName || 'Tower A'})
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
              Visitor Full Name
            </label>
            <input
              type="text"
              required
              value={visitorName}
              onChange={(e) => setVisitorName(e.target.value)}
              placeholder="Full Name"
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
            />
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
              Phone Number
            </label>
            <input
              type="tel"
              required
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              placeholder="+91 9876543210"
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-mono"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Purpose
              </label>
              <select
                value={purpose}
                onChange={(e) => setPurpose(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
              >
                <option value="GUEST">Guest / Relative</option>
                <option value="DELIVERY">Delivery / Courier</option>
                <option value="CAB">Cab / Uber / Ola</option>
                <option value="SERVICE">Service Technician</option>
                <option value="MAINTENANCE">Maintenance Worker</option>
              </select>
            </div>
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Vehicle No. (Optional)
              </label>
              <input
                type="text"
                value={vehicleNo}
                onChange={(e) => setVehicleNo(e.target.value)}
                placeholder="e.g. MH-12-AB-1234"
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-mono"
              />
            </div>
          </div>

          <button
            type="submit"
            className="w-full py-3 bg-emerald-600 hover:bg-emerald-700 text-white font-extrabold text-xs uppercase tracking-wider rounded-xl transition shadow-md"
          >
            Authorize Entry & Check-In
          </button>
        </form>
      </Modal>
    </div>
  );
};
