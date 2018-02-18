/**
 * CPEN 291 Project 1
 * WHEELZ
 *
 * Usage: 
 * void turn90Deg(int dir);
 * 			Rotates on the spot
 * 			- dir is the direction. (LEFT, RIGHT)
 * void straight(int v);
 * 			Moves straight
 * 			- v is speed. (0 to 255) in PWM
 * void smoothTurn(double radius, int dir, int v);
 * 			Turns in a circular arc.
 * 			- radius is radius of inner wheel (0cm to large)
 * 			- v is speed of outer wheel. (0 to 255) in PWM
 * 			- dir is direction of turn. (LEFT, RIGHT)
 * void setDirection(int dir);
 * 			flips the direction of movement until setDirection is called again
 * 			- dir is the direction of the movement. (FORWARD, BACKWARD)
 * void slowDown(int dist);
 * 			Slows to a stop in front of the obstacle, no effect if dist > 50cm
 * 			- dist is the distance to the obstacle
 */

//Arduino PWM Speed Control:
int E1 = 5;  
int M1 = 4; 
int E2 = 6;                      
int M2 = 7;                 

#define LEFT 1
#define RIGHT 0
#define FORWARD 1
#define BACKWARD 0
#define MAX_PWM 255

#define FIG8 8400
#define SQRTURN 1200

int roboDirection = FORWARD;
int cur_pwm = 0;

void setup() {
    pinMode(M1, OUTPUT);   
    pinMode(M2, OUTPUT); 
    Serial.begin(9600);
}

void loop() {
    straight(0); delay(5000);
    smoothTurn(15, LEFT, MAX_PWM);
    delay(FIG8);
    smoothTurn(15, RIGHT, MAX_PWM);
    delay(FIG8);
    straight(0); delay(5000);
    
    straight(MAX_PWM);
    delay(1000);
    smoothTurn(0, LEFT, MAX_PWM);
    delay(SQRTURN);
    straight(MAX_PWM);
    delay(2000);
    smoothTurn(0, LEFT, MAX_PWM);
    delay(SQRTURN);
    straight(MAX_PWM);
    delay(2000);
    smoothTurn(0, LEFT, MAX_PWM);
    delay(SQRTURN);
    straight(MAX_PWM);
    delay(2000);
    smoothTurn(0, LEFT, MAX_PWM);
    delay(SQRTURN);
    straight(MAX_PWM);
    delay(2000);
    smoothTurn(0, RIGHT, MAX_PWM);
    delay(SQRTURN);
    straight(MAX_PWM);
    delay(2000);
    smoothTurn(0, RIGHT, MAX_PWM);
    delay(SQRTURN);
    straight(MAX_PWM);
    delay(2000);
    smoothTurn(0, RIGHT, MAX_PWM);
    delay(SQRTURN);
    straight(MAX_PWM);
    delay(2000);
    smoothTurn(0, RIGHT, MAX_PWM);
    delay(SQRTURN);
    straight(MAX_PWM);
    delay(1000);
}

//set PWM to control speed of wheels
void setPWM(int velociraptor) {
    cur_pwm = velociraptor;
    analogWrite(E1, velociraptor);
    analogWrite(E2, velociraptor);
}

//turn LEFT or RIGHT by 90 degrees
void turn90Deg(int dir){
    digitalWrite(M1, dir);
    digitalWrite(M2, dir);
    //may need to change values
    setPWM(175);
    delay(1000);
    setPWM(0);
}

// 50cm --> MAX_PWM
// 10cm --> 0
void slowDown(int dist){
  if (dist > 50) {
    setPWM(MAX_PWM);
  } 
  else {
    setPWM(MAX_PWM * (dist-10) / 40);
  }
}

// 0 radius --> (-14)
// max radius --> "infinity"
void smoothTurn(double radius, int dir, int velociraptor){
    double factor = radius/(radius+14);
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

// sets the direction of the robot 
// dir = FORWARD or BACKWARD
void setDirection(int dir){
  roboDirection = dir;
}

//go STRAIGHT
void straight(int velociraptor){
    digitalWrite(M1, !roboDirection);
    digitalWrite(M2, roboDirection);
    setPWM(velociraptor);
}
