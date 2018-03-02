#include "line_following.h"
#include "obstacle_avoiding.h"
#include "ultrasonic_sensor.h"
#include "wheelZ.h"

//Define bluetooth pins
#define bRX 0
#define bTX 1

char string[20] = "";

void setup() {
  Serial.begin(9600);       //initialize bluetooth

  wheel_setup();
  ultrasonic_setup();
}

void loop() {
  int i = 1;
  int maxS = 1;
  char state, next_state;

  while(!Serial.available());
  string[0] = (char)Serial.read();

  if(string[0] == 'L')
    maxS = 18;
  else if(string[0] == 'M')
    maxS = 10;
  else if(string[0] == 'F')
    maxS = 1;
  else if(string[0] == 'O')
    maxS = 1;   
  
  while(i<maxS) { 
    state = 0;
    if (Serial.available()) {//check if there's any data sent from the remote bluetooth shield
      state = (char)Serial.read();
      string[i] = state;
      i++;
    }
    if(state == string[0])
      break;
  }
  
  if(string[0] == 'L' || next_state == 'L') {
    
  }
  else if(string[0] == 'M' || next_state == 'M')
    next_state = manual();
  else if(string[0] == 'F' || next_state == 'F')
    next_state = line_following();
  else if(string[0] == 'O' || next_state == 'O')
    next_state = obstacle_avoiding();

  clearArray(string, 20);
}

void clearArray(char arr[], int ln) {
  for (int i = 0; i < ln; i++) {
    arr[i] = '\0';
  }
}

char manual () {
  int radius, veloc, r, v = 0;
  char state;

  wheel_reset();
   
  while (true) {
    while(!Serial.available());
    state = Serial.read();

    //Check if state of robot has been changed by phone
    if (state != 'M')
      return state;

    int i = 0;
    while(i<12) { 
      state = 0;
      if (Serial.available()) {//check if there's any data sent from the remote bluetooth shield
        state = (char)Serial.read();
        string[i] = state;
        i++;
      }
      if(state == string[0])
        break;
    }

    veloc = (string[2]-'0')*10 + (string[3]-'0');
    radius = (string[9]-'0')*10 + (string[10]-'0');
    v = string[5];
    r = string[7];

    if(radius == 0)
      straight(veloc);
    else {
      if(v == 0)
        setDirection(0);
      else
        setDirection(1);

      if(r == 0)
        smoothTurn((double)radius, 1, veloc);
      else
        smoothTurn((double)radius, 0, veloc);
    }
    clearArray(string, 20);  
  } 
}

