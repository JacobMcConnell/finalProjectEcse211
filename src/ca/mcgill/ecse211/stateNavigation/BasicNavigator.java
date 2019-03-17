package ca.mcgill.ecse211.stateNavigation;

import ca.mcgill.ecse211.finalProject.Main;
import ca.mcgill.ecse211.odometer.Odometer;
/*
 * File: Navigation.java Written by: Sean Lawlor ECSE 211 - Design Principles and Methods, Head TA
 * Fall 2011 Ported to EV3 by: Francois Ouellet Delorme Fall 2015 Helper methods and extend Thread -
 * Jonah Caplan 2015
 * 
 * Movement control class (turnTo, travelTo, flt, localize)
 */
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class BasicNavigator extends Thread {

  final static int FAST = 200, SLOW = 100, ACCELERATION = 4000;
  final static double DEG_ERR = 3.0, CM_ERR = 1.0;
  Odometer odometer;
  EV3LargeRegulatedMotor leftMotor, rightMotor;

  public BasicNavigator(Odometer odo) {
    this.odometer = odo;

   //EV3LargeRegulatedMotor[] motors = this.odometer.getMotors();
    this.leftMotor = Main.leftMotor;
    this.rightMotor = Main.rightMotor;

    // set acceleration
    this.leftMotor.setAcceleration(ACCELERATION);
    this.rightMotor.setAcceleration(ACCELERATION);
  }


  /**
   * setSpeeds is used to set the motor speeds jointly it accepts the left and right speed as floats
   * 
   * @param lSpd
   * @param rSpd
   */
  public void setSpeeds(float lSpd, float rSpd) {
    this.leftMotor.setSpeed(lSpd);
    this.rightMotor.setSpeed(rSpd);
    if (lSpd < 0)
      this.leftMotor.backward();
    else
      this.leftMotor.forward();
    if (rSpd < 0)
      this.rightMotor.backward();
    else
      this.rightMotor.forward();
  }

  /**
   * setSpeeds is used to se the motor speeds jointly it accepts the left and right speed at ints
   * 
   * @param lSpd
   * @param rSpd
   */
  public void setSpeeds(int lSpd, int rSpd) {
    this.leftMotor.setSpeed(lSpd);
    this.rightMotor.setSpeed(rSpd);
    if (lSpd < 0)
      this.leftMotor.backward();
    else
      this.leftMotor.forward();
    if (rSpd < 0)
      this.rightMotor.backward();
    else
      this.rightMotor.forward();
  }

  /**
   * Float the two motors jointly
   */
  public void setFloat() {
    this.leftMotor.stop();
    this.rightMotor.stop();
    this.leftMotor.flt(true);
    this.rightMotor.flt(true);
  }


  /**
   * TravelTo function which takes as arguments the x and y position in cm Will travel to designated
   * position, while constantly updating it's heading
   * 
   * @param x
   * @param y
   */
  public void travelTo(double x, double y) {
    double minAng;
    while (!checkIfDone(x, y)) {
      minAng = getDestAngle(x, y);
      this.turnTo(minAng, false);
      this.setSpeeds(FAST, FAST);
    }
    this.setSpeeds(0, 0);
  }

  protected boolean checkIfDone(double x, double y) {
    return Math.abs(x - odometer.getX()) < CM_ERR && Math.abs(y - odometer.getY()) < CM_ERR;
  }

  /**
   * 
   * @param angle
   * @return
   */
  protected boolean facingDest(double angle) {
    return Math.abs(angle - odometer.getAng()) < DEG_ERR;
  }

  /**
   * Interesting note: Math.atan2() is used in this function. Math.atan2(x,y) takes the Cartesian
   * Coordinates and returns the angle of its polar form.
   * 
   * @param x
   * @param y
   * @return The angle of the the destination point (x,y) relative to the robot
   */
  protected double getDestAngle(double x, double y) {
    double minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX())) * (180.0 / Math.PI);
    if (minAng < 0) {
      minAng += 360.0;
    }
    return minAng;
  }

  /*
   * 
   * not to stop the motors when the turn is completed
   */

  /**
   * turnTo function determines the sign of the speed of the motors when turning, based on the the
   * angle created between the direction of its former path and the angle formed between the
   * waypoint the robot currently is on and the next destination (error).
   * 
   * @param angle
   * @param stop
   */
  public void turnTo(double angle, boolean stop) {

    double error = angle - this.odometer.getAng();

    while (Math.abs(error) > DEG_ERR) {

      error = angle - this.odometer.getAng();

      if (error < -180.0) {
        this.setSpeeds(-SLOW, SLOW);
      } else if (error < 0.0) {
        this.setSpeeds(SLOW, -SLOW);
      } else if (error > 180.0) {
        this.setSpeeds(SLOW, -SLOW);
      } else {
        this.setSpeeds(-SLOW, SLOW);
      }
    }

    if (stop) {
      this.setSpeeds(0, 0);
    }
  }

  /*
   * Go foward a set distance in cm
   */

  /**
   * Uses the travelTo method in order to go in straight of length distance.
   * 
   * @param distance
   */
  public void goForward(double distance) {
    this.travelTo(odometer.getX() + Math.cos(Math.toRadians(this.odometer.getAng())) * distance,
        odometer.getY() + Math.sin(Math.toRadians(this.odometer.getAng())) * distance);

  }
}
