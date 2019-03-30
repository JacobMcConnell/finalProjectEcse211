package ca.mcgill.ecse211.finalProject;

import java.awt.Color;
import java.util.ArrayList;
import ca.mcgill.ecse211.colorClassification.ColorClassification;
import ca.mcgill.ecse211.navigation.Navigation;
import ca.mcgill.ecse211.navigation.Navigator;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;

/**
 * This class contains methods that abstract portions of the final project. 
 * @author jacob mcconnell
 *
 */

public class FinalProjectMethods {
  /**
   * this method should cause the robot to grab and lift the can and hold it up. It should return when complete. 
   */
  public static void liftCan() {
    //method must be finished 
  }
  
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
   * tells the robot to travel around search area and and turn so that the search thread sees the cans 
   * returns after finished 
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
      double minDistance=100;
      double minAngle=0; 
      double[] minPoint= null;
      while(Main.leftMotor.isMoving()) {
        double USDistance =LocateCans.fetchUS();
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
      i--;
      
      
      }
      
      // no point to this 
      /*
      Main.leftMotor.setSpeed(100);
      Main.rightMotor.setSpeed(100);
      Main.leftMotor.rotate(Navigation.convertAngle(Main.WHEEL_RAD, Main.TRACK, 90), true);
      Main.rightMotor.rotate(Navigation.convertAngle(Main.WHEEL_RAD, Main.TRACK, -90), false);
      */
      
      
      
      /*while (Main.leftMotor.isMoving()) {
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        
      }
      */
      //Main.navigator.turnBy(90, true);
      //threadSafeTurn(90);
      //threadSafeTurn(180);
      
      /*
      if (Can.numberOfUnScannedCans()>0) {
        //search for cans 
        scanCans();
        Main.navigation.travelTo(waypoints[i][0], waypoints[i][1]);
        //Main.navigator.travelTo(waypoints[i][0],waypoints[i][1]);
      }
      */
      
      
      
      i++;
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
    closeGrabber(75);
   
    boolean ourColor =Main.colorScan();
    ColorClassification.rotateArmToLeft(90);
    openGrabber(75);
    closeGrabber(150);// use this to do weight and apropriate beeps
    //deal with the result of heavy / not heavy ect.
    if (ourColor) {
      // take it to our starting point 
      Navigation.travelToFP(Main.SZ_LL_x, Main.SZ_LL_y);
      //ASK ERDONG TO DO THIS GO ACCROSS BRIDGE 
      //return to starting point
      // let go of can 
      // backup a bit 
          //Main.leftMotor.rotate(360,true);
          // Main.rightMotor.rotate(360,false);
      // 
      //light localize mayber???
      // travel across bridge 
      //Main.navigator.travelTo(Main.SZ_LL_x,Main.SZ_LL_y);
      
    } else {
      Navigation.travelToFP(Main.SZ_LL_x, Main.SZ_LL_y);
      Main.navigator.travelTo(Main.SZ_LL_x-1, Main.SZ_LL_y+1);
      openGrabber(150);
      //ideally more elegant way to go backwards than this 
      Main.leftMotor.rotate(360,true);
      Main.rightMotor.rotate(360,false);
      Navigation.travelToFP(Main.SZ_LL_x, Main.SZ_LL_y);
    }
    
    //deal with the result of heavy / not heavy ect.
    
    
  }
  
  
  /**
   * turn medium motor
   * 
   * @param degrees to turn right
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
   * This method returns whether a can is inside the search zone. In order for it to be used handle project
   * variables must have been called first. 
   * @param canX
   * @param canY
   * @return whether or not the specified point is in the search zone 
   */
  private static boolean pointInSearchZone(double canX, double canY) {
    // TODO Auto-generated method stub
    
    if (canX/30.85+1>Main.SZ_UR_x||canX/30.85+1<Main.SZ_LL_x) {
      
      return false; 
    }
    if (canY/30.85+1>Main.SZ_UR_y||canY/30.85+1<Main.SZ_LL_y) {
     
      return false;
    }
    return true;
  }

  /**
   * travels to and scans the cans untill no unscanned cans 
   */
    public static void scanCans() {
     
      while (Can.numberOfUnScannedCans()>0) {
        Can closestCan=Can.getClosestCanToRobo();
        if (closestCan!=null){
          Main.navigation.travelToMINUS(closestCan.x, closestCan.y, Main.StoppingDistanceFromCan);
        }
       
        
        //scan the can
        // when we weigh the can and carry it in real version
        // make sure to turn off locate cans before 
        // and turn it on after 
        Sound.beep();
        Sound.beep();
        Sound.beep();
        Sound.beep();
        //Main.colorScan(); // EROROR HERE WHY?
        if (closestCan!=null) {
        closestCan.scanned=true;
        }
        //for the non beta here is where we would take the can to starting square 
        
      }
      
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
