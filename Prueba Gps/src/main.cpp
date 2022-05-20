#include <ArduinoJson.h>
#include <ESP8266WiFi.h>
#include <SoftwareSerial.h>
#include <SoftwareSerial.h>//incluimos SoftwareSerial
#include <TinyGPS.h>//incluimos TinyGPS


#define rxPin 4
#define txPin 3

TinyGPS gps;//laramos el objeto gps
SoftwareSerial serialgps(rxPin, txPin);//laramos el pin 4 Rx y 3 Tx

//laramos la variables para la obtención de datos
int year;
byte month, day, hour, minute, second, hundredths;
unsigned long chars;
unsigned short sentences, failed_checksum;

void setup()
{
  pinMode(rxPin, INPUT);
  pinMode(txPin, OUTPUT);
  
  Serial.begin(115200);//Iniciamos el puerto serie
  serialgps.begin(9600);//Iniciamos el puerto serie del gps
  //Imprimimos:
  Serial.write("");
  Serial.write("GPS GY-GPS6MV2 Leantec");
  Serial.write(" ---Buscando senal--- ");
  Serial.write("");
}

void loop()
{
  Serial.write("hola");
  char c = serialgps.read();
  Serial.write(c);
   
  
}