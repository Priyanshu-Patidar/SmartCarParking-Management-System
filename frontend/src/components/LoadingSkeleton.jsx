import { motion } from 'framer-motion'

export default function LoadingSkeleton({ variant = 'card', count = 1 }) {
  const variants = {
    card: (
      <div className="card p-6 space-y-4 animate-pulse">
        <div className="h-48 bg-slate-100 dark:bg-slate-800 rounded-2xl w-full" />
        <div className="space-y-2">
          <div className="h-4 bg-slate-100 dark:bg-slate-800 rounded w-3/4" />
          <div className="h-4 bg-slate-100 dark:bg-slate-800 rounded w-1/2" />
        </div>
      </div>
    ),
    list: (
      <div className="flex items-center gap-4 p-4 animate-pulse">
        <div className="w-12 h-12 bg-slate-100 dark:bg-slate-800 rounded-xl" />
        <div className="flex-1 space-y-2">
          <div className="h-4 bg-slate-100 dark:bg-slate-800 rounded w-1/4" />
          <div className="h-3 bg-slate-100 dark:bg-slate-800 rounded w-1/2" />
        </div>
      </div>
    ),
    stats: (
      <div className="card p-5 animate-pulse">
        <div className="w-10 h-10 bg-slate-100 dark:bg-slate-800 rounded-xl mb-4" />
        <div className="h-4 bg-slate-100 dark:bg-slate-800 rounded w-1/2 mb-2" />
        <div className="h-8 bg-slate-100 dark:bg-slate-800 rounded w-3/4" />
      </div>
    )
  }

  return (
    <div className="grid gap-4">
      {Array.from({ length: count }).map((_, i) => (
        <motion.div
          key={i}
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: i * 0.1 }}
        >
          {variants[variant]}
        </motion.div>
      ))}
    </div>
  )
}
