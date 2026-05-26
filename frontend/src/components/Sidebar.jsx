import { NavLink, useNavigate } from 'react-router-dom'
import { useSelector, useDispatch } from 'react-redux'
import { LayoutDashboard, MapPin, Calendar, User, Shield, Building2, Users, LogOut, Moon, Sun } from 'lucide-react'
import { selectAuth, selectIsAdmin, logout } from '../store/authSlice'
import { useTheme } from '../context/ThemeContext'

const userLinks = [
  { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/search', icon: MapPin, label: 'Find Parking' },
  { to: '/map', icon: MapPin, label: 'Map View' },
  { to: '/bookings', icon: Calendar, label: 'My Bookings' },
  { to: '/profile', icon: User, label: 'Profile' },
]

const adminLinks = [
  { to: '/admin', icon: Shield, label: 'Admin Dashboard' },
  { to: '/admin/parking', icon: Building2, label: 'Manage Parking' },
  { to: '/admin/users', icon: Users, label: 'Users' },
]

export default function Sidebar() {
  const { user } = useSelector(selectAuth)
  const isAdmin = useSelector(selectIsAdmin)
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const { dark, toggle } = useTheme()

  const linkClass = ({ isActive }) =>
    `flex items-center gap-3 px-4 py-3 rounded-xl transition ${isActive ? 'bg-brand-600 text-white' : 'hover:bg-slate-100 dark:hover:bg-slate-800'}`

  return (
    <aside className="w-64 bg-white dark:bg-slate-900 border-r border-slate-200 dark:border-slate-800 flex flex-col min-h-screen">
      <div className="p-6 border-b border-slate-200 dark:border-slate-800">
        <h2 className="font-bold text-lg text-brand-600">SmartPark</h2>
        <p className="text-xs text-slate-500 mt-1 truncate">{user?.fullName || user?.email}</p>
      </div>
      <nav className="flex-1 p-4 space-y-1">
        {userLinks.map((l) => (
          <NavLink key={l.to} to={l.to} className={linkClass}>
            <l.icon className="w-5 h-5" /> {l.label}
          </NavLink>
        ))}
        {isAdmin && (
          <>
            <div className="pt-4 pb-2 text-xs font-semibold text-slate-400 uppercase">Admin</div>
            {adminLinks.map((l) => (
              <NavLink key={l.to} to={l.to} className={linkClass}>
                <l.icon className="w-5 h-5" /> {l.label}
              </NavLink>
            ))}
          </>
        )}
      </nav>
      <div className="p-4 border-t border-slate-200 dark:border-slate-800 space-y-2">
        <button onClick={toggle} className="w-full flex items-center gap-3 px-4 py-2 rounded-xl hover:bg-slate-100 dark:hover:bg-slate-800">
          {dark ? <Sun className="w-5 h-5" /> : <Moon className="w-5 h-5" />}
          {dark ? 'Light Mode' : 'Dark Mode'}
        </button>
        <button
          onClick={() => { dispatch(logout()); navigate('/') }}
          className="w-full flex items-center gap-3 px-4 py-2 rounded-xl text-red-500 hover:bg-red-50 dark:hover:bg-red-950"
        >
          <LogOut className="w-5 h-5" /> Logout
        </button>
      </div>
    </aside>
  )
}
