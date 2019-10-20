
#include <ESP8266WiFi.h>
#include <Adafruit_MQTT_Client.h>
#define heartbeat A0
#define wifi "chandan"
#define password "9666459542"
#define server "io.adafruit.com"
#define port 1883
#define username "chandan3966"
#define key "7dbe3f897e5941eba1a06ba39c5093c4"


WiFiClient esp;

Adafruit_MQTT_Client mqtt(&esp,server,port,username,key);
Adafruit_MQTT_Publish feedHBR = Adafruit_MQTT_Publish(&mqtt,username"/feeds/hello");



void setup(void)
{
  Serial.begin(115200);
  pinMode(5, INPUT);
  Serial.println("Adafruit MQTT demo");
  Serial.print("Connecting to ");
  Serial.println(wifi);

  WiFi.begin(wifi,password);
  while(WiFi.status()!=WL_CONNECTED)
  {
     delay(500);
     Serial.print(".");
  }

  Serial.println("WiFi connected");
  Serial.println("IP Address: ");
  Serial.println(WiFi.localIP());

}
void loop() {
    MQTT_connect();
    
  int bpm = analogRead(5);
  Serial.print("Heartbeat found. bpm = ");
  Serial.println(bpm);
if(mqtt.connected())
  {
    Serial.print("Sending heart rate data. ");
   
    if(feedHBR.publish(bpm))
    {
      Serial.println("Success");
    }

    else
    {
      Serial.println("Fail upload!");
    }


   
  delay(2000);
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
