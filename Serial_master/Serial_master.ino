#define commIN 12
#define commOUT 13
#define interruptPin 11

#define BUFFER_SIZE 6

char tosend[BUFFER_SIZE] = "HELLO";
char toreceive[BUFFER_SIZE];

void setup() {
  Serial.begin(9600);
  pinMode(commIN, INPUT);
  pinMode(commOUT, OUTPUT);
  pinMode(interruptPin, OUTPUT);
}

void loop() {
  communication(true);

  delay(3000);
}

/*
 * Function triggers an interrupt on a slave device and
 * sends device a request for data or a request to send it data.
 * 
 * Parameter: tx - true if master wants to send data
 */
void communication (boolean tx) {
  digitalWrite(interruptPin, HIGH);

  while(!digitalRead(commIN));

  if(tx) {
    digitalWrite(commOUT, LOW);
    delayMicroseconds(100);

    for(int i = 0; i < BUFFER_SIZE; i++) {
      for(int j = 0; j < 8; j++) {
        if((tosend[i]>>j) & 1)
          digitalWrite(commOUT, HIGH);
        else
          digitalWrite(commOUT, LOW);
        delayMicroseconds(100);
      }
      delayMicroseconds(100);
    } 
  }
  else {
    digitalWrite(commOUT, HIGH);
    delayMicroseconds(100);
    
    char temp = 0;
    for(int i = 0; i < BUFFER_SIZE; i++) {
      for(int j = 0; j < 8; j++) {
        delayMicroseconds(100);
        temp += digitalRead(commIN)<<j;
      }
      toreceive[i] = temp;
      temp = 0;
      delayMicroseconds(100);
    }
  } 
  digitalWrite(interruptPin, LOW);
}

