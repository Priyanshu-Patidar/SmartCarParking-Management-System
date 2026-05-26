import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useDispatch } from 'react-redux'
import { motion } from 'framer-motion'
import toast from 'react-hot-toast'
import { authApi } from '../api/services'
import { setAuth } from '../store/authSlice'

export default function Register() {
  const [form, setForm] = useState({ fullName: '', email: '', password: '', phone: '' })
  const [loading, setLoading] = useState(false)
  const dispatch = useDispatch()
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      const { data } = await authApi.register(form)
      dispatch(setAuth(data))
      toast.success('Account created!')
      navigate('/dashboard')
    } catch (err) {
      toast.error(err.response?.data?.message || 'Registration failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-[80vh] flex items-center justify-center px-4">
      <motion.div initial={{ opacity: 0, scale: 0.95 }} animate={{ opacity: 1, scale: 1 }} className="card w-full max-w-md">
        <h1 className="text-2xl font-bold">Create your account</h1>
        <p className="text-slate-500 mt-1">Join SmartPark — reserve parking across 15 Indian cities</p>
        <form onSubmit={handleSubmit} className="mt-6 space-y-4">
          {['fullName', 'email', 'phone', 'password'].map((field) => (
            <input key={field} className="input-field"
              type={field === 'password' ? 'password' : field === 'email' ? 'email' : 'text'}
              placeholder={field === 'fullName' ? 'Full Name' : field.charAt(0).toUpperCase() + field.slice(1)}
              required={field !== 'phone'}
              value={form[field]} onChange={(e) => setForm({ ...form, [field]: e.target.value })} />
          ))}
          <button type="submit" disabled={loading} className="btn-primary w-full">
            {loading ? 'Creating...' : 'Sign Up'}
          </button>
        </form>
        <p className="mt-4 text-center text-sm">
          Have an account? <Link to="/login" className="text-brand-600 font-semibold">Login</Link>
        </p>
      </motion.div>
    </div>
  )
}
