/**
 * Ultrasonic sensor stuff, probably convincingly tested
 * CPEN 291
 */

#ifndef ULTRASONIC_H
#define ULTRASONIC_H

#include <Servo.h>

// ultrasonic and servo pins
#define TRIG 9
#define ECHO 8
#define SERVO_PIN 10
#define TSPIN A2

// Servo constants
#define SERVO_LEFT 180
#define SERVO_RIGHT 0
#define SERVO_MID 90
#define SERVO_DELAY 50

// function declarations
void ultrasonic_setup();
void ultrasonic_reset();

void turn_servo(int);
double read_dist();
double read_temperature();

int servo_pos;
Servo motor;

void ultrasonic_setup() {
    pinMode(TRIG, OUTPUT);
    pinMode(ECHO, INPUT);

    motor.attach(SERVO_PIN);
    servo_pos = motor.read();
    turn_servo(SERVO_MID);
}

void ultrasonic_reset() {
    servo_pos = motor.read();
    turn_servo(SERVO_MID);
}

void turn_servo(int goal) {
    int dir = (servo_pos < goal) ? 1 : -1;
    for ( ; servo_pos != goal; servo_pos += dir) {
        // sweep
        motor.write(servo_pos);
        delay(SERVO_DELAY);
    }
}

/**
 * Ultrasonic sensor
 * reads distance, will block until signal is received,
 * default timeout is 1 second
 */
double read_dist() {
    // calculate distance per microsecond based on temperature
    double distance_per_microsecond = ((331.5 + 0.6 * read_temperature()) * 100) / 2e6;

    digitalWrite(TRIG, LOW);
    delayMicroseconds(5); // ensure clean HIGH
    digitalWrite(TRIG, HIGH);
    delayMicroseconds(10); // pulse
    digitalWrite(TRIG, LOW);
    return pulseIn(ECHO, HIGH) * distance_per_microsecond; // (1/58)
}

/**
 * Function reads LM35 temperature sensor
 * 
 * Returns: value in Celsius of current temperature
 */
double read_temperature() {
    double val = analogRead(TSPIN);
    return (val*(500.0/1024.0));
}

#endif
