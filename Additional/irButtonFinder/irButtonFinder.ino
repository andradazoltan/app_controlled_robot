#include <IRremote.h>
#include <Servo.h> 

int RECV_PIN = 11;

IRrecv irrecv(RECV_PIN);
decode_results results;

void setup() {
  Serial.begin(9600);
  Serial.println("Started program");
  irrecv.enableIRIn(); // Start the receiver
}

void loop() {
  if (irrecv.decode(&results))
  {
     Serial.println(results.value, HEX);
     Serial.println(results.value);
     irrecv.resume(); // Receive the next value

}
}
