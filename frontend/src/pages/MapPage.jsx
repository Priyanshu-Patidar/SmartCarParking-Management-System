import { useEffect, useState, useCallback } from 'react'
import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet'
import L from 'leaflet'
import { parkingApi } from '../api/services'
import { Link } from 'react-router-dom'
import { Zap, Star, RefreshCw } from 'lucide-react'
import toast from 'react-hot-toast'

const icon = new L.Icon({
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
})

function MapController({ center }) {
  const map = useMap()
  useEffect(() => {
    if (center?.[0] && center?.[1]) map.setView(center, 13)
  }, [center, map])
  return null
}

function getErrorMessage(err) {
  if (err.response?.status === 429) return 'Too many requests — please wait'
  if (err.code === 'ERR_NETWORK') return 'Backend not reachable (start Spring Boot on port 8080)'
  return err.response?.data?.message || 'Failed to load parking'
}

export default function MapPage() {
  const [locations, setLocations] = useState([])
  const [center, setCenter] = useState([12.9716, 77.5946])
  const [selected, setSelected] = useState(null)
  const [loading, setLoading] = useState(true)

  const loadParking = useCallback(async (lat, lng) => {
    setLoading(true)
    try {
      const { data } = await parkingApi.nearby({ lat, lng, radiusKm: 100, sortBy: 'distance' })
      let list = Array.isArray(data) ? data : []
      if (list.length === 0) {
        const cities = ['Bangalore', 'Mumbai', 'Delhi', 'Pune', 'Hyderabad']
        for (const city of cities) {
          try {
            const res = await parkingApi.search(city, 'distance')
            if (res.data?.length) {
              list = res.data
              toast.success(`Showing parking in ${city}`)
              break
            }
          } catch {
            /* try next city */
          }
        }
      }
      setLocations(list)
      setSelected(list[0] || null)
      if (list.length === 0) {
        toast.error('No parking data in database. Restart backend to seed locations.')
      }
    } catch (err) {
      setLocations([])
      toast.error(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    navigator.geolocation?.getCurrentPosition(
      (p) => {
        const { latitude, longitude } = p.coords
        setCenter([latitude, longitude])
        loadParking(latitude, longitude)
      },
      () => {
        setCenter([12.9716, 77.5946])
        loadParking(12.9716, 77.5946)
      }
    )
  }, [loadParking])

  return (
    <div className="h-[calc(100vh-4rem)] flex flex-col lg:flex-row">
      <div className="flex-1 relative min-h-[50vh]">
        <MapContainer center={center} zoom={13} className="h-full w-full" style={{ height: '100%', minHeight: 400 }}>
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          <MapController center={center} />
          {locations.map((loc) => (
            <Marker
              key={loc.id}
              position={[loc.latitude, loc.longitude]}
              icon={icon}
              eventHandlers={{ click: () => setSelected(loc) }}
            >
              <Popup>
                <strong>{loc.name}</strong>
                <br />
                ₹{loc.hourlyRate}/hr — {loc.availableSlots} slots
              </Popup>
            </Marker>
          ))}
        </MapContainer>
      </div>
      <aside className="w-full lg:w-96 bg-white dark:bg-slate-900 border-l border-slate-200 dark:border-slate-800 overflow-y-auto p-4">
        <div className="flex items-center justify-between mb-4">
          <h2 className="font-bold text-lg">Nearby Parking ({locations.length})</h2>
          <button
            type="button"
            onClick={() => loadParking(center[0], center[1])}
            className="p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800"
            title="Refresh"
          >
            <RefreshCw className="w-4 h-4" />
          </button>
        </div>
        {loading ? (
          <p className="text-slate-500">Loading parking locations...</p>
        ) : locations.length === 0 ? (
          <p className="text-slate-500 text-sm">
            No locations loaded. Restart the backend (<code>mvn spring-boot:run</code>) and click refresh.
          </p>
        ) : (
          <>
            {selected && (
              <div className="card mb-4 border-2 border-brand-500">
                <img src={selected.imageUrl} alt="" className="w-full h-32 object-cover rounded-xl mb-3" />
                <h3 className="font-bold">{selected.name}</h3>
                <p className="text-sm text-slate-500">{selected.address}</p>
                <div className="flex gap-2 mt-2 text-sm flex-wrap">
                  <span className="text-brand-600 font-bold">₹{selected.hourlyRate}/hr</span>
                  <span>
                    {selected.availableSlots}/{selected.totalSlots} free
                  </span>
                  {selected.evChargingAvailable && (
                    <span className="text-green-600 flex items-center gap-1">
                      <Zap className="w-3 h-3" />
                      EV
                    </span>
                  )}
                  {selected.averageRating && (
                    <span className="flex items-center gap-1 text-amber-500">
                      <Star className="w-3 h-3" />
                      {selected.averageRating}
                    </span>
                  )}
                </div>
                {selected.distanceKm != null && (
                  <p className="text-xs mt-1">{selected.distanceKm} km away</p>
                )}
                <Link to={`/booking/${selected.id}`} className="btn-primary w-full mt-3 block text-center text-sm">
                  Book Now
                </Link>
              </div>
            )}
            <div className="space-y-2">
              {locations.map((loc) => (
                <button
                  key={loc.id}
                  type="button"
                  onClick={() => setSelected(loc)}
                  className={`w-full text-left p-3 rounded-xl border transition ${
                    selected?.id === loc.id
                      ? 'border-brand-500 bg-brand-50 dark:bg-brand-900/20'
                      : 'border-slate-200 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800'
                  }`}
                >
                  <p className="font-semibold text-sm">{loc.name}</p>
                  <p className="text-xs text-slate-500">
                    ₹{loc.hourlyRate} · {loc.availableSlots} slots · {loc.city}
                  </p>
                </button>
              ))}
            </div>
          </>
        )}
      </aside>
    </div>
  )
}
