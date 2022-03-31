package com.example.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.graphics.createBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.math.roundToInt

class MainViewModel: ViewModel() {
    // city name, pressure, temp, sunrise, sunset, czy uproszczony, date, time, icon, description
    var name: String = ""
    var pressure = MutableLiveData("")
    var temperature = MutableLiveData("")
    var sunrise = MutableLiveData("")
    var sunset = MutableLiveData("")
    var simpleView = MutableLiveData(false)
    var date = MutableLiveData("")
    var time = MutableLiveData("")
    var icon = MutableLiveData(createBitmap(100, 100))
    var description = MutableLiveData("")

    var loaded = MutableLiveData(false)
    var failed = MutableLiveData(false)

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    fun getWeather(context: Context) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)
        val url =
            "https://api.openweathermap.org/data/2.5/weather?q=${name}&units=metric&lang=pl&appid=528c0e9c2d63bb00540f336b0e748d00"

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                val jsonResponse = JSONObject(response)
                // for description and icon
                val jsonArray = jsonResponse.getJSONArray("weather")
                val jsonObjectWeather = jsonArray.getJSONObject(0)
                this.description.value = jsonObjectWeather.getString("description")
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

                val iconId = jsonObjectWeather.getString("icon")
                val iconUrl = "https://openweathermap.org/img/wn/${iconId}@4x.png"
                val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())
                executor.execute {
                    try {
                        val stream = URL(iconUrl).openStream()
                        val image = BitmapFactory.decodeStream(stream)
                        handler.post {
                            this.icon.value = image
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                // for temp and pressure
                val jsonObjectMain = jsonResponse.getJSONObject("main")
                this.temperature.value = jsonObjectMain.getDouble("temp").roundToInt().toString()
                this.pressure.value = jsonObjectMain.getInt("pressure").toString()
                // for datetime and city name
                val cityName = jsonResponse.getString("name")
                val timezone = jsonResponse.getLong("timezone")
                val timezoneOffset = (TimeZone.getDefault().getOffset(Date().time)/1000.0).toLong()
                val dt = jsonResponse.getLong("dt") + timezone - 3600
                val simpleDate = SimpleDateFormat("dd/MM/yyyy")
                val simpleTime = SimpleDateFormat("HH:mm")
                val currentDate = simpleDate.format(Date(dt * 1000))
                val currentTime = simpleTime.format(Date(dt * 1000))
                this.date.value = currentDate
                this.time.value = currentTime
                this.name = cityName
                // for sunset and sunrise
                val jsonObjectSys = jsonResponse.getJSONObject("sys")
                //sunrise
                val sunrise = jsonObjectSys.getLong("sunrise")
                this.sunrise.value = simpleTime.format(Date((sunrise + timezone - timezoneOffset) * 1000))
                //sunset
                val sunset = jsonObjectSys.getLong("sunset")
                this.sunset.value = simpleTime.format(Date((sunset + timezone - timezoneOffset) * 1000))
                this.loaded.value = true
            },
            {
                Toast.makeText(context, R.string.api_error, Toast.LENGTH_SHORT).show()
                this.failed.value = true
            }
        )
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }
}