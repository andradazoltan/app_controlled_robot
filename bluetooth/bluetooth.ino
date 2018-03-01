#include <SoftwareSerial.h>
char state; 
char string[20] = "";

 SoftwareSerial blueToothSerial (4,5);
void setup() {
  Serial.begin(9600); // Default communication rate of the Bluetooth module
  blueToothSerial.begin(9600);
}

void loop() {  
  int radius, veloc, r, v = 0;
  int i = 1;
  int maxS = 1;

  while(!blueToothSerial.available());
  string[0] = (char)blueToothSerial.read();

    if(string[0] == 'L') {
      maxS = 18;
    }
    else if(string[0] == 'M') {
      maxS = 12;
    }
    else if(string[0] == 'F') {
      maxS = 1;
    }
    else if(string[0] == 'O') {
      maxS = 1;
    }    
  
  while(i<maxS) { 
    state = 0;
    if (blueToothSerial.available()) {//check if there's any data sent from the remote bluetooth shield
      state = (char)blueToothSerial.read();
      string[i] = state;
      i++;
    }
    if(state == string[0])
      break;
  }

  veloc = string[2]*10 + string[3];
  radius = (string[9]-'0')*10 + (string[10]-'0');
  v = string[5];
  r = string[7];

  
Serial.println(string);
Serial.println(radius);
  clearArray(string, 10);
}

void clearArray(char arr[], int ln) {
  for (int i = 0; i < ln; i++) {
    arr[i] = '\0';
  }
}
