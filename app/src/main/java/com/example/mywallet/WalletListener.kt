package com.example.mywallet

import com.example.mywallet.classes.Wallet

interface WalletListener {
    fun onWalletAdded(wallet: Wallet)
}