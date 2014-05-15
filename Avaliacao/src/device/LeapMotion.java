package device;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.Type;

public class LeapMotion extends Listener
{
	//Enumeration containing all of the developed controlled modes.
	public enum ControlMode{HAND_WITHOUT_GESTURE, HAND_WITHOUT_GESTURE_INVERTED, HAND_WITH_SCREENTAP_GESTURE, 
							HANDS_WITH_KEYTAP_GESTURE, HANDS_WITH_GRABBING_GESTURE};

	//Variables related to the Leap Motion control.
	private Controller controller;
	private ControlMode choosenControlMode;
	private boolean isRightHanded;
	private Hand dominantHand = new Hand();
	private Hand auxiliaryHand = new Hand();
	private int pointerFingerID = -2;
	private Frame lastFrame = new Frame();

	//Position of the cursor controlled by the Leap Motion
	private int cursorPositionX = 0;
	private int cursorPositionY = 0;
	
	//Variable related with the touch zone, a functionality offered by the Leap Motion.
	private double touchZoneDistance = 0;
	
	//Variable that simulates a mouse click.
	private boolean pressOcurred = false;

	//Behavior of the function "onFrame()", a function belonging to the Leap Motion listener.
	private boolean keepExecutting = true;
	
	//Robot responsible for moving the pointer cursor and to simulate a mouse click.
	Robot cursor;
	
	//For testing purposes.
	final boolean debug = false;
		
	/**
	 * Constructor of Class LeapMotion.
	 * This class tries to emulate the mouse, allowing the Leap Motion device to take control of the pointer cursor 
	 * and perform "mouse clicks".
	 * 
	 * @param modeOfControl - Type of control the user intends to have.
	 * @param isRightHanded - Boolean indicating if the user is right handed.
	 */
	public LeapMotion(ControlMode modeOfControl, boolean isRightHanded)
	{
		this.isRightHanded = isRightHanded;
		this.choosenControlMode = modeOfControl;
	}

	/**
	 * Function responsible for starting the Robot that will move the cursor and enable the 
	 * respective listeners, gestures and configurations.
	 */
	public void initialize()
	{
		try 
		{
			cursor = new Robot();
		} 
		catch (AWTException e) 
		{
			System.err.println("It was not possible to create the Robot class. Without it, the cursor won't be able to move.\nNow exiting...");
			e.printStackTrace();
			System.exit(0);
		}
		
		//Creates the Leap Motion controller variable. This contains several information related to the device, including the frames captured.
		controller = new Controller();

		if(choosenControlMode == ControlMode.HANDS_WITH_KEYTAP_GESTURE)
		{
			activateKeyTapGesture();
		}
		else if(choosenControlMode == ControlMode.HAND_WITH_SCREENTAP_GESTURE)
		{
			activateScreenTapGesture();
		}

		System.out.println("Leap Motion has been initialized.");

		controller.addListener(this);

		//Since the class will be using listeners for controlling the cursor and inputing commands,
		//this thread just needs to keep the resources alive. To do so, an infinite loop is used.
		while(true)
		{}	
	}

	/**
	 * Function that returns the cursor current position on the X axis.
	 * 
	 * @return Cursor current X coordinate.
	 */
	public int getCursorPositionX()
	{ return this.cursorPositionX; }
	
	/**
	 * Function that returns the cursor current position on the Y axis.
	 *
	 * @return Cursor current Y coordinate.
	 */
	public int getCursorPositionY()
	{ return this.cursorPositionY; }

	/**
	 * Function that tells if "mouse button" is being pressed.
	 * 
	 * NOTE: Being the Leap Motion device, this "mouse button" is simulated.
	 * 
	 * NOTE2: Some gestures are instantaneous. In those, this function will always return false.
	 * 
	 * @return Boolean indicating if a " mouse button" is being pressed.
	 */
	public boolean isPressedOcurred()
	{
		return this.pressOcurred;
	}

	/**
	 * Returns the distance to an invisible, perpendicular plane to the Leap Motion.
	 * The Touch Zone is a functionality is provided by the Leap Motion device. 
	 * 
	 * NOTE: The distance always varies between [-1.0,1.0]. 
	 * 
	 * NOTE2: Some control modes don't use this functionality. In those, 0 will always be returned.
	 * 
	 * NOTE3: If the "HAND_WITHOUT_GESTURE_INVERTED" control mode is being used, the returned value will 
	 * 		  be the opposite value (i.e., - current value). 
	 *  
	 * @return Value with the distance. 
	 */
	public double getTouchZone()
	{
		if(choosenControlMode == ControlMode.HAND_WITHOUT_GESTURE)
		{
			if(this.touchZoneDistance < 0){	return 0; }
			return this.touchZoneDistance;
		}
		else if(choosenControlMode == ControlMode.HAND_WITHOUT_GESTURE_INVERTED)
		{
			double value = (- this.touchZoneDistance);
			
			if(value < 0) 
			{ value = 0; }
			
			return value;
		}
		else
		{
			return 0;
		}	
	}

	/**
	 * Leap Motion listener extended.
	 * 
	 * When the device captures a frame this function occurs. The frame received is 
	 * analyzed in search of hands and gestures. 
	 * Depending on the data recovered the respective action will be taken.
	 */
	public void onFrame(Controller controller)
	{	
		//If the device is turned off, no information should be read.
		//This conditions exits due to the fact that the Leap Motion could not be turned off through code.
		if(!keepExecutting)
		{ return; }

		Frame capturedFrame = controller.frame();

		if(!capturedFrame.isValid())
		{
			if(debug){System.err.println("Captured image not valid!");}
			return;
		}

		if(capturedFrame.equals(lastFrame))
		{ return; }

		HandList detectedHands = capturedFrame.hands();

		if(detectedHands.isEmpty())
		{
			if(debug){System.err.println("No hands were detected.");}
			return;
		}

		if(detectedHands.count() == 1)
		{
			dominantHand = detectedHands.get(0);

			if(!dominantHand.isValid())
			{
				if(debug){System.err.println("Invalid dominant hand...");}
				return;
			}
		}
		else if(detectedHands.count() == 2)
		{
			if( ( !detectedHands.get(0).isValid() ) || ( !detectedHands.get(1).isValid() ) )
			{ 
				if(debug){System.err.println("One of the detected hands is not valid.");}
				return; 
			}

			//Find which hand is the left and right.
			Hand leftHand = detectedHands.leftmost();
			Hand rightHand = detectedHands.rightmost();

			//Depending on the user preferences, define which hand is dominant and the auxiliary.
			if(leftHand.equals(rightHand))
			{
				if(debug){System.err.println("Left hand is the same as the right hand!!");}
				return;
			}

			if(isRightHanded)
			{
				dominantHand = rightHand;
				auxiliaryHand = leftHand;
			}
			else
			{
				dominantHand = leftHand;
				auxiliaryHand = rightHand;
			}
		}

		//Take the respective action depending on the control mode.
		if(choosenControlMode == ControlMode.HAND_WITH_SCREENTAP_GESTURE)
		{ 
			//User controls the cursor and simulates button presses with the same hand.
			//The button press is simulated by the SCREEN TAP gesture.
			typeControlScreenTap(controller); 
		}
		else if(choosenControlMode == ControlMode.HANDS_WITH_KEYTAP_GESTURE)
		{ 
			//User controls the cursor using his/her dominant hand and simulates button 
			//presses with the auxiliary hand by performing KEYTAP gesture.
			typeControlKeyTap(controller); 
		}
		else if(choosenControlMode == ControlMode.HAND_WITHOUT_GESTURE)
		{ 
			//User controls the cursor and simulates button presses with the same hand.
			//The button press is simulated by the touch zone distance. When "touching" a click will occur.
			typeControlTouchDistance(controller); 
		}
		else if(choosenControlMode == ControlMode.HAND_WITHOUT_GESTURE_INVERTED)
		{
			//User controls the cursor and simulates button presses with the same hand.
			//The button press is simulated by the touch zone distance. This time, a click will occur when not "touching" the 
			//touch zone.
			typeControlTouchDistanceInverted(controller);
		}
		else if(choosenControlMode == ControlMode.HANDS_WITH_GRABBING_GESTURE)
		{
			//User controls the cursor using his/her dominant hand and simulates button 
			//presses with the auxiliary hand by performing a grabbing gesture.
			typeControlHandsWithGrabbingGesture(controller, capturedFrame);
		}

		//Saves the current frame to be used in future comparisons.
		lastFrame = capturedFrame;
	}

	/**
	 * In this type of control a finger, from the dominant hand, closest to the screen (the farthest 
	 * from the hand center) is used to move the cursor and simulate the mouse button click.
	 * To perform the "mouse click" the SCREEN TAP Gesture is used. To do this, simply move the finger
	 * in the screen direction as if to touch it / press it.
	 * 
	 * For more information on the gesture: https://developer.leapmotion.com/documentation/java/api/Leap.ScreenTapGesture.html
	 * 
	 * In this type of control only one hand is required.
	 * 
	 * @param controller - Default device controller.
	 */
	private void typeControlScreenTap(Controller controller)
	{
		ScreenList availableScreens = controller.locatedScreens();
		
		if(availableScreens.isEmpty())
		{
			if(debug){System.err.println("No screen has been detected.");}
			return;
		}

		Pointable pointerFinger = dominantHand.pointables().frontmost();

		if(!pointerFinger.isValid())
		{
			if(debug){System.err.println("Invalid pointer finger!");}
			return;
		}

		Screen screen = availableScreens.get(0);

		Vector interscection = screen.intersect(pointerFinger, true, 1.0f);

		cursorPositionX = (int) ( screen.widthPixels() * interscection.getX() );
		cursorPositionY = (int) ( screen.heightPixels() * ( 1.0f - interscection.getY() ) );

		cursor.mouseMove(cursorPositionX, cursorPositionY);
		
		GestureList gestures = controller.frame().gestures();
		Gesture performedGesture = new Gesture();

		//If there are no gestures, no need to go any further.
		if(gestures.isEmpty())
		{ return; }

		if(gestures.count() == 1)
		{
			performedGesture = gestures.get(0);
		}
		else
		{
			for(int i = 0; i < gestures.count(); i++)
			{
				if( gestures.get(i).hands().get(0).equals(auxiliaryHand))
				{
					performedGesture = gestures.get(i);
				}
			}
		}

		if(!performedGesture.isValid())
		{
			if(debug){System.err.println("The performed gesture is invalid.");}
			return;
		}

		if(performedGesture.hands().count() != 1)
		{
			if(debug)
			{
				System.err.println( "More than one hand has performed the SCREEN TAP gesture.\n" +
						"Only one hand should be used and this should be the dominant hand." );
			}
			return;
		}

		if(!performedGesture.hands().get(0).equals(dominantHand))
		{
			if(debug){System.err.println("Only the dominant hand should perform gestures.");}
			return;
		}

		cursor.mousePress(InputEvent.BUTTON1_MASK);
		cursor.mouseRelease(InputEvent.BUTTON1_MASK);
	}
	
	/**
	 * Function that changes the Leap Motion configuration in order to detect the ScreenTap gestures.
	 */
	private void activateScreenTapGesture() 
	{
		//Alter the device configurations in order to detect the gesture with ease.
		if(!controller.config().setFloat("Gesture.ScreenTap.HistorySeconds", 0.5f) )
		{
			System.err.println("It was not possible to alter \"Gesture.ScreenTap.HistorySeconds\" configuration.");
			return;
		}

		if(! controller.config().setFloat("Gesture.ScreenTap.MinDistance", 0.2f) )
		{
			System.err.println("It was not possible to alter \"Gesture.ScreenTap.MinDistance\" configuration.");
			return;
		}

		if(! controller.config().setFloat("Gesture.ScreenTap.MinForwardVelocity", 0.1f) )
		{
			System.err.println("It was not possible to alter \"Gesture.ScreenTap.MinForwardVelocity\" configuration.");
			return;
		}

		controller.enableGesture(Type.TYPE_SCREEN_TAP);
	}

	/**
	 * Function that inhibits the Leap Motion device from detecting the ScreenTap gestures.
	 */
	private void deactivateScreenTapGesture() 
	{
		controller.enableGesture(Type.TYPE_SCREEN_TAP, false);
	}
	
	/**
	 * In this type of control a finger, from the dominant hand, closest to the screen (the farthest 
	 * from the hand center) is used to move the cursor.
	 * To simulate the a mouse button click, a finger from the auxiliary hand, closest to the screen (again,
	 * farthest from the hand center) must perform the gesture KEY TAP.
	 * To perform the Key Tap Gesture simply move the finger in the Leap Motion direction 
	 * as if going to press a key.
	 * 
	 * For more information on the gesture: https://developer.leapmotion.com/documentation/java/api/Leap.KeyTapGesture.html
	 * 
	 * In this type of control two hands are required.
	 * 
	 * @param controller - Default device controller.
	 */
	private void typeControlKeyTap(Controller controller)
	{
		ScreenList availableScreens = controller.locatedScreens();

		if(availableScreens.isEmpty())
		{
			if(debug){System.err.println("No screen was detected.");}
			return;
		}

		Pointable pointerFinger = dominantHand.pointables().frontmost();

		if(!pointerFinger.isValid())
		{
			if(debug){System.err.println("Invalid pointer finger!");}
			return;
		}

		Screen screen = availableScreens.get(0);

		Vector intersection = screen.intersect(pointerFinger, true, 1.0f);

		cursorPositionX = (int) ( screen.widthPixels() * intersection.getX() );
		cursorPositionY = (int) ( screen.heightPixels() * ( 1.0f - intersection.getY() ) );

		cursor.mouseMove(cursorPositionX, cursorPositionY);
	
		GestureList gestures = controller.frame().gestures();

		//If there are no gestures, no need to go any further.
		if(gestures.isEmpty())
		{ return; }

		Gesture performedGesture = new Gesture();

		if(gestures.count() == 1)
		{
			performedGesture = gestures.get(0);
		}
		else
		{
			for(int i = 0; i < gestures.count(); i++)
			{
				if( gestures.get(i).hands().get(0).equals(auxiliaryHand))
				{
					performedGesture = gestures.get(i);
				}
			}
		}

		if(!auxiliaryHand.isValid())
		{
			if(debug){System.err.println("Must place your auxiliary hand over the Leap Motion device.");}
			return;
		}

		if(!performedGesture.isValid())
		{
			if(debug){System.err.println("The performed gesture is invalid.");}
			return;
		}

		if(performedGesture.hands().count() != 1)
		{
			if(debug)
			{
				System.err.println("More than one hand has performed the KEY TAP gesture.\n" +
						"Only the dominant hand should be used.");
			}
			return;
		}

		if(!performedGesture.hands().get(0).equals(auxiliaryHand))
		{
			if(debug)
			{
				System.err.println( "Only the auxiliary hand should perform gestures.\n" +
						"The dominant hand is used only to control the cursor movement." );
			}
			return;
		}

		cursor.mousePress(InputEvent.BUTTON1_MASK);
		cursor.mouseRelease(InputEvent.BUTTON1_MASK);
	}
	
	/**
	 * Function that changes the Leap Motion configuration in order to detect the KeyTap gestures.
	 */
	private void activateKeyTapGesture() 
	{
		//Alter the device configurations in order to detect the gesture with ease.
		if(!controller.config().setFloat("Gesture.KeyTap.HistorySeconds", 0.5f) )
		{
			System.err.println("It was not possible to alter \"Gesture.KeyTap.HistorySeconds\" configuration.");
			return;
		}
		controller.config().save();

		if(!controller.config().setFloat("Gesture.KeyTap.MinDownVelocity", 5.0f) )
		{
			System.err.println("It was not possible to alter \"Gesture.KeyTap.MinDownVelocity\" configuration.");
			return;
		}
		controller.config().save();

		if(!controller.config().setFloat("Gesture.KeyTap.MinDistance", 20.0f) )
		{
			System.err.println("It was not possible to alter \"Gesture.KeyTap.MinDownVelocity\" configuration.");
			return;
		}
		controller.config().save();

		controller.enableGesture(Type.TYPE_KEY_TAP);
	}
	
	/**
	 * Function that inhibits the Leap Motion device from detecting the KeyTap gestures.
	 */
	private void deactivateKeyTapGesture() 
	{ 
		controller.enableGesture(Type.TYPE_KEY_TAP, false); 
	}

	/**
	 * In this type of control a finger, from the dominant hand, closest to the screen (the farthest 
	 * from the hand center) is used to move the cursor and simulate the mouse button click.
	 * To perform the "mouse click" the TOUCH DISTANCE is used.
	 * 
	 * For more information on the concept: https://developer.leapmotion.com/documentation/java/devguide/Leap_Touch_Emulation.html?highlight=touch%20distance
	 * 
	 * In this type of control only one hand is required.
	 * 
	 * @param controller - Default device controller.
	 */
	private void typeControlTouchDistance(Controller controlador)
	{
		ScreenList availableScreens = controlador.locatedScreens();

		if(availableScreens.isEmpty())
		{
			if(debug){System.err.println("No screen detected.");}
			return;
		}

		Pointable pointerFinger = dominantHand.pointables().frontmost();

		if(!pointerFinger.isValid())
		{
			if(debug){System.err.println("Invalid pointer finger!");}
			return;
		}

		Screen screen = availableScreens.get(0);

		Vector intersection = screen.intersect(pointerFinger, true, 1.0f);

		cursorPositionX = (int) ( screen.widthPixels() * intersection.getX() );
		cursorPositionY = (int) ( screen.heightPixels() * ( 1.0f - intersection.getY() ) );
		touchZoneDistance = pointerFinger.touchDistance();

		cursor.mouseMove(cursorPositionX, cursorPositionY);
		
		if(!pressOcurred && (touchZoneDistance <= 0.0) )
		{
			cursor.mousePress(InputEvent.BUTTON1_MASK);
			cursor.mouseRelease(InputEvent.BUTTON1_MASK);
			
			pressOcurred = true;
		}
		else if(pressOcurred && (touchZoneDistance >= 0.27) )
		{
			pressOcurred = false;
		}
	}

	/**
	 * In this type of control a finger, from the dominant hand, closest to the screen (the farthest 
	 * from the hand center) is used to move the cursor and simulate the mouse button click.
	 * To perform the "mouse click" the TOUCH DISTANCE is used. 
	 * In this function, when the finger from the auxiliary hand is not in "touching" state, 
	 * a click will occur.
	 * 
	 * For more information on the concept: https://developer.leapmotion.com/documentation/java/devguide/Leap_Touch_Emulation.html?highlight=touch%20distance
	 * 
	 * In this type of control only one hand is required.
	 * 
	 * @param controller - Default device controller.
	 */
	private void typeControlTouchDistanceInverted(Controller controller) 
	{
		ScreenList availableScreens = controller.locatedScreens();

		if(availableScreens.isEmpty())
		{
			if(debug){System.err.println("No screen detected.");}
			return;
		}

		Pointable pointerFinger = dominantHand.pointables().frontmost();

		if(!pointerFinger.isValid())
		{
			if(debug){System.err.println("Invalid pointer finger!");}
			return;
		}

		Screen screen = availableScreens.get(0);

		Vector intersection = screen.intersect(pointerFinger, true, 1.0f);

		cursorPositionX = (int) ( screen.widthPixels() * intersection.getX() );
		cursorPositionY = (int) ( screen.heightPixels() * ( 1.0f - intersection.getY() ) );
		touchZoneDistance = pointerFinger.touchDistance();

		cursor.mouseMove(cursorPositionX, cursorPositionY);
		
		if(!pressOcurred && (touchZoneDistance >= 0.0) )
		{
			cursor.mousePress(InputEvent.BUTTON1_MASK);
			cursor.mouseRelease(InputEvent.BUTTON1_MASK);

			pressOcurred = true;
		}
		else if(pressOcurred && (touchZoneDistance < -0.10) )
		{
			pressOcurred = false;
		}
	}

	/**
	 * In this type of control a finger, from the dominant hand, closest to the screen (the farthest 
	 * from the hand center) is used to move the cursor.
	 * 
	 * To simulate a "mouse button" click, the whole auxiliary hand must be close.
	 * To terminate it, the whole auxiliary hand must be opened.
	 * 
	 * In this type of control two hands are required.
	 * 
	 * @param controller - Default device controller.
	 */
	private void typeControlHandsWithGrabbingGesture(Controller controller, Frame frame)
	{
		ScreenList availableScreens = controller.locatedScreens();

		if(availableScreens.isEmpty())
		{
			if(debug){System.err.println("No screen was detected.");}
			return;
		}

		//If no pointables (fingers or other objects) are detected, do nothing.
		if(dominantHand.pointables().count() == 0)
		{
			return;
		}
		
		if(pointerFingerID == -2)
		{
			pointerFingerID = discoverPointingFinger();
		}
		
		Pointable pointerFinger = dominantHand.pointable(pointerFingerID);
		
		if(!pointerFinger.isValid())
		{
			pointerFingerID = -2;
			return;
		}
		
		if(!pointerFinger.isValid())
		{
			if(debug){System.err.println("Invalid pointer finger!");}
			return;
		}
		
		Screen screen = availableScreens.get(0);

		Vector intersection = screen.intersect(pointerFinger, true, 1.0f);

		cursorPositionX = (int) ( screen.widthPixels() * intersection.getX() );
		cursorPositionY = (int) ( screen.heightPixels() * ( 1.0f - intersection.getY() ) );
		
		/*
		float distanceToLeapMotionDetectionLimits = controller.devices().get(0).distanceToBoundary(pointerFinger.tipPosition());
		
		if( distanceToLeapMotionDetectionLimits < 80.0f )
		{
			if(debug){System.out.println("Threshold reached");}
			return;
		}
		 */
		
		cursor.mouseMove(cursorPositionX, cursorPositionY);
		
		if(!auxiliaryHand.isValid())
		{
			if(debug){System.err.println("Must place your auxiliary hand over the Leap Motion device.");}
			return;
		}
		
		if( (auxiliaryHand.fingers().count() <= 1) && !pressOcurred)
		{
			cursor.mousePress(InputEvent.BUTTON1_MASK);
			cursor.mouseRelease(InputEvent.BUTTON1_MASK);
			
			pressOcurred = !pressOcurred;
		}
		
		if( (auxiliaryHand.fingers().count() >= 4) && pressOcurred )
		{
			pressOcurred = !pressOcurred;
		}
	}

	
	private int discoverPointingFinger() 
	{
		PointableList apontadores = dominantHand.pointables();

		if(apontadores.count() == 1)
		{
			return apontadores.get(0).id();
		}
		else if(apontadores.count() > 1 && apontadores.count() <= 4)
		{
			if(isRightHanded)
			{
				return apontadores.leftmost().id();
			}
			else
			{
				return apontadores.rightmost().id();
			}
		}
		else if(apontadores.count() == 5)
		{
			if(isRightHanded)
			{
				for(int i = 0; i < apontadores.count(); i++)
				{
					Pointable thisPointable = apontadores.get(i);
					if(	!thisPointable.equals(apontadores.frontmost()) && !thisPointable.equals(apontadores.leftmost()) && (thisPointable.tipPosition().getX() < apontadores.frontmost().tipPosition().getX()) )
					{
						return thisPointable.id();
					}
				}
			}
			else
			{
				for(int i = 0; i < apontadores.count(); i++)
				{
					Pointable thisPointable = apontadores.get(i);
					if(	!thisPointable.equals(apontadores.frontmost()) && !thisPointable.equals(apontadores.rightmost()) && (thisPointable.tipPosition().getX() > apontadores.frontmost().tipPosition().getX()) )
					{
						return thisPointable.id();
					}
				}
			}
		}
			
		return -2;
	}
	

	/**
	 * Function that terminates the Leap Motion functions.
	 * 
	 * NOTE: This does not turn off the device. All the information read will be 
	 *       discarded and won't cause any changes.
	 */
	public void turnOff()
	{
		keepExecutting = false;
	}

	/**
	 * Function that restarts the Leap Motion functions.
	 * 
	 * NOTE: This function does not initialize the Leap Motion Class. This ONLY allows to revert
	 * 		 the effects of the "turnOff" function, allowing values to be read again, thus permitting 
	 * 		 the mouse to be moved and "mouse clicks" to be performed.
	 */
	public void turnOn()
	{
		keepExecutting = true;
	}

	/**
	 * Function that exchanges the User dominant hand with the auxiliary hand. 
	 * In other words, if the user was right-handed, he/she now is left-handed (and vice-versa).
	 */
	public void changeDominantHand()
	{ this.isRightHanded = !this.isRightHanded; }

	/**
	 * Function responsible for changing the control mode.
	 * The order of change is as follows:
	 * 	-> 1 Hand, Screen Tap gesture;
	 *  -> 1 Hand, based on touch zone distance, no gesture;
	 *  -> 1 Hand, based on opposite value of the touch zone distance, no gesture;
	 *  -> 2 Hands, grabbing gesture;
	 *  -> 2 Hands, Key Tap gesture;
	 * 	
	 * 	NOTE: 2 hands, Key Tap gesture is the default.
	 * 
	 * 	@return THe current chosen control mode.
	 */
	public ControlMode changeControlMode()
	{
		switch(choosenControlMode)
		{
			case HANDS_WITH_KEYTAP_GESTURE:
				deactivateKeyTapGesture();
				choosenControlMode = ControlMode.HAND_WITH_SCREENTAP_GESTURE;
				activateScreenTapGesture();
				break;
	
			case HAND_WITH_SCREENTAP_GESTURE:
				deactivateScreenTapGesture();
				choosenControlMode = ControlMode.HAND_WITHOUT_GESTURE;
				break;
	
			case HAND_WITHOUT_GESTURE:
				choosenControlMode = ControlMode.HAND_WITHOUT_GESTURE_INVERTED;
				break;
	
			case HAND_WITHOUT_GESTURE_INVERTED:
				choosenControlMode = ControlMode.HANDS_WITH_GRABBING_GESTURE;
				break;
	
			case HANDS_WITH_GRABBING_GESTURE:
				choosenControlMode = ControlMode.HANDS_WITH_KEYTAP_GESTURE;
				activateKeyTapGesture();
				break;
		}

		return this.choosenControlMode;
	}

	/**
	 * Function that converts the given Control Mode to a string.
	 * 
	 * @param crtmd - The control mode to be converted into a string.
	 * 
	 * @return A string with a little description of the the given control mode.
	 */
	public static String controlModeToString(ControlMode crtmd)
	{
		switch(crtmd)
		{
			case HANDS_WITH_KEYTAP_GESTURE:
				return "2 hands, Key Tap gesture.";
	
			case HAND_WITH_SCREENTAP_GESTURE:
				return "1 hand, Screen Tap gesture.";
	
			case HAND_WITHOUT_GESTURE:
				return "1 hand, distance zone.";
	
			case HAND_WITHOUT_GESTURE_INVERTED:
				return "1 hand, distance zone, inverted.";
	
			case HANDS_WITH_GRABBING_GESTURE:
				return "2 hand, Grabbing gesture.";
		}

		//This should not happen.
		return "ERROR CONVERTING CONTROL MODE TO STRING";
	}

	//For testing purposes
	public static void main(String[] args) 
	{
		LeapMotion lm = new LeapMotion(ControlMode.HANDS_WITH_GRABBING_GESTURE,true);
		lm.initialize();
	}
}