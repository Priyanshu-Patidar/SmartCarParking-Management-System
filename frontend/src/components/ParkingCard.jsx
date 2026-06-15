import { Link } from 'react-router-dom'
import { MapPin, Zap, Star, Car } from 'lucide-react'
import { motion } from 'framer-motion'
import { memo } from 'react'

function ParkingCard({ parking, index = 0 }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay: index * 0.05 }}
      className="card hover:shadow-xl transition-all border-l-4 border-brand-500 group"
    >
      <div className="flex justify-between items-start">
        <div>
          <h3 className="font-bold text-lg group-hover:text-brand-600 transition-colors">{parking.name}</h3>
          <div className="flex items-center gap-1 text-slate-500 text-sm mt-1">
            <MapPin className="w-3 h-3" /> {parking.city}
          </div>
        </div>
        <div className="text-right">
          <p className="text-xl font-black text-brand-600">₹{parking.hourlyRate}</p>
          <p className="text-[10px] uppercase font-bold text-slate-400">per hour</p>
        </div>
      </div>

      <div className="grid grid-cols-2 gap-3 mt-4">
        <div className="bg-slate-50 dark:bg-slate-800 p-2 rounded-lg">
          <p className="text-[10px] text-slate-400 font-bold uppercase">Availability</p>
          <p className="text-sm font-bold flex items-center gap-1 mt-0.5">
            <Zap className={`w-3 h-3 ${parking.availableSlots > 0 ? 'text-amber-500' : 'text-slate-300'}`} />
            {parking.availableSlots} free
          </p>
        </div>
        <div className="bg-slate-50 dark:bg-slate-800 p-2 rounded-lg">
          <p className="text-[10px] text-slate-400 font-bold uppercase">Rating</p>
          <p className="text-sm font-bold flex items-center gap-1 mt-0.5">
            <Star className="w-3 h-3 text-brand-500 fill-brand-500" />
            {parking.averageRating || 'New'}
          </p>
        </div>
      </div>

      <div className="mt-4 flex gap-2">
        <Link to={`/booking/${parking.id}`} className="btn-primary text-sm flex-1 text-center">
          Book Now
        </Link>
        <Link to={`/map?location=${parking.id}`} className="btn-secondary text-sm">
          <Car className="w-4 h-4" />
        </Link>
      </div>
    </motion.div>
  )
}

export default memo(ParkingCard)
