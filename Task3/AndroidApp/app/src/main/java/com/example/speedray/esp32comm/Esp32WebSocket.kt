package com.example.speedray.esp32comm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.speedray.TAG
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import kotlinx.serialization.json.*
import kotlinx.serialization.*


enum class DataType {
    ALL, TIME, SPEED1, SPEED2
}

@Serializable
class TimeJsonData(val Time: String)

@Serializable
data class Speed1JsonData(val Gate1Speed: String)

@Serializable
data class Speed2JsonData(val Gate2Speed: String)


class Esp32Data(var dataType: DataType, var dataValue: String)

class Esp32WebSocket() : WebSocketListener() {

    lateinit var timeJsonData: TimeJsonData
    lateinit var speed1JsonData: Speed1JsonData
    lateinit var speed2JsonData: Speed2JsonData
    val esp32DataLiveData = MutableLiveData<Esp32Data>()
    lateinit var esp32Data: Esp32Data

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Log.d(TAG, "Socket opened")
        webSocket.send("getReadings")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Log.d(TAG, text)
        // UI update

        when {
            "Time" in text && "Gate1Speed" in text && "Gate2Speed" in text -> {

                esp32Data = Esp32Data(DataType.ALL,"__.__")
                esp32DataLiveData.postValue(esp32Data)


            }

            "Time" in text -> {
                timeJsonData = Json.decodeFromString<TimeJsonData>(text)

                esp32Data = Esp32Data(DataType.TIME,timeJsonData.Time)
                esp32DataLiveData.postValue(esp32Data)

            }

            "Gate1Speed" in text -> {
                speed1JsonData = Json.decodeFromString<Speed1JsonData>(text)

                esp32Data = Esp32Data(DataType.SPEED1,speed1JsonData.Gate1Speed)
                esp32DataLiveData.postValue(esp32Data)

            }

            "Gate2Speed" in text -> {
                speed2JsonData = Json.decodeFromString<Speed2JsonData>(text)
                esp32Data = Esp32Data(DataType.SPEED2,speed2JsonData.Gate2Speed)
                esp32DataLiveData.postValue(esp32Data)
            }

            else -> {
                Log.d(TAG, "JSON error Format non accounted for")


            }

        }

    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.d(TAG, "Socket failed to open : ${t.message}")

    }
}