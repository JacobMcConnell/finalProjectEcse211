package ca.mcgill.ecse211.finalProject;

import ca.mcgill.ecse211.navigation.Navigation;
import ca.mcgill.ecse211.navigation.Navigator;

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
    //need to complete 
    int[][] waypoints= waypointsForSearch();
    int i=0; // maybe make this global to not repeat 
    for (int j=0 ; j< waypoints.length; j++) {
      
      //navigate.travelTo(waypoints[i][0],waypoints[i][1]);
      Main.navigator.travelTo(waypoints[i][0],waypoints[i][1]);
      // turn 180 degres around and it should be a thead that somehow sleeps every 10 mili seconds ect 
      threadSafeTurn(90);
      threadSafeTurn(180);
      if (Can.numberOfUnScannedCans()>0) {
        //search for cans 
        scanCans();
        Main.navigator.travelTo(waypoints[i][0],waypoints[i][1]);
      }
      
      
      
      i++;
    }
  }
  
  /**
   * travels to and scans the cans untill no unscanned cans 
   */
    public static void scanCans() {
      while (Can.numberOfUnScannedCans()>1) {
        Can closestCan=Can.getClosestCanToRobo();
        threadTravelTo(closestCan);
        //scan the can
        // when we weigh the can and carry it in real version
        // make sure to turn off locate cans before 
        // and turn it on after 
        closestCan.scanned=true;
        //for the non beta here is where we would take the can to starting square 
        
      }
      
    }
    
    
    //takes us to the can while allowing us to scan it 
    private static void threadTravelTo(Can closestCan) {
    // TODO Auto-generated method stub
      Main.navigation.travelToNoWaitMINUS(closestCan.x, closestCan.y, Main.StoppingDistanceFromCan);
      while(Main.leftMotor.isMoving()) {
        try {
          Thread.sleep(15);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    
  }

    /**
     * this should turn degrees to one side while allowing the scan thread to work (probably not actually 
     * safe) 
     * @param degrees
     * 
     */
    public static void threadSafeTurn(double degrees) {
      Main.navigation.turnToNoWait(degrees);
      while (Main.leftMotor.isMoving()) {
        try {
          Thread.sleep(15);
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
      Main.startingCorner= Main.redCorner;
      Main.SZ_UR_y= Main.SZR_UR_y;
      Main.SZ_LL_y= Main.SZR_LL_y;
      Main.SZ_LL_x= Main.SZR_LL_x;
      Main.SZ_UR_x = Main.SZR_LL_x;
      
      
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
      Main.startingCorner=Main.greenCorner;
      Main.SZ_UR_y= Main.SZG_UR_y;
      Main.SZ_LL_y= Main.SZG_LL_y;
      Main.SZ_LL_x= Main.SZG_LL_x;
      Main.SZ_UR_x = Main.SZG_LL_x;
      
      
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
    int width= Main.SZ_UR_x-Main.SZ_LL_x;
    int height = Main.SZ_UR_y - Main.SZ_LL_y;
    int [][] waypoints= new int[width*height][2]; 
    for (int i=0; i< height; i++) {
      for (int j=0; j<width; j++) {
        waypoints[i*width+j][0]=Main.SZ_LL_x+j;
        waypoints[i*width+j][1]= Main.SZ_LL_y+i;
      }
    }
    return waypoints; 
    
  }
  
  
  
  
  
  

}
