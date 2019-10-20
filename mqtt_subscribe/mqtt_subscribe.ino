#include <ESP8266WiFi.h>
#include <Adafruit_MQTT.h>
#include <Adafruit_MQTT_Client.h>

// Change YOUR Wi-Fi SSID and Password below 
const char* ssid     = "chandan";
const char* password = "9666459542";

// Set MQTT Server, User name and password details below
#define MQTT_SERVER      "io.adafruit.com"
#define MQTT_USERNAME    "chandan3966"
#define MQTT_PASSWORD    "7dbe3f897e5941eba1a06ba39c5093c4"
#define MQTT_SERVERPORT  1883
#define SUBSCRIBE_TOPIC  "chandan3966/feeds/hello"

#define RELAY_PIN  5

// Create an ESP8266 WiFiClient class to connect to the MQTT server.
WiFiClient client;

// Setup the MQTT client class by passing in the WiFi client and MQTT server and login details.
Adafruit_MQTT_Client mqtt(&client, MQTT_SERVER, MQTT_SERVERPORT, MQTT_USERNAME, MQTT_PASSWORD);

// Subscribe
Adafruit_MQTT_Subscribe onoffbutton = Adafruit_MQTT_Subscribe(&mqtt, SUBSCRIBE_TOPIC);

void MQTT_connect();

/*
 * Set Up
 */
void setup() {

  // Connect to WiFi Network
  connect2WiFi();

  mqtt.subscribe(&onoffbutton);
}

/* 
 *  Connects to WiFi Network
 */
void connect2WiFi()
{
  // set the baud rate
  Serial.begin(115200);

  // Set the Pin mode to "Output" - This is required!
  pinMode(RELAY_PIN, OUTPUT);
  
  // wait for 10 milli seconds for it to initialize
  delay(10);

  Serial.println();
  Serial.print("Connecting to WiFi Network - ");
  Serial.println(ssid);
  
  WiFi.begin(ssid, password);
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi Network is connected with the following.");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());  
}

void loop() {

  // Connect to MQTT
  MQTT_connect();
 

  Adafruit_MQTT_Subscribe *subscription;
  while ((subscription = mqtt.readSubscription(5000))) 
  {
    // Check if its the onoff button feed
    if (subscription == &onoffbutton) 
    {
//      Serial.print(F("Relay Status: ")); Serial.println((char *)onoffbutton.lastread);
      
      if ( strcmp((char *)onoffbutton.lastread, "1023") == 0)
      {
         digitalWrite(RELAY_PIN,LOW);
        Serial.println("LOW");
      }
      else{
           digitalWrite(RELAY_PIN,HIGH);
           Serial.println("HIGH");
      }
    }
  }
  
  // ping the server to keep the mqtt connection alive
  if(! mqtt.ping()) {
    mqtt.disconnect();
  }  
  
}

void MQTT_connect()
{
  int8_t ret;

  // Stop if already connected.
  if (mqtt.connected()) 
  {
    return;
  }

  Serial.print("Connecting to MQTT... ");

  uint8_t retries = 3;
  while ((ret = mqtt.connect()) != 0) 
  {
      // connect will return 0 for connected
      Serial.println(mqtt.connectErrorString(ret));
      Serial.println("Retrying MQTT connection in 5 seconds...");
      mqtt.disconnect();
      delay(5000);  // wait 5 seconds
      retries--;
      if (retries == 0) 
      {
        // basically die and wait for WDT to reset me
        while (1);
      }
  }
  Serial.println("MQTT Connected!");
}
