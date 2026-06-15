import axios from 'axios'
import { store } from '../store/store'
import { logout, setAuth } from '../store/authSlice'

const API_BASE = (import.meta.env.VITE_API_BASE_URL || '/api') + '/v1'

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true,
})

// AbortController map to handle request cancellation
const pendingRequests = new Map()

api.interceptors.request.use((config) => {
  // Cancel previous duplicate requests
  const requestKey = `${config.method}:${config.url}`
  if (pendingRequests.has(requestKey)) {
    pendingRequests.get(requestKey).abort()
  }
  
  const controller = new AbortController()
  config.signal = controller.signal
  pendingRequests.set(requestKey, controller)

  const auth = store.getState().auth.user
  if (auth?.accessToken) {
    config.headers.Authorization = `Bearer ${auth.accessToken}`
  }
  return config
})

api.interceptors.response.use(
  (res) => {
    const requestKey = `${res.config.method}:${res.config.url}`
    pendingRequests.delete(requestKey)
    return res
  },
  async (error) => {
    if (axios.isCancel(error)) {
      return new Promise(() => {}) // Silently swallow cancellations
    }

    const original = error.config
    const requestKey = `${original?.method}:${original?.url}`
    pendingRequests.delete(requestKey)

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
