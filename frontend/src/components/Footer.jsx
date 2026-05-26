import { Link } from 'react-router-dom'
import { brand } from '../content/siteCopy'

export default function Footer() {
  return (
    <footer className="border-t border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-950 mt-auto">
      <div className="max-w-7xl mx-auto px-6 py-12 grid md:grid-cols-4 gap-8">
        <div className="md:col-span-2">
          <p className="font-bold text-lg text-brand-600">{brand.name}</p>
          <p className="text-sm text-slate-500 mt-2 max-w-md">{brand.description}</p>
        </div>
        <div>
          <p className="font-semibold text-sm mb-3">Product</p>
          <ul className="space-y-2 text-sm text-slate-500">
            <li><Link to="/search" className="hover:text-brand-600">Find parking</Link></li>
            <li><Link to="/map" className="hover:text-brand-600">Live map</Link></li>
            <li><Link to="/register" className="hover:text-brand-600">Create account</Link></li>
          </ul>
        </div>
        <div>
          <p className="font-semibold text-sm mb-3">Company</p>
          <ul className="space-y-2 text-sm text-slate-500">
            <li><Link to="/about" className="hover:text-brand-600">About SmartPark</Link></li>
            <li><a href="mailto:support@smartpark.in" className="hover:text-brand-600">support@smartpark.in</a></li>
            <li><span>© {new Date().getFullYear()} SmartPark India</span></li>
          </ul>
        </div>
      </div>
    </footer>
  )
}
