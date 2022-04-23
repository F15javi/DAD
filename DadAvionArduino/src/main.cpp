#include "RestClient.h"
#include "ArduinoJson.h"
#include <ESP8266WiFi.h>

int test_delay = 1000; //so we don't spam the API
boolean describe_tests = true;

RestClient client = RestClient("192.168.1.102", 80);//IP del servidor

#define STASSID "MiFibra-E482-24G"//Usuario
#define STAPSK  "Ng9ggSvK"//Contraseña

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


    int id_Gps = doc["id_Gps"];
    int id_Fly = doc["id_Fly"];
    double lat = doc["lat"];
    double lon = doc["lon"];
    int dir = doc["dir"];
    double vel = doc["vel"];
    double alt = doc["alt"]; 
    long time = doc["time"];   



  

    // Print values.
    Serial.println(id_Gps);
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
  test_status(client.get("/api/gps", &response));
  test_response();

  describe("Test GET with path and response");
  test_status(client.get("/api/gps", &response));
  test_response();

  describe("Test GET with path");
  test_status(client.get("/api/gps/123", &response));
  test_response();
}

void POST_tests()
{
  String post_body = serializeBody(random(1,200),
                                  random(1,200),
                                  random(-900,900)/10.0, 
                                  random(-1800, 1800)/10.0, 
                                  random(0,359),
                                  random(1,20000)/10.0,
                                  random(0,200000)/10,
                                  random(141241154122,251241254122)//random esta loco
                                  ); 
  describe("Test POST with path and body and response");
  test_status(client.post("/api/gps", post_body.c_str(), &response));
  test_response();
}

void PUT_tests()
{
  int id_Fly = 2;
  long timestamp = 151241254122;

  double lat = 154.8680115;
  double lon = 79.86796334;
  int dir = 277;
  double vel = 1988.41;
  double alt = 3436.48;


  // POST TESTS
  String post_body = "{ 'id_Gps' : 1, 'id_Fly': " + id_Fly;
  post_body = post_body + " , 'lat' :" + timestamp;
  post_body = post_body + " , 'lon' :" + timestamp;
  post_body = post_body + " , 'dir' :" + timestamp;
  post_body = post_body + " , 'vel' :" + timestamp;
  post_body = post_body + " , 'alt' :" + timestamp;
  post_body = post_body + ", 'timestamp' : '151241254122'}";

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
  int id_Fly = 2;
  //long timestamp = 151241254122;

  double lat = 154.8680115;
  double lon = 79.86796334;
  int dir = 277;
  double vel = 1988.41;
  double alt = 3436.48;
  // POST TESTS
  String post_body = "{ 'id_Gps' : 1, 'id_Fly': " + id_Fly;
  post_body = post_body + " , 'lat' :" + lat;
  post_body = post_body + " , 'lon' :" + lon;
  post_body = post_body + " , 'dir' :" + dir;
  post_body = post_body + " , 'vel' :" + vel;
  post_body = post_body + " , 'alt' :" + alt;
  post_body = post_body + ", 'timestamp' : '151241254122'}";

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
