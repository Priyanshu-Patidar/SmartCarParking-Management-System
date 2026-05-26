import { useEffect, useState } from 'react'
import { motion } from 'framer-motion'
import toast from 'react-hot-toast'
import { bookingApi, sessionApi } from '../api/services'
import { Download, X, LogIn, LogOut as LogOutIcon } from 'lucide-react'

export default function Bookings() {
  const [bookings, setBookings] = useState([])
  const [loading, setLoading] = useState(true)

  const load = () =>
    bookingApi
      .userBookings()
      .then(({ data }) => setBookings(data.content || []))
      .finally(() => setLoading(false))

  useEffect(() => {
    load()
  }, [])

  const cancel = async (id) => {
    try {
      await bookingApi.cancel(id)
      toast.success('Reservation cancelled successfully')
      load()
    } catch (err) {
      toast.error(err.response?.data?.message || 'Unable to cancel reservation')
    }
  }

  const checkIn = async (id) => {
    try {
      await sessionApi.checkIn(id)
      toast.success('Check-in recorded — your session is now active')
      load()
    } catch (err) {
      toast.error(err.response?.data?.message || 'Check-in failed')
    }
  }

  const checkOut = async (id) => {
    try {
      const { data } = await sessionApi.checkOut(id)
      toast.success(`Session complete. Final amount: ₹${data.finalAmount}`)
      load()
    } catch (err) {
      toast.error(err.response?.data?.message || 'Check-out failed')
    }
  }

  const downloadReceipt = (b) => {
    const text = [
      'SMARTPARK — PARKING RECEIPT',
      '────────────────────────────',
      `Confirmation: ${b.bookingCode}`,
      `Facility: ${b.locationName}`,
      `Address: ${b.locationAddress || ''}`,
      `Bay: ${b.slotNumber}`,
      `Vehicle: ${b.vehicleType}`,
      `Scheduled: ${new Date(b.startTime).toLocaleString()}`,
      `Duration: ${b.durationHours} hour(s)`,
      `Amount: ₹${b.actualFee || b.estimatedFee}`,
      b.paymentMethod ? `Paid via: ${b.paymentMethod}` : '',
      b.transactionId ? `Reference: ${b.transactionId}` : '',
      '────────────────────────────',
      'Thank you for choosing SmartPark.',
    ].join('\n')
    const blob = new Blob([text], { type: 'text/plain' })
    const a = document.createElement('a')
    a.href = URL.createObjectURL(blob)
    a.download = `SmartPark-${b.bookingCode}.txt`
    a.click()
  }

  return (
    <div>
      <h1 className="text-2xl font-bold">Reservations</h1>
      <p className="text-slate-500 mt-1">View confirmations, manage sessions, and download receipts.</p>

      {loading ? (
        <p className="mt-6 text-slate-500">Loading your reservations…</p>
      ) : (
        <div className="mt-6 space-y-4">
          {bookings.map((b, i) => (
            <motion.div
              key={b.id}
              initial={{ opacity: 0, x: -10 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: i * 0.04 }}
              className="card"
            >
              <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
                <div>
                  <p className="font-bold text-lg">{b.locationName}</p>
                  <p className="text-sm text-slate-500">
                    {b.bookingCode} · Bay {b.slotNumber}
                  </p>
                  <p className="text-sm mt-1 text-slate-600 dark:text-slate-400">
                    {new Date(b.startTime).toLocaleString()} · {b.durationHours}h reserved
                  </p>
                  <span
                    className={`inline-block mt-2 text-xs font-medium px-2.5 py-1 rounded-full ${
                      b.status === 'CONFIRMED'
                        ? 'bg-emerald-100 text-emerald-800 dark:bg-emerald-900/40 dark:text-emerald-300'
                        : b.status === 'ACTIVE'
                          ? 'bg-blue-100 text-blue-800'
                          : b.status === 'CANCELLED'
                            ? 'bg-red-100 text-red-700'
                            : 'bg-slate-100 text-slate-600'
                    }`}
                  >
                    {b.status}
                  </span>
                </div>
                <div className="flex flex-wrap items-center gap-2">
                  <p className="font-bold text-brand-600 text-lg mr-2">₹{b.actualFee || b.estimatedFee}</p>
                  {b.status === 'CONFIRMED' && (
                    <button onClick={() => checkIn(b.id)} className="btn-primary text-sm flex items-center gap-1">
                      <LogIn className="w-4 h-4" /> Check in
                    </button>
                  )}
                  {b.status === 'ACTIVE' && (
                    <button onClick={() => checkOut(b.id)} className="btn-primary text-sm flex items-center gap-1">
                      <LogOutIcon className="w-4 h-4" /> Check out
                    </button>
                  )}
                  <button onClick={() => downloadReceipt(b)} className="btn-secondary text-sm flex items-center gap-1">
                    <Download className="w-4 h-4" /> Receipt
                  </button>
                  {(b.status === 'CONFIRMED' || b.status === 'PENDING') && (
                    <button
                      onClick={() => cancel(b.id)}
                      className="p-2 text-red-600 hover:bg-red-50 dark:hover:bg-red-950 rounded-lg"
                      title="Cancel reservation"
                    >
                      <X className="w-5 h-5" />
                    </button>
                  )}
                </div>
              </div>
            </motion.div>
          ))}
          {bookings.length === 0 && (
            <div className="card text-center py-12 text-slate-500">
              <p>No reservations yet.</p>
              <a href="/search" className="text-brand-600 font-semibold mt-2 inline-block">
                Find parking near you →
              </a>
            </div>
          )}
        </div>
      )}
    </div>
  )
}
