package ca.mcgill.ecse211.finalProject;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class Main {
//Motor Objects, and Robot related parameters
 private static final EV3LargeRegulatedMotor leftMotor =
     new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
 private static final EV3LargeRegulatedMotor rightMotor =
     new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

 private static final TextLCD lcd = LocalEV3.get().getTextLCD();
 private static final Port usPort = LocalEV3.get().getPort("S2");

 private static final EV3ColorSensor colorSamplerSensor =
     new EV3ColorSensor(LocalEV3.get().getPort("S3"));

 // Set vehicle constants
 public static final double WHEEL_RAD = 2.1;
 public static final double TRACK = 16.4; // 9.8

 public static final double TILE_SIZE = 30.48;

 public static final int SC = 0;



 public static final int CAN_TO_SENSOR = 5;
 private static final double DISTANCE_CAN_OUT_OF_WAY = 12;



 static SensorModes ultrasonicSensor = new EV3UltrasonicSensor(usPort);
 static SampleProvider usDistance = ultrasonicSensor.getMode("Distance");


  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }

}
