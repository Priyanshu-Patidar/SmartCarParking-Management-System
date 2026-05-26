import { Link } from 'react-router-dom'
import { MapPin, Zap, Star, Car } from 'lucide-react'
import { motion } from 'framer-motion'

export default function ParkingCard({ parking, index = 0 }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay: index * 0.05 }}
      className="card hover:shadow-xl transition group overflow-hidden"
    >
      <div className="relative h-40 -mx-6 -mt-6 mb-4 overflow-hidden">
        <img
          src={parking.imageUrl || 'https://images.unsplash.com/photo-1506521781263-d8422e82f27a?w=600'}
          alt={parking.name}
          className="w-full h-full object-cover group-hover:scale-105 transition duration-500"
        />
        {parking.evChargingAvailable && (
          <span className="absolute top-3 right-3 bg-green-500 text-white text-xs px-2 py-1 rounded-full flex items-center gap-1">
            <Zap className="w-3 h-3" /> EV
          </span>
        )}
      </div>
      <h3 className="font-bold text-lg">{parking.name}</h3>
      <p className="text-sm text-slate-500 flex items-center gap-1 mt-1">
        <MapPin className="w-4 h-4" /> {parking.address}
      </p>
      <div className="flex flex-wrap gap-3 mt-4 text-sm">
        <span className="bg-brand-50 dark:bg-brand-900/30 text-brand-700 dark:text-brand-300 px-3 py-1 rounded-lg font-semibold">
          ₹{parking.hourlyRate}/hr
        </span>
        <span className="bg-green-50 dark:bg-green-900/30 text-green-700 px-3 py-1 rounded-lg">
          {parking.availableSlots} available
        </span>
        {parking.distanceKm != null && (
          <span className="text-slate-500">{parking.distanceKm} km</span>
        )}
        {parking.averageRating && (
          <span className="flex items-center gap-1 text-amber-500">
            <Star className="w-4 h-4 fill-current" /> {parking.averageRating}
          </span>
        )}
      </div>
      <div className="flex gap-2 mt-4">
        <Link to={`/booking/${parking.id}`} className="btn-primary flex-1 text-center text-sm">
          Book Now
        </Link>
        <Link to={`/map?location=${parking.id}`} className="btn-secondary text-sm">
          <Car className="w-4 h-4" />
        </Link>
      </div>
    </motion.div>
  )
}
