
package ca.mcgill.ecse211.navigation;
/**
 * Navigation.java This class is used for navigating the robot (without obstacles)
 * 
 * NOTE FROM JACOB: this needs to be updated so that it can handle obstacles well 
 * 
 * @author Erdong Luo
 * @author Lara Ghanem
 */

import ca.mcgill.ecse211.finalProject.Main;
import ca.mcgill.ecse211.finalProject.Main;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometerExceptions;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigation extends Thread {

  private Odometer odometer;
  private EV3LargeRegulatedMotor leftMotor;
  private EV3LargeRegulatedMotor rightMotor;

  private double deltax;
  private double deltay;

  // current location of the vehicle
  private double current_x_pos;
  private double current_y_pos;
  private double currTheta;

  // set constants
  private static final int FORWARD_SPEED = 120;
  private static final int ROTATE_SPEED = 100;
  private static final double TILE_SIZE = 30.48;

  private boolean navigate = true;

  // constructor for navigation
  public Navigation(Odometer odo, EV3LargeRegulatedMotor leftMotor,
      EV3LargeRegulatedMotor rightMotor) {
    this.odometer = odo;
    this.leftMotor = leftMotor;
    this.rightMotor = rightMotor;
  }

  // main run method for navigation
  public void run() {



    /*
     * travelTo(0.0, 2.0 ); travelTo(1.0,1.0 ); travelTo(2.0,2.0 ); travelTo(2.0,1.0);
     * travelTo(1.0,0.0);
     */



  }

  /**
   * A method to drive our vehicle to a certain Cartesian coordinate
   * 
   * @param x X-Coordinate
   * @param y Y-Coordinate
   */
  public void travelTo(double x, double y) {

    current_x_pos = odometer.getXYT()[0];
    current_y_pos = odometer.getXYT()[1];

    deltax = x * TILE_SIZE - current_x_pos;
    deltay = y * TILE_SIZE - current_y_pos;

    // Calculate the angle to turn around
    currTheta = (odometer.getXYT()[2]) * Math.PI / 180;
    double mTheta = Math.atan2(deltax, deltay) - currTheta;

    double hypot = Math.hypot(deltax, deltay);

    // Turn to the correct angle towards the endpoint
    turnTo(mTheta);

    leftMotor.setSpeed(FORWARD_SPEED);
    rightMotor.setSpeed(FORWARD_SPEED);

    leftMotor.rotate(convertDistance(Main.WHEEL_RAD, hypot), true);
    rightMotor.rotate(convertDistance(Main.WHEEL_RAD, hypot), false);

    // stop vehicle
    leftMotor.stop(true);
    rightMotor.stop(true);
  }


  /**
   * A method to drive our vehicle to a certain Cartesian coordinate but does not wait to return
   * 
   * @param x X-Coordinate
   * @param y Y-Coordinate
   */
  public void travelToNoWait(double x, double y) {

    current_x_pos = odometer.getXYT()[0];
    current_y_pos = odometer.getXYT()[1];

    deltax = x - current_x_pos;
    deltay = y - current_y_pos;

    // Calculate the angle to turn around
    currTheta = (odometer.getXYT()[2]) * Math.PI / 180;
    double mTheta = Math.atan2(deltax, deltay) - currTheta;

    double hypot = Math.hypot(deltax, deltay);

    // Turn to the correct angle towards the endpoint
    turnTo(mTheta);

    leftMotor.setSpeed(FORWARD_SPEED);
    rightMotor.setSpeed(FORWARD_SPEED);

    leftMotor.rotate(convertDistance(Main.WHEEL_RAD, hypot), true);
    rightMotor.rotate(convertDistance(Main.WHEEL_RAD, hypot), true);

    // stop vehicle
    // leftMotor.stop(true);
    // rightMotor.stop(true);
  }

  public void travelTodis(double x, double y) {

    current_x_pos = odometer.getXYT()[0];
    current_y_pos = odometer.getXYT()[1];

    deltax = x * -current_x_pos;
    deltay = y * -current_y_pos;

    // Calculate the angle to turn around
    currTheta = (odometer.getXYT()[2]) * Math.PI / 180;
    double mTheta = Math.atan2(deltax, deltay) - currTheta;

    double hypot = Math.hypot(deltax, deltay);

    // Turn to the correct angle towards the endpoint
    turnTo(mTheta);

    leftMotor.setSpeed(FORWARD_SPEED);
    rightMotor.setSpeed(FORWARD_SPEED);

    leftMotor.rotate(convertDistance(Main.WHEEL_RAD, hypot), true);
    rightMotor.rotate(convertDistance(Main.WHEEL_RAD, hypot), false);

    // stop vehicle
    leftMotor.stop(true);
    rightMotor.stop(true);
  }

  /**
   * A method to turn our vehicle by a certain angle
   * 
   * JACOB would like this renamed
   * 
   * @param theta the angle you want to turn to
   */
  public void turnTo(double theta) {

    // ensures minimum angle for turning
    if (theta > Math.PI) {
      theta -= 2 * Math.PI;
    } else if (theta < -Math.PI) {
      theta += 2 * Math.PI;
    }

    // set Speed
    leftMotor.setSpeed(ROTATE_SPEED);
    rightMotor.setSpeed(ROTATE_SPEED);

    // rotate motors at set speed

    // if angle is negative, turn to the left
    if (theta < 0) {
      leftMotor.rotate(-convertAngle(Main.WHEEL_RAD, Main.TRACK, -(theta * 180) / Math.PI), true);
      rightMotor.rotate(convertAngle(Main.WHEEL_RAD, Main.TRACK, -(theta * 180) / Math.PI), false);

    } else {
      // angle is positive, turn to the right
      leftMotor.rotate(convertAngle(Main.WHEEL_RAD, Main.TRACK, (theta * 180) / Math.PI), true);
      rightMotor.rotate(-convertAngle(Main.WHEEL_RAD, Main.TRACK, (theta * 180) / Math.PI), false);
    }
  }

  /**
   * A method to turn our vehicle to a certain angle. 
   * 
   * @author jacob
   * 
   * 
   * @param theta
   */
  public void turnToJ(double theta) {

    // Calculate the angle to turn around
    currTheta = (odometer.getXYT()[2]) * Math.PI / 180;
    double mTheta = theta - currTheta;
    turnTo(mTheta);

  }

  /**
   * A method to determine whether another thread has called travelTo and turnTo methods or not
   * 
   * @return
   */
  boolean isNavigating() throws OdometerExceptions {
    return navigate;
  }

  /**
   * This method allows the conversion of a distance to the total rotation of each wheel need to
   * cover that distance.
   * 
   * @param radius
   * @param distance
   * @return
   */
  public static int convertDistance(double radius, double distance) {
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
  public static int convertAngle(double radius, double width, double angle) {
    return convertDistance(radius, Math.PI * width * angle / 360.0);
  }
  
  
  /**
   * This performs navigation using the coordinate system for the final project 
   * @param x
   * @param y
   * 
   */
  public static void travelToFP(double x, double y) {
    // this needs to be filled in
    
  }
}
