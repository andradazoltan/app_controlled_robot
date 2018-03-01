/**
 * CPEN 291 line following SEVERELY BROKEN code, UNTESTED, use and pray
 * We follow the left edge of a black tape on a white background.
 */

#ifndef LINE_FOLLOWING_H
#define LINE_FOLLOWING_H

#include "wheelZ.h"

// TODO add docs

#define IRPIN_L A0
#define IRPIN_R A1

#define WHITE 0
#define BLACK 1

#define MAX_CURV 300
#define LINE_DELAY 10
#define START_LINE_DELAY 1000
#define MIN_DIFF 100

// function declarations
void follow_line();
void test_vals();
bool checkBlackOrWhite(int val);
void calibrate();

// TODO add docs
int threshold;
double curvature;
int line_spd;

void follow_line() {
    wheel_reset();
    calibrate();
    curvature = 0;
    line_spd = MAX_PWM/2;

    while (true) {
        if (Serial.available()) {
            char state = Serial.read();
            while (Serial.available()) {
                Serial.read();
            }
            if (state != 'F') {
                break;
            }
        }

        int left = checkBlackOrWhite(analogRead(IRPIN_L));
        int right = checkBlackOrWhite(analogRead(IRPIN_R));

        // white black --> turn right
        if (left == WHITE && right == BLACK) {
            //Serial.println("turn right");
            curvature = max(-MAX_CURV-1, curvature-2);
            smoothTurn(-MAX_CURV/curvature, RIGHT, line_spd);
        }

        // black black
        else if (left == BLACK && right == BLACK) {
            //Serial.println("straight");
            straight(line_spd);
            curvature = 0;
        }

        // black white
        else if (left == BLACK && right == WHITE) {
            //Serial.println("turn left");
            curvature = min(MAX_CURV+1, curvature+4);
            smoothTurn(MAX_CURV/curvature, LEFT, line_spd);
        }

        // white white, rotate right
        else if (curvature < -4) {
            //Serial.println("PANIC! right");
            curvature = -(MAX_CURV+1);
            smoothTurn(0, RIGHT, line_spd);
            //rotate(RIGHT, line_spd);
        }

        // white white, rotate left
        else if (curvature > 8) {
            //Serial.println("PANIC! left");
            curvature = MAX_CURV+1;
            smoothTurn(0, LEFT, line_spd);
            //rotate(LEFT, line_spd);
        }

        // white white, line lost, stop
        else {
            //Serial.println("LINE LOST");
            halt();
            break;
        }

        delay(LINE_DELAY);
    }
}

// TODO add docs
bool checkBlackOrWhite(int val) {
    return val > threshold;
}

// calibrate light and dark
// Note: assume that we start left of the line
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

