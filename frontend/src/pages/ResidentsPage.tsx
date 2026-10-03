import React, { useEffect, useState, useMemo } from 'react';
import { residentApi } from '../api/residentApi';
import { flatApi } from '../api/flatApi';
import { Resident, Flat, FamilyMember, Vehicle } from '../types';
import { Modal } from '../components/common/Modal';
import { Badge } from '../components/common/Badge';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import {
  Plus, Phone, Mail, Home, Search, Users, Eye,
  Car, UserPlus, Trash2, Calendar, Shield, AlertCircle,
  Building2, KeyRound, Loader2
} from 'lucide-react';

export const ResidentsPage: React.FC = () => {
  const { hasAnyRole } = useAuth();
  const toast = useToast();
  const [residents, setResidents] = useState<Resident[]>([]);
  const [flats, setFlats] = useState<Flat[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [typeFilter, setTypeFilter] = useState('ALL');

  const canManage = hasAnyRole(['SUPER_ADMIN', 'SOCIETY_ADMIN']);

  // Add Resident Modal
  const [showModal, setShowModal] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [flatId, setFlatId] = useState<string>('');
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [resType, setResType] = useState('OWNER');
  const [emergencyName, setEmergencyName] = useState('');
  const [emergencyPhone, setEmergencyPhone] = useState('');

  // Resident Detail Modal
  const [selectedResident, setSelectedResident] = useState<Resident | null>(null);
  const [activeTab, setActiveTab] = useState<'profile' | 'family' | 'vehicles'>('profile');
  const [loadingDetail, setLoadingDetail] = useState(false);

  // Add Family Member subform
  const [famName, setFamName] = useState('');
  const [famRelation, setFamRelation] = useState('SPOUSE');
  const [famPhone, setFamPhone] = useState('');
  const [addingFam, setAddingFam] = useState(false);

  // Add Vehicle subform
  const [vehNumber, setVehNumber] = useState('');
  const [vehType, setVehType] = useState('FOUR_WHEELER');
  const [vehModel, setVehModel] = useState('');
  const [addingVeh, setAddingVeh] = useState(false);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [rRes, fRes] = await Promise.all([
        residentApi.getAllResidents({ size: 100 }),
        flatApi.getAllFlats({ size: 100 }),
      ]);

      if (rRes && Array.isArray((rRes as any).content)) {
        setResidents((rRes as any).content);
      } else if (rRes && (rRes as any).data && Array.isArray((rRes as any).data.content)) {
        setResidents((rRes as any).data.content);
      } else if (rRes && Array.isArray((rRes as any).data)) {
        setResidents((rRes as any).data);
      } else if (Array.isArray(rRes)) {
        setResidents(rRes as any);
      } else {
        setResidents([]);
      }

      if (fRes && Array.isArray((fRes as any).content)) {
        setFlats((fRes as any).content);
      } else if (fRes && (fRes as any).data && Array.isArray((fRes as any).data.content)) {
        setFlats((fRes as any).data.content);
      } else if (fRes && Array.isArray((fRes as any).data)) {
        setFlats((fRes as any).data);
      } else if (Array.isArray(fRes)) {
        setFlats(fRes as any);
      } else {
        setFlats([]);
      }
    } catch (err) {
      console.error('Failed to load residents', err);
      toast.error('Network Error', 'Unable to retrieve resident records');
      setResidents([]);
      setFlats([]);
    } finally {
      setLoading(false);
    }
  };

  const openDetailModal = async (resident: Resident) => {
    try {
      setLoadingDetail(true);
      setSelectedResident(resident);
      setActiveTab('profile');
      const fullRes = await residentApi.getById(resident.id);
      setSelectedResident(fullRes);
    } catch (err) {
      toast.error('Error', 'Unable to load comprehensive resident profile');
    } finally {
      setLoadingDetail(false);
    }
  };

  const handleCreateResident = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!flatId) {
      toast.error('Validation Error', 'Please select an assigned flat');
      return;
    }

    try {
      setSubmitting(true);
      const nameParts = fullName.trim().split(' ');
      const fName = nameParts[0] || fullName;
      const lName = nameParts.slice(1).join(' ') || 'Resident';
      await residentApi.createResident({
        flatId: String(flatId),
        fullName: fullName.trim(),
        firstName: fName,
        lastName: lName,
        email: email.trim(),
        phone: phone.trim(),
        emergencyContactName: emergencyName.trim() || undefined,
        emergencyContactPhone: emergencyPhone.trim() || undefined,
        moveInDate: new Date().toISOString().split('T')[0],
        isOwner: resType === 'OWNER',
        residentType: resType as any,
      });

      toast.success('Resident Registered', `${fullName} has been assigned to the flat.`);
      setShowModal(false);
      setFullName('');
      setEmail('');
      setPhone('');
      setEmergencyName('');
      setEmergencyPhone('');
      setFlatId('');
      fetchData();
    } catch (err: any) {
      const msg = err?.response?.data?.message || 'Failed to register resident.';
      toast.error('Registration Failed', msg);
    } finally {
      setSubmitting(false);
    }
  };

  const handleAddFamilyMember = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedResident) return;
    try {
      setAddingFam(true);
      await residentApi.addFamilyMember(selectedResident.id, {
        fullName: famName.trim(),
        relation: famRelation,
        phoneNumber: famPhone.trim() || undefined,
      });
      toast.success('Family Member Added', `${famName} added to profile.`);
      setFamName('');
      setFamPhone('');
      const updated = await residentApi.getById(selectedResident.id);
      setSelectedResident(updated);
    } catch (err: any) {
      toast.error('Failed to add family member', err?.response?.data?.message || 'Error occurred');
    } finally {
      setAddingFam(false);
    }
  };

  const handleRemoveFamilyMember = async (memberId: string) => {
    if (!selectedResident) return;
    try {
      await residentApi.removeFamilyMember(memberId);
      toast.success('Removed', 'Family member removed from profile.');
      const updated = await residentApi.getById(selectedResident.id);
      setSelectedResident(updated);
    } catch (err: any) {
      toast.error('Remove Failed', 'Could not remove family member.');
    }
  };

  const handleAddVehicle = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedResident) return;
    try {
      setAddingVeh(true);
      await residentApi.addVehicle(selectedResident.id, {
        vehicleNumber: vehNumber.trim().toUpperCase(),
        vehicleType: vehType,
      });
      toast.success('Vehicle Enrolled', `${vehNumber.toUpperCase()} added to parking registry.`);
      setVehNumber('');
      const updated = await residentApi.getById(selectedResident.id);
      setSelectedResident(updated);
    } catch (err: any) {
      toast.error('Failed to add vehicle', err?.response?.data?.message || 'Error occurred');
    } finally {
      setAddingVeh(false);
    }
  };

  const handleRemoveVehicle = async (vehicleId: string) => {
    if (!selectedResident) return;
    try {
      await residentApi.removeVehicle(vehicleId);
      toast.success('Vehicle Removed', 'Vehicle removed from society register.');
      const updated = await residentApi.getById(selectedResident.id);
      setSelectedResident(updated);
    } catch (err: any) {
      toast.error('Remove Failed', 'Could not remove vehicle.');
    }
  };

  const filteredResidents = useMemo(() => {
    return (residents || []).filter((r) => {
      if (!r) return false;
      const q = (search || '').toLowerCase();
      const matchSearch =
        (r.fullName || '').toLowerCase().includes(q) ||
        (r.flatNumber || '').toLowerCase().includes(q) ||
        (r.email || '').toLowerCase().includes(q) ||
        (r.phone || '').includes(q);

      const typeLabel = r.residentType || (r.isOwner ? 'OWNER' : 'TENANT');
      const matchType = typeFilter === 'ALL' || typeLabel === typeFilter;
      return matchSearch && matchType;
    });
  }, [residents, search, typeFilter]);

  return (
    <div className="space-y-6 animate-fadeIn text-slate-900">
      {/* Header Banner */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 flex items-center gap-2">
            <Users className="w-7 h-7 text-indigo-600" /> Society Resident Directory
          </h1>
          <p className="text-xs text-slate-500 mt-1 font-medium">
            Manage society flat owners, tenant leases, family members, and registered vehicles
          </p>
        </div>
        {canManage && (
          <button
            onClick={() => setShowModal(true)}
            className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-bold uppercase tracking-wider flex items-center gap-2 transition shadow-md shadow-indigo-600/20"
          >
            <Plus className="w-4 h-4" /> Enroll New Resident
          </button>
        )}
      </div>

      {/* Search and Filters Bar */}
      <div className="bg-white border border-slate-200 rounded-2xl p-4 shadow-sm flex flex-col md:flex-row gap-4 items-center justify-between">
        <div className="relative w-full md:w-80">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Search by name, flat, email, phone..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full bg-slate-50 border border-slate-200 rounded-xl pl-10 pr-4 py-2 text-xs text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-indigo-500/20 font-medium"
          />
        </div>

        <div className="flex items-center gap-3 w-full md:w-auto">
          <span className="text-xs font-bold text-slate-500 uppercase tracking-wider">Occupancy:</span>
          <select
            value={typeFilter}
            onChange={(e) => setTypeFilter(e.target.value)}
            className="bg-slate-50 border border-slate-200 rounded-xl px-3 py-2 text-xs font-semibold text-slate-700 focus:outline-none"
          >
            <option value="ALL">All Occupants</option>
            <option value="OWNER">Owners Only</option>
            <option value="TENANT">Tenants Only</option>
          </select>
        </div>
      </div>

      {/* Residents Grid */}
      {loading ? (
        <div className="text-center py-16 text-slate-400">Loading resident directory...</div>
      ) : filteredResidents.length === 0 ? (
        <div className="text-center py-16 bg-white rounded-2xl border border-slate-200 text-slate-500">
          <Users className="w-10 h-10 text-slate-300 mx-auto mb-2" />
          <p className="font-semibold">No residents match your search criteria.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {filteredResidents.map((r) => {
            const typeLabel = r.residentType || (r.isOwner ? 'OWNER' : 'TENANT');
            const name = r.fullName || `${r.firstName || ''} ${r.lastName || ''}`.trim() || 'Resident';
            return (
              <div
                key={r.id}
                className="bg-white border border-slate-200 rounded-2xl p-5 shadow-sm hover:shadow-md transition flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center justify-between mb-3">
                    <div className="flex items-center gap-3">
                      <div className="w-11 h-11 rounded-2xl bg-indigo-50 border border-indigo-200 text-indigo-700 flex items-center justify-center font-extrabold text-base shadow-sm">
                        {name.charAt(0).toUpperCase()}
                      </div>
                      <div>
                        <h3 className="font-extrabold text-slate-900 text-sm">{name}</h3>
                        <div className="mt-0.5">
                          <Badge variant={typeLabel === 'OWNER' ? 'success' : 'info'}>
                            {typeLabel}
                          </Badge>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div className="space-y-2 text-xs text-slate-600 pt-3 border-t border-slate-100">
                    <div className="flex items-center gap-2 text-slate-900 font-bold">
                      <Home className="w-4 h-4 text-indigo-600" /> Flat {r.flatNumber || r.flatId}{' '}
                      {r.buildingName ? `(${r.buildingName})` : ''}
                    </div>
                    <div className="flex items-center gap-2 font-mono text-slate-600">
                      <Phone className="w-4 h-4 text-slate-400" /> {r.phone || 'N/A'}
                    </div>
                    <div className="flex items-center gap-2 truncate text-slate-600">
                      <Mail className="w-4 h-4 text-slate-400" /> {r.email || 'N/A'}
                    </div>
                  </div>
                </div>

                <div className="pt-4 mt-3 border-t border-slate-100 flex justify-end">
                  <button
                    onClick={() => openDetailModal(r)}
                    className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-800 rounded-lg text-xs font-bold transition"
                  >
                    <Eye className="w-3.5 h-3.5" /> View Profile & Assets
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Resident Detail Modal with Tabs */}
      {selectedResident && (
        <Modal
          isOpen={!!selectedResident}
          onClose={() => setSelectedResident(null)}
          title={`Resident Profile: ${selectedResident.fullName || selectedResident.firstName}`}
        >
          <div className="space-y-4 text-slate-900">
            {/* Tabs */}
            <div className="flex border-b border-slate-200 gap-4 text-xs font-bold">
              <button
                onClick={() => setActiveTab('profile')}
                className={`pb-2.5 transition border-b-2 ${
                  activeTab === 'profile'
                    ? 'border-indigo-600 text-indigo-600'
                    : 'border-transparent text-slate-500 hover:text-slate-700'
                }`}
              >
                Profile & Unit
              </button>
              <button
                onClick={() => setActiveTab('family')}
                className={`pb-2.5 transition border-b-2 flex items-center gap-1.5 ${
                  activeTab === 'family'
                    ? 'border-indigo-600 text-indigo-600'
                    : 'border-transparent text-slate-500 hover:text-slate-700'
                }`}
              >
                Family Members ({selectedResident.familyMembers?.length || 0})
              </button>
              <button
                onClick={() => setActiveTab('vehicles')}
                className={`pb-2.5 transition border-b-2 flex items-center gap-1.5 ${
                  activeTab === 'vehicles'
                    ? 'border-indigo-600 text-indigo-600'
                    : 'border-transparent text-slate-500 hover:text-slate-700'
                }`}
              >
                Vehicles ({selectedResident.vehicles?.length || 0})
              </button>
            </div>

            {loadingDetail ? (
              <div className="py-10 text-center text-slate-400">Loading details...</div>
            ) : activeTab === 'profile' ? (
              <div className="space-y-4">
                <div className="flex items-center gap-4 p-4 bg-slate-50 rounded-2xl border border-slate-200">
                  <div className="w-14 h-14 rounded-2xl bg-indigo-600 text-white flex items-center justify-center font-black text-2xl">
                    {(selectedResident.fullName || 'R').charAt(0).toUpperCase()}
                  </div>
                  <div>
                    <h3 className="text-base font-extrabold text-slate-900">{selectedResident.fullName}</h3>
                    <div className="flex items-center gap-2 mt-1">
                      <Badge variant={selectedResident.isOwner ? 'success' : 'info'}>
                        {selectedResident.residentType || (selectedResident.isOwner ? 'OWNER' : 'TENANT')}
                      </Badge>
                      <Badge variant={selectedResident.active !== false ? 'success' : 'secondary'}>
                        {selectedResident.active !== false ? 'Active Resident' : 'Moved Out'}
                      </Badge>
                    </div>
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-3 text-xs">
                  <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                    <span className="text-[10px] text-slate-400 font-bold uppercase">Allocated Unit</span>
                    <div className="font-extrabold text-slate-800 mt-0.5">
                      Flat {selectedResident.flatNumber} {selectedResident.buildingName && `(${selectedResident.buildingName})`}
                    </div>
                  </div>
                  <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                    <span className="text-[10px] text-slate-400 font-bold uppercase">Contact Phone</span>
                    <div className="font-mono font-bold text-slate-800 mt-0.5">{selectedResident.phone || 'N/A'}</div>
                  </div>
                  <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                    <span className="text-[10px] text-slate-400 font-bold uppercase">Email Address</span>
                    <div className="font-bold text-slate-800 mt-0.5">{selectedResident.email || 'N/A'}</div>
                  </div>
                  <div className="p-3 bg-slate-50 rounded-xl border border-slate-200">
                    <span className="text-[10px] text-slate-400 font-bold uppercase">Move-In Date</span>
                    <div className="font-mono font-medium text-slate-800 mt-0.5">{selectedResident.moveInDate || 'N/A'}</div>
                  </div>
                  <div className="p-3 bg-slate-50 rounded-xl border border-slate-200 col-span-2">
                    <span className="text-[10px] text-slate-400 font-bold uppercase">Emergency Contact</span>
                    <div className="font-bold text-slate-800 mt-0.5">
                      {selectedResident.emergencyContactName ? (
                        `${selectedResident.emergencyContactName} (${selectedResident.emergencyContactPhone || 'No Phone'})`
                      ) : (
                        'None Provided'
                      )}
                    </div>
                  </div>
                </div>
              </div>
            ) : activeTab === 'family' ? (
              <div className="space-y-4">
                {/* List */}
                <div className="space-y-2 max-h-48 overflow-y-auto">
                  {(!selectedResident.familyMembers || selectedResident.familyMembers.length === 0) ? (
                    <div className="p-4 bg-slate-50 rounded-xl text-center text-xs text-slate-500">
                      No family members registered under this flat profile.
                    </div>
                  ) : (
                    selectedResident.familyMembers.map((fm) => (
                      <div key={fm.id || fm.fullName} className="p-3 bg-slate-50 rounded-xl border border-slate-200 flex items-center justify-between text-xs">
                        <div>
                          <span className="font-bold text-slate-900">{fm.fullName}</span>
                          <span className="ml-2 text-slate-500 font-medium">({fm.relation})</span>
                          {fm.phoneNumber && <span className="ml-2 font-mono text-slate-400">{fm.phoneNumber}</span>}
                        </div>
                        {canManage && fm.id && (
                          <button
                            onClick={() => handleRemoveFamilyMember(fm.id!)}
                            className="p-1 hover:bg-rose-100 text-rose-600 rounded transition"
                            title="Remove Family Member"
                          >
                            <Trash2 className="w-3.5 h-3.5" />
                          </button>
                        )}
                      </div>
                    ))
                  )}
                </div>

                {/* Add Member Form */}
                {canManage && (
                  <form onSubmit={handleAddFamilyMember} className="p-3 bg-slate-100/70 rounded-xl border border-slate-200 space-y-2">
                    <div className="text-[11px] font-bold text-slate-700 uppercase">Add Family Member</div>
                    <div className="grid grid-cols-3 gap-2">
                      <input
                        type="text"
                        required
                        placeholder="Member Name"
                        value={famName}
                        onChange={(e) => setFamName(e.target.value)}
                        className="bg-white border border-slate-300 rounded-lg px-2.5 py-1.5 text-xs text-slate-900"
                      />
                      <select
                        value={famRelation}
                        onChange={(e) => setFamRelation(e.target.value)}
                        className="bg-white border border-slate-300 rounded-lg px-2.5 py-1.5 text-xs text-slate-900"
                      >
                        <option value="SPOUSE">Spouse</option>
                        <option value="CHILD">Child</option>
                        <option value="PARENT">Parent</option>
                        <option value="SIBLING">Sibling</option>
                        <option value="OTHER">Other</option>
                      </select>
                      <input
                        type="text"
                        placeholder="Phone (Optional)"
                        value={famPhone}
                        onChange={(e) => setFamPhone(e.target.value)}
                        className="bg-white border border-slate-300 rounded-lg px-2.5 py-1.5 text-xs text-slate-900 font-mono"
                      />
                    </div>
                    <button
                      type="submit"
                      disabled={addingFam}
                      className="px-4 py-1.5 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 text-white font-bold text-xs rounded-lg transition"
                    >
                      {addingFam ? 'Saving...' : '+ Add Member'}
                    </button>
                  </form>
                )}
              </div>
            ) : (
              <div className="space-y-4">
                {/* Vehicles list */}
                <div className="space-y-2 max-h-48 overflow-y-auto">
                  {(!selectedResident.vehicles || selectedResident.vehicles.length === 0) ? (
                    <div className="p-4 bg-slate-50 rounded-xl text-center text-xs text-slate-500">
                      No vehicles enrolled for this flat resident.
                    </div>
                  ) : (
                    selectedResident.vehicles.map((v) => (
                      <div key={v.id || v.vehicleNumber} className="p-3 bg-slate-50 rounded-xl border border-slate-200 flex items-center justify-between text-xs">
                        <div className="flex items-center gap-2">
                          <Car className="w-4 h-4 text-indigo-600" />
                          <span className="font-mono font-bold text-slate-900">{v.vehicleNumber}</span>
                          <span className="text-slate-500">({v.vehicleType})</span>
                        </div>
                        {canManage && v.id && (
                          <button
                            onClick={() => handleRemoveVehicle(v.id!)}
                            className="p-1 hover:bg-rose-100 text-rose-600 rounded transition"
                            title="Remove Vehicle"
                          >
                            <Trash2 className="w-3.5 h-3.5" />
                          </button>
                        )}
                      </div>
                    ))
                  )}
                </div>

                {/* Add Vehicle Form */}
                {canManage && (
                  <form onSubmit={handleAddVehicle} className="p-3 bg-slate-100/70 rounded-xl border border-slate-200 space-y-2">
                    <div className="text-[11px] font-bold text-slate-700 uppercase">Register Vehicle</div>
                    <div className="grid grid-cols-2 gap-2">
                      <input
                        type="text"
                        required
                        placeholder="e.g. MH12AB1234"
                        value={vehNumber}
                        onChange={(e) => setVehNumber(e.target.value)}
                        className="bg-white border border-slate-300 rounded-lg px-2.5 py-1.5 text-xs text-slate-900 font-mono"
                      />
                      <select
                        value={vehType}
                        onChange={(e) => setVehType(e.target.value)}
                        className="bg-white border border-slate-300 rounded-lg px-2.5 py-1.5 text-xs text-slate-900"
                      >
                        <option value="FOUR_WHEELER">4-Wheeler (Car)</option>
                        <option value="TWO_WHEELER">2-Wheeler (Bike/Scooter)</option>
                        <option value="BICYCLE">Bicycle</option>
                        <option value="OTHER">Other</option>
                      </select>
                    </div>
                    <button
                      type="submit"
                      disabled={addingVeh}
                      className="px-4 py-1.5 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 text-white font-bold text-xs rounded-lg transition"
                    >
                      {addingVeh ? 'Enrolling...' : '+ Add Vehicle'}
                    </button>
                  </form>
                )}
              </div>
            )}

            <div className="flex justify-end pt-2 border-t border-slate-200">
              <button
                onClick={() => setSelectedResident(null)}
                className="px-5 py-2 bg-slate-200 hover:bg-slate-300 text-slate-800 font-bold text-xs uppercase tracking-wider rounded-xl transition"
              >
                Close
              </button>
            </div>
          </div>
        </Modal>
      )}

      {/* Add Resident Modal */}
      <Modal isOpen={showModal} onClose={() => setShowModal(false)} title="Register Society Resident">
        <form onSubmit={handleCreateResident} className="space-y-4 text-slate-900">
          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
              Assign Flat Unit *
            </label>
            <select
              required
              value={flatId}
              onChange={(e) => setFlatId(e.target.value)}
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium focus:outline-none focus:border-indigo-500"
            >
              <option value="">-- Select Target Flat --</option>
              {flats.map((f) => (
                <option key={f.id} value={f.id}>
                  Flat {f.flatNumber} (Block {f.buildingName || f.buildingId}) - {f.occupancyStatus || 'VACANT'}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
              Full Name *
            </label>
            <input
              type="text"
              required
              placeholder="e.g. Ramesh Kumar"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium focus:outline-none focus:border-indigo-500"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Email Address
              </label>
              <input
                type="email"
                placeholder="ramesh@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium focus:outline-none focus:border-indigo-500"
              />
            </div>
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
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-mono focus:outline-none focus:border-indigo-500"
              />
            </div>
          </div>

          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
              Occupancy Role *
            </label>
            <select
              value={resType}
              onChange={(e) => setResType(e.target.value)}
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium focus:outline-none focus:border-indigo-500"
            >
              <option value="OWNER">Owner (Property Owner)</option>
              <option value="TENANT">Tenant (Leaseholder)</option>
            </select>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Emergency Contact Name
              </label>
              <input
                type="text"
                placeholder="e.g. Suresh Kumar"
                value={emergencyName}
                onChange={(e) => setEmergencyName(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium focus:outline-none focus:border-indigo-500"
              />
            </div>
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">
                Emergency Contact Phone
              </label>
              <input
                type="text"
                placeholder="e.g. 9811223344"
                value={emergencyPhone}
                onChange={(e) => setEmergencyPhone(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-mono focus:outline-none focus:border-indigo-500"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={submitting}
            className="w-full py-3 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 text-white font-extrabold text-xs uppercase tracking-wider rounded-xl transition shadow-md flex items-center justify-center gap-2"
          >
            {submitting ? <Loader2 className="w-4 h-4 animate-spin" /> : null}
            {submitting ? 'Registering Resident...' : 'Save Resident Record'}
          </button>
        </form>
      </Modal>
    </div>
  );
};
