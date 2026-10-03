import React from 'react';
import { Link } from 'react-router-dom';
import { ShieldAlert } from 'lucide-react';

export const NotFoundPage: React.FC = () => {
  return (
    <div className="flex flex-col items-center justify-center min-h-[70vh] text-center p-6">
      <div className="p-4 bg-rose-500/10 border border-rose-500/20 rounded-2xl text-rose-400 mb-4">
        <ShieldAlert className="w-12 h-12" />
      </div>
      <h1 className="text-4xl font-extrabold text-slate-100">404 - Page Not Found</h1>
      <p className="text-sm text-slate-400 mt-2 max-w-md">
        The requested page does not exist or you do not have appropriate role permissions to access it.
      </p>
      <Link
        to="/dashboard/admin"
        className="mt-6 px-6 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white rounded-xl font-bold text-xs uppercase tracking-wider transition shadow-lg shadow-indigo-600/30"
      >
        Return to Dashboard
      </Link>
    </div>
  );
};
