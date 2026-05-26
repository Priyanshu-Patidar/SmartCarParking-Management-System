import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Sparkles, Leaf, Wallet } from 'lucide-react'
import { insightsApi } from '../api/services'

export default function SmartRecommendations() {
  const [data, setData] = useState(null)

  useEffect(() => {
    navigator.geolocation?.getCurrentPosition(
      (p) => insightsApi.recommendations({ lat: p.coords.latitude, lng: p.coords.longitude }).then(({ data: d }) => setData(d)),
      () => insightsApi.recommendations({}).then(({ data: d }) => setData(d))
    )
  }, [])

  if (!data) return null

  const Section = ({ title, icon: Icon, items, color }) =>
    items?.length > 0 && (
      <div>
        <div className={`flex items-center gap-2 mb-3 ${color}`}>
          <Icon className="w-5 h-5" />
          <h3 className="font-semibold">{title}</h3>
        </div>
        <ul className="space-y-2">
          {items.slice(0, 3).map((p) => (
            <li key={p.id}>
              <Link to={`/booking/${p.id}`} className="text-sm hover:text-brand-600 block">
                {p.name} — ₹{p.hourlyRate}/hr · {p.availableSlots} slots
              </Link>
            </li>
          ))}
        </ul>
      </div>
    )

  return (
    <div className="card border-brand-200 dark:border-brand-800 bg-gradient-to-br from-brand-50/50 to-transparent dark:from-brand-950/30">
      <div className="flex items-start gap-3">
        <Sparkles className="w-8 h-8 text-brand-600 shrink-0" />
        <div className="flex-1">
          <h2 className="font-bold text-lg">Recommended for you</h2>
          <p className="text-sm text-slate-500 mt-1">{data.insightMessage}</p>
          <div className="grid sm:grid-cols-3 gap-6 mt-6">
            <Section title="Best availability" icon={Sparkles} items={data.bestMatch} color="text-brand-600" />
            <Section title="Budget-friendly" icon={Wallet} items={data.budgetFriendly} color="text-green-600" />
            <Section title="EV charging" icon={Leaf} items={data.evRecommended} color="text-emerald-600" />
          </div>
        </div>
      </div>
    </div>
  )
}
