This Folder and also [Task1](../Task1/) is for protoryping diffrent sub systems of the final product:

1. The first one is [TwoGateSystems](TwoGateSystem/) which uses buttons (will be repalced by photodiodes later) to create a timing 
system started by the Start Gate and terminated by the End Gate.

Principle : The athlete crosses the laser of the StartGate with his torso deactivating the photodiode (FALLING edge) , which commands the StartGate to send a signal 
to the EndGate which in turn starts the timer internally and waits till the athletes crosses it's laser to deactivate it's photodiode to stop the timer, the time is
then sent via websocket to be displayed (app or pc(using a browser to render the small server of the esp32)).

*will add a raw sketch to illustrate this*

here is a [demo](https://github.com/FIRMach16/Laser-Gates-project/issues/4) of it with the app. (I worked on one that uses html/css/js first but did not film it)  

2. The second one is [BluetoothAudio](BluetoothAudio/) which uses the [ESP_A2DP](github.com/pschatzmann/ESP32-A2DP) Library to send audio to a Bluetooth sink (speaker)
too play, like in a real race, the starting instructions to athletes ("On your marks!", "Set!", "Gun sound!").
here is the [demo](https://github.com/FIRMach16/Laser-Gates-project/issues/7) for it.

3. upcomming(**After Task4**) : measure the two way latency of the esp-now protocol across variable distances (500 tries for each distance)
4. upcomming(**After Task4**) : command a built-in speaker to do the same as [BluetoothAudio](BluetoothAudio/)
5. upcomming(**After Task4**) : upload a firmware with OTA to both gates 
