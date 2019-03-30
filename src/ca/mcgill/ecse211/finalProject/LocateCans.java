package ca.mcgill.ecse211.finalProject;

import ca.mcgill.ecse211.odometer.Odometer;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

/**
 * This class should run as a thread. It should locate cans that are in the line of sight of the US sensor.
 * If the can that is sensed is close to an already existing can (its sensitivy should be changable easily for the programmer) 
 * then it should just update the cans location if this scan of the can is equal to or closer than the last scan (distance from vehivle when it was made) 
 * if it is equal(or close) average two 
 * if it is current distance is closer than previous distance than use new one.
 * if distance is very small (ie close enough that we are scanning it) then do nothing
 * if distance is too far away to be accurate then do nothing  and add no cans 
 *  
 * 
 * @author jacobmcconnell
 *
 */
public class LocateCans implements Runnable {
  /**
   * this method is called to run the thread that detects new cans and updates already detected ones to be more accurate about the location
   */
  private int mSeconds= Main.locate_cans_mSeconds;
  private final static double MAX_SENSOR_DISTANCE =25;
  
  private static boolean running = true;
  static SampleProvider usDistance= Main.usDistance;
  static float[] usData= new float[usDistance.sampleSize()];
  
  /**
   * A method to get the distance from our sensor
   * 
   * @return
   */
  public static double fetchUS() {
    usDistance.fetchSample(usData, 0);
    return (double) (usData[0] * 100);
  }
  
  public void run() {
    
    while (running) {
      double USDistance = fetchUS();
      
      
      if (USDistance< MAX_SENSOR_DISTANCE) {
        double theta = Main.odometer.getAngJ();
        double roboX = Main.odometer.getX();
        double roboY = Main.odometer.getY();
        double deltaX = Math.cos(Math.toRadians(theta))*USDistance;
        double deltaY = Math.sin(Math.toRadians(theta))*USDistance;
        double canX = roboX + deltaX; 
        double canY = roboX + deltaY;
        if (canX/30.85+1>Main.SZ_UR_x||canX/30.85+1<Main.SZ_LL_x) {
          break;
        }
        if (canY/30.85+1>Main.SZ_UR_y||canY/30.85+1<Main.SZ_LL_y) {
          break;
        }
        
        
        Can can = Can.closestCan(canX,canY, USDistance);
        if ( can == null) {
          Can newCan = new Can(canX, canY, USDistance);
        } else {
          can.updateCan(canX,canY,USDistance);
          
        }
        
      }
      
      
      
      
      try {
        Thread.sleep(mSeconds);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      }
    
  }
  public static boolean isRunning() {
    return running;
  }
  public static void setRunning(boolean running1) {
    running = running1;
  }

  public static void lookForACan() {
    // TODO Auto-generated method stub
    double USDistance = fetchUS();
    
    
    if (USDistance< MAX_SENSOR_DISTANCE) {
      //Sound.beep();
      
      double theta = Main.odometer.getAngJ();
      double roboX = Main.odometer.getX();
      double roboY = Main.odometer.getY();
      double deltaX = Math.cos(Math.toRadians(theta))*USDistance;
      double deltaY = Math.sin(Math.toRadians(theta))*USDistance;
      double canX = roboX + deltaX; 
      double canY = roboY + deltaY;
      if (canX/30.85+1>Main.SZ_UR_x||canX/30.85+1<Main.SZ_LL_x) {
        return; 
      }
      if (canY/30.85+1>Main.SZ_UR_y||canY/30.85+1<Main.SZ_LL_y) {
        return;
      }
      
      
      Sound.beep();
      Can can = Can.closestCan(canX,canY, USDistance);
      if ( can == null) {
        Can newCan = new Can(canX, canY, USDistance);
      } else {
        can.updateCan(canX,canY,USDistance);
        
      }
      
    }
  }
  
  
  public static double[] lookForACan2() {
    // TODO Auto-generated method stub
    double USDistance = fetchUS();
    double[] returnList = new double[5];
    returnList[2]=Main.odometer.getAngJ();
    returnList[3]=USDistance;
    returnList[4]=-100;
    
    
    if (USDistance< MAX_SENSOR_DISTANCE) {
      //Sound.beep();
      
      double theta = Main.odometer.getAngJ();
      double roboX = Main.odometer.getX();
      double roboY = Main.odometer.getY();
      double deltaX = Math.cos(Math.toRadians(theta))*USDistance;
      double deltaY = Math.sin(Math.toRadians(theta))*USDistance;
      double canX = roboX + deltaX; 
      double canY = roboY + deltaY;
      returnList[0]= canX;
      returnList[1]= canY;
      returnList[2]=theta;
      
      if (canX/30.85+1>Main.SZ_UR_x||canX/30.85+1<Main.SZ_LL_x) {
        returnList[4]=-2;
        return returnList; 
      }
      if (canY/30.85+1>Main.SZ_UR_y||canY/30.85+1<Main.SZ_LL_y) {
        returnList[4]=-3;
        return returnList;
      }
      
      returnList[4]=2;
      Sound.beep();
      Can can = Can.closestCan(canX,canY, USDistance);
      if ( can == null) {
        returnList[4]=4;
        Can newCan = new Can(canX, canY, USDistance);
      } else {
        returnList[4]=3;
        can.updateCan(canX,canY,USDistance);
        
      }
      return returnList;
      
    }
    
    return returnList;
  }
  

}
