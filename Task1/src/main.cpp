#include <Arduino.h>
#include <WiFi.h>
#include <esp_system.h>
#include <ESPAsyncWebServer.h>
#include <AsyncTCP.h>
#include "LittleFS.h"
#include <ArduinoJson.h>

#define WIFI_SSID "Laser_Gate_Connect"
#define WIFI_Password "Firas1235"
#define WIFI_TIMEOUT_MS 20000


const byte LaserOnIndicator = 33;
const byte ResetBoard = 32;
const byte LaserCrossed = 21;
bool ShouldReset = false;
bool ShouldPublish = false;
unsigned long timeOfCrossing;
String timeJSON;


JsonDocument doc;


// Create AsyncWebServer object on port 80
AsyncWebServer server(80);

// Create a WebSocket object
AsyncWebSocket ws("/ws");


void SaveTimeOfCrossing(){
  if(!ShouldPublish){
    timeOfCrossing = micros();
    float timeInSeconds = timeOfCrossing / 1000000.0;

    char formattedTime[10]; // Enough to hold the float with 4 decimals
    dtostrf(timeInSeconds, 1, 4, formattedTime); // width=1, 4 decimal


    doc["TimeStamp"] =String(formattedTime);
    serializeJson(doc,timeJSON) ;
    ShouldPublish =true;

  }

}

void reset() {
 ShouldReset = true; 
}

void PublishTime(String timeOfCrossing){
  
 ws.textAll(timeOfCrossing);
  
}
void BroadcastAP()
{
  Serial.print("Setting AP (Access Point)…");
  
  WiFi.softAP(WIFI_SSID, WIFI_Password);

  IPAddress IP = WiFi.softAPIP();
  Serial.print("AP IP address: ");
  Serial.println(IP);
  
  
}


void initLittleFS() {
  if (!LittleFS.begin(true)) {
    Serial.println("An error has occurred while mounting LittleFS");
  }
  Serial.println("LittleFS mounted successfully");
}

void handleWebSocketMessage(void *arg, uint8_t *data, size_t len) {
  AwsFrameInfo *info = (AwsFrameInfo*)arg;
  if (info->final && info->index == 0 && info->len == len && info->opcode == WS_TEXT) {
    String message;
    for (size_t i = 0; i < len; i++) {
      message += (char) data[i];
    }
    if (message =="getReadings") {
     doc["TimeStamp"] = "0.0";
      timeJSON = "";
      serializeJson(doc, timeJSON);
      PublishTime(timeJSON);
    }
  }
}

void onEvent(AsyncWebSocket *server, AsyncWebSocketClient *client, AwsEventType type, void *arg, uint8_t *data, size_t len) {
  switch (type) {
    case WS_EVT_CONNECT:
      Serial.printf("WebSocket client #%u connected from %s\n", client->id(), client->remoteIP().toString().c_str());
      break;
    case WS_EVT_DISCONNECT:
      Serial.printf("WebSocket client #%u disconnected\n", client->id());
      break;
    case WS_EVT_DATA:
      handleWebSocketMessage(arg, data, len);
      break;
    case WS_EVT_PONG:
    case WS_EVT_ERROR:
      break;
  }
}

void initWebSocket() {
  ws.onEvent(onEvent);
  server.addHandler(&ws);
}


void setup() {
 Serial.begin(115200);
 Serial.println();
 Serial.println("Hello");
 pinMode(LaserOnIndicator,OUTPUT);
 pinMode(ResetBoard,INPUT_PULLDOWN); // reset button is at LOW
 pinMode(LaserCrossed,INPUT_PULLUP); // Laser is at High (to simulate the real case)

 attachInterrupt(digitalPinToInterrupt(ResetBoard), reset, RISING);
 attachInterrupt(digitalPinToInterrupt(LaserCrossed), SaveTimeOfCrossing, FALLING);
 
  BroadcastAP();
  initLittleFS();
  initWebSocket();

  // Web Server Root URL
  server.on("/", HTTP_GET, [](AsyncWebServerRequest *request) {
    request->send(LittleFS, "/index.html", "text/html");
  });

  server.serveStatic("/", LittleFS, "/");

  // Start server
  server.begin();



}

void loop() {
  // put your main code here, to run repeatedly:

  if(digitalRead(LaserCrossed)==HIGH){
    digitalWrite(LaserOnIndicator,HIGH); // in the real use case this will help setup the laser
                                         // at the correct angle and height ...   
    
  }
  else{
    digitalWrite(LaserOnIndicator,LOW); 
  }
  
      
  if(ShouldReset){
    esp_restart();
}
  if(ShouldPublish){
    Serial.println("ButtonClicked!");
    
   
    

    PublishTime(timeJSON);
    ShouldPublish = false;

  }


}
