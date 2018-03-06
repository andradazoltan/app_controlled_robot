/**
 * Using this file the robot is controlled using the ultrasonic sensor to
 * navigate around a series of obstacles without hitting them
 */

#ifndef OBSTACLE_AVOIDING_H
#define OBSTACLE_AVOIDING_H

#include "wheelZ.h"
#include "ultrasonic_sensor.h"

//define robot radius (in cm)
#define ROBOT_R 9.5

//define distance variables (in cm)
#define STOPPING_DIST 4
#define UNIFORM_DIST 15
#define THRESHOLD_DIST 50
#define DIST_DIFF (THRESHOLD_DIST - UNIFORM_DIST)

//define miscellaneous variables
#define AVOID_DELAY 5

//function declarations
void slow_down();
double check_dist(int pos);

//private variables
int avoid_spd = MAX_PWM/2;
int min_spd = MAX_PWM/3;

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

#endif

