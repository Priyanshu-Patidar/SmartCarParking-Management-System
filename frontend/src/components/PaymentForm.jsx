import { CreditCard, Smartphone, Wallet } from 'lucide-react'

const METHODS = [
  { id: 'UPI', label: 'UPI', icon: Smartphone },
  { id: 'CARD', label: 'Credit / Debit Card', icon: CreditCard },
  { id: 'WALLET', label: 'Digital Wallet', icon: Wallet },
]

export default function PaymentForm({ payment, setPayment, amount }) {
  return (
    <div className="space-y-4">
      <div className="bg-brand-50 dark:bg-brand-900/20 rounded-xl p-4 flex justify-between items-center">
        <span className="font-medium">Amount to pay</span>
        <span className="text-2xl font-bold text-brand-600">₹{amount ?? '—'}</span>
      </div>

      <div>
        <label className="text-sm font-medium">Payment method</label>
        <div className="grid grid-cols-3 gap-2 mt-2">
          {METHODS.map(({ id, label, icon: Icon }) => (
            <button
              key={id}
              type="button"
              onClick={() => setPayment({ ...payment, paymentMethod: id })}
              className={`p-3 rounded-xl border text-sm font-medium flex flex-col items-center gap-1 transition ${
                payment.paymentMethod === id
                  ? 'border-brand-500 bg-brand-50 dark:bg-brand-900/30 text-brand-700'
                  : 'border-slate-200 dark:border-slate-700 hover:border-brand-300'
              }`}
            >
              <Icon className="w-5 h-5" />
              {label}
            </button>
          ))}
        </div>
      </div>

      {payment.paymentMethod === 'UPI' && (
        <div>
          <label className="text-sm font-medium">UPI ID</label>
          <input
            className="input-field mt-1"
            placeholder="yourname@upi"
            value={payment.upiId || ''}
            onChange={(e) => setPayment({ ...payment, upiId: e.target.value })}
            required
          />
        </div>
      )}

      {payment.paymentMethod === 'CARD' && (
        <div className="grid sm:grid-cols-2 gap-4">
          <div className="sm:col-span-2">
            <label className="text-sm font-medium">Cardholder name</label>
            <input
              className="input-field mt-1"
              placeholder="Name on card"
              value={payment.cardHolderName || ''}
              onChange={(e) => setPayment({ ...payment, cardHolderName: e.target.value })}
              required
            />
          </div>
          <div>
            <label className="text-sm font-medium">Card number</label>
            <input
              className="input-field mt-1"
              placeholder="1234 5678 9012 3456"
              maxLength={19}
              value={payment.cardNumber || ''}
              onChange={(e) => setPayment({ ...payment, cardNumber: e.target.value })}
              required
            />
          </div>
          <div>
            <label className="text-sm font-medium">Last 4 digits (for receipt)</label>
            <input
              className="input-field mt-1"
              placeholder="3456"
              maxLength={4}
              value={payment.cardLastFour || ''}
              onChange={(e) => setPayment({ ...payment, cardLastFour: e.target.value })}
              required
            />
          </div>
        </div>
      )}

      {payment.paymentMethod === 'WALLET' && (
        <p className="text-sm text-slate-500 bg-slate-100 dark:bg-slate-800 rounded-lg p-3">
          Pay with your linked SmartPark Wallet balance for faster checkout.
        </p>
      )}

      <p className="text-xs text-slate-400">
        Payments are processed securely. You will receive an instant confirmation and receipt by email.
      </p>
    </div>
  )
}
