/**
 * CPEN 291 obstacle avoiding SEVERELY BROKEN code, UNTESTED, use and pray
 * We follow the left edge of a black tape on a white background.
 */

#include "wheelZ.h"

// TODO add docs

// robot dimensions in centimeters
#define ROBOT_R 9.5

#define EPS 1

//units are cm
#define STOPPING_DIST 5
#define UNIFORM_DIST 12
#define THRESHOLD_DIST 50
#define DIST_DIFF (THRESHOLD_DIST - UNIFORM_DIST)

//units are PWM
#define MIN_SPEED 20
#define FULL_SPEED (MAX_PWM/2)

// delay
#define SHORT_DELAY 20

#define TURN_DELAY 1243

void setup() {
    wheel_setup();
    ultrasonic_setup();
    Serial.begin(9600);
}

void loop() {
    slow_down();
    double left_dist = check_dist(LEFT);
    double right_dist = check_dist(RIGHT);
    if (right_dist > left_dist);
        turn90(RIGHT, right_dist);
    } else {
        turn90(LEFT, left_dist);
    }
}

void slow_down() {
    while (true) {
        double dist = read_dist();
        if (dist <= STOPPING_DIST) {
            halt();
            break;
        } else if (dist > THRESHOLD_DIST) {
            straight(FULL_SPEED);
        } else if (dist < UNIFORM_DIST) {
            straight(MIN_SPEED);
        } else {
            straight(MIN_SPEED + (FULL_SPEED - MIN_SPEED) * (dist - UNIFORM_DIST) / DIST_DIFF);
        }
        delay(SHORT_DELAY);
    }
}

double check_dist(int pos) {
    turn_servo(pos);
    double dist = read_dist();
    delay(SHORT_DELAY);
    turn_servo(SERVO_MID);
    return dist;
}

void turn90(int dir, double end_dist) {
    double goal_dist = read_dist() + ROBOT_R;
    turn_servo(180 - dir);
    rotate(dir/180, FULL_SPEED/2);
    while (abs(read_dist() - goal_dist) > EPS) {
        delay(SHORT_DELAY);
    }
    halt();

    turn_servo(SERVO_MID);
    if (abs((read_dist() - ROBOT_R) - end_dist) > 1) {
        Serial.println("PANIC " + String(read_dist() + ROBOT_R) 
                + " but expected " + String(end_dist));
    }
}
