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
char follow_line();
void test_vals();
bool checkBlackOrWhite(int val);
void calibrate();

int threshold;
double curvature;
int line_spd;

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

    //Check if state of robot has been changed by phone
    if (Serial.available()) {
      char state = Serial.read();
      while (Serial.available())        //continue reading until the serial buffer clears
          Serial.read();
      if (state != 'F')
        return state;
    }
  } 
}


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

