import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useSelector, useDispatch } from 'react-redux'
import { Menu, X, User, LogOut, MapPin, Shield, Moon, Sun, LayoutDashboard } from 'lucide-react'
import { useState, useEffect, memo } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { selectAuth, selectIsAdmin, logout } from '../store/authSlice'
import { useTheme } from '../context/ThemeContext'

function Navbar() {
  const { isAuthenticated, user } = useSelector(selectAuth)
  const isAdmin = useSelector(selectIsAdmin)
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const [isOpen, setIsOpen] = useState(false)
  const [scrolled, setScrolled] = useState(false)
  const { dark, toggle } = useTheme()

  useEffect(() => {
    const handleScroll = () => setScrolled(window.scrollY > 20)
    window.addEventListener('scroll', handleScroll)
    return () => window.removeEventListener('scroll', handleScroll)
  }, [])

  const handleLogout = () => {
    dispatch(logout())
    setIsOpen(false)
    navigate('/')
  }

  const navLinks = [
    { to: '/search', label: 'Find Parking' },
    { to: '/map', label: 'Map View' },
    { to: '/about', label: 'About' },
  ]

  return (
    <nav className={`fixed w-full z-50 transition-all duration-300 ${scrolled ? 'bg-white/80 dark:bg-slate-900/80 backdrop-blur-md shadow-lg py-3' : 'bg-transparent py-5'}`}>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center">
          <Link to="/" className="flex items-center gap-2 group">
            <div className="w-10 h-10 bg-brand-600 rounded-xl flex items-center justify-center shadow-lg group-hover:rotate-12 transition-transform">
              <MapPin className="text-white w-6 h-6" />
            </div>
            <span className="text-xl font-black tracking-tighter text-slate-900 dark:text-white uppercase">SmartPark</span>
          </Link>

          {/* Desktop Nav */}
          <div className="hidden md:flex items-center gap-8">
            <div className="flex items-center gap-6">
              {navLinks.map((link) => (
                <NavLink key={link.to} to={link.to} className={({ isActive }) => `text-sm font-bold uppercase tracking-widest transition-colors ${isActive ? 'text-brand-600' : 'text-slate-500 hover:text-brand-600'}`}>
                  {link.label}
                </NavLink>
              ))}
            </div>

            <div className="h-6 w-px bg-slate-200 dark:bg-slate-800" />

            <div className="flex items-center gap-4">
              <button onClick={toggle} className="p-2 rounded-full hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors">
                {dark ? <Sun className="w-5 h-5 text-amber-500" /> : <Moon className="w-5 h-5 text-slate-600" />}
              </button>

              {isAuthenticated ? (
                <div className="flex items-center gap-3">
                  <Link to={isAdmin ? '/admin' : '/dashboard'} className="flex items-center gap-2 px-4 py-2 bg-brand-50 dark:bg-brand-900/30 text-brand-700 dark:text-brand-400 rounded-full font-bold text-xs uppercase tracking-widest hover:bg-brand-100 transition-all">
                    {isAdmin ? <Shield className="w-4 h-4" /> : <LayoutDashboard className="w-4 h-4" />}
                    Dashboard
                  </Link>
                  <button onClick={handleLogout} className="p-2 text-slate-400 hover:text-red-500 transition-colors">
                    <LogOut className="w-5 h-5" />
                  </button>
                </div>
              ) : (
                <div className="flex items-center gap-2">
                  <Link to="/login" className="text-sm font-bold uppercase tracking-widest px-6 py-2 rounded-full hover:bg-slate-100 dark:hover:bg-slate-800 transition-all">Login</Link>
                  <Link to="/register" className="btn-primary px-6 py-2 rounded-full text-xs font-bold uppercase tracking-widest shadow-brand-500/20 shadow-lg">Sign Up</Link>
                </div>
              )}
            </div>
          </div>

          {/* Mobile toggle */}
          <div className="md:hidden flex items-center gap-4">
             <button onClick={toggle} className="p-2 text-slate-500">{dark ? <Sun className="w-5 h-5" /> : <Moon className="w-5 h-5" />}</button>
             <button onClick={() => setIsOpen(!isOpen)} className="p-2 text-slate-900 dark:text-white"><Menu className="w-6 h-6" /></button>
          </div>
        </div>
      </div>

      <AnimatePresence>
        {isOpen && (
          <motion.div initial={{ opacity: 0, height: 0 }} animate={{ opacity: 1, height: 'auto' }} exit={{ opacity: 0, height: 0 }} className="md:hidden bg-white dark:bg-slate-900 border-b dark:border-slate-800">
            <div className="px-4 py-6 space-y-4">
              {navLinks.map((link) => (
                <Link key={link.to} to={link.to} onClick={() => setIsOpen(false)} className="block text-lg font-bold text-slate-900 dark:text-white uppercase tracking-tighter">{link.label}</Link>
              ))}
              <div className="pt-4 border-t dark:border-slate-800 flex flex-col gap-3">
                {isAuthenticated ? (
                  <>
                    <Link to={isAdmin ? '/admin' : '/dashboard'} onClick={() => setIsOpen(false)} className="btn-secondary w-full text-center">Dashboard</Link>
                    <button onClick={handleLogout} className="btn-primary w-full bg-red-500 hover:bg-red-600 border-none">Logout</button>
                  </>
                ) : (
                  <>
                    <Link to="/login" onClick={() => setIsOpen(false)} className="btn-secondary w-full text-center">Login</Link>
                    <Link to="/register" onClick={() => setIsOpen(false)} className="btn-primary w-full text-center">Create Account</Link>
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

export default memo(Navbar)
