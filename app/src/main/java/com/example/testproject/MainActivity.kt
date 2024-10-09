package com.example.testproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import com.example.testproject.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.locks.Condition

//63678de315c82bbf8622d7e742956a6c
class MainActivity : AppCompatActivity() {
    //need to understand
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Cairo")
        searchCity()

    }

    private fun searchCity() {
     val searchView = binding.searchView
        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }//end of SearchCity

    private fun fetchWeatherData(cityName : String) {
val retrofit =Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl("https://api.openweathermap.org/data/2.5/")
    .build()
    .create(APIinterface::class.java)

        val response = retrofit.getWeatherData(cityName,
            "63678de315c82bbf8622d7e742956a6c","metric")
        response.enqueue( object : Callback<weatherapp>{
            override fun onResponse(call: Call<weatherapp>, response: Response<weatherapp>) {
                val responseBody =response.body()
                if (response.isSuccessful && responseBody != null){
                    val temperature =responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed =responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unkown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min

                    binding.temp.text = "$temperature"
                    binding.weather.text=condition
                    binding.maxTemp.text = "Max Temp: $maxTemp °C"
                    binding.minTemp.text = "Min Temp: $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed M/s"
                    binding.sunRise.text = "${time(sunRise)}"
                    binding.sunset.text = "${time(sunSet)}"
                    binding.sea.text = "$seaLevel hPa"
                    binding.condition.text=condition
                    binding.day.text=date()
                    binding.date.text=dayName(System.currentTimeMillis())
                    binding.cityName.text="$cityName"
                    changeImagesBasedOnWeathercondition(condition)
                } //end of if
            }//end of onResponse


            override fun onFailure(call: Call<weatherapp>, t: Throwable) {
                Log.e("TAG", "onFailure: ${t.localizedMessage}")
            } // end of onFailure


        } )//end of enqueue

    }//end offetchWeatherData

    private fun changeImagesBasedOnWeathercondition(conditions :String) {
            when(conditions){
                "Partly Clouds","Clouds","Overcast","Mist","Foggy" -> {
                    binding.root.setBackgroundResource(R.drawable.colud_background)
                    binding.lottieAnimationView.setAnimation(R.raw.cloud)
                }
                "Clear Sky","Sunny","Clear" -> {
                    binding.root.setBackgroundResource(R.drawable.sunny_background)
                    binding.lottieAnimationView.setAnimation(R.raw.sun)
                }
                "Rain","Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" -> {
                    binding.root.setBackgroundResource(R.drawable.rain_background)
                    binding.lottieAnimationView.setAnimation(R.raw.rain)
                }
                "Light Snow","Moderate Snow","Heavy Snow","Blizzard" -> {
                    binding.root.setBackgroundResource(R.drawable.snow_background)
                    binding.lottieAnimationView.setAnimation(R.raw.snow)
                }
                else -> {
                    binding.root.setBackgroundResource(R.drawable.sunny_background)
                    binding.lottieAnimationView.setAnimation(R.raw.sun)
                }

            }
        binding.lottieAnimationView.playAnimation()
    }


    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm ", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    private fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}






