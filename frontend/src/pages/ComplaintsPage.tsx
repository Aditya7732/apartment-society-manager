import React, { useEffect, useState, useCallback } from 'react';
import { complaintApi } from '../api/complaintApi';
import { staffApi } from '../api/staffApi';
import { Complaint, Staff } from '../types';
import { Badge } from '../components/common/Badge';
import { Modal } from '../components/common/Modal';
import { Pagination } from '../components/common/Pagination';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { AlertTriangle, Plus, Send, Loader2, Users } from 'lucide-react';

export const ComplaintsPage: React.FC = () => {
  const { hasAnyRole } = useAuth();
  const toast = useToast();
  const [complaints, setComplaints] = useState<Complaint[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [submitting, setSubmitting] = useState(false);

  // Staff list for assignment dropdown
  const [staffList, setStaffList] = useState<Staff[]>([]);
  const canManage = hasAnyRole(['SUPER_ADMIN', 'SOCIETY_ADMIN', 'ACCOUNTANT', 'SECURITY']);

  // New Complaint Modal
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [category, setCategory] = useState('PLUMBING');
  const [priority, setPriority] = useState('MEDIUM');

  // Detail / Manage Modal
  const [selectedComplaint, setSelectedComplaint] = useState<Complaint | null>(null);
  const [newComment, setNewComment] = useState('');
  const [statusUpdate, setStatusUpdate] = useState('');
  const [assignedStaffId, setAssignedStaffId] = useState<string>('');
  const [resolutionNotes, setResolutionNotes] = useState('');

  useEffect(() => {
    fetchComplaints();
    if (canManage) fetchStaff();
  }, [page]);

  const fetchStaff = async () => {
    try {
      const res = await staffApi.getAllStaff({ size: 100, activeOnly: true });
      const list = (res as any)?.data?.content || (res as any)?.content || [];
      setStaffList(list);
    } catch (err) {
      // Staff list is optional - silently fail
    }
  };

  const fetchComplaints = async () => {
    setLoading(true);
    try {
      const response = await complaintApi.getAllComplaints({ page, size: 10 });
      if (response && response.data) {
        setComplaints(response.data.content || []);
        setTotalPages(response.data.totalPages || 1);
      } else if (response && (response as any).content) {
        setComplaints((response as any).content);
        setTotalPages((response as any).totalPages || 1);
      }
    } catch (err) {
      console.error('Failed to load complaints', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateComplaint = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await complaintApi.createComplaint({ title, description, category, priority });
      toast.success('Complaint ticket created', `Ticket logged: ${title}`);
      setShowCreateModal(false);
      setTitle('');
      setDescription('');
      fetchComplaints();
    } catch (err: any) {
      const msg = err?.response?.data?.message || 'Failed to log complaint ticket';
      toast.error('Ticket creation failed', msg);
    } finally {
      setSubmitting(false);
    }
  };

  const handleAddComment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedComplaint || !newComment.trim()) return;
    setSubmitting(true);
    try {
      const res = await complaintApi.addComment(selectedComplaint.id, newComment);
      if (res && res.data) {
        setSelectedComplaint((prev) =>
          prev ? { ...prev, comments: [...(prev.comments || []), res.data] } : null
        );
        setNewComment('');
        toast.success('Comment added');
        fetchComplaints();
      }
    } catch (err) {
      toast.error('Failed to add comment');
    } finally {
      setSubmitting(false);
    }
  };

  const handleUpdateStatus = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedComplaint || !statusUpdate) return;
    setSubmitting(true);
    try {
      const res = await complaintApi.updateStatus(selectedComplaint.id, {
        status: statusUpdate,
        assignedStaffId: assignedStaffId || undefined,
        resolutionNotes,
      });
      if (res && res.data) {
        setSelectedComplaint(res.data);
        toast.success('Ticket updated', `Status changed to ${statusUpdate}`);
        fetchComplaints();
      }
    } catch (err: any) {
      const msg = err?.response?.data?.message || 'Failed to update ticket status';
      toast.error('Update failed', msg);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="space-y-8 animate-fadeIn text-slate-900">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 flex items-center gap-2">
            <AlertTriangle className="w-7 h-7 text-indigo-600" /> Helpdesk & Complaints Log
          </h1>
          <p className="text-xs text-slate-500 mt-1 font-medium">Track resident issues, assign staff technicians, and post updates</p>
        </div>
        <button
          onClick={() => setShowCreateModal(true)}
          className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-bold uppercase tracking-wider flex items-center gap-2 transition shadow-md shadow-indigo-600/20"
        >
          <Plus className="w-4 h-4" /> Raise Complaint Ticket
        </button>
      </div>

      <div className="bg-white border border-slate-200 rounded-3xl shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-700">
            <thead className="bg-slate-50 text-slate-500 uppercase tracking-wider font-extrabold border-b border-slate-200">
              <tr>
                <th className="px-4 py-3.5">Ticket #</th>
                <th className="px-4 py-3.5">Issue Title</th>
                <th className="px-4 py-3.5">Category</th>
                <th className="px-4 py-3.5">Flat</th>
                <th className="px-4 py-3.5">Priority</th>
                <th className="px-4 py-3.5">Status</th>
                <th className="px-4 py-3.5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan={7} className="px-4 py-8 text-center text-slate-400">
                    Loading tickets...
                  </td>
                </tr>
              ) : complaints.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-4 py-8 text-center text-slate-400">
                    No complaint tickets raised yet.
                  </td>
                </tr>
              ) : (
                complaints.map((c) => (
                  <tr key={c.id} className="hover:bg-slate-50 transition">
                    <td className="px-4 py-3.5 font-mono font-bold text-indigo-700">
                      {c.ticketNumber || `#TICK-${c.id}`}
                    </td>
                    <td className="px-4 py-3.5 font-bold text-slate-900">{c.title}</td>
                    <td className="px-4 py-3.5 text-slate-600 font-medium">{c.category}</td>
                    <td className="px-4 py-3.5 text-slate-900 font-semibold">{c.flatNumber || c.flatId}</td>
                    <td className="px-4 py-3.5">
                      <Badge
                        variant={
                          c.priority === 'CRITICAL' || c.priority === 'URGENT' || c.priority === 'HIGH'
                            ? 'danger'
                            : c.priority === 'MEDIUM'
                            ? 'warning'
                            : 'secondary'
                        }
                      >
                        {c.priority}
                      </Badge>
                    </td>
                    <td className="px-4 py-3.5">
                      <Badge
                        variant={
                          c.status === 'RESOLVED' || c.status === 'CLOSED'
                            ? 'success'
                            : c.status === 'IN_PROGRESS'
                            ? 'info'
                            : c.status === 'ASSIGNED'
                            ? 'warning'
                            : 'danger'
                        }
                      >
                        {c.status}
                      </Badge>
                    </td>
                    <td className="px-4 py-3.5 text-right">
                      <button
                        onClick={() => {
                          setSelectedComplaint(c);
                          setStatusUpdate(c.status);
                          setAssignedStaffId(c.assignedStaffId ? String(c.assignedStaffId) : '');
                          setResolutionNotes(c.resolutionNotes || c.resolution || '');
                        }}
                        className="px-3.5 py-1.5 bg-slate-100 hover:bg-slate-200 text-indigo-700 border border-slate-300 font-bold rounded-xl text-xs transition ml-auto"
                      >
                        Manage Ticket
                      </button>
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

      {/* Ticket Details & Timeline Modal */}
      <Modal
        isOpen={Boolean(selectedComplaint)}
        onClose={() => setSelectedComplaint(null)}
        title={`Ticket Details: ${selectedComplaint?.ticketNumber || `#TICK-${selectedComplaint?.id}`}`}
        maxWidth="xl"
      >
        {selectedComplaint && (
          <div className="space-y-6 text-slate-900">
            <div>
              <div className="flex items-center justify-between mb-2">
                <h3 className="text-lg font-bold text-slate-900">{selectedComplaint.title}</h3>
                <Badge variant={selectedComplaint.status === 'RESOLVED' ? 'success' : 'warning'}>
                  {selectedComplaint.status}
                </Badge>
              </div>
              <p className="text-xs text-slate-700 bg-slate-50 p-3.5 rounded-2xl border border-slate-200">
                {selectedComplaint.description}
              </p>
            </div>

            {/* Update Ticket Form */}
            <form onSubmit={handleUpdateStatus} className="bg-slate-50 p-4 rounded-2xl border border-slate-200 space-y-3">
              <h4 className="text-xs font-bold text-slate-700 uppercase tracking-wider">Update Status & Resolution</h4>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-[10px] font-bold text-slate-600 mb-1">STATUS</label>
                  <select
                    value={statusUpdate}
                    onChange={(e) => setStatusUpdate(e.target.value)}
                    className="w-full bg-white border border-slate-300 rounded-xl px-3 py-2 text-xs text-slate-900 font-medium"
                  >
                    <option value="OPEN">OPEN</option>
                    <option value="ASSIGNED">ASSIGNED</option>
                    <option value="IN_PROGRESS">IN_PROGRESS</option>
                    <option value="RESOLVED">RESOLVED</option>
                    <option value="CLOSED">CLOSED</option>
                  </select>
                </div>
                <div>
                  <label className="block text-[10px] font-bold text-slate-600 mb-1">ASSIGN TO STAFF</label>
                  <select
                    value={assignedStaffId}
                    onChange={(e) => setAssignedStaffId(e.target.value)}
                    className="w-full bg-white border border-slate-300 rounded-xl px-3 py-2 text-xs text-slate-900 font-medium focus:outline-none focus:border-indigo-500"
                  >
                    <option value="">— Unassigned —</option>
                    {staffList.map((s) => (
                      <option key={s.id} value={s.id}>
                        {s.fullName} ({s.role})
                      </option>
                    ))}
                    {staffList.length === 0 && (
                      <option disabled>No staff loaded</option>
                    )}
                  </select>
                </div>
              </div>
              <div>
                <label className="block text-[10px] font-bold text-slate-600 mb-1">RESOLUTION / NOTES</label>
                <input
                  type="text"
                  value={resolutionNotes}
                  onChange={(e) => setResolutionNotes(e.target.value)}
                  placeholder="Resolution summary or technician notes"
                  className="w-full bg-white border border-slate-300 rounded-xl px-3 py-2 text-xs text-slate-900 font-medium"
                />
              </div>
              <button
                type="submit"
                disabled={submitting}
                className="w-full py-2.5 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 text-white font-bold rounded-xl text-xs transition shadow-sm uppercase tracking-wider flex items-center justify-center gap-2"
              >
                {submitting && <Loader2 className="w-3.5 h-3.5 animate-spin" />}
                Save Ticket Update
              </button>
            </form>

            {/* Comments Stream */}
            <div className="space-y-3">
              <h4 className="text-xs font-bold text-slate-700 uppercase tracking-wider">Activity Comments</h4>
              <div className="max-h-48 overflow-y-auto space-y-2 pr-1">
                {selectedComplaint.comments && selectedComplaint.comments.length > 0 ? (
                  selectedComplaint.comments.map((comment) => (
                    <div key={comment.id} className="bg-slate-50 p-3 rounded-xl border border-slate-200 text-xs">
                      <div className="flex items-center justify-between mb-1">
                        <span className="font-bold text-indigo-700">{comment.userFullName || comment.userName}</span>
                        <span className="text-[10px] text-slate-400 font-mono">{new Date(comment.createdAt).toLocaleString()}</span>
                      </div>
                      <p className="text-slate-700">{comment.comment}</p>
                    </div>
                  ))
                ) : (
                  <p className="text-xs text-slate-400 italic">No comments added yet.</p>
                )}
              </div>

              <form onSubmit={handleAddComment} className="flex gap-2">
                <input
                  type="text"
                  value={newComment}
                  onChange={(e) => setNewComment(e.target.value)}
                  placeholder="Type a comment or status update..."
                  className="flex-1 bg-slate-50 border border-slate-300 rounded-xl px-4 py-2 text-xs text-slate-900 font-medium"
                />
                <button
                  type="submit"
                  disabled={submitting || !newComment.trim()}
                  className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 text-white rounded-xl text-xs font-bold flex items-center gap-1.5 shadow-sm transition"
                >
                  {submitting ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <Send className="w-3.5 h-3.5" />}
                  Send
                </button>
              </form>
            </div>
          </div>
        )}
      </Modal>

      {/* New Complaint Modal */}
      <Modal isOpen={showCreateModal} onClose={() => setShowCreateModal(false)} title="Log New Complaint Ticket">
        <form onSubmit={handleCreateComplaint} className="space-y-4 text-slate-900">
          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">Ticket Summary Title</label>
            <input
              type="text"
              required
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="e.g. Water Leakage in Kitchen"
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
            />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">Category</label>
              <select
                value={category}
                onChange={(e) => setCategory(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
              >
                <option value="PLUMBING">PLUMBING</option>
                <option value="ELECTRICAL">ELECTRICAL</option>
                <option value="SECURITY">SECURITY</option>
                <option value="CLEANING">CLEANING</option>
                <option value="LIFT">LIFT</option>
                <option value="WATER">WATER</option>
                <option value="PARKING">PARKING</option>
                <option value="NOISE">NOISE</option>
                <option value="OTHER">OTHER</option>
              </select>
            </div>
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">Priority</label>
              <select
                value={priority}
                onChange={(e) => setPriority(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
              >
                <option value="LOW">LOW</option>
                <option value="MEDIUM">MEDIUM</option>
                <option value="HIGH">HIGH</option>
                <option value="CRITICAL">CRITICAL</option>
              </select>
            </div>
          </div>
          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">Detailed Description</label>
            <textarea
              required
              rows={4}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Describe the issue in detail..."
              className="w-full bg-slate-50 border border-slate-300 rounded-xl p-4 text-xs text-slate-900 font-medium"
            />
          </div>
          <button
            type="submit"
            className="w-full py-3 bg-indigo-600 hover:bg-indigo-700 text-white font-extrabold text-xs uppercase tracking-wider rounded-xl transition shadow-md"
          >
            Submit Complaint Ticket
          </button>
        </form>
      </Modal>
    </div>
  );
};
