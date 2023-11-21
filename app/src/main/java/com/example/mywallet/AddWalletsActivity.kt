package com.example.mywallet

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.example.mywallet.R
import com.example.mywallet.classes.Wallet


class AddWalletsActivity : AppCompatActivity() {

    private lateinit var editTextNombre: EditText
    private lateinit var editTextMonto: EditText
    private lateinit var spinnerMoneda: Spinner
    private lateinit var btnGuardar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_wallets)
        editTextNombre = findViewById(R.id.addTextNombre)
        editTextMonto = findViewById(R.id.addTextMonto)
        spinnerMoneda = findViewById(R.id.addspinnerMoneda)
        btnGuardar = findViewById(R.id.addBtnSave)
        val monedasArray = resources.getStringArray(R.array.monedas)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, monedasArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMoneda.adapter = adapter
        btnGuardar.setOnClickListener {
            guardarWallet()
        }
    }

    private fun guardarWallet() {
        val nombre = editTextNombre.text.toString()
        val monto = editTextMonto.text.toString().toIntOrNull() ?: 0
        val moneda = spinnerMoneda.selectedItem.toString()
        val wallet = Wallet(System.currentTimeMillis(), nombre, monto, moneda)
        val resultIntent = Intent()
        resultIntent.putExtra("wallet", wallet)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}