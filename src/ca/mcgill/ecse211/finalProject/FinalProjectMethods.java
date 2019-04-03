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
 * @author jacob mcconnell
 *
 */

public class FinalProjectMethods {
  private static final int grabberConstant = 85;
  private static double lastDirection;
  private static boolean justHandledCan=false;

 
  
  /**
   * this method should put the can down. it should return when  compleated. 
   */
  public static void putCanDown() {
    
  }
  
  /**
   * this method tells us if all the cans of our color have been moved to our starting spot
   * @return true if all the cans of our color are in our starting spot 
   */
  public static boolean allCansOfOurColorGrabbed() {
    // needs to be finished 
    return false;
  }
  /**
   * intructs the robot to return to the starting square (either it retreats its steps or it checks for obstical avoidance
   * which may require a navigation that has obstical avoidance!!!) 
   * 
   */
  public static void returnCanToStartingSquare() {
    
  }
  
  /**
   * takes us back throught the tunnel it does not use odometry correction 
   */
  public static void goBackThroughTunnel() {
    Main.navigation.travelToFP(Main.TN_END_x, Main.TN_END_y);
    Main.navigation.travelToFP(Main.TN_START_x, Main.TN_START_y);
    Main.navigator.travelDist(5, 50);
    
    
  }
  /**
   * tells the robot to navigate to the nearest search corner. 
   */
  public static void takeRobotToNearestSearchCorner() {
    
  }
  
  /**
   * This method takes and angle a position and a distance to find the new cartiesian point of the distance at the angle from the 
   * orignal point x,y
   * @param angle
   * @param x
   * @param y
   * @param USDistance
   * @return an array of two doubles representing the location of the new point
   */
  static double[] getXY(double angle,double x, double y, double USDistance) {
    double[] point = new double[2];
    double theta = angle;
    double roboX = x;
    double roboY = y;
    double deltaX = Math.cos(Math.toRadians(theta))*USDistance;
    double deltaY = Math.sin(Math.toRadians(theta))*USDistance;
    double canX = roboX + deltaX; 
    double canY = roboY + deltaY;
    point[0]=canX;
    point[1]=canY;
    return point;
    
  }
  
  
  
  /**
   * tells the robot to travel around search area and search for cans
   * It is the bulk of the search algorithm. We just follow a snake pattern and turn to find the cans. when a can is found
   * go to it. Scan it. Take it either to the starting position or outside of the search zone. then return to the search zone 
   * and return to the last position in the snake and begin scanning for cans at that position again. 
   * _BULK OF THE SEARCH ALGORITHM
   */
  public static void searchForCans() {
    //Can c = new Can(75,45,10);
    //need to complete 
    int[][] waypoints= waypointsForSearch();
    //int i=0; // maybe make this global to not repeat 
    for (int i=0 ; i< waypoints.length; i++) {
      
      //navigate.travelTo(waypoints[i][0],waypoints[i][1]);
      Main.navigator.setOdoCorrection(Main.odoCorr);
      Main.navigator.travelTo(waypoints[i][0],waypoints[i][1]);
      if(justHandledCan) {
        Main.navigator.turnTo(lastDirection);
        justHandledCan=false;
      }
      lastDirection= Main.odometer.getAng();
      
      // turn 180 degres around and it should be a thead that somehow sleeps every 10 mili seconds ect 
      //Main.navigator.turnBy(90, true);
     // Main.navigator.turnBy(180, false);
      
      
      Main.leftMotor.rotate(Navigation.convertAngle(Main.WHEEL_RAD, Main.TRACK, 90), true);
      Main.rightMotor.rotate(Navigation.convertAngle(Main.WHEEL_RAD, Main.TRACK, -90), false);
      Main.leftMotor.setSpeed(65);
      Main.rightMotor.setSpeed(65);
      Main.leftMotor.rotate(Navigation.convertAngle(Main.WHEEL_RAD, Main.TRACK, -180), true);
      Main.rightMotor.rotate(Navigation.convertAngle(Main.WHEEL_RAD, Main.TRACK, 180), true);
     // List
      //ArrayList<double[]> data = new ArrayList<double[]>();
      double minDistance=30;
      double minAngle=0; 
      double[] minPoint= null;
      while(Main.leftMotor.isMoving()) {
        float[] sample = new float[Main.usDistance.sampleSize()];
        Main.usDistance.fetchSample(sample, 0);
        double USDistance = sample[0]*100;
           // LocateCans.fetchUS();
        if (USDistance<minDistance) {
          double angle=Main.odometer.getAngJ();
          double x = Main.odometer.getX();
          
          double y = Main.odometer.getY();
          
          double[] point = getXY(angle,x,y,USDistance);
          if (pointInSearchZone(point[0],point[1])) {
            minDistance=USDistance;
            minAngle=angle;
            minPoint=point;
            
          }
          
          
        }
        
        
        //data.add(LocateCans.lookForACan2());
       //maybe gives the odometer time to work  
        try {
          Thread.sleep(30);//probably 30 
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        
        
        
      }
      int doNOthing=0;
      doNOthing++;
      
      Main.leftMotor.setSpeed(100);
      Main.rightMotor.setSpeed(100);
      if (minPoint!=null) {
     
      Main.navigation.travelToMINUS(minPoint[0], minPoint[1], Main.StoppingDistanceFromCan);
      HandleCan();
      justHandledCan=true;
      i--;
      
      
      }
      
      
      
      
    }
  }
  /**
   * This method is called when we have reached the can. It should grab the can (but not lift) then let go. then it should 
   * do the color scan (which should handle the apropriate beeps). then the light sensor should be moved to the middle. then the can should be 
   * lifted. (Ideally weight should be scanned - still unclear how to do this). depending on if can color is ours or not then we take the can
   * back to our starting position. if its not we bring it outside the search zone 
   */
  private static void HandleCan() {
    // TODO Auto-generated method stub
    closeGrabber(70);
   
    int canColor =Main.colorScan();
    ColorClassification.rotateArmToLeft(90);
    openGrabber(70);
    boolean heavy =closeGrabber(grabberConstant);// use this to do weight and apropriate beeps
    //deal with the result of heavy / not heavy ect.
    
    for (int i =0; i <=canColor;i++) {
      if(heavy) {
        Sound.buzz();//long beep?
      } else {
        Sound.beep();
      }
    }
    if (canColor==Main.targetColor) {
      // take it to our starting point 
     
      Main.navigator.directTravelTo(Main.SZ_LL_x, Main.SZ_LL_y);
     // this is hacky but its what i have 
      goBackThroughTunnel();
     
      Main.tn_navigator.travelToStartingPoint();
      openGrabber(grabberConstant);
      ColorClassification.rotateArmToRight(90);
      //ideally more elegant way to go backwards than this 
      Main.leftMotor.rotate(-360,true);
      Main.rightMotor.rotate(-360,false);
      Main.tn_navigator.travelToTunnel();
      Main.tn_navigator.travelThroughTunnel();
      Main.tn_navigator.travelTostartSet();
      Main.navigator.turnTo(90);
      
    
      
    } else {
      Main.navigator.directTravelTo(Main.SZ_LL_x, Main.SZ_LL_y);
      Main.navigator.travelTo(Main.SZ_LL_x-1, Main.SZ_LL_y+1);
      openGrabber(grabberConstant);
      ColorClassification.rotateArmToRight(90);
      //ideally more elegant way to go backwards than this 
      Main.leftMotor.rotate(-360,true);
      Main.rightMotor.rotate(-360,false);
      Main.navigator.directTravelTo(Main.SZ_LL_x, Main.SZ_LL_y);
     // Main.navigation.turnToJ(90);
      Main.navigator.turnTo(90);
     ///Main.navigator.turnTo();l
    }
    
    //deal with the result of heavy / not heavy ect.
    
    
  }
  
  
  /**
   * turn medium motor
   * 
   * @param degrees to turn right
   * @return 
   */
  public static boolean closeGrabber(int degrees) {
    
    Main.grabMotor.setAcceleration(750);
    Main.grabMotor.setSpeed(100);
    Main.grabMotor.rotate(-degrees);
  
    
    
    Main.grabMotor.setSpeed(0);
    return false;// THIS NEEDS TO BE COMPLEATED 

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
   * This method returns whether a can is inside the search zone. In order for it to be used handle project
   * variables must have been called first. 
   * @param canX
   * @param canY
   * @return whether or not the specified point is in the search zone 
   */
  private static boolean pointInSearchZone(double canX, double canY) {
    // TODO Auto-generated method stub
    
    if ((canX/30.85>Main.SZ_UR_x)||(canX/30.85<Main.SZ_LL_x)) {
      
      return false; 
    }
    if ((canY/30.85>Main.SZ_UR_y)||(canY/30.85<Main.SZ_LL_y)) {
     
      return false;
    }
    return true;
  }

 
    
 

    /**
     * this should turn degrees to one side while allowing the scan thread to work (probably not actually 
     * safe) DOES NOT WORK SHOULD BE THROWN OUT
     * @param degrees
     * 
     */
    public static void threadSafeTurn(double degrees) {
      Main.navigation.turnToNoWait(degrees);
      while (Main.leftMotor.isMoving()) {
        try {
          Thread.sleep(30);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      
      
    }
    
    
    
    
   
    
    
    
    
    
  
  
  /**
   * handles the project variables 
   * 
   */
  public static void hadleProjectVariables() {
    
 // setting all our variables depending on our team 
    if (Main.RedTeam==14) {
      //Main.targetColor= 3;
      Main.startingCorner= Main.redCorner;
      Main.SZ_UR_y= Main.SZR_UR_y;
      Main.SZ_LL_y= Main.SZR_LL_y;
      Main.SZ_LL_x= Main.SZR_LL_x;
      Main.SZ_UR_x = Main.SZR_UR_x;
      
      
      Main.TN_LL_x = Main.TNR_LL_x;
      Main.TN_UR_x = Main.TNR_UR_x;
      Main.TN_LL_y = Main.TNR_LL_y;
      Main.TN_UR_y= Main.TNR_UR_y;
      
      if (Math.abs(Main.TN_LL_x-Main.TN_UR_x)<1.1) {
        // then the tunnel is verticle 
        Main.TN_START_x= (Main.TN_LL_x+Main.TN_UR_x)/2;
        Main.TN_START_y= Main.TN_LL_y;
        Main.TN_END_x= (Main.TN_LL_x+Main.TN_UR_x)/2;
        Main.TN_END_y = Main.TN_UR_y; 
        
      } else {
        // the tunnel is horizontal 
        Main.TN_START_x = Main.TN_LL_x; 
        Main.TN_START_y= (Main.TN_LL_y+Main.TN_UR_y)/2;
        Main.TN_END_x= Main.TN_UR_x;
        Main.TN_END_y= (Main.TN_LL_y+Main.TN_UR_y)/2;
        
      }
      
      
      
      
      
    } else {
      Main.targetColor= 1;
      Main.startingCorner=Main.greenCorner;
      Main.SZ_UR_y= Main.SZG_UR_y;
      Main.SZ_LL_y= Main.SZG_LL_y;
      Main.SZ_LL_x= Main.SZG_LL_x;
      Main.SZ_UR_x = Main.SZG_UR_x;
      
      
      Main.TN_LL_x = Main.TNG_LL_x;
      Main.TN_UR_x = Main.TNG_UR_x;
      Main.TN_LL_y = Main.TNG_LL_y;
      Main.TN_UR_y= Main.TNG_UR_y;
      
      
      
      if (Math.abs(Main.TN_LL_x-Main.TN_UR_x)<1.1) {
        // then the tunnel is verticle 
        Main.TN_START_x= (Main.TN_LL_x+Main.TN_UR_x)/2;
        Main.TN_START_y= Main.TN_LL_y;
        Main.TN_END_x= (Main.TN_LL_x+Main.TN_UR_x)/2;
        Main.TN_END_y = Main.TN_UR_y; 
        
      } else {
        // the tunnel is horizontal 
        Main.TN_START_x = Main.TN_LL_x; 
        Main.TN_START_y= (Main.TN_LL_y+Main.TN_UR_y)/2;
        Main.TN_END_x= Main.TN_UR_x;
        Main.TN_END_y= (Main.TN_LL_y+Main.TN_UR_y)/2;
        
      }
    }
    
  }
  
  public static int[][] waypointsForSearch(){
    int width= Main.SZ_UR_x-Main.SZ_LL_x+1;
    int height = Main.SZ_UR_y - Main.SZ_LL_y+1;
    int [][] waypoints= new int[width*height][2]; 
    for (int i=0; i< height; i++) {
      // if i is even
    
      for (int j=0; j<width; j++) {
        if (i%2==0) {
        waypoints[i*width+j][0]=Main.SZ_LL_x+j;
        waypoints[i*width+j][1]= Main.SZ_LL_y+i;
        }else {
          waypoints[i*width+j][0]=Main.SZ_LL_x+(width-1-j);
          waypoints[i*width+j][1]= Main.SZ_LL_y+i;
        }
      }
      
             
    }
    return waypoints; 
    
  }
  
  
  
  
  
  

}
