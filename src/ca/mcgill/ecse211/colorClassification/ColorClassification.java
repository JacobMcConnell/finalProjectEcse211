/**
 * This class serves to detect and classify rings colors
 * 
 * @author Erdong Luo
 *
 */
package ca.mcgill.ecse211.colorClassification;


import java.text.DecimalFormat;
import ca.mcgill.ecse211.odometer.OdometerExceptions;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import lejos.robotics.SampleProvider;

public class ColorClassification {

  // Motor objects
  public static final EV3MediumRegulatedMotor lightMotor =
      new EV3MediumRegulatedMotor(LocalEV3.get().getPort("B"));
  private EV3ColorSensor colorSensor;

  // Light sensor objects
  private SampleProvider rgbValue;

  // Float arrays for Color data
  private float[] rgbData;

  // RGB Mean Values
  private static final float[][] mean =
      {{0.33517449f, 0.60333634f, 0.68564513f}, {0.40930347f, 0.84016509f, 0.30740707f},
          {0.87895914f, 0.46552525f, 0.103523332f}, {0.97694436f, 0.11901002f, 0.10200376f}};


  // Constructor
  public ColorClassification(EV3ColorSensor colorSensor) {
    this.colorSensor = colorSensor;
    rgbValue = colorSensor.getMode("RGB");
    rgbData = new float[rgbValue.sampleSize()];
  }



  /**
   * This method allows to detect the color of the can
   * 
   * @return (integer representing the color)
   */
  public int detect() {
    int color;
    do {
      color = findMatch(fetch());
      // System.out.println("stuck in detect()");
    } while (color == 4);
    return color;
  }

  // Return RGB values to rgbData
  /**
   * This method allows to collect rgb values
   * 
   * @return (array containing rgb values)
   */

  public float[] fetch() {
    rgbValue.fetchSample(rgbData, 0);
    return rgbData;
  }

  /**
   * This method allows to match the readings and the mean to determine the color detected
   * 
   * @param a RGB array
   * @return (integer representing color)
   */
  public static int findMatch(float array[]) {

    float euc =
        (float) Math.sqrt((Math.pow(array[0], 2) + Math.pow(array[1], 2) + Math.pow(array[2], 2)));

    // normalize
    float R = array[0] / euc;
    float G = array[1] / euc;
    float B = array[2] / euc;


    for (int i = 0; i < 4; i++) {
      float differenceR = Math.abs(R - (float) mean[i][0]);
      float differenceG = Math.abs(G - (float) mean[i][1]);
      float differenceB = Math.abs(B - (float) mean[i][2]);
      if (i == 0 && differenceR <= 0.14456052 && differenceG < 19272766
          && differenceB < 0.18959367) {
        return 0;
      } else if (i == 1 && differenceR < 0.27392922 && differenceG < 0.17782465
          && differenceB < 0.14737035) {
        return 1;
      } else if (i == 2 && differenceR < 0.07453081 && differenceG < 0.13094911
          && differenceB < 0.02612884) {
        return 2;
      } else if (i == 3 && differenceR < 0.14456052 && differenceG < 0.19272766
          && differenceB < 0.18959367) {
        return 3;
      }
    }
    return 4;
  }

  /**
   * turn color sensor right
   * 
   * @param degrees to turn right
   */
  public static void rotateArmToRight(int degrees) {
    lightMotor.setAcceleration(750);
    lightMotor.setSpeed(30);
    lightMotor.rotate(-degrees);
    lightMotor.setSpeed(0);

  }

  /**
   * turn color sensor left
   * 
   * @param degrees to turn left
   */
  public static void rotateArmToLeft(int degrees) {
    lightMotor.setAcceleration(750);
    lightMotor.setSpeed(30);
    lightMotor.rotate(degrees);
    lightMotor.setSpeed(0);

  }

  /**
   * This method allows to scan the whole can and get 6 samples to determine the color detected
   * 
   * @param a RGB array
   * @return (integer representing color)
   */
  public static int canClassify(float array[]) {
    int result = 4;
    rotateArmToLeft(30);
    int i = findMatch(array);
    if (i != 4) {
      result = i;
    }
    rotateArmToLeft(30);
    i = findMatch(array);
    if (i != 4) {
      result = i;
    }
    rotateArmToLeft(30);
    i = findMatch(array);
    if (i != 4) {
      result = i;
    }
    rotateArmToLeft(30);
    i = findMatch(array);
    if (i != 4) {
      result = i;
    }
    rotateArmToLeft(30);
    i = findMatch(array);
    if (i != 4) {
      result = i;
    }
    rotateArmToLeft(30);
    i = findMatch(array);
    if (i != 4) {
      result = i;
    }
    rotateArmToRight(180);
    return result;

  }
}
