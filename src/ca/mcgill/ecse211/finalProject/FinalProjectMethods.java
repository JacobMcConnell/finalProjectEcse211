package ca.mcgill.ecse211.finalProject;
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
    while (Can.numberOfUnScannedCans()<1) {
      
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
