char data;
void setup() {
  Serial.begin(38400);
  pinMode(13, OUTPUT);
}

void loop() {
  if(Serial.available() > 0) {
    data = Serial.read();
    if(data == '1')
      digitalWrite(13,LOW);
    else if(data == '0')
      digitalWrite(13,HIGH);
  }

}
