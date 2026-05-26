import { configureStore } from '@reduxjs/toolkit'
import authReducer from './authSlice'
import parkingReducer from './parkingSlice'

export const store = configureStore({
  reducer: {
    auth: authReducer,
    parking: parkingReducer,
  },
})
