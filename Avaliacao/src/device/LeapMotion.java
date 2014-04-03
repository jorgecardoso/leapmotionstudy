package device;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.State;
import com.leapmotion.leap.Gesture.Type;

public class LeapMotion extends Listener
{
	public enum ControlMode{HAND_WITHOUT_GESTURE, HANDS_WITH_KEYTAP_GESTURE,
		HAND_WITH_SCREENTAP_GESTURE, HANDS_WITH_SWIPE_GESTURE};

		private ControlMode choosenControlMode;

		private boolean isRightHanded;
		private Hand dominantHand = new Hand();
		private Hand auxiliaryHand = new Hand();

		private int cursorPositionX = 0;
		private int cursorPositionY = 0;
		private double touchZoneDistance = 0;
		private boolean isButtonPressed = false;

		//////<<<<<<<-----------------------------------------------******
		Robot cursor;
		//////<<<<<<<-----------------------------------------------******

		//int teste = 0;
		final boolean debug = false;
		
		/**
		 * Constructor of Class LeapMotion.
		 * This class allows the Leap Motion device to take control of the cursor and some 
		 * mouse functions, trying to emulate it.
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
		 * Function that responsible for starting the Robot that will move the cursor and enable the 
		 * respective listeners, gestures and configurations.
		 */
		public void initialize()
		{
			//////<<<<<<<-----------------------------------------------******
			try 
			{
				cursor = new Robot();
			} 
			catch (AWTException e) 
			{
				System.err.println("It was not possible to create the Robot class. Without it, the cursor won't be able to move.");
				e.printStackTrace();
				System.exit(0);
			}
			//////<<<<<<<-----------------------------------------------******

			Controller device = new Controller();

			if(choosenControlMode == ControlMode.HANDS_WITH_KEYTAP_GESTURE)
			{
				//Extend gesture recognition time in order to detect it with more ease.
				if(!device.config().setFloat("Gesture.KeyTap.HistorySeconds", 0.3f) )
				{
					if(debug){System.err.println("It was not possible to alter \"Gesture.KeyTap.HistorySeconds\" configuration.");}
					return;
				}
				device.config().save();

				device.enableGesture(Type.TYPE_KEY_TAP);
			}
			else if(choosenControlMode == ControlMode.HAND_WITH_SCREENTAP_GESTURE)
			{
				//Extend gesture recognition time in order to detect it with more ease.
				if(!device.config().setFloat("Gesture.ScreenTap.HistorySeconds", 0.3f) )
				{
					if(debug){System.out.println("It was not possible to alter \"Gesture.ScreenTap.HistorySeconds\" configuration.");}
					return;
				}

				device.enableGesture(Type.TYPE_SCREEN_TAP);
			}
			else if(choosenControlMode == ControlMode.HANDS_WITH_SWIPE_GESTURE)
			{
				//Extend gesture recognition time in order to detect it with more ease.
				if(!device.config().setFloat("Gesture.KeyTap.HistorySeconds", 0.3f) )
				{
					if(debug){System.out.println("It was not possible to alter \"Gesture.KeyTap.HistorySeconds\" configuration.");}
					return;
				}
				device.config().save();

				device.enableGesture(Type.TYPE_SWIPE);
			}

			device.addListener(this);	

			//////<<<<<<<-----------------------------------------------******
			//Since the class will be using listeners for controlling the cursor and inputing commands,
			//this thread just need to keep the resources alive. To keep alive the While(true) is used.
			while(true)
			{}
			//////<<<<<<<-----------------------------------------------******
		}

		/**
		 * Function that returns the cursor current position on the X axis.
		 * 
		 * @return Computer cursor current X coordinates.
		 */
		public int getCursorPositionX()
		{ return this.cursorPositionX; }

		/**
		 * Function that returns the cursor current position on the Y axis.
		 *
		 * @return Computer cursor current Y coordinates.
		 */
		public int getCursorPositionY()
		{ return this.cursorPositionY; }

		/**
		 * Function that returns if a "mouse button click" has occurred.
		 * 
		 * Note: The "mouse button click" is simulated when certain gestures are used.
		 * 
		 * @return Boolean informing if the "mouse button" was pressed (true) or not (false).
		 */
		public boolean isButtonPressed()
		{ return this.isButtonPressed;	}

		/**
		 * Function that cancels the "mouse button click".
		 */
		public void resetButtonPressed()
		{ this.isButtonPressed = false; }
		
		/**
		 * Leap Motion listener extended.
		 * 
		 * Just a simple message confirming the presence of the Leap Motion Software.
		 */
		public void onInit(Controller controller)
		{	
			System.out.println("Leap Motion software found.");
		}

		/**
		 * Leap Motion listener extended.
		 * 
		 * Just a simple message confirming that the Leap Motion device is connected to the computer.
		 */
		public void onConnect(Controller controller)
		{
			System.out.println("Leap Motion connected!");
		}

		/**
		 * Leap Motion listener extended.
		 * 
		 * Just a simple message confirming that the Leap Motion device has been disconnected.
		 */
		public void onDisconnect(Controller controller)
		{
			System.out.println("Leap Motion disconnected...");
		}

		/**
		 * Leap Motion listener extended.
		 * 
		 * A message confirming that the Leap Motion is terminating it's processing.
		 * The listeners are removed and the controller deleted.
		 */
		public void onExit(Controller controller)
		{
			System.out.println("A sair...");
			controller.removeListener(this);
			controller.delete();
		}

		/**
		 * Leap Motion listener extended.
		 * 
		 * When the device captures a frame this function occurs.
		 * The frame received is analyzed in search of hands and gestures. Depending on the data 
		 * recovered the respective action will be taken.
		 */
		public void onFrame(Controller controller)
		{		
			Frame capturedFrame = controller.frame();

			if(!capturedFrame.isValid())
			{
				if(debug){System.err.println("Captured image not valid!");}
				return;
			}

			HandList detectedHands = capturedFrame.hands();

			if(detectedHands.isEmpty())
			{
				if(debug) {System.err.println("No hands were detected.");}
				return;
			}

			if(detectedHands.count() == 1)
			{
				dominantHand = detectedHands.get(0);

				if(!dominantHand.isValid())
				{
					if(debug){System.out.println("Invalid dominant hand...");}
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
			else
			{
				//If the device finds more than two hands, do nothing.
				System.err.println("Three hands were detected. Only one or two can be used.");
				return;
			}

			//Take the respective action depending on the control mode.
			if(choosenControlMode == ControlMode.HAND_WITH_SCREENTAP_GESTURE)
			{ 
				//User controls the cursor and simulates button presses with the same hand.
				//The button press is simulated by the SCREEN TAP gesture.
				typeControl1(controller); 
			}
			else if(choosenControlMode == ControlMode.HANDS_WITH_KEYTAP_GESTURE)
			{ 
				//User controls the cursor using his/her dominant hand and simulates button 
				//presses with the auxiliary hand by performing KEYTAP gesture.
				typeControl2(controller); 
			}
			else if(choosenControlMode == ControlMode.HAND_WITHOUT_GESTURE)
			{ 
				//User controls the cursor and simulates button presses with the same hand.
				//The button press is simulated by the touch zone distance.
				typeControl3(controller); 
			}
			else if(choosenControlMode == ControlMode.HANDS_WITH_SWIPE_GESTURE)
			{
				//User controls the cursor using his/her dominant hand and simulates button 
				//presses with the auxiliary hand by performing SWIPE gesture.
				typeControl4(controller);
			}
		}

		/**
		 * In this type of control a finger, from the dominant hand, closest to the screen (the farthest 
		 * from the hand center) is used to move the cursor and simulate the mouse button click.
		 * To perform the "mouse click" the Screen Tap Gesture is used. To do this, simply move the finger
		 * in the screen direction as if to touch it / press it.
		 * 
		 * For more information on the gesture: https://developer.leapmotion.com/documentation/java/api/Leap.ScreenTapGesture.html
		 * 
		 * In this type of control only one hand is required.
		 * 
		 * @param controller - Default device controller.
		 */
		private void typeControl1(Controller controller)
		{
			ScreenList availableScreens = controller.locatedScreens();

			if(availableScreens.isEmpty())
			{
				System.err.println("No screen has been detected.");
				return;
			}

			Pointable pointerFinger = dominantHand.pointables().frontmost();

			if(!pointerFinger.isValid())
			{
				System.out.println("Invalid pointer finger!");
				return;
			}

			Screen screen = availableScreens.get(0);

			Vector interscection = screen.intersect(pointerFinger, true, 1.0f);

			cursorPositionX = (int) ( screen.widthPixels() * interscection.getX() );
			cursorPositionY = (int) ( screen.heightPixels() * ( 1.0f - interscection.getY() ) );

			//////<<<<<<<-----------------------------------------------******
			cursor.mouseMove(cursorPositionX, cursorPositionY);
			//////<<<<<<<-----------------------------------------------******

			Gesture performedGesture = controller.frame().gestures().get(0);

			if(!performedGesture.isValid())
			{
				System.out.println("The performed gesture is invalid.");
				return;
			}

			if(performedGesture.hands().count() != 1)
			{
				System.out.println(
						"More than one hand has performed the SCREEN TAP gesture.\n" +
								"Only one hand should be used and this should be the dominant hand."
						);
				return;
			}

			if(!performedGesture.hands().get(0).equals(dominantHand))
			{
				System.out.println("Only the dominant hand should perform gestures.");
				return;
			}

			isButtonPressed = true;
			//////<<<<<<<---------------------------------------
			cursor.mousePress(InputEvent.BUTTON1_MASK);
			cursor.mouseRelease(InputEvent.BUTTON1_MASK);
			//////<<<<<<<---------------------------------------	
		}

		/**
		 * In this type of control a finger, from the dominant hand, closest to the screen (the farthest 
		 * from the hand center) is used to move the cursor.
		 * To simulate the a mouse button click a finger, from the auxiliary hand, closest to the screen (again,
		 * farthest from the hand center) must perform the gesture Key Tap.
		 * To perform the Key Tap Gesture simply move the finger in the Leap Motion direction 
		 * as if going to press a key.
		 * 
		 * For more information on the gesture: https://developer.leapmotion.com/documentation/java/api/Leap.KeyTapGesture.html
		 * 
		 * In this type of control two hands are required.
		 * 
		 * @param controller - Default device controller.
		 */
		public void typeControl2(Controller controller)
		{
			ScreenList availableScreens = controller.locatedScreens();

			if(availableScreens.isEmpty())
			{
				System.out.println("No screen was detected.");
				return;
			}

			Pointable pointerFinger = dominantHand.pointables().frontmost();

			if(!pointerFinger.isValid())
			{
				System.out.println("Invalid pointer finger!");
				return;
			}

			Screen screen = availableScreens.get(0);

			Vector intersection = screen.intersect(pointerFinger, true, 1.0f);

			cursorPositionX = (int) ( screen.widthPixels() * intersection.getX() );
			cursorPositionY = (int) ( screen.heightPixels() * ( 1.0f - intersection.getY() ) );

			//////<<<<<<<-----------------------------------------------******
			cursor.mouseMove(cursorPositionX, cursorPositionY);
			//////<<<<<<<-----------------------------------------------******

			Gesture performedGesture = controller.frame().gestures().get(0);

			if(!auxiliaryHand.isValid())
			{
				System.out.println("Most place your auxiliary hand over the Leap Motion device.");
				return;
			}

			if(!performedGesture.isValid())
			{
				System.out.println("The performed gesture is invalid.");
				return;
			}

			if(performedGesture.hands().count() != 1)
			{
				System.out.println(
						"More than one hand has performed the KEY TAP gesture.\n" +
								"Only the dominant hand should be used."
						);

				return;
			}

			if(!performedGesture.hands().get(0).equals(auxiliaryHand))
			{
				System.out.println(
						"Only the auxiliary hand should perform gestures.\n" +
								"The dominant hand is used only to control the cursor movement."
						);

				return;
			}

			isButtonPressed = true;
			//////<<<<<<<---------------------------------------
			cursor.mousePress(InputEvent.BUTTON1_MASK);
			cursor.mouseRelease(InputEvent.BUTTON1_MASK);
			//////<<<<<<<---------------------------------------
		}

		/**
		 * In this type of control a finger, from the dominant hand, closest to the screen (the farthest 
		 * from the hand center) is used to move the cursor and simulate the mouse button click.
		 * To perform the "mouse click" the Touch Distance is used.
		 * 
		 * For more information on the concept: https://developer.leapmotion.com/documentation/java/devguide/Leap_Touch_Emulation.html?highlight=touch%20distance
		 * 
		 * In this type of control only one hand is required.
		 * 
		 * @param controller - Default device controller.
		 */
		public void typeControl3(Controller controlador)
		{
			ScreenList availableScreens = controlador.locatedScreens();

			if(availableScreens.isEmpty())
			{
				System.out.println("No screen detected.");
				return;
			}

			Pointable pointerFinger = dominantHand.pointables().frontmost();

			if(!pointerFinger.isValid())
			{
				System.out.println("Invalid pointer finger!");
				return;
			}

			Screen screen = availableScreens.get(0);

			Vector intersection = screen.intersect(pointerFinger, true, 1.0f);

			cursorPositionX = (int) ( screen.widthPixels() * intersection.getX() );
			cursorPositionY = (int) ( screen.heightPixels() * ( 1.0f - intersection.getY() ) );
			touchZoneDistance = pointerFinger.touchDistance();

			//////<<<<<<<-----------------------------------------------******
			cursor.mouseMove(cursorPositionX, cursorPositionY);
			//////<<<<<<<-----------------------------------------------******

			if(touchZoneDistance < 0.0)
			{
				isButtonPressed = true;
				//////<<<<<<<-----------------------------------------------******
				cursor.mousePress(InputEvent.BUTTON1_MASK);
				//////<<<<<<<-----------------------------------------------******
			}
			else
			{
				isButtonPressed = false;
				//////<<<<<<<-----------------------------------------------******
				cursor.mouseRelease(InputEvent.BUTTON1_MASK);
				//////<<<<<<<-----------------------------------------------******
			}
		}

		/**
		 * In this type of control a finger, from the dominant hand, closest to the screen (the farthest 
		 * from the hand center) is used to move the cursor.
		 * To simulate the a mouse button click, a finger, from the auxiliary hand, closest to the screen (again,
		 * farthest from the hand center) must perform the gesture Swipe.
		 * To perform the Swipe Gesture simply move the finger from the left to right above the Leap Motion device.
		 * 
		 * For more information on the gesture: https://developer.leapmotion.com/documentation/java/api/Leap.SwipeGesture.html
		 * 
		 * In this type of control two hands are required.
		 * 
		 * @param controller - Default device controller.
		 */
		private void typeControl4(Controller controller) 
		{
			ScreenList availableScreens = controller.locatedScreens();

			if(availableScreens.isEmpty())
			{
				if(debug){System.out.println("No screen detected.");}
				return;
			}

			Pointable pointerFinger = dominantHand.pointables().frontmost();

			if(!pointerFinger.isValid())
			{
				if(debug){System.out.println("Invalid pointing finger!");}
				return;
			}

			Screen screen = availableScreens.get(0);

			Vector intersection = screen.intersect(pointerFinger, true, 1.0f);

			cursorPositionX = (int) ( screen.widthPixels() * intersection.getX() );
			cursorPositionY = (int) ( screen.heightPixels() * ( 1.0f - intersection.getY() ) );

			//////<<<<<<<-----------------------------------------------******
			cursor.mouseMove(cursorPositionX, cursorPositionY);
			//////<<<<<<<-----------------------------------------------******

			Gesture performedGesture = controller.frame().gestures().get(0);

			if(!auxiliaryHand.isValid())
			{
				if(debug){System.out.println("Place the auxiliary hand above the Leap Motion device.");}
				return;
			}

			if(!performedGesture.isValid())
			{
				if(debug){System.out.println("Invalid gesture.");}
				return;
			}

			if(performedGesture.hands().count() != 1)
			{
				if(debug)
				{
					System.out.println(
						"More than one hand performed the SWIPE gesture.\n" +
						"Only the auxiliary hand should be used."
					);
				}
				
				return;
			}

			if(!performedGesture.hands().get(0).equals(auxiliaryHand))
			{
				if(debug)
				{
					System.out.println(
						"Only the auxiliary hand should perform gestures.\n" + 
						"The dominant hand is only used to move the cursor."
					);
				}
				
				return;
			}

			//The swipe gesture is a continuous one. In other words, it has 4 states: Start, Update, Stop and Invalid.
			//Only one click is suppose to happen when it's executed. As such, it's necessary to check if the gesture is stopped.
			//If this check didn't exist, more one click would happen when the gesture was being updated.
			if( !performedGesture.state().equals(State.STATE_STOP) )
			{
				return;
			}

			isButtonPressed = true;
			//////<<<<<<<---------------------------------------
			cursor.mousePress(InputEvent.BUTTON1_MASK);
			cursor.mouseRelease(InputEvent.BUTTON1_MASK);
			//////<<<<<<<---------------------------------------
		}

		//For testing purposes
		public static void main(String[] args) 
		{
			@SuppressWarnings("unused")
			LeapMotion lm = new LeapMotion(ControlMode.HANDS_WITH_SWIPE_GESTURE,true);
		}
}