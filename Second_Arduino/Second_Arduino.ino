#include <Wire.h>

//define serial pins
#define commIN 7
#define commOUT 4
#define interruptPin 2
#define BUFFER_SIZE 11

//define LED pins
#define R 6
#define G 3
#define B 5

//define LCD pins
#define RS 8
#define EN 9
#define D4 10
#define D5 11
#define D6 12
#define D7 13

//define commands for LCD
#define CLEAR 0x01
#define RESETHOME 0x02
#define SHIFTLEFT 0x18
#define SHIFTRIGHT 0x1C
#define FIRSTROW 0x80
#define SECONDROW 0xC0

//define accelerometer variables
#define addr         0x1D
#define OUTMSB_X     0x01
#define OUTMSB_Y     0x03
#define OUTMSB_Z     0x05
#define OUTLSB_Z     0x06
#define DATA_CFG     0x0E
#define PL_CFG       0x11
#define CTRLREG1     0x2A
#define CTRLREG2     0x2B

float x_accel, y_accel;

char tosend[BUFFER_SIZE];
char toreceive[BUFFER_SIZE];
boolean newD = false;

char xaccel[8]; 
char yaccel[8];

void setup() {
  Wire.begin();
  Serial.begin(9600);

  //Initialize serial pins
  pinMode(commIN, INPUT);
  pinMode(commOUT, OUTPUT);
  pinMode(interruptPin, INPUT_PULLUP);
  attachInterrupt(digitalPinToInterrupt(interruptPin), communication, RISING);

  //Initialize LED pins
  pinMode (R, OUTPUT);
  pinMode (G, OUTPUT);
  pinMode (B, OUTPUT);

  //Initialize LCD pins 
  DDRB |= B11111111;  

  accel_setup();
  lcd_init();
}

void loop() {
  accel_readX();
  accel_readY();
  convert(xaccel, x_accel<0, abs(x_accel));
  convert(yaccel, y_accel<0, abs(y_accel));  
Serial.println(x_accel);
Serial.println(y_accel);
  to_write(xaccel);
  lcd_command(SECONDROW);
  to_write(yaccel);    

  if(newD == true)
    LED_control();

  delay(1000);
  lcdclear();
}


//LED CONTROL --------------------------------------------------------
/*
 * Function is called if new LED control data is received from the first
 * Arduino. The LED PWM pins are then set based on the received values.
 */
void LED_control () {
  int r,g,b;
  r = (toreceive[2]-'0')*10 + (toreceive[3]-'0');
  g = (toreceive[5]-'0')*10 + (toreceive[6]-'0');
  b = (toreceive[8]-'0')*10 + (toreceive[9]-'0');


  analogWrite(R, r);
  analogWrite(G, g);
  analogWrite(B, b);
  newD = false;
}

//ACCELEROMETER FUNCTIONS -------------------------------------------
/*
 * Function initializes the I2C communication with the accelerometer.
 * The function also sets the accelerometer to:
 *          -read a range up to 4Gs of acceleration
 *          -turn orientation configuration
 */
void accel_setup() {
  accel_writeReg(CTRLREG2, 0x40);
  
  accel_writeReg(DATA_CFG, 0b01);

  accel_writeReg(PL_CFG, 0x40);
  accel_writeReg(CTRLREG1, 0x01|0x04);
}

/*
 * Function writes to a register on the accelerometer chip.
 * 
 * Paramter: reg - the register number to write to
 * Parameter: cmd - the command to write to the register
 */
void accel_writeReg (int reg, int cmd) {
  Wire.beginTransmission(addr);
  Wire.write(reg);
  Wire.write(cmd);
  Wire.endTransmission();
}

/*
 * Function reads a value from a register on the accelerometer chip.
 * 
 * Paramter: reg - the register to read from
 * Returns: the value read
 */
int accel_readReg (int reg) {
  Wire.beginTransmission(addr);
  Wire.write(reg);
  Wire.endTransmission(false);

  Wire.requestFrom(addr, 1);

  if(Wire.available())
    return Wire.read();
}

/*
 * Function reads the x-acceleration and converts it into a value
 * that is in m/s^2.
 *    -Max value equal to 4*9.81 (or 4G)
 */
void accel_readX() {
  int16_t x;
  Wire.beginTransmission(addr);
  Wire.write(OUTMSB_X);
  Wire.endTransmission(false);

  Wire.requestFrom(addr, 6);
  x = Wire.read(); x <<= 8; x |= Wire.read(); x >>= 2;

  x_accel = (float)x/2058*9.80665F;
}

/*
 * Function reads the y-acceleration and converts it into a value that
 * is in m/s^2.
 *    -Max value equal to 4*9.81 (or 4G)
 */
void accel_readY() {
  int16_t y;
  Wire.beginTransmission(addr);
  Wire.write(OUTMSB_Y);
  Wire.endTransmission(false);

  Wire.requestFrom(addr, 6);
  y = Wire.read(); y <<= 8; y |= Wire.read(); y >>= 2;

  y_accel = (float)y/2048*9.80665F;
}

/*
 * Funciton takes in a float and converts it to a char array;
 * only considers the float up to 3 significant figures.
 * 
 * Parameter: converted - array to store the converted values in
 * Parameter: value - the float value to convert
 */
void convert(char converted[], boolean neg, float value) {
  char digits[6];
  int len = 0;
  int rounded = (int)(value *1000);
  
  while (rounded > 0) {
    if(len == 3)
      digits[len++] = '.';
    else {
      digits[len++] = rounded % 10 + '0';
      value /= 10;
    }
  }
  for (int i = 0; i < len; i++) {
    converted[i] = digits[len-1-i];
  }
  converted[len] = '\0';

  if(neg) {
    for(int i = len; i > 0; i++) {
      converted[i] = converted[i-1];
    }
    converted[0] = '-';
  }
}

//SERIAL COMMUNICATION ISR -------------------------------------------
/*
 * Interrupt service routine occurs when the master devices wants
 * to communicate with the slave. The master will provide a command
 * about whether it wants data sent or it wants to send data.
 */
void communication () {
  digitalWrite(commOUT, HIGH);

  boolean tx = digitalRead(commIN);
  delayMicroseconds(100); 
  
  //SEND TO MASTER
  if(tx) {
    for(int i = 0; i < BUFFER_SIZE; i++) {
      for(int j = 0; j < 8; j++) {
        if((tosend[i]>>j) & 1)
          digitalWrite(commOUT, HIGH);
        else
          digitalWrite(commOUT, LOW);
        delayMicroseconds(100);
      }
      delayMicroseconds(100);
    } 
  }
  
  //RECEIVE FROM MASTER
  else{
    char temp = 0;
    for(int i = 0; i < BUFFER_SIZE; i++) {
      for(int j = 0; j < 8; j++) {
        delayMicroseconds(100);
        temp += digitalRead(commIN)<<j;
      }
      toreceive[i] = temp;
      temp = 0;
      delayMicroseconds(100);
    }
    newD = true;
  } 
  digitalWrite(commOUT, LOW);
}

//HIGH LEVEL LCD FUNCTIONS -------------------------------------------
/*
 * Function clears what is currently on the LCD display
 * and resets cursor and data RAM back to 0.
 */
void lcdclear() {
  lcd_command(CLEAR);
  delayMicroseconds(2000);
  lcd_command(RESETHOME);
  delayMicroseconds(2000);
}

/*
 * Function that calls on the LCD to shift the 
 * text currently on display on the LCD.
 * 
 * Parameter: times - number of times to shift the text
 * Parameter: directions - 0 if left, 1 if right
 */
void shift_banner(int times, boolean directions) {
  for(int i = 0; i < times; i++) {
    if(directions)
      lcd_command(SHIFTRIGHT);
    else
      lcd_command(SHIFTLEFT);
    delay(500);
  }
}

/*
 * Function that sends a character (1 byte) at a time
 * to the lcd_write function to be printed on the screen.
 * 
 * Parameter: data[] - string to be printed on the LCD
 */
void to_write(char data[]) {
  for(int index = 0; data[index] != '\0'; index++) {
    lcd_write(data[index]);
  }
}

//LCD FUNCTIONS -------------------------------------------------------
/*
 * Function initializes the LCD display at startup, and 
 * prints the initial message:
 *                            CPEN291
 *                            Team L2B-7A
 */
void lcd_init(){
  //Initialize all output pins to low
  PORTD &= B11000000;

  //Begin in 8-bit mode; wait for power to be sufficient
  delay(50);
  lcd_command(0x3);
  
  //Try to set 4-bit mode
  delayMicroseconds(4500);
  lcd_command(0x3);

  //Try to set 4-bit mode again
  lcd_command(0x3);

  //Finally set 4-bit mode
  lcd_command(0x2);

  //Set number of lines and font size
  lcd_command(0x28);

  //Display clear
  lcd_command(0x01);

  //Display on, cursor blinking off
  lcd_command(0xc);

  //Increment cursor
  lcd_command(0x06);

  //Print initial banner messages
  delayMicroseconds(1000);
  char opening[] = "  TURTLE";
  to_write(opening);
  
  //Cursor blinking off
  lcd_command(0xc);

  delay(2000);
  lcdclear();
}

/*
 * Used to send data to the LCD; data sent in two waves
 * due to LCD being in 4-bit mode. Can only send one character
 * at a time.
 * 
 * Parameter: data - character to be printed on the display
 */
void lcd_write(char data) {
  char higherbits = data >> 0x04;
  char lowerbits = data - (higherbits << 0x04);

  //Send higher 4 bits over data bus
  PORTB &= B11000011;                 //Clear previous data
  PORTB |= higherbits << 2;

  //Send HIGH on RS pin to select command register
  PORTB |= 1;

  //Trigger rising edge on EN pin
  PORTB |= (1 << 1);
  delayMicroseconds(100);
  PORTB &= ~(1 << 1);

  //Send lower 4 bits over data bus
  PORTB &= B11000011;                 //Clear previous data
  PORTB |= lowerbits << 2;

  //Trigger rising edge on EN pin
  PORTB |= (1 << 1);
  delayMicroseconds(100);
  PORTB &= ~(1 << 1);
}

/*
 * Used to send an command to the LCD; command sent in two waves
 * due to LCD being in 4-bit mode.
 * 
 * Parameter: cmd - value of the command to be executed
 */
void lcd_command(byte cmd){
  int higherbits = cmd >> 4;
  int lowerbits = cmd - (higherbits << 4);
  
  //Send higher 4 bits over data bus
  PORTB &= B11000011;                 //Clear previous data
  PORTB |= higherbits << 2;

  //Send LOW on RS pin to select command register
  PORTB &= ~(1);

  //Trigger rising edge on EN pin
  PORTB |= (1 << 1);
  delayMicroseconds(100);
  PORTB &= ~(1 << 1);
  
  //Send lower 4 bits over data bus
  PORTB &= B11000011;                 //Clear previous data
  PORTB |= lowerbits << 2;

  //Trigger rising edge on EN pin
  PORTB |= (1 << 1);
  delayMicroseconds(100);
  PORTB &= ~(1 << 1);
}
