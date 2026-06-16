import { useEffect, useState, memo, useMemo } from 'react'
import { motion } from 'framer-motion'
import { Building2, Users, Car, DollarSign } from 'lucide-react'
import { dashboardApi } from '../api/services'
import { BookingTrendChart, RevenueChart } from '../components/StatsChart'

const AdminStatCard = memo(({ icon: Icon, label, value, color, delay }) => (
  <motion.div initial={{ opacity: 0, scale: 0.95 }} animate={{ opacity: 1, scale: 1 }}
    transition={{ delay }} className="card flex items-center gap-4">
    <div className={`${color} p-3 rounded-xl text-white shadow-lg`}>
      <Icon className="w-6 h-6" />
    </div>
    <div>
      <p className="text-2xl font-bold">{value ?? '—'}</p>
      <p className="text-sm text-slate-500 font-medium">{label}</p>
    </div>
  </motion.div>
))

export default function AdminDashboard() {
  const [stats, setStats] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let active = true
    dashboardApi.stats()
      .then(({ data }) => { if (active) setStats(data) })
      .catch(() => {})
      .finally(() => { if (active) setLoading(false) })
    return () => { active = false }
  }, [])

  if (loading) return (
    <div className="space-y-8">
      <div className="h-8 bg-slate-200 dark:bg-slate-800 rounded w-1/4 animate-pulse" />
      <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4 mt-6">
        {[1,2,3,4].map(i => <div key={i} className="h-24 bg-slate-200 dark:bg-slate-800 rounded-3xl animate-pulse" />)}
      </div>
      <div className="grid lg:grid-cols-2 gap-6 mt-8">
        <div className="h-64 bg-slate-200 dark:bg-slate-800 rounded-3xl animate-pulse" />
        <div className="h-64 bg-slate-200 dark:bg-slate-800 rounded-3xl animate-pulse" />
      </div>
    </div>
  )

  const cardData = useMemo(() => [
    { icon: Building2, label: 'Locations', value: stats?.totalLocations, color: 'bg-brand-500' },
    { icon: Car, label: 'Total Slots', value: stats?.totalSlots, color: 'bg-green-500' },
    { icon: Users, label: 'Users', value: stats?.totalUsers, color: 'bg-purple-500' },
    { icon: DollarSign, label: 'Revenue', value: stats?.totalRevenue ? `₹${Number(stats.totalRevenue).toFixed(0)}` : '0', color: 'bg-amber-500' },
  ], [stats])

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-2xl font-bold">Platform Oversight</h1>
        <p className="text-slate-500">Global metrics and real-time operational status.</p>
      </div>
      
      <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {cardData.map((c, i) => (
          <AdminStatCard key={c.label} {...c} delay={i * 0.08} />
        ))}
      </div>

      <div className="grid lg:grid-cols-2 gap-6">
        <div className="card">
          <h3 className="font-bold mb-6 text-slate-400 uppercase text-xs tracking-widest">Real-time Capacity</h3>
          <div className="grid grid-cols-3 gap-4 text-center">
            <div className="p-4 bg-green-50 dark:bg-green-950/20 rounded-2xl border border-green-100 dark:border-green-900/30">
              <p className="text-3xl font-black text-green-600">{stats?.availableSlots}</p>
              <p className="text-[10px] font-bold uppercase text-green-700/60 mt-1">Available</p>
            </div>
            <div className="p-4 bg-red-50 dark:bg-red-950/20 rounded-2xl border border-red-100 dark:border-red-900/30">
              <p className="text-3xl font-black text-red-500">{stats?.occupiedSlots}</p>
              <p className="text-[10px] font-bold uppercase text-red-700/60 mt-1">Occupied</p>
            </div>
            <div className="p-4 bg-amber-50 dark:bg-amber-950/20 rounded-2xl border border-amber-100 dark:border-amber-900/30">
              <p className="text-3xl font-black text-amber-500">{stats?.reservedSlots}</p>
              <p className="text-[10px] font-bold uppercase text-amber-700/60 mt-1">Reserved</p>
            </div>
          </div>
        </div>

        <div className="card">
          <h3 className="font-bold mb-6 text-slate-400 uppercase text-xs tracking-widest">Booking Velocity</h3>
          <BookingTrendChart data={stats?.bookingTrends} />
        </div>

        <div className="card lg:col-span-2">
          <h3 className="font-bold mb-6 text-slate-400 uppercase text-xs tracking-widest">Revenue Distribution</h3>
          <RevenueChart data={stats?.revenueByLocation} />
        </div>
      </div>
    </div>
  )
}
