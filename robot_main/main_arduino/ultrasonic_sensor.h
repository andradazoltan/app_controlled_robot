/**
 * This file controls an ultrasonic sensor attached to a servo motor.
 * The main purpose of this file to is read the distance on an ultrasonic
 * sensor at any rotation of the servo motor.
 */

#ifndef ULTRASONIC_H
#define ULTRASONIC_H

#include <Servo.h>

// ultrasonic and servo pins
#define TRIG 13
#define ECHO 12
#define SERVO_PIN 8
#define TSPIN A2

// Servo constants
#define SERVO_LEFT 160
#define SERVO_RIGHT 20
#define SERVO_MID 90
#define SERVO_DELAY 30

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

