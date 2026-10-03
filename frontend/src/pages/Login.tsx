import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { authApi } from '../api/authApi';
import {
  Building2,
  Lock,
  User,
  ShieldCheck,
  CheckCircle2,
  ArrowRight,
  Eye,
  EyeOff,
  Sparkles,
} from 'lucide-react';

export const Login: React.FC = () => {
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('Password@123');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const response = await authApi.login({ username, password });
      if (response && response.data) {
        login(response.data);

        const userObj = response.data.user;
        const roles = (userObj?.roles || []) as string[];
        if (roles.some((r) => r === 'ROLE_RESIDENT' || r === 'RESIDENT')) {
          navigate('/resident/dashboard');
        } else if (roles.some((r) => r === 'ROLE_SECURITY' || r === 'SECURITY')) {
          navigate('/security/dashboard');
        } else {
          navigate('/admin/dashboard');
        }
      } else {
        setError(response?.message || 'Login failed');
      }
    } catch (err: any) {
      setError(
        err.response?.data?.message ||
          'Invalid username or password. Please verify server status.'
      );
    } finally {
      setLoading(false);
    }
  };

  const quickLogin = (u: string, p: string = 'Password@123') => {
    setUsername(u);
    setPassword(p);
  };

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col justify-center items-center p-4 sm:p-6 relative font-sans text-slate-900 selection:bg-emerald-500 selection:text-white">
      {/* Background Soft Gradients */}
      <div className="absolute top-10 left-1/3 w-96 h-96 bg-emerald-100/60 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute bottom-10 right-1/3 w-96 h-96 bg-teal-100/60 rounded-full blur-3xl pointer-events-none" />

      {/* Grid Pattern */}
      <div className="absolute inset-0 bg-[radial-gradient(#e2e8f0_1px,transparent_1px)] [background-size:24px_24px] opacity-70 pointer-events-none" />

      <div className="w-full max-w-md bg-white border border-slate-200 rounded-3xl p-8 sm:p-10 shadow-xl relative z-10">
        {/* Header Branding */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-emerald-50 border border-emerald-200 text-emerald-600 mb-4 shadow-sm">
            <Building2 className="w-7 h-7" />
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight">
            Apartment Society Manager
          </h1>
          <p className="text-xs text-slate-500 mt-1 font-medium">
            Residential Governance & Operations Portal
          </p>
        </div>

        {error && (
          <div className="mb-6 p-4 rounded-2xl bg-rose-50 border border-rose-200 text-rose-700 text-xs font-semibold flex items-center gap-2.5">
            <span className="w-2 h-2 rounded-full bg-rose-500 animate-ping flex-shrink-0" />
            <span>{error}</span>
          </div>
        )}

        {/* Credentials Form */}
        <form onSubmit={handleLogin} className="space-y-4">
          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
              Username or Email
            </label>
            <div className="relative">
              <User className="w-4 h-4 text-slate-400 absolute left-3.5 top-3.5" />
              <input
                type="text"
                required
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="e.g. admin"
                className="w-full bg-slate-50 border border-slate-300 rounded-xl pl-10 pr-4 py-3 text-xs text-slate-900 placeholder-slate-400 focus:outline-none focus:bg-white focus:border-emerald-500 focus:ring-2 focus:ring-emerald-500/20 transition font-medium"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
              Password
            </label>
            <div className="relative">
              <Lock className="w-4 h-4 text-slate-400 absolute left-3.5 top-3.5" />
              <input
                type={showPassword ? 'text' : 'password'}
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••••••"
                className="w-full bg-slate-50 border border-slate-300 rounded-xl pl-10 pr-10 py-3 text-xs text-slate-900 placeholder-slate-400 focus:outline-none focus:bg-white focus:border-emerald-500 focus:ring-2 focus:ring-emerald-500/20 transition font-medium"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3.5 top-3.5 text-slate-400 hover:text-slate-600 transition"
              >
                {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
              </button>
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full py-3.5 bg-emerald-600 hover:bg-emerald-700 text-white font-extrabold text-xs uppercase tracking-wider rounded-xl shadow-lg shadow-emerald-600/20 transition disabled:opacity-50 flex items-center justify-center gap-2 mt-2"
          >
            {loading ? (
              <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
            ) : (
              <>
                <ShieldCheck className="w-4 h-4" /> Sign In To Portal <ArrowRight className="w-4 h-4" />
              </>
            )}
          </button>
        </form>

        {/* Quick Demo Role Selector */}
        <div className="mt-8 pt-6 border-t border-slate-200">
          <div className="flex items-center justify-between mb-3">
            <span className="text-[11px] font-bold text-slate-500 uppercase tracking-wider flex items-center gap-1.5">
              <Sparkles className="w-3.5 h-3.5 text-emerald-600" /> Demo Role Quick Selector
            </span>
            <span className="text-[10px] text-slate-400">Click to select</span>
          </div>

          <div className="grid grid-cols-2 gap-2 text-xs">
            <button
              type="button"
              onClick={() => quickLogin('admin')}
              className={`p-3 rounded-xl border text-left transition flex items-center justify-between ${
                username === 'admin'
                  ? 'bg-purple-50 border-purple-300 text-purple-900 font-bold shadow-sm'
                  : 'bg-slate-50 border-slate-200 text-slate-700 hover:bg-slate-100'
              }`}
            >
              <div>
                <div className="font-bold text-slate-900">Admin</div>
                <div className="text-[10px] text-slate-500">Super Admin</div>
              </div>
              {username === 'admin' && <CheckCircle2 className="w-4 h-4 text-purple-600" />}
            </button>

            <button
              type="button"
              onClick={() => quickLogin('manager')}
              className={`p-3 rounded-xl border text-left transition flex items-center justify-between ${
                username === 'manager'
                  ? 'bg-sky-50 border-sky-300 text-sky-900 font-bold shadow-sm'
                  : 'bg-slate-50 border-slate-200 text-slate-700 hover:bg-slate-100'
              }`}
            >
              <div>
                <div className="font-bold text-slate-900">Manager</div>
                <div className="text-[10px] text-slate-500">Society Admin</div>
              </div>
              {username === 'manager' && <CheckCircle2 className="w-4 h-4 text-sky-600" />}
            </button>

            <button
              type="button"
              onClick={() => quickLogin('resident1')}
              className={`p-3 rounded-xl border text-left transition flex items-center justify-between ${
                username === 'resident1'
                  ? 'bg-emerald-50 border-emerald-300 text-emerald-900 font-bold shadow-sm'
                  : 'bg-slate-50 border-slate-200 text-slate-700 hover:bg-slate-100'
              }`}
            >
              <div>
                <div className="font-bold text-slate-900">Resident</div>
                <div className="text-[10px] text-slate-500">Flat Owner</div>
              </div>
              {username === 'resident1' && <CheckCircle2 className="w-4 h-4 text-emerald-600" />}
            </button>

            <button
              type="button"
              onClick={() => quickLogin('security')}
              className={`p-3 rounded-xl border text-left transition flex items-center justify-between ${
                username === 'security'
                  ? 'bg-amber-50 border-amber-300 text-amber-900 font-bold shadow-sm'
                  : 'bg-slate-50 border-slate-200 text-slate-700 hover:bg-slate-100'
              }`}
            >
              <div>
                <div className="font-bold text-slate-900">Security</div>
                <div className="text-[10px] text-slate-500">Gatekeeper</div>
              </div>
              {username === 'security' && <CheckCircle2 className="w-4 h-4 text-amber-600" />}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
