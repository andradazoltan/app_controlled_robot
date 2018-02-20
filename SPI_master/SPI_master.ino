#define SS 10
#define DATAOUT 11
#define DATAIN 12
#define CLK 13
char data[] = "eeLee";
int clr;
void setup() {
  Serial.begin(9600);
  init_SPI();
}

void loop() {
  send_data(data, 5);
  delay(3000);

}


void init_SPI() {
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
  clr = SPDR + SPSR;
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
  /*SPDR = '0';
  
    while (!(SPSR & _BV(SPIF))) ;

  SPDR = numBytes;
 
    while (!(SPSR & _BV(SPIF))) ;
*/
  for (int i = 0; i <= numBytes; i++) {
    SPDR = data[i];
    
    while (!(SPSR & _BV(SPIF))) ;
  }
}

/*
 * Function used to receive data from slave device.
 */
void receive_data(char received[]) {
  //Give signal that data is requested sent
  SPDR = '1';
  while (!(SPSR & (1<<SPIF)));
  int numBytes = SPDR;
  
  for (int i = 0; i <= numBytes; i++) {
    while (!(SPSR & (1<<SPIF)));
    received[i] = SPDR;
  }
}

