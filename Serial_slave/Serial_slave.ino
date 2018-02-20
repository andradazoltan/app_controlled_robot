#define commIN 12
#define commOUT 13
#define interruptPin 2

#define BUFFER_SIZE 6

char tosend[BUFFER_SIZE] = "HELLO";
char toreceive[BUFFER_SIZE];

void setup() {
  Serial.begin(9600);
  pinMode(commIN, INPUT);
  pinMode(commOUT, OUTPUT);
  pinMode(interruptPin, INPUT_PULLUP);
  attachInterrupt(digitalPinToInterrupt(interruptPin), communication, RISING);
}

void loop() {
  Serial.println(toreceive);
  delay(3000);
}

/*
 * Interrupt service routine occurs when the master devices wants
 * to communicate with the slave. The master will provide a command
 * about whether it wants data sent or it wants to send data.
 */
void communication () {
  digitalWrite(commOUT, HIGH);

  boolean tx = digitalRead(commIN);
  delayMicroseconds(100); 
  
  //SEND TO MASTER
  if(tx) {
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
  
  //RECEIVE FROM MASTER
  else{
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
  digitalWrite(commOUT, LOW);
}

