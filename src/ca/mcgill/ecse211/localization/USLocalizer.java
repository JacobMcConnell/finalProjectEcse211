/**
 * This class serves to drive the robot to the 0 degreee axis
 * 
 * @author Erdong Luo
 * @author Lara Ghanem
 */
package ca.mcgill.ecse211.localization;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;
import ca.mcgill.ecse211.navigation.*;
import ca.mcgill.ecse211.odometer.*;
import ca.mcgill.ecse211.finalProject.Main;

public class USLocalizer {

  // vehicle constants
  public static int ROTATION_SPEED = 100;
  private double deltaTheta;

  private Odometer odometer;
  private float[] usData;
  private EV3LargeRegulatedMotor leftMotor, rightMotor;

  private SampleProvider usDistance;

  // Create a navigation
  public Navigation navigation;

  private double d_falling = 30.00;

  private double k_falling = 3;


  /**
   * Constructor to initialize variables
   * 
   * @param Odometer
   * @param EV3LargeRegulatedMotor
   * @param EV3LargeRegulatedMotor
   * @param boolean
   * @param SampleProvider
   */
  public USLocalizer(Odometer odo, EV3LargeRegulatedMotor leftMotor,
      EV3LargeRegulatedMotor rightMotor, SampleProvider usDistance) {
    this.odometer = odo;
    this.leftMotor = leftMotor;
    this.rightMotor = rightMotor;



    this.usDistance = usDistance;
    this.usData = new float[usDistance.sampleSize()];
    navigation = new Navigation(odometer, leftMotor, rightMotor);

    leftMotor.setSpeed(ROTATION_SPEED);
    rightMotor.setSpeed(ROTATION_SPEED);
  }



  /**
   * A method to localize position using the falling edge
   * 
   */
  public void localizeFallingEdge() {

    double angleA, angleB, turningAngle;

    // Rotate to open space
    while (fetchUS() < d_falling + k_falling) {
      leftMotor.backward();
      rightMotor.forward();
    }
    // Rotate to the first wall
    while (fetchUS() > d_falling) {
      leftMotor.backward();
      rightMotor.forward();
    }
    Sound.buzz();
    // record angle
    angleA = odometer.getXYT()[2];

    // rotate out of the wall range
    while (fetchUS() < d_falling + k_falling) {
      leftMotor.forward();
      rightMotor.backward();
    }

    // rotate to the second wall
    while (fetchUS() > d_falling) {
      leftMotor.forward();
      rightMotor.backward();
    }
    Sound.buzz();
    angleB = odometer.getXYT()[2];

    leftMotor.stop(true);
    rightMotor.stop();

    // calculate angle of rotation
    if (angleA < angleB) {
      deltaTheta = 45 - (angleA + angleB) / 2;

    } else if (angleA > angleB) {
      deltaTheta = 225 - (angleA + angleB) / 2;
    }

    turningAngle = deltaTheta + odometer.getXYT()[2];

    // rotate robot to the theta = 0.0 and we account for small error
    leftMotor.rotate(-convertAngle(Main.WHEEL_RAD, Main.TRACK, turningAngle), true);
    rightMotor.rotate(convertAngle(Main.WHEEL_RAD, Main.TRACK, turningAngle), false);

    // set odometer to theta = 0
    odometer.setXYT(0.0, 0.0, 0.0);

  }

  /**
   * A method to get the distance from our sensor
   * 
   * @return
   */
  private int fetchUS() {
    usDistance.fetchSample(usData, 0);
    return (int) (usData[0] * 100);
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

}
