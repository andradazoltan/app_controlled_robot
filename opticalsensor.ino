//TCRT5000 analog pins
int IRPin_1 = 1; 
int IRPin_2 = 2; 
int IRPin_3 = 3; 
int IRPin_4 = 4; 
int IRValues[4];

void setup() {
  Serial.begin(9600);
  IRValues[0] = IRValues[1] = IRValues[2] = IRValues[3] = 0;
}

void loop() {
  readIR(); 
}

void readIR(){
  IRValues[0] = analogRead(IRPin_1);
  IRValues[1] = analogRead(IRPin_2);
  IRValues[2] = analogRead(IRPin_3);
  IRValues[3] = analogRead(IRPin_4);
  
  Serial.println(IRValues[2]);

  Serial.print("Sensor 1: ");
   switch(checkBlackOrWhite(IRValues[0])) {
    case 1:
      Serial.println("black");
      break;
    case 0:
      Serial.println("white");
      break;
    default: 
      break;
  }
  
  Serial.print("Sensor 2: ");
   switch(checkBlackOrWhite(IRValues[1])) {
    case 1:
      Serial.println("black");
      break;
    case 0:
      Serial.println("white");
      break;
    default: 
      break;
  }
  
  Serial.print("Sensor 3: ");
   switch(checkBlackOrWhite(IRValues[2])) {
    case 1:
      Serial.println("black");
      break;
    case 0:
      Serial.println("white");
      break;
    default: 
      break;
  }

  Serial.print("Sensor 4: ");
   switch(checkBlackOrWhite(IRValues[3])) {
    case 1:
      Serial.println("black");
      break;
    case 0:
      Serial.println("white");
      break;
    default: 
      break;
  }

  delay(1000);
}

//returns whether or not the detected sensor value represents black or white
byte checkBlackOrWhite(int val){
  if (val > 500) {
    return 1;
  } else {
    return 0;
  }
}
