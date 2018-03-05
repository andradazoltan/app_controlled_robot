/**
 * This file is used to control two motors connected to a 2A
 * Arduino motor shield. 
 */

#ifndef WHEELZ_H
#define WHEELZ_H

// Arduino Motor pins
#define E1 5
#define M1 4
#define E2 6                      
#define M2 7                 

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

// Private variables
int roboDirection; 

/*
 * Initialize pins and direction of robot.
 */
void wheel_setup() {
	pinMode(M1, OUTPUT);  
	pinMode(M2, OUTPUT);
  pinMode(E1, OUTPUT);
  pinMode(E2, OUTPUT);
	roboDirection = FORWARD;
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
	analogWrite(E1, velociraptor);
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
	digitalWrite(M1, dir);
	digitalWrite(M2, dir);
	//may need to change values
	setPWM(175);
	delay(600);
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
    double factor = radius/(radius+20);
    digitalWrite(M1, !roboDirection);
    digitalWrite(M2, roboDirection);
    if (dir == LEFT) {
        analogWrite(E1, factor*velociraptor);
        analogWrite(E2, velociraptor);
    } 
    else {
        analogWrite(E2, factor*velociraptor);
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

