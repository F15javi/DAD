<<<<<<< HEAD
#include <ArduinoJson.h>
#include <ESP8266WiFi.h>
#include <SoftwareSerial.h>
#include <TinyGPS.h>

TinyGPS gps;
SoftwareSerial Serialgps(4,3); //Declaramos el pin 4 Tx y 3 Rx
//Declaramos la variables para la obtención de datos
int year;
byte month, day, hour, minute, second, hundredths;
unsigned long chars;
unsigned short sentences, failed_checksum;

void setup()
{
  Serial.begin(115200);
  Serialgps.begin(9600);//Iniciamos el puerto serie del gps
  //Imprimimos
  Serial.println();
  Serial.println('GPS GY-GPS6MV2 Leantec');
  Serial.println('---Buscando senal---');
  Serial.println();
}

void loop()
{
  while(Serialgps.available()) 
  {
    int c = Serialgps.read();
 
    if(gps.encode(c))  
    {
      float latitude, longitude;
      gps.f_get_position(&latitude, &longitude);
      Serial.print('Latitud Longitud'); 
      Serial.print(latitude,5); 
      Serial.print(','); 
      Serial.println(longitude,5);


  gps.crack_datetime(&year,&month,&day,&hour,&minute,&second,&hundredths);
      Serial.print('Fecha'); Serial.print(day, DEC); Serial.print(' '); 
      Serial.print(month, DEC); Serial.print(' '); Serial.print(year);
      Serial.print('Hora'); Serial.print(hour, DEC); Serial.print(' '); 
      Serial.print(minute, DEC); Serial.print(' '); Serial.print(second, DEC); 
      Serial.print('.'); Serial.println(hundredths, DEC);
      Serial.print('Altitud (metros) ');
      Serial.println(gps.f_altitude()); 
      Serial.print('Rumbo (grados) '); Serial.println(gps.f_course()); 
      Serial.print('Velocidad (kmph) ');
      Serial.println(gps.f_speed_kmph());
      Serial.print('Satelites'); Serial.println(gps.satellites());
      Serial.println();
      gps.stats(&chars, &sentences, &failed_checksum);  
    }
  }
=======
#include <ESP8266WiFi.h>
#include <PubSubClient.h>
 
const char* ssid = "MiFibra-E482-24G";
const char* password = "Ng9ggSvK";
const char* mqtt_server = "localhost";
 
WiFiClient espClient;
PubSubClient client(espClient);
long lastMsg = 0;
char msg[50];
 

void setup_wifi() {
 
  delay(10);
  // We start by connecting to a WiFi network
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);
 
  WiFi.begin(ssid, password);
 
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
 
  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}
 
void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();
 
  if ((char)payload[0] == '1') {
    digitalWrite(BUILTIN_LED, LOW);
  } else {
    digitalWrite(BUILTIN_LED, HIGH);  // Turn the LED off by making the voltage HIGH
  }
 
}
 
void reconnect() {
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    if (client.connect("ESP8266Client")) {
      Serial.println("connected");
      client.publish("casa/despacho/temperatura", "Enviando el primer mensaje");
      client.subscribe("casa/despacho/luz");
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      delay(5000);
    }
  }
}
void setup() {
  //pinMode(BUILTIN_LED, OUTPUT);
  Serial.begin(9600);
  setup_wifi();
  client.setServer(mqtt_server, 1883);
  client.setCallback(callback);
}
 
 
void loop() {
 
  if (!client.connected()) {
    reconnect();
  }
  client.loop();
 
  long now = millis();
  if (now - lastMsg > 2000) {
    lastMsg = now;
    Serial.print("Publish message: ");
    Serial.println(msg);
    client.publish("casa/despacho/temperatura", msg);
  }
>>>>>>> 6e88a3e7cffd787ed252647194a8e9401ee285a7
}