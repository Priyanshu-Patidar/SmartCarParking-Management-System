import { Info, AlertTriangle, CheckCircle2, TrendingUp } from 'lucide-react'

export default function PricingBreakdown({ breakdown }) {
  if (!breakdown) return null

  const items = [
    { label: 'Base Amount', value: breakdown.baseAmount, icon: CheckCircle2, color: 'text-slate-600' },
    { label: 'Peak Surcharge', value: breakdown.peakSurcharge, icon: TrendingUp, color: 'text-amber-600' },
    { label: 'Weekend Surcharge', value: breakdown.weekendSurcharge, icon: TrendingUp, color: 'text-brand-600' },
    { label: 'Occupancy Surge', value: breakdown.occupancySurcharge, icon: AlertTriangle, color: 'text-rose-600' },
  ].filter(i => i.value > 0 || i.label === 'Base Amount')

  return (
    <div className="bg-slate-50 dark:bg-slate-800/50 rounded-2xl p-4 border border-slate-200 dark:border-slate-700 space-y-3">
      <div className="flex items-center justify-between border-b border-slate-200 dark:border-slate-700 pb-2">
        <h4 className="font-bold text-sm flex items-center gap-2">
          <Info className="w-4 h-4 text-brand-600" />
          Pricing Breakdown
        </h4>
        <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full uppercase tracking-wider ${
          breakdown.occupancyStatus === 'Critical' ? 'bg-rose-100 text-rose-700' : 
          breakdown.occupancyStatus === 'High' ? 'bg-amber-100 text-amber-700' : 'bg-emerald-100 text-emerald-700'
        }`}>
          {breakdown.occupancyStatus} Demand
        </span>
      </div>
      
      <div className="space-y-2">
        {items.map((item) => (
          <div key={item.label} className="flex items-center justify-between text-sm">
            <div className="flex items-center gap-2 text-slate-500">
              <item.icon className={`w-3.5 h-3.5 ${item.color}`} />
              {item.label}
            </div>
            <span className="font-semibold">₹{(item.value || 0).toFixed(2)}</span>
          </div>
        ))}
      </div>

      <div className="pt-2 border-t border-slate-200 dark:border-slate-700 flex items-center justify-between">
        <span className="font-bold">Total Estimate</span>
        <span className="text-lg font-bold text-brand-600">₹{(breakdown.totalAmount || 0).toFixed(2)}</span>
      </div>

      {breakdown.appliedRules?.length > 0 && (
        <div className="pt-2">
          <p className="text-[10px] text-slate-400 uppercase font-bold tracking-widest mb-1">Applied Factors</p>
          <div className="flex flex-wrap gap-1">
            {breakdown.appliedRules.map(rule => (
              <span key={rule} className="text-[10px] bg-white dark:bg-slate-700 border border-slate-200 dark:border-slate-600 px-2 py-0.5 rounded-md text-slate-500">
                {rule}
              </span>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
