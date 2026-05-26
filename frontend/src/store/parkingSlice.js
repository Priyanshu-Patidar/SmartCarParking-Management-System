import { createSlice } from '@reduxjs/toolkit'

const parkingSlice = createSlice({
  name: 'parking',
  initialState: {
    searchQuery: '',
    searchResults: [],
    selectedLocation: null,
    userLocation: null,
    sortBy: 'distance',
    filters: { vehicleType: '', evOnly: false, maxPrice: '' },
  },
  reducers: {
    setSearchQuery: (state, action) => { state.searchQuery = action.payload },
    setSearchResults: (state, action) => { state.searchResults = action.payload },
    setSelectedLocation: (state, action) => { state.selectedLocation = action.payload },
    setUserLocation: (state, action) => { state.userLocation = action.payload },
    setSortBy: (state, action) => { state.sortBy = action.payload },
    setFilters: (state, action) => { state.filters = { ...state.filters, ...action.payload } },
  },
})

export const { setSearchQuery, setSearchResults, setSelectedLocation, setUserLocation, setSortBy, setFilters } =
  parkingSlice.actions
export default parkingSlice.reducer
