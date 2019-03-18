package ca.mcgill.ecse211.finalProject;

import java.util.ArrayList;// is this allowed 
import java.util.List;

/**
 * this class represents the cans for the robot to keep track of. it also provides methods that deal with cans and it has the list that 
 * keeps track of all the cans
 * @author jacobmcconnell
 *
 */
public class Can {
  //PLEASE SOMEONE FIND THE RIGHT VALUE FOR THIS 
  private static final double distance_Factor = Main.distance_Factor_for_Cans;

  private static final double USDE = Main.USDistance_Can_Equality_Radius;
  
  double x;
  double y; 
  double closest_distance; 
  boolean scanned; 
  
  //static can variables 
  public static List<Can> canList= new ArrayList<Can>();
  static double canSensitivity= Main.canSensitivity; 
  
  /**
   * constructor makes new can object 
   * @param x
   * @param y
   * @param usDistance
   */
  Can(double x,double y, double usDistance){
    this.x= x; 
    this.y=y;
    scanned=false;
    this.closest_distance= usDistance;
    canList.add(this);
    
    
  }
  /**
   * This method is used to determine the closest can to the input coordinates. It returns that can. 
   * It iterates through canList and checks if each can is the same as the coordinates using sameCan method
   * 
   * @param x
   * @param y
   * @param USDistance
   * @return the can that is closest to these coordinates 
   */
  public static Can closestCan(double x,double y, double USDistance){
    // must make this method 
    Can minCan = null;
    double min; 
    min =0;
    for (int i =0; i < canList.size();i++) {
      
      
      Can canI= canList.get(i);
      
     if ( sameCan(x,y,USDistance,canI)) {
       double eD = euclideanDistance(x,y,canI.x,canI.y);
       if (eD < min) {
        // if ()
           if (sameCan(x,y,USDistance, canI)) {
             min= eD;
             minCan= canI;
           }
        
         
         
       }
     }
      
    }
    
    return minCan;
    
   //breturn new Can(1,1,1);
    
  }
  
  
  
  
  
     double howClose(double Can2x,double Can2y,double USDistance) {
      Can can1= this;
      
      return USDistance;
       
     }
     /**
      * returns the euclidean distance between the points
      * @param x
      * @param y
      * @param x2
      * @param y2
      * @return distance between the points 
      */
     static double euclideanDistance(double x, double y, double x2, double y2) {
       return Math.sqrt((x-x2)*(x-x2)+(y-y2)*(y-y2)); 
     }
  
  /**
   * This method is used to determine whether a new can should be made or if we should update an old can.
   * It does this by iterating through canList and checking if each can is the same as the coordinates using same can
   * @param x
   * @param y
   * @param USDistance
   * @return whether a can near here already exists 
   */
  public static boolean doesCanAlreadyExist(double x,double y, double USDistance){
    // must make this method 
    return false;
    
  }
  
  
  /**
   * This method is used to determine whether a set of coordinates probably refers to a specific can
   * @param x
   * @param y
   * @param USDistance
   * @param can
   * @return this is the same as an already known can 
   */
  public static boolean sameCan(double x,double y, double USDistance, Can can){
    // must make this method 
    
    if (euclideanDistance(x,y,can.x ,can.y )< (Can.canSensitivity+USDistance*distance_Factor+can.closest_distance*distance_Factor)) {
      return true;
      
    }else {
    return false;
    }
    
  }
  /**
   * this method updates the cans position if necessary to be more accurate 
   * 
   * we may use some kind of waiting system with US distance or maybe just discrete 
   * but if the cans are almost equal then take the average location for it.  
   * @param x
   * @param y
   * @param USDistance
   * 
   */
  public void updateCan(double x, double y, double USDistance) {
    // must make this method 
    
    if  (USDistance < (this.closest_distance - USDE) ){
      // new one is closer so just use it 
      this.x= x;
      this.y =y;
      this.closest_distance= USDistance;
    } else if (USDistance> (this.closest_distance+ USDE)) {
      //dont update can cause it will be less accurate 
      
    } else {
      //around equal so take the average 
      this.x= (this.x+ x)/2;
      this.y= (this.y+ y)/2;
      this.closest_distance= (this.closest_distance+ USDistance)/2;
    }
  }
  
  
 
  
  
  
  
  

}
