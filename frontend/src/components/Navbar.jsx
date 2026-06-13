import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useSelector, useDispatch } from 'react-redux'
import { Menu, X, User, LogOut, MapPin, Shield, Moon, Sun, LayoutDashboard } from 'lucide-react'
import { useState, useEffect } from 'react'
import { selectAuth, logout } from '../store/authSlice'
import { useTheme } from '../context/ThemeContext'
import { motion, AnimatePresence } from 'framer-motion'

export default function Navbar() {
  const { isAuthenticated, user } = useSelector(selectAuth)
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const { dark, toggle } = useTheme()
  const [isOpen, setIsOpen] = useState(false)
  const [scrolled, setScrolled] = useState(false)

  useEffect(() => {
    const handleScroll = () => setScrolled(window.scrollY > 20)
    window.addEventListener('scroll', handleScroll)
    return () => window.removeEventListener('scroll', handleScroll)
  }, [])

  const navLinks = [
    { to: '/search', label: 'Find Parking', icon: MapPin },
    { to: '/map', label: 'Map View', icon: MapPin },
    { to: '/about', label: 'About', icon: null },
  ]

  const activeClass = ({ isActive }) => 
    `text-sm font-bold transition-all px-4 py-2 rounded-xl ${isActive ? 'bg-brand-50 text-brand-600 dark:bg-brand-900/20 dark:text-brand-400' : 'hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-600 dark:text-slate-300'}`

  return (
    <nav className={`fixed top-0 left-0 right-0 z-50 transition-all duration-300 ${scrolled ? 'py-3 glass shadow-lg' : 'py-5 bg-transparent'}`}>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center">
          <Link to="/" className="flex items-center gap-2 group">
            <div className="w-10 h-10 bg-brand-600 rounded-2xl flex items-center justify-center shadow-lg shadow-brand-500/20 group-hover:scale-110 transition-transform">
              <Shield className="text-white w-6 h-6" />
            </div>
            <span className="text-xl font-black tracking-tight dark:text-white">SmartPark</span>
          </Link>

          {/* Desktop Nav */}
          <div className="hidden md:flex items-center gap-2">
            {navLinks.map((link) => (
              <NavLink key={link.to} to={link.to} className={activeClass}>
                {link.label}
              </NavLink>
            ))}
            <div className="h-6 w-px bg-slate-200 dark:bg-slate-800 mx-2" />
            <button 
              onClick={toggle}
              className="p-2.5 rounded-xl hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-500 transition-colors"
            >
              {dark ? <Sun className="w-5 h-5" /> : <Moon className="w-5 h-5" />}
            </button>
            {isAuthenticated ? (
              <div className="flex items-center gap-3 ml-2">
                <Link to="/dashboard" className="btn-primary py-2 px-5 text-sm flex items-center gap-2">
                  <LayoutDashboard className="w-4 h-4" /> Dashboard
                </Link>
                <button onClick={() => { dispatch(logout()); navigate('/') }} className="p-2.5 text-rose-500 hover:bg-rose-50 dark:hover:bg-rose-950/30 rounded-xl transition-colors">
                  <LogOut className="w-5 h-5" />
                </button>
              </div>
            ) : (
              <div className="flex items-center gap-2 ml-2">
                <Link to="/login" className="text-sm font-bold px-4 py-2 text-slate-600 dark:text-slate-300 hover:text-brand-600 transition-colors">Log in</Link>
                <Link to="/register" className="btn-primary py-2 px-5 text-sm">Join Free</Link>
              </div>
            )}
          </div>

          {/* Mobile Toggle */}
          <div className="md:hidden flex items-center gap-2">
            <button onClick={toggle} className="p-2 rounded-lg text-slate-500">
              {dark ? <Sun className="w-5 h-5" /> : <Moon className="w-5 h-5" />}
            </button>
            <button onClick={() => setIsOpen(!isOpen)} className="p-2 rounded-lg text-slate-500">
              {isOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile Menu */}
      <AnimatePresence>
        {isOpen && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            exit={{ opacity: 0, height: 0 }}
            className="md:hidden glass border-t border-slate-100 dark:border-slate-800 overflow-hidden"
          >
            <div className="px-4 py-6 space-y-2">
              {navLinks.map((link) => (
                <Link 
                  key={link.to} 
                  to={link.to} 
                  onClick={() => setIsOpen(false)}
                  className="block px-4 py-3 rounded-xl font-bold text-slate-600 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800"
                >
                  {link.label}
                </Link>
              ))}
              <div className="pt-4 space-y-2">
                {isAuthenticated ? (
                  <Link to="/dashboard" onClick={() => setIsOpen(false)} className="btn-primary w-full text-center block">Go to Dashboard</Link>
                ) : (
                  <>
                    <Link to="/login" onClick={() => setIsOpen(false)} className="btn-secondary w-full text-center block">Log in</Link>
                    <Link to="/register" onClick={() => setIsOpen(false)} className="btn-primary w-full text-center block">Create Account</Link>
                  </>
                )}
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </nav>
  )
}
