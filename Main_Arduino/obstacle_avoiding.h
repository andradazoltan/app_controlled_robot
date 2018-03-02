/**
 * Using this file the robot is controlled using the ultrasonic sensor to
 * navigate around a series of obstacles without hitting them
 */

#ifndef OBSTACLE_AVOIDING_H
#define OBSTACLE_AVOIDING_H

#include "wheelZ.h"
#include "ultrasonic_sensor.h"

//define robot dimension (in cm)
#define ROBOT_R 9.5

//define distance variables (in cm)
#define STOPPING_DIST 5
#define UNIFORM_DIST 12
#define THRESHOLD_DIST 50
#define DIST_DIFF (THRESHOLD_DIST - UNIFORM_DIST)

//define miscellaneous variables
#define AVOID_DELAY 20
#define EPS 0.1

//function declarations
char avoid_obstacles();
void slow_down();
double check_dist(int pos);
void turn90(int dir);

int avoid_spd, min_spd;

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
    slow_down();
    double left_dist = check_dist(SERVO_LEFT);
    double right_dist = check_dist(SERVO_RIGHT);

    //if an object is closer on the left side, turn right to avoid
    if (right_dist > left_dist)
      turn90(RIGHT, right_dist);

    //if an object is closer on the right side, turn left to avoid
    else
      turn90(LEFT, left_dist);

    //Check if state of robot has been changed by phone
    if (Serial.available()) {
      char state = Serial.read();
      while (Serial.available())        //continue reading until the serial buffer clears
        Serial.read();
      if (state != 'O')
        return state;
    }
  }
}


/*
 * Controls the robot to move forward at different speeds until
 * it encounters an object. The closer it gets to the object, the robot
 * slows down until a threshold distance is reached.
 */
void slow_down() {
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

    delay(AVOID_DELAY);
  }
}


/* 
 * Checks the distance read on the ultrasonic sensor at a specified
 * position of the servo motor.
 * 
 * Parameter: pos - angle in degrees of the servo motor
 * Returns: distance read on ultrasonic sensor
 */
double check_dist(int pos) {
  turn_servo(pos);
  double dist = read_dist();
  delay(AVOID_DELAY);
  
  turn_servo(SERVO_MID);
  return dist;
}


/*
 * Turn robot 90 degrees left or right to avoid obstacle, turn servo motor
 * opposite direction to check at what point the robot has avoided the 
 * obstacle. If at any point robot can move without hitting the obstacle,
 * stop turning and return back to main roop.
 * 
 * Parameter: dir - direction to turn (left or right) 
 */
void turn90(int dir) {
  double goal_dist = read_dist() + ROBOT_R;
  turn_servo(180 - dir);
  rotate(dir/180, min_spd);
  while (abs(read_dist() - goal_dist) > EPS)
    delay(AVOID_DELAY);
  
  halt();
  turn_servo(SERVO_MID);
}

#endif

