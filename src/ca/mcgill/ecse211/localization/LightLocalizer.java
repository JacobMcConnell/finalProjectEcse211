package ca.mcgill.ecse211.localization;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.utility.Delay;
import ca.mcgill.ecse211.odometer.*;
import ca.mcgill.ecse211.finalProject.LightSensorCon;
import ca.mcgill.ecse211.navigation.Navigator;
import ca.mcgill.ecse211.navigation.*;
import ca.mcgill.ecse211.finalProject.*;

/** 
 *This class serves to localize the robot at the starting corner
 * using the two front light sensors.It basically moves to the intersection 
 * of the starting corner and correct its orientation every time it detects a line in 
 * its path to this intersection. The robot will beep 3 three when the robot is parallel
 * to the left wall
 * 
 * @author Erdong Luo
 */
public class LightLocalizer {

	//Constants
	private final int FORWARD_SPEED;
	private final int ROTATE_SPEED;
	private final static double TILE_SIZE = Main.TILE_SIZE;
	private final static double SENSOR_LENGTH = Main.SENSOR_LENGTH;

	private Odometer odometer;

	private static Navigator navigator;

	private static LightSensorCon leftLS;
	private static LightSensorCon rightLS;

	private double color = 0.30;
	/**
	 * This is a constructor for this lightLocalizer class
	 * @param odometer odometer of the navigator (singleton)
	 * @param leftLS left front light sensor that is used
	 * @param rightLS right front light sensor that is used
	 *@param navigator navigator controller to control the motors
	 */
	public LightLocalizer(Odometer odometer,Navigator navigator,LightSensorCon leftLS,LightSensorCon rightLS ) {
		this.odometer = odometer;
		this.navigator = navigator;
		this.FORWARD_SPEED = navigator.FORWARD_SPEED;
		this.ROTATE_SPEED = navigator.ROTATE_SPEED;		
		this.leftLS = leftLS;
		this.rightLS = rightLS;
	}

	/**
	 * This method localizes the navigator to the starting point using the two front light sensors
	 */
	public void initialLocalize() {

		// Start moving the navigator forward
		navigator.setSpeeds(200,200);

		navigator.moveForward();

		correct();

		navigator.travelDist(SENSOR_LENGTH,200);
		navigator.turnBy(90,true);

		navigator.setSpeeds(200,200);
		navigator.moveForward();

		correct();

		navigator.travelDist(SENSOR_LENGTH,200);

		navigator.turnBy(90, false); //90 is a bit too much

		Sound.beep();
		Sound.beep();
		Sound.beep();
	}

	/**
	 * This method serves to correct the orientation of the navigator with line detection
	 */
	private void correct() {

		boolean rightLineDetected = false;
		boolean leftLineDetected = false;

		// Move the navigator until one of the sensors detects a line
		while (!leftLineDetected && !rightLineDetected ) {
			if (rightLS.fetch() < color) {
				rightLineDetected = true;
				// Stop the right motor
				navigator.stopMoving(false, true);

			} else if (leftLS.fetch() < color) {
				leftLineDetected = true;

				// Stop the left motor
				navigator.stopMoving(true, false);
			}
		}

		// Keep moving the left/right motor until both lines have been detected
		while ((!leftLineDetected || !rightLineDetected)) {
			// If the other line detected, stop the motors
			if (rightLineDetected && leftLS.fetch() < color) {
				leftLineDetected = true;
				navigator.stopMoving();
			} else if (leftLineDetected && rightLS.fetch() < color) {
				rightLineDetected = true;
				navigator.stopMoving();
			}
		}

	}
}
