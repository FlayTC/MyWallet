package com.example.mywallet.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mywallet.AddWalletsActivity
import com.example.mywallet.EditWalletActivity
import com.example.mywallet.R
import com.example.mywallet.classes.Wallet
import com.example.mywallet.WalletListener
import com.example.mywallet.adapter.WalletAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class WalletsFragment : Fragment(), WalletListener {

    private var dashboardListener: DashboardFragment? = null

    private var walletAdapter: WalletAdapter? = null
    private var walletListener: WalletListener? = null

    private lateinit var addWalletLauncher: ActivityResultLauncher<Intent>
    private lateinit var sharedPreferences: SharedPreferences
    private val WALLET_PREFS_KEY = "wallet_prefs"

    private lateinit var recyclerView: RecyclerView
    private val walletList = mutableListOf(
        Wallet((System.currentTimeMillis()),"Ejemplo 1", 100, "USD"),
    )

    private val editWalletLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val editedWallet: Wallet? = data?.getParcelableExtra("editedWallet")
            if (editedWallet != null) {
                updateWallet(editedWallet)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wallets, container, false)

        addWalletLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val wallet: Wallet? = data?.getParcelableExtra("wallet")
                if (wallet != null) {
                    addWallet(wallet)
                }
            }
        }

        val fabAddWallet = view.findViewById<FloatingActionButton>(R.id.fabAddWallet)
        fabAddWallet.setOnClickListener {
            val intent = Intent(requireContext(), AddWalletsActivity::class.java)
            addWalletLauncher.launch(intent)
        }

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewWallets)

        walletAdapter = WalletAdapter(requireContext(), walletList)
        recyclerView.adapter = walletAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        sharedPreferences = requireContext().getSharedPreferences(WALLET_PREFS_KEY, Context.MODE_PRIVATE)

        restoreWallets()
        walletAdapter?.onItemEditListener = { wallet ->
            editWallet(wallet)
        }
        walletAdapter?.onItemRemoveListener = { walletId ->
            removeWallet(walletId)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is WalletListener) {
            walletListener = context
        } else {
            throw RuntimeException("$context must implement WalletListener")
        }
    }

    fun addWallet(wallet: Wallet) {
        val newId = walletList.size.toLong() + 1
        val walletWithId = wallet.copy(id = newId)
        walletListener?.onWalletAdded(wallet)
        walletList.add(walletWithId)
        requireActivity().runOnUiThread {
            walletAdapter?.notifyDataSetChanged()
        }
        saveWallets()
        Log.d("WalletsFragment", "Wallet saved. Amount: ${wallet.balance} ${wallet.currency}")

        walletListener?.onWalletAdded(wallet)
        updateDashboardFragment(wallet)
    }

    private fun updateWallet(editedWallet: Wallet) {
        val editedPosition = walletList.indexOfFirst { it.id == editedWallet.id }
        if (editedPosition != -1) {
            walletList[editedPosition] = editedWallet
            requireActivity().runOnUiThread {
                walletAdapter?.notifyItemChanged(editedPosition)
            }
            saveWallets()
        }
    }

    private fun saveWallets() {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(walletList)
        editor.putString(WALLET_PREFS_KEY, json)
        editor.apply()
        walletList.forEach { wallet ->
            saveAmountForWallet(wallet.currency, wallet.balance.toDouble())
        }
        Log.d("WalletsFragment", "Wallets saved.")
    }

    private fun saveAmountForWallet(currency: String, amount: Double) {
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat(currency, amount.toFloat())
        editor.apply()

        Log.d("WalletsFragment", "Saved amount for $currency: $amount")
    }

    private fun restoreWallets() {
        val json = sharedPreferences.getString(WALLET_PREFS_KEY, null)
        if (json != null) {
            val gson = Gson()
            val type = object : TypeToken<List<Wallet>>() {}.type
            walletList.clear()
            walletList.addAll(gson.fromJson(json, type))
            walletAdapter?.notifyDataSetChanged()
        }
    }

    private fun removeWallet(walletId: Long) {
        Log.d("WalletsFragment", "removeWallet: $walletId")
        val removedPosition = walletList.indexOfFirst { it.id == walletId }
        if (removedPosition != -1) {
            val removedWallet = walletList.removeAt(removedPosition)
            requireActivity().runOnUiThread {
                walletAdapter?.notifyItemRemoved(removedPosition)
            }
            saveWallets()

            // Asignar IDs nuevamente en orden
            walletList.forEachIndexed { index, wallet ->
                wallet.id = (index + 1).toLong()
            }
        }
    }

    private fun editWallet(wallet: Wallet) {
        Log.d("WalletsFragment", "editWallet: $wallet")
        val intent = Intent(requireContext(), EditWalletActivity::class.java)
        intent.putExtra("wallet", wallet)
        editWalletLauncher.launch(intent)
    }

    private fun updateDashboardFragment(wallet: Wallet) {
        walletListener?.onWalletAdded(wallet)
    }

    fun setDashboardListener(listener: DashboardFragment) {
        dashboardListener = listener
    }

    override fun onWalletAdded(wallet: Wallet) {
        dashboardListener?.updateWalletInfo(wallet.currency, wallet.balance.toDouble())
        Log.d("WalletsFragment", "onWalletAdded: $wallet")
    }



}