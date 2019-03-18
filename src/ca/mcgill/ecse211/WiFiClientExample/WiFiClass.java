package ca.mcgill.ecse211.WiFiClientExample;

import java.util.Map;

import ca.mcgill.ecse211.WiFiClient.WifiConnection;
import ca.mcgill.ecse211.finalProject.Main;
import lejos.hardware.Button;

/**
 * Modified from WifiExample. Sets all of the public variables for the final project using data from the server. 
 
 */
public class WiFiClass {

  // ** Set these as appropriate for your team and current situation **
  private static final String SERVER_IP = Main.server_IP;
      //"192.168.2.3";
  private static final int TEAM_NUMBER = Main.team_NUM;

  // Enable/disable printing of debug info from the WiFi class
  private static final boolean ENABLE_DEBUG_WIFI_PRINT = false;
  /**
   * sets all of the final project variables 
   */
  @SuppressWarnings("rawtypes")
  public static void GetWIFIinfo() {

   

    // Initialize WifiConnection class
    WifiConnection conn = new WifiConnection(SERVER_IP, TEAM_NUMBER, ENABLE_DEBUG_WIFI_PRINT);

    // Connect to server and get the data, catching any errors that might occur
    try {
      /*
       * getData() will connect to the server and wait until the user/TA presses the "Start" button
       * in the GUI on their laptop with the data filled in. Once it's waiting, you can kill it by
       * pressing the upper left hand corner button (back/escape) on the EV3. getData() will throw
       * exceptions if it can't connect to the server (e.g. wrong IP address, server not running on
       * laptop, not connected to WiFi router, etc.). It will also throw an exception if it connects
       * but receives corrupted data or a message from the server saying something went wrong. For
       * example, if TEAM_NUMBER is set to 1 above but the server expects teams 17 and 5, this robot
       * will receive a message saying an invalid team number was specified and getData() will throw
       * an exception letting you know.
       */
      Map data = conn.getData();

     
     

     
      
      
      //stuff we will be transfered for the project
      Main.RedTeam= ((Long) data.get("RedTeam")).intValue();
      Main.GreenTeam =((Long) data.get("GreenTeam")).intValue();
      
      Main.redCorner= ((Long) data.get("RedCorner")).intValue();
      Main.greenCorner= ((Long) data.get("GreenCorner")).intValue();
      
      Main.Red_UR_x = ((Long) data.get("Red_UR_x")).intValue();
      Main.Red_LLx = ((Long) data.get("Red_LL_x")).intValue();
      Main.Red_UR_y= ((Long) data.get("Red_UR_y")).intValue();
      Main.Red_LL_y= ((Long) data.get("Red_LL_y")).intValue();
      
      Main.Green_UR_x= ((Long) data.get("Green_UR_x")).intValue();
      Main.Green_LL_x= ((Long) data.get("Green_LL_x")).intValue();
      Main.Green_UR_y = ((Long) data.get("Green_UR_y")).intValue();
      Main.Green_LL_y= ((Long) data.get("Green_LL_y")).intValue();
      
      Main.Island_UR_x = ((Long) data.get("Island_UR_x")).intValue();
      Main.Island_LL_x= ((Long) data.get("Island_LL_x")).intValue();
      Main.Island_UR_y = ((Long) data.get("Island_UR_y")).intValue();
      Main.Island_LL_y = ((Long) data.get("Island_LL_y")).intValue();
      
      
      Main.TNR_LL_x = ((Long) data.get("TNR_LL_x")).intValue();
      Main.TNR_UR_x = ((Long) data.get("TNR_UR_x")).intValue();
      Main.TNR_LL_y = ((Long) data.get("TNR_LL_y")).intValue();
      Main.TNR_UR_y = ((Long) data.get("TNR_UR_y")).intValue();
      
      
      Main.TNG_LL_x = ((Long) data.get("TNG_LL_x")).intValue();
      Main.TNG_UR_x = ((Long) data.get("TNG_UR_x")).intValue();
      Main.TNG_LL_y= ((Long) data.get("TNG_LL_y")).intValue();
      Main.TNG_UR_y= ((Long) data.get("TNG_UR_y")).intValue();
      
      

      
      
      Main.SZR_UR_x = ((Long) data.get("SZR_UR_x")).intValue();
      Main.SZR_UR_y = ((Long) data.get("SZR_UR_y")).intValue(); 
      
      Main.SZG_UR_x = ((Long) data.get("SZG_UR_x")).intValue();
      Main.SZG_UR_y = ((Long) data.get("SZG_UR_y")).intValue();
      
      Main.SZR_LL_x = ((Long) data.get("SZR_LL_x")).intValue();
      Main.SZR_LL_y = ((Long) data.get("SZR_LL_y")).intValue(); 
      
      Main.SZG_LL_x = ((Long) data.get("SZG_LL_x")).intValue();
      Main.SZG_LL_y = ((Long) data.get("SZG_LL_y")).intValue();

 

      
      
      
      
      
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }

    // Wait until user decides to end program
    Button.waitForAnyPress();
  }
}
