package ca.mcgill.ecse211.finalProject;

import java.util.Map;

import ca.mcgill.ecse211.WiFiClient.WifiConnection;
import lejos.hardware.Button;

/**
 * Modified from WifiExample. Sets all of the public variables for the final project using data from the server. 
 
 */
public class WiFiClass {

  // ** Set these as appropriate for your team and current situation **
  private static final String SERVER_IP = Main.server_IP;
  
  private static final int TEAM_NUMBER = Main.team_NUM;
  
  private static Map data;

  // Enable/disable printing of debug info from the WiFi class
  private static final boolean ENABLE_DEBUG_WIFI_PRINT = false;
  /**
   * sets all of the final project variables 
   */
  @SuppressWarnings("rawtypes")
	public WiFiClass() {
		// Store the data
		this.getData();

		// Clear the console
		System.out.flush();
	}
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
      data = conn.getData();

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
   // Button.waitForAnyPress();
  }
  
  
  
  
  
  /**
   * handles the project variables 
   * 
   */
  public static void hadleProjectVariables() {
    
 // setting all our variables depending on our team 
    if (Main.RedTeam==14) {
      //Main.targetColor= 3;
      Main.startingCorner= Main.redCorner;
      Main.SZ_UR_y= Main.SZR_UR_y;
      Main.SZ_LL_y= Main.SZR_LL_y;
      Main.SZ_LL_x= Main.SZR_LL_x;
      Main.SZ_UR_x = Main.SZR_UR_x;
      
      
      Main.TN_LL_x = Main.TNR_LL_x;
      Main.TN_UR_x = Main.TNR_UR_x;
      Main.TN_LL_y = Main.TNR_LL_y;
      Main.TN_UR_y= Main.TNR_UR_y;
      
      if (Math.abs(Main.TN_LL_x-Main.TN_UR_x)<1.1) {
        // then the tunnel is verticle 
        Main.TN_START_x= (Main.TN_LL_x+Main.TN_UR_x)/2;
        Main.TN_START_y= Main.TN_LL_y;
        Main.TN_END_x= (Main.TN_LL_x+Main.TN_UR_x)/2;
        Main.TN_END_y = Main.TN_UR_y; 
        
      } else {
        // the tunnel is horizontal 
        Main.TN_START_x = Main.TN_LL_x; 
        Main.TN_START_y= (Main.TN_LL_y+Main.TN_UR_y)/2;
        Main.TN_END_x= Main.TN_UR_x;
        Main.TN_END_y= (Main.TN_LL_y+Main.TN_UR_y)/2;
        
      }
      
      
      
      
      
    } else {
      Main.targetColor= 1;
      Main.startingCorner=Main.greenCorner;
      Main.SZ_UR_y= Main.SZG_UR_y;
      Main.SZ_LL_y= Main.SZG_LL_y;
      Main.SZ_LL_x= Main.SZG_LL_x;
      Main.SZ_UR_x = Main.SZG_UR_x;
      
      
      Main.TN_LL_x = Main.TNG_LL_x;
      Main.TN_UR_x = Main.TNG_UR_x;
      Main.TN_LL_y = Main.TNG_LL_y;
      Main.TN_UR_y= Main.TNG_UR_y;
      
      
      
      if (Math.abs(Main.TN_LL_x-Main.TN_UR_x)<1.1) {
        // then the tunnel is verticle 
        Main.TN_START_x= (Main.TN_LL_x+Main.TN_UR_x)/2;
        Main.TN_START_y= Main.TN_LL_y;
        Main.TN_END_x= (Main.TN_LL_x+Main.TN_UR_x)/2;
        Main.TN_END_y = Main.TN_UR_y; 
        
      } else {
        // the tunnel is horizontal 
        Main.TN_START_x = Main.TN_LL_x; 
        Main.TN_START_y= (Main.TN_LL_y+Main.TN_UR_y)/2;
        Main.TN_END_x= Main.TN_UR_x;
        Main.TN_END_y= (Main.TN_LL_y+Main.TN_UR_y)/2;
        
      }
    }
    
  }
  

	/**
	 * Stores all the data sent by the server JAR file into a Map object.
	 */
	public void getData() {

		// Initialize WifiConnection class
		WifiConnection conn = new WifiConnection(SERVER_IP, TEAM_NUMBER, ENABLE_DEBUG_WIFI_PRINT);

		// Connect to server and get the data, catching any errors that might
		// occur
		try {
			
			data = conn.getData();

		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	/**
	 * Gets the color of the team you are in.
	 * 
	 * @return Team color enumeration for the team you are in (green or red)
	 */
	public Team getTeam() {
		// Return the corresponding team colour for the team number (8)
		if (((Long) data.get("RedTeam")).intValue() == TEAM_NUMBER) {
			return Team.RED;
		} else if (((Long) data.get("GreenTeam")).intValue() == TEAM_NUMBER) {
			return Team.GREEN;
		} else {
			return null;
		}
	}

	/**
	 * Gets the starting corner of the given team.
	 * @param team green or red team
	 * @return starting corner (0,1,2,3)
	 */
	public int getStartingCorner(Team team) {
		// Check which team we are
		if (team == Team.RED) {
			return ((Long) data.get("RedCorner")).intValue();
		} else if (team == Team.GREEN) {
			return ((Long) data.get("GreenCorner")).intValue();
		}
		return -1;
	}

	/**BETA DEMO
	 * Gets the starting corner coordinates of your team.
	 * coords(x,y)
	 * @param startingCorner 0,1,2,3
	 * @return starting corner coordinates
	 */
	public int[] getStartingCornerCoords(int startingCorner) {
		int[] coords = { 0, 0 };
		switch (startingCorner) {
		case 0:
			coords[0] = 1;
			coords[1] = 1;
			break;
		case 1:
			coords[0] = 14;
			coords[1] = 1;
			break;
		case 2:
			coords[0] = 14;
			coords[1] = 8;
			break;
		case 3:
			coords[0] = 1;
			coords[1] = 8;
			break;
		}
		return coords;
	}

	/**
	 * Gets the coordinates of the home zone depending team color
	 * @param team green or red team
	 * @return 2-D array containing coordinates of the 4 corners of the home zone
	 */
	public int[][] getHomeZone(Team team) {
		int llx,lly,urx,ury;
		switch(team) {
		case GREEN:
			// Get coords of green zone
			llx = ((Long) data.get("Green_LL_x")).intValue();
			lly = ((Long) data.get("Green_LL_y")).intValue();
			urx = ((Long) data.get("Green_UR_x")).intValue();
			ury = ((Long) data.get("Green_UR_y")).intValue();

			// Corner convention:
			// [0] = Lower Left
			// [1] = Lower Right
			// [2] = Upper Right
			// [3] = Upper Left
			int[][] greenZone = { { llx, lly }, { urx, lly }, { urx, ury },{ llx, ury } };

			return greenZone;
		case RED:
			// Get coords of red zone
			llx = ((Long) data.get("Red_LL_x")).intValue();
			lly = ((Long) data.get("Red_LL_y")).intValue();
			urx = ((Long) data.get("Red_UR_x")).intValue();
			ury = ((Long) data.get("Red_UR_y")).intValue();

			// Corner convention:
			// [0] = Lower Left
			// [1] = Lower Right
			// [2] = Upper Right
			// [3] = Upper Left
			int[][] redZone = { { llx, lly }, { urx, lly }, { urx, ury },{ llx, ury } };

			return redZone;

		default : 
			return  null;
		}

	}


	/**
	 * Gets the coordinates of the tunnel.
	 * @param team team green or red
	 * @return 2-D array containing coordinates of the 4 corners of the tunnel
	 */
	public int[][] getTunnelZone(Team team) {
		int llx, lly, urx, ury;
		switch (team) {
		case RED:
			// Get coords of red search zone
			llx = ((Long) data.get("TNR_LL_x")).intValue();
			lly = ((Long) data.get("TNR_LL_y")).intValue();
			urx = ((Long) data.get("TNR_UR_x")).intValue();
			ury = ((Long) data.get("TNR_UR_y")).intValue();

			// Corner convention:
			// [0] = Lower Left
			// [1] = Lower Right
			// [2] = Upper Right
			// [3] = Upper Left
			int[][] redTunnelZone = { { llx, lly }, { urx, lly },{ urx, ury }, { llx, ury } };

			return redTunnelZone;

		case GREEN:
			// Get coords of red search zone
			llx = ((Long) data.get("TNG_LL_x")).intValue();
			lly = ((Long) data.get("TNG_LL_y")).intValue();
			urx = ((Long) data.get("TNG_UR_x")).intValue();
			ury = ((Long) data.get("TNG_UR_y")).intValue();

			// Corner convention:
			// [0] = Lower Left
			// [1] = Lower Right
			// [2] = Upper Right
			// [3] = Upper Left
			int[][] greenTunnelZone = { { llx, lly }, { urx, lly },{ urx, ury }, { llx, ury } };

			return greenTunnelZone;
		default:
			return null;
		}
	}

	/**
	 * Determines the orientation of the tunnel
	 * vertical(along y axis) horizontal(along x-axis)
	 * @return boolean true for vertical, false for horizontal
	 */
	public boolean isTunnelVertical(Team team) {

		int [][] tunnelZone = this.getTunnelZone(team);
		int [][] teamZone = this.getHomeZone(team);
		// [0] = Lower Left
		// [1] = Lower Right
		// [2] = Upper Right
		// [3] = Upper Left
		switch(this.getStartingCorner(team)) {
		case 0:{
			//compare lly of tunnel to ury of team zone 
			//compare llx & urx of tunnel to urx of team zone
			if(tunnelZone[0][1] <= teamZone[2][1] && tunnelZone[1][1] <= teamZone[2][1]) {
				if(tunnelZone[0][0] >= teamZone[3][0] &&tunnelZone[1][0]<= teamZone[2][0]) {
					return true;
				}
			}
			return false;
		}
		case 1:{
			if(tunnelZone[0][1] <= teamZone[2][1] && tunnelZone[1][1] <= teamZone[2][1]) {
				if(tunnelZone[0][0] >= teamZone[3][0] &&tunnelZone[1][0]<= teamZone[2][0]) {
					return true;
				}
			}
			return false;
		}
		case 2:{
			if(tunnelZone[2][1] >= teamZone[0][1] && tunnelZone[3][1] >= teamZone[0][1]) {
				if(tunnelZone[2][0] <= teamZone[1][0] &&tunnelZone[3][0]>= teamZone[0][0]) {
					return true;
				}
			}
			return false;
		}
		case 3:{
			if(tunnelZone[2][1] >= teamZone[0][1] && tunnelZone[3][1] >= teamZone[0][1]) {
				if(tunnelZone[2][0] <= teamZone[1][0] &&tunnelZone[3][0]>= teamZone[0][0]) {
					return true;
				}
			}
			return false;
		}
		default: return false;
		}
	}

	/**
	 * Determines the size of the tunnel in number of tiles covered
	 * @return size of the tunnel 
	 */
	public int getTunnelSize(Team team) {
		int tunnelSize = 0;
		int llx, lly, urx, ury;
		switch (team) {
		case RED:
			// Get coords of red search zone
			llx = ((Long) data.get("TNR_LL_x")).intValue();
			lly = ((Long) data.get("TNR_LL_y")).intValue();
			urx = ((Long) data.get("TNR_UR_x")).intValue();
			ury = ((Long) data.get("TNR_UR_y")).intValue();

			if(urx-llx >= ury - lly) {
				return (urx-llx);
			}
			else {
				return (ury - lly);
			}


		case GREEN:
			// Get coords of red search zone
			llx = ((Long) data.get("TNG_LL_x")).intValue();
			lly = ((Long) data.get("TNG_LL_y")).intValue();
			urx = ((Long) data.get("TNG_UR_x")).intValue();
			ury = ((Long) data.get("TNG_UR_y")).intValue();

			if(urx-llx >= ury - lly) {
				return (urx-llx);
			}
			else {
				return (ury - lly);
			}

		default:
			return 0;
		}

	}


	
	public int[] getstartSet(Team team) {
		int SZR_LL_x,SZR_LL_y,SZG_LL_x,SZG_LL_y;

		switch (team) {
		case RED:
			// Get coords of red search zone
			SZR_LL_x = ((Long) data.get("SZR_LL_x")).intValue();
			SZR_LL_y = ((Long) data.get("SZR_LL_y")).intValue();


			

			int[] redstartSet = {SZR_LL_x, SZR_LL_y};

			return redstartSet;

		case GREEN:
			// Get coords of red search zone
			SZG_LL_x = ((Long) data.get("SZG_LL_x")).intValue();
			SZG_LL_y = ((Long) data.get("SZG_LL_y")).intValue();

			

			int[]greenstartSet = {SZG_LL_x,SZG_LL_y};

			return greenstartSet;

		default:
			return null;
		}
	}
	/**
	 * Gets the closest corner of the tunnel to startset
	 * @param team team green or red
	 * 
	 */
	public int[] getClosestCornerToSS(Team team) {
		int[] coords = { 0, 0, 0 };
		int [] startSet = getstartSet(team);
		int[][] tunnelZone = getTunnelZone(team);
		// Corner convention:
		// [0] = Lower Left
		// [1] = Lower Right
		// [2] = Upper Right
		// [3] = Upper Left
		double min = Double.MAX_VALUE;

		for(int i =0; i < 4; i++) {
			int x =Math.abs(startSet[0]-tunnelZone[i][0]);
			int y =Math.abs(startSet[1]-tunnelZone[i][1]);
			double hypo = Math.sqrt((x*x+y*y));
			if(min > hypo ) {
				min = hypo;
				coords[0]=tunnelZone[i][0];
				coords[1]=tunnelZone[i][1];
				coords[2]=i;

			}
		}
		return coords;

	}
	/**
	 * Gets the closest corner of the tunnel to starting corner
	 * @param team team green or red
	 * 
	 */
	public int[] getClosestCornerToSC(Team team) {
		int[] coords = { 0, 0, 0 };
		int [] sc = getStartingCornerCoords(this.getStartingCorner(team));
		int[][] tunnelZone = getTunnelZone(team);
		// Corner convention:
		// [0] = Lower Left
		// [1] = Lower Right
		// [2] = Upper Right
		// [3] = Upper Left
		double min = Double.MAX_VALUE;

		for(int i =0; i < 4; i++) {
			int x =Math.abs(sc[0]-tunnelZone[i][0]);
			int y =Math.abs(sc[1]-tunnelZone[i][1]);
			double hypo = Math.sqrt((x*x+y*y));
			if(min > hypo ) {
				min = hypo;
				coords[0]=tunnelZone[i][0];
				coords[1]=tunnelZone[i][1];
				coords[2]=i;
			}
		}
		return coords;
	}

	/**
	 * Checks if startSet is in front of the tunnel
	 * @param team team green or red
	 */
	public boolean checkSSPos(Team team) {
		int[] startSet = getstartSet(team);
		int[][] tunnelZone = getTunnelZone(team);
		int llx = tunnelZone[0][0];
		int lly = tunnelZone[0][1];
		int urx = tunnelZone[2][0];
		int ury = tunnelZone[2][1];


		if(isTunnelVertical(team)) {
			if(startSet[0]== llx || startSet[0] == urx) {
				return true;
			}
		}
		else {
			if(startSet[1]==lly || startSet[1]==ury) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Checks if tunnel is touching border
	 * @param team team green or red
	 */
	public boolean checkTunnel(Team team) {
		int[][] tunnelZone = getTunnelZone(team);
		int llx = tunnelZone[0][0];
		int lly = tunnelZone[0][1];
		int urx = tunnelZone[2][0];
		int ury = tunnelZone[2][1];


		if(isTunnelVertical(team)) {
			if( llx ==0 ||  urx == 8) {
				return true;
			}
		}
		else {
			if(lly == 0|| ury == 8) {
				return true;
			}
		}
		return false;
	}

}
