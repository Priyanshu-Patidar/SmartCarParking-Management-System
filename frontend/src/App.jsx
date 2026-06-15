import { Routes, Route, Navigate } from 'react-router-dom'
import { useSelector } from 'react-redux'
import { lazy, Suspense, memo } from 'react'
import { selectAuth, selectIsAdmin } from './store/authSlice'
import MainLayout from './layouts/MainLayout'
import DashboardLayout from './layouts/DashboardLayout'
import LoadingSkeleton from './components/LoadingSkeleton'

// Lazy loaded pages for code splitting
const Home = lazy(() => import('./pages/Home'))
const Login = lazy(() => import('./pages/Login'))
const Register = lazy(() => import('./pages/Register'))
const Search = lazy(() => import('./pages/Search'))
const MapPage = lazy(() => import('./pages/MapPage'))
const BookingPage = lazy(() => import('./pages/BookingPage'))
const Bookings = lazy(() => import('./pages/Bookings'))
const UserDashboard = lazy(() => import('./pages/UserDashboard'))
const AdminDashboard = lazy(() => import('./pages/AdminDashboard'))
const AdvancedAnalytics = lazy(() => import('./pages/admin/AdvancedAnalytics'))
const AdminSystem = lazy(() => import('./pages/admin/AdminSystem'))
const AdminAudit = lazy(() => import('./pages/admin/AdminAudit'))
const AdminNotifications = lazy(() => import('./pages/admin/AdminNotifications'))
const Profile = lazy(() => import('./pages/Profile'))
const About = lazy(() => import('./pages/About'))
const AdminParking = lazy(() => import('./pages/admin/AdminParking'))
const AdminUsers = lazy(() => import('./pages/admin/AdminUsers'))

// Memoized PrivateRoute to prevent unnecessary rerenders of the entire route tree
const PrivateRoute = memo(({ children }) => {
  const { isAuthenticated } = useSelector(selectAuth)
  return isAuthenticated ? children : <Navigate to="/login" replace />
})

const AdminRoute = memo(({ children }) => {
  const isAdmin = useSelector(selectIsAdmin)
  const { isAuthenticated } = useSelector(selectAuth)
  if (!isAuthenticated) return <Navigate to="/login" replace />
  return isAdmin ? children : <Navigate to="/dashboard" replace />
})

export default function App() {
  return (
    <Suspense fallback={<div className="min-h-screen flex items-center justify-center"><LoadingSkeleton variant="stats" count={1} /></div>}>
      <Routes>
        <Route element={<MainLayout />}>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/search" element={<Search />} />
          <Route path="/map" element={<MapPage />} />
          <Route path="/about" element={<About />} />
          <Route path="/booking/:id" element={<PrivateRoute><BookingPage /></PrivateRoute>} />
        </Route>
        
        <Route element={<PrivateRoute><DashboardLayout /></PrivateRoute>}>
          <Route path="/dashboard" element={<UserDashboard />} />
          <Route path="/bookings" element={<Bookings />} />
          <Route path="/profile" element={<Profile />} />
          
          <Route path="/admin" element={<AdminRoute><AdminDashboard /></AdminRoute>} />
          <Route path="/admin/analytics" element={<AdminRoute><AdvancedAnalytics /></AdminRoute>} />
          <Route path="/admin/system" element={<AdminRoute><AdminSystem /></AdminRoute>} />
          <Route path="/admin/audit" element={<AdminRoute><AdminAudit /></AdminRoute>} />
          <Route path="/admin/notifications" element={<AdminRoute><AdminNotifications /></AdminRoute>} />
          <Route path="/admin/parking" element={<AdminRoute><AdminParking /></AdminRoute>} />
          <Route path="/admin/users" element={<AdminRoute><AdminUsers /></AdminRoute>} />
        </Route>
        
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Suspense>
  )
}
