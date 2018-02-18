#define SS 10
#define DATAOUT 11
#define DATAIN 12
#define CLK 13

char received[10];
int numBytes;
char data[10];
int requestedBytes;

void setup() {
   init_SPI();
}

void loop() {
  // put your main code here, to run repeatedly:

}

void init_SPI() {
  //Initialize pins used for SPI
  pinMode(DATAIN, INPUT);
  pinMode(DATAOUT, OUTPUT);
  pinMode(CLK, OUTPUT);
  pinMode(SS, OUTPUT);
  
  //Control register programmed such that:
      //interrupt enabled
      //spi enabled
      //LSB sent first
      //this arduino is slave 
      //clk low when idle
      //sample on rising edge of clk
      //use the fastest clock      
  SPCR = B11100000;  

  //Clear any previous data in the registers into garbage variable
  int clr = SPDR + SPSR;
}

/*
 * When master begins transmission, check if it a send or receive request,
 * and follow through accordingly.
 */
ISR (SPI_STC_vect) {
  int command = SPDR;

  switch(command) {
    case 0: //data is about to be sent
      SPDR = 0;
      numBytes = SPDR;
      for(int i = 0; i < numBytes; i++) {
        SPDR = 0;
        received[i] = SPDR;
      }
      break;

    case 1: //data is requested (most recent data sent)
      SPDR = requestedBytes;
      for(int i = 0; i < requestedBytes; i++)
        SPDR = data[i];
  }
}

