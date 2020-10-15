#include <Servo.h>


const uint8_t VescOutputPin = 3;

const uint8_t PotentiometerPin = A0;

String BluetoothCommand;

Servo esc;


void setup() {
  Serial.begin(9600);

  esc.attach(VescOutputPin);

  esc.writeMicroseconds(1500);

}

void loop() {
  if(Serial.available()) //to judge whether the serial port receives the data.
  {
      BluetoothCommand = String(Serial.read());  //reading (Bluetooth) data of serial port,giving the value of val;
      Serial.println(BluetoothCommand);
  } 
  
  double potValue = analogRead(PotentiometerPin);
  double mappedValue = map(potValue, 0 , 1023, 1000, 2000);
  esc.writeMicroseconds(mappedValue);

}
