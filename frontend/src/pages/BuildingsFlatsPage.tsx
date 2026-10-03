import React, { useEffect, useState, useMemo } from 'react';
import { buildingApi } from '../api/buildingApi';
import { flatApi } from '../api/flatApi';
import { Building, Flat } from '../types';
import { Modal } from '../components/common/Modal';
import { Badge } from '../components/common/Badge';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import {
  Building2, Home, Plus, Search, Filter, Eye,
  Layers, Maximize2, UserCheck, Loader2
} from 'lucide-react';

export const BuildingsFlatsPage: React.FC = () => {
  const { hasAnyRole } = useAuth();
  const toast = useToast();
  const [buildings, setBuildings] = useState<Building[]>([]);
  const [flats, setFlats] = useState<Flat[]>([]);
  const [loading, setLoading] = useState(true);

  // Filters
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedBuilding, setSelectedBuilding] = useState<string>('ALL');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');

  const canManage = hasAnyRole(['SUPER_ADMIN', 'SOCIETY_ADMIN']);

  // New Building Modal
  const [showBuildingModal, setShowBuildingModal] = useState(false);
  const [submittingB, setSubmittingB] = useState(false);
  const [bName, setBName] = useState('');
  const [bFloors, setBFloors] = useState(5);

  // New Flat Modal
  const [showFlatModal, setShowFlatModal] = useState(false);
  const [submittingF, setSubmittingF] = useState(false);
  const [fNumber, setFNumber] = useState('');
  const [fFloor, setFFloor] = useState(1);
  const [fBhk, setFBhk] = useState(2);
  const [fSqft, setFSqft] = useState(1200);
  const [fBuildingId, setFBuildingId] = useState<string>('');

  // Flat Detail Modal
  const [detailFlat, setDetailFlat] = useState<Flat | null>(null);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [bRes, fRes] = await Promise.all([
        buildingApi.getAllBuildings(),
        flatApi.getAllFlats({ size: 100 }),
      ]);
      if (Array.isArray(bRes)) {
        setBuildings(bRes);
      } else if (bRes && (bRes as any).success && (bRes as any).data) {
        setBuildings((bRes as any).data);
      }

      if (fRes && fRes.content) {
        setFlats(fRes.content);
      } else if (fRes && (fRes as any).data && (fRes as any).data.content) {
        setFlats((fRes as any).data.content);
      } else if (Array.isArray(fRes)) {
        setFlats(fRes as any);
      }
    } catch (err) {
      console.error('Failed to load buildings & flats', err);
      toast.error('Network Error', 'Unable to retrieve building and flat records');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateBuilding = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!bName.trim()) {
      toast.error('Validation Error', 'Building block name is required');
      return;
    }

    try {
      setSubmittingB(true);
      const floorsNum = Number(bFloors) || 1;
      const calculatedCode = 'BLK-' + bName.trim().toUpperCase().replace(/[^A-Z0-9]/g, '').slice(0, 8);
      await buildingApi.createBuilding({
        name: bName.trim(),
        code: calculatedCode || 'BLK-' + Date.now(),
        totalFloors: floorsNum,
        totalFlats: floorsNum * 4,
      });
      toast.success('Building Block Added', `Block ${bName} created successfully.`);
      setShowBuildingModal(false);
      setBName('');
      fetchData();
    } catch (err: any) {
      toast.error('Creation Failed', err?.response?.data?.message || 'Failed to create building block');
    } finally {
      setSubmittingB(false);
    }
  };

  const handleCreateFlat = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!fBuildingId) {
      toast.error('Validation Error', 'Please select a parent building block');
      return;
    }
    if (!fNumber.trim()) {
      toast.error('Validation Error', 'Flat number is required');
      return;
    }

    try {
      setSubmittingF(true);
      await flatApi.createFlat({
        buildingId: String(fBuildingId),
        flatNumber: fNumber.trim(),
        floorNumber: Number(fFloor),
        flatType: `${fBhk}BHK`,
        bhkType: `${fBhk} BHK`,
        areaSqft: Number(fSqft),
        squareFeet: Number(fSqft),
      });
      toast.success('Flat Added', `Flat ${fNumber} enrolled into the society unit directory.`);
      setShowFlatModal(false);
      setFNumber('');
      fetchData();
    } catch (err: any) {
      toast.error('Creation Failed', err?.response?.data?.message || 'Failed to create flat unit');
    } finally {
      setSubmittingF(false);
    }
  };

  const filteredFlats = useMemo(() => {
    return flats.filter((f) => {
      const q = searchQuery.toLowerCase();
      const matchSearch =
        f.flatNumber.toLowerCase().includes(q) ||
        (f.buildingName && f.buildingName.toLowerCase().includes(q));

      const matchBuilding =
        selectedBuilding === 'ALL' ||
        f.buildingId === selectedBuilding ||
        f.buildingName === selectedBuilding;

      const occStatus = f.occupancyStatus || 'VACANT';
      const matchStatus = statusFilter === 'ALL' || occStatus === statusFilter;

      return matchSearch && matchBuilding && matchStatus;
    });
  }, [flats, searchQuery, selectedBuilding, statusFilter]);

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <div className="w-10 h-10 border-4 border-indigo-200 border-t-indigo-600 rounded-full animate-spin" />
      </div>
    );
  }

  return (
    <div className="space-y-6 animate-fadeIn text-slate-900">
      {/* Header Banner */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 flex items-center gap-2">
            <Building2 className="w-7 h-7 text-indigo-600" /> Society Wings & Flat Inventory
          </h1>
          <p className="text-xs text-slate-500 mt-1 font-medium">
            Manage society blocks, wings, floor plans, and unit occupancy status
          </p>
        </div>
        {canManage && (
          <div className="flex items-center gap-3">
            <button
              onClick={() => setShowBuildingModal(true)}
              className="px-4 py-2.5 bg-slate-100 hover:bg-slate-200 text-slate-800 border border-slate-300 rounded-xl text-xs font-bold flex items-center gap-2 transition"
            >
              <Building2 className="w-4 h-4 text-indigo-600" /> Add Building Block
            </button>
            <button
              onClick={() => setShowFlatModal(true)}
              className="px-4 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-bold uppercase tracking-wider flex items-center gap-2 transition shadow-md shadow-indigo-600/20"
            >
              <Plus className="w-4 h-4" /> Add Flat Unit
            </button>
          </div>
        )}
      </div>

      {/* Buildings Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
        {buildings.map((b) => (
          <div key={b.id} className="bg-white border border-slate-200 rounded-2xl p-5 shadow-sm hover:shadow-md transition">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="p-3 bg-indigo-50 border border-indigo-200 rounded-xl text-indigo-700 font-bold">
                  <Building2 className="w-6 h-6" />
                </div>
                <div>
                  <h3 className="font-extrabold text-slate-900 text-base">Block {b.name}</h3>
                  <p className="text-xs text-slate-500 font-medium">{b.totalFloors} Floors</p>
                </div>
              </div>
              <Badge variant="info">{b.totalFlats || 0} Units</Badge>
            </div>
          </div>
        ))}
      </div>

      {/* Search and Filters Bar */}
      <div className="bg-white border border-slate-200 rounded-2xl p-4 shadow-sm flex flex-col md:flex-row gap-4 items-center justify-between">
        <div className="relative w-full md:w-80">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Search flat number or block..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full bg-slate-50 border border-slate-200 rounded-xl pl-10 pr-4 py-2 text-xs text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500/20 font-medium"
          />
        </div>

        <div className="flex flex-wrap items-center gap-3 w-full md:w-auto">
          <div className="flex items-center gap-2">
            <span className="text-xs font-bold text-slate-500 uppercase tracking-wider">Block:</span>
            <select
              value={selectedBuilding}
              onChange={(e) => setSelectedBuilding(e.target.value)}
              className="bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 text-xs font-semibold text-slate-700 focus:outline-none"
            >
              <option value="ALL">All Blocks</option>
              {buildings.map((b) => (
                <option key={b.id} value={b.name}>
                  Block {b.name}
                </option>
              ))}
            </select>
          </div>

          <div className="flex items-center gap-2">
            <span className="text-xs font-bold text-slate-500 uppercase tracking-wider">Status:</span>
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 text-xs font-semibold text-slate-700 focus:outline-none"
            >
              <option value="ALL">All Status</option>
              <option value="OCCUPIED">Occupied</option>
              <option value="VACANT">Vacant</option>
            </select>
          </div>
        </div>
      </div>

      {/* Flat Table */}
      <div className="bg-white border border-slate-200 rounded-2xl shadow-sm overflow-hidden">
        <div className="p-4 border-b border-slate-200 flex items-center justify-between">
          <h3 className="text-sm font-extrabold text-slate-900 flex items-center gap-2">
            <Home className="w-4 h-4 text-indigo-600" /> Society Flat Directory ({filteredFlats.length} Units)
          </h3>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-700">
            <thead className="bg-slate-50 text-slate-500 uppercase tracking-wider font-extrabold border-b border-slate-200">
              <tr>
                <th className="px-5 py-3.5">Flat No</th>
                <th className="px-5 py-3.5">Building Block</th>
                <th className="px-5 py-3.5">Floor Level</th>
                <th className="px-5 py-3.5">BHK Plan</th>
                <th className="px-5 py-3.5">Carpet Area</th>
                <th className="px-5 py-3.5">Occupancy</th>
                <th className="px-5 py-3.5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {filteredFlats.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-5 py-12 text-center text-slate-500">
                    <Home className="w-8 h-8 text-slate-300 mx-auto mb-2" />
                    No flats match your filter criteria.
                  </td>
                </tr>
              ) : (
                filteredFlats.map((f) => {
                  const isVacant = f.occupancyStatus === 'VACANT';
                  return (
                    <tr key={f.id} className="hover:bg-slate-50 transition">
                      <td className="px-5 py-3.5 font-bold text-slate-900">{f.flatNumber}</td>
                      <td className="px-5 py-3.5 font-medium">{f.buildingName ? `Block ${f.buildingName}` : '-'}</td>
                      <td className="px-5 py-3.5">Floor {f.floorNumber}</td>
                      <td className="px-5 py-3.5 font-semibold text-indigo-700">{f.bhkType || f.flatType || '2 BHK'}</td>
                      <td className="px-5 py-3.5 font-mono text-slate-600">{f.squareFeet || f.areaSqft || 1200} sq ft</td>
                      <td className="px-5 py-3.5">
                        <Badge variant={isVacant ? 'secondary' : 'success'}>
                          {f.occupancyStatus || 'VACANT'}
                        </Badge>
                      </td>
                      <td className="px-5 py-3.5 text-right">
                        <button
                          onClick={() => setDetailFlat(f)}
                          className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-lg text-xs font-bold transition"
                        >
                          <Eye className="w-3.5 h-3.5" /> Details
                        </button>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Flat Detail Modal */}
      {detailFlat && (
        <Modal isOpen={!!detailFlat} onClose={() => setDetailFlat(null)} title={`Flat Unit Profile: ${detailFlat.flatNumber}`}>
          <div className="space-y-4 text-slate-900">
            <div className="p-4 bg-slate-50 rounded-2xl border border-slate-200 flex items-center justify-between">
              <div>
                <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Unit Identifier</span>
                <div className="text-xl font-extrabold text-slate-900 mt-0.5">Flat {detailFlat.flatNumber}</div>
                <div className="text-xs text-slate-500 mt-0.5">
                  Block {detailFlat.buildingName || 'A'} • Floor {detailFlat.floorNumber}
                </div>
              </div>
              <Badge variant={detailFlat.occupancyStatus === 'VACANT' ? 'secondary' : 'success'}>
                {detailFlat.occupancyStatus || 'VACANT'}
              </Badge>
            </div>

            <div className="grid grid-cols-2 gap-3 text-xs">
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                <span className="text-[10px] text-slate-400 font-bold uppercase">Configuration</span>
                <div className="font-extrabold text-indigo-700 mt-0.5">{detailFlat.bhkType || detailFlat.flatType || '2 BHK'}</div>
              </div>
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                <span className="text-[10px] text-slate-400 font-bold uppercase">Super Built-up Area</span>
                <div className="font-mono font-bold text-slate-800 mt-0.5">{detailFlat.squareFeet || detailFlat.areaSqft || 1200} Sq. Ft.</div>
              </div>
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                <span className="text-[10px] text-slate-400 font-bold uppercase">Current Occupant</span>
                <div className="font-bold text-slate-800 mt-0.5">{detailFlat.currentResidentName || 'No Active Tenant / Owner Assigned'}</div>
              </div>
              <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                <span className="text-[10px] text-slate-400 font-bold uppercase">Parking Bay</span>
                <div className="font-mono font-bold text-slate-800 mt-0.5">Assigned (Standard Slot)</div>
              </div>
            </div>

            <div className="flex justify-end pt-2 border-t border-slate-200">
              <button
                onClick={() => setDetailFlat(null)}
                className="px-5 py-2 bg-slate-200 hover:bg-slate-300 text-slate-800 font-bold text-xs uppercase tracking-wider rounded-xl transition"
              >
                Close
              </button>
            </div>
          </div>
        </Modal>
      )}

      {/* Add Building Modal */}
      <Modal isOpen={showBuildingModal} onClose={() => setShowBuildingModal(false)} title="Register Building Block">
        <form onSubmit={handleCreateBuilding} className="space-y-4 text-slate-900">
          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
              Block / Wing Name *
            </label>
            <input
              type="text"
              required
              placeholder="e.g. Block C / Wing Tulip"
              value={bName}
              onChange={(e) => setBName(e.target.value)}
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium focus:outline-none focus:border-indigo-500"
            />
          </div>
          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
              Total Floors *
            </label>
            <input
              type="number"
              required
              min="1"
              max="100"
              value={bFloors}
              onChange={(e) => setBFloors(Number(e.target.value))}
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium focus:outline-none focus:border-indigo-500"
            />
          </div>
          <button
            type="submit"
            disabled={submittingB}
            className="w-full py-3 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 text-white font-extrabold text-xs uppercase tracking-wider rounded-xl transition shadow-md flex items-center justify-center gap-2"
          >
            {submittingB ? <Loader2 className="w-4 h-4 animate-spin" /> : null}
            {submittingB ? 'Registering...' : 'Save Building Block'}
          </button>
        </form>
      </Modal>

      {/* Add Flat Modal */}
      <Modal isOpen={showFlatModal} onClose={() => setShowFlatModal(false)} title="Register Flat Unit">
        <form onSubmit={handleCreateFlat} className="space-y-4 text-slate-900">
          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
              Building Block *
            </label>
            <select
              required
              value={fBuildingId}
              onChange={(e) => setFBuildingId(e.target.value)}
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium focus:outline-none focus:border-indigo-500"
            >
              <option value="">-- Select Target Block --</option>
              {buildings.map((b) => (
                <option key={b.id} value={b.id}>
                  Block {b.name} ({b.totalFloors} floors)
                </option>
              ))}
            </select>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Flat Number *
              </label>
              <input
                type="text"
                required
                placeholder="e.g. C-104"
                value={fNumber}
                onChange={(e) => setFNumber(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium focus:outline-none focus:border-indigo-500"
              />
            </div>
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Floor Level *
              </label>
              <input
                type="number"
                required
                min="0"
                value={fFloor}
                onChange={(e) => setFFloor(Number(e.target.value))}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium focus:outline-none focus:border-indigo-500"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                BHK Type *
              </label>
              <select
                value={fBhk}
                onChange={(e) => setFBhk(Number(e.target.value))}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium focus:outline-none focus:border-indigo-500"
              >
                <option value={1}>1 BHK</option>
                <option value={2}>2 BHK</option>
                <option value={3}>3 BHK</option>
                <option value={4}>4 BHK</option>
              </select>
            </div>
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Area (Sq. Ft.) *
              </label>
              <input
                type="number"
                required
                min="100"
                value={fSqft}
                onChange={(e) => setFSqft(Number(e.target.value))}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium focus:outline-none focus:border-indigo-500"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={submittingF}
            className="w-full py-3 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 text-white font-extrabold text-xs uppercase tracking-wider rounded-xl transition shadow-md flex items-center justify-center gap-2"
          >
            {submittingF ? <Loader2 className="w-4 h-4 animate-spin" /> : null}
            {submittingF ? 'Saving Flat...' : 'Save Flat Unit'}
          </button>
        </form>
      </Modal>
    </div>
  );
};
