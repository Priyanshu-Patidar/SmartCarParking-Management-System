import { useEffect, useState } from 'react'
import toast from 'react-hot-toast'
import { adminApi } from '../../api/services'

export default function AdminUsers() {
  const [users, setUsers] = useState([])

  const load = () => adminApi.getUsers(0).then(({ data }) => setUsers(data.content || []))
  useEffect(() => { load() }, [])

  const toggleBlock = async (id, blocked) => {
    await adminApi.blockUser(id, !blocked)
    toast.success(blocked ? 'User unblocked' : 'User blocked')
    load()
  }

  const remove = async (id) => {
    if (!confirm('Delete this user?')) return
    await adminApi.deleteUser(id)
    toast.success('User deleted')
    load()
  }

  return (
    <div>
      <h1 className="text-2xl font-bold">User Management</h1>
      <div className="card mt-6 overflow-x-auto">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-slate-200 dark:border-slate-700 text-left">
              <th className="py-3 px-2">Name</th>
              <th className="py-3 px-2">Email</th>
              <th className="py-3 px-2">Status</th>
              <th className="py-3 px-2">Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map((u) => (
              <tr key={u.id} className="border-b border-slate-100 dark:border-slate-800">
                <td className="py-3 px-2">{u.fullName}</td>
                <td className="py-3 px-2">{u.email}</td>
                <td className="py-3 px-2">{u.blocked ? 'Blocked' : 'Active'}</td>
                <td className="py-3 px-2 flex gap-2">
                  <button onClick={() => toggleBlock(u.id, u.blocked)} className="text-amber-600 text-xs font-semibold">
                    {u.blocked ? 'Unblock' : 'Block'}
                  </button>
                  <button onClick={() => remove(u.id)} className="text-red-500 text-xs font-semibold">Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
