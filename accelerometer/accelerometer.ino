#include <Wire.h>

#define addr 0x1D
#define OUTMSB_X     0x01
#define OUTMSB_Y     0x03
#define OUTMSB_Z     0x05
#define OUTLSB_Z     0x06
#define DATA_CFG     0x0E
#define PL_CFG       0x11
#define CTRLREG1     0x2A
#define CTRLREG2     0x2B
#define CTRLREG4     0x2D
#define CTRLREG5     0x2E

#define RESET 0x40
#define HIGHRES 0x2

int16_t x,y,z;

void setup() {
  Wire.begin();
  Serial.begin(9600);

  accel_setup();
}

void loop() {
  accel_readx();
  accel_ready();
  accel_readz();
  Serial.print("x: ");
  Serial.println((float)x/2048*9.80665F);
  Serial.print("y: ");
  Serial.println((float)y/2048*9.80665F);
  Serial.print("z: ");
  Serial.println((float)z/2048*9.80665F);
  delay(1000);
}

void accel_setup() {
  accel_writeReg(CTRLREG2, 0x40);
  while(accel_readReg(CTRLREG2) & 0x40);
  
  accel_writeReg(DATA_CFG, 0b01);

  accel_writeReg(PL_CFG, 0x40);
  accel_writeReg(CTRLREG1, 0x01|0x04);
}

void accel_writeReg (int reg, int cmd) {
  Wire.beginTransmission(addr);
  Wire.write(reg);
  Wire.write(cmd);
  Wire.endTransmission();
}

int accel_readReg (int reg) {
  Wire.beginTransmission(addr);
  Wire.write(reg);
  Wire.endTransmission(false);

  Wire.requestFrom(addr, 1);

  if(Wire.available())
    return Wire.read();
}

void accel_readx() {
  Wire.beginTransmission(addr);
  Wire.write(OUTMSB_X);
  Wire.endTransmission(false);

  Wire.requestFrom(addr, 6);
  x = Wire.read(); x <<= 8; x |= Wire.read(); x >>= 2;
}

void accel_ready() {
  Wire.beginTransmission(addr);
  Wire.write(OUTMSB_Y);
  Wire.endTransmission(false);

  Wire.requestFrom(addr, 6);
  y = Wire.read(); y <<= 8; y |= Wire.read(); y >>= 2;
}


void accel_readz() {
  Wire.beginTransmission(addr);
  Wire.write(OUTMSB_Z);
  Wire.endTransmission(false);

  Wire.requestFrom(addr, 6);
  z = Wire.read(); z <<= 8; z |= Wire.read(); z >>= 2;


  
}

