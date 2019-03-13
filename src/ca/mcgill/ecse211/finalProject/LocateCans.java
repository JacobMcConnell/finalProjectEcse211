package ca.mcgill.ecse211.finalProject;
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
  public void run() {
    
  }

}
