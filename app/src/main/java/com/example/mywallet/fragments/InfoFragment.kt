package com.example.mywallet.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mywallet.R
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
class InfoFragment : Fragment() {

    private lateinit var tvDollarOfficialToPeso: TextView
    private lateinit var  tvBitcoinToDollar: TextView
    private lateinit var tvDollarBlueToPeso: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_info, container, false)

        tvDollarOfficialToPeso = view.findViewById(R.id.tv_DollarOfficialToPeso)
        tvBitcoinToDollar = view.findViewById(R.id.tv_BitcoinToDollar)
        tvDollarBlueToPeso = view.findViewById(R.id.tv_DollarBlueToPeso)

        fetchDollarOfficialToPeso()

        fetchDollarBlueToPeso()

        fetchBitcoinToDollar()

        return view
    }

    private fun fetchDollarOfficialToPeso() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.bluelytics.com.ar/v2/latest")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val json = responseBody?.let { JSONObject(it) }
                Log.d("JSON_RESPONSE", json.toString())
                val oficialObject = json?.optJSONObject("oficial")
                if (oficialObject != null && oficialObject.has("value_avg")) {
                    val dollarToPeso = oficialObject.getDouble("value_avg")

                    activity?.runOnUiThread {
                        tvDollarOfficialToPeso.text = "$ $dollarToPeso"
                    }
                } else {
                    Log.e("JSON_PARSING_ERROR", "No se encontró el valor 'value_avg' en el objeto 'oficial'")
                }
            }
        })
    }

    private fun fetchDollarBlueToPeso() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.bluelytics.com.ar/v2/latest")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val json = responseBody?.let { JSONObject(it) }
                Log.d("JSON_RESPONSE", json.toString())
                val blueObject = json?.optJSONObject("blue")
                if (blueObject != null && blueObject.has("value_avg")) {
                    val dollarBlueToPeso = blueObject.getDouble("value_avg")

                    activity?.runOnUiThread {
                        tvDollarBlueToPeso.text = "$ $dollarBlueToPeso"
                    }
                } else {
                    Log.e("JSON_PARSING_ERROR", "No se encontró el valor 'value_avg' en el objeto 'blue'")
                }
            }
        })
    }

    private fun fetchBitcoinToDollar() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://cex.io/api/tickers/BTC/USD")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Manejar el fallo en la llamada
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val json = responseBody?.let { JSONObject(it) }
                val dataArray = json?.optJSONArray("data")
                if (dataArray != null && dataArray.length() > 0) {
                    val dataObject = dataArray.getJSONObject(0)
                    val bitcoinToDollar = dataObject.getString("last")
                    activity?.runOnUiThread {
                        // Actualizar la interfaz de usuario con la cotización obtenida
                        tvBitcoinToDollar.text = "$ $bitcoinToDollar dollars"
                    }
                } else {
                    Log.e("JSON_PARSING_ERROR", "No se encontró el array 'data' o estaba vacío")
                }
            }
        })
    }

    fun getUsdToPesoRate(): Double {
        Log.d("InfoFragment", "getUsdToPesoRate: Returning rate")
        return 1.0
    }
}
