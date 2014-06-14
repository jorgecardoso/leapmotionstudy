package device;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;

import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.Type;

public class LeapMotion extends Listener
{
	//Enumeration containing all of the developed and created control modes.
	public enum ControlMode{HAND_WITHOUT_GESTURE, HAND_WITHOUT_GESTURE_INVERTED, HAND_WITH_SCREENTAP_GESTURE, 
							HANDS_WITH_KEYTAP_GESTURE, HAND_WITH_GRABBING_GESTURE ,HANDS_WITH_GRABBING_GESTURE };

	//Variables related to the Leap Motion control.
	private Controller controller;
	private ControlMode choosenControlMode;
	private boolean isRightHanded;
	private Hand dominantHand = new Hand();
	private Hand auxiliaryHand = new Hand();
	private Frame lastFrame = new Frame();
	Pointable pointerFinger = new Pointable();
	private int screenResolutionX = 0;
	private int screenResolutionY = 0;

	//Position of the cursor controlled by the Leap Motion
	private int cursorPositionX = 0;
	private int cursorPositionY = 0;
	
	//Variable related with the touch zone, a functionality offered by the Leap Motion.
	private double touchZoneDistance = 0;
	
	//Variable that simulates a mouse click.
	private boolean clickHappened = false;

	//Behavior of the function "onFrame()", a function belonging to the Leap Motion listener.
	private boolean keepExecutting = true;
	
	//Robot responsible for moving the pointer/cursor and to simulate a mouse click.
	Robot cursor;
	
	//For testing purposes.
	final boolean debug = false;
		
	/**
	 * Constructor of Class LeapMotion.
	 * <br>This class tries to emulate the mouse, allowing the Leap Motion device to take control of the pointer/cursor 
	 * and perform clicks.
	 * 
	 * @param modeOfControl - Type of control the user intends to have. See "ControlMode" enumeration for the available possibilities.
	 * @param isRightHanded - Boolean indicating if the user is right-handed (true) or not (false).
	 * @param screenResX - The screen resolution (width)
	 * @param screenResY - THe screen resolution (height) 
	 */
	public LeapMotion(ControlMode modeOfControl, boolean isRightHanded, int screenResX, int screenResY)
	{
		this.isRightHanded = isRightHanded;
		this.choosenControlMode = modeOfControl;
		this.screenResolutionX = screenResX;
		this.screenResolutionY = screenResY;
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

		//If a Keytap or Screentap gesture are to be used, extra configurations are need.
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
	 * Function that returns the cursor current screen position on the X (width) axis.
	 * 
	 * @return Cursor current X coordinate.
	 */
	public int getCursorPositionX()
	{ return this.cursorPositionX; }
	
	/**
	 * Function that returns the cursor current screen position on the Y (height) axis.
	 *
	 * @return Cursor current Y coordinate.
	 */
	public int getCursorPositionY()
	{ return this.cursorPositionY; }

	/**
	 * Function that tells if click is happening.
	 * 
	 * <p><b>Note:</b> Some gestures perform a click instantaneously. In those, this function will always return false.
	 * 
	 * @return Boolean indicating if a click is occurring.
	 */
	public boolean isClickHappening()
	{
		return this.clickHappened;
	}

	/**
	 * Returns the distance to an invisible, perpendicular plane to the Leap Motion.
	 * The Touch Zone is a functionality provided by the Leap Motion device. 
	 * 
	 * <p><b>Note:</b> The touh zone values always varies between [-1.0,1.0]. 
	 * 
	 * <p><b>Note2:</b> Some control modes don't use this functionality. In those, 0 will always be returned.
	 * 
	 * <p><b>Note3:</b> If the "HAND_WITHOUT_GESTURE_INVERTED" control mode is being used, the returned value will 
	 * 		  be the opposite value (i.e., -current value). 
	 *  
	 * @return Distance to the touch zone (double). 
	 */
	public double getTouchZone()
	{
		if(choosenControlMode == ControlMode.HAND_WITHOUT_GESTURE)
		{
			if(this.touchZoneDistance < 0)
			{return 0;}
			
			return this.touchZoneDistance;
		}
		else if(choosenControlMode == ControlMode.HAND_WITHOUT_GESTURE_INVERTED)
		{
			//Return the inverse value.
			double value = (- this.touchZoneDistance);
			
			if(value < 0) 
			{ value = 0; }
			
			return value;
		}
		else
		{
			//If using other control modes, the touch zone is always 0.
			return 0;
		}	
	}

	/**
	 * Leap Motion listener extended.
	 * 
	 * <p>When the device captures a frame this function occurs. The frame received is 
	 * analyzed in search of hands and gestures. 
	 * <br>Depending on the data recovered, the respective action will be taken.
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

		//If no new frame was captured, there is no need to analyze it. 
		if(capturedFrame.equals(lastFrame))
		{ return; }

		HandList detectedHands = capturedFrame.hands();

		if(detectedHands.isEmpty())
		{
			if(debug){System.err.println("No hands were detected.");}
			return;
		}

		//If only one hand was detected then that hand will be the dominant hand. 
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
		
		//Depending on the control mode being used, perform a different process for moving the screen pointer.
		if( (choosenControlMode == ControlMode.HAND_WITH_SCREENTAP_GESTURE) 	||
			(choosenControlMode == ControlMode.HAND_WITHOUT_GESTURE)			||
			(choosenControlMode == ControlMode.HAND_WITHOUT_GESTURE_INVERTED)	||
			(choosenControlMode == ControlMode.HANDS_WITH_KEYTAP_GESTURE)  	)
		{
			//In this option, a interaction box is used, allowing for a more precise pointing. The pointing is done 
			//using the index finger tip position. 
			if(!pointerFinger.isValid() || dominantHand.pointables().frontmost().equals(pointerFinger))
			{
				pointerFinger = discoverIndexFinger();
			}
			else
			{
				pointerFinger = dominantHand.pointable(pointerFinger.id());
			}
			
			if(!pointerFinger.isValid())
			{
				if(debug){System.err.println("Invalid pointer finger!");}
				return;
			}
			
			Vector stabilizedPosition = pointerFinger.stabilizedTipPosition();
			
			InteractionBox iBox = capturedFrame.interactionBox();
			Vector normalizedPosition = iBox.normalizePoint(stabilizedPosition);
			
			float x = normalizedPosition.getX() * screenResolutionX;
			float y = screenResolutionY - normalizedPosition.getY() * screenResolutionY;
			
			cursorPositionX = (int) x;
			cursorPositionY = (int) y;
			
			cursor.mouseMove(cursorPositionX, cursorPositionY);
		}
		else if( choosenControlMode == ControlMode.HANDS_WITH_GRABBING_GESTURE )
		{
			//In this option, a interaction box is used, allowing for a more precise pointing. The pointing is done 
			//using the index finger tip position. 
			if(!pointerFinger.isValid() || dominantHand.pointables().frontmost().equals(pointerFinger))
			{
				pointerFinger = discoverIndexFinger();
			}
			else
			{
				pointerFinger = dominantHand.pointable(pointerFinger.id());
			}
			
			if(!pointerFinger.isValid())
			{
				if(debug){System.err.println("Invalid pointer finger!");}
				return;
			}
			
			if( !(auxiliaryHand.isValid() && auxiliaryHand.pointables().count() > 2) )
			{
				Vector stabilizedPosition = pointerFinger.stabilizedTipPosition();
				
				InteractionBox iBox = capturedFrame.interactionBox();
				Vector normalizedPosition = iBox.normalizePoint(stabilizedPosition);
				
				float x = normalizedPosition.getX() * screenResolutionX;
				float y = screenResolutionY - normalizedPosition.getY() * screenResolutionY;
				
				cursorPositionX = (int) x;
				cursorPositionY = (int) y;
				
				cursor.mouseMove(cursorPositionX, cursorPositionY);
			}
		}
		else if( choosenControlMode == ControlMode.HAND_WITH_GRABBING_GESTURE )
		{
			if(! (dominantHand.fingers().count() >= 3) )
			{
				//In this option, similar to the first, an interactive box is used allowing for a more precise pointing. 
				//However, the pointing is done with the hand palm instead.  
				Vector stabilizedPosition = dominantHand.stabilizedPalmPosition();
				
				InteractionBox iBox = capturedFrame.interactionBox();
				Vector normalizedPosition = iBox.normalizePoint(stabilizedPosition);
				float x = normalizedPosition.getX() * screenResolutionX;
				float y = screenResolutionY - normalizedPosition.getY() * screenResolutionY;
				
				cursorPositionX = (int) x;
				cursorPositionY = (int) y;
				
				cursor.mouseMove(cursorPositionX, cursorPositionY);	
			}			
		}
		
		//Take the respective action depending on the control mode.
		if(choosenControlMode == ControlMode.HAND_WITH_SCREENTAP_GESTURE)
		{ 
			//User controls the cursor and simulates button presses with the same hand.
			//The click is simulated by the SCREEN TAP gesture.
			typeControlScreenTap(controller, capturedFrame); 
		}
		else if(choosenControlMode == ControlMode.HANDS_WITH_KEYTAP_GESTURE)
		{ 
			//User controls the cursor using his/her dominant hand and simulates a click 
			//by performing KEYTAP gesture with the auxiliary hand.
			typeControlKeyTap(controller, capturedFrame); 
		}
		else if(choosenControlMode == ControlMode.HAND_WITHOUT_GESTURE)
		{ 
			//Information required for this control mode.
			touchZoneDistance = pointerFinger.touchDistance();
			
			//User controls the cursor and simulates a click with the same hand.
			//The click is simulated thanks to the touch zone distance. When "touching" a click will occur.
			typeControlTouchDistance(controller); 
		}
		else if(choosenControlMode == ControlMode.HAND_WITHOUT_GESTURE_INVERTED)
		{
			//Information required for this control mode.
			touchZoneDistance = pointerFinger.touchDistance();
			
			//User controls the cursor and simulates a click with the same hand.
			//The click is simulated thanks to the touch zone distance. 
			//This time, a click will occur when not "touching".
			typeControlTouchDistanceInverted(controller);
		}
		else if(choosenControlMode == ControlMode.HANDS_WITH_GRABBING_GESTURE)
		{
			//User controls the cursor using his/her dominant hand and simulates  
			//a click with the auxiliary hand by performing a grabbing gesture.
			typeControlHandsWithGrabbingGesture(controller);
		}
		else if(choosenControlMode == ControlMode.HAND_WITH_GRABBING_GESTURE)
		{
			//User controls the cursor and simulates a click, with the dominant hand, by performing a grabbing gesture.
			typeControlHandWithGrabbingGesture(controller);
		}

		//Saves the current frame to be used in future comparisons.
		lastFrame = capturedFrame;
	}

	/**
	 * In this type of control a finger from the dominant hand, the closest to the screen (the farthest 
	 * from the hand center), is used to move the cursor and simulate the click.
	 * <br>To perform a click, the SCREEN TAP Gesture is used. 
	 * 
	 * <p>For more information on the gesture: <a href="https://developer.leapmotion.com/documentation/java/api/Leap.ScreenTapGesture.html">https://developer.leapmotion.com/documentation/java/api/Leap.ScreenTapGesture.html</a> 
	 * <p>In this type of control only one hand is required.
	 * 
	 * @param controller - Default device controller.
	 */
	private void typeControlScreenTap(Controller controller, Frame frame)
	{
		GestureList gestures = frame.gestures();
		Gesture performedGesture = new Gesture();

		//If there are no gestures, no need to go any further.
		if(gestures.isEmpty())
		{ return; }

		performedGesture = gestures.get(0);
		
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
						"Only one hand should do it (the dominant hand)." );
			}
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
		//Alter the device configurations in order to detect the gesture more easily.
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
	 * In this type of control a finger from the dominant hand, the closest to the screen (the farthest 
	 * from the hand center), is used to move the cursor.
	 * <br>To simulate a click, a finger from the auxiliary hand, the closest to the screen (again,
	 * farthest from the hand center), must perform the gesture KEY TAP.
	 * 
	 * <p>For more information on the gesture: <a href="https://developer.leapmotion.com/documentation/java/api/Leap.KeyTapGesture.html">https://developer.leapmotion.com/documentation/java/api/Leap.KeyTapGesture.html</a>
	 * 
	 * <p>In this type of control two hands are required.
	 * 
	 * @param controller - Default device controller.
	 */
	private void typeControlKeyTap(Controller controller, Frame frame)
	{
		GestureList gestures = frame.gestures();

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
			//Only gestures from the auxiliary hand should be regarded, as this is the hand that will perform the gesture.
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
		//Alter the device configurations in order to detect the gesture more easily.
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
	 * In this type of control a finger from the dominant hand, the closest to the screen (the farthest 
	 * from the hand center), is used to move the cursor and simulate a click.
	 * <br>To perform a click, the TOUCH DISTANCE is used.
	 * 
	 * <p>For more information on the concept: <a href="https://developer.leapmotion.com/documentation/java/devguide/Leap_Touch_Emulation.html?highlight=touch%20distance">https://developer.leapmotion.com/documentation/java/devguide/Leap_Touch_Emulation.html?highlight=touch%20distance</a>
	 * 
	 * <p>In this type of control only one hand is required.
	 * 
	 * @param controller - Default device controller.
	 */
	private void typeControlTouchDistance(Controller controlador)
	{
		if(!clickHappened && (touchZoneDistance <= 0.0) )
		{
			cursor.mousePress(InputEvent.BUTTON1_MASK);
			cursor.mouseRelease(InputEvent.BUTTON1_MASK);
			
			clickHappened = true;
		}
		else if(clickHappened && (touchZoneDistance >= 0.27) )
		{
			clickHappened = false;
		}
	}

	/**
	 * In this type of control a finger from the dominant hand, the closest to the screen (the farthest 
	 * from the hand center), is used to move the cursor and simulate a click.
	 * <br>To perform the click the TOUCH DISTANCE is used. 
	 * <br>In this function, a click will occur when the touch zone is not in the "touching" zone.
	 * 
	 * <p>For more information on the concept: <a href="https://developer.leapmotion.com/documentation/java/devguide/Leap_Touch_Emulation.html?highlight=touch%20distance">https://developer.leapmotion.com/documentation/java/devguide/Leap_Touch_Emulation.html?highlight=touch%20distance</a>
	 * 
	 * <p>In this type of control only one hand is required.
	 * 
	 * @param controller - Default device controller.
	 */
	private void typeControlTouchDistanceInverted(Controller controller) 
	{
		if(!clickHappened && (touchZoneDistance >= 0.0) )
		{
			cursor.mousePress(InputEvent.BUTTON1_MASK);
			cursor.mouseRelease(InputEvent.BUTTON1_MASK);

			clickHappened = true;
		}
		else if(clickHappened && (touchZoneDistance < -0.10) )
		{
			clickHappened = false;
		}
	}

	/**
	 * In this type of control a finger from the dominant hand, the closest to the screen 
	 * (the farthest from the hand center) is used to move the cursor.
	 * 
	 * <p>To simulate a click, the whole auxiliary hand must be opened.
	 * <br>To terminate it, the whole auxiliary hand must be closed.
	 * 
	 * <p>In this type of control two hands are required.
	 * 
	 * @param controller - Default device controller.
	 */
	private void typeControlHandsWithGrabbingGesture(Controller controller)
	{	
		if(!auxiliaryHand.isValid())
		{
			if(debug){System.err.println("Must place your auxiliary hand over the Leap Motion device.");}
			return;
		}
		
		if( (auxiliaryHand.fingers().count() <= 1) && clickHappened)
		{
			clickHappened = false;
		}
		
		if( (auxiliaryHand.fingers().count() >= 3) && !clickHappened )
		{
			cursor.mousePress(InputEvent.BUTTON1_MASK);
			cursor.mouseRelease(InputEvent.BUTTON1_MASK);
			
			clickHappened = true;
		}
	}
	
	/**
	 * In this type of control the dominant hand is used to move the cursor.
	 * 
	 * <p>To simulate a click, the whole dominant hand must be opened.
	 * <br>To terminate it, the whole auxiliary hand must be closed.
	 * 
	 * <p>In this type of control only one hand is required.
	 * 
	 * @param controller - Default device controller.
	 */
	private void typeControlHandWithGrabbingGesture(Controller controller)
	{	
		if(!dominantHand.isValid())
		{
			if(debug){System.err.println("Must place your dominant hand over the Leap Motion device.");}
			return;
		}
		
		if( (dominantHand.fingers().count() <= 1) && clickHappened)
		{
			clickHappened = false;
		}
		
		if( (dominantHand.fingers().count() >= 4) && !clickHappened )
		{
			cursor.mousePress(InputEvent.BUTTON1_MASK);
			cursor.mouseRelease(InputEvent.BUTTON1_MASK);
			
			clickHappened = true;
		}
	}

	/**
	 * Function responsible of discovering which finger is the index finger.
	 * 
	 * @return The (supposedly) index finger (as a pointable). 
	 */
	private Pointable discoverIndexFinger() 
	{
		PointableList apontadores = dominantHand.pointables();

		switch(apontadores.count())
		{
			case 1:
				return apontadores.get(0);
				
			case 2:
				//Assuming that only the Thumb and Index finger are being shown.
				return apontadores.frontmost();		
				
			case 3:
				//Assuming that the Thumb, Index and any (but only one) finger to the right of the Index finger.
				//This is necessary since, sometimes, the Leap Motion detects one of the hidden fingers unintencionally.
				for(int i = 0; i < 3; i++)	
				{
					Pointable thisPointable = apontadores.get(i);
						
					//If this finger is not the leftmost and rightmost then it's the Index finger.
					if( !thisPointable.equals(apontadores.leftmost()) && !thisPointable.equals(apontadores.rightmost()) )
					{
						return thisPointable;
					}
				}
				
			case 4:
				//Assume that the thumb is not being shown.
				if(isRightHanded)
				{
					return apontadores.leftmost();		
				}
				else
				{
					return apontadores.rightmost();
				}
				
			case 5:
				//Assume that all fingers are being shown and the middle finger is the frontmost.
				if(isRightHanded)
				{
					for(int i = 0; i < apontadores.count(); i++)
					{
						Pointable thisPointable = apontadores.get(i);
						if(	!thisPointable.equals(apontadores.frontmost()) && !thisPointable.equals(apontadores.leftmost()) && (thisPointable.tipPosition().getX() < apontadores.frontmost().tipPosition().getX()) )
						{
							return thisPointable;
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
							return thisPointable;
						}
					}
				}
				
			default:
				//If no fingers are detected return an empty pointable.
				return new Pointable();
		}
	}
	

	/**
	 * Function that "terminates" the Leap Motion functions.
	 * 
	 * <p><b>Note:</b> This does not turn off the device, since it was not possible to stop the device through code.
	 * 		 This function simply changes the "onFrame" function behavior by having all the information read discarded.
	 * 		 With that, no actions (click or pointer movement) are executed.
	 */
	public void turnOff()
	{
		keepExecutting = false;
	}

	/**
	 * Function that "restarts" the Leap Motion functions.
	 * 
	 * <p><b>Note:</b> This function does not initialize the Leap Motion Class. This ONLY reverts	the effects of the 
	 * 		 "turnOff" function, allowing values to be read again, thus permitting the pointer to be moved
	 * 		 and clicks to be performed again.
	 */
	public void turnOn()
	{
		keepExecutting = true;
	}

	/**
	 * Function that exchanges the user dominant hand with the auxiliary hand. 
	 * <br>In other words, if the user was right-handed, he/she now is left-handed (and vice-versa).
	 */
	public void changeDominantHand()
	{ this.isRightHanded = !this.isRightHanded; }

	/**
	 * Function responsible for changing the control mode.
	 * <br>The order of change is as follows:
	 * 	<br>-> 1 Hand, Screen Tap gesture;
	 *  <br>-> 1 Hand, based on touch zone distance, no gesture;
	 *  <br>-> 1 Hand, based on opposite value of the touch zone distance, no gesture;
	 *  <br>-> 2 Hands, grabbing gesture;
	 *  <br>-> 2 Hands, Key Tap gesture;
	 *  <br>-> ...
	 * 	
	 * 	<p><b>Note:</b> 2 Hands, grabbing gesture is the default.
	 * 	
	 * 	<p><b>Note2:</b> This function may be used several times in order to achieve the intended Control Mode. 
	 * 
	 * 	@return The next control mode.
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
				choosenControlMode = ControlMode.HAND_WITH_GRABBING_GESTURE;
				break;
				
			case HAND_WITH_GRABBING_GESTURE:
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
				
			case HAND_WITH_GRABBING_GESTURE:
				return "1 hand, Grabbing gesture.";
		}

		//This should not happen.
		return "ERROR CONVERTING CONTROL MODE TO STRING";
	}

	//For testing purposes
	public static void main(String[] args) 
	{
		int windowHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		int windowWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		
		LeapMotion lm = new LeapMotion(ControlMode.HANDS_WITH_GRABBING_GESTURE,true, windowWidth, windowHeight);
		lm.initialize();
	}
}