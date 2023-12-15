package com.example.weather_mayank

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.weather_mayank.api.RetrofitInstance
import com.example.weather_mayank.api.WeatherAppInterface
import com.example.weather_mayank.data.WeatherReport
import com.example.weather_mayank.databinding.ActivityMainBinding
import com.example.weather_mayank.models.Weather
import com.example.weather_mayank.repositories.WeatherRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.Locale


class MainActivity : AppCompatActivity() {


    private val TAG: String = "MY_LOCATION_APP"

    lateinit var binding: ActivityMainBinding


    // Device location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var weatherreport: WeatherReport
    private lateinit var weatherRepository: WeatherRepository


    // permissions array
    private val APP_PERMISSIONS_LIST = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )


    // showing the permissions dialog box & its result
    private val multiplePermissionsResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {


            resultsList ->
        Log.d(TAG, resultsList.toString())


        var allPermissionsGrantedTracker = true


        for (item in resultsList.entries) {
            if (item.key in APP_PERMISSIONS_LIST && item.value == false) {
                allPermissionsGrantedTracker = false
            }
        }


        if (allPermissionsGrantedTracker == true) {
            var snackbar =
                Snackbar.make(binding.root, "All permissions granted", Snackbar.LENGTH_LONG)
            snackbar.show()
//            getDeviceLocation()


        } else {
            var snackbar =
                Snackbar.make(binding.root, "Some permissions NOT granted", Snackbar.LENGTH_LONG)
            snackbar.show()
            handlePermissionDenied()
        }


    }


    private fun getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            multiplePermissionsResultLauncher.launch(APP_PERMISSIONS_LIST)
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location === null) {
                    Log.d(TAG, "Location is null")
                    return@addOnSuccessListener
                }
                // Output the location
                val message =
                    "The device is located at: ${location.latitude}, ${location.longitude}"
//                binding.test.text = "${location.latitude},${location.longitude}"
                var api: WeatherAppInterface = RetrofitInstance.retrofitService
                lifecycleScope.launch {
                    val Data: Weather =
                        api.getWeatherByCoordinates(location.latitude, location.longitude)
                    Log.d("WeatherData", Data.currentConditions.humidity.toString())
                }
                Log.d(TAG, message)
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()


            }
    }


    private fun handlePermissionDenied() {
        // output the rationale
        // disable the get device location button
//        binding.test.setText("Sorry, you need to give us permissions before we can get your location. Check your settings menu and update your location permissions for this app.")
        // disable the button
        multiplePermissionsResultLauncher.launch(APP_PERMISSIONS_LIST)
//        binding.btnGetDeviceLocation.isEnabled = false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(this.binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        binding.errorCity.isVisible = false
        binding.errorCoordinates.isVisible = false


        // instantiate the fusedLocationProvider
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        // click handlers
        binding.searchCity.setOnClickListener {
            binding.errorCoordinates.isVisible = false

            val geocoder: Geocoder = Geocoder(applicationContext, Locale.getDefault())

            val city = binding.inputCity.text.toString()


            Log.d(TAG, "Getting coordinates for ${city}")

            if (city.isNullOrEmpty()) {
                binding.errorCity.isVisible = true
                return@setOnClickListener
            } else {
                binding.errorCity.isVisible = false
                try {
                    val searchResults: MutableList<Address>? = geocoder.getFromLocationName(city, 1)
                    if (searchResults == null) {
                        Log.e(TAG, "searchResults variable is null")
                        return@setOnClickListener
                    }


                    // if not null, then we were able to get some results (and it is possible for the results to be empty)
                    if (searchResults.size == 0) {
//                    binding.tvResults.setText("Search results are empty.")
                    } else {
                        lifecycleScope.launch {
                            var api: WeatherAppInterface = RetrofitInstance.retrofitService
                            val foundLocation: Address = searchResults.get(0)
                            val Data = api.getWeatherByCoordinates(
                                foundLocation.latitude,
                                foundLocation.longitude
                            )
                            weatherreport = WeatherReport(Data.days[0].datetime.toString(),Data.currentConditions.datetime.toString(),city,Data.currentConditions.temp,Data.currentConditions.conditions,Data.description)
                            val output:String = "City: ${city}\n" +
                                    "Date: ${Data.days[0].datetime}, Time: ${Data.currentConditions.datetime}\n" +
                                    "Temperature: ${Data.currentConditions.temp} C\n" +
                                    "Conditions: ${Data.currentConditions.conditions}\n" +
                                    "Description: ${Data.description}"

                            binding.resultText.text = output
                            binding.resultBlock.isVisible = true

                        }
                    }
                } catch (ex: Exception) {
                    Log.e(TAG, "Error encountered while getting coordinate location.")
                    Log.e(TAG, ex.toString())
                }
            }
        }


        binding.searchCoordinates.setOnClickListener {
            binding.errorCity.isVisible = false


            val latitude = binding.inputLatitude.text.toString()
            val longitude = binding.inputLongitude.text.toString()
            var city:String = ""
            val geocoder: Geocoder = Geocoder(applicationContext, Locale.getDefault())

            if (latitude.isNullOrEmpty() || longitude.isNullOrEmpty()) {
                binding.errorCoordinates.isVisible = true
                return@setOnClickListener
            } else {
                binding.errorCoordinates.isVisible = false

                try {
                    val searchResults: MutableList<Address>? =
                        geocoder.getFromLocation(latitude.toDouble(),longitude.toDouble(), 1)
                    if (searchResults == null) {
                        Log.d(TAG, "ERROR: When retrieving results")
                    }
                    else if (searchResults.size == 0){
                        Log.d(TAG, "ERROR: No result found")
                    }
                    else{
                        city = searchResults[0].locality
                    }
                }catch (exception:Exception) {
                    Log.d(TAG, "Exception occurred while getting matching address")
                    Log.d(TAG, exception.toString())
                }


                lifecycleScope.launch {
                    var api: WeatherAppInterface = RetrofitInstance.retrofitService
                    val Data =
                        api.getWeatherByCoordinates(latitude.toDouble(), longitude.toDouble())
                    weatherreport = WeatherReport(Data.days[0].datetime.toString(),Data.currentConditions.datetime.toString(),city,Data.currentConditions.temp,Data.currentConditions.conditions,Data.description)
                    val output:String = "City: ${city}\n" +
                            "Date: ${Data.days[0].datetime}, Time: ${Data.currentConditions.datetime}\n" +
                            "Temperature: ${Data.currentConditions.temp} C\n" +
                            "Conditions: ${Data.currentConditions.conditions}\n" +
                            "Description: ${Data.description}"

                    binding.resultText.text = output
                    binding.resultBlock.isVisible = true
                }
            }

        }



        multiplePermissionsResultLauncher.launch(APP_PERMISSIONS_LIST)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                // Got last known location. In some rare situations this can be null.
                var city:String = ""
                val geocoder: Geocoder = Geocoder(applicationContext, Locale.getDefault())
                if (location === null) {
                    Log.d(TAG, "Location is null")
                    return@addOnSuccessListener
                }
                // Output the location
                val message = "The device is located at: ${location.latitude}, ${location.longitude}"
                try {
                    val searchResults: MutableList<Address>? =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (searchResults == null) {
                        Log.d(TAG, "ERROR: When retrieving results")
                    }
                    else if (searchResults.size == 0){
                        Log.d(TAG, "ERROR: No result found")
                    }
                    else{
                        city = searchResults[0].locality
                    }
                }catch (exception:Exception) {
                    Log.d(TAG, "Exception occurred while getting matching address")
                    Log.d(TAG, exception.toString())
                }

//                binding.test.text = "${location.latitude},${location.longitude}"
                var api:WeatherAppInterface = RetrofitInstance.retrofitService
                lifecycleScope.launch {
                    val Data: Weather = api.getWeatherByCoordinates(location.latitude,location.longitude)
                    Log.d("WeatherData", Data.currentConditions.humidity.toString())

                    weatherreport = WeatherReport(Data.days[0].datetime.toString(),Data.currentConditions.datetime.toString(),city,Data.currentConditions.temp,Data.currentConditions.conditions,Data.description)
                    val output:String = "City: ${city}(Obtained from device location)\n" +
                            "Date: ${Data.days[0].datetime}, Time: ${Data.currentConditions.datetime}\n" +
                            "Temperature: ${Data.currentConditions.temp} C\n" +
                            "Conditions: ${Data.currentConditions.conditions}\n" +
                            "Description: ${Data.description}"

                    binding.resultText.text = output
                    binding.resultBlock.isVisible = true
                }
                Log.d(TAG, message)


            }

        this.weatherRepository = WeatherRepository(application)

        binding.saveBtn.setOnClickListener {
            binding.errorCity.isVisible = false
            binding.errorCoordinates.isVisible = false
            if(!this::weatherreport.isInitialized){
                val snackbar = Snackbar.make(binding.root,"No Report found to save",Snackbar.LENGTH_LONG).show()
            }else{

                lifecycleScope.launch{
                    weatherRepository.saveReport(weatherreport)
                    val snackbar1 = Snackbar.make(binding.root,"Report Saved",Snackbar.LENGTH_LONG).show()

                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.homePage -> {
                return true
            }

            R.id.savedReports -> {
                val savedReportsPageIntent = Intent(this, SavedRecordsActivity::class.java)
                startActivity(savedReportsPageIntent)
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
