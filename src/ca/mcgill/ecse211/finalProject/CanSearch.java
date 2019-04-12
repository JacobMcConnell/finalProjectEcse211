package ca.mcgill.ecse211.finalProject;

import java.awt.Color;
import java.util.ArrayList;
import ca.mcgill.ecse211.colorClassification.ColorClassification;
import ca.mcgill.ecse211.navigation.Navigation;
import ca.mcgill.ecse211.navigation.Navigator;
import ca.mcgill.ecse211.navigation.TN_navigator;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;

/**
 * This class contains methods that abstract portions of the final project.
 * 
 * @author jacob mcconnell
 *
 */

public class CanSearch {
  private static final int grabberConstant = 150;
  private static double lastDirection;
  private static boolean justHandledCan = false;



  /**
   * takes us back throught the tunnel it does not use odometry correction
   */
  public static void goBackThroughTunnel() {
    Main.navigator.directTravelTo(Main.TN_END_x, Main.TN_END_y);
    Main.navigator.directTravelTo(Main.TN_START_x, Main.TN_START_y);
    Main.navigator.travelDist(5, 50);


  }


  /**
   * This method takes and angle a position and a distance to find the new cartiesian point of the
   * distance at the angle from the orignal point x,y
   * 
   * @param angle
   * @param x
   * @param y
   * @param USDistance
   * @return an array of two doubles representing the location of the new point
   */
  private static double[] getXY(double angle, double x, double y, double USDistance) {
    double[] point = new double[2];
    double theta = angle;
    double roboX = x;
    double roboY = y;
    double deltaX = Math.cos(Math.toRadians(theta)) * USDistance;
    double deltaY = Math.sin(Math.toRadians(theta)) * USDistance;
    double canX = roboX + deltaX;
    double canY = roboY + deltaY;
    point[0] = canX;
    point[1] = canY;
    return point;

  }



  /**
   * tells the robot to travel around search area and search for cans It is the bulk of the search
   * algorithm. We just follow a snake pattern and turn to find the cans. when a can is found go to
   * it. Scan it. Take it either to the starting position or outside of the search zone. then return
   * to the search zone and return to the last position in the snake and begin scanning for cans at
   * that position again. _BULK OF THE SEARCH ALGORITHM
   */
  public static void searchForCans() {

    int[][] waypoints = waypointsForSearch();

    for (int i = 0; i < waypoints.length; i++) {

      Main.navigator.setOdoCorrection(Main.odoCorr);
      Main.navigator.travelTo(waypoints[i][0], waypoints[i][1]);
      if (justHandledCan) {
        Main.navigator.turnTo(lastDirection);
        justHandledCan = false;
      }
      lastDirection = Main.odometer.getAng();

      Main.leftMotor.rotate(Navigation.convertAngle(Main.WHEEL_RAD, Main.TRACK, 90), true);
      Main.rightMotor.rotate(Navigation.convertAngle(Main.WHEEL_RAD, Main.TRACK, -90), false);
      Main.leftMotor.setSpeed(65);
      Main.rightMotor.setSpeed(65);
      Main.leftMotor.rotate(Navigation.convertAngle(Main.WHEEL_RAD, Main.TRACK, -180), true);
      Main.rightMotor.rotate(Navigation.convertAngle(Main.WHEEL_RAD, Main.TRACK, 180), true);

      double minDistance = 30;
      double minAngle = 0;
      double[] minPoint = null;
      while (Main.leftMotor.isMoving()) {
        float[] sample = new float[Main.usDistance.sampleSize()];
        Main.usDistance.fetchSample(sample, 0);
        double USDistance = sample[0] * 100;

        if (USDistance < minDistance) {
          double angle = Main.odometer.getAngJ();
          double x = Main.odometer.getX();

          double y = Main.odometer.getY();

          double[] point = getXY(angle, x, y, USDistance);
          if (pointInSearchZone(point[0], point[1])) {
            minDistance = USDistance;
            minAngle = angle;
            minPoint = point;

          }


        }
        try {
          Thread.sleep(30);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }



      }


      Main.leftMotor.setSpeed(100);
      Main.rightMotor.setSpeed(100);
      if (minPoint != null) {

        Main.navigation.travelToMINUS(minPoint[0], minPoint[1], Main.StoppingDistanceFromCan);
        HandleCan();
        justHandledCan = true;
        i--;


      }



    }
  }

  /**
   * This method is called when we have reached the can. It should grab the can (but not lift) then
   * let go. then it should do the color scan (which should handle the apropriate beeps). then the
   * light sensor should be moved to the middle. then the can should be lifted. (Ideally weight
   * should be scanned - still unclear how to do this). depending on if can color is ours or not
   * then we take the can back to our starting position. if its not we bring it outside the search
   * zone
   */
  private static void HandleCan() {
    closeGrabber(100);

    int canColor = Main.colorScan();
    ColorClassification.rotateArmToLeft(90);
    openGrabber(100);
    boolean heavy = WeightMeasurment.isHeavy();
    closeGrabber(grabberConstant);


    for (int i = 0; i <= canColor; i++) {
      if (heavy) {
        Sound.buzz();// long beep
      } else {
        Sound.beep();
      }
    }
    // if (canColor==Main.targetColor) {
    if (true) {
      // take it to our starting point
      Main.navigator.directTravelTo(Main.SZ_LL_x, Main.SZ_LL_y);
      // this is hacky but its what i have
      goBackThroughTunnel();

      Main.tn_navigator.travelToStartingPoint();
      openGrabber(grabberConstant);
      ColorClassification.rotateArmToRight(90);
      Main.leftMotor.rotate(-360, true);
      Main.rightMotor.rotate(-360, false);
      Main.tn_navigator.travelToTunnel();
      Main.tn_navigator.travelThroughTunnel();
      Main.tn_navigator.travelTostartSet();
      Main.navigator.turnTo(90);



    } else {
      Main.navigator.directTravelTo(Main.SZ_LL_x, Main.SZ_LL_y);
      Main.navigator.travelTo(Main.SZ_LL_x - 1, Main.SZ_LL_y + 1);
      openGrabber(grabberConstant);
      ColorClassification.rotateArmToRight(90);
      Main.leftMotor.rotate(-360, true);
      Main.rightMotor.rotate(-360, false);
      Main.navigator.directTravelTo(Main.SZ_LL_x, Main.SZ_LL_y);
      Main.navigator.turnTo(90);

    }


  }


  /**
   * turn medium motor
   * 
   * @param degrees to turn right
   * @return
   */
  public static void closeGrabber(int degrees) {

    Main.grabMotor.setAcceleration(750);
    Main.grabMotor.setSpeed(100);
    Main.grabMotor.rotate(-degrees);



    Main.grabMotor.setSpeed(0);


  }



  /**
   * turn medium motor
   * 
   * @param degrees to turn left
   */
  public static void openGrabber(int degrees) {
    Main.grabMotor.setAcceleration(750);
    Main.grabMotor.setSpeed(100);
    Main.grabMotor.rotate(degrees);



    Main.grabMotor.setSpeed(0);

  }



  /**
   * This method returns whether a can is inside the search zone. In order for it to be used handle
   * project variables must have been called first.
   * 
   * @param canX
   * @param canY
   * @return whether or not the specified point is in the search zone
   */
  private static boolean pointInSearchZone(double canX, double canY) {


    if ((canX / 30.85 > Main.SZ_UR_x) || (canX / 30.85 < Main.SZ_LL_x)) {

      return false;
    }
    if ((canY / 30.85 > Main.SZ_UR_y) || (canY / 30.85 < Main.SZ_LL_y)) {

      return false;
    }
    return true;
  }



  /**
   * This generates a list of points in an S pattern in the search zone.
   * 
   * @return 2d array of points in the search zone
   */
  private static int[][] waypointsForSearch() {
    int width = Main.SZ_UR_x - Main.SZ_LL_x + 1;
    int height = Main.SZ_UR_y - Main.SZ_LL_y + 1;
    int[][] waypoints = new int[width * height][2];
    for (int i = 0; i < height; i++) {

      for (int j = 0; j < width; j++) {
        if (i % 2 == 0) {
          waypoints[i * width + j][0] = Main.SZ_LL_x + j;
          waypoints[i * width + j][1] = Main.SZ_LL_y + i;
        } else {
          waypoints[i * width + j][0] = Main.SZ_LL_x + (width - 1 - j);
          waypoints[i * width + j][1] = Main.SZ_LL_y + i;
        }
      }
    }
    return waypoints;
  }
}
