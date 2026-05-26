import { useSelector } from 'react-redux'
import { selectAuth } from '../store/authSlice'

export default function Profile() {
  const { user } = useSelector(selectAuth)

  return (
    <div className="max-w-lg">
      <h1 className="text-2xl font-bold">Profile</h1>
      <div className="card mt-6 space-y-4">
        <div>
          <label className="text-sm text-slate-500">Full Name</label>
          <p className="font-semibold">{user?.fullName}</p>
        </div>
        <div>
          <label className="text-sm text-slate-500">Email</label>
          <p className="font-semibold">{user?.email}</p>
        </div>
        <div>
          <label className="text-sm text-slate-500">Roles</label>
          <div className="flex gap-2 mt-1">
            {user?.roles?.map((r) => (
              <span key={r} className="text-xs bg-brand-100 dark:bg-brand-900 text-brand-700 px-2 py-1 rounded-lg">{r}</span>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
