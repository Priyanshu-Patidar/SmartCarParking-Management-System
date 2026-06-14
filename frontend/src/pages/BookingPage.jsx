import { useCallback, useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'
import { parkingApi, bookingApi } from '../api/services'
import { QrCode, ArrowLeft, ArrowRight, Loader2, LayoutGrid, TrendingUp, AlertTriangle } from 'lucide-react'
import PaymentForm from '../components/PaymentForm'
import ParkingLayoutModal from '../components/ParkingLayoutModal'
import PricingBreakdown from '../components/PricingBreakdown'
import { formatLocalDateTimeForApi, addHoursToLocalDateTime, minDateTimeLocal } from '../utils/dateUtils'

const STEPS = ['Details', 'Payment', 'Confirmed']

export default function BookingPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  
  const [step, setStep] = useState(0)
  const [location, setLocation] = useState(null)
  const [error, setError] = useState(null)
  const [slots, setSlots] = useState([])
  const [slotsLoading, setSlotsLoading] = useState(false)
  const [showLayout, setShowLayout] = useState(false)
  
  const [form, setForm] = useState({
    slotId: '',
    vehicleType: 'CAR',
    startTime: '',
    durationHours: 2,
    vehicleNumber: '',
  })
  
  const [payment, setPayment] = useState({ 
    paymentMethod: 'UPI', 
    upiId: '', 
    cardHolderName: '', 
    cardLastFour: '' 
  })
  
  const [estimate, setEstimate] = useState(null)
  const [breakdown, setBreakdown] = useState(null)
  const [booking, setBooking] = useState(null)
  const [loading, setLoading] = useState(false)

  // 1. Initial Data Load
  useEffect(() => {
    if (!id || id === 'undefined') {
      setError('Please select a parking location from the search results.')
      return
    }

    const loadFacility = async () => {
      try {
        const { data } = await parkingApi.getById(id)
        if (!data) throw new Error('Facility data missing')
        setLocation(data)
      } catch (err) {
        console.error('API Fetch Error:', err)
        setError(err.response?.data?.message || 'Unable to connect to the parking facility. Please try again.')
      }
    }
    loadFacility()
  }, [id])

  // 2. Slot Discovery
  const loadSlots = useCallback(async () => {
    if (!form.startTime || !id || !location) return
    setSlotsLoading(true)
    try {
      const start = formatLocalDateTimeForApi(form.startTime)
      const end = addHoursToLocalDateTime(form.startTime, form.durationHours)
      const { data } = await parkingApi.getSlots(id, {
        vehicleType: form.vehicleType,
        startTime: start,
        endTime: end,
      })
      setSlots(Array.isArray(data) ? data : [])
    } catch (err) {
      console.error('Slots error:', err)
      setSlots([])
    } finally {
      setSlotsLoading(false)
    }
  }, [form.startTime, form.durationHours, form.vehicleType, id, location])

  useEffect(() => {
    if (form.startTime && location) loadSlots()
  }, [form.startTime, form.durationHours, form.vehicleType, loadSlots, location])

  // 3. Dynamic Pricing
  const loadEstimate = useCallback(async () => {
    if (!form.startTime || !id || !location) return
    try {
      const { data } = await bookingApi.estimateBreakdown(id, {
        vehicleType: form.vehicleType,
        startTime: formatLocalDateTimeForApi(form.startTime),
        durationHours: form.durationHours,
      })
      setBreakdown(data)
      setEstimate(data.totalAmount)
    } catch (err) {
      console.error('Pricing error:', err)
    }
  }, [form.startTime, form.durationHours, form.vehicleType, id, location])

  useEffect(() => {
    if (form.startTime && location) loadEstimate()
  }, [form.startTime, form.durationHours, form.vehicleType, loadEstimate, location])

  // 4. Final Booking Submission
  const handlePayAndBook = async () => {
    if (!form.slotId || !form.vehicleNumber) {
      toast.error('All fields are required to secure your spot.')
      return
    }
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
           ...payment,
           upiId: payment.paymentMethod === 'UPI' ? '9617248701@ybl' : payment.upiId
        },
      })
      setBooking(data)
      setStep(2)
      toast.success('Parking Spot Secured!')
    } catch (err) {
      console.error('Booking Submission Error:', err)
      toast.error(err.response?.data?.message || 'Transaction failed. Please check your data.')
    } finally {
      setLoading(false)
    }
  }

  // UI STATE: Error
  if (error) {
    return (
      <div className="max-w-2xl mx-auto px-4 py-32 text-center">
        <div className="card border-rose-100 bg-rose-50/20 p-12">
          <AlertTriangle className="w-16 h-16 text-rose-500 mx-auto mb-6" />
          <h2 className="text-3xl font-black text-slate-900">System Error</h2>
          <p className="text-slate-500 mt-4 text-lg leading-relaxed">{error}</p>
          <div className="flex gap-4 justify-center mt-12">
            <button onClick={() => navigate('/search')} className="btn-primary">Browse Other Spots</button>
            <button onClick={() => window.location.reload()} className="btn-secondary">Retry Connection</button>
          </div>
        </div>
      </div>
    )
  }

  // UI STATE: Confirmed
  if (booking && step === 2) {
    return (
      <div className="max-w-xl mx-auto px-4 py-12">
        <motion.div 
          initial={{ scale: 0.95, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          className="card text-center shadow-2xl p-12 border-emerald-100 bg-white dark:bg-slate-900 rounded-[3.5rem]"
        >
          <div className="w-24 h-24 bg-emerald-50 dark:bg-emerald-950/30 rounded-full flex items-center justify-center mx-auto mb-8 relative">
             <div className="absolute inset-0 rounded-full border-4 border-emerald-500/20 animate-ping" />
             <CheckCircle2 className="w-12 h-12 text-emerald-500 relative z-10" />
          </div>

          <h2 className="text-4xl font-black text-slate-900 dark:text-white uppercase tracking-tighter">Spot Secured!</h2>
          <p className="text-slate-500 mt-3 font-bold uppercase tracking-widest text-[10px]">Reference ID: {booking.bookingCode}</p>
          
          {booking.qrCodeData && (
            <div className="mt-12 p-8 bg-slate-50 dark:bg-slate-800/50 rounded-[3rem] border border-slate-100 dark:border-slate-700 inline-block relative group">
               <div className="absolute -top-4 left-1/2 -translate-x-1/2 bg-brand-600 text-white text-[9px] font-black px-4 py-1 rounded-full uppercase tracking-widest">Digital Entry Pass</div>
               <img src={booking.qrCodeData} alt="Gate Pass" className="w-56 h-56 rounded-2xl shadow-2xl group-hover:scale-105 transition-transform duration-500" />
               <p className="mt-6 text-[10px] font-black text-slate-400 uppercase tracking-[0.3em]">Scan at Facility Gates</p>
            </div>
          )}

          <div className="mt-12 pt-8 border-t border-slate-100 dark:border-slate-800 grid grid-cols-2 gap-4 text-left">
            <div className="bg-slate-50 dark:bg-slate-800/30 p-4 rounded-2xl">
               <p className="text-[9px] font-black text-slate-400 uppercase mb-1">Facility</p>
               <p className="text-sm font-bold text-slate-900 dark:text-white truncate">{booking.locationName}</p>
            </div>
            <div className="bg-slate-50 dark:bg-slate-800/30 p-4 rounded-2xl text-center">
               <p className="text-[9px] font-black text-slate-400 uppercase mb-1">Spot No</p>
               <p className="text-lg font-black text-brand-600">{booking.slotNumber}</p>
            </div>
            <div className="col-span-2 bg-brand-50 dark:bg-brand-950/20 p-5 rounded-2xl flex justify-between items-center border border-brand-100 dark:border-brand-900/50">
               <p className="text-xs font-black text-brand-700 dark:text-brand-300 uppercase tracking-widest">Amount Paid</p>
               <p className="text-2xl font-black text-slate-900 dark:text-white tracking-tighter">₹{booking.estimatedFee}</p>
            </div>
          </div>
          
          <button 
            onClick={() => navigate('/bookings')} 
            className="btn-primary mt-12 w-full py-6 text-lg uppercase font-black tracking-widest rounded-3xl shadow-2xl hover:scale-[1.02] active:scale-95 transition-all"
          >
            My Active Passes
          </button>
        </motion.div>
      </div>
    )
  }

  // UI STATE: Loading
  if (!location) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-32 text-center">
        <div className="inline-flex flex-col items-center gap-8">
          <div className="relative">
             <div className="w-20 h-20 border-4 border-slate-100 dark:border-slate-800 rounded-full" />
             <div className="w-20 h-20 border-4 border-brand-600 border-t-transparent rounded-full animate-spin absolute top-0" />
          </div>
          <div>
             <p className="text-2xl font-black text-slate-900 dark:text-white uppercase tracking-tighter">Syncing Vitals</p>
             <p className="text-slate-400 font-bold text-sm mt-2 animate-pulse uppercase tracking-[0.2em]">Contacting Facility Hub...</p>
          </div>
        </div>
      </div>
    )
  }

  // MAIN RENDER
  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      {/* Header */}
      <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-8 mb-12">
        <div className="space-y-2">
          <div className="flex items-center gap-3">
             <div className="w-12 h-12 bg-brand-600 rounded-2xl flex items-center justify-center shadow-lg shadow-brand-500/30">
                <LayoutGrid className="text-white w-6 h-6" />
             </div>
             <h1 className="text-4xl font-black tracking-tight text-slate-900 dark:text-white uppercase">{location.name}</h1>
          </div>
          <p className="text-slate-500 text-lg flex items-center gap-2 pl-1">
            <ArrowRight className="w-5 h-5 rotate-90 text-brand-500" /> {location.address}
          </p>
        </div>
        <button
          onClick={() => setShowLayout(true)}
          className="btn-secondary group flex items-center gap-3 px-10 py-5 shadow-xl hover:shadow-2xl transition-all rounded-3xl"
        >
          <LayoutGrid className="w-6 h-6 text-brand-600 group-hover:rotate-90 transition-transform duration-500" /> 
          <span className="font-black text-xs uppercase tracking-[0.2em]">Live Floor Plan</span>
        </button>
      </div>

      {showLayout && <ParkingLayoutModal locationId={id} onClose={() => setShowLayout(false)} />}

      {/* Steps Progress */}
      <div className="flex gap-4 mb-16">
        {STEPS.slice(0, 2).map((label, i) => (
          <div
            key={label}
            className={`flex-1 text-center py-6 rounded-[2rem] text-[10px] font-black uppercase tracking-[0.3em] transition-all border-2 ${
              step === i ? 'bg-brand-600 text-white border-brand-500 shadow-2xl shadow-brand-500/40' : 'bg-white dark:bg-slate-900 text-slate-400 border-slate-100 dark:border-slate-800'
            }`}
          >
            {i + 1}. {label}
          </div>
        ))}
      </div>

      <div className="grid lg:grid-cols-12 gap-12">
        {/* Main Form Area */}
        <div className="lg:col-span-7">
          <div className="card !p-12 shadow-2xl rounded-[3rem]">
            {step === 0 ? (
              <div className="space-y-10">
                <div className="grid sm:grid-cols-2 gap-10">
                  <FormField label="Vehicle Category">
                    <select
                      className="input-field !py-4"
                      value={form.vehicleType}
                      onChange={(e) => setForm({ ...form, vehicleType: e.target.value, slotId: '' })}
                    >
                      <option value="CAR">Four Wheeler (Car)</option>
                      <option value="BIKE">Two Wheeler (Bike)</option>
                      <option value="EV">Electric Vehicle (EV)</option>
                    </select>
                  </FormField>
                  <FormField label="Arrival Time">
                    <input
                      type="datetime-local"
                      className="input-field !py-4"
                      min={minDateTimeLocal()}
                      value={form.startTime}
                      onChange={(e) => setForm({ ...form, startTime: e.target.value, slotId: '' })}
                    />
                  </FormField>
                  <FormField label="Stay Duration">
                    <div className="relative">
                      <input
                        type="number"
                        min="1"
                        className="input-field !py-4 pr-16"
                        value={form.durationHours}
                        onChange={(e) => setForm({ ...form, durationHours: +e.target.value, slotId: '' })}
                      />
                      <span className="absolute right-6 top-1/2 -translate-y-1/2 text-[10px] font-black text-slate-400">HRS</span>
                    </div>
                  </FormField>
                  <FormField label="Registration Plate">
                    <input
                      className="input-field !py-4"
                      placeholder="e.g. MH 12 AB 1234"
                      value={form.vehicleNumber}
                      onChange={(e) => setForm({ ...form, vehicleNumber: e.target.value })}
                    />
                  </FormField>
                </div>

                <div className="space-y-4">
                  <label className="text-[10px] font-black uppercase tracking-[0.3em] text-slate-400 ml-2">Secure Spot Selection</label>
                  {slotsLoading ? (
                    <div className="p-8 bg-slate-50 dark:bg-slate-800/50 rounded-[2rem] text-center flex flex-col items-center gap-4 border border-slate-100 dark:border-slate-700">
                      <Loader2 className="w-8 h-8 animate-spin text-brand-600" />
                      <span className="text-xs font-black text-slate-500 uppercase tracking-widest">Locking secure connection...</span>
                    </div>
                  ) : (
                    <select
                      className="input-field !py-5 !text-lg font-bold border-brand-500/10 focus:border-brand-500"
                      value={form.slotId}
                      onChange={(e) => setForm({ ...form, slotId: e.target.value })}
                    >
                      <option value="">{slots.length > 0 ? `→ Select from ${slots.length} optimized spots` : 'No spots found for this period'}</option>
                      {slots.map((s) => (
                        <option key={s.id} value={String(s.id)}>
                          BLOCK {s.slotNumber} — FLOOR {s.floorNumber}
                        </option>
                      ))}
                    </select>
                  )}
                </div>

                <button 
                  onClick={() => setStep(1)} 
                  disabled={!form.slotId || !form.startTime || !form.vehicleNumber}
                  className="btn-primary w-full py-6 text-xl uppercase font-black tracking-widest shadow-2xl hover:scale-[1.02] active:scale-95 transition-all disabled:opacity-30 disabled:grayscale rounded-[2rem]"
                >
                  Proceed to Secure Checkout
                </button>
              </div>
            ) : (
              <div className="space-y-10">
                <button onClick={() => setStep(0)} className="text-[10px] font-black uppercase tracking-[0.3em] text-brand-600 hover:text-brand-500 flex items-center gap-3 transition-all hover:-translate-x-1">
                  <ArrowLeft className="w-5 h-5" /> Back to Facility Info
                </button>
                <div className="p-2">
                   <PaymentForm payment={payment} setPayment={setPayment} amount={estimate} />
                </div>
                <button 
                  onClick={handlePayAndBook} 
                  disabled={loading} 
                  className="btn-primary w-full py-6 text-xl uppercase font-black tracking-widest shadow-2xl hover:scale-[1.02] active:scale-95 transition-all rounded-[2rem]"
                >
                  {loading ? 'Securing Transaction...' : `Pay ₹${estimate} & Confirm`}
                </button>
              </div>
            )}
          </div>
        </div>

        {/* Sidebar Analytics/Pricing */}
        <div className="lg:col-span-5 space-y-10">
          <PricingBreakdown breakdown={breakdown} />
          
          <div className="card glass !p-10 relative overflow-hidden rounded-[3rem]">
             <div className="absolute top-0 right-0 w-48 h-48 bg-brand-500/5 rounded-full blur-[80px] -mr-24 -mt-24" />
             <div className="relative z-10">
                <h5 className="font-black text-xs uppercase tracking-[0.3em] text-brand-700 dark:text-brand-400 mb-6 flex items-center gap-3">
                  <TrendingUp className="w-5 h-5" />
                  Dynamic Intelligence
                </h5>
                <p className="text-sm text-slate-600 dark:text-slate-400 leading-loose font-medium">
                  SmartPark utilizes AI to adjust rates in real-time based on local facility demand, 
                  upcoming events, and historic traffic data. Your rate is locked once payment is initiated.
                </p>
                <div className="mt-8 pt-8 border-t border-slate-100 dark:border-slate-800 flex items-center gap-6">
                   <div className="flex -space-x-4">
                      {[1,2,3,4].map(i => <div key={i} className="w-10 h-10 rounded-full bg-slate-200 dark:bg-slate-800 border-4 border-white dark:border-slate-900 shadow-lg" />)}
                   </div>
                   <div>
                      <p className="text-xs font-black text-slate-900 dark:text-white uppercase tracking-tight">Verified Community</p>
                      <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest mt-0.5">Join 12k+ Smart Drivers</p>
                   </div>
                </div>
             </div>
          </div>
        </div>
      </div>
    </div>
  )
}

function FormField({ label, children }) {
  return (
    <div className="space-y-3">
      <label className="text-[10px] font-black uppercase tracking-[0.3em] text-slate-400 ml-2">{label}</label>
      {children}
    </div>
  )
}
