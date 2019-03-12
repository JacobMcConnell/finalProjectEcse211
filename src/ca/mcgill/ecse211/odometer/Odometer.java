/**
 * * This class is meant as a skeleton for the odometer class to be used.
 * 
 * 
 */

package ca.mcgill.ecse211.odometer;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Odometer extends OdometerData implements Runnable {

  private OdometerData odoData;
  private static Odometer odo = null; // Returned as singleton

  // Motors and related variables
  private int leftMotorTachoCount;
  private int rightMotorTachoCount;
  private EV3LargeRegulatedMotor leftMotor;
  private EV3LargeRegulatedMotor rightMotor;
  private double theta;

  private double displacementL;
  private double displacementR;
  private int oldLeftMotorTachoCount;
  private int oldRightMotorTachoCount;


  private final double TRACK;
  private final double WHEEL_RAD;

  private int startingCorner;
  private int nbXLines;
  private int nbYLines;
  private static final double TILE_LENGTH = 30.48;


  private static final long ODOMETER_PERIOD = 25; // odometer update period in ms

  /**
   * This is the default constructor of this class. It initiates all motors and variables once.It
   * cannot be accessed externally.
   * 
   * @param leftMotor
   * @param rightMotor
   * @throws OdometerExceptions
   */
  private Odometer(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
      final double TRACK, final double WHEEL_RAD, int startingCorner) throws OdometerExceptions {
    odoData = OdometerData.getOdometerData(); // Allows access to x,y,z
    // manipulation methods
    this.leftMotor = leftMotor;
    this.rightMotor = rightMotor;

    // Reset the values of x, y and z to 0
    odoData.setXYT(0, 0, 0);

    this.leftMotorTachoCount = 0;
    this.rightMotorTachoCount = 0;
    this.theta = 0;

    this.TRACK = TRACK;
    this.WHEEL_RAD = WHEEL_RAD;

    this.startingCorner = startingCorner;
  }

  /**
   * This method initializes the odometer depending its starting Position
   * 
   */
  public void initialize() {
    switch (startingCorner) {
      case 0:
        nbXLines = 1;
        nbYLines = 1;
        this.theta = 0.0;
        odo.setXYT(nbXLines * TILE_LENGTH, nbYLines * TILE_LENGTH, theta);
        break;
      case 1:
        nbXLines = 7;
        nbYLines = 1;
        this.theta = 270.0;
        odo.setXYT(nbXLines * TILE_LENGTH, nbYLines * TILE_LENGTH, theta);
        break;
      case 2:
        nbXLines = 7;
        nbYLines = 7;
        this.theta = 180.0;
        odo.setXYT(nbXLines * TILE_LENGTH, nbYLines * TILE_LENGTH, theta);
        break;
      case 3:
        nbXLines = 1;
        nbYLines = 7;
        this.theta = 90.0;
        odo.setXYT(nbXLines * TILE_LENGTH, nbYLines * TILE_LENGTH, theta);
        break;
    }
  }

  /**
   * This method resets the theta in the odometer
   * 
   */
  public void reset() {
    this.theta = 0;
  }

  /**
   * This method is meant to ensure only one instance of the odometer is used throughout the code.
   * 
   * @param leftMotor
   * @param rightMotor
   * @return new or existing Odometer Object
   * @throws OdometerExceptions
   */
  public synchronized static Odometer getOdometer(EV3LargeRegulatedMotor leftMotor,
      EV3LargeRegulatedMotor rightMotor, final double TRACK, final double WHEEL_RAD,
      int startingCorner) throws OdometerExceptions {
    if (odo != null) { // Return existing object
      return odo;
    } else { // create object and return it
      odo = new Odometer(leftMotor, rightMotor, TRACK, WHEEL_RAD, startingCorner);
      return odo;
    }
  }

  /**
   * This class is meant to return the existing Odometer Object. It is meant to be used only if an
   * odometer object has been created
   * 
   * @return error if no previous odometer exists
   */
  public synchronized static Odometer getOdometer() throws OdometerExceptions {

    if (odo == null) {
      throw new OdometerExceptions("No previous Odometer exits.");

    }
    return odo;
  }

  /**
   * tells us the x coordinate alone
   * 
   * @return x value
   */
  public double getX() {
    // TODO Auto-generated method stub
    return odo.getXYT()[0];
  }

  /**
   * tells us the y coordinate alone
   * 
   * @return y value
   */
  public double getY() {
    // TODO Auto-generated method stub
    return odo.getXYT()[1];
  }

  /**
   * tells us heading of robot alone
   * 
   * @return theta
   */
  public double getAng() {
    // TODO Auto-generated method stub
    return odo.getXYT()[2];
  }

  /**
   * tells us heading of robot alone in standard coordinate system
   * 
   * @return theta
   */
  public double getAngJ() {
    // TODO Auto-generated method stub
    double a = (90 - (odo.getXYT()[2])) % 360;
    if (a < 0) {
      a += 360;
    }


    return a;
  }

  /**
   * This method is where the logic for the odometer will run. Use the methods provided from the
   * OdometerData class to implement the odometer.
   */
  // run method (required for Thread)
  public void run() {
    long updateStart, updateEnd;

    while (true) {
      updateStart = System.currentTimeMillis();

      leftMotorTachoCount = leftMotor.getTachoCount();
      rightMotorTachoCount = rightMotor.getTachoCount();

      double dX, dY, dTheta, dDisplace;

      // TODO Calculate new robot position based on tachometer counts

      displacementL = Math.PI * WHEEL_RAD * (leftMotorTachoCount - oldLeftMotorTachoCount) / 180;
      displacementR = Math.PI * WHEEL_RAD * (rightMotorTachoCount - oldRightMotorTachoCount) / 180;

      oldLeftMotorTachoCount = leftMotorTachoCount;
      oldRightMotorTachoCount = rightMotorTachoCount;
      dDisplace = 0.5 * (displacementL + displacementR);
      dTheta = (displacementL - displacementR) / TRACK;
      theta += dTheta;

      dX = dDisplace * Math.sin(theta);
      dY = dDisplace * Math.cos(theta);


      // TODO Update odometer values with new calculated values
      odo.update(dX, dY, dTheta * 180 / Math.PI);

      // this ensures that the odometer only runs once every period
      updateEnd = System.currentTimeMillis();
      if (updateEnd - updateStart < ODOMETER_PERIOD) {
        try {
          Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
        } catch (InterruptedException e) {
          // there is nothing to be done
        }
      }
    }
  }

}
