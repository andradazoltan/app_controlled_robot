/**
 * Using this file the robot is controlled using photoresistive sensors to
 * track and follow the left edge of black tape on a white background.
 */

#ifndef LINE_FOLLOWING_H
#define LINE_FOLLOWING_H

#include "wheelZ.h"

//Define photoresistive sensor pins
#define IRPIN_L A0
#define IRPIN_R A1

//Define other variables
#define WHITE 0
#define BLACK 1

#define MAX_CURV 300
#define LINE_DELAY 10
#define START_LINE_DELAY 1000
#define MIN_DIFF 100

//Function declarations
void test_vals();
bool checkBlackOrWhite(int val);
void calibrate();

//Private variables
int threshold;
double curvature;
int line_spd;
char state;

/*
 * Function checks if a read value on the photosensor represents
 * a black or white value.
 * 
 * Parameter: val - value read on sensor
 * Returns: true if value is black
 */
bool checkBlackOrWhite(int val) {
  return val > threshold;
}


/*
 * Function calibrates the sensors to the initial starting 
 * position, and the expected values to be read from photosensonrs.
 * NOTE: we assume that we start left of the line
 */
void calibrate() {
  int minval = 1024;
  int maxval = 0;
  rotate(RIGHT, MAX_PWM/3);
  
  while (maxval-minval < MIN_DIFF) {
    int val1 = analogRead(IRPIN_L);
    int val2 = analogRead(IRPIN_R);
    minval = min(minval, min(val1, val2));
    maxval = max(maxval, max(val1, val2));
    delay(LINE_DELAY);
  }
  
  halt();
  threshold = maxval - (maxval - minval)/3;
}

#endif

