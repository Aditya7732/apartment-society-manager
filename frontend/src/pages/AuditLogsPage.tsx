import React, { useEffect, useState } from 'react';
import { auditLogApi } from '../api/auditLogApi';
import { AuditLog } from '../types';
import { Badge } from '../components/common/Badge';
import { Pagination } from '../components/common/Pagination';
import { Activity } from 'lucide-react';

export const AuditLogsPage: React.FC = () => {
  const [logs, setLogs] = useState<AuditLog[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    fetchLogs();
  }, [page]);

  const fetchLogs = async () => {
    setLoading(true);
    try {
      const response = await auditLogApi.getLogs({ page, size: 15 });
      if (response && response.data) {
        setLogs(response.data.content || []);
        setTotalPages(response.data.totalPages || 1);
      } else if (response && (response as any).content) {
        setLogs((response as any).content);
        setTotalPages((response as any).totalPages || 1);
      }
    } catch (err) {
      console.error('Failed to load audit logs', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-8 animate-fadeIn text-slate-900">
      <div>
        <h1 className="text-2xl font-extrabold text-slate-900 flex items-center gap-2">
          <Activity className="w-7 h-7 text-indigo-600" /> System Audit Trail & Security Logs
        </h1>
        <p className="text-xs text-slate-500 mt-1 font-medium">Immutable security log tracking user actions, entity mutations, and IP addresses</p>
      </div>

      <div className="bg-white border border-slate-200 rounded-3xl shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-700">
            <thead className="bg-slate-50 text-slate-500 uppercase tracking-wider font-extrabold border-b border-slate-200">
              <tr>
                <th className="px-4 py-3.5">Timestamp</th>
                <th className="px-4 py-3.5">Actor (Username)</th>
                <th className="px-4 py-3.5">Action</th>
                <th className="px-4 py-3.5">Entity Type</th>
                <th className="px-4 py-3.5">Entity ID</th>
                <th className="px-4 py-3.5">Details</th>
                <th className="px-4 py-3.5">IP Address</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {loading ? (
                <tr>
                  <td colSpan={7} className="px-4 py-8 text-center text-slate-400">
                    Loading audit trail...
                  </td>
                </tr>
              ) : logs.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-4 py-8 text-center text-slate-400">
                    No audit logs recorded yet.
                  </td>
                </tr>
              ) : (
                logs.map((log) => (
                  <tr key={log.id} className="hover:bg-slate-50 transition">
                    <td className="px-4 py-3.5 text-slate-500 font-mono">
                      {new Date(log.timestamp || log.createdAt || Date.now()).toLocaleString()}
                    </td>
                    <td className="px-4 py-3.5 font-bold text-slate-900">{log.userName || log.username || 'SYSTEM'}</td>
                    <td className="px-4 py-3.5">
                      <Badge
                        variant={
                          (log.action || '').includes('CREATE')
                            ? 'success'
                            : (log.action || '').includes('DELETE')
                            ? 'danger'
                            : 'info'
                        }
                      >
                        {log.action}
                      </Badge>
                    </td>
                    <td className="px-4 py-3.5 font-mono text-indigo-700 font-bold">{log.entityName || log.entityType}</td>
                    <td className="px-4 py-3.5 font-mono text-slate-500">#{log.entityId || 'N/A'}</td>
                    <td className="px-4 py-3.5 text-slate-600 max-w-xs truncate">{log.details || log.previousValue || log.newValue || '-'}</td>
                    <td className="px-4 py-3.5 font-mono text-slate-500">{log.ipAddress || '127.0.0.1'}</td>
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
    </div>
  );
};
