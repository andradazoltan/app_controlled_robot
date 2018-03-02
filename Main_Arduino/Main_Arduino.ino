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
  int state, next_state;

  while(!Serial.available());
  string[0] = (char)Serial.read();

    if(string[0] == 'L') {
      maxS = 18;
    }
    else if(string[0] == 'M') {
      maxS = 10;
    }
    else if(string[0] == 'F') {
      maxS = 1;
    }
    else if(string[0] == 'O') {
      maxS = 1;
    }    
  
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
  
  if(string[0] == 'L') {
    
  }
  else if(string[0] == 'M') {
    next_state = manual();
  }
  else if(string[0] == 'F') {
   // next_state = (int)line_following();
  }
  else if(string[0] == 'O') {
   // next_state = obstacle_avoiding();
  }

  clearArray(string, 10);
}

void clearArray(char arr[], int ln) {
  for (int i = 0; i < ln; i++) {
    arr[i] = '\0';
  }
}

int manual () {
  int state, radius, veloc = 0;

  wheel_reset();
   
  while (true) {
    //Check if state of robot has been changed by phone
    if (Serial.available())
      char state = Serial.read();

    if (state != 'M')
      return state;

    int i = 0;
    while(i<10) { 
      state = 0;
      if (Serial.available()) {//check if there's any data sent from the remote bluetooth shield
        state = (char)Serial.read();
        string[i] = state;
        i++;
      }
      if(state == string[0])
        break;
    }

    radius = string[2]*10 + string[3];
    veloc = string[5]*10 + string[6];
    


    for (i = 2; string[i] != ' '; i++)
      radius += (string[i] - '0')*pow(10,(i-2)); 
    for(int y = i; string[y] != 'M'; y++)
      veloc += (string[y] - '0')*pow(10,(y-i));

    if(radius == 0)
      straight(veloc);
    else {
      if(veloc < 0)
        setDirection(0);
      else
        setDirection(1);

      if(radius < 0)
        smoothTurn((double)abs(radius), 1, abs(veloc));
      else
        smoothTurn((double)radius, 0, abs(veloc));
    }
      
  } 
}

