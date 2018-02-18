//Arduino PWM Speed Control：
int E1 = 5;  
int M1 = 4; 
int E2 = 6;                      
int M2 = 7;                        
int value;
String inString;
void setup() 
{ 
    Serial.begin(9600);
    pinMode(M1, OUTPUT);   
    pinMode(M2, OUTPUT); 
    value = 100;
    pinMode(3, INPUT);
    attachInterrupt(digitalPinToInterrupt(3), magnet, RISING);
} 

void loop() 
{ 
//  while (Serial.available() > 0) {
//    int inChar = Serial.read();
//    if (isDigit(inChar)) {
//      // convert the incoming byte to a char and add it to the string:
//      inString += (char)inChar;
//    }
//    // if you get a newline, print the string, then the string's value:
//    if (inChar == '\n') {
//      value = Serial.println(inString.toInt());
//      inString = "";
//    }
//  }
//    
//    digitalWrite(M1,HIGH);   
//    digitalWrite(M2, LOW);       
//    analogWrite(E1, value);   //PWM Speed Control
//    analogWrite(E2, value);   //PWM Speed Control
}

void magnet() {
  Serial.println("MAGNET!");
}
