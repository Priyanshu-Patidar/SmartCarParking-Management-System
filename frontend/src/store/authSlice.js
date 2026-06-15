import { createSlice } from '@reduxjs/toolkit'

const initialAuth = (() => {
  try {
    const data = localStorage.getItem('smartpark_auth')
    const parsed = data ? JSON.parse(data) : null
    return {
      user: parsed,
      isAuthenticated: !!parsed?.accessToken,
    }
  } catch {
    return { user: null, isAuthenticated: false }
  }
})()

const authSlice = createSlice({
  name: 'auth',
  initialState: initialAuth,
  reducers: {
    setAuth: (state, action) => {
      state.user = action.payload
      state.isAuthenticated = !!action.payload?.accessToken
      localStorage.setItem('smartpark_auth', JSON.stringify(action.payload))
    },
    logout: (state) => {
      state.user = null
      state.isAuthenticated = false
      localStorage.removeItem('smartpark_auth')
    },
  },
})

export const { setAuth, logout } = authSlice.actions
export const selectAuth = (state) => state.auth
export const selectIsAdmin = (state) =>
  state.auth.user?.roles?.includes('ROLE_ADMIN')
export default authSlice.reducer
