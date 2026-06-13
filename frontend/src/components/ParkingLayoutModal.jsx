import { useEffect, useState, useMemo } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { 
  X, Car, Bike, Zap, Info, Layers, 
  CheckCircle2, AlertCircle, Clock, Construction 
} from 'lucide-react'
import { parkingApi } from '../api/services'
import toast from 'react-hot-toast'
import SockJS from 'sockjs-client'
import { Stomp } from '@stomp/stompjs'

const API_BASE = import.meta.env.VITE_API_BASE_URL || '/api'

const STATUS_CONFIG = {
  AVAILABLE: { color: 'bg-emerald-500', label: 'Available', icon: CheckCircle2, border: 'border-emerald-200' },
  OCCUPIED: { color: 'bg-rose-500', label: 'Occupied', icon: AlertCircle, border: 'border-rose-200' },
  RESERVED: { color: 'bg-amber-500', label: 'Reserved', icon: Clock, border: 'border-amber-200' },
  MAINTENANCE: { color: 'bg-slate-500', label: 'Maintenance', icon: Construction, border: 'border-slate-300' },
}

const VEHICLE_ICONS = {
  CAR: Car,
  BIKE: Bike,
  EV: Zap,
}

export default function ParkingLayoutModal({ locationId, onClose }) {
  const [slots, setSlots] = useState([])
  const [loading, setLoading] = useState(true)
  const [activeFloor, setActiveFloor] = useState(null)

  const fetchLayout = async () => {
    try {
      const { data } = await parkingApi.getSlots(locationId)
      setSlots(data)
      if (data.length > 0 && activeFloor === null) {
        setActiveFloor(data[0].floorNumber)
      }
    } catch (err) {
      toast.error('Failed to load parking layout')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchLayout()

    // WebSocket for live updates
    const socket = new SockJS(`${API_BASE.replace('/api', '')}/ws`)
    const stompClient = Stomp.over(socket)
    stompClient.debug = () => {} // Disable debug logs

    stompClient.connect({}, () => {
      stompClient.subscribe(`/topic/parking/${locationId}/slots`, () => {
        fetchLayout() // Re-fetch on any update
      })
    })

    return () => {
      if (stompClient.connected) {
        stompClient.disconnect()
      }
    }
  }, [locationId])

  const floors = useMemo(() => {
    const grouped = slots.reduce((acc, slot) => {
      if (!acc[slot.floorNumber]) acc[slot.floorNumber] = []
      acc[slot.floorNumber].push(slot)
      return acc
    }, {})
    return Object.entries(grouped)
      .sort(([a], [b]) => Number(a) - Number(b))
      .map(([num, floorSlots]) => ({
        number: Number(num),
        name: floorSlots[0].floorName || `Floor ${num}`,
        slots: floorSlots.sort((a, b) => a.slotNumber.localeCompare(b.slotNumber))
      }))
  }, [slots])

  const currentFloor = floors.find(f => f.number === activeFloor)
  
  const stats = useMemo(() => {
    if (!slots.length) return null
    const total = slots.length
    const occupied = slots.filter(s => s.status === 'OCCUPIED' || s.status === 'RESERVED').length
    const available = slots.filter(s => s.status === 'AVAILABLE').length
    return {
      total,
      occupied,
      available,
      occupancyPercent: Math.round((occupied / total) * 100)
    }
  }, [slots])

  return (
    <div className="fixed inset-0 z-[60] flex items-center justify-center p-4 sm:p-6 bg-slate-900/60 backdrop-blur-sm">
      <motion.div 
        initial={{ opacity: 0, scale: 0.95, y: 20 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        exit={{ opacity: 0, scale: 0.95, y: 20 }}
        className="bg-white dark:bg-slate-900 w-full max-w-5xl max-h-[90vh] rounded-3xl shadow-2xl overflow-hidden flex flex-col border border-white/20"
      >
        {/* Header */}
        <div className="p-6 border-b border-slate-100 dark:border-slate-800 flex items-center justify-between bg-white/50 dark:bg-slate-900/50 backdrop-blur-md sticky top-0 z-10">
          <div>
            <h2 className="text-2xl font-bold text-slate-900 dark:text-white flex items-center gap-2">
              <Layers className="w-6 h-6 text-brand-600" />
              Live Floor Layout
            </h2>
            <p className="text-slate-500 text-sm mt-1">Real-time occupancy visualization</p>
          </div>
          <button 
            onClick={onClose}
            className="p-2 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-full transition-colors"
          >
            <X className="w-6 h-6 text-slate-400" />
          </button>
        </div>

        <div className="flex-1 overflow-y-auto p-6 space-y-8">
          {loading ? (
            <div className="h-64 flex flex-col items-center justify-center gap-4 text-slate-400">
              <div className="w-12 h-12 border-4 border-brand-500 border-t-transparent rounded-full animate-spin" />
              <p className="animate-pulse">Loading facility layout...</p>
            </div>
          ) : (
            <>
              {/* Stats Panel */}
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                <StatCard label="Total Capacity" value={stats?.total} sub="Slots" />
                <StatCard label="Currently Free" value={stats?.available} sub="Available" color="text-emerald-500" />
                <StatCard label="Occupied" value={stats?.occupied} sub="Parked/Reserved" color="text-rose-500" />
                <div className="card p-4 bg-brand-50/50 dark:bg-brand-950/20 border-brand-100 dark:border-brand-900">
                  <p className="text-xs font-semibold text-brand-600 uppercase tracking-wider">Occupancy</p>
                  <div className="flex items-end gap-2 mt-1">
                    <p className="text-3xl font-bold text-slate-900 dark:text-white">{stats?.occupancyPercent}%</p>
                    <div className="flex-1 h-2 bg-slate-200 dark:bg-slate-700 rounded-full mb-2 overflow-hidden">
                      <div 
                        className="h-full bg-brand-600 transition-all duration-1000" 
                        style={{ width: `${stats?.occupancyPercent}%` }}
                      />
                    </div>
                  </div>
                </div>
              </div>

              {/* Floor Tabs */}
              <div className="flex flex-wrap gap-2 p-1 bg-slate-100 dark:bg-slate-800 rounded-2xl w-fit">
                {floors.map(floor => (
                  <button
                    key={floor.number}
                    onClick={() => setActiveFloor(floor.number)}
                    className={`px-6 py-2.5 rounded-xl text-sm font-semibold transition-all ${
                      activeFloor === floor.number 
                        ? 'bg-white dark:bg-slate-700 text-brand-600 shadow-sm' 
                        : 'text-slate-500 hover:text-slate-700'
                    }`}
                  >
                    {floor.name}
                  </button>
                ))}
              </div>

              {/* Grid Layout */}
              <div className="space-y-4">
                <div className="flex items-center justify-between">
                  <h3 className="font-bold text-lg flex items-center gap-2">
                    {currentFloor?.name}
                    <span className="text-xs font-normal text-slate-400 bg-slate-100 dark:bg-slate-800 px-2 py-1 rounded-md">
                      {currentFloor?.slots.length} Slots
                    </span>
                  </h3>
                  <div className="flex items-center gap-4 text-xs">
                    {Object.entries(STATUS_CONFIG).map(([key, config]) => (
                      <div key={key} className="flex items-center gap-1.5">
                        <div className={`w-3 h-3 rounded-full ${config.color}`} />
                        <span className="text-slate-500">{config.label}</span>
                      </div>
                    ))}
                  </div>
                </div>

                <div className="grid grid-cols-2 sm:grid-cols-4 md:grid-cols-6 lg:grid-cols-8 gap-3">
                  {currentFloor?.slots.map(slot => {
                    const status = STATUS_CONFIG[slot.status] || STATUS_CONFIG.AVAILABLE
                    const Icon = VEHICLE_ICONS[slot.vehicleType] || Car
                    return (
                      <motion.div
                        key={slot.id}
                        whileHover={{ scale: 1.05 }}
                        className={`relative group p-3 rounded-2xl border-2 transition-all flex flex-col items-center gap-2 cursor-help ${status.border} ${status.color.replace('bg-', 'bg-')}/5 hover:shadow-lg`}
                        title={`${slot.slotNumber} - ${status.label} (${slot.vehicleType})`}
                      >
                        <div className={`p-2 rounded-xl ${status.color} text-white shadow-lg shadow-${status.color.replace('bg-', '')}/30`}>
                          <Icon className="w-5 h-5" />
                        </div>
                        <span className="text-xs font-bold text-slate-700 dark:text-slate-300">{slot.slotNumber}</span>
                        {slot.evCharging && (
                          <div className="absolute top-1 right-1">
                            <Zap className="w-3 h-3 text-brand-500 fill-brand-500" />
                          </div>
                        )}
                        
                        {/* Hover Detail Overlay */}
                        <div className="absolute inset-0 opacity-0 group-hover:opacity-100 transition-opacity bg-white/90 dark:bg-slate-800/90 rounded-2xl flex flex-col items-center justify-center p-2 text-center pointer-events-none">
                          <p className="text-[10px] font-bold text-brand-600 uppercase">{slot.status}</p>
                          <p className="text-[10px] text-slate-500">{slot.vehicleType}</p>
                        </div>
                      </motion.div>
                    )
                  })}
                </div>
              </div>
            </>
          )}
        </div>

        {/* Footer */}
        <div className="p-4 bg-slate-50 dark:bg-slate-800/50 border-t border-slate-100 dark:border-slate-800 flex justify-center">
          <p className="text-[10px] text-slate-400 uppercase tracking-widest flex items-center gap-2">
            <Info className="w-3 h-3" />
            Live data synchronized via WebSocket
          </p>
        </div>
      </motion.div>
    </div>
  )
}

function StatCard({ label, value, sub, color = 'text-slate-900 dark:text-white' }) {
  return (
    <div className="card p-4">
      <p className="text-xs font-semibold text-slate-400 uppercase tracking-wider">{label}</p>
      <div className="flex items-baseline gap-1 mt-1">
        <p className={`text-2xl font-bold ${color}`}>{value ?? '—'}</p>
        <p className="text-[10px] text-slate-400 font-medium">{sub}</p>
      </div>
    </div>
  )
}
