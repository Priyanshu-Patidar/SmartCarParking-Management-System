import { Link, useNavigate } from 'react-router-dom'
import { useSelector, useDispatch } from 'react-redux'
import { MapPin, Moon, Sun, LogOut, LayoutDashboard } from 'lucide-react'
import { selectAuth, logout } from '../store/authSlice'
import { useTheme } from '../context/ThemeContext'
import { motion } from 'framer-motion'

export default function Navbar() {
  const { isAuthenticated, user } = useSelector(selectAuth)
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const { dark, toggle } = useTheme()

  return (
    <motion.nav
      initial={{ y: -20, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      className="sticky top-0 z-50 bg-white/80 dark:bg-slate-900/80 backdrop-blur-lg border-b border-slate-200 dark:border-slate-800"
    >
      <div className="max-w-7xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between">
        <Link to="/" className="flex items-center gap-2 font-bold text-xl text-brand-600">
          <MapPin className="w-7 h-7" />
          SmartPark
        </Link>
        <div className="hidden md:flex items-center gap-6 text-sm font-medium">
          <Link to="/search" className="hover:text-brand-600 transition">Find Parking</Link>
          <Link to="/map" className="hover:text-brand-600 transition">Map</Link>
          <Link to="/about" className="hover:text-brand-600 transition">About</Link>
        </div>
        <div className="flex items-center gap-3">
          <button onClick={toggle} className="p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800">
            {dark ? <Sun className="w-5 h-5" /> : <Moon className="w-5 h-5" />}
          </button>
          {isAuthenticated ? (
            <>
              <Link to="/dashboard" className="btn-secondary flex items-center gap-2 text-sm">
                <LayoutDashboard className="w-4 h-4" /> Dashboard
              </Link>
              <button onClick={() => { dispatch(logout()); navigate('/') }} className="p-2 text-red-500 hover:bg-red-50 dark:hover:bg-red-950 rounded-lg">
                <LogOut className="w-5 h-5" />
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="text-sm font-semibold hover:text-brand-600">Login</Link>
              <Link to="/register" className="btn-primary text-sm">Sign Up</Link>
            </>
          )}
        </div>
      </div>
    </motion.nav>
  )
}
