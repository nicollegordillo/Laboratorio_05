package com.example.laboratorio05

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var fromCurrencyEditText:EditText
    private lateinit var toCurrencyEditText:EditText
    private lateinit var amountEditText:EditText
    private lateinit var conversionResultTextView:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fromCurrencyEditText = findViewById(R.id.fromCurrencyEditText)
         toCurrencyEditText = findViewById(R.id.toCurrencyEditText)
         amountEditText = findViewById(R.id.amountEditText)
        val fetchLatestRatesButton = findViewById<Button>(R.id.fetchLatestRatesButton)
        val convertCurrencyButton = findViewById<Button>(R.id.convertCurrencyButton)
        conversionResultTextView = findViewById(R.id.conversionResultTextView)


        fetchLatestRatesButton.setOnClickListener {
            fetchLatestExchangeRates()
        }

        convertCurrencyButton.setOnClickListener {
            performCurrencyConversion()
        }
    }

    private fun fetchLatestExchangeRates() {
        val baseUrl = "https://api.frankfurter.app"
        val endpoint = "/latest"

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$baseUrl$endpoint")
            .build()

        client.newCall(request).enqueue(object : Callback {


            override fun onFailure(call: okhttp3.Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonResponse = JSONObject(responseBody)
                    val rates = jsonResponse.getJSONObject("rates")


                    val usdRate = rates.optDouble("USD")
                    val gbpRate = rates.optDouble("GBP")

                    runOnUiThread {

                        val message = "1 EUR = $usdRate USD\n1 EUR = $gbpRate GBP"
                        conversionResultTextView.text = message
                    }
                } else {
                    runOnUiThread {
                        conversionResultTextView.text = "Error"
                    }
                }
            }
        })
    }

    private fun performCurrencyConversion() {
        val fromCurrency = fromCurrencyEditText.text.toString()
        val toCurrency = toCurrencyEditText.text.toString()
        val amount = amountEditText.text.toString().toDoubleOrNull()

        if (amount != null) {
            val baseUrl = "https://api.frankfurter.app"
            val endpoint = "/latest"

            val client = OkHttpClient()

            val request = Request.Builder()
                .url("$baseUrl$endpoint?from=$fromCurrency&to=$toCurrency")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    TODO("Not yet implemented")
                }


                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {

                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody)
                        val exchangeRate = jsonResponse.getJSONObject("rates").optDouble(toCurrency)

                        if (exchangeRate != null) {
                            val convertedAmount = amount * exchangeRate

                            runOnUiThread {

                                val message = "$amount $fromCurrency = $convertedAmount $toCurrency"
                                conversionResultTextView.text = message
                            }
                        } else {

                            runOnUiThread {
                                conversionResultTextView.text = "Conversion rate no disponible"
                            }
                        }
                    } else {

                        runOnUiThread {
                            conversionResultTextView.text = "Error"
                        }
                    }
                }
            })
        } else {
            conversionResultTextView.text = "Por favor ingrese algo valido"
        }
    }
}