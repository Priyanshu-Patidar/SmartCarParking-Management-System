import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import { MapPin, Calendar, Heart, Activity } from 'lucide-react'
import { dashboardApi, parkingApi } from '../api/services'
import { BookingTrendChart } from '../components/StatsChart'
import SmartRecommendations from '../components/SmartRecommendations'

import LoadingSkeleton from '../components/LoadingSkeleton'

export default function UserDashboard() {
  const [stats, setStats] = useState(null)
  const [favorites, setFavorites] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([
      dashboardApi.stats().then(({ data }) => setStats(data)),
      parkingApi.getFavorites().then(({ data }) => setFavorites(data))
    ]).finally(() => setLoading(false))
  }, [])

  if (loading) {
    return (
      <div className="space-y-8 animate-pulse">
        <div className="h-8 bg-slate-200 dark:bg-slate-800 rounded w-1/4" />
        <div className="h-4 bg-slate-200 dark:bg-slate-800 rounded w-1/3 mt-2" />
        <LoadingSkeleton variant="stats" count={4} />
        <div className="grid lg:grid-cols-2 gap-6 mt-8">
           <div className="h-64 bg-slate-200 dark:bg-slate-800 rounded-3xl" />
           <div className="h-64 bg-slate-200 dark:bg-slate-800 rounded-3xl" />
        </div>
      </div>
    )
  }

  const cards = [
    { icon: MapPin, label: 'Network facilities', value: stats?.totalLocations ?? '—', color: 'text-brand-600' },
    { icon: Calendar, label: 'Open slots now', value: stats?.availableSlots ?? '—', color: 'text-green-600' },
    { icon: Activity, label: 'Active reservations', value: stats?.activeBookings ?? '—', color: 'text-amber-600' },
    { icon: Heart, label: 'Saved locations', value: favorites.length, color: 'text-red-500' },
  ]

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-2xl font-bold">Your parking hub</h1>
        <p className="text-slate-500">Reservations, recommendations, and network insights in one place.</p>
      </div>

      <SmartRecommendations />

      <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {cards.map((c, i) => (
          <motion.div key={c.label} initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }}
            transition={{ delay: i * 0.08 }} className="card">
            <c.icon className={`w-8 h-8 ${c.color}`} />
            <p className="text-2xl font-bold mt-2">{c.value}</p>
            <p className="text-sm text-slate-500">{c.label}</p>
          </motion.div>
        ))}
      </div>

      <div className="grid lg:grid-cols-2 gap-6">
        <div className="card">
          <h3 className="font-bold mb-4">Reservation activity</h3>
          <BookingTrendChart data={stats?.bookingTrends} />
        </div>
        <div className="card">
          <h3 className="font-bold mb-4">Shortcuts</h3>
          <div className="space-y-3">
            <Link to="/search" className="btn-primary block text-center">Find parking</Link>
            <Link to="/map" className="btn-secondary block text-center">Live facility map</Link>
            <Link to="/bookings" className="btn-secondary block text-center">Manage reservations</Link>
          </div>
          {favorites.length > 0 && (
            <div className="mt-6 border-t border-slate-200 dark:border-slate-700 pt-4">
              <h4 className="font-semibold text-sm text-slate-500 mb-2">Saved facilities</h4>
              {favorites.slice(0, 5).map((f) => (
                <Link key={f.id} to={`/booking/${f.id}`} className="text-sm py-1 block hover:text-brand-600">
                  {f.name}
                </Link>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
