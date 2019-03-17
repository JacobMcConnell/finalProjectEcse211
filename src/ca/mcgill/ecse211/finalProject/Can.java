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
  
  double x;
  double y; 
  double closest_distance; 
  boolean scanned; 
  
  //static can variables 
  public static List<Can> canList= new ArrayList<Can>();
  static double canSensitivity= 5; 
  
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
    Can maxCan = 0;
    double max; 
    for (int i =0; i < canList.size();i++) {
      
      
      Can canI= canList.get(i);
      
     if ( sameCan(x,y,USDistance,canI)) {
       double eD = euclideanDistance(x,y,canI.x,canI.y);
       if (eD < max) {
         
         
       }
     }
      
    }
    
    return new Can(1,1,1);
    
  }
  
  
  
  
  
     double howClose(double Can2x,double Can2y,double USDistance) {
      Can can1= this;
      
      return USDistance;
       
     }
     
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
    return false;
    
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
  }
  
  
 
  
  
  
  
  

}
