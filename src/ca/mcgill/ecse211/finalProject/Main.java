package ca.mcgill.ecse211.finalProject;


import java.text.DecimalFormat;

import ca.mcgill.ecse211.USPoller.UltrasonicPollerJ;
import ca.mcgill.ecse211.WiFiClientExample.WiFiClass;
import ca.mcgill.ecse211.colorClassification.ColorClassification;
import ca.mcgill.ecse211.finalProject.LightSensorCon;
import ca.mcgill.ecse211.finalProject.Display;
import ca.mcgill.ecse211.localization.LightLocalizer;
import ca.mcgill.ecse211.localization.USLocalizer;
import ca.mcgill.ecse211.navigation.Navigation;
import ca.mcgill.ecse211.navigation.Navigator;
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
//Motor Objects, and Robot related parameters
	
// Set vehicle constants
public static final double WHEEL_RAD = 2.1;
public static final double TRACK = 10.55; // 9.8
public static final double TILE_SIZE = 30.48;
public static final int SC = 0;
public static final int CAN_TO_SENSOR = 5;
private static final double DISTANCE_CAN_OUT_OF_WAY = 12;
	
public static final double SENSOR_LENGTH = -4.9;


// Motor Objects, and navigator related parameters
static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));


private static final TextLCD lcd = LocalEV3.get().getTextLCD();
private static final Port usPort = LocalEV3.get().getPort("S4");
static Odometer odometer = Odometer.getOdometer(leftMotor, rightMotor);
static Navigation navigation;

private static final EV3ColorSensor colorSamplerSensor = new EV3ColorSensor(LocalEV3.get().getPort("S1"));

private static final EV3ColorSensor leftLight = new EV3ColorSensor(LocalEV3.get().getPort("S2"));
private static final EV3ColorSensor rightLight = new EV3ColorSensor(LocalEV3.get().getPort("S3"));
private static LightSensorCon leftLS = new LightSensorCon(leftLight,lcd);
private static LightSensorCon rightLS = new LightSensorCon(rightLight,lcd);
public static Navigator navigator = new Navigator(odometer,leftMotor,rightMotor);
private static OdometryCorrection odoCorr = new OdometryCorrection(odometer,navigator,leftLS,rightLS);





static SensorModes ultrasonicSensor = new EV3UltrasonicSensor(usPort);
static SampleProvider usDistance = ultrasonicSensor.getMode("Distance");
 
 

 
 // project variable
 static int startingCorner; 
 
 
 
 ////
 public static final EV3LargeRegulatedMotor grabMotor =
     new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
 
 public static final EV3MediumRegulatedMotor lightMotor =
     new EV3MediumRegulatedMotor(LocalEV3.get().getPort("D"));
 
 


public static final String server_IP = "192.168.2.24";
public static final int team_NUM = 14;
/**
 * this factor controlls how sensitive we are for deciding whether something is the same can with respect 
 * to the ultra sonic distance when we measured the can 
 * in other words it is a measure of how much we care about the fuzzyness of our measuremnt  of the can 
 * SOMEONE PLEASE TEST THIS TO MAKE SURE THAT IT IS A GOOD VALUE I THINK IT SHOULD BE FAIRLY LOW 
 */
public static final double distance_Factor_for_Cans = 0.01;
/**
 * can sensitivity is how far from a point should we consider a can to be 
 * in theory this should be the diameter of a can 5.5 
 * may need to be bigger in practice 
 */
public static final double canSensitivity = 5.5;
/**
 * this constant tells us whether a measurement is basically equal for the US sensor
 */
public static final double USDistance_Can_Equality_Radius = 3;

                                                    

 
 
 
 
 //stuff we will be transfered for the project
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
 public  static int SZR_UR_y; 
 
 public static int SZG_UR_x;
 public static int  SZG_UR_y;
 
 
 public static int SZR_LL_y;
 public static int SZR_LL_x;
 public static int SZG_LL_x;
 public static int SZG_LL_y;

 
 
 public static int TN_LL_x;
public  static int TN_UR_x;
public static int TN_LL_y;
 public static int TN_UR_y;
 
 
 
static double TN_START_x;// this shoudl be just before it 
/// thesee must be calculated as averages from TN_LL_y ect 
public static double TN_START_y;
static double TN_END_y;
static double TN_END_x;
// end of stuff to be calculated 
public static int locate_cans_mSeconds= 50;
public static UltrasonicPollerJ usPoller;
public static int SZ_UR_y;
public static int SZ_LL_y;
public static int SZ_LL_x;
public static int SZ_UR_x;




 
public static void main(String[] args) throws OdometerExceptions {
	
	
			
            WiFiClass.GetWIFIinfo();
            FinalProjectMethods.hadleProjectVariables();
			//Odometer objects
			
			Display odometryDisplay = new Display(lcd); // No need to change

			//Odometer thread
			Thread odoThread = new Thread(odometer);
			odoThread.start();

			//Odometer display thread
			Thread odoDisplayThread = new Thread(odometryDisplay);
			odoDisplayThread.start();
			
			USLocalizer USLocalizer = new USLocalizer(odometer, leftMotor, rightMotor, usDistance);
			
			
			USLocalizer.localizeFallingEdge();
			
			
			odometer.setXYT(0, 0, 0);
			navigator.setOdoCorrection(odoCorr);
			
			navigator.travelTo(0, 2);
			navigator.travelTo(2, 2);
			navigator.travelTo(2, 0);
			navigator.travelTo(0, 0);
			
			
			
			Sound.beep();
			
			//GEt me to begining of seach space as described
			FinalProjectMethods.searchForCans();
			
			navigator.directTravelTo(SZ_UR_x, SZ_UR_y);
			// do apropriate beeps 
			// 5 beeps for beta demo 
			
			
			
			
			
			
			//navigation.travelTodis(variable[0]*TILE_SIZE, (variable[0]+1)*TILE_SIZE);



}

}


