import { CreditCard, Smartphone, Wallet, CheckCircle2, ExternalLink } from 'lucide-react'
import { motion } from 'framer-motion'
import toast from 'react-hot-toast'

const METHODS = [
  { id: 'UPI', label: 'UPI / QR', icon: Smartphone },
  { id: 'CARD', label: 'Debit/Credit', icon: CreditCard },
  { id: 'WALLET', label: 'Wallet', icon: Wallet },
]

const UPI_APPS = [
  { 
    id: 'phonepe', 
    name: 'PhonePe', 
    color: 'bg-white', 
    logo: 'https://uxwing.com/wp-content/themes/uxwing/download/brands-and-social-media/phonepe-logo-icon.png' 
  },
  { 
    id: 'paytm', 
    name: 'Paytm', 
    color: 'bg-white', 
    logo: 'https://uxwing.com/wp-content/themes/uxwing/download/brands-and-social-media/paytm-icon.png' 
  },
  { 
    id: 'gpay', 
    name: 'GPay', 
    color: 'bg-white', 
    logo: 'https://uxwing.com/wp-content/themes/uxwing/download/brands-and-social-media/google-pay-icon.png'
  },
  { 
    id: 'bhim', 
    name: 'BHIM', 
    color: 'bg-white', 
    logo: 'https://uxwing.com/wp-content/themes/uxwing/download/brands-and-social-media/bhim-upi-icon.png' 
  }
]

export default function PaymentForm({ payment, setPayment, amount }) {
  const upiId = '9617248701@ybl'
  const upiUrl = `upi://pay?pa=${upiId}&pn=SmartPark&am=${amount || 0}&cu=INR`
  const qrUrl = `https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=${encodeURIComponent(upiUrl)}`

  return (
    <div className="space-y-8">
      {/* Amount Header */}
      <div className="card glass !p-8 border-brand-500/20 relative overflow-hidden">
        <div className="absolute top-0 right-0 w-32 h-32 bg-brand-500/10 rounded-full blur-3xl" />
        <div className="flex justify-between items-center relative z-10">
          <div>
            <p className="text-[10px] font-black uppercase tracking-[0.3em] text-brand-600 mb-1">Total Payable</p>
            <p className="text-4xl font-black text-slate-900 dark:text-white tracking-tighter">₹{amount ?? '—'}</p>
          </div>
          <div className="flex flex-col items-end">
            <div className="flex items-center gap-2 text-emerald-500 font-bold text-[10px] bg-emerald-50 dark:bg-emerald-950/30 px-3 py-1.5 rounded-full uppercase tracking-widest border border-emerald-100 dark:border-emerald-900/50">
              <CheckCircle2 className="w-3 h-3" />
              Direct Node Transfer
            </div>
          </div>
        </div>
      </div>

      {/* Method Selector */}
      <div className="space-y-4">
        <label className="text-[10px] font-black uppercase tracking-[0.3em] text-slate-400 ml-2">Secure Methods</label>
        <div className="grid grid-cols-3 gap-4">
          {METHODS.map(({ id, label, icon: Icon }) => (
            <button
              key={id}
              type="button"
              onClick={() => setPayment({ ...payment, paymentMethod: id })}
              className={`p-5 rounded-[2rem] border-2 transition-all flex flex-col items-center gap-3 ${
                payment.paymentMethod === id
                  ? 'border-brand-500 bg-brand-50 dark:bg-brand-900/30 text-brand-700 dark:text-brand-300 shadow-xl shadow-brand-500/10'
                  : 'border-slate-100 dark:border-slate-800 hover:border-brand-200 text-slate-500'
              }`}
            >
              <Icon className="w-6 h-6" />
              <span className="text-[10px] font-black uppercase tracking-tight">{label}</span>
            </button>
          ))}
        </div>
      </div>

      {/* UPI Section */}
      {payment.paymentMethod === 'UPI' && (
        <motion.div 
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          className="space-y-6"
        >
          {/* App Intents */}
          <div className="space-y-4">
             <p className="text-[10px] font-black uppercase tracking-widest text-center text-slate-400">One-Tap Checkout (Mobile Only)</p>
             <div className="grid grid-cols-4 gap-4">
                {UPI_APPS.map(app => (
                  <a
                    key={app.id}
                    href={upiUrl}
                    onClick={() => toast.success(`Launching ${app.name}...`)}
                    className="flex flex-col items-center gap-2 group"
                  >
                    <div className={`w-14 h-14 ${app.color} rounded-2xl p-2.5 shadow-lg group-hover:scale-110 active:scale-95 transition-all border border-slate-100 dark:border-slate-800 flex items-center justify-center overflow-hidden hover:border-brand-500/50`}>
                       <img 
                        src={app.logo} 
                        alt={app.name} 
                        className="w-full h-full object-contain" 
                        onError={(e) => e.target.src = 'https://uxwing.com/wp-content/themes/uxwing/download/brands-and-social-media/upi-icon.png'} 
                       />
                    </div>
                    <span className="text-[9px] font-black uppercase text-slate-500 tracking-tighter">{app.name}</span>
                  </a>
                ))}
             </div>
          </div>

          <div className="relative py-4">
             <div className="absolute inset-0 flex items-center"><div className="w-full border-t border-slate-100 dark:border-slate-800" /></div>
             <div className="relative flex justify-center"><span className="bg-white dark:bg-slate-950 px-4 text-[10px] font-black text-slate-300 uppercase tracking-[0.3em]">OR SCAN QR</span></div>
          </div>

          {/* QR Fallback */}
          <div className="card !p-8 bg-slate-50 dark:bg-slate-900 flex flex-col items-center text-center rounded-[2.5rem] border-2 border-dashed border-slate-200 dark:border-slate-800">
             <div className="p-4 bg-white rounded-3xl shadow-2xl mb-6 border-8 border-white group relative overflow-hidden">
                <img src={qrUrl} alt="UPI QR" className="w-48 h-48 rounded-lg" />
                <div className="absolute inset-0 flex items-center justify-center bg-white/90 opacity-0 group-hover:opacity-100 transition-opacity rounded-lg">
                   <p className="text-xs font-black text-brand-600">ENCRYPTED NODE</p>
                </div>
             </div>
             <div className="space-y-2">
                <p className="text-lg font-black text-slate-900 dark:text-white tracking-tight">{upiId}</p>
                <div className="flex items-center justify-center gap-2 text-[10px] font-bold text-slate-400 uppercase tracking-widest">
                   <ExternalLink className="w-3 h-3" />
                   Scannable via any App
                </div>
             </div>
          </div>
          
          <div className="space-y-2">
            <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 ml-2">Transaction ID (Optional)</label>
            <input
              className="input-field !rounded-2xl"
              placeholder="e.g. 1234567890"
              value={payment.upiId || ''}
              onChange={(e) => setPayment({ ...payment, upiId: e.target.value })}
            />
          </div>
        </motion.div>
      )}

      {/* Card Section */}
      {payment.paymentMethod === 'CARD' && (
        <motion.div 
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          className="grid sm:grid-cols-2 gap-6"
        >
          <div className="sm:col-span-2">
            <label className="text-[10px] font-black uppercase text-slate-400 ml-2">Holder Name</label>
            <input
              className="input-field mt-1"
              placeholder="Full name on card"
              value={payment.cardHolderName || ''}
              onChange={(e) => setPayment({ ...payment, cardHolderName: e.target.value })}
            />
          </div>
          <div>
            <label className="text-[10px] font-black uppercase text-slate-400 ml-2">Card Number</label>
            <input
              className="input-field mt-1"
              placeholder="•••• •••• •••• ••••"
              maxLength={19}
              value={payment.cardNumber || ''}
              onChange={(e) => setPayment({ ...payment, cardNumber: e.target.value })}
            />
          </div>
          <div>
            <label className="text-[10px] font-black uppercase text-slate-400 ml-2">CVV / Expiry</label>
            <input
              className="input-field mt-1"
              placeholder="MM/YY •••"
              maxLength={7}
              value={payment.cardLastFour || ''}
              onChange={(e) => setPayment({ ...payment, cardLastFour: e.target.value })}
            />
          </div>
        </motion.div>
      )}

      {/* Wallet Section */}
      {payment.paymentMethod === 'WALLET' && (
        <motion.div 
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          className="p-8 bg-brand-50 dark:bg-brand-950/20 rounded-[3rem] border-2 border-brand-100 dark:border-brand-900/50 flex flex-col items-center text-center gap-4"
        >
          <div className="p-4 bg-white dark:bg-slate-800 rounded-2xl shadow-xl">
            <Wallet className="w-8 h-8 text-brand-600" />
          </div>
          <div>
             <p className="text-lg font-black text-brand-700 dark:text-brand-300 uppercase tracking-tight">SmartPark Wallet</p>
             <p className="text-xs text-slate-600 dark:text-slate-400 mt-2 leading-relaxed max-w-xs">
               Zero-fee internal transfer. Funds are deducted securely from your linked account balance.
             </p>
          </div>
        </motion.div>
      )}

      <div className="pt-4 border-t border-slate-100 dark:border-slate-800">
         <p className="text-[8px] text-center text-slate-400 font-bold uppercase tracking-[0.3em] leading-relaxed">
           DIRECT NODE P2P ENCRYPTION • 9617248701@YBL • VERIFIED BANK TRANSFER
         </p>
      </div>
    </div>
  )
}
