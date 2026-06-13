import { useEffect, useState } from 'react'
import { Search, Filter, FileText, Download, User, Calendar, Shield } from 'lucide-react'
import { adminApi } from '../../api/services'
import { formatDate } from '../../utils/dateUtils'

export default function AdminAudit() {
  const [logs, setLogs] = useState([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [query, setQuery] = useState('')

  useEffect(() => {
    adminApi.auditLogs(page)
      .then(({ data }) => setLogs(data.content))
      .finally(() => setLoading(false))
  }, [page])

  const filteredLogs = logs.filter(l => 
    l.userEmail?.toLowerCase().includes(query.toLowerCase()) ||
    l.action?.toLowerCase().includes(query.toLowerCase()) ||
    l.details?.toLowerCase().includes(query.toLowerCase())
  )

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold">Audit Explorer</h1>
          <p className="text-sm text-slate-500">Traceable system activity and security events</p>
        </div>
        <button className="btn-secondary flex items-center gap-2">
          <Download className="w-4 h-4" /> Export CSV
        </button>
      </div>

      <div className="card p-4">
        <div className="flex flex-col sm:flex-row gap-4 mb-6">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
            <input 
              type="text" 
              placeholder="Search by user, action, or details..." 
              className="input-field pl-10"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
            />
          </div>
          <button className="btn-secondary flex items-center gap-2">
            <Filter className="w-4 h-4" /> Filter
          </button>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead>
              <tr className="text-xs font-bold text-slate-400 uppercase tracking-widest border-b border-slate-100 dark:border-slate-800">
                <th className="px-4 py-3">Timestamp</th>
                <th className="px-4 py-3">User</th>
                <th className="px-4 py-3">Action</th>
                <th className="px-4 py-3">Details</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-50 dark:divide-slate-800/50">
              {loading ? (
                <tr><td colSpan="4" className="py-12 text-center text-slate-400 animate-pulse">Scanning records...</td></tr>
              ) : filteredLogs.map((log) => (
                <tr key={log.id} className="text-sm hover:bg-slate-50 dark:hover:bg-slate-800/50 transition-colors group">
                  <td className="px-4 py-4 whitespace-nowrap text-slate-500">
                    <div className="flex items-center gap-2">
                      <Calendar className="w-3.5 h-3.5" />
                      {formatDate(log.createdAt)}
                    </div>
                  </td>
                  <td className="px-4 py-4 font-medium">
                    <div className="flex items-center gap-2">
                      <User className="w-3.5 h-3.5 text-brand-500" />
                      {log.userEmail}
                    </div>
                  </td>
                  <td className="px-4 py-4">
                    <span className={`px-2 py-1 rounded-md text-[10px] font-bold uppercase ${
                      log.action.includes('CREATED') ? 'bg-emerald-100 text-emerald-700' :
                      log.action.includes('ERROR') ? 'bg-rose-100 text-rose-700' :
                      log.action.includes('LOGIN') ? 'bg-brand-100 text-brand-700' : 'bg-slate-100 text-slate-700'
                    }`}>
                      {log.action}
                    </span>
                  </td>
                  <td className="px-4 py-4 text-slate-600 dark:text-slate-400 max-w-xs truncate" title={log.details}>
                    {log.details}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
