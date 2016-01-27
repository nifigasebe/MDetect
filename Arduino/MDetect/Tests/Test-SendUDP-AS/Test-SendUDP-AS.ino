#include <SPI.h>
#include <Ethernet2.h>
#include <EthernetUdp2.h>

byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
IPAddress ip(10, 0, 0, 150);
unsigned int localPort = 9191;
IPAddress serverIp(10, 0, 0, 1);
unsigned int serverPort = 9191;

EthernetUDP udp;

char pulse[] = "11";

void setup() {
  pinMode(13, OUTPUT);    
  //Serial.begin(9600);      
  //while (!Serial) {    
  //}
  //Serial.println("Setup start");  
  Ethernet.begin(mac,ip);
  //Serial.println("Ethernet.begin");      
  //if (udp.begin(localPort) == 0){
    //Serial.println("ERROR: No sockets available to use.");      
  //}else{
      udp.begin(localPort);
    //Serial.println("UDP.begin");
    //Serial.println("Setup finish");   
   //}  
}

void loop() {
  blink();
  sendUDP(pulse);  
}

void sendUDP (char data[]){  
  //Serial.println("Start send");
  udp.beginPacket(serverIp,serverPort);
  //Serial.println("1");
  udp.write(pulse);
  //Serial.println("2");
  udp.endPacket();
  //Serial.println("Finish send");  
}

void blink(){
  for(int i=0;i<10;i++){
    digitalWrite(13, HIGH);   
    delay(100);              
    digitalWrite(13, LOW);    
    delay(100);  
  }       
}
