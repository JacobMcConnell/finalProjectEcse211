package ca.mcgill.ecse211.finalProject;


import ca.mcgill.ecse211.USPoller.UltrasonicPollerJ;
import ca.mcgill.ecse211.localization.LightLocalizer;
import ca.mcgill.ecse211.localization.USLocalizer;
import ca.mcgill.ecse211.navigation.Navigation;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometerExceptions;
import ca.mcgill.ecse211.odometer.OdometryCorrection;
import ca.mcgill.ecse211.stateNavigation.NavigatorJ;
import lejos.hardware.Button;
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
 public static final EV3LargeRegulatedMotor leftMotor =
     new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
 public static final EV3LargeRegulatedMotor rightMotor =
     new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));

 private static final TextLCD lcd = LocalEV3.get().getTextLCD();
 private static final Port usPort = LocalEV3.get().getPort("S4");

 private static final EV3ColorSensor colorSamplerSensor =
     new EV3ColorSensor(LocalEV3.get().getPort("S3"));

 // Set vehicle constants
 public static final double WHEEL_RAD = 2.1;
 public static final double TRACK = 16.4; // 9.8
 public static final double TILE_SIZE = 30.48;
 public static final int SC = 0;
 public static final int CAN_TO_SENSOR = 5;
 private static final double DISTANCE_CAN_OUT_OF_WAY = 12;

 static SensorModes ultrasonicSensor = new EV3UltrasonicSensor(usPort);
 static SampleProvider usDistance = ultrasonicSensor.getMode("Distance");
 
 
 
 static Odometer odometer;
 static Navigation navigation;
 
 
 //navigation 
 
 static NavigatorJ nav;
 
 // project variable
 static int startingCorner; 
 
 
 
 ////
 public static final EV3LargeRegulatedMotor grabMotor =
     new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
 
 public static final EV3MediumRegulatedMotor lightMotor =
     new EV3MediumRegulatedMotor(LocalEV3.get().getPort("B"));
 
 

 /**
  * This is an initialization of the light(color) sensor.
  * */
 public static final Port colourPort = LocalEV3.get().getPort("S1");
 
 /**
  * This is an initialization of the right light port.
  * */
 public static final Port rightLightPort = LocalEV3.get().getPort("S3");
 
 /**
  * This is an initialization of the left light port.
  * */
 public static final Port leftLightPort = LocalEV3.get().getPort("S2");
 
 //setting up the left light sensor 
public static EV3ColorSensor leftLightSensor = new EV3ColorSensor(leftLightPort); 
public static SampleProvider leftLightSampler = leftLightSensor.getRedMode(); 
public static float[] leftLightData = new float[leftLightSampler.sampleSize()]; 
                                                     
 




//setting up the right light sensor 
public static EV3ColorSensor rightLightSensor = new EV3ColorSensor(rightLightPort); 
public static SampleProvider rightLightSampler = rightLightSensor.getRedMode(); 
public static float[] rightLightData = new float[rightLightSampler.sampleSize()]; 
                                                    




//colour sensor objects
public static EV3ColorSensor colorSensor = new EV3ColorSensor(colourPort);
public static SampleProvider rgbSampleProvider = colorSensor.getMode("RGB");
public static float[] rgbData = new float[rgbSampleProvider.sampleSize()];


 
 
 
 
 //stuff we will be transfered for the project
 static int RedTeam;
 int GreenTeam;
 
 static int redCorner;
 static int greenCorner;
 
 int Red_UR_x;
 int Red_LLx;
 int Red_UR_y;
 int Red_LL_y; 
 
 int Green_UR_x;
 int Green_LL_x;
 int Green_UR_y; 
 int Green_LL_y;
 
 int Island_UR_x;
 int Island_LL_x;
 int Island_UR_y;
 int Island_LL_y;
 
 
 static int TNR_LL_x;
 static int TNR_UR_x;
 static int TNR_LL_y;
 static int TNR_UR_y;
 
 
 static int TNG_LL_x;
 static int TNG_UR_x;
 static int TNG_LL_y;
 static int TNG_UR_y;
 
 
 static int TN_LL_x;
 static int TN_UR_x;
 static int TN_LL_y;
 static int TN_UR_y;
 
 
 
 static int SZR_UR_x;
 static int SZR_UR_y; 
 
 static int SZG_UR_x;
 static int  SZG_UR_y;

 
 
private static double TN_START_x;// this shoudl be just before it 
/// thesee must be calculated as averages from TN_LL_y ect 
private static double TN_START_y;
private static double TN_END_y;
private static double TN_END_x;
// end of stuff to be calculated 
public static int locate_cans_mSeconds= 50;
public static UltrasonicPollerJ usPoller;
 



 


  public static void main(String[] args) {
    
    
    //pre lab testing for navigation (should also do localization ) 
    
 // Odometer objects
    try {
      odometer = Odometer.getOdometer(leftMotor, rightMotor, TRACK, WHEEL_RAD, startingCorner);
    } catch (OdometerExceptions e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
   
    // Odometer thread
    Thread odoThread = new Thread(odometer);
    odoThread.start();
    
    
    // nav = new NavigatorJ();
    
    
    
    
    
    
    /*
    
    
    //zeroeth thing we do is get all the data from the wifi class
    
    
    
    
    
    
    // TODO Auto-generated method stub
    //wait here for button press to start
    Button.waitForAnyPress();
    
    //first thing we do is set up all of the necessary sensors!!
    
    
    if (RedTeam==14) {
      startingCorner= redCorner;
    } else {
      startingCorner=greenCorner;
    }
    
    
    
    // Odometer objects
    try {
      odometer = Odometer.getOdometer(leftMotor, rightMotor, TRACK, WHEEL_RAD, startingCorner);
    } catch (OdometerExceptions e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
   
    // Odometer thread
    Thread odoThread = new Thread(odometer);
    odoThread.start();
    
    OdometryCorrection odometerCorrection= new OdometryCorrection();//may need to be passed
    // red light sensor and also odometer as inputs 
    
    odometerCorrection.run();
    
    
    
    // second thing we do is localize
    
    
    
    USLocalizer USLocalizer = new USLocalizer(odometer, leftMotor, rightMotor, usDistance);
    LightLocalizer lightLocatizer = new LightLocalizer(odometer, leftMotor, rightMotor);

    USLocalizer.localizeFallingEdge();
    lightLocatizer.localize();   
    
    odometer.initialize(); // fixes our localization to our starting corner i am assuming the 
    //numbers of the corners have not changed but make sure 
    
     for (int i =0 ; i <4; i++) { // for each of the cans of our color ie 4 times 
       
       // next we go to our bridge starting point 
       Navigation.travelToFP(TN_START_x,TN_START_y);
       Navigation.travelToFP(TN_END_x,TN_END_y);
       
       //begin search process
       // search process searches weighs cans checks color and does appropriate beeping 
       // until  can of right color is detected 
      /// it grabs the can and holds it makes apropriate beep 
       // it then returns 
       Navigation.travelToFP(TN_END_x,TN_END_y);
       
       
       //start the locate cans thread 
       
       // FinalProjectMethods.searchForCans();
       //Can closestCan =closestCan(odometer.x,odometer.y);
       //Navigation.travelToCan(closestCan.x,ClosestCan.Y);
       // while it travels to the can it should stop travelling when it almost reaches can
       //right before can
       // Light sensor scan and apropriate beep 
       int colorscanned; 
       // set the color of the can to color scanned 
       // test the weight of the can and make apropriate beep 
       // set the cans weight 
       // set the can to checked 
       // if can is not of our color set its final location to our location plus offset// another mehtods
       // if can is our color don't set it down
       //stop the locat cans thread 
       FinalProjectMethods.returnCanToStartingSquare();
       FinalProjectMethods.putCanDown();
       
       
       
       
       
       
       
       
       
     }
    */
    
    
    
    
    
    
    
    
    

  }

}


