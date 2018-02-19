/**
 * CPEN 291 line following SEVERELY BROKEN code, UNTESTED, use and pray
 * We follow the left edge of a black tape on a white background.
 */

#include "wheelZ.h"

//TCRT5000 analog pins

// TODO add docs

#define IRPL A0
#define IRPR A1

#define WHITE 0
#define BLACK 1

#define MAX_K 300
#define DELAY 10
#define START_DELAY 1000
#define MIN_DIFF 100

// TODO add docs
int threshold;
double curvature;
int spd;

void setup() {
  wheel_setup();
  Serial.begin(9600);
  delay(START_DELAY);
  calibrate();
  curvature = 0;
  spd = MAX_PWM/2; // set spd
}

void loop() {
  test_vals();

  int left = checkBlackOrWhite(analogRead(IRPL));
  int right = checkBlackOrWhite(analogRead(IRPR));

  // white black
  if (left == WHITE && right == BLACK) {
    //Serial.println("straight");
    straight(spd);
    curvature = 0;
  }

  // black black
  else if (left == BLACK && right == BLACK) {
    //Serial.println("turn left");
    curvature = min(MAX_K+1, curvature+2);
    smoothTurn(MAX_K/curvature, LEFT, spd);
  }

  // black white
  else if (left == BLACK && right == WHITE) {
    //Serial.println("pivot");
    curvature = MAX_K+1;
    smoothTurn(0, LEFT, spd);
  }

  // white white, right turn
  else if (curvature <= 0) {
    //Serial.println("turn right");
    curvature = max(-MAX_K-1, curvature-8);
    smoothTurn(-MAX_K/curvature, RIGHT, spd);
  }

  else {
    //Serial.println("PANIC!");
    curvature = MAX_K+1;
    rotate(LEFT, spd);
  }
  
  delay(DELAY);

/*
  Serial.print("Sensor 1: ");
  Serial.print(" val: "); Serial.print(val1);
  Serial.println(checkBlackOrWhite(val1) ? " black " : " white ");
  
  Serial.print("Sensor 2: ");
  Serial.print(" val: "); Serial.print(val2);
  Serial.println(checkBlackOrWhite(val2) ? " black " : " white ");
*/
}

void test_vals() {
  int val1 = analogRead(IRPL);
  int val2 = analogRead(IRPR);
  Serial.println("Left " + String(val1) + "   Right " + String(val2));
}

// TODO add docs
bool checkBlackOrWhite(int val){
  return val > threshold;
}

// calibrate light and dark
// Note: assume that we start left of the line
void calibrate() {
  int minval = 1024;
  int maxval = 0;
  rotate(RIGHT, MAX_PWM/5);
  while (maxval-minval < MIN_DIFF) {
    int val1 = analogRead(IRPL);
    int val2 = analogRead(IRPR);
    minval = min(minval, min(val1, val2));
    maxval = max(maxval, max(val1, val2));
    delay(DELAY);
  }
  halt();
  threshold = maxval - (maxval - minval)/3;
}

