#include <Arduino.h>
#include "RestClient.h"
#include "ArduinoJson.h"
#include <ESP8266WiFi.h>
#include <PubSubClient.h>

int test_delay = 1000; //so we don't spam the API
boolean describe_tests = true;

RestClient resrClient = RestClient("192.168.43.253", 8080);//IP del servidor

#define STASSID "le wifi"//Usuario
#define STAPSK  "Javier15"//Contraseña
const char* mqtt_server = "192.168.43.253";
double lat_prueba = 40.0;

WiFiClient espClient;
PubSubClient client(espClient);
long lastMsg = 0;

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
      client.subscribe("topic_1");
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      delay(5000);
    }
  }
}



//Setup
void setup()
{
  
  pinMode(BUILTIN_LED, OUTPUT);
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

void GET_tests()
{
  describe("Test GET with path");
  test_status(resrClient.get("/api/gps", &response));
  test_response();

  describe("Test GET with path and response");
  test_status(resrClient.get("/api/gps", &response));
  test_response();

  describe("Test GET with path");
  test_status(resrClient.get("/api/gps/123", &response));
  test_response();
}

void POST_tests()
{
  lat_prueba = lat_prueba + 0.000001; 
  String post_body = serializeBody(1,
                                  lat_prueba, 
                                  90.542384, 
                                  85,
                                  1000.0,
                                  10000,
                                  millis()//random esta loco
                                  ); 
  describe("Test POST with path and body and response");
  test_status(resrClient.post("/api/gps", post_body.c_str(), &response));
  test_response();
}






// Run the tests!
void loop()
{
  if (!client.connected()) {
    reconnect();
  }
  client.loop();


 
  POST_tests();
  
  
  
}