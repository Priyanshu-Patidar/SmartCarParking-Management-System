import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import {
  MapPin, Shield, Zap, BarChart3, Bell, Sparkles, Leaf, QrCode, Clock, TrendingUp,
} from 'lucide-react'
import { brand, hero, features, statsLabels } from '../content/siteCopy'
import { insightsApi } from '../api/services'

const icons = [MapPin, Clock, Shield, QrCode, TrendingUp, BarChart3, Leaf, Sparkles, Bell, Zap]

export default function Home() {
  const [stats, setStats] = useState(null)

  useEffect(() => {
    insightsApi.publicStats().then(({ data }) => setStats(data)).catch(() => {})
  }, [])

  const statCards = stats
    ? [
        { label: statsLabels.locations, value: stats.totalLocations },
        { label: statsLabels.cities, value: stats.totalCities },
        { label: statsLabels.slots, value: stats.availableSlots },
        { label: statsLabels.occupancy, value: `${stats.averageOccupancyPercent}%` },
      ]
    : []

  return (
    <div>
      <section className="relative overflow-hidden bg-gradient-to-br from-slate-900 via-brand-900 to-brand-600 text-white">
        <div className="absolute inset-0 opacity-20 bg-[radial-gradient(circle_at_30%_20%,white,transparent_50%)]" />
        <div className="max-w-7xl mx-auto px-6 py-24 lg:py-32 relative">
          <motion.div initial={{ opacity: 0, y: 24 }} animate={{ opacity: 1, y: 0 }}>
            <p className="text-brand-200 text-sm font-semibold tracking-wide uppercase">{brand.tagline}</p>
            <h1 className="text-4xl lg:text-6xl font-bold leading-tight max-w-3xl mt-4">{hero.title}</h1>
            <p className="mt-6 text-lg text-slate-200 max-w-2xl leading-relaxed">{hero.subtitle}</p>
            <div className="mt-10 flex flex-wrap gap-4">
              <Link to="/search" className="bg-white text-brand-800 font-semibold px-8 py-3.5 rounded-xl hover:shadow-xl transition">
                {hero.ctaPrimary}
              </Link>
              <Link to="/map" className="border border-white/40 px-8 py-3.5 rounded-xl hover:bg-white/10 transition font-semibold">
                {hero.ctaSecondary}
              </Link>
            </div>
          </motion.div>
        </div>
      </section>

      {statCards.length > 0 && (
        <section className="max-w-7xl mx-auto px-6 -mt-10 relative z-10">
          <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4">
            {statCards.map((s) => (
              <div key={s.label} className="card text-center shadow-xl">
                <p className="text-3xl font-bold text-brand-600">{s.value}</p>
                <p className="text-sm text-slate-500 mt-1">{s.label}</p>
              </div>
            ))}
          </div>
        </section>
      )}

      <section className="max-w-7xl mx-auto px-6 py-20">
        <div className="text-center max-w-2xl mx-auto mb-14">
          <h2 className="text-3xl font-bold">Built for drivers and facility operators</h2>
          <p className="text-slate-500 mt-4">
            SmartPark combines real-time data, secure transactions, and actionable insights — the same capabilities
            used by modern mobility platforms worldwide.
          </p>
        </div>
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {features.map((f, i) => {
            const Icon = icons[i % icons.length]
            return (
              <motion.div
                key={f.title}
                initial={{ opacity: 0, y: 16 }}
                whileInView={{ opacity: 1, y: 0 }}
                transition={{ delay: (i % 3) * 0.08 }}
                viewport={{ once: true }}
                className="card hover:shadow-lg transition"
              >
                <Icon className="w-10 h-10 text-brand-600 mb-4" />
                <h3 className="font-bold text-lg">{f.title}</h3>
                <p className="text-slate-500 mt-2 text-sm leading-relaxed">{f.desc}</p>
              </motion.div>
            )
          })}
        </div>
      </section>

      <section className="bg-brand-50 dark:bg-brand-950/40 py-16">
        <div className="max-w-4xl mx-auto px-6 text-center">
          <h2 className="text-2xl font-bold">Ready to reserve your next spot?</h2>
          <p className="text-slate-600 dark:text-slate-400 mt-3">
            Create a free account to book parking, manage sessions, save favorites, and receive availability alerts.
          </p>
          <div className="mt-8 flex flex-wrap justify-center gap-4">
            <Link to="/register" className="btn-primary">Create free account</Link>
            <Link to="/search" className="btn-secondary">Browse facilities</Link>
          </div>
        </div>
      </section>
    </div>
  )
}
