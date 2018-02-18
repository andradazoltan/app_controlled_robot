#define BLUEPIN 6
#define REDPIN 5
#define GREENPIN 9
#define RECV_PIN 11
#define FADESPEED 5

//BUTTON CODE LOG
//ON/OFF SWITCH 4294967295
//FST FORWARD 16762935
//BCK FORWARD 16746615
//PLUS 16713975 
//MINUS 16722135

//HOLD 4294967295

#include <IRremote.h>
#include <Servo.h> 

IRrecv irrecv(RECV_PIN);
decode_results results;

int g, b;

void setup() {
  // put your setup code here, to run once:

  Serial.begin(9600);
  Serial.println("Started program");
  
  irrecv.enableIRIn(); // Start the receiver
  
  pinMode(REDPIN, OUTPUT);
  pinMode(GREENPIN, OUTPUT);
  pinMode(BLUEPIN, OUTPUT);

  analogWrite(BLUEPIN, 255);
  analogWrite(GREENPIN, 0);
}

void loop() {
  // put your main code here, to run repeatedly:

  if (irrecv.decode(&results))
  {
     Serial.println(results.value);
     switch(results.value){
      case 16762935:
        Serial.println("0, 255");
        analogWrite(BLUEPIN,255);
        analogWrite(GREENPIN,0);
        break;
//        for (b = 255; b > 0; b--) { 
//          analogWrite(BLUEPIN, b);
//          delay(FADESPEED);
//        }

      case 16746615:
        Serial.println("255,0");
        analogWrite(BLUEPIN,0);
        analogWrite(GREENPIN,255);
        break;
//        for (g = 0; g < 256; g++) { 
//          analogWrite(GREENPIN, g);
//          delay(FADESPEED);
//        } 

      case 16713975:
        Serial.println("100");
        analogWrite(BLUEPIN, 100);
        analogWrite(GREENPIN, 100);
        break;
        
      case 4294967295:
        Serial.println("0");
        analogWrite(BLUEPIN, 0);
        analogWrite(GREENPIN, 0);
        break;
  
//        for (g = 255; g > 0; g--) { 
//          analogWrite(GREENPIN, g);
//          delay(FADESPEED);
//        } 

        break;
        default:
          delay(100);
     }
     
     irrecv.resume(); // Receive the next value

  }

}
