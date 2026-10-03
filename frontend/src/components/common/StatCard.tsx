import React from 'react';
import { LucideIcon, TrendingUp, TrendingDown } from 'lucide-react';

interface StatCardProps {
  title: string;
  value: string | number;
  subtitle?: string;
  icon: LucideIcon;
  color?: 'emerald' | 'amber' | 'rose' | 'sky' | 'purple' | 'cyan' | 'indigo';
  trend?: {
    value: string;
    positive: boolean;
  };
  actionLabel?: string;
  onAction?: () => void;
}

export const StatCard: React.FC<StatCardProps> = ({
  title,
  value,
  subtitle,
  icon: Icon,
  color = 'emerald',
  trend,
  actionLabel,
  onAction,
}) => {
  const colorStyles = {
    emerald: {
      bg: 'bg-emerald-50 text-emerald-600 border-emerald-200',
      glow: 'hover:border-emerald-300 hover:shadow-emerald-500/10',
      bar: 'from-emerald-500 via-teal-400 to-transparent',
    },
    amber: {
      bg: 'bg-amber-50 text-amber-600 border-amber-200',
      glow: 'hover:border-amber-300 hover:shadow-amber-500/10',
      bar: 'from-amber-500 via-yellow-400 to-transparent',
    },
    rose: {
      bg: 'bg-rose-50 text-rose-600 border-rose-200',
      glow: 'hover:border-rose-300 hover:shadow-rose-500/10',
      bar: 'from-rose-500 via-pink-400 to-transparent',
    },
    sky: {
      bg: 'bg-sky-50 text-sky-600 border-sky-200',
      glow: 'hover:border-sky-300 hover:shadow-sky-500/10',
      bar: 'from-sky-500 via-blue-400 to-transparent',
    },
    purple: {
      bg: 'bg-purple-50 text-purple-600 border-purple-200',
      glow: 'hover:border-purple-300 hover:shadow-purple-500/10',
      bar: 'from-purple-500 via-indigo-400 to-transparent',
    },
    cyan: {
      bg: 'bg-cyan-50 text-cyan-600 border-cyan-200',
      glow: 'hover:border-cyan-300 hover:shadow-cyan-500/10',
      bar: 'from-cyan-500 via-teal-400 to-transparent',
    },
    indigo: {
      bg: 'bg-indigo-50 text-indigo-600 border-indigo-200',
      glow: 'hover:border-indigo-300 hover:shadow-indigo-500/10',
      bar: 'from-indigo-500 via-blue-400 to-transparent',
    },
  };

  const style = colorStyles[color] || colorStyles.emerald;

  return (
    <div
      className={`bg-white border border-slate-200 rounded-2xl p-5 shadow-sm relative overflow-hidden group transition-all duration-200 ${style.glow} hover:-translate-y-0.5`}
    >
      <div className="flex items-start justify-between">
        <div className="flex-1 pr-2">
          <p className="text-[11px] font-bold uppercase tracking-wider text-slate-500">{title}</p>
          <h3 className="text-2xl font-black text-slate-900 mt-1.5 tracking-tight font-sans">
            {value}
          </h3>
          {subtitle && <p className="text-[11px] text-slate-500 mt-1 font-medium">{subtitle}</p>}
          {trend && (
            <div className="flex items-center gap-1.5 mt-2.5">
              <span
                className={`inline-flex items-center gap-1 text-[11px] font-bold px-1.5 py-0.5 rounded border ${
                  trend.positive
                    ? 'bg-emerald-50 text-emerald-700 border-emerald-200'
                    : 'bg-rose-50 text-rose-700 border-rose-200'
                }`}
              >
                {trend.positive ? (
                  <TrendingUp className="w-3 h-3" />
                ) : (
                  <TrendingDown className="w-3 h-3" />
                )}
                {trend.value}
              </span>
              <span className="text-[10px] text-slate-400 font-medium">vs prior period</span>
            </div>
          )}
        </div>

        <div className="flex flex-col items-end gap-2">
          <div
            className={`p-3 rounded-2xl border ${style.bg} group-hover:scale-110 transition-transform duration-200 shadow-sm`}
          >
            <Icon className="w-5 h-5" />
          </div>

          {actionLabel && onAction && (
            <button
              onClick={onAction}
              className="text-[10px] font-bold text-slate-500 hover:text-emerald-600 transition"
            >
              {actionLabel} →
            </button>
          )}
        </div>
      </div>

      <div
        className={`absolute inset-x-0 bottom-0 h-0.5 bg-gradient-to-r ${style.bar} opacity-40 group-hover:opacity-100 transition-opacity`}
      />
    </div>
  );
};
