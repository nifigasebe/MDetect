#include <SPI.h>
#include <nRF24L01.h>
#include <RF24.h>                              // http://tmrh20.github.io/RF24/

RF24 radio(7, 8);                              // CE, CSN
const int pirPin = 2;                          // PIR
int ledPin = 13;                               // Onboard led

const int waitTime = 15000;                    // Время(мс) ожидания после обнаружения движения
const int ledBlinkTime = 500;                  // Интервал(мс) мигния светодиодом
const unsigned int calibrationTime = 30;       // Время(с) необходимое для калибровки PIR (!60!)
const unsigned long pulseInterval = 30000;     // Интервал(мс) отправки пульса (30 сек)
const uint64_t writingPipe = 0xE8E8F0F0EBLL;   // Индитификатор передачи, "труба"

const int pulse = 11;                          // Пульс
const int motion = 12;                         // Обнаружено движение
const int a1Ready = 10;                        // Arduino Node1 Ready

static unsigned long previousMillis = 0;

void setup() {

  //Serial.begin(115200);                      // Открываем последовательное соединение
  //Serial.println("Setup start");

  pinMode(pirPin, INPUT);
  pinMode(ledPin, OUTPUT);
  
  for (int i=0; i<calibrationTime; i++) {      // Мигаем светодиодом пока идет калибровка
    digitalWrite(ledPin, HIGH);
    delay(ledBlinkTime);
    digitalWrite(ledPin, LOW);
    delay(ledBlinkTime);
    //Serial.println(i);
  }
    
  radio.begin();                         // Инициализация радио модуля
  delay(100);                            // Умные люди советуют так сделать.
  radio.powerUp();
  delay(100);                     
  radio.setChannel(91);                  // Канал (0-127)
  radio.setRetries(15, 15);              // Колличество повыток и задержка после неудачной отправки (15,15 максимальные значние)
  radio.setDataRate(RF24_250KBPS);       // Скорость, RF24_250KBPS, RF24_1MBPS или RF24_2MBPS RF24_250KBPS на nRF24L01 (без +) неработает. Меньше скорость, выше чувствительность приемника.
  radio.setPALevel(RF24_PA_MAX);         // Мощьность передатчика RF24_PA_MIN=-18dBm, RF24_PA_LOW=-12dBm, RF24_PA_MED=-6dBM,  
  radio.setAutoAck(1);                   // Enable or disable auto-acknowlede packets on a per pipeline basis
  radio.setCRCLength(RF24_CRC_16);       // radio.setCRCLength(RF24_CRC_8); //radio.setCRCLength(RF24_CRC_DISABLED);
  radio.openWritingPipe(writingPipe);    // Открываем трубу на передачу.
  radio.startListening();
  
  sendData(a1Ready);
  delay(50);

  //Serial.println("FinishSetup");
}

void loop() {
  
  if(digitalRead(pirPin) == HIGH){                    // Если движение обнаружено

    //Serial.println("Motion!");
    sendData(motion);
    delay(waitTime);
  
  }else{                                              // Если движения нет    
    
    if(millis() - previousMillis > pulseInterval) {   // Отправляем пульс каждый pulseInterval
      previousMillis = millis();
      sendData(pulse);
      //Serial.println("Pulse!");
    }
  }
}

void sendData(int data){

  //Serial.print("SendingData: ");
  
  radio.powerUp();                          // This will take up to 5ms for maximum compatibility
  delay(5); 
  radio.stopListening();                    // останавливает приём (нужно перед началом передачи)
  radio.write(&data, sizeof(data));         // Отправляем данные и указываем сколько байт пакет
  //Serial.println(data);
  radio.powerDown();                        // Выключаемся, экономим батарейку
}
