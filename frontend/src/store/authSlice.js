import { createSlice } from '@reduxjs/toolkit'

const loadAuth = () => {
  try {
    const data = localStorage.getItem('smartpark_auth')
    return data ? JSON.parse(data) : null
  } catch {
    return null
  }
}

const authSlice = createSlice({
  name: 'auth',
  initialState: {
    user: loadAuth(),
    isAuthenticated: !!loadAuth()?.accessToken,
  },
  reducers: {
    setAuth: (state, action) => {
      state.user = action.payload
      state.isAuthenticated = true
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
