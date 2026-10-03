import React, { useEffect, useState } from 'react';
import { noticeApi } from '../api/noticeApi';
import { Notice } from '../types';
import { useAuth } from '../context/AuthContext';
import { Modal } from '../components/common/Modal';
import { Badge } from '../components/common/Badge';
import { Megaphone, Plus, Trash2, Calendar } from 'lucide-react';

export const NoticesPage: React.FC = () => {
  const [notices, setNotices] = useState<Notice[]>([]);
  const [loading, setLoading] = useState(true);
  const { hasAnyRole } = useAuth();

  // Create Notice Modal
  const [showModal, setShowModal] = useState(false);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [priority, setPriority] = useState('HIGH');
  const [targetAudience, setTargetAudience] = useState('ALL');

  useEffect(() => {
    fetchNotices();
  }, []);

  const fetchNotices = async () => {
    try {
      const response = await noticeApi.getActiveNotices();
      if (Array.isArray(response)) {
        setNotices(response);
      } else if (response && response.data) {
        setNotices(response.data);
      }
    } catch (err) {
      console.error('Failed to load notices', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateNotice = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await noticeApi.createNotice({
        title,
        content,
        priority,
        targetAudience,
      });
      setShowModal(false);
      setTitle('');
      setContent('');
      fetchNotices();
    } catch (err) {
      alert('Failed to publish notice');
    }
  };

  const handleDeleteNotice = async (id: string | number) => {
    if (!confirm('Are you sure you want to delete this notice?')) return;
    try {
      await noticeApi.deleteNotice(id);
      fetchNotices();
    } catch (err) {
      alert('Failed to delete notice');
    }
  };

  return (
    <div className="space-y-8 animate-fadeIn text-slate-900">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-slate-900 flex items-center gap-2">
            <Megaphone className="w-7 h-7 text-indigo-600" /> Society Notice Board
          </h1>
          <p className="text-xs text-slate-500 mt-1 font-medium">Official announcements, maintenance schedules, and events</p>
        </div>
        {hasAnyRole(['SUPER_ADMIN', 'SOCIETY_ADMIN']) && (
          <button
            onClick={() => setShowModal(true)}
            className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-xl text-xs font-bold uppercase tracking-wider flex items-center gap-2 transition shadow-md shadow-indigo-600/20"
          >
            <Plus className="w-4 h-4" /> Publish Announcement
          </button>
        )}
      </div>

      {/* Notice Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {loading ? (
          <div className="col-span-2 text-center text-slate-400 py-10">Loading notices...</div>
        ) : notices.length === 0 ? (
          <div className="col-span-2 text-center text-slate-500 py-10 bg-slate-50 rounded-2xl border border-slate-200">
            No active notices published at this moment.
          </div>
        ) : (
          notices.map((n) => {
            const audience = n.targetAudience || n.audience || 'ALL';
            const author = n.postedByName || n.createdByName || 'Society Admin';
            const pubDate = n.postedAt || n.publishDate || n.createdAt || new Date().toISOString();
            const isHigh = n.priority === 'EMERGENCY' || n.priority === 'HIGH' || n.priority === 'URGENT';
            return (
              <div
                key={n.id}
                className={`bg-white border rounded-3xl p-6 shadow-sm relative group transition hover:shadow-md ${
                  isHigh ? 'border-rose-300 bg-rose-50/20' : 'border-slate-200'
                }`}
              >
                <div className="flex items-center justify-between mb-3">
                  <div className="flex items-center gap-2">
                    <Badge variant={isHigh ? 'danger' : 'info'}>
                      {n.priority}
                    </Badge>
                    <span className="text-[10px] text-slate-400 uppercase font-bold tracking-wider">
                      Audience: {audience}
                    </span>
                  </div>
                  {hasAnyRole(['SUPER_ADMIN', 'SOCIETY_ADMIN']) && (
                    <button
                      onClick={() => handleDeleteNotice(n.id)}
                      className="text-slate-400 hover:text-rose-600 transition"
                      title="Delete Notice"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  )}
                </div>
                <h3 className="text-base font-extrabold text-slate-900 mb-2">{n.title}</h3>
                <p className="text-xs text-slate-700 leading-relaxed whitespace-pre-line mb-4 font-medium">{n.content}</p>
                <div className="pt-3 border-t border-slate-100 flex items-center justify-between text-[11px] text-slate-500 font-semibold">
                  <span>By: {author}</span>
                  <span className="flex items-center gap-1 font-mono">
                    <Calendar className="w-3.5 h-3.5 text-indigo-600" />
                    {new Date(pubDate).toLocaleDateString()}
                  </span>
                </div>
              </div>
            );
          })
        )}
      </div>

      {/* Publish Notice Modal */}
      <Modal isOpen={showModal} onClose={() => setShowModal(false)} title="Publish Official Notice">
        <form onSubmit={handleCreateNotice} className="space-y-4 text-slate-900">
          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">Notice Title</label>
            <input
              type="text"
              required
              placeholder="e.g. Annual General Body Meeting (AGM) Notice"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
            />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">Priority</label>
              <select
                value={priority}
                onChange={(e) => setPriority(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
              >
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
                <option value="EMERGENCY">Emergency</option>
              </select>
            </div>
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">Target Audience</label>
              <select
                value={targetAudience}
                onChange={(e) => setTargetAudience(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
              >
                <option value="ALL">All Residents</option>
                <option value="OWNERS">Owners Only</option>
                <option value="TENANTS">Tenants Only</option>
              </select>
            </div>
          </div>
          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">Notice Content</label>
            <textarea
              required
              rows={4}
              placeholder="Detailed announcement text..."
              value={content}
              onChange={(e) => setContent(e.target.value)}
              className="w-full bg-slate-50 border border-slate-300 rounded-xl p-4 text-xs text-slate-900 font-medium"
            />
          </div>
          <button type="submit" className="w-full py-3 bg-indigo-600 hover:bg-indigo-700 text-white font-extrabold text-xs uppercase tracking-wider rounded-xl transition shadow-md">
            Publish Notice Board Post
          </button>
        </form>
      </Modal>
    </div>
  );
};
