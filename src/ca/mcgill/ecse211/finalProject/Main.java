package ca.mcgill.ecse211.finalProject;


import java.text.DecimalFormat;
// import ca.mcgill.ecse211.USPoller.UltrasonicPollerJ;
import ca.mcgill.ecse211.colorClassification.ColorClassification;
import ca.mcgill.ecse211.finalProject.LightSensorCon;
import ca.mcgill.ecse211.finalProject.Display;
import ca.mcgill.ecse211.localization.LightLocalizer;
import ca.mcgill.ecse211.localization.USLocalizer;
import ca.mcgill.ecse211.navigation.Navigation;
import ca.mcgill.ecse211.navigation.Navigator;
import ca.mcgill.ecse211.navigation.TN_navigator;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometerExceptions;
import ca.mcgill.ecse211.odometer.OdometryCorrection;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class Main {
  // Motor Objects, and Robot related parameters

  // Set vehicle constants
  public static final double WHEEL_RAD = 2.1;
  public static final double TRACK = 16.2; // 10.55
  public static final double TILE_SIZE = 30.48;
  public static final int SC = 0;
  public static final int CAN_TO_SENSOR = 5;
  private static final double DISTANCE_CAN_OUT_OF_WAY = 12;

  public static final double SENSOR_LENGTH = -12;


  // Motor Objects, and navigator related parameters
  static final EV3LargeRegulatedMotor leftMotor =
      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
  public static final EV3LargeRegulatedMotor rightMotor =
      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));


  private static final TextLCD lcd = LocalEV3.get().getTextLCD();
  private static final Port usPort = LocalEV3.get().getPort("S4");
  static Odometer odometer = Odometer.getOdometer(leftMotor, rightMotor);
  static Navigation navigation = new Navigation(odometer, leftMotor, rightMotor);

  private static final EV3ColorSensor colorSamplerSensor =
      new EV3ColorSensor(LocalEV3.get().getPort("S1"));

  private static final EV3ColorSensor leftLight = new EV3ColorSensor(LocalEV3.get().getPort("S2"));
  private static final EV3ColorSensor rightLight = new EV3ColorSensor(LocalEV3.get().getPort("S3"));
  private static LightSensorCon leftLS = new LightSensorCon(leftLight, lcd);
  private static LightSensorCon rightLS = new LightSensorCon(rightLight, lcd);
  public static Navigator navigator = new Navigator(odometer, leftMotor, rightMotor);
  public static OdometryCorrection odoCorr =
      new OdometryCorrection(odometer, navigator, leftLS, rightLS);
  public static LightLocalizer lightLocalizer =
      new LightLocalizer(odometer, navigator, leftLS, rightLS);

  static SensorModes ultrasonicSensor = new EV3UltrasonicSensor(usPort);
  static SampleProvider usDistance = ultrasonicSensor.getMode("Distance");
  static int startingCorner;
  public static final EV3LargeRegulatedMotor grabMotor =
      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));

  public static final EV3MediumRegulatedMotor lightMotor =
      new EV3MediumRegulatedMotor(LocalEV3.get().getPort("D"));

  public static final String server_IP = "192.168.2.4";
  public static final int team_NUM = 14;


  public static final double StoppingDistanceFromCan = 3.5;



  // stuff we will be transfered for the project
  public static int RedTeam;
  public static int GreenTeam;

  public static int redCorner;
  public static int greenCorner;

  public static int Red_UR_x;
  public static int Red_LLx;
  public static int Red_UR_y;
  public static int Red_LL_y;

  public static int Green_UR_x;
  public static int Green_LL_x;
  public static int Green_UR_y;
  public static int Green_LL_y;

  public static int Island_UR_x;
  public static int Island_LL_x;
  public static int Island_UR_y;
  public static int Island_LL_y;


  public static int TNR_LL_x;
  public static int TNR_UR_x;
  public static int TNR_LL_y;
  public static int TNR_UR_y;


  public static int TNG_LL_x;
  public static int TNG_UR_x;
  public static int TNG_LL_y;
  public static int TNG_UR_y;



  public static int SZR_UR_x;
  public static int SZR_UR_y;

  public static int SZG_UR_x;
  public static int SZG_UR_y;


  public static int SZR_LL_y;
  public static int SZR_LL_x;
  public static int SZG_LL_x;
  public static int SZG_LL_y;



  public static int TN_LL_x;
  public static int TN_UR_x;
  public static int TN_LL_y;
  public static int TN_UR_y;



  static double TN_START_x;
  public static double TN_START_y;
  static double TN_END_y;
  static double TN_END_x;
  // end of stuff to be calculated
  public static int locate_cans_mSeconds = 50;
  public static int SZ_UR_y;
  public static int SZ_LL_y;
  public static int SZ_LL_x;
  public static int SZ_UR_x;
  public static int targetColor;


  public static double currentConsumption;

  private static WiFiClass wifi = new WiFiClass();
  public static TN_navigator tn_navigator = new TN_navigator(odometer, navigator, wifi);


  public static void main(String[] args) throws OdometerExceptions {


    WiFiClass.hadleProjectVariables();
    Display odometryDisplay = new Display(lcd); // No need to change
    // Odometer thread
    Thread odoThread = new Thread(odometer);
    odoThread.start();

    // Odometer display thread
    Thread odoDisplayThread = new Thread(odometryDisplay);
    odoDisplayThread.start();
    USLocalizer USLocalizer = new USLocalizer(odometer, leftMotor, rightMotor, usDistance);

    targetColor = 3;


    USLocalizer.localizeFallingEdge();
    lightLocalizer.initialLocalize();

    odometer.initialize(wifi.getStartingCorner(wifi.getTeam()));
    Sound.beep();
    Sound.beep();
    Sound.beep();

    navigator.setOdoCorrection(odoCorr);
    tn_navigator.setOdoCorrection(odoCorr);
    tn_navigator.travelToTunnel();
    tn_navigator.travelThroughTunnel();
    tn_navigator.travelTostartSet();


    Sound.beep();
    Sound.beep();
    Sound.beep();

    CanSearch.searchForCans();

    navigator.travelTo(SZ_UR_x, SZ_UR_y);

    Sound.beep();
    Sound.beep();
    Sound.beep();
    Sound.beep();
    Sound.beep();

  }



  /**
   * This method allows to scan the whole can and get 6 sampels to determine the colors detected it
   * will beep once if it can't find the target can it will beep twice if it can find the target can
   */
  public static int colorScan() {
    int targetCan = targetColor;
    ColorClassification colorClass = new ColorClassification(colorSamplerSensor);
    float[] rgb;
    int result = 4;
    rgb = colorClass.fetch();
    ColorClassification.rotateArmToLeft(30);
    rgb = colorClass.fetch();
    int i = ColorClassification.findMatch(rgb);
    if (i != 4) {
      result = i;
    }
    ColorClassification.rotateArmToLeft(30);
    rgb = colorClass.fetch();
    i = ColorClassification.findMatch(rgb);
    if (i != 4) {
      result = i;
    }
    ColorClassification.rotateArmToLeft(30);
    rgb = colorClass.fetch();
    i = ColorClassification.findMatch(rgb);
    if (i != 4) {
      result = i;
    }
    ColorClassification.rotateArmToLeft(30);
    rgb = colorClass.fetch();
    i = ColorClassification.findMatch(rgb);
    if (i != 4) {
      result = i;
    }
    ColorClassification.rotateArmToLeft(30);
    rgb = colorClass.fetch();
    i = ColorClassification.findMatch(rgb);
    if (i != 4) {
      result = i;
    }
    ColorClassification.rotateArmToLeft(30);
    rgb = colorClass.fetch();
    i = ColorClassification.findMatch(rgb);

    if (i != 4) {
      result = i;
    }


    ColorClassification.rotateArmToRight(180); // may be able to get rid later for efficiency
    return result;

  }



  private static void cross_tn(int x, int y) {
    navigator.travelTo((int) (0.5 * (x - 1)), (int) (0.5 * y));
    navigator.travelTo(x - 1, y);

    navigator.turnTo(0);
    navigator.travelDist(15, 200);
    navigator.turnTo(90);
    navigator.travelDist(4 * TILE_SIZE, 300);
  }


}


