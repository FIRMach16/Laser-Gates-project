#include <Arduino.h>
#include <stdint.h> 
#include "BluetoothA2DPSource.h"
#include <LittleFS.h>
#include "FS.h"
#include <cstdlib>  

BluetoothA2DPSource a2dp_source;
bool in_delay = false;
bool begin = false;
bool set =false;
bool go = false;
long first_delay = 3000;
long second_delay_min_value = 1000;
long second_delay_max_value = 2000;
long second_delay=second_delay_min_value;
long start_time=0;


int32_t get_sound_data(uint8_t *data, int32_t byteCount) {
	static File current_file;
	if(!begin){
		// Waiting until the button is pressed otherwise silence
		memset(data,0,byteCount);
		return byteCount;
	}
	else if(in_delay){
		// for the moment i will consider that fully filling the data buffer with 0 (silence)
		// will a introduce a negligeable delay relative to the delays I want 	
		// we will have two delays 
		// 1. between "On your marks" and "Set" : 6 seconds
		// 2. between "Set" and The gun sound : Random value between 1.5 to 3 seconds
		if(set && !go){
			// first case 
			if((millis()-start_time)<=first_delay){
				// silence
				memset(data,0,byteCount);
			}
			else{
				memset(data,0,byteCount);
				in_delay = false;
			}
		}
		if(set && go) {
			if((millis()-start_time)<=second_delay){
				// silence
				memset(data,0,byteCount);
			}
			else{
				memset(data,0,byteCount);
				in_delay = false;
			}
		}
		return byteCount;
	}
	else if(begin && !set && !go){
		// play "On Your Marks"
		if(!current_file)
		{
			current_file = LittleFS.open("/OnYourMarks.wav","r");
		}
		size_t bytesRead = current_file.read(data,byteCount); //no buffer overflow risk
								//as this method limits the 
								//number of bytes read to len
								//it has also an internal pointer 
								//to the current read position
		if(bytesRead <byteCount){
			// fill the rest with silence
			memset(data+bytesRead,0,byteCount-bytesRead);
			in_delay = true;
			start_time = millis();
			set = true;
			current_file.close();
		}

		return byteCount;
	}
	else if(begin && set && !go){
		// play "Set"
		if(!current_file)
		{
			current_file = LittleFS.open("/Set.wav","r");
		}
		size_t bytesRead = current_file.read(data,byteCount); 

		if(bytesRead <byteCount){
			// fill the rest with silence
			memset(data+bytesRead,0,byteCount-bytesRead);
			go = true;
			in_delay = true;
			start_time=millis();
			current_file.close();
		}
		return byteCount;
	}
	else if (begin && set && go){
		// play the gun sound
		if(!current_file)
		{
			current_file = LittleFS.open("/Gun.wav","r");
		}
		size_t bytesRead = current_file.read(data,byteCount); 
		if(bytesRead <byteCount){
			// fill the rest with silence
			memset(data+bytesRead,0,byteCount-bytesRead);
			begin = false;
			set = false;
			go =false;
			current_file.close();
			}
		return byteCount;
	}
}
void replay(){
	if(begin){
		begin =false;
		set = false;
		in_delay= false;
		go =false;
	}
	else{
		begin =true;
		second_delay=  second_delay_min_value +(rand() %(second_delay_max_value - second_delay_min_value));
	}
}
void setup() {
	Serial.begin(115200);
	Serial.println("Hello");
	if(!LittleFS.begin()){
		Serial.println("Failed to mount FS");
		//failed
		return;
	}
	//LittleFs is initialized
	Serial.println("little fs mounted!");	
	pinMode(21,INPUT_PULLDOWN);
	attachInterrupt(digitalPinToInterrupt(21),replay,RISING);
	a2dp_source.set_data_callback(get_sound_data);
	a2dp_source.set_auto_reconnect(true);
  	a2dp_source.start("RaceStarter");  
	Serial.println("Broadcasting ...");
}

void loop(){
}
