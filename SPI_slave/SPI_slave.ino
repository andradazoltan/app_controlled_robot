#define SS 10
#define DATAOUT 11
#define DATAIN 12
#define CLK 13

char command;
int i = 0;
char received[6];
int numBytes;
char data[6];
int requestedBytes;

void setup() {
  Serial.begin(9600);
   init_SPI();
}

void loop() {
  Serial.println(received);
  
  delay(3000);
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
  char got = SPDR;
  received[i] = got;
  i++;
  if(i==6)
    i = 0;
/*
  if(i==0)
    command = got;

  else if (i==1)
    numBytes = (int)got;

  else if (i == 7) {
    i=0;
    
  }

  else
    received[i-2] = got;*/
}

/*
 * Function takes in an integer array of any size
 * and sets all the values in the array to 0.
 * 
 * Parameter: arr - integer array
 * Parameter: width - size of the array
 */
void clearArray(char arr[], int width) {
  for(int index = 0; index < width; index++)
    arr[index] = '\0';
}
