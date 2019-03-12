/**
 * This class serves to drive the cart to the origin
 * 
 * WE MUST ADD ANGLE CORRECTION TO THIS IT IS NOT IN IT YET NOTe from jacob 
 * 
 * @author Erdong Luo
 * @author Lara Ghanem
 */
package ca.mcgill.ecse211.localization;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import ca.mcgill.ecse211.odometer.*;
import ca.mcgill.ecse211.localization.*;
import ca.mcgill.ecse211.navigation.*; 
import ca.mcgill.ecse211.finalProject.Main;



public class LightLocalizer {

  // vehicle constants
  public static int ROTATION_SPEED = 80;
  private double SENSOR_LENGTH = 13.5;

  private Odometer odometer;
  private EV3LargeRegulatedMotor leftMotor, rightMotor;
  public Navigation navigation;
  // Instantiate the EV3 Color Sensor
  private static final EV3ColorSensor lightSensor =
      new EV3ColorSensor(LocalEV3.get().getPort("S1"));
  private float sample;

  private SensorMode idColour;

  double[] lineData;



  public LightLocalizer(Odometer odometer, EV3LargeRegulatedMotor leftMotor,
      EV3LargeRegulatedMotor rightMotor) {

    this.odometer = odometer;
    this.leftMotor = leftMotor;
    this.rightMotor = rightMotor;

    idColour = lightSensor.getRedMode(); // set the sensor light to red
    lineData = new double[4];
    navigation = new Navigation(odometer, leftMotor, rightMotor);
  }

  /**
   * This method localizes the robot using the light sensor to precisely move to the right location
   */
  public void localize() {

    int index = 0;
    leftMotor.setSpeed(ROTATION_SPEED);
    rightMotor.setSpeed(ROTATION_SPEED);

    // ensure that we are close to origin before rotating
    moveToOrigin();

    // Scan all four lines and record our angle
    while (index < 4) {

      leftMotor.forward();
      rightMotor.backward();

      sample = fetchSample();

      if (sample < 0.38) {

        lineData[index] = odometer.getXYT()[2];
        Sound.beepSequenceUp();
        index++;
      }
    }


    leftMotor.stop(true);
    rightMotor.stop();

    double deltax, deltay, thetax, thetay;

    // calculate our location from 0 using the calculated angles
    thetay = lineData[3] - lineData[1];
    thetax = lineData[2] - lineData[0];

    deltax = -1 * SENSOR_LENGTH * Math.cos(Math.toRadians(thetay / 2));
    deltay = -1 * SENSOR_LENGTH * Math.cos(Math.toRadians(thetax / 2));

    // travel to origin to correct position
    odometer.setXYT(deltax, deltay, odometer.getXYT()[2]);


    navigation.travelTo(0.0, 0.0);

    leftMotor.setSpeed(ROTATION_SPEED / 2);
    rightMotor.setSpeed(ROTATION_SPEED / 2);

    // if we are not facing 0.0 then turn ourselves so that we are
    // if (odometer.getXYT()[2] <= 359.999 && odometer.getXYT()[2] >= 1.0) {
    Sound.beep();
    leftMotor.rotate(convertAngle(Main.WHEEL_RAD, Main.TRACK, -odometer.getXYT()[2] - 5), true);
    rightMotor.rotate(-convertAngle(Main.WHEEL_RAD, Main.TRACK, -odometer.getXYT()[2] - 5), false);
    // }


    leftMotor.stop(true);
    rightMotor.stop();

  }

  /**
   * This method moves the robot towards the origin
   */
  public void moveToOrigin() {

    navigation.turnTo(Math.PI / 4);

    leftMotor.setSpeed(ROTATION_SPEED);
    rightMotor.setSpeed(ROTATION_SPEED);

    // get sample
    sample = fetchSample();

    // move forward past the origin until light sensor sees the line
    while (sample > 0.38) {
      sample = fetchSample();
      leftMotor.forward();
      rightMotor.forward();

    }
    leftMotor.stop(true);
    rightMotor.stop();
    Sound.beep();

    // Move backwards so our origin is close to origin
    leftMotor.rotate(convertDistance(Main.WHEEL_RAD, -12), true);
    rightMotor.rotate(convertDistance(Main.WHEEL_RAD, -12), false);

  }

  /**
   * This method allows the conversion of a distance to the total rotation of each wheel need to
   * cover that distance.
   * 
   * @param radius
   * @param distance
   * @return
   */
  private static int convertDistance(double radius, double distance) {
    return (int) ((180.0 * distance) / (Math.PI * radius));
  }

  /**
   * This method allows the conversion of a angle to the total rotation of each wheel need to cover
   * that distance.
   * 
   * @param radius
   * @param distance
   * @param angle
   * @return
   */
  private static int convertAngle(double radius, double width, double angle) {
    return convertDistance(radius, Math.PI * width * angle / 360.0);
  }

  /**
   * This method gets the color value of the light sensor
   * 
   */
  private float fetchSample() {
    float[] colorValue = new float[idColour.sampleSize()];
    idColour.fetchSample(colorValue, 0);
    return colorValue[0];
  }

}
