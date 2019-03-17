package ca.mcgill.ecse211.stateNavigation;

import ca.mcgill.ecse211.USPoller.UltrasonicPollerJ;
import ca.mcgill.ecse211.odometer.Odometer;

/*
 * 
 * The Navigator class extends the functionality of the Navigation class. It offers an alternative
 * travelTo() method which uses a state machine to implement obstacle avoidance.
 * 
 * The Navigator class does not override any of the methods in Navigation. All methods with the same
 * name are overloaded i.e. the Navigator version takes different parameters than the Navigation
 * version.
 * 
 * This is useful if, for instance, you want to force travel without obstacle detection over small
 * distances. One place where you might want to do this is in the ObstacleAvoidance class. Another
 * place is methods that implement specific features for future milestones such as retrieving an
 * object.
 * 
 * 
 */

/**
 * The when the navigator runs it controls the robots motion to take it to the destination position. 
 * 
 * A state machine is used to implement the navigator. The four states are INIT, Turning,
 * Traveling, Emergency Depending on the state in each pass through the loop the robot takes
 * is instructed to take actions that reflect its state.
 * 
 * INIT means that the state should be changed to TURNING
 * 
 * TURNING means that the robot should turn towards its destination it will keep doing this
 * until it finds that it is facing close enough to its destination. Then it will change its
 * state to traveling
 * 
 * TRAVELLING means that the robot is moving towards its destination. It will continuously
 * turn towards its destination and keep moving forward at each pass through the loop. When
 * the robot is close enough to the destination points, it will change its state to INIT.
 * 
 * While traveling, if the distance sensor indicates that it is 10 centimeters or less away
 * from something then it will change state to emergency and it will start the Obstacle
 * avoidance thread.
 * 
 * When it is in the Emergency state it will keep checking to see if the emergency is
 * resolved. it will be resolved when the obstacle avoidance thread is finished.
 * 
 * The state machine has been implemented because they are easier to work with when running
 * different threads.
 * 
 */

public class NavigatorJ extends BasicNavigator {

  enum State {
    INIT, TURNING, TRAVELLING, EMERGENCY
  };

  State state;

  private boolean isNavigating = false;

  private double destx, desty;

  final static int SLEEP_TIME = 50;

  UltrasonicPollerJ usSensor;

  public NavigatorJ(Odometer odo, UltrasonicPollerJ usSensor) {
    super(odo);
    this.usSensor = usSensor;
  }

  /*
   * TravelTo function which takes as arguments the x and y position in cm Will travel to designated
   * position, while constantly updating it's heading
   * 
   * When avoid=true, the nav thread will handle traveling. If you want to travel without avoidance,
   * this is also possible. In this case, the method in the Navigation class is used.
   * 
   */
  /**
   * TravelTo function which takes as arguments the x and y position in cm Will travel to designated
   * position, while constantly updating it's heading
   * 
   * When avoid=true, the nav thread will handle traveling. If you want to travel without avoidance,
   * this is also possible. In this case, the method in the Navigation class is used.
   * 
   * @param x
   * @param y
   * @param avoid
   */
  public void travelTo(double x, double y, boolean avoid) {
    if (avoid) {
      destx = x;
      desty = y;
      isNavigating = true;
    } else {
      super.travelTo(x, y);
    }
  }


  /**
   * Updates the h
   */
  private void updateTravel() {
    double minAng;

    minAng = getDestAngle(destx, desty);
    /*
     * Use the BasicNavigator turnTo here because minAng is going to be very small so just complete
     * the turn.
     */
    super.turnTo(minAng, false);
    this.setSpeeds(FAST, FAST);
  }
  /**
   * creates the state machine of the robot and updates it periodically. 
   */
  public void run() {
    //ObstacleAvoidance avoidance = null;
    state = State.INIT;
    while (true) {
      
      switch (state) {
        case INIT:
          if (isNavigating) {
            state = State.TURNING;
          }
          break;
        case TURNING:
          /*
           * Note: you could probably use the original turnTo() from BasicNavigator here without
           * doing any damage. It's cheating the idea of "regular and periodic" a bit but if you're
           * sure you never need to interrupt a turn there's no harm.
           * 
           * However, this implementation would be necessary if you would like to stop a turn in the
           * middle (e.g. if you were travelling but also scanning with a sensor for something...)
           * 
           */
          double destAngle = getDestAngle(destx, desty);
          turnTo(destAngle);
          if (facingDest(destAngle)) {
            setSpeeds(0, 0);
            state = State.TRAVELLING;
          }
          break;
        case TRAVELLING:
          if (checkEmergency()) { // order matters!
            state = State.EMERGENCY;
            //avoidance = new ObstacleAvoidance(this);
            //avoidance.start();
          } else if (!checkIfDone(destx, desty)) {
            updateTravel();
          } else { // Arrived!
            setSpeeds(0, 0);
            isNavigating = false;
            state = State.INIT;
          }
          break;
        case EMERGENCY:
          //if (avoidance.resolved()) {
          //  state = State.TURNING;
          //}
          break;
      }
      //Log.log(Log.Sender.Navigator, "state: " + state);
      try {
        Thread.sleep(SLEEP_TIME);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * when called this checks whether the sensor detects that it is closer than 10 cm
   * FOR FINAL PROJECT IT MAY BE USEFUL TO RENAME AND REPLACE WITH SOMETHING ELSE RIGHT NOW ALWAYS RETURNS FALSE 
   * 
   * @return true if distance from usSensor is less than 10 false otherwise
   */
  private boolean checkEmergency() {
    return false; 
    //return usSensor.getDistance() < 10;
  }

  /**
   * It sets the motor speed to slow and it sets the sign of the the speeds of the motors based on
   * the error (the destination angle minus the current angle).
   * 
   * @param angle
   */
  private void turnTo(double angle) {
    double error;
    error = angle - this.odometer.getAng();

    if (error < -180.0) {
      this.setSpeeds(-SLOW, SLOW);
    } else if (error < 0.0) {
      this.setSpeeds(SLOW, -SLOW);
    } else if (error > 180.0) {
      this.setSpeeds(SLOW, -SLOW);
    } else {
      this.setSpeeds(-SLOW, SLOW);
    }

  }

  /*
   * Go foward a set distance in cm with or without avoidance
   */
  /**
   * Go foward a set distance in cm with or without avoidance
   * 
   * @param distance
   * @param avoid
   */
  public void goForward(double distance, boolean avoid) {
    double x = odometer.getX() + Math.cos(Math.toRadians(this.odometer.getAng())) * distance;
    double y = odometer.getY() + Math.sin(Math.toRadians(this.odometer.getAng())) * distance;

    this.travelTo(x, y, avoid);

  }

  /**
   * 
   * @return whether or not the robot is currently travelling
   */
  public boolean isTravelling() {
    return isNavigating;
  }

}
