#include "RestClient.h"
#include "ArduinoJson.h"
#include <ESP8266WiFi.h>

int test_delay = 1000; //so we don't spam the API
boolean describe_tests = true;

RestClient client = RestClient("192.168.1.102", 80);//IP del servidor

#define STASSID "DADLinksys"//Usuario
#define STAPSK  "dad202122"//Contraseña

//Setup
void setup()
{
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
}

String response;

String serializeBody(int id_Gps, int id_Fly, double lat, double lon, int dir, double vel, double alt, long time) // cambiar para nuestro gps
{
  StaticJsonDocument<200> doc;

  // StaticJsonObject allocates memory on the stack, it can be
  // replaced by DynamicJsonDocument which allocates in the heap.
  //
  // DynamicJsonDocument  doc(200);

  // Add values in the document
  //
  doc["id_Gps"] = id_Gps;//modificar esto tambien
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
    const char *sensor = doc["sensor"];//cambiar esto!!!
    long time = doc["time"];
    double latitude = doc["data"][0];
    double longitude = doc["data"][1];

    // Print values.
    Serial.println(sensor);
    Serial.println(time);
    Serial.println(latitude, 6);
    Serial.println(longitude, 6);
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
  test_status(client.get("/api/sensors", &response));
  test_response();

  describe("Test GET with path and response");
  test_status(client.get("/api/sensors", &response));
  test_response();

  describe("Test GET with path");
  test_status(client.get("/api/sensors/123", &response));
  test_response();
}

void POST_tests()
{
  String post_body = serializeBody(millis(), "gps", millis(), random(200, 400)/10, random(-150, -30)/10);
  describe("Test POST with path and body and response");
  test_status(client.post("/api/sensors", post_body.c_str(), &response));
  test_response();
}

void PUT_tests()
{
  int temp = 38;
  long timestamp = 151241254122;
  // POST TESTS
  String post_body = "{ 'idsensor' : 18, 'value': " + temp;
  post_body = post_body + " , 'timestamp' :";
  post_body = post_body + timestamp;
  post_body = post_body + ", 'user' : 'Luismi'}";

  describe("Test PUT with path and body");
  test_status(client.put("/data/445654", post_body.c_str()));

  describe("Test PUT with path and body and response");
  test_status(client.put("/data/1241231", post_body.c_str(), &response));
  test_response();

  describe("Test PUT with path and body and header");
  client.setHeader("X-Test-Header: true");
  test_status(client.put("/data-header/1241231", post_body.c_str()));

  describe("Test PUT with path and body and header and response");
  client.setHeader("X-Test-Header: true");
  test_status(client.put("/data-header/1241231", post_body.c_str(), &response));
  test_response();

  describe("Test PUT with 2 headers and response");
  client.setHeader("X-Test-Header1: one");
  client.setHeader("X-Test-Header2: two");
  test_status(client.put("/data-headers/1241231", post_body.c_str(), &response));
  test_response();
}

void DELETE_tests()
{
  int temp = 37;
  long timestamp = 151241254122;
  // POST TESTS
  String post_body = "{ 'idsensor' : 18, 'value': " + temp;
  post_body = post_body + " , 'timestamp' :";
  post_body = post_body + timestamp;
  post_body = post_body + ", 'user' : 'Luismi'}";

  describe("Test DELETE with path");
  //note: requires a special endpoint
  test_status(client.del("/del/1241231"));

  describe("Test DELETE with path and body");
  test_status(client.del("/data/1241231", post_body.c_str()));

  describe("Test DELETE with path and body and response");
  test_status(client.del("/data", post_body.c_str(), &response));
  test_response();

  describe("Test DELETE with path and body and header");
  client.setHeader("X-Test-Header: true");
  test_status(client.del("/data-header", post_body.c_str()));

  describe("Test DELETE with path and body and header and response");
  client.setHeader("X-Test-Header: true");
  test_status(client.del("/data-header", post_body.c_str(), &response));
  test_response();

  describe("Test DELETE with 2 headers and response");
  client.setHeader("X-Test-Header1: one");
  client.setHeader("X-Test-Header2: two");
  test_status(client.del("/data-headers", post_body.c_str(), &response));
  test_response();
}

// Run the tests!
void loop()
{
  GET_tests();
  POST_tests();
  //PUT_tests();
  //DELETE_tests();
}
