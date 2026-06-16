import { NavLink, useNavigate } from 'react-router-dom'
import { useSelector, useDispatch } from 'react-redux'
import { memo } from 'react'
import { 
  LayoutDashboard, MapPin, Calendar, User, Shield, 
  Building2, Users, LogOut, Moon, Sun, Activity,
  Server, FileText, Bell, X
} from 'lucide-react'
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
  { to: '/admin/analytics', icon: Activity, label: 'Premium Analytics' },
  { to: '/admin/system', icon: Server, label: 'System Health' },
  { to: '/admin/audit', icon: FileText, label: 'Audit Explorer' },
  { to: '/admin/notifications', icon: Bell, label: 'Notification Center' },
  { to: '/admin/parking', icon: Building2, label: 'Manage Parking' },
  { to: '/admin/users', icon: Users, label: 'Users' },
]

function Sidebar({ isOpen, onClose }) {
  const { user } = useSelector(selectAuth)
  const isAdmin = useSelector(selectIsAdmin)
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const { dark, toggle } = useTheme()

  const linkClass = ({ isActive }) =>
    `flex items-center gap-3 px-4 py-3 rounded-xl transition ${isActive ? 'bg-brand-600 text-white' : 'hover:bg-slate-100 dark:hover:bg-slate-800'}`

  return (
    <>
      {/* Mobile Backdrop Overlay */}
      {isOpen && (
        <div 
          className="fixed inset-0 bg-slate-900/50 backdrop-blur-sm z-40 md:hidden"
          onClick={onClose}
        />
      )}

      {/* Sidebar Container */}
      <aside className={`
        fixed md:static inset-y-0 left-0 z-50 w-64 bg-white dark:bg-slate-900 border-r border-slate-200 dark:border-slate-800 flex flex-col h-full transform transition-transform duration-300 ease-in-out
        ${isOpen ? 'translate-x-0' : '-translate-x-full md:translate-x-0'}
      `}>
        <div className="p-6 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between">
          <div>
            <h2 className="font-bold text-lg text-brand-600">SmartPark</h2>
            <p className="text-xs text-slate-500 mt-1 truncate max-w-[160px]">{user?.fullName || user?.email}</p>
          </div>
          <button 
            className="md:hidden p-2 -mr-2 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200"
            onClick={onClose}
          >
            <X className="w-5 h-5" />
          </button>
        </div>
        
        <nav className="flex-1 p-4 space-y-1 overflow-y-auto custom-scrollbar">
          {userLinks.map((l) => (
            <NavLink key={l.to} to={l.to} className={linkClass} onClick={() => onClose && onClose()}>
              <l.icon className="w-5 h-5" /> {l.label}
            </NavLink>
          ))}
          {isAdmin && (
            <>
              <div className="pt-4 pb-2 text-xs font-semibold text-slate-400 uppercase">Admin</div>
              {adminLinks.map((l) => (
                <NavLink key={l.to} to={l.to} className={linkClass} onClick={() => onClose && onClose()}>
                  <l.icon className="w-5 h-5" /> {l.label}
                </NavLink>
              ))}
            </>
          )}
        </nav>
        
        <div className="p-4 border-t border-slate-200 dark:border-slate-800 space-y-2 mt-auto">
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
    </>
  )
}

export default memo(Sidebar)
