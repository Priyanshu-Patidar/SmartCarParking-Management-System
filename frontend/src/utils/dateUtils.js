/** Converts datetime-local value to API format without UTC shift */
export function formatLocalDateTimeForApi(datetimeLocal) {
  if (!datetimeLocal) return ''
  if (datetimeLocal.length === 16) return `${datetimeLocal}:00`
  return datetimeLocal
}

export function addHoursToLocalDateTime(datetimeLocal, hours) {
  const start = formatLocalDateTimeForApi(datetimeLocal)
  if (!start) return ''
  const [datePart, timePart] = start.split('T')
  const [y, m, d] = datePart.split('-').map(Number)
  const [hh, mm, ss] = timePart.split(':').map(Number)
  const date = new Date(y, m - 1, d, hh, mm, ss || 0)
  date.setHours(date.getHours() + hours)
  const pad = (n) => String(n).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

/** Min value for datetime-local input (now, local) */
export function minDateTimeLocal() {
  const now = new Date()
  now.setMinutes(now.getMinutes() + 15)
  const pad = (n) => String(n).padStart(2, '0')
  return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}T${pad(now.getHours())}:${pad(now.getMinutes())}`
}
