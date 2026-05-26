import { useEffect } from 'react'
import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet'
import L from 'leaflet'
import { Link } from 'react-router-dom'

const icon = new L.Icon({
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
})

function FitBounds({ locations }) {
  const map = useMap()
  useEffect(() => {
    if (locations?.length > 0) {
      const bounds = L.latLngBounds(
        locations.map((loc) => [loc.latitude, loc.longitude])
      )
      map.fitBounds(bounds, { padding: [48, 48], maxZoom: 13 })
    }
  }, [locations, map])
  return null
}

export default function SearchResultsMap({ locations, selectedId, onSelect }) {
  if (!locations?.length) return null

  const center = [locations[0].latitude, locations[0].longitude]

  return (
    <div className="rounded-2xl overflow-hidden border border-slate-200 dark:border-slate-700 shadow-lg h-[420px] w-full">
      <MapContainer
        center={center}
        zoom={11}
        className="h-full w-full"
        scrollWheelZoom
      >
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        <FitBounds locations={locations} />
        {locations.map((loc) => (
          <Marker
            key={loc.id}
            position={[loc.latitude, loc.longitude]}
            icon={icon}
            eventHandlers={{
              click: () => onSelect?.(loc),
            }}
          >
            <Popup>
              <div className="min-w-[180px]">
                <p className="font-bold text-slate-900">{loc.name}</p>
                <p className="text-xs text-slate-600 mt-1">{loc.address}</p>
                <p className="text-sm mt-2">
                  <span className="font-semibold text-sky-700">₹{loc.hourlyRate}/hr</span>
                  {' · '}
                  {loc.availableSlots} slots free
                </p>
                <Link
                  to={`/booking/${loc.id}`}
                  className="inline-block mt-2 text-sm font-semibold text-sky-600 hover:underline"
                >
                  Book now →
                </Link>
              </div>
            </Popup>
          </Marker>
        ))}
      </MapContainer>
    </div>
  )
}
