import { useEffect, useState } from 'react'
import { motion } from 'framer-motion'
import { 
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, 
  LineChart, Line, AreaChart, Area, PieChart, Pie, Cell 
} from 'recharts'
import { 
  TrendingUp, Users, DollarSign, Calendar, Filter, Download, 
  Clock, MapPin, PieChart as PieIcon, Activity
} from 'lucide-react'
import { dashboardApi } from '../../api/services'
import toast from 'react-hot-toast'

const COLORS = ['#0ea5e9', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6']

export default function AdvancedAnalytics() {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [timeRange, setTimeRange] = useState('7d')

  useEffect(() => {
    dashboardApi.advanced()
      .then(({ data }) => setData(data))
      .catch(() => toast.error('Failed to load analytics data'))
      .finally(() => setLoading(false))
  }, [timeRange])

  if (loading) return (
    <div className="flex items-center justify-center h-96">
      <div className="w-12 h-12 border-4 border-brand-500 border-t-transparent rounded-full animate-spin" />
    </div>
  )

  const summary = data?.summary || {}

  return (
    <div className="space-y-8 pb-12">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-slate-900 dark:text-white">Premium Analytics</h1>
          <p className="text-slate-500">In-depth insights into facility performance and revenue</p>
        </div>
        <div className="flex items-center gap-3">
          <select 
            className="input-field py-2"
            value={timeRange}
            onChange={(e) => setTimeRange(e.target.value)}
          >
            <option value="24h">Last 24 Hours</option>
            <option value="7d">Last 7 Days</option>
            <option value="30d">Last 30 Days</option>
            <option value="90d">Last 90 Days</option>
          </select>
          <button className="btn-secondary flex items-center gap-2">
            <Download className="w-4 h-4" /> Export Report
          </button>
        </div>
      </div>

      {/* Summary Cards */}
      <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard 
          icon={DollarSign} 
          label="Total Revenue" 
          value={`₹${summary.totalRevenue?.toLocaleString()}`} 
          trend="+12.5% from last period"
          color="bg-emerald-500" 
        />
        <StatCard 
          icon={Calendar} 
          label="Total Bookings" 
          value={summary.totalBookings} 
          trend="+8.2% from last period"
          color="bg-brand-500" 
        />
        <StatCard 
          icon={TrendingUp} 
          label="Avg Booking Value" 
          value={`₹${Math.round(summary.avgBookingValue)}`} 
          trend="+3.1% from last period"
          color="bg-purple-500" 
        />
        <StatCard 
          icon={Activity} 
          label="Peak Occupancy" 
          value="84%" 
          trend="Consistent with average"
          color="bg-amber-500" 
        />
      </div>

      <div className="grid lg:grid-cols-2 gap-8">
        {/* Revenue Trends */}
        <ChartContainer title="Revenue & Booking Trends" icon={TrendingUp}>
          <ResponsiveContainer width="100%" height={300}>
            <AreaChart data={data?.revenueTrends}>
              <defs>
                <linearGradient id="colorRev" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#0ea5e9" stopOpacity={0.1}/>
                  <stop offset="95%" stopColor="#0ea5e9" stopOpacity={0}/>
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
              <XAxis dataKey="date" stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} />
              <YAxis stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} />
              <Tooltip 
                contentStyle={{ backgroundColor: '#fff', borderRadius: '12px', border: 'none', boxShadow: '0 10px 15px -3px rgb(0 0 0 / 0.1)' }}
              />
              <Area type="monotone" dataKey="revenue" stroke="#0ea5e9" fillOpacity={1} fill="url(#colorRev)" strokeWidth={3} />
            </AreaChart>
          </ResponsiveContainer>
        </ChartContainer>

        {/* Occupancy Trends */}
        <ChartContainer title="Historical Occupancy %" icon={Clock}>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={data?.occupancyTrends}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
              <XAxis dataKey="time" stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} />
              <YAxis stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} />
              <Tooltip 
                contentStyle={{ backgroundColor: '#fff', borderRadius: '12px', border: 'none', boxShadow: '0 10px 15px -3px rgb(0 0 0 / 0.1)' }}
              />
              <Line type="monotone" dataKey="occupancy" stroke="#10b981" strokeWidth={3} dot={{ fill: '#10b981', r: 4 }} activeDot={{ r: 6 }} />
            </LineChart>
          </ResponsiveContainer>
        </ChartContainer>

        {/* Vehicle Type Distribution */}
        <ChartContainer title="Vehicle Type Distribution" icon={PieIcon}>
          <div className="flex flex-col md:flex-row items-center gap-8">
            <ResponsiveContainer width="100%" height={250}>
              <PieChart>
                <Pie
                  data={data?.vehicleTypeStats}
                  cx="50%"
                  cy="50%"
                  innerRadius={60}
                  outerRadius={80}
                  paddingAngle={5}
                  dataKey="count"
                  nameKey="type"
                >
                  {data?.vehicleTypeStats?.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
            <div className="space-y-2 w-full max-w-[200px]">
              {data?.vehicleTypeStats?.map((v, i) => (
                <div key={v.type} className="flex items-center justify-between text-sm">
                  <div className="flex items-center gap-2">
                    <div className="w-3 h-3 rounded-full" style={{ backgroundColor: COLORS[i % COLORS.length] }} />
                    <span className="text-slate-500">{v.type}</span>
                  </div>
                  <span className="font-bold">{v.count}</span>
                </div>
              ))}
            </div>
          </div>
        </ChartContainer>

        {/* Peak Hour Analytics */}
        <ChartContainer title="Peak Hour Activity" icon={Activity}>
          <ResponsiveContainer width="100%" height={250}>
            <BarChart data={data?.peakHourStats}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
              <XAxis dataKey="hour" stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} label={{ value: 'Hour of Day', position: 'insideBottom', offset: -5 }} />
              <YAxis stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} />
              <Tooltip cursor={{ fill: '#f1f5f9' }} />
              <Bar dataKey="count" fill="#8b5cf6" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </ChartContainer>

        {/* Slot Utilization Heatmap (Approx) */}
        <ChartContainer title="Slot Utilization Heatmap" icon={MapPin} className="lg:col-span-2">
          <div className="grid grid-cols-2 sm:grid-cols-5 md:grid-cols-10 gap-2">
            {data?.slotUtilization?.map((s, i) => {
              const intensity = Math.min(1, s.utilization / 10) // 10 bookings = 100% intensity for demo
              return (
                <div 
                  key={s.slot} 
                  className="p-3 rounded-xl border border-slate-100 dark:border-slate-800 flex flex-col items-center gap-1 transition-transform hover:scale-105"
                  style={{ backgroundColor: `rgba(14, 165, 233, ${0.05 + intensity * 0.9})` }}
                >
                  <span className={`text-xs font-bold ${intensity > 0.6 ? 'text-white' : 'text-brand-700'}`}>{s.slot}</span>
                  <span className={`text-[10px] ${intensity > 0.6 ? 'text-white/80' : 'text-slate-500'}`}>{s.utilization} bookings</span>
                </div>
              )
            })}
          </div>
          <div className="mt-6 flex items-center justify-end gap-2 text-[10px] text-slate-400 uppercase tracking-widest">
            <span>Low Use</span>
            <div className="flex h-2 w-24 rounded-full overflow-hidden bg-slate-100">
              <div className="h-full bg-brand-600" style={{ width: '100%', opacity: 0.1 }} />
              <div className="h-full bg-brand-600" style={{ width: '100%', opacity: 0.4 }} />
              <div className="h-full bg-brand-600" style={{ width: '100%', opacity: 0.7 }} />
              <div className="h-full bg-brand-600" style={{ width: '100%', opacity: 1 }} />
            </div>
            <span>High Use</span>
          </div>
        </ChartContainer>
      </div>
    </div>
  )
}

function StatCard({ icon: Icon, label, value, trend, color }) {
  return (
    <motion.div 
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className="card p-5 group hover:shadow-xl transition-all"
    >
      <div className="flex items-start justify-between">
        <div className={`${color} p-3 rounded-2xl text-white shadow-lg shadow-inherit/20`}>
          <Icon className="w-6 h-6" />
        </div>
        <div className="text-right">
          <p className="text-sm font-medium text-slate-400">{label}</p>
          <p className="text-2xl font-bold mt-1">{value}</p>
        </div>
      </div>
      <div className="mt-4 pt-4 border-t border-slate-50 dark:border-slate-800 flex items-center justify-between">
        <span className="text-xs font-semibold text-emerald-500">{trend}</span>
      </div>
    </motion.div>
  )
}

function ChartContainer({ title, icon: Icon, children, className }) {
  return (
    <motion.div 
      initial={{ opacity: 0, scale: 0.98 }}
      whileInView={{ opacity: 1, scale: 1 }}
      viewport={{ once: true }}
      className={`card p-6 ${className}`}
    >
      <div className="flex items-center gap-3 mb-6">
        <div className="bg-slate-100 dark:bg-slate-800 p-2 rounded-lg">
          <Icon className="w-5 h-5 text-slate-600 dark:text-slate-400" />
        </div>
        <h3 className="font-bold text-lg">{title}</h3>
      </div>
      {children}
    </motion.div>
  )
}
