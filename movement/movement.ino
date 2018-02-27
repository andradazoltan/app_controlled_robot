/**
 * Testing file
 */

#include "line_following.h"
#include "obstacle_avoiding.h"

void setup() {
  wheel_setup();
  ultrasonic_setup();

  follow_line();
  //avoid_obstacles();
}

void loop() {
  ;
}
