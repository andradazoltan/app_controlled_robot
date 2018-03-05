#include "line_following.h"
#include "obstacle_avoiding.h"
#include "ultrasonic_sensor.h"
#include "wheelZ.h"

//Define bluetooth pins
#define bRX 0
#define bTX 1

//Define Serial pins
#define commIN 12
#define commOUT 13
#define interruptPin 11

int BUFFER_SIZE = 11;
char string[20] = "";
char toreceive[11] = "";
boolean start = true;

void setup() {
  Serial.begin(9600);       //initialize bluetooth

  pinMode(commIN, INPUT);
  pinMode(commOUT, OUTPUT);
  pinMode(interruptPin, OUTPUT);

  wheel_setup();
  ultrasonic_setup();
}

void loop() { 
  int i = 1;
  char state, next_state;
  if(start) {
    start = false;
    while(!Serial.available());
    string[0] = (char)Serial.read(); 
    
    while(true) { 
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
      communication(true);
      start = true;
    }
    else if(string[0] == 'M')
      next_state = manual();
    else if(string[0] == 'F')
      next_state = follow_line();
    else if(string[0] == 'O')
      next_state = avoid_obstacles();
  }

  else {
    if(next_state == 'M')
      next_state = manual();
    else if(next_state == 'F')
      next_state = follow_line();
    else if(next_state == 'O')
      next_state = avoid_obstacles();
    else if(next_state == 'S')
      start = true;
  }
  clearArray(string, 20);
}

/*
 * Function loops to continually move the robot according to
 * how an Arduino phone is tilted. Robot continues to proceed in
 * this state until it detects a different state from the 
 * Android (via Bluetooth)
 * 
 * Returns: character that represents the next state for the
 *        robot to be in
 */
char manual () {
  int radius, veloc, r, v, i = 0;
  char state;
   
  while (true) {
    while(!Serial.available());
    state = Serial.read();
    string[0] = state;

    i = 1;
    while(true) { 
      state = 0;
      if (Serial.available()) {//check if there's any data sent from the remote bluetooth shield
        state = (char)Serial.read();
        string[i] = state;
        i++;
      }
      if(state == string[0])
        break;
    }

    //Check if state of robot has been changed by phone
    if(string[0] == 'L')
      communication(true);
    else if (string[0] != 'M'){
      halt();
      return state;
    }
    else {
      veloc = (string[2]-'0')*10 + (string[3]-'0');
      radius = (string[9]-'0')*10 + (string[10]-'0');
      v = string[5];
      r = string[7];

     if(v == 0)
        setDirection(0);
      else if(v == 1)
        setDirection(1);
  
      if(radius == 0)
        straight(veloc*155/90);
      else if(radius != 0){
        if(r == 0)
          smoothTurn(90.0-(double)radius, 1, veloc*155/90);
        else if(r == 1)
          smoothTurn(90.0-(double)radius, 0, veloc*155/90);
      }
    }
    clearArray(string, 20);  
  } 
}

/*
 * Function loops to continually follow a line of black tape.
 * Robot continues to proceed in this state until it detects 
 * a different state from the Android (via bluetooth).
 * 
 * Returns: character that represents the next state for the
 *        robot to be in
 */
char follow_line() {
  wheel_reset();
  calibrate();
  curvature = 0;
  line_spd = MAX_PWM/3;

  while (true) {
    if(string[0] == 'L')
      communication(true);
    else if (string[0] != 'F')
      return string[0];
    else {
      int left = checkBlackOrWhite(analogRead(IRPIN_L));
      int right = checkBlackOrWhite(analogRead(IRPIN_R));
  
      // white black --> turn right
      if (left == WHITE && right == BLACK) {
        curvature = max(-MAX_CURV-1, curvature-2);
        smoothTurn(-MAX_CURV/curvature, RIGHT, line_spd);
      }
  
      // black black --> go straight
      else if (left == BLACK && right == BLACK) {
        straight(line_spd);
        curvature = 0;
      }
  
      // black white --> turn left
      else if (left == BLACK && right == WHITE) {
        curvature = min(MAX_CURV+1, curvature+4);
        smoothTurn(MAX_CURV/curvature, LEFT, line_spd);
      }
  
      // white white --> rotate right
      else if (curvature < -4) {
        curvature = -(MAX_CURV+1);
        smoothTurn(0, RIGHT, line_spd);
      }
  
      // white white --> rotate left
      else if (curvature > 8) {
        curvature = MAX_CURV+1;
        smoothTurn(0, LEFT, line_spd);
      }
  
      // white white, line lost, stop
      else {
        halt();
        break;
      }
  
      delay(LINE_DELAY);
    }

    //Check if state of robot has been changed by phone
    if (Serial.available()) {
      state = (char)Serial.read();
      int i = 1;
      while(true) { 
        state = 0;
        if (Serial.available()) {//check if there's any data sent from the remote bluetooth shield
          state = (char)Serial.read();
          string[i] = state;
          i++;
        }
        if(state == string[0])
          break;
      }
    }
  } 
}

/*
 * Function loops to continually check for obstacles using an ultrasonic
 * sensor (moved by a servo motor) and maneuver the robot around any found 
 * obstacles.
 * 
 * Returns: character that represents the next state for the
 *        robot to be in
 */
char avoid_obstacles() {
  wheel_reset();
  ultrasonic_reset();
  avoid_spd = MAX_PWM/2;
  min_spd = MAX_PWM/3;

  while (true) {
    if(string[0] == 'L')
      communication(true);
    else if (string[0] != 'F')
      return string[0];
    else {
      slow_down();
      double left_dist = check_dist(SERVO_LEFT);
      double right_dist = check_dist(SERVO_RIGHT);
  
      //if an object is closer on the left side, turn right to avoid
      if (right_dist > left_dist)
        turn90Deg(RIGHT);
  
      //if an object is closer on the right side, turn left to avoid
      else
        turn90Deg(LEFT);
    }

    //Check if state of robot has been changed by phone
    if (Serial.available()) {
      state = (char)Serial.read();
      int i = 1;
      while(true) { 
        state = 0;
        if (Serial.available()) {//check if there's any data sent from the remote bluetooth shield
          state = (char)Serial.read();
          string[i] = state;
          i++;
        }
        if(state == string[0])
          break;
      }
    }
  }
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
        if((string[i]>>j) & 1)
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

/*
 * Given a character array and lenght of the array,
 * function clears the array to hold all null characters.
 * 
 * Parameter: arr[] - character array to be cleared
 * Parameter: ln - length of the array
 */
void clearArray(char arr[], int ln) {
  for (int i = 0; i < ln; i++) {
    arr[i] = '\0';
  }
}

