package com.example.weatherapp

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private var binding : ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        fetchWeatherData("jaipur")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding?.searchView
        binding?.searchView?.setOnQueryTextListener(
            object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null) {
                        fetchWeatherData(query)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }

            }
        )
    }

    private fun fetchWeatherData(cityName : String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName, "0aa4e4dd425f80fdc83c97aa6c5bf7d3", "metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val wind = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                    val maxtemp = responseBody.main.temp_max
                    val mintemp = responseBody.main.temp_min

                    binding?.temperature?.text = "$temperature ℃"
                    binding?.textView?.text = condition
                    binding?.max?.text = "Max Temp: $maxtemp ℃"
                    binding?.min?.text = "Min Temp: $mintemp ℃"
                    binding?.humidity?.text = "$humidity"
                    binding?.wind?.text = "$wind"
                    binding?.sunrise?.text = "${time(sunRise)}"
                    binding?.sunset?.text = "${time(sunSet)}"
                    binding?.sea?.text = "$seaLevel hPa"
                    binding?.conditions?.text = condition
                    binding?.day?.text =  dayName(System.currentTimeMillis())
                    binding?.date?.text = date()
                    binding?.locationView ?.text = "$cityName"

                    changeBackground(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

            }

        })


    }

    private fun changeBackground(conditions: String) {
        when(conditions) {
            "Clear Sky", "Sunny", "Clear" ->{
                binding?.root?.setBackgroundResource(R.drawable.sunny_background)
                binding?.lottieAnimationView?.setAnimation(R.raw.sun)
            }
            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" ->{
                binding?.root?.setBackgroundResource(R.drawable.sunny_background)
                binding?.lottieAnimationView?.setAnimation(R.raw.sun)
            }
            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" ->{
                binding?.root?.setBackgroundResource(R.drawable.sunny_background)
                binding?.lottieAnimationView?.setAnimation(R.raw.sun)
            }
            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" ->{
                binding?.root?.setBackgroundResource(R.drawable.sunny_background)
                binding?.lottieAnimationView?.setAnimation(R.raw.sun)
            }
            else  -> {
                binding?.root?.setBackgroundResource(R.drawable.sunny_background)
                binding?.lottieAnimationView?.setAnimation(R.raw.sun)
            }
        }
        binding?.lottieAnimationView?.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }

    fun dayName(timestamp: Long) : String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
}