package com.example.speedray

import android.content.Context
import android.content.Intent
import android.os.Bundle

import android.util.Log


import androidx.appcompat.app.AppCompatActivity

import android.net.wifi.WifiNetworkSpecifier
import android.net.wifi.WifiManager

import android.net.ConnectivityManager
import android.net.MacAddress
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.speedray.data.Sprint
import com.example.speedray.data.SprintDatabase
import com.example.speedray.data.SprintRepository
import com.example.speedray.esp32comm.DataType
import com.example.speedray.esp32comm.Esp32WebSocket
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.Date
import java.util.Random


const val TAG = "SpeedRay"


class MainActivity : AppCompatActivity(){
    private lateinit var DummySprintRepositry: SprintRepository
    private lateinit var esp32WebSocket: Esp32WebSocket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.livedatalayout)

        val notConnectedDrawable =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_not_connected, null)
        val connectedDrawable =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_connected, null)
        val connectImageStatus = findViewById<ImageView>(R.id.ConnectionStatus)
        val connectToEspButton = findViewById<ImageButton>(R.id.ConnectToEspButton)
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        Log.d(
            TAG,
            "Can have 2 WIFIs : ${wifiManager.isStaConcurrencyForLocalOnlyConnectionsSupported}"
        )
        esp32WebSocket = Esp32WebSocket()

        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wns = WifiNetworkSpecifier.Builder()
            .setSsid("Laser_Gate_Connect")
            .setBssid(MacAddress.fromString("20:20:20:20:20:20"))
            .setWpa2Passphrase("Firas1235")
            .build()
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) // Because i the ESP32 is an AP
            .setNetworkSpecifier(wns)
            .build()
        val networkCallback = object : ConnectivityManager.NetworkCallback() {

            override fun onLost(network: Network) {
                super.onLost(network)
                connectImageStatus.setImageDrawable(notConnectedDrawable)
                connectToEspButton.isClickable = true
                Log.d(TAG, "Lost connection to ESP32_WIFI")
            }

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                connectImageStatus.setImageDrawable(connectedDrawable)
                connectToEspButton.isClickable = false
                Log.d(TAG, "Connected to ESP32_WIFI")
                connectivityManager.bindProcessToNetwork(network)
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("ws://192.168.4.1/ws")
                    .build()
                val ws = client.newWebSocket(request, esp32WebSocket)


            }

            override fun onUnavailable() {
                super.onUnavailable()
                connectImageStatus.setImageDrawable(notConnectedDrawable)
                Log.d(TAG, "Could not connect to ESP32_WIFI")
            }
        }

        connectToEspButton.setOnClickListener {
            connectivityManager.requestNetwork(networkRequest, networkCallback)
        }
        val timeText = findViewById<TextView>(R.id.TimeDifferenceResult)
        val speed1Text = findViewById<TextView>(R.id.EntrySpeedResult)
        val speed2Text = findViewById<TextView>(R.id.ExitSpeedResult)

        esp32WebSocket.esp32DataLiveData.observe(this){ esp32Data ->
            val dataType = esp32Data.dataType
            val dataValue = esp32Data.dataValue
            when (dataType) {
                DataType.ALL -> {
                    timeText.text = dataValue
                    speed1Text.text = dataValue
                    speed2Text.text = dataValue
                }
                DataType.TIME -> {
                    timeText.text = dataValue
                }
                DataType.SPEED1 -> {
                    speed1Text.text = dataValue
                }
                DataType.SPEED2 -> {
                    speed2Text.text = dataValue
                }
            }
        }


        //  data from ui
        val weightedCheckBox = findViewById<CheckBox>(R.id.WeightedAnswer)
        val weightAmount = findViewById<EditText>(R.id.WeightAmount)
        weightAmount.isEnabled = weightedCheckBox.isChecked
        weightedCheckBox.setOnCheckedChangeListener { _, state
            ->
            weightAmount.isEnabled = state
        }
        val distanceBetweenGates = findViewById<EditText>(R.id.DistanceEntryText)
        val distanceOfBuildUp = findViewById<EditText>(R.id.BuildUpDistanceEntryText)
        val random = Random()
        val addFloatingButton: View = findViewById(R.id.floatingActionButton)
        addFloatingButton.setOnClickListener { view ->
            connectImageStatus.setImageDrawable(notConnectedDrawable)
            val weight = if (weightedCheckBox.isChecked) weightAmount.text.toString().toInt() else 0
            val sprint = Sprint(
                1,
                (timeText.text).toString().toFloat(),
                (speed1Text.text).toString().toFloat(),
                (speed2Text.text).toString().toFloat(),
                Date(),
                distanceBetweenGates.text.toString().toInt(),
                distanceOfBuildUp.text.toString().toInt(),
                weightedCheckBox.isChecked,
                weight
            )
            insertDummyDataToDatabase(this, sprint = sprint)
        }


    }

    fun insertDummyDataToDatabase(
        owner: LifecycleOwner,
        context: Context = owner as Context,
        sprint: Sprint
    ) {
        val sprintDao = SprintDatabase.getDatabase(context).sprintDao()
        DummySprintRepositry = SprintRepository(sprintDao)
        // Example dummy data
        // the owner of this lifecycleScope is the activity where this was used (In this case it is the main activity)
        lifecycleScope.launch {
            DummySprintRepositry.clearAllSprints() // just for demo purposes
            DummySprintRepositry.addSprint(sprint)
        }
        DummySprintRepositry.readAllData.observe(owner) { sprints ->
            Log.d(TAG, "Db : $sprints")
        }
    }

    fun onClickOfSwitchToProgression(view: View?) {
        val intent = Intent(this, ProgressionActivity::class.java)
        startActivity(intent)
        //TODO : replace deprecated overridePendingTransition(int,int) by  overrideActivityTransition(int, int, int)
        // note : overrideActivityTransition needs API > 34 current min 31
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }


}