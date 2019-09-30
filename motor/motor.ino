int relayPin = 8;// define output pin for relay 
#define inputpin 7
int x = 0;
int t = 0;

void setup() {
  Serial.begin(115200);
  pinMode(relayPin, OUTPUT);// define pin 8 as output
  pinMode(inputpin, INPUT);
}

void loop() {
  
//  digitalWrite(relayPin, LOW); // turn the relay ON (low is ON if relay is LOW trigger. change it to HIGH if you have got HIGH trigger relay)
//
//   delay(1000); // wait for 500 millisecond
//   digitalWrite(relayPin, HIGH);// // turn the relay OFF (HIGH is OFF if relay is LOW trigger. change it to LOW if you have got HIGH trigger relay)
//   delay(1000);// wait for 500 millisecond
  int x = digitalRead(inputpin);
  Serial.println(x);
  if(x == LOW){
   t = 0;
  }
  else{
    t = 1;
  }

  if(t == 0){
    digitalWrite(relayPin,LOW);
    delay(1000);
  }
  else{
    
     digitalWrite(relayPin,HIGH);
    delay(1000);
  }
  
}
