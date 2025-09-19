package com.example.speedray

import android.content.Context
import android.content.Intent
import android.os.Bundle

import android.util.Log


import androidx.appcompat.app.AppCompatActivity

import android.net.wifi.WifiNetworkSpecifier
import android.net.wifi.WifiManager

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.speedray.data.Sprint
import com.example.speedray.data.SprintDatabase
import com.example.speedray.data.SprintRepository
import com.example.speedray.data.SprintViewModel
import kotlinx.coroutines.launch

import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.Calendar
import java.util.Date


const val TAG = "SpeedRay"




class MainActivity : AppCompatActivity() {
    private lateinit var DummySprintRepositry: SprintRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.livedatalayout)

        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        Log.d(TAG,"Can have 2 WIFIs : ${wifiManager.isStaConcurrencyForLocalOnlyConnectionsSupported}")
        // My test phone don't have the option for concurrent connections

        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wns = WifiNetworkSpecifier.Builder()
            .setSsid("Laser_Gate_Connect")
            .setWpa2Passphrase("Firas1235")
            .build()
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) // Because i the ESP32 is an AP
            .setNetworkSpecifier(wns)
            .build()
        val networkCallback = object : ConnectivityManager.NetworkCallback(){
            // WILL ADD INDICATOR IN UI IN SPRINT2
            override fun onLost(network: Network) {
                super.onLost(network)
                Log.d(TAG,"Lost connection to ESP32_WIFI")

            }

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Log.d(TAG,"Connected to ESP32_WIFI")
                connectivityManager.bindProcessToNetwork(network)

                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("ws://192.168.4.1/ws")
                    .build()
                val  ws = client.newWebSocket(request, Esp32WebSocket())

            }

            override fun onUnavailable() {
                super.onUnavailable()
                Log.d(TAG,"Could not connect to ESP32_WIFI")
            }
        }
        Log.d(TAG,"Hello")
        connectivityManager.requestNetwork(networkRequest,networkCallback)

        insertDummyDataToDatabase(this)




    }
    fun insertDummyDataToDatabase(owner: LifecycleOwner, context: Context = owner as Context){
        val sprintDao = SprintDatabase.getDatabase(context).sprintDao()
        DummySprintRepositry = SprintRepository(sprintDao)

// Example dummy data

            val Sprint4= Sprint(
                id = 4,
                time = 3.25f,
                entrySpeed = 7.5f,
                exitSpeed = 9.0f,
                dateOfSprint = Calendar.getInstance().apply { set(2025, 9, 18) }.time,
                distanceBetweenGates = 25,
                distanceOfBuildUp = 12,
                weighted = false,
                weight = 0
            )
        lifecycleScope.launch {

            DummySprintRepositry.addSprint(Sprint4)
        }
        DummySprintRepositry.readAllData.observe(owner){
            sprints -> Log.d(TAG,"Db : $sprints")
        }





    }
    fun OnClickOfSwitchToProgression(view: View?){
        val intent = Intent(this,ProgressionActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}