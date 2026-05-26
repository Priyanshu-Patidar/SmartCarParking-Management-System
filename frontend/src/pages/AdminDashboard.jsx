import { useEffect, useState } from 'react'
import { motion } from 'framer-motion'
import { Building2, Users, Car, DollarSign } from 'lucide-react'
import { dashboardApi } from '../api/services'
import { BookingTrendChart, RevenueChart } from '../components/StatsChart'

export default function AdminDashboard() {
  const [stats, setStats] = useState(null)

  useEffect(() => {
    dashboardApi.stats().then(({ data }) => setStats(data))
  }, [])

  const cards = [
    { icon: Building2, label: 'Locations', value: stats?.totalLocations, color: 'bg-brand-500' },
    { icon: Car, label: 'Total Slots', value: stats?.totalSlots, color: 'bg-green-500' },
    { icon: Users, label: 'Users', value: stats?.totalUsers, color: 'bg-purple-500' },
    { icon: DollarSign, label: 'Revenue', value: stats?.totalRevenue ? `₹${Number(stats.totalRevenue).toFixed(0)}` : '—', color: 'bg-amber-500' },
  ]

  return (
    <div>
      <h1 className="text-2xl font-bold">Admin Dashboard</h1>
      <p className="text-slate-500">Platform analytics & management</p>
      <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4 mt-6">
        {cards.map((c, i) => (
          <motion.div key={c.label} initial={{ opacity: 0, scale: 0.95 }} animate={{ opacity: 1, scale: 1 }}
            transition={{ delay: i * 0.08 }} className="card flex items-center gap-4">
            <div className={`${c.color} p-3 rounded-xl text-white`}>
              <c.icon className="w-6 h-6" />
            </div>
            <div>
              <p className="text-2xl font-bold">{c.value ?? '—'}</p>
              <p className="text-sm text-slate-500">{c.label}</p>
            </div>
          </motion.div>
        ))}
      </div>
      <div className="grid lg:grid-cols-2 gap-6 mt-8">
        <div className="card">
          <h3 className="font-bold mb-4">Slot Occupancy</h3>
          <div className="grid grid-cols-3 gap-4 text-center">
            <div><p className="text-2xl font-bold text-green-600">{stats?.availableSlots}</p><p className="text-xs">Available</p></div>
            <div><p className="text-2xl font-bold text-red-500">{stats?.occupiedSlots}</p><p className="text-xs">Occupied</p></div>
            <div><p className="text-2xl font-bold text-amber-500">{stats?.reservedSlots}</p><p className="text-xs">Reserved</p></div>
          </div>
        </div>
        <div className="card">
          <h3 className="font-bold mb-4">Booking Trends</h3>
          <BookingTrendChart data={stats?.bookingTrends} />
        </div>
        <div className="card lg:col-span-2">
          <h3 className="font-bold mb-4">Revenue by Location</h3>
          <RevenueChart data={stats?.revenueByLocation} />
        </div>
      </div>
    </div>
  )
}
