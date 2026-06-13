import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'
import {
  MapPin, Shield, Zap, BarChart3, Bell, Sparkles, Leaf, QrCode, Clock, TrendingUp,
  ChevronRight, Star, CheckCircle2, Globe, Users, Trophy
} from 'lucide-react'
import { brand, hero, features, statsLabels } from '../content/siteCopy'
import { insightsApi } from '../api/services'
import SmartRecommendations from '../components/SmartRecommendations'

const icons = [Zap, TrendingUp, BarChart3, Shield, QrCode, Leaf, Sparkles, Bell, MapPin, Clock]

export default function Home() {
  const [stats, setStats] = useState(null)

  useEffect(() => {
    insightsApi.publicStats().then(({ data }) => setStats(data)).catch(() => {})
  }, [])

  const container = {
    hidden: { opacity: 0 },
    show: {
      opacity: 1,
      transition: { staggerChildren: 0.1 }
    }
  }

  return (
    <div className="space-y-24 pb-24 overflow-x-hidden">
      {/* Hero Section */}
      <section className="relative pt-12 md:pt-24 lg:pt-32">
        {/* Background Gradients */}
        <div className="absolute top-0 left-1/2 -translate-x-1/2 w-full h-full -z-10 overflow-hidden pointer-events-none">
          <div className="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] bg-brand-500/10 blur-[120px] rounded-full animate-pulse" />
          <div className="absolute bottom-[10%] right-[-10%] w-[30%] h-[30%] bg-purple-500/10 blur-[120px] rounded-full" />
        </div>

        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center space-y-8">
          <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-brand-50 dark:bg-brand-950/30 border border-brand-100 dark:border-brand-900/50 text-brand-700 dark:text-brand-300 text-sm font-bold shadow-sm mb-4"
          >
            <Sparkles className="w-4 h-4" />
            <span>{brand.tagline}</span>
          </motion.div>

          <motion.h1 
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="heading-xl max-w-4xl mx-auto"
          >
            {hero.title.split(',')[0]}, <br />
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-brand-600 to-brand-400">{hero.title.split(',')[1] || 'Reimagined for Enterprise.'}</span>
          </motion.h1>
          
          <motion.p 
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1 }}
            className="text-lg md:text-xl text-slate-500 dark:text-slate-400 max-w-2xl mx-auto leading-relaxed"
          >
            {hero.subtitle}
          </motion.p>

          <motion.div 
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2 }}
            className="flex flex-col sm:flex-row items-center justify-center gap-4 pt-4"
          >
            <Link to="/register" className="btn-primary group w-full sm:w-auto">
              Get Started for Free
              <ChevronRight className="w-5 h-5 group-hover:translate-x-1 transition-transform" />
            </Link>
            <Link to="/search" className="btn-secondary w-full sm:w-auto flex items-center justify-center gap-2">
              <MapPin className="w-5 h-5" /> Explore Network
            </Link>
          </motion.div>

          {/* Social Proof / Stats */}
          <motion.div 
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.4 }}
            className="pt-12 grid grid-cols-2 md:grid-cols-4 gap-8 max-w-4xl mx-auto border-t border-slate-100 dark:border-slate-800"
          >
            <QuickStat label={statsLabels.locations} value={stats?.totalLocations || '150+'} />
            <QuickStat label={statsLabels.slots} value={stats?.availableSlots || '12k+'} />
            <QuickStat label="User Rating" value="4.9/5.0" />
            <QuickStat label="Cities" value={stats?.totalCities || '25+'} />
          </motion.div>
        </div>
      </section>

      {/* Trust Section */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="card glass p-12 text-center relative overflow-hidden rounded-[2rem]">
          <div className="absolute inset-0 bg-gradient-to-br from-brand-500/5 to-transparent pointer-events-none" />
          <h2 className="heading-lg mb-4 text-slate-400 uppercase tracking-widest text-sm font-bold">Trusted by Leading Property Developers</h2>
          <div className="flex flex-wrap justify-center gap-8 md:gap-16 mt-8 opacity-40 grayscale group-hover:grayscale-0 transition-all duration-700">
             <div className="text-xl font-black flex items-center gap-2 tracking-tighter text-slate-500 dark:text-slate-400"><Globe className="w-6 h-6" /> METRO PROPERTY</div>
             <div className="text-xl font-black flex items-center gap-2 tracking-tighter text-slate-500 dark:text-slate-400"><Shield className="w-6 h-6" /> SECURE ESTATE</div>
             <div className="text-xl font-black flex items-center gap-2 tracking-tighter text-slate-500 dark:text-slate-400"><Users className="w-6 h-6" /> URBAN HUB</div>
             <div className="text-xl font-black flex items-center gap-2 tracking-tighter text-slate-500 dark:text-slate-400"><Trophy className="w-6 h-6" /> PRIME CORE</div>
          </div>
        </div>
      </section>

      {/* Recommendations */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between mb-8">
          <div>
            <h2 className="heading-lg">Smart Recommendations</h2>
            <p className="text-slate-500">AI-tailored spots based on your preferences</p>
          </div>
          <Link to="/search" className="text-brand-600 font-bold hover:underline hidden sm:block">View all →</Link>
        </div>
        <SmartRecommendations />
      </section>

      {/* Features Grid */}
      <section className="bg-slate-50/50 dark:bg-slate-900/50 py-24 border-y border-slate-100 dark:border-slate-800/50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16 space-y-4">
            <h2 className="heading-lg">Enterprise-Grade Performance</h2>
            <p className="text-slate-500 max-w-2xl mx-auto">Everything you need to manage modern parking facilities at scale.</p>
          </div>

          <motion.div 
            variants={container}
            initial="hidden"
            whileInView="show"
            viewport={{ once: true }}
            className="grid md:grid-cols-3 gap-8"
          >
            {features.slice(0, 6).map((f, i) => (
              <FeatureCard 
                key={f.title}
                icon={icons[i % icons.length]} 
                title={f.title} 
                desc={f.desc} 
                color={i % 2 === 0 ? 'bg-brand-600' : 'bg-slate-900 dark:bg-slate-800'}
              />
            ))}
          </motion.div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <div className="card bg-brand-600 text-white border-none py-20 px-8 relative overflow-hidden group rounded-[3rem]">
          <div className="absolute top-0 right-0 w-96 h-96 bg-white/10 rounded-full -mr-48 -mt-48 blur-[80px] group-hover:scale-150 transition-transform duration-1000" />
          <div className="absolute bottom-0 left-0 w-96 h-96 bg-black/10 rounded-full -ml-48 -mb-48 blur-[80px]" />
          
          <div className="relative z-10 space-y-8">
            <h2 className="text-4xl md:text-5xl font-black tracking-tight !text-white">Ready to optimize your facility?</h2>
            <p className="text-brand-100 text-lg md:text-xl max-w-2xl mx-auto">
              Join thousands of operators transforming their parking spaces into high-performance digital assets.
            </p>
            <div className="flex flex-col sm:flex-row items-center justify-center gap-4 pt-6">
              <Link to="/register" className="bg-white text-brand-600 hover:bg-brand-50 font-black px-10 py-5 rounded-2xl transition-all shadow-2xl active:scale-95 w-full sm:w-auto text-lg">
                Create Free Account
              </Link>
              <Link to="/search" className="bg-brand-700/50 hover:bg-brand-700 text-white font-black px-10 py-5 rounded-2xl transition-all active:scale-95 border border-brand-500/30 w-full sm:w-auto text-lg">
                Browse Facilities
              </Link>
            </div>
          </div>
        </div>
      </section>
    </div>
  )
}

function QuickStat({ label, value }) {
  return (
    <div className="space-y-1">
      <p className="text-3xl font-black text-slate-900 dark:text-white tracking-tighter">{value}</p>
      <p className="text-xs font-bold text-slate-400 uppercase tracking-widest">{label}</p>
    </div>
  )
}

function FeatureCard({ icon: Icon, title, desc, color }) {
  return (
    <motion.div 
      variants={{ hidden: { opacity: 0, y: 20 }, show: { opacity: 1, y: 0 } }}
      className="card card-hover flex flex-col items-center text-center group p-8 rounded-[2rem]"
    >
      <div className={`${color} p-5 rounded-[1.5rem] text-white shadow-2xl shadow-inherit/20 mb-8 group-hover:scale-110 group-hover:rotate-3 transition-all duration-500`}>
        <Icon className="w-8 h-8" />
      </div>
      <h3 className="text-xl font-bold mb-4">{title}</h3>
      <p className="text-slate-500 leading-relaxed text-sm">{desc}</p>
    </motion.div>
  )
}
