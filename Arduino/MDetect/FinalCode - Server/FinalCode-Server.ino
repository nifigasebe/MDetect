#include <SPI.h>
#include <RF24.h>                                              // http://tmrh20.github.io/RF24/
#include <nRF24L01.h>
#include <Ethernet2.h> 
#include <EthernetUdp2.h>

#define BUZZER_PIN 3

boolean soundAlarmIsOff = false;

// Radio instance
RF24 radio(7, 8); // CE, CSN
const uint64_t readingPipe = 0xE8E8F0F0EBLL;                   // Индитификатор передачи, "труба"

// Задаём сетевую конфигурацию
byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
byte ip[] = { 10, 0, 0, 145 };                                // 10, 10, 65, 191     10, 0, 0, 145 
byte dns1[] = { 10, 0, 0, 101 };                              // 10, 10, 100, 16     10, 0, 0, 101 
byte gateway[] = { 10, 0, 0, 250 };                           // 10, 10, 65, 1       10, 0, 0, 250
byte subnet[] = { 255, 255, 255, 0 };                         // 255, 255, 255, 0    255, 255, 255, 0
byte serverIp[] = { 10, 0, 0, 100 };                            // 10, 10, 65, 200     10, 0, 0, 1
const unsigned int localPort = 9191;                          // Local port to listen on
const unsigned int serverPort = 9191;                         // Порт сервера на который отправляются сообщения через eth

// buffers for receiving and sending data
int pulseIN = 11;                                              // Нода1 посылает пульс
int motionIN = 12;                                             // Нода1 посылает обнаружено движение
int n1ReadyIN = 10;                                            // Нода1 посылает что она готова к работе
char pulseOUT[] = "11";                                        // Конвертить int to Char[] неудобно, проще сделавть так 
char motionOUT[] = "12";                                       // ...
char n1ReadyOUT[] = "10";                                      // ...
char sReady[] = "00";                                          // Ардуино сервер посылает что он готов к работе
char soundOff[] = "92";                                        // Сервер посылает Откоючить Buzzer
char soundOn[] = "91";                                         // Сервер посылает Вкоючить Buzzer
char packetBuffer[UDP_TX_PACKET_MAX_SIZE];                     // Buffer to hold incoming packet
String convert;                                                //For convert int to Char[]

// An EthernetUDP instance to let us send and receive packets over UDP
EthernetUDP Udp;

// Флаг для отправки Ready
boolean readyIsSend = false;

void setup() {

  //Serial.begin(115200);                  // открываем последовательное соединение
  //while(!Serial){
  //} 
  
  pinMode (BUZZER_PIN, OUTPUT);

  // Инициализация радио модуля  
  radio.begin();                         
  delay(100);                            // Умные люди советуют так сделать.
  radio.powerUp();
  delay(100);                     
  radio.setChannel(91);                  // Канал (0-127)
  radio.setRetries(15, 15);              // Колличество повыток и задержка после неудачной отправки (15,15 максимальные значние)
  radio.setDataRate(RF24_250KBPS);         // Скорость, RF24_250KBPS, RF24_1MBPS или RF24_2MBPS RF24_250KBPS на nRF24L01 (без +) неработает. Меньше скорость, выше чувствительность приемника.
  radio.setPALevel(RF24_PA_MAX);         // Мощьность передатчика RF24_PA_MIN=-18dBm, RF24_PA_LOW=-12dBm, RF24_PA_MED=-6dBM,  
  radio.setAutoAck(1);                   // Enable or disable auto-acknowlede packets on a per pipeline basis. 
  radio.setCRCLength(RF24_CRC_16);       // radio.setCRCLength(RF24_CRC_8); //radio.setCRCLength(RF24_CRC_DISABLED);
  radio.openReadingPipe(1,readingPipe);  // Открываем трубу на прием.
  radio.startListening();

  // Start the Ethernet and UDP:
  Ethernet.begin(mac, ip, dns1, gateway, subnet); 
  Udp.begin(localPort);
  
  //Serial.println("Setup finish");  
}

void loop() {
  
  if (!readyIsSend){
    readyIsSend = true;
    sendUDP(sReady);  
  }
  
  if (radio.available()){                // Проверяем не пришло ли чего в буфер через радиоканал    
    
    int data; 
    radio.read(&data, sizeof(data));     // Читаем данные, указываем сколько байт читать    

    //Serial.println(data);    
    
    if(data == pulseIN){                 // Если по радио пришло "пульс"      
      sendUDP(pulseOUT);                 // Пересылаем сообщение по eth 
    }

    if(data == motionIN){                // Если по радио пришео "движение"
      sendUDP(motionOUT);                // Пересылаем сообщение по eth 
        if(!soundAlarmIsOff){            // Если Buzzer включен
          soundAlarm();                  // Включаем звуковую сигнализацию
        }
    }

    if(data == n1ReadyIN){               // Если по радио пришло "готовность"
      sendUDP(n1ReadyOUT);               // Пересылаем сообщение по eth 
    }
  }

  int packetSize = Udp.parsePacket();                   
  
  if ((packetSize) ){ //& (Udp.remoteIP() == serverIp)) {      // if there's data available & serverIP is ok, read a packet   
    
    Udp.read(packetBuffer, UDP_TX_PACKET_MAX_SIZE);       // read the packet into packetBufffer    
    //Serial.print("packetBuffer: ");
    //Serial.println(packetBuffer);    
    
    if(packetBuffer[0] == soundOff[0] && packetBuffer[1] == soundOff[1]){  // Если пришло выключить buzzer, выключаем
      soundAlarmIsOff = true;
      //Serial.println("soundAlarmIsOff");  
    }
    
    if(packetBuffer[0] == soundOn[0] && packetBuffer[1] == soundOn[1]){    // Если пришло выключить buzzer, включаем
      soundAlarmIsOff = false;
      //Serial.println("soundAlarmIsON");
    }
  }
}

void soundAlarm(){
    tone(BUZZER_PIN,3000,80);
    delay(200);
    tone(BUZZER_PIN,2000,80);
    delay(200);
    tone(BUZZER_PIN,1000,80);
    delay(200);
  }

void sendUDP (char data[]){  
  Udp.beginPacket(serverIp,serverPort);
  Udp.write(data);
  Udp.endPacket();  
}
    
