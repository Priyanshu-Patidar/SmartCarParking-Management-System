import api from './axios'

export const authApi = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
}

export const parkingApi = {
  search: (location, sortBy) => api.get('/parking/search', { params: { location, sortBy } }),
  nearby: (params) => api.get('/parking/nearby', { params }),
  getById: (id, lat, lng) => api.get(`/parking/${id}`, { params: { lat, lng } }),
  getSlots: (id, params) => api.get(`/parking/${id}/slots`, { params }),
  toggleFavorite: (id) => api.post(`/parking/favorites/${id}`),
  getFavorites: () => api.get('/parking/favorites'),
}

export const bookingApi = {
  prebook: (data) => api.post('/parking/prebook', data),
  cancel: (id) => api.put(`/parking/cancel/${id}`),
  estimate: (locationId, params) => api.get('/parking/estimate', {
    params: { locationId, ...params },
  }),
  userBookings: (page = 0) => api.get('/bookings/user', { params: { page, size: 10 } }),
  adminBookings: (page = 0) => api.get('/bookings/admin', { params: { page, size: 20 } }),
  getByCode: (code) => api.get(`/bookings/${code}`),
}

export const dashboardApi = {
  stats: () => api.get('/dashboard/stats'),
}

export const adminApi = {
  createParking: (data) => api.post('/admin/parking', data),
  updateParking: (id, data) => api.put(`/admin/parking/${id}`, data),
  addFloor: (id, data) => api.post(`/admin/parking/${id}/floors`, data),
  getUsers: (page) => api.get('/admin/users', { params: { page } }),
  blockUser: (id, blocked) => api.put(`/admin/users/${id}/block`, null, { params: { blocked } }),
  deleteUser: (id) => api.delete(`/admin/users/${id}`),
  auditLogs: (page) => api.get('/admin/audit-logs', { params: { page } }),
}

export const notificationApi = {
  getAll: (page) => api.get('/notifications', { params: { page } }),
  markRead: (id) => api.put(`/notifications/${id}/read`),
}

export const reviewApi = {
  add: (data) => api.post('/reviews', data),
}

export const insightsApi = {
  publicStats: () => api.get('/public/stats'),
  recommendations: (params) => api.get('/parking/recommendations', { params }),
  peakHours: () => api.get('/insights/peak-hours'),
}

export const waitlistApi = {
  join: (params) => api.post('/waitlist', null, { params }),
  mine: () => api.get('/waitlist'),
}

export const sessionApi = {
  checkIn: (bookingId) => api.post(`/sessions/${bookingId}/check-in`),
  checkOut: (bookingId) => api.post(`/sessions/${bookingId}/check-out`),
}
