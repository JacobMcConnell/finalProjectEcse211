package ca.mcgill.ecse211.navigation;

import ca.mcgill.ecse211.finalProject.LightSensorCon;
import ca.mcgill.ecse211.navigation.Navigator;
import ca.mcgill.ecse211.finalProject.Team;
import ca.mcgill.ecse211.WiFiClientExample.WiFiClass;
import ca.mcgill.ecse211.finalProject.*;
import ca.mcgill.ecse211.odometer.Odometer;
import ca.mcgill.ecse211.odometer.OdometerExceptions;
import ca.mcgill.ecse211.odometer.OdometryCorrection;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * This class is used for navigating the navigator accros the board depending on the placement 
 * of the tunnel,the starting corner and the startset. It allows to navigate from the starting point
 * to the startset through the tunnel and go back to the starting point
 *
 * @author Erdong Luo
 * 
 */
public class TN_navigator extends Thread {

	//navigator 
	private Navigator navigator;

	//Constants
	private final int FORWARD_SPEED;
	private final int ROTATE_SPEED;
	private final double TILE_SIZE;
	private final double SENSOR_LENGTH;
	private boolean navigate = true;


	//Odometer class
	private Odometer odometer;

	private OdometryCorrection odoCorr;

	//Wifi class
	private WiFiClass wifi;

	//Starting corner
	int startingCorner;
	int[] startingCornerCoords;

	//Tunnel coordinates
	private int[][] tunnelZone;

	//Search zone
	private int[][] searchZone;

	//Home zone
	private int[][] homeZone;

	//start set
	private int[] startSet;

	//Team
	private Team team;

	//Position of the navigator in respect to the tunnel
	private boolean upperLeft = false;
	private boolean upperRight = false;
	private boolean lowerLeft = false;
	private boolean lowerRight = false;

	private boolean goingTostartSet = true;
	
	/**
	 * This is a constructor for the TN_navigator class
	 * @param odometer odometer of the navigator (singleton)
	 * @param navigator navigator  to control the motors
	 * @param wifi wifi class that contains the game parameters
	 */
	public TN_navigator(Odometer odometer,Navigator navigator, WiFiClass wifi) {
		this.odometer = odometer;
		this.navigator = navigator;
		this.FORWARD_SPEED = navigator.FORWARD_SPEED;
		this.ROTATE_SPEED = navigator.ROTATE_SPEED;
		this.TILE_SIZE = navigator.TILE_SIZE;
		this.SENSOR_LENGTH = navigator.SENSOR_LENGTH;
		this.wifi = wifi;
		this.team = wifi.getTeam();
		this.startingCorner = wifi.getStartingCorner(team);
		this.startingCornerCoords = wifi.getStartingCornerCoords(startingCorner);
		this.homeZone = wifi.getHomeZone(team);
		this.tunnelZone = wifi.getTunnelZone(team);
		this.startSet = wifi.getstartSet(team);
	}


	/**
	 * A method to drive our vehicle to the tunnel
	 * Travels to the closest point to the tunnel
	 */
	public void travelToTunnel() {
				
		int[] closestCorner = wifi.getClosestCornerToSC(team);

		//tunnel is along y-axis (vertical)
		if(wifi.isTunnelVertical(team)) {
			switch(closestCorner[2]){
			case 0: // LL
				//travel to lower left corner of tunnel
				navigator.travelTo(closestCorner[0],startingCornerCoords[1]);
				navigator.travelTo(closestCorner[0], closestCorner[1]-1);
				lowerLeft = true;
				navigator.checkAngle(0); //assure that navigator is pointing 0
				break;
			case 1:	// LR
				//travel to the point under lower-right corner 
				navigator.travelTo(closestCorner[0],startingCornerCoords[1]);
				navigator.travelTo(closestCorner[0], closestCorner[1]-1);
				lowerRight = true;
				navigator.checkAngle(0); // assure that navigator is pointing 0
				break;
			case 2: // UR
				//travel to upper right corner
				navigator.travelTo(closestCorner[0],startingCornerCoords[1]);
				navigator.travelTo(closestCorner[0], closestCorner[1]+1);
				upperRight = true;
				navigator.checkAngle(180);
				break;
			case 3: // UL
				//travel to upper left corner
				navigator.travelTo(closestCorner[0],startingCornerCoords[1]);
				navigator.travelTo(closestCorner[0], closestCorner[1]+1);
				upperLeft = true;
				navigator.checkAngle(180);
				break;
			}	
		}
		//tunnel is along x-axis (horizontal)
		else {
			switch(closestCorner[2]){
			case 0: //LL
				//travel to the point under lower-left corner 
				navigator.travelTo(startingCornerCoords[0], closestCorner[1]);
				navigator.travelTo(closestCorner[0]-1,closestCorner[1]);
				lowerLeft = true;
				navigator.checkAngle(90); // assure that navigator is pointing 90
				break;
			case 1: //LR
				//travel to the point under lower-right corner 
				navigator.travelTo(startingCornerCoords[0], closestCorner[1]);
				navigator.travelTo(closestCorner[0]+1,closestCorner[1]);
				lowerRight = true;
				navigator.checkAngle(270); // assure that navigator is pointing 270
				break;				 
			case 2: //UR
				//travel to the point under upper-right corner 
				navigator.travelTo(startingCornerCoords[0], closestCorner[1]);
				navigator.travelTo(closestCorner[0]+1,closestCorner[1]);
				upperRight = true;
				navigator.checkAngle(270); // assure that navigator is pointing 270
				break;				 
			case 3: //UL
				//travel to the point under upper-left corner 
				navigator.travelTo(startingCornerCoords[0], closestCorner[1]);
				navigator.travelTo(closestCorner[0]-1,closestCorner[1]);
				upperLeft = true;
				navigator.checkAngle(90); // assure that navigator is pointing 90
				break;
			}
		}
	}

	/**
	 * A method to travel through the tunnel
	 * 
	 */
	public void travelThroughTunnel(){

		turnToTunnel();

		//Correct at initial line
		odoCorr.correct(odometer.getXYT()[2]);
		
		navigator.travelDist(TILE_SIZE,250);

		//Correct at line at the tunnel entrance
		odoCorr.correct(odometer.getXYT()[2]);

		//Move in the tunnel
		navigator.resetMotors();
		
		//navigator.setSpeeds(200,200);
		navigator.setAcceleration(250);
		int tunnelSize = wifi.getTunnelSize(team);
		navigator.travelDist(tunnelSize*TILE_SIZE,350);
		
		navigator.setAcceleration(0);
		
		//Correct at line at the tunnel exit
		odoCorr.correct(odometer.getXYT()[2]);
		
		navigator.travelDist(TILE_SIZE,250);

		//Correct at next line
		odoCorr.correct(odometer.getXYT()[2]);
		
		navigator.travelDist(SENSOR_LENGTH,150);

		turnOutTunnel(goingTostartSet);
		
		if(goingTostartSet) {
			goingTostartSet = false;
		}
		
	}
	/**
	 * A method to turn the navigator to the inside and place itself 
	 * to enter the tunnel entrance
	 * 
	 */
	public void turnToTunnel() {
		//Tunnel is vertical
		if(wifi.isTunnelVertical(team)) {
			if(upperLeft) {
				navigator.turnBy(90,false);
				odoCorr.correct(odometer.getXYT()[2]);
				navigator.travelDist(TILE_SIZE/2+SENSOR_LENGTH,150);
				navigator.turnBy(90,true);
				upperLeft = false; //reinitialize
			}
			//Red tunnel along y-axis
			else if (upperRight) {
				navigator.turnBy(90,true);
				odoCorr.correct(odometer.getXYT()[2]);
				navigator.travelDist(TILE_SIZE/2+SENSOR_LENGTH,150);
				navigator.turnBy(90,false);
				upperRight = false; //reinitialize
			}
			//Green tunnel along y-axis
			else if (lowerLeft) {
				navigator.turnBy(90,true);
				odoCorr.correct(odometer.getXYT()[2]);
				navigator.travelDist(TILE_SIZE/2+SENSOR_LENGTH,150);
				navigator.turnBy(90,false);
				lowerLeft = false; //reinitialize
			}
			//Green tunnel along x-axis
			else if(lowerRight) {
				navigator.turnBy(90,false);
				odoCorr.correct(odometer.getXYT()[2]);
				navigator.travelDist(TILE_SIZE/2+SENSOR_LENGTH,150);
				navigator.turnBy(90,true);
				lowerRight = false; //reinitialize
			}
		}
		else {
			if(upperLeft) {
				navigator.turnBy(90,true);
				odoCorr.correct(odometer.getXYT()[2]);
				navigator.travelDist(TILE_SIZE/2+SENSOR_LENGTH,150);
				navigator.turnBy(90,false);
				upperLeft = false; //reinitialize
			}
			//Red tunnel along y-axis
			else if (upperRight) {
				navigator.turnBy(90,false);
				odoCorr.correct(odometer.getXYT()[2]);
				navigator.travelDist(TILE_SIZE/2+SENSOR_LENGTH,150);
				navigator.turnBy(90,true);
				upperRight = false; //reinitialize
			}
			//Green tunnel along y-axis
			else if (lowerLeft) {
				navigator.turnBy(90,false);
				odoCorr.correct(odometer.getXYT()[2]);
				navigator.travelDist(TILE_SIZE/2+SENSOR_LENGTH,150);
				navigator.turnBy(90,true);
				lowerLeft = false; //reinitialize
			}
			//Green tunnel along x-axis
			else if(lowerRight) {
				navigator.turnBy(90,true);
				odoCorr.correct(odometer.getXYT()[2]);
				navigator.travelDist(TILE_SIZE/2+SENSOR_LENGTH,150);
				navigator.turnBy(90,false);
				lowerRight = false; //reinitialize
			}
		}
		

	}
	/**
	 * A method to turn to one of the two closest intersection of the tunnel 
	 * It will turn to the one closest to the startset or the starting corner
	 * It is used after traveling the tunnel (tunnel exit)
	 * 
	 */
	public void turnOutTunnel(boolean goingTostartSet) {
		int[] closestCorner;
		int corner;
		if(goingTostartSet) {
			closestCorner = wifi.getClosestCornerToSS(team);
			corner = closestCorner[2];
			//Check if startset is in front of the tunnel (true: rs is in front)
			if(wifi.checkSSPos(team)) {
				if(wifi.isTunnelVertical(team)) {
					switch(corner) {
					case 0: corner = 1; closestCorner[0]++;break;
					case 1:	corner = 0; closestCorner[0]--;break;
					case 2: corner = 3; closestCorner[0]--;break;
					case 3: corner = 2; closestCorner[0]++;break;
					}
				}
				else {
					switch(corner) {
					case 0: corner = 3; closestCorner[1]++; break;
					case 1:	corner = 2; closestCorner[1]++;break;
					case 2: corner = 1; closestCorner[1]--;break;
					case 3: corner = 0; closestCorner[1]--;break;
					}	
				}
			}
		}
		else {
			closestCorner = wifi.getClosestCornerToSC(team);
			corner = closestCorner[2];
		}

		if(wifi.isTunnelVertical(team)) {
			switch(corner) {
			case 0: //LL
				navigator.turnBy(90, true);
				odoCorr.correct(odometer.getXYT()[2]);
				navigator.travelDist(SENSOR_LENGTH,150);
				navigator.turnBy(90, false);
				//odoCorr.correct(odometer.getXYT()[2]);
				//navigator.travelDist(SENSOR_LENGTH);
				odometer.setXYT(closestCorner[0]*TILE_SIZE,(closestCorner[1]-1)*TILE_SIZE, 180);
				break;
			case 1: //LR
				navigator.turnBy(90, false);
				odoCorr.correct(odometer.getXYT()[2]);
				navigator.travelDist(SENSOR_LENGTH,150);
				navigator.turnBy(90, true);
				//odoCorr.correct(odometer.getXYT()[2]);
				//navigator.travelDist(SENSOR_LENGTH);
				odometer.setXYT(closestCorner[0]*TILE_SIZE,(closestCorner[1]-1)*TILE_SIZE, 180);
				break;
			case 2: //UR
				navigator.turnBy(90, true);
				odoCorr.correct(odometer.getXYT()[2]);
				navigator.travelDist(SENSOR_LENGTH,150);
				navigator.turnBy(90, false);
				//odoCorr.correct(odometer.getXYT()[2]);
				//navigator.travelDist(SENSOR_LENGTH);
				odometer.setXYT(closestCorner[0]*TILE_SIZE,(closestCorner[1]+1)*TILE_SIZE, 0);
				break;
			case 3: //UL
				navigator.turnBy(90, false);
				odoCorr.correct(odometer.getXYT()[2]);
				navigator.travelDist(SENSOR_LENGTH,150);
				navigator.turnBy(90, true);
				//odoCorr.correct(odometer.getXYT()[2]);
				//navigator.travelDist(SENSOR_LENGTH);
				odometer.setXYT(closestCorner[0]*TILE_SIZE,(closestCorner[1]+1)*TILE_SIZE, 0);
				break;
			}
		}
		else {
			switch(corner) {
			case 0: //LL
				navigator.turnBy(90, false);
				odoCorr.correct(odometer.getXYT()[2]);
				navigator.travelDist(SENSOR_LENGTH,150);
				navigator.turnBy(90, true);
				//odoCorr.correct(odometer.getXYT()[2]);
				//navigator.travelDist(SENSOR_LENGTH);
				odometer.setXYT((closestCorner[0]-1)*TILE_SIZE,closestCorner[1]*TILE_SIZE, 270);
				break;
			case 1: //LR
				navigator.turnBy(90, true);
				odoCorr.correct(odometer.getXYT()[2]);
				navigator.travelDist(SENSOR_LENGTH,150);
				navigator.turnBy(90, false);
				//odoCorr.correct(odometer.getXYT()[2]);
				//navigator.travelDist(SENSOR_LENGTH);
				odometer.setXYT((closestCorner[0]+1)*TILE_SIZE,closestCorner[1]*TILE_SIZE, 90);
				break;
			case 2: //UR
				navigator.turnBy(90, false);
				odoCorr.correct(odometer.getXYT()[2]);
				navigator.travelDist(SENSOR_LENGTH,150);
				navigator.turnBy(90, true);
				//odoCorr.correct(odometer.getXYT()[2]);
				//navigator.travelDist(SENSOR_LENGTH);
				odometer.setXYT((closestCorner[0]+1)*TILE_SIZE,closestCorner[1]*TILE_SIZE, 90);
				break;
			case 3: //UL
				navigator.turnBy(90, true);
				odoCorr.correct(odometer.getXYT()[2]);
				navigator.travelDist(SENSOR_LENGTH,150);
				navigator.turnBy(90, false);
				//odoCorr.correct(odometer.getXYT()[2]);
				//navigator.travelDist(SENSOR_LENGTH);
				odometer.setXYT((closestCorner[0]-1)*TILE_SIZE,(closestCorner[1])*TILE_SIZE, 270);
				break;
			}	
		}
	}

	/**
	 * A method to travel to tunnel exit
	 * 
	 */
	public void travelToTunnelExit() {
		int[] closestCorner = wifi.getClosestCornerToSS(team);
		int corner = closestCorner[2];

		if(wifi.checkSSPos(team)) {
			if(wifi.isTunnelVertical(team)) {
				switch(corner) {
				case 0: corner = 1; closestCorner[0]++;break;
				case 1:	corner = 0; closestCorner[0]--;break;
				case 2: corner = 3; closestCorner[0]--;break;
				case 3: corner = 2; closestCorner[0]++;break;
				}
			}
			else {
				switch(corner) {
				case 0: corner = 3; closestCorner[1]++;break;
				case 1:	corner = 2; closestCorner[1]++;break;
				case 2: corner = 1; closestCorner[1]--;break;
				case 3: corner = 0; closestCorner[1]--;break;
				}	
			}
		}
		if(wifi.isTunnelVertical(team)) {
			switch(corner) { 
			case 0:{ //LL
				//x then y
				int y = (int) Math.round(odometer.getXYT()[1] / TILE_SIZE);
				navigator.travelTo(closestCorner[0], y);
				navigator.travelTo(closestCorner[0], closestCorner[1]-1);
				navigator.checkAngle(0);
				lowerLeft = true;
				break;
			}
			case 1:{ //LR
				//x then y
				int y = (int) Math.round(odometer.getXYT()[1] / TILE_SIZE);
				navigator.travelTo(closestCorner[0], y);
				navigator.travelTo(closestCorner[0], closestCorner[1]-1);
				navigator.checkAngle(0);
				lowerRight = true;
				break;
			}
			case 2:{ //UR
				//x then y
				int y = (int) Math.round(odometer.getXYT()[1] / TILE_SIZE);
				navigator.travelTo(closestCorner[0], y);
				navigator.travelTo(closestCorner[0], closestCorner[1]+1);
				navigator.checkAngle(180);
				upperRight = true;
				break;
			}
			case 3:{ //UL
				//x then y
				int y = (int) Math.round(odometer.getXYT()[1] / TILE_SIZE);
				navigator.travelTo(closestCorner[0], y);
				navigator.travelTo(closestCorner[0], closestCorner[1]+1);
				navigator.checkAngle(180);
				upperLeft = true;
				break;
			}
			}
		}
		else {
			switch(corner) {
			case 0:{ //LL
				//y then x
				int x = (int) Math.round(odometer.getXYT()[0] / TILE_SIZE);
				navigator.travelTo(x, closestCorner[1]);
				navigator.travelTo(closestCorner[0]-1, closestCorner[1]);
				navigator.checkAngle(90);
				lowerLeft = true;
				break;
			}
			case 1:{ //LR
				int x = (int) Math.round(odometer.getXYT()[0] / TILE_SIZE);
				navigator.travelTo(x, closestCorner[1]);
				navigator.travelTo(closestCorner[0]+1, closestCorner[1]);
				navigator.checkAngle(270);
				lowerRight = true;
				break;
			}
			case 2:{ //UR
				int x = (int) Math.round(odometer.getXYT()[0] / TILE_SIZE);
				navigator.travelTo(x, closestCorner[1]);
				navigator.travelTo(closestCorner[0]+1, closestCorner[1]);
				navigator.checkAngle(270);
				upperRight = true;
				break;
			}
			case 3:{ //UL
				//y then x
				int x = (int) Math.round(odometer.getXYT()[0] / TILE_SIZE);
				navigator.travelTo(x, closestCorner[1]);
				navigator.travelTo(closestCorner[0]-1, closestCorner[1]);
				navigator.checkAngle(90);
				upperLeft = true;
				break;
			}
			}
		}
	}
	
	
	public void travelTostartSet() {
		int currx=0, curry=0;
		int [] closestCorner = wifi.getClosestCornerToSS(team);
		
		if(wifi.isTunnelVertical(team)) {
			switch(closestCorner[2]) { 
			case 0: //LL
				currx = (int) Math.round(odometer.getXYT()[0] / TILE_SIZE);
				navigator.travelTo(currx,startSet[1]);
				navigator.travelTo(startSet[0],startSet[1]);
				navigator.checkAngle(270);
				break;
			case 1: //LR
				currx = (int) Math.round(odometer.getXYT()[0] / TILE_SIZE);
				navigator.travelTo(currx,startSet[1]);
				navigator.travelTo(startSet[0],startSet[1]);
				navigator.checkAngle(90);
				break;
			case 2: //UR
				currx = (int) Math.round(odometer.getXYT()[0] / TILE_SIZE);
				navigator.travelTo(currx,startSet[1]);
				navigator.travelTo(startSet[0],startSet[1]);
				navigator.checkAngle(90);
				break;
			case 3: //UL
				currx = (int) Math.round(odometer.getXYT()[0] / TILE_SIZE);
				navigator.travelTo(currx,startSet[1]);
				navigator.travelTo(startSet[0],startSet[1]);
				navigator.checkAngle(270);
				break;
			}
		}
		//Tunnel along x-axis (horizontal)
		else {
			switch(closestCorner[2]) { 
			case 0: //LL
				curry = (int) Math.round(odometer.getXYT()[1] / TILE_SIZE);
				navigator.travelTo(startSet[0],curry);
				navigator.travelTo(startSet[0],startSet[1]);
				navigator.checkAngle(180);
				break;
			case 1: //LR
				curry = (int) Math.round(odometer.getXYT()[1] / TILE_SIZE);
				navigator.travelTo(startSet[0],curry);
				navigator.travelTo(startSet[0],startSet[1]);
				navigator.checkAngle(180);
				break;
			case 2: //UR
				curry = (int) Math.round(odometer.getXYT()[1] / TILE_SIZE);
				navigator.travelTo(startSet[0],curry);
				navigator.travelTo(startSet[0],startSet[1]);
				navigator.checkAngle(0);
				break;
			case 3: //UL
				curry = (int) Math.round(odometer.getXYT()[1] / TILE_SIZE);
				navigator.travelTo(startSet[0],curry);
				navigator.travelTo(startSet[0],startSet[1]);
				navigator.checkAngle(0);
				break;
			}
		}
	}
	
	/**
	 * A method to travel to the starting point
	 * 
	 */
	public void travelToStartingPoint() {
		int currx=0, curry=0;
		int [] closestCorner = wifi.getClosestCornerToSC(team);
		//closestCorner[0] : x - coords
		//closestCorner[1] : y - coords
		//closestCorner[2] : corner of the tunnel (LL(0),LR(1),UR(2),UL(3)

		//Tunnel along y-axis (vertical)
		if(wifi.isTunnelVertical(team)) {
			currx = (int) Math.round(odometer.getXYT()[0] / TILE_SIZE);
			navigator.travelTo(currx,startingCornerCoords[1]);
			navigator.travelTo(startingCornerCoords[0],startingCornerCoords[1]);
		}
		//Tunnel along x-axis (horizontal)
		else {
			curry = (int) Math.round(odometer.getXYT()[1] / TILE_SIZE);
			navigator.travelTo(startingCornerCoords[0],curry);
			navigator.travelTo(startingCornerCoords[0],startingCornerCoords[1]);
		}
	}

	/**
	 * Sets the OdometryCorrection object to be used by the navigator controller.
	 * 
	 * @param odoCorrection the OdometryCorrection object to be used
	 */
	public void setOdoCorrection(OdometryCorrection odoCorrection) {
		this.odoCorr = odoCorrection;
	}

}

