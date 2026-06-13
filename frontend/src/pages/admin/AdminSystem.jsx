import { useEffect, useState } from 'react'
import { 
  ShieldCheck, Database, Zap, Clock, 
  Cpu, HardDrive, Users, Activity 
} from 'lucide-react'
import { adminApi } from '../../api/services'
import { motion } from 'framer-motion'

export default function AdminSystem() {
  const [health, setHealth] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    adminApi.health()
      .then(({ data }) => setHealth(data))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <div className="h-64 flex items-center justify-center animate-pulse text-slate-400">Loading system vitals...</div>

  const services = [
    { label: 'API Server', status: health?.status, icon: ShieldCheck, color: 'text-emerald-500' },
    { label: 'Database', status: health?.database, icon: Database, color: 'text-brand-500' },
    { label: 'WebSocket', status: health?.webSocket, icon: Zap, color: 'text-amber-500' },
    { label: 'Scheduler', status: health?.scheduler, icon: Clock, color: 'text-purple-500' },
  ]

  return (
    <div className="space-y-6">
      <div className="grid md:grid-cols-4 gap-4">
        {services.map((s) => (
          <div key={s.label} className="card p-4 flex items-center gap-4">
            <div className={`p-3 rounded-xl bg-slate-50 dark:bg-slate-800 ${s.color}`}>
              <s.icon className="w-6 h-6" />
            </div>
            <div>
              <p className="text-xs font-bold text-slate-400 uppercase tracking-widest">{s.label}</p>
              <p className="text-sm font-bold text-slate-900 dark:text-white flex items-center gap-1.5">
                <span className={`w-2 h-2 rounded-full ${s.status === 'UP' || s.status === 'CONNECTED' || s.status === 'ACTIVE' || s.status === 'RUNNING' ? 'bg-emerald-500' : 'bg-rose-500'}`} />
                {s.status}
              </p>
            </div>
          </div>
        ))}
      </div>

      <div className="grid lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 card p-6">
          <h3 className="font-bold text-lg mb-6 flex items-center gap-2">
            <Activity className="w-5 h-5 text-brand-600" />
            Infrastructure Metrics
          </h3>
          <div className="grid sm:grid-cols-3 gap-6">
            <MetricProgress label="CPU Load" value={health?.metrics?.cpuUsage} icon={Cpu} />
            <MetricProgress label="Memory" value={health?.metrics?.memoryUsage} icon={HardDrive} />
            <MetricProgress label="Active Users" value={health?.metrics?.activeSessions} icon={Users} />
          </div>
        </div>
        
        <div className="card p-6 bg-slate-900 text-white border-none">
          <h3 className="font-bold text-lg mb-4">Instance Info</h3>
          <div className="space-y-4 text-sm">
            <InfoRow label="Environment" value="Production" />
            <InfoRow label="Region" value="Vercel Edge / Render" />
            <InfoRow label="Version" value="1.0.0-enterprise" />
            <InfoRow label="Uptime" value="14d 6h 22m" />
          </div>
          <button className="btn-primary w-full mt-8 bg-white text-slate-900 hover:bg-slate-100 border-none">
            System Logs
          </button>
        </div>
      </div>
    </div>
  )
}

function MetricProgress({ label, value, icon: Icon }) {
  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2 text-slate-500">
          <Icon className="w-4 h-4" />
          <span className="text-xs font-semibold">{label}</span>
        </div>
        <span className="text-sm font-bold">{value}</span>
      </div>
      <div className="h-1.5 w-full bg-slate-100 dark:bg-slate-800 rounded-full overflow-hidden">
        <motion.div 
          initial={{ width: 0 }}
          animate={{ width: typeof value === 'string' ? value : '50%' }}
          className="h-full bg-brand-500"
        />
      </div>
    </div>
  )
}

function InfoRow({ label, value }) {
  return (
    <div className="flex items-center justify-between border-b border-white/10 pb-2">
      <span className="text-slate-400">{label}</span>
      <span className="font-mono">{value}</span>
    </div>
  )
}
