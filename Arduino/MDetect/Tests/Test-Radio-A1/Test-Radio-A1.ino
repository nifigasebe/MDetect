#include <SPI.h>
#include <nRF24L01.h>
#include <RF24.h> // https://github.com/maniacbug/RF24

RF24 radio(7, 8); // CE, CSN

const int waitTime = 500; 
const uint64_t writingPipe = 0xE8E8F0F0EBLL; // индитификатор передачи, "труба" //const uint64_t pipe2 = 0xF0F1F2F3F3LL;

int message = 0;


void setup(){

  Serial.begin(115200);
  Serial.println("Setup start");
 
  radio.begin();
  delay(100);
  radio.powerUp();
  delay(100);
  
  radio.setChannel(91);
  radio.setRetries(15,15);
  radio.setDataRate(RF24_250KBPS); // скорость, RF24_250KBPS, RF24_1MBPS или RF24_2MBPS RF24_250KBPS на nRF24L01 (без +) неработает.
  radio.setPALevel(RF24_PA_MAX);; // мощьность передатчика RF24_PA_MIN=-18dBm, RF24_PA_LOW=-12dBm, RF24_PA_MED=-6dBM, radio.setPALevel(RF24_PA_HIGH);  
  radio.setAutoAck(1);
  radio.setCRCLength(RF24_CRC_16);  //radio.setCRCLength(RF24_CRC_8); //radio.setCRCLength(RF24_CRC_DISABLED);
  radio.openWritingPipe(writingPipe); 
  //radio.openReadingPipe(1,readingPipe);
  radio.startListening();

   Serial.println("Setup finish");
}

void loop() {
  sendData(message);
  delay(waitTime);
}

void sendData(int data){
  
  radio.stopListening(); // останавливает приём (нужно перед началом передачи)  
  radio.write(&data, sizeof(data)); // отправляем val и указываем сколько байт пакет
  radio.startListening(); // включаем приемник, начинаем слушать трубу
  
  Serial.print("Send data: ");
  Serial.println(message);
  message++;  
}

