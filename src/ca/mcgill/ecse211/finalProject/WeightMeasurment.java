package ca.mcgill.ecse211.finalProject;

import java.util.Arrays;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * this class is used to measure the weight of the can
 * 
 * @author jacobmcconnell
 *
 */
public class WeightMeasurment {
  public static float current_variable = 0;
  // public static float[] current= new float[12300];
  public static float[] current = new float[12300];
  static int j = 0;
  static float median;
  public static final EV3LargeRegulatedMotor grabberMotor = Main.grabMotor;

  public static boolean altIsHeavy() {
    // something tacho count initial
    // close a standard amount or a passed in ammount
    // new tacho count - old
    // depending on size return true or false
    return false;

  }

  /**
   * this method checks if can is heavy
   * 
   * @return if can is heavy or not
   */
  public static boolean isHeavy() {
    current = new float[12300];
    j = 0;
    float weight = numericalWeight();
    boolean isHeavy = false;
    if (weight > 0.46) {
      isHeavy = true;
    }
    return isHeavy;

  }

  /**
   * this is used to calibrate the weight measurment system it will find the initial angle then it
   * will turn a set amount of time then it will find the final angle it returns the difference in
   * angles.
   * 
   * @return the difference between intiial and final angle
   */
  public static float numericalWeight() {
    rotateArmToRight(150);
    rotateArmToLeft(150);
    rotateArmToRight(165);
    rotateArmToLeft(150);
    rotateArmToRight(165);
    rotateArmToLeft(150);
    Arrays.sort(current);
    median = current[6150];
    return median;

  }

  /**
   * turn medium motor
   * 
   * @param degrees to turn right
   */
  public static void rotateArmToRight(int degrees) {

    grabberMotor.setAcceleration(750);
    grabberMotor.setSpeed(120);
    grabberMotor.rotate(-degrees, true);
    while (grabberMotor.isMoving()) {
      current_variable = LocalEV3.ev3.getPower().getBatteryCurrent();
      // current_variable= (double)LocalEV3.get().getPower().getBatteryCurrent()*10000;
      // 12300
      if (j < 12300) {
        current[j] = current_variable;
        // (double)LocalEV3.get().getPower().getBatteryCurrent()*10000;
        // LocalEV3.ev3.getPower().getMotorCurrent();
        j++;
      }
    }
    // for (int i = 0; i< current.length; i++) {
    // total = total + current[i];
    // }
    // average = total / 12300;
    grabberMotor.setSpeed(0);



  }



  /**
   * turn medium motor
   * 
   * @param degrees to turn left
   */
  public static void rotateArmToLeft(int degrees) {
    grabberMotor.setAcceleration(750);
    grabberMotor.setSpeed(100);
    grabberMotor.rotate(degrees, true);
    while (grabberMotor.isMoving()) {
      // LocalEV3.get().getPower().g
      current_variable = LocalEV3.ev3.getPower().getBatteryCurrent();
      // current_variable= (double)LocalEV3.get().getPower().getBatteryCurrent()*10000;
      // LocalEV3.ev3.getPower().getVoltageMilliVolt();
      if (j < 12300) {
        current[j] = current_variable;
        // current[j]=(double)LocalEV3.get().getPower().getBatteryCurrent()*10000;
        j++;
      }
    }
    // for (int i = 0; i< current.length; i++) {
    // total = total + current[i];
    // }
    // average = total / 12300;

    grabberMotor.setSpeed(0);

  }

}
