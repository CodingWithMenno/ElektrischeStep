#include <Servo.h>


const uint8_t VescOutputPin = 3;
const uint8_t PotentiometerPin = A0;


String BluetoothCommand;
Servo esc;


bool electronicBrakeOn = false; //eloctronic braking controlled by the esc.
double power = 0;

bool connectedToMobile = false;


void setup() {
  Serial.begin(9600);

  esc.attach(VescOutputPin);

  esc.writeMicroseconds(1500);

}

void loop() {
  if(Serial.available()) //to judge whether the serial port receives the data.
  {
      BluetoothCommand = String(Serial.readString());  //reading bluetooth data.

      if (BluetoothCommand == "CONNECT") {
        Serial.println("OKE");
      }

      if (BluetoothCommand == "START") {
        connectedToMobile = true;
      }
  } 
  
  double potValue = analogRead(PotentiometerPin); //reading the gasthrottle.
  double oldPower = power;
  double mappedValue = 1000;
  
  if(electronicBrakeOn) {
    mappedValue = map(potValue, 310 , 865, 1000, 2000);
    power = map(mappedValue, 1000, 2000, 0, 100);
  } else {
    mappedValue = map(potValue, 310 , 865, 1250, 2000);
    power = map(mappedValue, 1250, 2000, 0, 100);
  }

  if (connectedToMobile) {
    double difference = oldPower - power;
    if (difference > 0.999 || difference < -0.999) { //if the power has changed, then write the new power to bluetooth
      Serial.write((int) power);
    }
  }
  
  esc.writeMicroseconds(mappedValue);
}
