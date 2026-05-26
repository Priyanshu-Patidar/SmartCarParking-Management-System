import axios from 'axios'
import { store } from '../store/store'
import { logout, setAuth } from '../store/authSlice'

const API_BASE = import.meta.env.VITE_API_BASE_URL || '/api'

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.request.use((config) => {
  const auth = store.getState().auth.user
  if (auth?.accessToken) {
    config.headers.Authorization = `Bearer ${auth.accessToken}`
  }
  return config
})

api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const original = error.config
    if (error.response?.status === 401 && !original._retry) {
      original._retry = true
      const auth = store.getState().auth.user
      if (auth?.refreshToken) {
        try {
          const { data } = await axios.post(`${API_BASE}/auth/refresh-token`, {
            refreshToken: auth.refreshToken,
          })
          store.dispatch(setAuth({ ...auth, ...data }))
          original.headers.Authorization = `Bearer ${data.accessToken}`
          return api(original)
        } catch {
          store.dispatch(logout())
        }
      }
    }
    return Promise.reject(error)
  }
)

export default api
