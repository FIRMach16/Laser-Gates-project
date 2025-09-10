#include <Arduino.h>
#include <WiFi.h>
#include <esp_wifi.h>
#include <esp_now.h>


// this code is for the start gate
// it will send a command to the end gate when the button is clicked (laser corssed)
// for this i Will be using ESP-NOW protocol
// One way communication is sufficent 

#define WIFI_SSID "Laser_Gate_Connect"
#define WIFI_Password "Firas1235"
#define WIFI_TIMEOUT_MS 20000
uint8_t StartGateMac[] = {0x10, 0x10, 0x10, 0x10, 0x10, 0x10};
uint8_t EndGateMac[] = {0x20, 0x20, 0x20, 0x20, 0x20, 0x20};
const byte ConnectionIndicator = 19;

const byte Laser2Crossed = 33;
const byte Laser1Crossed = 21;

bool L1 = false;
bool L2 = false;
bool L1CorssedOnce = false;

esp_now_peer_info_t peerInfo;
esp_err_t result ;

typedef struct struct_message {  
    int gate;
} struct_message;

struct_message myData;
// void reset() {
//  ShouldReset = true; 
// }

void readMacAddress(){
  uint8_t baseMac[6];
  esp_err_t ret = esp_wifi_get_mac(WIFI_IF_STA, baseMac);
  if (ret == ESP_OK) {
    Serial.printf("%02x:%02x:%02x:%02x:%02x:%02x\n",
                  baseMac[0], baseMac[1], baseMac[2],
                  baseMac[3], baseMac[4], baseMac[5]);
  } else {
    Serial.println("Failed to read MAC address");
  }
}
void ConnectToEndGate(){
    
    
    WiFi.begin(WIFI_SSID, WIFI_Password);
    Serial.println("Connecting to WiFi...");
    while (WiFi.status() != WL_CONNECTED) {
        Serial.print(".");
        delay(1000);
    }
    Serial.println("Connected");

}


void L1Crossed(){

 L1 = true; 
}

void L2Crossed(){
 L2 = true;
 
}

void setup() {
 Serial.begin(115200);
 Serial.println();
 Serial.println("Hello");

 pinMode(ConnectionIndicator,OUTPUT);
 pinMode(Laser1Crossed,INPUT_PULLUP); // reset button is at LOW
 pinMode(Laser2Crossed,INPUT_PULLUP); // Laser is at High (to simulate the real case)

// attachInterrupt(digitalPinToInterrupt(ResetBoard), reset, RISING);
 attachInterrupt(digitalPinToInterrupt(Laser1Crossed),L1Crossed,FALLING);
 attachInterrupt(digitalPinToInterrupt(Laser2Crossed),L2Crossed,FALLING);
 WiFi.mode(WIFI_STA);


 //change mac address
 esp_err_t err = esp_wifi_set_mac(WIFI_IF_STA, &StartGateMac[0]);
  if (err == ESP_OK) {
    Serial.println("Success changing Mac Address");
  }
  else{
    Serial.printf("Failed to change Mac Address , error number %d \n",err);
    
  }
  ConnectToEndGate();
 readMacAddress();
  if (esp_now_init() != ESP_OK) {
    Serial.println("Error initializing ESP-NOW");
    return;
  }
  memcpy(peerInfo.peer_addr, EndGateMac, 6);
  peerInfo.channel = 0;  
  peerInfo.encrypt = false;

  if (esp_now_add_peer(&peerInfo) != ESP_OK){
    Serial.println("Failed to add peer");
    return;
  }
 
}

void loop() {
  // put your main code here, to run repeatedly:


  // digitalWrite(LaserOnIndicator,digitalRead(Laser1Crossed)); // in the real use case this will help setup the laser
  //                                                               // at the correct angle and height ...   
    
  if(WiFi.status() != WL_CONNECTED){
    digitalWrite(ConnectionIndicator,LOW);
  }
  else{
    digitalWrite(ConnectionIndicator,HIGH);
  }
 if ((L1)&&(!L1CorssedOnce))  {
  myData.gate = Laser1Crossed;
  
  // Send message via ESP-NOW
  result = esp_now_send(EndGateMac, (uint8_t *) &myData, sizeof(myData)); 
  L1CorssedOnce= true;
  
 }
 if((L2) &&(L1)){
   myData.gate = Laser2Crossed;
    
    // Send message via ESP-NOW
    result = esp_now_send(EndGateMac, (uint8_t *) &myData, sizeof(myData));  
    
  L1 = false;
  L2 = false;
  L1CorssedOnce = false;
 }
  


}
