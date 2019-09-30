int floatswitch = 7;
int button = 8;

void setup() {
  // put your setup code here, to run once:
  pinMode(floatswitch,INPUT_PULLUP);
  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(button, INPUT);
  Serial.begin(115200);
}

void loop() {
  // put your main code here, to run repeatedly:
  int x = digitalRead(floatswitch);
//  int y= digitalRead(button);
  Serial.println(x);
//  Serial.println(y);
  if(x == 0)
   digitalWrite(LED_BUILTIN, LOW);
  else
   digitalWrite(LED_BUILTIN, HIGH);
  delay(100);
}
