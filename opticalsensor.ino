//TCRT5000 analog pins
int IRPin_1 = 1; 
int IRVal_1;
int IRPin_2 = 2; 
int IRVal_2;

void setup() {
  Serial.begin(9600);
}

void loop() {
  readIR();
  processIR();
}

void readIR(){
  IRVal_1 = analogRead(IRPin_1);
  IRVal_2 = analogRead(IRPin_2);
}

void processIR(){
  Serial.print("Sensor 1: ");
   switch(checkBlackOrWhite(IRVal_1)) {
    case 1:
      Serial.println("black");
      while (IRVal_1==1){
        smoothTurn(1,LEFT,MAX_PWM);
      }
      break;
    case 0:
      Serial.println("white");
      //add moving forward code here
      break;
    default: break;
  }
  
  Serial.print("Sensor 2: ");
   switch(checkBlackOrWhite(IRVal_2)) {
    case 1:
      Serial.println("black");
      while (IRVal_2==1){
        smoothTurn(1,LEFT,MAX_PWM);
      }
      break;
    case 0:
      Serial.println("white");
      //add moving forward code here
      break;
    default: break;
  }
}

byte checkBlackOrWhite(int val){
  if (val > 500) {
    return 1;
  } else {
    return 0;
  }
}
