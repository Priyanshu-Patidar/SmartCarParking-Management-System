import { brand } from '../content/siteCopy'
import { Shield, Globe, Users } from 'lucide-react'

export default function About() {
  return (
    <div className="max-w-4xl mx-auto px-4 py-16">
      <h1 className="text-4xl font-bold">About {brand.name}</h1>
      <p className="mt-6 text-lg text-slate-600 dark:text-slate-400 leading-relaxed">
        {brand.name} is a cloud-native parking platform designed for urban mobility in India. We help drivers
        find, compare, and reserve parking while giving operators the tools to manage capacity, pricing, and revenue.
      </p>

      <div className="grid sm:grid-cols-3 gap-6 mt-12">
        <div className="card text-center">
          <Globe className="w-10 h-10 text-brand-600 mx-auto" />
          <h3 className="font-bold mt-4">Nationwide network</h3>
          <p className="text-sm text-slate-500 mt-2">Facilities across 15 cities with neighborhood-level coverage.</p>
        </div>
        <div className="card text-center">
          <Shield className="w-10 h-10 text-brand-600 mx-auto" />
          <h3 className="font-bold mt-4">Enterprise security</h3>
          <p className="text-sm text-slate-500 mt-2">Encrypted authentication, role-based access, and audit trails.</p>
        </div>
        <div className="card text-center">
          <Users className="w-10 h-10 text-brand-600 mx-auto" />
          <h3 className="font-bold mt-4">Built for scale</h3>
          <p className="text-sm text-slate-500 mt-2">Architecture ready for high-volume booking and real-time updates.</p>
        </div>
      </div>

      <div className="card mt-12">
        <h2 className="text-xl font-bold">Technology</h2>
        <p className="text-slate-500 mt-3 text-sm leading-relaxed">
          The platform is powered by Spring Boot and React, with OpenStreetMap for mapping, WebSocket for live
          occupancy, demand-based pricing engines, and secure payment processing. Our roadmap includes mobile apps,
          operator APIs, and municipal integrations.
        </p>
        <p className="mt-4 text-sm">
          <strong>Contact:</strong>{' '}
          <a href="mailto:support@smartpark.in" className="text-brand-600">support@smartpark.in</a>
        </p>
      </div>
    </div>
  )
}
