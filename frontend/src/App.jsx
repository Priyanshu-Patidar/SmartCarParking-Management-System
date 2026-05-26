import { Routes, Route, Navigate } from 'react-router-dom'
import { useSelector } from 'react-redux'
import { selectAuth, selectIsAdmin } from './store/authSlice'
import MainLayout from './layouts/MainLayout'
import DashboardLayout from './layouts/DashboardLayout'
import Home from './pages/Home'
import Login from './pages/Login'
import Register from './pages/Register'
import Search from './pages/Search'
import MapPage from './pages/MapPage'
import BookingPage from './pages/BookingPage'
import Bookings from './pages/Bookings'
import UserDashboard from './pages/UserDashboard'
import AdminDashboard from './pages/AdminDashboard'
import Profile from './pages/Profile'
import About from './pages/About'
import AdminParking from './pages/admin/AdminParking'
import AdminUsers from './pages/admin/AdminUsers'

function PrivateRoute({ children }) {
  const { isAuthenticated } = useSelector(selectAuth)
  return isAuthenticated ? children : <Navigate to="/login" />
}

function AdminRoute({ children }) {
  const isAdmin = useSelector(selectIsAdmin)
  const { isAuthenticated } = useSelector(selectAuth)
  if (!isAuthenticated) return <Navigate to="/login" />
  return isAdmin ? children : <Navigate to="/dashboard" />
}

export default function App() {
  return (
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
        <Route path="/admin/parking" element={<AdminRoute><AdminParking /></AdminRoute>} />
        <Route path="/admin/users" element={<AdminRoute><AdminUsers /></AdminRoute>} />
      </Route>
      <Route path="*" element={<Navigate to="/" />} />
    </Routes>
  )
}
