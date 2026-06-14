import { CreditCard, Smartphone, Wallet, QrCode } from 'lucide-react'

const METHODS = [
  { id: 'UPI', label: 'Direct UPI / QR', icon: Smartphone },
  { id: 'CARD', label: 'Credit / Debit Card', icon: CreditCard },
  { id: 'WALLET', label: 'Digital Wallet', icon: Wallet },
]

export default function PaymentForm({ payment, setPayment, amount }) {
  // UPI URI for direct payment to the user's account
  const upiId = '9617248701@ybl'
  const upiUrl = `upi://pay?pa=${upiId}&pn=SmartPark&am=${amount || 0}&cu=INR`
  const qrUrl = `https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${encodeURIComponent(upiUrl)}`

  return (
    <div className="space-y-6">
      <div className="bg-brand-50 dark:bg-brand-900/20 rounded-2xl p-6 flex justify-between items-center border border-brand-100 dark:border-brand-900/50">
        <div>
          <p className="text-xs font-black uppercase tracking-widest text-brand-600 dark:text-brand-400">Payable Amount</p>
          <p className="text-3xl font-black text-slate-900 dark:text-white mt-1">₹{amount ?? '—'}</p>
        </div>
        <div className="p-3 bg-white dark:bg-slate-800 rounded-xl shadow-sm">
          <Smartphone className="w-6 h-6 text-brand-600" />
        </div>
      </div>

      <div className="space-y-3">
        <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 ml-1">Payment Method</label>
        <div className="grid grid-cols-3 gap-3">
          {METHODS.map(({ id, label, icon: Icon }) => (
            <button
              key={id}
              type="button"
              onClick={() => setPayment({ ...payment, paymentMethod: id })}
              className={`p-4 rounded-2xl border-2 transition-all flex flex-col items-center gap-2 ${
                payment.paymentMethod === id
                  ? 'border-brand-500 bg-brand-50 dark:bg-brand-900/30 text-brand-700 dark:text-brand-300'
                  : 'border-slate-100 dark:border-slate-800 hover:border-brand-200 text-slate-500'
              }`}
            >
              <Icon className="w-5 h-5" />
              <span className="text-[10px] font-black uppercase tracking-tighter text-center leading-none">{label}</span>
            </button>
          ))}
        </div>
      </div>

      {payment.paymentMethod === 'UPI' && (
        <div className="space-y-4 animate-in fade-in slide-in-from-top-2 duration-300">
          <div className="card !p-6 bg-slate-50 dark:bg-slate-800/50 flex flex-col items-center text-center">
             <p className="text-xs font-bold text-slate-500 mb-4 uppercase tracking-widest">Scan to Pay via Any UPI App</p>
             <div className="p-4 bg-white rounded-3xl shadow-xl mb-4 border-4 border-brand-100">
                <img src={qrUrl} alt="UPI QR Code" className="w-40 h-40" />
             </div>
             <div className="space-y-1">
                <p className="font-black text-slate-900 dark:text-white">{upiId}</p>
                <p className="text-[10px] text-slate-400 font-bold uppercase tracking-widest">Direct Bank Transfer Enabled</p>
             </div>
          </div>
          
          <div className="space-y-2">
            <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 ml-1">Transaction Ref / ID (Optional)</label>
            <input
              className="input-field"
              placeholder="Enter UPI ID or Ref No."
              value={payment.upiId || ''}
              onChange={(e) => setPayment({ ...payment, upiId: e.target.value })}
            />
          </div>
        </div>
      )}

      {payment.paymentMethod === 'CARD' && (
        <div className="grid sm:grid-cols-2 gap-4 animate-in fade-in slide-in-from-top-2">
          <div className="sm:col-span-2">
            <label className="text-xs font-bold text-slate-400 block mb-1">Cardholder Name</label>
            <input
              className="input-field"
              placeholder="Name on card"
              value={payment.cardHolderName || ''}
              onChange={(e) => setPayment({ ...payment, cardHolderName: e.target.value })}
            />
          </div>
          <div>
            <label className="text-xs font-bold text-slate-400 block mb-1">Card Number</label>
            <input
              className="input-field"
              placeholder="•••• •••• •••• ••••"
              maxLength={19}
              value={payment.cardNumber || ''}
              onChange={(e) => setPayment({ ...payment, cardNumber: e.target.value })}
            />
          </div>
          <div>
            <label className="text-xs font-bold text-slate-400 block mb-1">Expiry / CVV</label>
            <input
              className="input-field"
              placeholder="MM/YY •••"
              maxLength={7}
              value={payment.cardLastFour || ''}
              onChange={(e) => setPayment({ ...payment, cardLastFour: e.target.value })}
            />
          </div>
        </div>
      )}

      {payment.paymentMethod === 'WALLET' && (
        <div className="p-5 bg-brand-50 dark:bg-brand-900/10 rounded-2xl border border-brand-100 dark:border-brand-900/30 flex gap-4 items-start animate-in fade-in slide-in-from-top-2">
          <Wallet className="w-5 h-5 text-brand-600 mt-1" />
          <div>
             <p className="text-sm font-bold text-brand-700 dark:text-brand-400 uppercase tracking-widest">SmartPark Wallet</p>
             <p className="text-xs text-slate-600 dark:text-slate-400 mt-1 leading-relaxed">
               Pay with your secure digital wallet for instant entry authorization. 
               Insufficient balance? Top up in your profile.
             </p>
          </div>
        </div>
      )}

      <p className="text-[10px] text-center text-slate-400 font-medium px-4">
        By clicking pay, you authorize this transaction. Payments are processed securely via SSL encryption.
      </p>
    </div>
  )
}
