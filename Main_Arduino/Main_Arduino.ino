#include <SoftwareSerial.h>

#include "line_following.h"
#include "obstacle_avoiding.h"
#include "ultrasonic_sensor.h"
#include "wheelZ.h"

//Define bluetooth pins
#define bRX 0
#define bTX 1

//Define Serial pins
#define RX 12
#define TX 13
SoftwareSerial comms (RX,TX);

int BUFFER_SIZE = 11;
char string[20] = {'L',' ','5','0',' ','5','0',' ','5','0','L','\0'};
char toreceive[11] = "";
boolean start = true;
char next_state;

void setup() {
  Serial.begin(9600);       //initialize bluetooth
  comms.begin(9600);

  wheel_setup();
  ultrasonic_setup();
}

void loop() { 
  int i = 1;
  char state;
  if(start) {
    start = false;
    while(!Serial.available());
    string[0] = (char)Serial.read();

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

    if(string[0] == 'L') {
      comms.println(string);
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
    string[0] = (char)Serial.read();

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
      comms.println(string);
    else if (string[0] != 'M') {
      halt();
      return string[0];
    }
    else { 
      veloc = (string[2]-'0')*10 + (string[3]-'0');
      radius = (string[9]-'0')*10 + (string[10]-'0');
      v = string[5]-'0';
      r = string[7]-'0';

      if(v == 0)
        setDirection(0);
      else if(v == 1)
        setDirection(1);
  
      if(radius == 0)
        straight(veloc*255/90);
      else if(radius != 0){
        if(r == 0)
          smoothTurn(90.0-(double)radius, 1, veloc*255/90);
        else if(r == 1)
          smoothTurn(90.0-(double)radius, 0, veloc*255/90);
      }
    }   
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
  double curvature = 0;
  int line_spd = MAX_PWM/2;
  char state;
  boolean haltt = false;
  long last_seen = millis();

  while (true) {
    if(string[0] == 'L')
      comms.println(string);
    else if (string[0] != 'F')
      return string[0];
    else {
      int left = checkBlackOrWhite(analogRead(IRPIN_L));
      int right = checkBlackOrWhite(analogRead(IRPIN_R));
  
      // white black --> turn right
      if (left == WHITE && right == BLACK) {
        last_seen = millis();
        curvature = max(-MAX_CURV-1, curvature-2);
        smoothTurn(-MAX_CURV/curvature, RIGHT, line_spd);
      }
  
      // black black --> go straight
      else if (left == BLACK && right == BLACK) {
        last_seen = millis();
        straight(line_spd);
        curvature = 0;
      }
  
      // black white --> turn left
      else if (left == BLACK && right == WHITE) {
        last_seen = millis();
        curvature = min(MAX_CURV+1, curvature+4);
        smoothTurn(MAX_CURV/curvature, LEFT, line_spd);
      }
  
      // white white --> rotate right
      else if (curvature < -4) {
        if (millis() - last_seen > LOST_DELAY) {
          halt();
        } else {
          curvature = -(MAX_CURV+1);
          smoothTurn(0, RIGHT, line_spd);
        }
      }
  
      // white white --> rotate left
      else if (curvature > 8) {
        if (millis() - last_seen > LOST_DELAY) {
          halt();
        } else {
          curvature = MAX_CURV+1;
          smoothTurn(0, LEFT, line_spd);
        }
      }
  
      // white white, line lost, stop
      else {
        halt();
        haltt = true;
        break;
      }
  
      delay(LINE_DELAY);
    }

    if(haltt)
      while(!Serial.available());
    if (Serial.available()) {
      string[0] = (char)Serial.read();

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
  char state;

  while (true) {
    if(string[0] == 'L')
      comms.println(string);
    else if (string[0] != 'O')
      return string[0];
    else {
      char mode_val = slow_down();
      if (mode_val != 'O') {
        return mode_val;
      }

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
      string[0] = (char)Serial.read();
      
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
 * Part of obstacle avoidance
 * Controls the robot to move forward at different speeds until
 * it encounters an object. The closer it gets to the object, the robot
 * slows down until a threshold distance is reached.
 */
char slow_down() {
  while (true) {
    double dist = read_dist();
    if (dist <= STOPPING_DIST) {
      halt();
      break;
    } 
    else if (dist > THRESHOLD_DIST)
      straight(avoid_spd);
    else if (dist < UNIFORM_DIST)
      straight(min_spd);
    else
      straight(min_spd + (avoid_spd - min_spd) * (dist - UNIFORM_DIST) / DIST_DIFF);

    //Check if state of robot has been changed by phone
    if (Serial.available()) {
      string[0] = (char)Serial.read();
      
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

      if (string[0] == 'L')
        comms.println(string);
      else if (string[0] != 'O')
        return string[0];
    }

    delay(AVOID_DELAY);
  }

  return 'O';
}

