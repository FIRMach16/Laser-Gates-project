package com.example.speedray

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import kotlinx.serialization.json.*
import kotlinx.serialization.*




@Serializable
data class TimeOnly( val Time: String)
@Serializable
data class Gate1SpeedOnly(val Gate1Speed: String)
@Serializable
data class Gate2SpeedOnly(val Gate2Speed: String)
@Serializable
data class FullData(val Time: String,val Gate1Speed: String,val Gate2Speed: String)


class Esp32WebSocket : WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Log.d(TAG,"Socket opened")
        webSocket.send("getReadings")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Log.d(TAG,text)
        // UI update

        when{
            "Time" in text && "Gate1Speed" in text && "Gate2Speed" in text ->{
               val m = Json.decodeFromString<FullData>(text)


            }
            "Time" in text -> {
                val m = Json.decodeFromString<TimeOnly>(text)
            }
            "Gate1Speed" in text -> {
               val m = Json.decodeFromString<Gate1SpeedOnly>(text)
            }
            "Gate2Speed" in text -> {
               val m = Json.decodeFromString<Gate2SpeedOnly>(text)
            }

            else-> Log.d(TAG,"JSON error Format non accounted for")
        }

    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.d(TAG,"Socket failed to open : ${t.message}")

    }
}