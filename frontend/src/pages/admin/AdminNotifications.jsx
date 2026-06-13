import { useEffect, useState } from 'react'
import { Bell, Info, AlertCircle, CheckCircle2, Search, Trash2 } from 'lucide-react'
import { notificationApi } from '../../api/services'
import { formatDate } from '../../utils/dateUtils'
import toast from 'react-hot-toast'

export default function AdminNotifications() {
  const [notifications, setNotifications] = useState([])
  const [loading, setLoading] = useState(true)
  const [query, setQuery] = useState('')

  useEffect(() => {
    notificationApi.getAll(0) // Mocking admin access to all, usually needs a separate admin endpoint but reusing for now
      .then(({ data }) => setNotifications(data.content))
      .finally(() => setLoading(false))
  }, [])

  const filtered = notifications.filter(n => 
    n.title.toLowerCase().includes(query.toLowerCase()) || 
    n.message.toLowerCase().includes(query.toLowerCase()) ||
    n.user?.email.toLowerCase().includes(query.toLowerCase())
  )

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 dark:text-white">Notification Center</h1>
          <p className="text-sm text-slate-500">Monitor system-wide alerts and user communications</p>
        </div>
        <div className="flex items-center gap-2">
          <button className="btn-secondary">Mark all read</button>
          <button className="btn-primary">New System Alert</button>
        </div>
      </div>

      <div className="card p-4">
        <div className="relative mb-6">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
          <input 
            type="text" 
            placeholder="Search alerts or recipients..." 
            className="input-field pl-10"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
        </div>

        <div className="space-y-3">
          {loading ? (
            <div className="py-12 text-center text-slate-400 animate-pulse">Loading broadcast history...</div>
          ) : filtered.map((n) => (
            <div key={n.id} className={`p-4 rounded-2xl border flex items-start justify-between group transition-all ${n.read ? 'bg-white dark:bg-slate-900 border-slate-100 dark:border-slate-800' : 'bg-brand-50/30 dark:bg-brand-950/10 border-brand-100 dark:border-brand-900'}`}>
              <div className="flex gap-4">
                <div className={`p-2 rounded-xl h-fit ${n.type === 'BOOKING_CONFIRMED' ? 'bg-emerald-100 text-emerald-600' : 'bg-brand-100 text-brand-600'}`}>
                  <Bell className="w-5 h-5" />
                </div>
                <div>
                  <div className="flex items-center gap-2">
                    <h4 className="font-bold text-sm text-slate-900 dark:text-white">{n.title}</h4>
                    {!n.read && <span className="w-2 h-2 rounded-full bg-brand-500" />}
                  </div>
                  <p className="text-sm text-slate-600 dark:text-slate-400 mt-0.5">{n.message}</p>
                  <div className="flex items-center gap-3 mt-2 text-[10px] font-bold text-slate-400 uppercase tracking-widest">
                    <span>{formatDate(n.createdAt)}</span>
                    <span>•</span>
                    <span className="text-brand-600">{n.user?.email || 'System'}</span>
                  </div>
                </div>
              </div>
              <button className="p-2 text-slate-400 hover:text-rose-500 opacity-0 group-hover:opacity-100 transition-opacity">
                <Trash2 className="w-4 h-4" />
              </button>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
