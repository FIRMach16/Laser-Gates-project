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
import android.widget.CheckBox
import android.widget.EditText
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
import java.util.Random


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

        //  data from ui
        val weightedCheckBox= findViewById<CheckBox>(R.id.WeightedAnswer)
        val weightAmount= findViewById<EditText>(R.id.WeightAmount)

        weightAmount.isEnabled = weightedCheckBox.isChecked

        weightedCheckBox.setOnCheckedChangeListener { _,state
        -> weightAmount.isEnabled = state
        }
        val distanceBetweenGates = findViewById<EditText>(R.id.DistanceEntryText)
        val distanceOfBuildUp = findViewById<EditText>(R.id.BuildUpDistanceEntryText)

        val random = Random()

        val addFloatingButton: View = findViewById(R.id.floatingActionButton)
        addFloatingButton.setOnClickListener { view ->

            val weight = if(weightedCheckBox.isChecked) weightAmount.text.toString().toInt() else 0
            val sprint = Sprint(1,random.nextFloat()*3,random.nextFloat()*4,random.nextFloat()*10,Date(),
                distanceBetweenGates.text.toString().toInt(),distanceOfBuildUp.text.toString().toInt(),weightedCheckBox.isChecked,
                weight)
            insertDummyDataToDatabase(this, sprint = sprint)
        }



    }
    fun insertDummyDataToDatabase(owner: LifecycleOwner, context: Context = owner as Context,sprint: Sprint){
        val sprintDao = SprintDatabase.getDatabase(context).sprintDao()
        DummySprintRepositry = SprintRepository(sprintDao)


// Example dummy data


        lifecycleScope.launch {
            DummySprintRepositry.clearAllSprints() // just for demo purposes
            DummySprintRepositry.addSprint(sprint)
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