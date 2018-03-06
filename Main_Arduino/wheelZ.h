/**
 * This file is used to control two motors connected to a 2A
 * Arduino motor shield. 
 */

#ifndef WHEELZ_H
#define WHEELZ_H

// Arduino Motor pins
#define E1 5           //Left wheel
#define M1 4           //Left wheel
#define E2 6           //Right wheel           
#define M2 7           //Right wheel

// Hall sensor pins
#define RHALL 2
#define LHALL 3

// Direction and speed constants
#define LEFT 1
#define RIGHT 0
#define FORWARD 1
#define BACKWARD 0
#define MAX_PWM 255

// Function declarations
void wheel_setup();
void wheel_reset();
void setPWM(int velociraptor);
void rotate(int dir, int val);
void turn90Deg(int dir);
void smoothTurn(double radius, int dir, int velociraptor);
void setDirection(int dir);
void straight(int velociraptor);
void halt();
void calibrateMotor();

// Private variables
int roboDirection; 
int currentSpeed;
double factor = 1.0;

/*
 * Initialize pins and direction of robot.
 */
void wheel_setup() {
  int timeTracker = 0;
  int timesR = 0;
  int timesL = 0;
	pinMode(M1, OUTPUT);  
	pinMode(M2, OUTPUT);
	roboDirection = FORWARD;

  pinMode(RHALL, INPUT);
  pinMode(LHALL, INPUT);

  straight(MAX_PWM/2);
  while(timeTracker <= 100) {
    if(digitalRead(RHALL) == HIGH)
      timesR++;
    if(digitalRead(LHALL) == HIGH)
      timesL++;

    delay(100);
    timeTracker++;
  }

  factor = (double)timesR/timesL;
  halt();
}

/*
 * Reset the direction of the robot and stop robot
 * from moving.
 */
void wheel_reset() {
	roboDirection = FORWARD;
	halt();
}

//set PWM to control speed of wheels
void setPWM(int velociraptor) {
  currentSpeed = velociraptor;
	analogWrite(E1, velociraptor*factor);
	analogWrite(E2, velociraptor);
}

/*
 * Set direction of robot.
 * Paramter: dir - 1 to move FORWARD, 0 to move BACKWARD
 */
void setDirection(int dir) {
  roboDirection = dir;
}

/*
 * Robot is directed to rotate on the spot. Robot will pivot
 * on its center of gravity.
 * 
 * Parameter: dir - 1 for turn LEFT, 0 for turn RIGHT
 * Parameter: val - speed in PWM (0 to 255)
 */
void rotate(int dir, int val) {
  digitalWrite(M1, dir);
  digitalWrite(M2, dir);
  setPWM(val);
}

/*
 *  Robot directed to rotate on the spot by 90 degrees
 *  Parameter: dir - 1 for turn LEFT, 0 for turn RIGHT
 */
void turn90Deg(int dir){ 
  boolean currentHstate = digitalRead(LHALL);
 
	digitalWrite(M1, dir);
	digitalWrite(M2, dir);
  setPWM(175); 
	delay(300);
	setPWM(0); 
}

/*
 *  Robot directed to turn in a circular arc of specified radius at
 *  a certain speed.
 *  
 *  Paramter: radius - radius of inner wheel in cm
 *  Parameter: dir - 1 for turn LEFT, 0 for turn RIGHT
 *  Paramter: velociraptor - speed in PWM (0 to 255)
 */
void smoothTurn(double radius, int dir, int velociraptor){
    double factorR = radius/(radius+14);
    digitalWrite(M1, !roboDirection);
    digitalWrite(M2, roboDirection);
    if (dir == LEFT) {
        analogWrite(E1, factorR*velociraptor);
        analogWrite(E2, velociraptor);
    } 
    else {
        analogWrite(E2, factorR*velociraptor);
        analogWrite(E1, velociraptor);
    }
}

/*
 * Robot directed to move straight at a certain speed.
 * Paramter: velociraptor - speed in PWM (0 to 255)
 */
void straight(int velociraptor) {
  digitalWrite(M1, !roboDirection);
  digitalWrite(M2, roboDirection);
  setPWM(velociraptor);
}

/*
 * Stop the robot in place from moving.
 */
void halt() {
  setPWM(0);
}

#endif

