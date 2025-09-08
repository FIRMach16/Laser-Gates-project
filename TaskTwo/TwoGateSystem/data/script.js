var gateway = `ws://${window.location.hostname}/ws`;
var websocket;
// Init web socket when the page loads
window.addEventListener('load', onload);

function onload(event) {
    initWebSocket();
}

function getReadings(){
    websocket.send("getReadings");
}
function ResetLaserGates(){
    
    
    document.getElementById('Time').innerHTML ="--.--"
    document.getElementById('Gate1Speed').innerHTML ="--.--"
    document.getElementById('Gate2Speed').innerHTML ="--.--"
    websocket.send("reset");

}
//use case measure only the speed of entry instead of resetting everything 
function PauseLaserGates(){
    
    websocket.send("pause");
    
}

function initWebSocket() {
    console.log('Trying to open a WebSocket connection…');
    websocket = new WebSocket(gateway);
    websocket.onopen = onOpen;
    websocket.onclose = onClose;
    websocket.onmessage = onMessage;
}

// When websocket is established, call the getReadings() function
function onOpen(event) {
    console.log('Connection opened');
    getReadings();
}

function onClose(event) {
    console.log('Connection closed');
    setTimeout(initWebSocket, 2000);
}

// Function that receives the message from the ESP32 with the readings
function onMessage(event) {
    console.log(event.data);
    var myObj = JSON.parse(event.data);
    var keys = Object.keys(myObj);

    for (var i = 0; i < keys.length; i++){
        var key = keys[i];
        document.getElementById(key).innerHTML = myObj[key];
    }
}

window.addEventListener('DOMContentLoaded', () => {
    document.getElementById('reset')
        .addEventListener('click', ResetLaserGates);
});
window.addEventListener('DOMContentLoaded', () => {
    document.getElementById('pause')
        .addEventListener('click', PauseLaserGates);
});