import { useCallback, useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { motion } from 'framer-motion'
import toast from 'react-hot-toast'
import { parkingApi, bookingApi } from '../api/services'
import { QrCode, ArrowLeft, ArrowRight, Loader2 } from 'lucide-react'
import PaymentForm from '../components/PaymentForm'
import { formatLocalDateTimeForApi, addHoursToLocalDateTime, minDateTimeLocal } from '../utils/dateUtils'

const STEPS = ['Details', 'Payment', 'Confirmed']

export default function BookingPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [step, setStep] = useState(0)
  const [location, setLocation] = useState(null)
  const [slots, setSlots] = useState([])
  const [slotsLoading, setSlotsLoading] = useState(false)
  const [form, setForm] = useState({
    slotId: '',
    vehicleType: 'CAR',
    startTime: '',
    durationHours: 2,
    vehicleNumber: '',
  })
  const [payment, setPayment] = useState({ paymentMethod: 'UPI', upiId: '', cardHolderName: '', cardLastFour: '' })
  const [estimate, setEstimate] = useState(null)
  const [booking, setBooking] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    parkingApi.getById(id)
      .then(({ data }) => setLocation(data))
      .catch(() => toast.error('Location not found'))
  }, [id])

  const loadSlots = useCallback(async () => {
    if (!form.startTime || !id) return
    setSlotsLoading(true)
    setForm((f) => ({ ...f, slotId: '' }))
    try {
      const startTime = formatLocalDateTimeForApi(form.startTime)
      const endTime = addHoursToLocalDateTime(form.startTime, form.durationHours)
      const { data } = await parkingApi.getSlots(id, {
        vehicleType: form.vehicleType,
        startTime,
        endTime,
      })
      const list = Array.isArray(data) ? data : []
      setSlots(list)
      if (list.length === 0) {
        toast.error(
          `No ${form.vehicleType} slots free for this time. Try Car/Bike, another time, or search "${location?.city}" for more parkings.`,
          { duration: 5000 }
        )
      }
    } catch (err) {
      setSlots([])
      toast.error(err.response?.data?.message || 'Could not load slots')
    } finally {
      setSlotsLoading(false)
    }
  }, [form.startTime, form.durationHours, form.vehicleType, id])

  useEffect(() => {
    if (form.startTime) loadSlots()
  }, [form.startTime, form.durationHours, form.vehicleType, loadSlots])

  const loadEstimate = useCallback(async () => {
    if (!form.startTime || !id) return
    try {
      const { data } = await bookingApi.estimate(id, {
        vehicleType: form.vehicleType,
        startTime: formatLocalDateTimeForApi(form.startTime),
        durationHours: form.durationHours,
      })
      setEstimate(data)
    } catch {
      setEstimate(null)
    }
  }, [form.startTime, form.durationHours, form.vehicleType, id])

  useEffect(() => {
    if (form.startTime) loadEstimate()
  }, [form.startTime, form.durationHours, form.vehicleType, loadEstimate])

  const validateDetails = () => {
    if (!form.startTime) {
      toast.error('Select arrival time')
      return false
    }
    if (!form.slotId) {
      toast.error('Select an available slot')
      return false
    }
    if (!form.vehicleNumber?.trim()) {
      toast.error('Enter vehicle number')
      return false
    }
    return true
  }

  const validatePayment = () => {
    if (!payment.paymentMethod) {
      toast.error('Select a payment method')
      return false
    }
    if (payment.paymentMethod === 'UPI' && !payment.upiId?.trim()) {
      toast.error('Enter UPI ID')
      return false
    }
    if (payment.paymentMethod === 'CARD' && (!payment.cardHolderName || !payment.cardLastFour)) {
      toast.error('Complete card details')
      return false
    }
    return true
  }

  const handlePayAndBook = async () => {
    if (!validatePayment()) return
    setLoading(true)
    try {
      const { data } = await bookingApi.prebook({
        locationId: Number(id),
        slotId: Number(form.slotId),
        vehicleType: form.vehicleType,
        startTime: formatLocalDateTimeForApi(form.startTime),
        durationHours: form.durationHours,
        vehicleNumber: form.vehicleNumber,
        payment: {
          paymentMethod: payment.paymentMethod,
          upiId: payment.upiId,
          cardLastFour: payment.cardLastFour,
          cardHolderName: payment.cardHolderName,
        },
      })
      setBooking(data)
      setStep(2)
      toast.success('Payment successful — booking confirmed!')
    } catch (err) {
      toast.error(err.response?.data?.message || 'Booking failed')
    } finally {
      setLoading(false)
    }
  }

  if (booking && step === 2) {
    return (
      <div className="max-w-lg mx-auto px-4 py-12">
        <motion.div initial={{ scale: 0.9, opacity: 0 }} animate={{ scale: 1, opacity: 1 }} className="card text-center">
          <QrCode className="w-16 h-16 text-brand-600 mx-auto" />
          <h2 className="text-2xl font-bold mt-4">Booking Confirmed!</h2>
          <p className="text-slate-500 mt-2">Code: {booking.bookingCode}</p>
          {booking.qrCodeData?.startsWith('data:') && (
            <img src={booking.qrCodeData} alt="QR Code" className="mx-auto mt-4 w-48 h-48 rounded-lg" />
          )}
          <p className="mt-4 font-semibold text-lg">₹{booking.estimatedFee}</p>
          <p className="text-sm text-slate-500">{booking.locationName} — Slot {booking.slotNumber}</p>
          {booking.paymentMethod && (
            <p className="text-xs text-green-600 mt-2">
              Paid via {booking.paymentMethod} · {booking.transactionId}
            </p>
          )}
          <button onClick={() => navigate('/bookings')} className="btn-primary mt-6 w-full">
            View My Bookings
          </button>
        </motion.div>
      </div>
    )
  }

  if (!location) {
    return (
      <div className="p-12 text-center flex items-center justify-center gap-2">
        <Loader2 className="w-5 h-5 animate-spin" /> Loading...
      </div>
    )
  }

  return (
    <div className="max-w-3xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold">Book — {location.name}</h1>
      <p className="text-slate-500">{location.address}</p>

      <div className="flex gap-2 mt-6 mb-6">
        {STEPS.slice(0, 2).map((label, i) => (
          <div
            key={label}
            className={`flex-1 text-center py-2 rounded-lg text-sm font-medium ${
              step === i ? 'bg-brand-600 text-white' : step > i ? 'bg-green-100 text-green-700 dark:bg-green-900/30' : 'bg-slate-100 dark:bg-slate-800 text-slate-500'
            }`}
          >
            {i + 1}. {label}
          </div>
        ))}
      </div>

      <div className="card space-y-4">
        {step === 0 && (
          <>
            <div className="grid sm:grid-cols-2 gap-4">
              <div>
                <label className="text-sm font-medium">Vehicle Type</label>
                <select
                  className="input-field mt-1"
                  value={form.vehicleType}
                  onChange={(e) => setForm({ ...form, vehicleType: e.target.value, slotId: '' })}
                >
                  <option value="CAR">Car</option>
                  <option value="BIKE">Bike</option>
                  <option value="EV">EV</option>
                </select>
              </div>
              <div>
                <label className="text-sm font-medium">Arrival Time</label>
                <input
                  type="datetime-local"
                  className="input-field mt-1"
                  required
                  min={minDateTimeLocal()}
                  value={form.startTime}
                  onChange={(e) => setForm({ ...form, startTime: e.target.value, slotId: '' })}
                />
              </div>
              <div>
                <label className="text-sm font-medium">Duration (hours)</label>
                <input
                  type="number"
                  min="1"
                  max="24"
                  className="input-field mt-1"
                  value={form.durationHours}
                  onChange={(e) => setForm({ ...form, durationHours: +e.target.value, slotId: '' })}
                />
              </div>
              <div>
                <label className="text-sm font-medium">Vehicle Number</label>
                <input
                  className="input-field mt-1"
                  placeholder="MH12AB1234"
                  value={form.vehicleNumber}
                  onChange={(e) => setForm({ ...form, vehicleNumber: e.target.value })}
                />
              </div>
            </div>

            <div>
              <label className="text-sm font-medium">Available Slot</label>
              {slotsLoading ? (
                <p className="text-sm text-slate-500 mt-2 flex items-center gap-2">
                  <Loader2 className="w-4 h-4 animate-spin" /> Loading slots...
                </p>
              ) : !form.startTime ? (
                <p className="text-sm text-slate-500 mt-2">Select arrival time to see available slots</p>
              ) : slots.length === 0 ? (
                <p className="text-sm text-amber-600 dark:text-amber-400 mt-2">
                  No slots for {form.vehicleType}. Change vehicle type to Car/Bike, pick a different time, or try another parking in {location?.city}.
                </p>
              ) : (
                <select
                  className="input-field mt-1"
                  value={form.slotId}
                  onChange={(e) => setForm({ ...form, slotId: e.target.value })}
                >
                  <option value="">Select slot ({slots.length} available)</option>
                  {slots.map((s) => (
                    <option key={s.id} value={String(s.id)}>
                      {s.slotNumber} — Floor {s.floorNumber} ({s.vehicleType})
                      {s.evCharging ? ' · EV charging' : ''}
                    </option>
                  ))}
                </select>
              )}
            </div>

            {estimate != null && (
              <p className="text-lg font-bold text-brand-600">Estimated Fee: ₹{estimate}</p>
            )}

            <button
              type="button"
              onClick={() => {
                if (validateDetails()) setStep(1)
              }}
              className="btn-primary w-full flex items-center justify-center gap-2"
            >
              Continue to Payment <ArrowRight className="w-4 h-4" />
            </button>
          </>
        )}

        {step === 1 && (
          <>
            <button type="button" onClick={() => setStep(0)} className="text-sm text-brand-600 flex items-center gap-1">
              <ArrowLeft className="w-4 h-4" /> Back to details
            </button>
            <PaymentForm payment={payment} setPayment={setPayment} amount={estimate} />
            <button
              type="button"
              onClick={handlePayAndBook}
              disabled={loading}
              className="btn-primary w-full flex items-center justify-center gap-2"
            >
              {loading ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin" /> Processing payment...
                </>
              ) : (
                <>Pay ₹{estimate ?? '—'} & Confirm Booking</>
              )}
            </button>
          </>
        )}
      </div>
    </div>
  )
}
