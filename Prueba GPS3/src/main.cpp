#include <ESP8266WiFi.h>
#include <TinyGPSPlus.h>
#include <SoftwareSerial.h>
#include <PubSubClient.h>
#include "RestClient.h"
#include "ArduinoJson.h"

/*
   This sample sketch demonstrates the normal use of a TinyGPSPlus (TinyGPSPlus) object.
   It requires the use of SoftwareSerial, and assumes that you have a
   4800-baud serial GPS device hooked up on pins 4(rx) and 3(tx).
*///               D3            D4
static const int RXPin = 0, TXPin = 2;
int last = 0;

// The TinyGPSPlus object
TinyGPSPlus gps;

// The serial connection to the GPS device
SoftwareSerial ss(RXPin, TXPin);

int test_delay = 1000; //so we don't spam the API
boolean describe_tests = true;
//REST
RestClient restclient = RestClient("192.168.43.253", 80);//IP del servidor


//MQTT


const char* ssid = "Iphone F";
const char* password = "12345678";
const char* mqtt_server = "172.20.10.2";
 
WiFiClient espClient;
PubSubClient client(espClient);
long lastMsg = 0;
char msg[50];

String response;

String serializeBody(int id_Fly, double lat, double lon, int dir, double vel, double alt) // cambiar para nuestro gps
{
  StaticJsonDocument<200> doc;

  // StaticJsonObject allocates memory on the stack, it can be
  // replaced by DynamicJsonDocument which allocates in the heap.
  //
  // DynamicJsonDocument  doc(200);

  // Add values in the document
  //
  doc["id_Fly"] = id_Fly;//nuesstro vuelo
  doc["lat"] = lat;
  doc["lon"] = lon;
  doc["dir"] = dir;
  doc["vel"] = vel;
  doc["alt"] = alt;




  // Generate the minified JSON and send it to the Serial port.
  //
  String output;
  serializeJson(doc, output);
  // The above line prints:
  // {"sensor":"gps","time":1351824120,"data":[48.756080,2.302038]}

  // Start a new line
  Serial.println(output);

  // Generate the prettified JSON and send it to the Serial port.
  //
  //serializeJsonPretty(doc, output);
  // The above line prints:
  // {
  //   "sensor": "gps",
  //   "time": 1351824120,
  //   "data": [
  //     48.756080,
  //     2.302038
  //   ]
  // }
  return output;
}

void deserializeBody(String responseJson){
  if (responseJson != "")
  {
    StaticJsonDocument<200> doc;

    //char json[] =
    //    "{"id_Gps": 4,"id_Fly": 13,"lat": 103.60306397,"lon": 87.99266856,"dir": 162,"vel": 1335.07,"alt": 8075,"time": 1647291835}";

    // Deserialize the JSON document
    DeserializationError error = deserializeJson(doc, responseJson);

    // Test if parsing succeeds.
    if (error)
    {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.f_str());
      return;
    }

    // Fetch values.
    //
    // Most of the time, you can rely on the implicit casts.
    // In other case, you can do doc["time"].as<long>();


    int id_Gps = doc["id_Gps"];
    int id_Fly = doc["id_Fly"];
    double lat = doc["lat"];
    double lon = doc["lon"];
    int dir = doc["dir"];
    double vel = doc["vel"];
    double alt = doc["alt"]; 
    long time = doc["time"];   



  

    // Print values.
    Serial.println(id_Fly);
    Serial.println(lat);
    Serial.println(lon);
    Serial.println(dir);
    Serial.println(vel);
    Serial.println(alt);

  }
}

void test_response()
{
  Serial.println("TEST RESULT: (response body = " + response + ")");
  response = "";
}

void describe(char *description)
{
  if (describe_tests)
    Serial.println(description);
}



void test_status(int statusCode)
{
  delay(test_delay);
  if (statusCode == 200 || statusCode == 201)
  {
    Serial.print("TEST RESULT: ok (");
    Serial.print(statusCode);
    Serial.println(")");
  }
  else
  {
    Serial.print("TEST RESULT: fail (");
    Serial.print(statusCode);
    Serial.println(")");
  }
}




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
  Serial.println("Setup!");

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
  

void setup() {
  pinMode(BUILTIN_LED, OUTPUT);
  Serial.begin(115200);
  //Serial.begin(19200);
  ss.begin(9600);

  Serial.write("Inicialización del Gps");
  setup_wifi();
  client.setServer(mqtt_server, 1883);
  client.setCallback(callback);
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
 


void POST_tests()
{
   Serial.write("No GPS detected: check wiring.");
      int id_Fly = 1;//modificar manualmente
      double lat = gps.location.lat();
      double lon = gps.location.lng();
      int dir = gps.course.deg();
      double vel = gps.speed.kmph();
      double alt = gps.altitude.meters(); 

      String post_body = serializeBody(id_Fly,lat,lon,dir,vel,alt);
  describe("Test POST with path and body and response");
  test_status(restclient.post("/api/gps", post_body.c_str(), &response));
  test_response();
}
 
void loop() {
 
  if (!client.connected()) {
    reconnect();
  }
  client.loop();
  while (ss.available() > 0)
    if (gps.encode(ss.read()))


  if (millis() > 5000 && gps.charsProcessed() < 10)
  {
   
  }
 

  long now = millis();
  if (now - lastMsg > 2000) {
    lastMsg = now;
    Serial.print("Publish message: ");
    Serial.println(msg);
 uhvhbbnnj   }
}