import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import { authApi } from '../api/authApi';
import { User, Mail, Phone, Shield, Lock, Loader2 } from 'lucide-react';
import { Badge } from '../components/common/Badge';

export const ProfilePage: React.FC = () => {
  const { user } = useAuth();
  const toast = useToast();
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [msg, setMsg] = useState<{ text: string; type: 'success' | 'error' } | null>(null);

  const handlePasswordChange = async (e: React.FormEvent) => {
    e.preventDefault();
    if (newPassword.length < 6) {
      const err = 'New password must be at least 6 characters long';
      setMsg({ text: err, type: 'error' });
      toast.error('Validation Error', err);
      return;
    }
    if (newPassword !== confirmPassword) {
      const err = 'New passwords do not match!';
      setMsg({ text: err, type: 'error' });
      toast.error('Mismatch', err);
      return;
    }

    try {
      setSubmitting(true);
      setMsg(null);
      await authApi.changePassword(currentPassword, newPassword);
      setMsg({ text: 'Password successfully updated!', type: 'success' });
      toast.success('Success', 'Password updated successfully. Please use your new password next time you log in.');
      setCurrentPassword('');
      setNewPassword('');
      setConfirmPassword('');
    } catch (err: any) {
      const errMsg = err?.response?.data?.message || err?.message || 'Failed to update password. Check your current password.';
      setMsg({ text: errMsg, type: 'error' });
      toast.error('Password Update Failed', errMsg);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="space-y-8 animate-fadeIn max-w-3xl text-slate-900">
      <div>
        <h1 className="text-2xl font-extrabold text-slate-900">User Profile Settings</h1>
        <p className="text-xs text-slate-500 mt-1 font-medium">Manage personal account credentials and security roles</p>
      </div>

      {/* Account Info */}
      <div className="bg-white border border-slate-200 rounded-3xl p-6 shadow-sm space-y-4">
        <div className="flex items-center gap-4">
          <div className="w-16 h-16 rounded-2xl bg-indigo-600 flex items-center justify-center font-extrabold text-white text-2xl shadow-md shadow-indigo-600/20">
            {user?.fullName?.charAt(0).toUpperCase() || 'U'}
          </div>
          <div>
            <h2 className="text-lg font-extrabold text-slate-900">{user?.fullName}</h2>
            <div className="flex items-center gap-2 mt-1">
              {user?.roles?.map((r: string) => (
                <Badge key={r} variant="info">{r}</Badge>
              ))}
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-4 border-t border-slate-100 text-xs">
          <div className="flex items-center gap-3 p-3.5 bg-slate-50 rounded-2xl border border-slate-200">
            <User className="w-4 h-4 text-indigo-600" />
            <div>
              <div className="text-[10px] text-slate-500 font-bold uppercase tracking-wider">Username</div>
              <div className="text-slate-900 font-bold">{user?.username}</div>
            </div>
          </div>

          <div className="flex items-center gap-3 p-3.5 bg-slate-50 rounded-2xl border border-slate-200">
            <Mail className="w-4 h-4 text-indigo-600" />
            <div>
              <div className="text-[10px] text-slate-500 font-bold uppercase tracking-wider">Email Address</div>
              <div className="text-slate-900 font-bold">{user?.email}</div>
            </div>
          </div>

          <div className="flex items-center gap-3 p-3.5 bg-slate-50 rounded-2xl border border-slate-200">
            <Phone className="w-4 h-4 text-indigo-600" />
            <div>
              <div className="text-[10px] text-slate-500 font-bold uppercase tracking-wider">Phone Number</div>
              <div className="text-slate-900 font-mono font-bold">{user?.phone || user?.phoneNumber || 'N/A'}</div>
            </div>
          </div>

          <div className="flex items-center gap-3 p-3.5 bg-slate-50 rounded-2xl border border-slate-200">
            <Shield className="w-4 h-4 text-indigo-600" />
            <div>
              <div className="text-[10px] text-slate-500 font-bold uppercase tracking-wider">Account Status</div>
              <div className="text-emerald-700 font-bold">ACTIVE</div>
            </div>
          </div>
        </div>
      </div>

      {/* Change Password */}
      <div className="bg-white border border-slate-200 rounded-3xl p-6 shadow-sm">
        <h3 className="text-base font-bold text-slate-900 mb-4 flex items-center gap-2">
          <Lock className="w-5 h-5 text-indigo-600" /> Security & Password Update
        </h3>

        {msg && (
          <div
            className={`p-3.5 rounded-2xl mb-4 text-xs font-bold ${
              msg.type === 'success' ? 'bg-emerald-50 text-emerald-700 border border-emerald-200' : 'bg-rose-50 text-rose-700 border border-rose-200'
            }`}
          >
            {msg.text}
          </div>
        )}

        <form onSubmit={handlePasswordChange} className="space-y-4 text-slate-900">
          <div>
            <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">Current Password</label>
            <input
              type="password"
              required
              value={currentPassword}
              onChange={(e) => setCurrentPassword(e.target.value)}
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
            />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">New Password</label>
              <input
                type="password"
                required
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
              />
            </div>
            <div>
              <label className="block text-[11px] font-bold text-slate-700 uppercase tracking-wider mb-1">Confirm New Password</label>
              <input
                type="password"
                required
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-4 py-2.5 text-xs text-slate-900 font-medium"
              />
            </div>
          </div>
          <button
            type="submit"
            disabled={submitting}
            className="inline-flex items-center gap-2 px-6 py-2.5 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 text-white font-extrabold text-xs uppercase tracking-wider rounded-xl transition shadow-md"
          >
            {submitting && <Loader2 className="w-4 h-4 animate-spin" />}
            {submitting ? 'Updating Password...' : 'Update Password'}
          </button>
        </form>
      </div>
    </div>
  );
};
