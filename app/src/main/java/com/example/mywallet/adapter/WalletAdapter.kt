package com.example.mywallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mywallet.R
import com.example.mywallet.classes.Wallet


class WalletAdapter(private val context: Context, private val wallets: List<Wallet>) :
    RecyclerView.Adapter<WalletAdapter.WalletViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.wallet_item, parent, false)
        return WalletViewHolder(view)
    }

    override fun onBindViewHolder(holder: WalletViewHolder, position: Int) {
        val wallet = wallets[position]
        holder.bind(wallet)

    }

    override fun getItemCount(): Int {
        return wallets.size
    }
    var onItemRemoveListener: ((Long) -> Unit)? = null
    var onItemEditListener: ((Wallet) -> Unit)? = null

    inner class WalletViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val walletNameTextView: TextView = itemView.findViewById(R.id.tvWalletName)
        private val amountTextView: TextView = itemView.findViewById(R.id.tvBalance)
        private val currencyTextView: TextView = itemView.findViewById(R.id.tvCurrency)

        private val btnEdit: ImageButton = itemView.findViewById(R.id.fabEditWallet)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.fabDeleteWallet)

        fun bind(wallet: Wallet) {
            walletNameTextView.text = wallet.walletName
            amountTextView.text = "${wallet.balance}"
            currencyTextView.text = wallet.currency

            btnEdit.setOnClickListener {
                onItemEditListener?.invoke(wallet)
            }

            btnDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemRemoveListener?.invoke(wallet.id)
                }
            }
        }
    }
}