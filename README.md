# App Controlled Robot

The purpose of this project was to construct an Arduino-based robot capabale of three functions:

* Autonomous line following
* Autonomous obstacle avoiding
* Manual app-controlled movement

## Autonomous Line Following Mode
To enable line following of inch wide black tape, 2 reflective optical sensors were placed underneath the robot. This enabled us to get two bits of information, for a total of four scenarios. The robot is commanded to move as follows depending on the reading of the sensors:

* **Dark/Dark:** both sensors are detecting the tape, move straight
* **Dark/Light:** right sensor is no longer detecting tape, turn left
* **Light/Dark:** left sensor is no longer detecting tape, turn right
* **Light/Light:** line is no longer detected, stop robot

## Autonomous Obstacle Avoiding
An ultrasonic sensor was used for the obstacle avoidance feature, with distance readings executed every 20ms. The robot begins to slow down when it detects an obstacle 50 cm away, until it is 1cm away and stops. At this point, a servo motor is used to rotate the ultrasonic sensor left and right. The robot then turns 90deg in the direction that has the furthest obstacle.

## Manual App-Controlled
By using the internal gyroscope in an Android phone, we enabled manual control of the robot by tilting the phone different directions. The phone was made to talk to the robot via a bluetooth module, and periodically transmitted x- and y- coordinates that the robot converted into motor controls.
