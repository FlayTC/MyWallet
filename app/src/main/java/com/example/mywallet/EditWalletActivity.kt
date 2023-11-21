package com.example.mywallet

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.example.mywallet.R
import com.example.mywallet.classes.Wallet


class EditWalletActivity : AppCompatActivity() {

    private lateinit var editedWallet: Wallet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_wallet)
        editedWallet = intent.getParcelableExtra("wallet") ?: return

        val editTextNombre = findViewById<EditText>(R.id.editTextNombre)
        val editTextMonto = findViewById<EditText>(R.id.editTextMonto)
        val spinnerMoneda = findViewById<Spinner>(R.id.editSpinnerMoneda)
        val btnGuardar = findViewById<Button>(R.id.editBtnSave)

        editTextNombre.setText(editedWallet.walletName)
        editTextMonto.setText(editedWallet.balance.toString())
        val currencyIndex = resources.getStringArray(R.array.monedas).indexOf(editedWallet.currency)
        spinnerMoneda.setSelection(currencyIndex)

        btnGuardar.setOnClickListener {
            saveChanges()
        }
    }

    private fun saveChanges() {
        val editedName = findViewById<EditText>(R.id.editTextNombre).text.toString()
        val editedAmount = findViewById<EditText>(R.id.editTextMonto).text.toString().toIntOrNull()
            ?: 0
        val editedCurrency = findViewById<Spinner>(R.id.editSpinnerMoneda).selectedItem.toString()
        editedWallet.apply {
            walletName = editedName
            balance = editedAmount
            currency = editedCurrency
        }
        Log.d("EditWalletActivity", "Changes saved. Edited Wallet: $editedWallet")
        val resultIntent = Intent().apply {
            putExtra("editedWallet", editedWallet)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

}