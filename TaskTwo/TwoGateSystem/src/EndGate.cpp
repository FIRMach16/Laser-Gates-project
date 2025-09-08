#include <Arduino.h>
#include <WiFi.h>
#include <esp_wifi.h>
#include <esp_now.h>
#include <esp_system.h>
#include <esp_system.h>
#include <ESPAsyncWebServer.h>
#include <AsyncTCP.h>
#include "LittleFS.h"
#include <ArduinoJson.h>

// this code is for the End Gate
// it will receive a command from the start gate and will save the timestamp
// this gate will be also a wifi AP and a websocket 
#define WIFI_SSID "Laser_Gate_Connect"
#define WIFI_Password "Firas1235"
uint8_t StartGateMac[] = {0x10, 0x10, 0x10, 0x10, 0x10, 0x10};
uint8_t EndGateMac[] = {0x20, 0x20, 0x20, 0x20, 0x20, 0x20};

const byte LaserOnIndicator = 33;
const byte Laser2Crossed = 32;
const byte Laser1Crossed = 21;

// the variable below change in ISRs or Callback functions
volatile bool ShouldReset = false;
volatile bool StartGateLaserCrossed = false;
volatile unsigned long tmp,tmp2=0;
volatile unsigned long timeOfCrossingStartGateL1;
volatile bool StartGateL1 =false;
volatile unsigned long timeOfCrossingStartGateL2;
volatile bool StartGateL2 =false;
volatile unsigned long timeOfCrossingEndGateL1;
volatile bool EndGateL1 =false;
volatile unsigned long timeOfCrossingEndGateL2;
volatile bool EndGateL2 =false;
// L1 and L2 represent Laser 1 and Laser 2 for each gate this will hopefully allow accurate measurment of instanous speed
// the plan is to have a distance of 10 cm between the lasers of both gate which will allow speed calculaion in Km/h which is 3.6*m/s
bool pauseSignalL1 =false;
bool pauseSignalL2 =false;
String text;
JsonDocument doc;

// Create AsyncWebServer object on port 80
AsyncWebServer server(80);
// Create a WebSocket object
AsyncWebSocket ws("/ws");


void Publish(String text){
  
 ws.textAll(text);
  
}

void reset() {
 ShouldReset = true; 
}

typedef struct struct_message {  
    int gate;
} struct_message;
struct_message myData;
void OnDataRecv(const uint8_t * mac, const uint8_t *incomingData, int len) {
    tmp = micros();
    StartGateLaserCrossed = true;
    
    memcpy(&myData, incomingData, sizeof(myData));
    if (myData.gate==21){
      timeOfCrossingStartGateL1 =tmp;
      StartGateL1 = true;
    }
    else if(myData.gate == 33){
      // calculate and publish gate 1 instant speed
      timeOfCrossingStartGateL2 =tmp;
      StartGateL2 = true;
      // instant speed calculaion and formatting and publishing
      if(StartGateL1 == true)
      
      { 
        float speed1 =  (0.1/((timeOfCrossingStartGateL2 - timeOfCrossingStartGateL1)/1000000.0f))*3.6; // km/h Distance =10 cm =0.01 m 
        doc.clear();
        text = "";
        doc["Gate1Speed"] =String(speed1);
        serializeJson(doc,text) ;
        Publish(text);}

    }

    // for now we consider that the sprinter will cross a particular laser once and that the latency of the esp-now protocol is 0
    // TaskFour will tackle the problem of multicrossing and also other cases like:
    // 1. a sprinter crosses the start gate and stops before reaching the End gate
    // 2. multiple sprints in one session (without reset)
    // 3. disable or enable instant speed measurment
    
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
   
    //Check if the message is "getReadings"
    if (message == "getReadings") {
     doc.clear();
     doc["Time"] = "--.--";
     doc["Gate1Speed"] = "--.--";
     doc["Gate2Speed"] = "--.--";
     text = "";
     serializeJson(doc, text);
     Publish(text);
    StartGateLaserCrossed = false;
    StartGateL1 =false;
    StartGateL2 =false;
    EndGateL1 =false;
    EndGateL2 =false;
    pauseSignalL1 =false;
    pauseSignalL2 =false;
      
    }
    else if(message =="reset") {
    
      StartGateLaserCrossed = false;
      StartGateL1 =false;
      StartGateL2 =false;
      EndGateL1 =false;
      EndGateL2 =false;
      pauseSignalL1 =false;
      pauseSignalL2 =false;
      
    }
    else if(message =="pause") {
      // use case : we only need the instantanous speed of gate 1, may be deleted in later versions ....
      StartGateLaserCrossed = false;
      StartGateL1 =false;
      StartGateL2 =false;
    
      
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

void BroadcastAP()
{
  Serial.print("Setting AP (Access Point)…");
  
  WiFi.softAP(WIFI_SSID, WIFI_Password);

  IPAddress IP = WiFi.softAPIP();
  Serial.print("AP IP address: ");
  Serial.println(IP);  
}


void initWebSocket() {
  ws.onEvent(onEvent);
  server.addHandler(&ws);
}


void readMacAddress(){
  uint8_t baseMac[6];
  esp_err_t ret = esp_wifi_get_mac(WIFI_IF_AP, baseMac);
  if (ret == ESP_OK) {
    Serial.printf("%02x:%02x:%02x:%02x:%02x:%02x\n",
                  baseMac[0], baseMac[1], baseMac[2],
                  baseMac[3], baseMac[4], baseMac[5]);
  } else {
    Serial.println("Failed to read MAC address");
  }
}


void L1Crossed(){
// get timestamp and set flag => ISR (needs to be very quick)
timeOfCrossingEndGateL1 = micros();
EndGateL1 = true;
}

void L2Crossed(){
// get timestamp and set flag => ISR (needs to be very quick)
  timeOfCrossingEndGateL2 = micros();
  EndGateL2 = true;
}

void setup() {
 Serial.begin(115200);
 Serial.println();
 Serial.println("Hello");
 pinMode(LaserOnIndicator,OUTPUT);
 pinMode(Laser1Crossed,INPUT_PULLUP);
 pinMode(Laser2Crossed,INPUT_PULLUP);
 // attachInterrupt(digitalPinToInterrupt(ResetBoard), reset, RISING);
 attachInterrupt(digitalPinToInterrupt(Laser1Crossed),L1Crossed,FALLING);
 attachInterrupt(digitalPinToInterrupt(Laser2Crossed),L2Crossed,FALLING);


 WiFi.mode(WIFI_AP);


 //change mac address
 esp_err_t err = esp_wifi_set_mac(WIFI_IF_AP, &EndGateMac[0]);
  if (err == ESP_OK) {
    Serial.println("Success changing Mac Address");
  }
  else{
    Serial.printf("Failed to change Mac Address , error number %d \n",err);
    
  }
 
 
 BroadcastAP();
 initLittleFS();
 
 readMacAddress();
 if (esp_now_init() != ESP_OK) {
    Serial.println("Error initializing ESP-NOW");
    return;
  }
 esp_now_register_recv_cb(esp_now_recv_cb_t(OnDataRecv));

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

  digitalWrite(LaserOnIndicator,digitalRead(Laser1Crossed));// in the real use case this will help setup the laser
                                                            // at the correct angle and height ...   
  if((StartGateL1) &&(!EndGateL1) &&((micros() - tmp2)>100000)){
    //every 100ms after the start is crossed update the time on the dashbord
    tmp2 = micros();
    float timeInSeconds =  (tmp2- timeOfCrossingStartGateL1)/1000000.0F;  
    char formattedTime[10]; 
    dtostrf(timeInSeconds, 1, 2, formattedTime); // width=1, 4 decimal
    doc.clear();
    text = "";
    
    doc["Time"] =String(timeInSeconds);
    serializeJson(doc,text) ;
    Publish(text);

  }   
            
//   if(ShouldReset){
//     esp_restart();
// }
  if(EndGateL1){
    if ((StartGateL1==true) && (StartGateLaserCrossed ==true) &&(!pauseSignalL1))
    {
    
      float timeInSeconds =  (timeOfCrossingEndGateL1 - timeOfCrossingStartGateL1)/1000000.0F;  
      char formattedTime[10]; 
      dtostrf(timeInSeconds, 1, 4, formattedTime); // width=1, 4 decimal
      doc.clear();
      text = "";
      
      doc["Time"] =String(timeInSeconds);
      serializeJson(doc,text) ;
      Publish(text);
      pauseSignalL1 = true; // this ensures that the timediff get published only once after getting calculated
    }
  }
  if(EndGateL2){
    if ((EndGateL1==true)&&(!pauseSignalL2))
    {
    
      float speed2 =  (0.1/((timeOfCrossingEndGateL2 - timeOfCrossingEndGateL1)/1000000.0f))*3.6; // km/h Distance =10 cm =0.01 m 
      doc.clear();
      text = "";
      doc["Gate2Speed"] =String(speed2);
      serializeJson(doc,text) ;
      Publish(text);
      pauseSignalL2 = true;//this ensures that the speed of gate 2 get published only once after getting calculated
    }
    
  }


}
