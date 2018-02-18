#define SS 10
#define DATAOUT 11
#define DATAIN 12
#define CLK 13

void setup() {
  init_SPI();
}

void loop() {
  // put your main code here, to run repeatedly:

}


void init_SPI() {
  noInterrupts();
  
  //Initialize pins used for SPI
  pinMode(DATAIN, INPUT);
  pinMode(DATAOUT, OUTPUT);
  pinMode(CLK, OUTPUT);
  pinMode(SS, OUTPUT);
  
  //Control register programmed such that:
      //interrupt disabled
      //spi enabled
      //LSB sent first
      //this arduino is master 
      //clk low when idle
      //sample on rising edge of clk
      //use the fastest clock      
  SPCR = B01110000;  

  //Clear any previous data in the registers into garbage variable
  int clr = SPDR + SPSR;
}

/*
 * Function used to send data from master to slave device.
 * 
 * Parameter: data - character array of data to send, first
 *                  character is the size of the data block
 * Parameter: numBytes - number of charcters to send + 1 for size char
 */
void send_data(char data[], int numBytes) {
  //Give signal that data is being sent
  SPDR = 0;
  while (!(SPSR & (1<<SPIF)));
  
  for (int i = 0; i < numBytes; i++) {
    SPDR = data[i];
    while (!(SPSR & (1<<SPIF)));
  }
}

/*
 * Function used to receive data from slave device.
 */
void receive_data(char received[]) {
  //Give signal that data is requested sent
  SPDR = 1;
  while (!(SPSR & (1<<SPIF)));
  int numBytes = SPDR;
  
  for (int i = 0; i < numBytes; i++) {
    SPDR = 1;
    while (!(SPSR & (1<<SPIF)));
    received[i] = SPDR;
  }
}

