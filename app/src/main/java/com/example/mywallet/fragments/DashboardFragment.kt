package com.example.mywallet.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mywallet.MainActivity
import com.example.mywallet.R
import com.example.mywallet.classes.Wallet
import com.example.mywallet.WalletListener
import java.util.Locale


class DashboardFragment : Fragment(), WalletListener {

    private var walletListener: WalletListener? = null
    private lateinit var pesoTotalTextView: TextView
    private var pesoTotal: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        pesoTotalTextView = view.findViewById(R.id.pesoTotalTextView)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            logoutUser()
        }
        (requireActivity() as? WalletListener)?.let { walletListener ->
            this.walletListener = walletListener
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is WalletListener) {
            walletListener = context
            (requireActivity() as? WalletsFragment)?.setDashboardListener(this)
        } else {
            throw RuntimeException("$context must implement WalletListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        (requireActivity() as? WalletListener)?.let { walletListener ->
            this.walletListener = walletListener
        }
    }

    internal fun updateTotalAmounts() {
        Log.d("DashboardFragment", "updateTotalAmounts: Updating total amounts")
        val totalAmountByCurrency = mapOf("PESOS" to pesoTotal) // Puedes agregar otras monedas si es necesario
        pesoTotalTextView.text = getString(R.string.peso_total, pesoTotal.toString())
    }

    private fun logoutUser() {
        with(requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit()) {
            remove("username")
            remove("password")
            apply()
        }

        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onPause() {
        super.onPause()
        saveTotalAmountsToSharedPreferences()
    }
    private fun saveTotalAmountsToSharedPreferences() {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putFloat("PESO_AMOUNT", pesoTotal.toFloat())
            apply()
        }
    }
    override fun onWalletAdded(wallet: Wallet) {
        Log.d("DashboardFragment", "onWalletAdded: $wallet")

        updateWalletInfo(wallet.currency, wallet.balance.toDouble())
    }

    internal fun updateWalletInfo(currency: String, amount: Double) {
        when (currency.toUpperCase(Locale.ROOT)) {
            "PESOS" -> {
                pesoTotal += amount
                updateTotalAmounts()
            }
            "USD" -> {
                val usdToPesoRate = getInfoFragment()?.getUsdToPesoRate() ?: 1.0
                pesoTotal += amount * usdToPesoRate
                updateTotalAmounts()
            }
        }
        Log.d("DashboardFragment", "Currency: $currency, Amount: $amount")

    }

    private fun getInfoFragment(): InfoFragment? {
        return requireActivity().supportFragmentManager.findFragmentByTag("infoFragment") as? InfoFragment
    }

    override fun onResume() {
        super.onResume()
        Log.d("DashboardFragment", "onResume")
    }

}
