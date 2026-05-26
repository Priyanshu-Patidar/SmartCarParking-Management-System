import { useState } from 'react'
import toast from 'react-hot-toast'
import { adminApi } from '../../api/services'

export default function AdminParking() {
  const [form, setForm] = useState({
    name: '', address: '', city: '', latitude: '', longitude: '',
    hourlyRate: '', evChargingAvailable: false, description: '',
  })

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      await adminApi.createParking({
        ...form,
        latitude: parseFloat(form.latitude),
        longitude: parseFloat(form.longitude),
        hourlyRate: parseFloat(form.hourlyRate),
        supportedVehicleTypes: ['CAR', 'BIKE', 'EV'],
      })
      toast.success('Parking location created')
      setForm({ name: '', address: '', city: '', latitude: '', longitude: '', hourlyRate: '', evChargingAvailable: false, description: '' })
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to create')
    }
  }

  return (
    <div>
      <h1 className="text-2xl font-bold">Manage Parking</h1>
      <form onSubmit={handleSubmit} className="card mt-6 grid sm:grid-cols-2 gap-4 max-w-2xl">
        {['name', 'address', 'city', 'latitude', 'longitude', 'hourlyRate', 'description'].map((f) => (
          <input key={f} className="input-field" placeholder={f}
            value={form[f]} onChange={(e) => setForm({ ...form, [f]: e.target.value })} required={f !== 'description'} />
        ))}
        <label className="flex items-center gap-2 sm:col-span-2">
          <input type="checkbox" checked={form.evChargingAvailable}
            onChange={(e) => setForm({ ...form, evChargingAvailable: e.target.checked })} />
          EV Charging Available
        </label>
        <button type="submit" className="btn-primary sm:col-span-2">Add Location</button>
      </form>
    </div>
  )
}
