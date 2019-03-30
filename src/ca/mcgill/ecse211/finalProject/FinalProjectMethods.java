package ca.mcgill.ecse211.finalProject;

import java.awt.Color;
import java.util.ArrayList;
import ca.mcgill.ecse211.navigation.Navigation;
import ca.mcgill.ecse211.navigation.Navigator;
import lejos.hardware.Sound;

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
   * tells the robot to travel around search area and and turn so that the search thread sees the cans 
   * returns after finished 
   */
  public static void searchForCans() {
    //Can c = new Can(75,45,10);
    //need to complete 
    int[][] waypoints= waypointsForSearch();
    int i=0; // maybe make this global to not repeat 
    for (int j=0 ; j< waypoints.length; j++) {
      
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
      ArrayList<double[]> data = new ArrayList<double[]>();
      while(Main.leftMotor.isMoving()) {
        //double USDistance =LocateCans.fetchUS();
        
        
        
        data.add(LocateCans.lookForACan2());
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
      Main.leftMotor.rotate(Navigation.convertAngle(Main.WHEEL_RAD, Main.TRACK, 90), true);
      Main.rightMotor.rotate(Navigation.convertAngle(Main.WHEEL_RAD, Main.TRACK, -90), false);
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
      if (Can.numberOfUnScannedCans()>0) {
        //search for cans 
        scanCans();
        Main.navigation.travelTo(waypoints[i][0], waypoints[i][1]);
        //Main.navigator.travelTo(waypoints[i][0],waypoints[i][1]);
      }
      
      
      
      i++;
    }
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
