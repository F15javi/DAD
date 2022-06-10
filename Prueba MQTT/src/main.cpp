#include <Arduino.h>
#include "RestClient.h"
#include "ArduinoJson.h"
#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <TinyGPSPlus.h>
#include <SoftwareSerial.h>

//////////////////////////////////////////////////////////////////////////////////
/////////////////////////////// VARIABLES GENERALES //////////////////////////////
//////////////////////////////////////////////////////////////////////////////////
double lat_prueba1 = 39.95;
double lat_prueba2 = 39.9;
double alt_prueba = 700;

/////////////////////////////// SENSOR GPS ///////////////////////////////////////
//               D3            D4           Declaracion de los pines de para el gps
static const int RXPin = 0, TXPin = 2;
// Objeto gps
TinyGPSPlus gps;
// Objeto SoftwareSerial
SoftwareSerial ss(RXPin, TXPin);


/////////////////////////////// CONEXIONES ///////////////////////////////////////
int test_delay = 0; //so we don't spam the API
boolean describe_tests = true;

/////////////////////////////// CONEXIÓN WIFI ////////////////////////////////////
RestClient resrClient = RestClient("192.168.43.253", 8080);//IP del servidor

#define STASSID "le wifi"//Usuario
#define STAPSK  "Javier15"//Contraseña
const char* mqtt_server = "192.168.43.253";

WiFiClient espClient;

PubSubClient client(espClient);
long lastMsg = 0;
/////////////////////////////// CONEXIÓN MQTT ////////////////////////////////////
void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();

  
  long now = millis();
  if (now - lastMsg > 10000) {
    lastMsg = now;
    Serial.print("10 segundos");
    if ((char)payload[0] == '1') {
      digitalWrite(5, HIGH);   //Si queremos que se encienda el BUILTIN_LED cambiamos el 5 por BUILTIN_LED
    } else {
      digitalWrite(5, LOW);  // Turn the LED off by making the voltage HIGH  //Lo mismo qeu arriba
    }
    
  }
  
  if(alt_prueba <= 500){

    digitalWrite(4, HIGH);
    Serial.println("Pull up");

  }else if (alt_prueba > 500){
      digitalWrite(4, LOW);
      Serial.println("OK");

  }else{
    Serial.println("Crash");
  }

}

void reconnect() {
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    if (client.connect("Avion_1")) {   //Cambiamos dependiendo de la placa también
      Serial.println("connected");
      client.subscribe("topic_1");    //Cambiar dependiendo de la placa
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      delay(5000);
    }
  }
}



//////////////////////////////////////////////////////////////////////////////////
/////////////////////////////// FUNCION SETUP ////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////
void setup()
{
  
  //pinMode(BUILTIN_LED, OUTPUT);     Descomenta esto para que el led de la condición de cercanía se encienda por el BUILTIN_LED
  pinMode(5, OUTPUT);     // D1 condicion de cercania
  pinMode(4, OUTPUT);     // D2 condicion de altura
  //digitalWrite(BUILTIN_LED, HIGH);  // Turn the LED off by making the voltage HIGH
  digitalWrite(5, LOW);  // Turn the LED off by making the voltage HIGH

  Serial.begin(9600);
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(STASSID);

  /* Explicitly set the ESP8266 to be a WiFi-client, otherwise, it by default,
     would try to act as both a client and an access-point and could cause
     network-issues with your other WiFi-devices on your WiFi-network. */
  WiFi.mode(WIFI_STA);
  WiFi.begin(STASSID, STAPSK);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
  Serial.println("Setup!");
  client.setServer(mqtt_server, 1883);
  client.setCallback(callback);
}

//////////////////////////////////////////////////////////////////////////////////
/////////////////////////////// FUNCIONES AUXILIARES /////////////////////////////
//////////////////////////////////////////////////////////////////////////////////
String response;

String serializeBody(int id_Fly, double lat, double lon, int dir, double vel, double alt, long time) // cambiar para nuestro gps
{
  StaticJsonDocument<200> doc;

  // StaticJsonObject allocates memory on the stack, it can be
  // replaced by DynamicJsonDocument which allocates in the heap.
  //
  // DynamicJsonDocument  doc(200);

  // Add values in the document
  //
  doc["id_Fly"] = id_Fly;
  doc["lat"] = lat;
  doc["lon"] = lon;
  doc["dir"] = dir;
  doc["vel"] = vel;
  doc["alt"] = alt;
  doc["time"] = time;

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

void deserializeBody(String responseJson){
  if (responseJson != "")
  {
    StaticJsonDocument<200> doc;

    //char json[] =
    //    "{"id_Fly": 13,"lat": 103.60306397,"lon": 87.99266856,"dir": 162,"vel": 1335.07,"alt": 8075,"time": 1647291835}";

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
    Serial.println(time);

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

/////////////////////////////// FUNCION DE SENSOR ///////////////////////////////

void POST_GPS()
{
  String post_body = serializeBody(1,
                                  gps.altitude.meters(), 
                                  gps.location.lat(), 
                                  gps.location.lng(),
                                  gps.course.deg(),
                                  gps.speed.kmph(),
                                  millis()
                                  ); 
  //describe("Test POST with path and body and response");
  //test_status(resrClient.post("/api/gps", post_body.c_str(), &response));
  //test_response();
  resrClient.post("/api/gps", post_body.c_str(), &response);
  delay(test_delay);

}
void POST_GPS1_TestProximidad()
{
  lat_prueba1 = lat_prueba1 - 0.001;
  alt_prueba = alt_prueba - 10; 
  String post_body = serializeBody(1,
                                  lat_prueba1, 
                                  89, 
                                  180,
                                  1000.0,
                                  alt_prueba,
                                  millis()//random esta loco
                                  ); 
  //describe("Test POST with path and body and response");
  //test_status(resrClient.post("/api/gps", post_body.c_str(), &response));
  //test_response();
  resrClient.post("/api/gps", post_body.c_str(), &response);
  delay(test_delay);

}
void POST_GPS2_TestProximidad()
{
  lat_prueba2 = lat_prueba2 + 0.001; 
  alt_prueba = alt_prueba + 10;
  String post_body = serializeBody(2,
                                  lat_prueba2, 
                                  89, 
                                  0,
                                  1000.0,
                                  alt_prueba,
                                  millis()//random esta loco
                                  ); 
  //describe("Test POST with path and body and response");
  //test_status(resrClient.post("/api/gps", post_body.c_str(), &response));
  //test_response();
  resrClient.post("/api/gps", post_body.c_str(), &response);
  delay(test_delay);
}
void POST_GPS1_TestDistancia()
{
  String post_body = serializeBody(1,
                                  40, 
                                  89, 
                                  180,
                                  1000.0,
                                  10000,
                                  millis()//random esta loco
                                  ); 
  //describe("Test POST with path and body and response");
  //test_status(resrClient.post("/api/gps", post_body.c_str(), &response));
  //test_response();
  resrClient.post("/api/gps", post_body.c_str(), &response);
  delay(test_delay);

}
void POST_GPS2_TestDistancia()
{
  String post_body = serializeBody(2,
                                  40, 
                                  89, 
                                  0,
                                  1000.0,
                                  10000,
                                  millis()//random esta loco
                                  ); 
  //describe("Test POST with path and body and response");
  //test_status(resrClient.post("/api/gps", post_body.c_str(), &response));
  //test_response();
  resrClient.post("/api/gps", post_body.c_str(), &response);
  delay(test_delay);
}
void POST_GPS_TestAltura()
{
  alt_prueba = alt_prueba - 10; 
  String post_body = serializeBody(2,
                                  40, 
                                  89, 
                                  0,
                                  1000.0,
                                  alt_prueba,
                                  millis()//random esta loco
                                  ); 
  //describe("Test POST with path and body and response");
  //test_status(resrClient.post("/api/gps", post_body.c_str(), &response));
  //test_response();
  resrClient.post("/api/gps", post_body.c_str(), &response);
  delay(test_delay);

  if(alt_prueba <= 500){

    digitalWrite(4, HIGH);
    Serial.println("Pull up");

  }else if (alt_prueba > 500){
      digitalWrite(4, LOW);
      Serial.println("OK");

  }else{
    Serial.println("Crash");
  }



}
void check_alt_gps(){

  if(gps.altitude.meters()<=500){

    digitalWrite(4, HIGH);
    Serial.println("Pull up");

  }else if (gps.altitude.meters() > 500){
      digitalWrite(4, LOW);
      Serial.println("OK");

  }else{
    Serial.println("Crash");
  }



}


//////////////////////////////////////////////////////////////////////////////////
/////////////////////////////// LOOP /////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////
// Run the tests!
void loop()
{
  if (!client.connected()) {
    reconnect();
  }
  client.loop();

  //POST_GPS();       //Descomenta para ejecutar el GPS

/////////////////////////////// PRUEBAS DE PROXIMIDAD /////////////////////////////// 
  POST_GPS1_TestProximidad();//proximida y altura descendindo
  //POST_GPS2_TestProximidad();//proximidad y altura ascendiendo
  
/////////////////////////////// PRUEBAS DE ALTURA /////////////////////////////// 
  //POST_GPS_TestAltura();
  //check_alt_gps();
}
