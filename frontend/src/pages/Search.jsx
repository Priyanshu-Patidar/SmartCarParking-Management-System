import { useState } from 'react'
import { useDispatch } from 'react-redux'
import { Search as SearchIcon, Navigation, Filter, MapPin } from 'lucide-react'
import toast from 'react-hot-toast'
import { parkingApi } from '../api/services'
import { setSearchResults } from '../store/parkingSlice'
import ParkingCard from '../components/ParkingCard'
import LoadingSkeleton from '../components/LoadingSkeleton'
import SearchResultsMap from '../components/SearchResultsMap'

function getErrorMessage(err) {
  if (err.response?.status === 429) return 'Too many requests — wait a moment and try again'
  if (err.code === 'ERR_NETWORK') return 'Cannot reach server — is the backend running on port 8080?'
  return err.response?.data?.message || err.message || 'Request failed'
}

export default function Search() {
  const [query, setQuery] = useState('')
  const [results, setResults] = useState([])
  const [selected, setSelected] = useState(null)
  const [loading, setLoading] = useState(false)
  const [sortBy, setSortBy] = useState('distance')
  const [searched, setSearched] = useState(false)
  const dispatch = useDispatch()

  const handleSearch = async (q = query) => {
    const term = q.trim()
    if (!term) return toast.error('Enter a city or location')
    if (loading) return

    setLoading(true)
    setSearched(true)
    setSelected(null)
    try {
      const { data } = await parkingApi.search(term, sortBy)
      const list = Array.isArray(data) ? data : []
      setResults(list)
      dispatch(setSearchResults(list))
      if (list.length === 0) {
        toast.error(`No parking found for "${term}". Try Mumbai, Delhi, Pune, Bangalore, or Chennai.`)
      } else {
        toast.success(`Found ${list.length} parking spots across ${term}`)
        setSelected(list[0])
      }
    } catch (err) {
      setResults([])
      toast.error(getErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  const useGps = () => {
    if (loading) return
    navigator.geolocation?.getCurrentPosition(
      async (pos) => {
        const { latitude: lat, longitude: lng } = pos.coords
        setLoading(true)
        setSearched(true)
        try {
          const { data } = await parkingApi.nearby({ lat, lng, radiusKm: 50, sortBy })
          const list = Array.isArray(data) ? data : []
          setResults(list)
          dispatch(setSearchResults(list))
          if (list.length === 0) {
            toast.error('No parking within 50 km. Try searching by city name.')
          } else {
            toast.success(`Found ${list.length} nearby parking(s)`)
            setSelected(list[0])
          }
        } catch (err) {
          setResults([])
          toast.error(getErrorMessage(err))
        } finally {
          setLoading(false)
        }
      },
      () => toast.error('Enable location access in your browser')
    )
  }

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold">Find parking</h1>
      <p className="text-slate-500 mt-1">
        Search by city or use your location — every facility is mapped to its neighborhood
      </p>

      <div className="mt-6 flex flex-col sm:flex-row gap-3">
        <div className="flex-1 relative">
          <SearchIcon className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400" />
          <input
            className="input-field pl-12"
            placeholder="City: Delhi, Mumbai, Pune, Bangalore..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
          />
        </div>
        <select
          className="input-field sm:w-40"
          value={sortBy}
          onChange={(e) => setSortBy(e.target.value)}
        >
          <option value="distance">Distance</option>
          <option value="price">Price</option>
          <option value="availability">Availability</option>
          <option value="rating">Rating</option>
        </select>
        <button onClick={() => handleSearch()} disabled={loading} className="btn-primary disabled:opacity-50">
          {loading ? 'Searching...' : 'Search'}
        </button>
        <button onClick={useGps} disabled={loading} className="btn-secondary flex items-center gap-2">
          <Navigation className="w-4 h-4" /> GPS
        </button>
      </div>

      <div className="mt-4 flex flex-wrap gap-2">
        {['Delhi', 'Mumbai', 'Pune', 'Bangalore', 'Hyderabad', 'Chennai'].map((city) => (
          <button
            key={city}
            type="button"
            onClick={() => {
              setQuery(city)
              handleSearch(city)
            }}
            className="text-xs px-3 py-1 rounded-full bg-slate-100 dark:bg-slate-800 hover:bg-brand-100 dark:hover:bg-brand-900/30"
          >
            {city}
          </button>
        ))}
      </div>

      {loading && <div className="mt-8"><LoadingSkeleton /></div>}

      {!loading && results.length > 0 && (
        <>
          <div className="mt-8">
            <div className="flex items-center gap-2 mb-3">
              <MapPin className="w-5 h-5 text-brand-600" />
              <h2 className="font-bold text-lg">
                {results.length} parking locations {query ? `in ${query}` : ''} — map view
              </h2>
            </div>
            <SearchResultsMap
              locations={results}
              selectedId={selected?.id}
              onSelect={setSelected}
            />
            <p className="text-xs text-slate-500 mt-2 text-center">
              Click a marker or card below to view details. Each pin is a different area in the city.
            </p>
          </div>

          <div className="mt-8 grid lg:grid-cols-3 gap-6">
            <div className="lg:col-span-2 grid md:grid-cols-2 gap-6">
              {results.map((p, i) => (
                <div
                  key={p.id}
                  onMouseEnter={() => setSelected(p)}
                  className={selected?.id === p.id ? 'ring-2 ring-brand-500 rounded-2xl' : ''}
                >
                  <ParkingCard parking={p} index={i} />
                </div>
              ))}
            </div>
            {selected && (
              <aside className="card h-fit sticky top-24">
                <h3 className="font-bold">{selected.name}</h3>
                <p className="text-sm text-slate-500 mt-1">{selected.address}</p>
                <p className="text-sm mt-2">{selected.city}</p>
                <div className="mt-4 space-y-2 text-sm">
                  <p>
                    <span className="font-semibold text-brand-600">₹{selected.hourlyRate}/hr</span>
                  </p>
                  <p>
                    {selected.availableSlots} / {selected.totalSlots} slots available
                  </p>
                  {selected.latitude && (
                    <p className="text-xs text-slate-400">
                      📍 {selected.latitude.toFixed(4)}, {selected.longitude.toFixed(4)}
                    </p>
                  )}
                </div>
                <a href={`/booking/${selected.id}`} className="btn-primary w-full mt-4 block text-center text-sm">
                  Book this spot
                </a>
              </aside>
            )}
          </div>
        </>
      )}

      {!loading && searched && results.length === 0 && (
        <div className="text-center py-20 text-slate-500">
          <Filter className="w-12 h-12 mx-auto mb-4 opacity-50" />
          <p>No results. Restart backend to spread parking across city areas.</p>
        </div>
      )}

      {!loading && !searched && (
        <div className="text-center py-20 text-slate-500">
          <Filter className="w-12 h-12 mx-auto mb-4 opacity-50" />
          <p>Search a city to see all parking locations on the map</p>
        </div>
      )}
    </div>
  )
}
