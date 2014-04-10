package core;

import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Thread.State;
import java.util.TimerTask;
import java.util.Vector;

import datastructure.*;
import device.LeapMotion;
import device.LeapMotion.ControlMode;
import other.*;
import processing.core.*;


public class EvaluationApp extends PApplet
{
	private static final long serialVersionUID = 1L;
	
	private int centralPositionX;
	private int centralPositionY;

	Vector<Circle> circles;
	private int numberOfCircles;
	private int circleRadius;
	private int centerDistance;
	
	private int targetSizeX;		//For drawing the +
	private int targetSizeY;		//the shows on the target circle
	
	protected boolean redrawElements;

	private final int backgroundColor = 255; 			//White
	private final int HoverColorCircleRed = 183;
	private final int HoverColorCircleGreen = 228;
	private final int HoverColorCircleBlue = 240;
	
	private Sequence sequenceToPerform;
	private boolean generateRandomSequence = false;
	private int currentSequenceIndex = 0;
	
	private Information informationFromCurrentTrial = new Information();
	private boolean isEvaluationComplete = false;
	protected boolean acceptKeyInput = false;
	
	private final Chronometer chronometer = new Chronometer();
	
	protected boolean makePause = true;	//To be used on the Thread that saves the mouse movement.
	
	private Thread lmThread;
	protected LeapMotion leapMotionDevice;
	private boolean activateLeapMotion;
	
	//Variables related to the presentation of text on the application.
	private PFont font;
	private int displayFontSize;
	private String displayText;
	
	private int teste = 0;
	private boolean debug = false;
	private boolean testeBoolean = false;
	
	/**
	 * Extended function from Processing.
	 * This function is only executed once.
	 */
	public void setup()
	{
		//Loading the required values to the execution of the application from the configuration text file ("Config.txt").
		loadConfigurationFile();
		
		//Start Leap Motion device in its own thread
		if(activateLeapMotion)
		{
			activateLeapMotion();
		}
		
		//Some drawing parameters for Processing.
		rectMode(PConstants.CENTER); 			// When drawing rectangles, the given coordinates are their center and not the extremes points.
		stroke(0);								// Lines are drawn in black
		font = createFont("Arial",16,true); 	// Create a Font in Arial, 16 point, anti-aliasing on.
		
		//Set starting text.
		displayText = "Welcome to the Evaluation application!\nTo start press the + on the top!";
		
		//Discover the screen resolution.
		int windowHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		int windowWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		
		//Window will occupy all the available screen area. Some OSes restrict this area.
		size(windowWidth,windowHeight);
		
		//The occupied screen area is always inferior to the resolution of the screen ( window may not overlap certain bars).
		centralPositionX = (int) (windowWidth / 2);
		centralPositionY = (int) (windowHeight / 2);
		
		//Set text font size
		displayFontSize = ( 25 * windowWidth * windowHeight ) / ( 1280*960 );
		
		double angle = 360.0 / ( (double) numberOfCircles);
		
		//Calculate, create and/or redraw the circles required for the evaluation of the device.
		for(int i = 0; i < numberOfCircles; i++)
		{
			double currentAngle = Math.toRadians(angle * i);
						
			double coordinateX = centralPositionX + centerDistance * Math.cos(currentAngle);
						
			double coordinateY = centralPositionY + centerDistance * Math.sin(currentAngle);
					
			circles.add( 
				new Circle( ((int) coordinateX), ((int) coordinateY), ((int) circleRadius))
			);
		}
		
		this.addComponentListener( new ComponentAdapter() {
			public void componentResized(ComponentEvent e)
			{	redrawElements = true;	 }
		});
		
		//Create and start the thread that will save the mouse position over time
		createMouseMovementThread().start();
		
		this.addMouseListener( createMouseListener() );
		this.addKeyListener( createKeyListener() );
		
		//Paint the application background with desired color. Processing function.
		background(backgroundColor);
	}

	/**
	 * Extended function from Processing.
	 * This function is executed in Loop by the processing. In other words, when it ends, it is executed again.
	 */
	public void draw() 
	{  
		//When the application is resized the center of the frame and respective circles must be recalculated.
		if(redrawElements)
		{ redrawElements(); }
		
		//If a resizing is still going, do nothing. The next "draw()" will deal with the resize.
		if(redrawElements)
		{ return ; }
		
		//Paint background with the selected color. Processing function. Processing function.
		background(backgroundColor);
		
		displayText();
		
		if(isEvaluationComplete)
		{
			return;
		}
		
		//To avoid errors due synchronization issues between the listener and the drawing function.
		int readCurrentSequenceIndex = currentSequenceIndex;
		
		if( readCurrentSequenceIndex == sequenceToPerform.size() )
		{	
			return;
		}
		
		//Draw the circles.
		for(int i = 0; i < circles.size(); i++)
		{
			Circle target = circles.get(i);
			
			//If the cursor is hovering over a circle, paint the circle's interior in a different color.
			//Note: mouseX and mouseY are Processing variables and return, respectively, the X and Y coordinates of the mouse.
			if( target.doesPointBelongToCircle(mouseX, mouseY) )
			{
				fill(HoverColorCircleRed,HoverColorCircleGreen,HoverColorCircleBlue);
			}
		
			drawCircle( target.getCenterX(), target.getCenterY(), target.getRadius() );
			
			//The circle's interior is painted white. Processing function.
			noFill();
		}
		
		//If a resizing is still going, do nothing. The next "draw()" will deal with the resize.
		if(redrawElements)
		{ return ; }
		
		Circle targetCircle = circles.get(sequenceToPerform.get(readCurrentSequenceIndex));
		drawTargetSign(targetCircle.getCenterX(), targetCircle.getCenterY());
	}

	/**
	 * Handler to be called when the mouse button is pressed.
	 */
	protected void mouseButtonPressed() 
	{	
		//Error prevention. If the user performs a button press too fast on the last circle of the sequence, this action 
		//may be called again, and error would occur when getting targetCircle.
		if( currentSequenceIndex >= sequenceToPerform.size() )
		{return;}
			
		Circle targetCircle = circles.get(sequenceToPerform.get(currentSequenceIndex));
		
		if( currentSequenceIndex > 0 )
		{
			Circle selectedCircle = circles.get(sequenceToPerform.get(currentSequenceIndex));
			
			informationFromCurrentTrial.clickedOccurred();
			
			boolean isSelectionRight = selectedCircle.doesPointBelongToCircle(mouseX, mouseY);
			
			if( isSelectionRight  )
			{
				chronometer.stop();
				
				stopStoringMousePosition();
				
				Sound.playSucessSound();
					
				Circle lastCircle = circles.get(sequenceToPerform.get(currentSequenceIndex - 1));
				
				//Save collected data.
				informationFromCurrentTrial.setNumberOfCircles(numberOfCircles);
				informationFromCurrentTrial.setStartingCircleCenter(lastCircle.getCenterPixel());
				informationFromCurrentTrial.setEndingCircleCenter(targetCircle.getCenterPixel());
				informationFromCurrentTrial.setTargetWidth(targetCircle.getRadius() * 2); 		//A largura do alvo é o diametro da circunferência.
				informationFromCurrentTrial.setElapsedTime(chronometer.getTimeInMilliseconds());
				informationFromCurrentTrial.setDistanceBetweenFrameAndCircleCenter(centerDistance);
				informationFromCurrentTrial.setCircleID(currentSequenceIndex);
				informationFromCurrentTrial.setDistanceBetweenCircles(
						Information.calculateDistanceBetweenPoints(
								lastCircle.getCenterX(), lastCircle.getCenterY(), 
								targetCircle.getCenterX(), targetCircle.getCenterY())
				);
				
				//Store trial results...
				informationFromCurrentTrial.storeInformationInFile();
				
				//...and start a new one.
				informationFromCurrentTrial.resetInformation();

				currentSequenceIndex++;
				
				//Print goodbye message
				if( currentSequenceIndex == sequenceToPerform.size() )
				{	
					isEvaluationComplete = true;
					
					displayText = "Well done!\nLet's rest a bit.";
					System.out.println("Evaluation successfully completed!");
					
					//A little time out to make sure the system has time to present the displayText
					try {Thread.sleep(2000);}catch (InterruptedException e) {}
					
					informationFromCurrentTrial.increaseBlockNumber();
					displayText = "When ready to continue, please press the \"space\" key.";
					acceptKeyInput = true;	
					return;
				}
			}	
			else
			{
				Sound.playFailureSound();
			}
			
			//A little pause to avoid several "button presses" if the user remains with the buttom pressed. 
			try {Thread.sleep(200);}catch (InterruptedException e) {}
			
			if( isSelectionRight )
			{
				startStoringMousePosition();
				
				//Restart chronometer.
				chronometer.reset();	chronometer.start();
			}
			
		}
		else if( currentSequenceIndex == 0 )
		{
			//The experience should only be started when the user presses with success the first default target.
			Circle selectedCircle = circles.get(sequenceToPerform.get(currentSequenceIndex));
			
			Boolean wasRightCirclePressed = selectedCircle.doesPointBelongToCircle(mouseX, mouseY);
			
			if( wasRightCirclePressed )
			{
				Sound.playSucessSound();
				currentSequenceIndex++;
			}	
			
			//A little pause to avoid several "button presses" if the user remains with the button pressed. 
			try {Thread.sleep(200);}catch (InterruptedException e) {}
			
			if( wasRightCirclePressed )
			{
				//Don't print anything in the screen.
				displayText = "";
				
				//Start saving the mouse position over time.
				startStoringMousePosition();
				
				chronometer.start();
			}
		}
	}

	/**
	 * Function that prints, on the application frame, the string stored in "displayText" variable.
	 */
	private void displayText() 
	{
		textFont(font,displayFontSize);            
		fill(0);
		textAlign(CENTER);
		text(displayText,centralPositionX,centralPositionY);
		noFill();
	}

	/**
	 * Function responsible for recalculating certain values when a resize happens.
	 */
	private void redrawElements() 
	{
		//Stop function from re-invoking herself if there's no need.
		redrawElements = false;
		
		//In case the application gets resized again, stop it. The "draw()" function will invoke it again.
		if(redrawElements)
		{ return ; }
		
		//The space occupied is always less that the screens resolution (the window may be unable to overlap the tool bars).
		//For that reason, it may be need to uodate the application frame size.		
		centralPositionX = (int) (this.width / 2.0);
		centralPositionY = (int) (this.height / 2.0);
		
		//Save resources.
		if(redrawElements)
		{ return ; }
		
		//Check if the circles were already drawn. 
		if(!circles.isEmpty())
		{ circles.clear(); }
		
		double angle = 360.0 / ( (double) numberOfCircles);
		
		//Calculate, create and/or redraw the circles required for the evaluation of the device.
		for(int i = 0; i < numberOfCircles; i++)
		{
			double currentAngle = Math.toRadians(angle * i);
						
			double coordinateX = centralPositionX + 
					centerDistance * Math.cos(currentAngle);
						
			double coordinateY = centralPositionY + 
					centerDistance * Math.sin(currentAngle);
					
			circles.add( 
					new Circle( ((int) coordinateX), ((int) coordinateY), ((int) circleRadius) )
			);
			
			//Save resources.
			if(redrawElements)
			{ return; }
		}
	}
	
	/**
	 * Function that draws a circle on the application frame.
	 * 
	 * @param circleCenterCoordinateX - X axis position of the circle's center.
	 * @param circleCenterCoordinateY - Y axis position of the circle's center.
	 * @param circleRadius - The circle radius.
	 */
	protected void drawCircle(int circleCenterCoordinateX, int circleCenterCoordinateY, int circleRadius)
	{
		//Warning: Processing asks not for the radius but for the diameter in X and Y. As such, the values must be multiplied by two.
		//Note: ellipse is a Processing function
		ellipse(circleCenterCoordinateX, circleCenterCoordinateY, circleRadius * 2, circleRadius * 2);
	}
	
	/**
	 * Function that draws a "+" on the given coordinates.
	 * 
	 * @param centerTargetX - X axis position of the target's center.
	 * @param centerTargetY - Y axis position of the target's center.
	 */
	protected void drawTargetSign(int centerTargetX, int centerTargetY)
	{
		//Processing function. No lines are drawn.
		noStroke();
		fill(137,42,139); 			//Purple
		
		//Processing function. Draws a rectangle.
		rect(centerTargetX, centerTargetY, targetSizeX, targetSizeY);
		rect(centerTargetX, centerTargetY, targetSizeY, targetSizeX);
		
		fill(255,255,255);			//White
		stroke(0);					//Processing function. Lines are drawn black.
	}
	
	/**
	 * Function responsible for reading the configuration values necessary for running
	 * the application.
	 */
	private void loadConfigurationFile() 
	{
		BufferedReader reader = null;
		
		try 
		{
			reader = new BufferedReader( new FileReader( new File ("../Config.txt")	) );
		} 
		catch (FileNotFoundException e) 
		{
			System.err.println("The configuration file (\"Config.txt\") was not found.\nExiting...\n");
			e.printStackTrace();
			System.exit(0);
		}
		
		String readLine = "";
		
		while( readLine != null)
		{
			try 
			{
				readLine = reader.readLine();
			} 
			catch (IOException e) 
			{
				System.err.println("It was not possible to read the file.\nExiting...");
				System.exit(0);
			}
			
			if(readLine == null)
			{ continue; }
			
			String[] result = readLine.split(":");
			
			if(result.length <= 1)
			{continue;}
			
			//Remove certain symbols that may exist.
			result[1] = result[1].replace(" ", "");
			result[1] = result[1].replace(".", "");
			
			//Store the read values in the respective variable.
			if( result[0].equals("Number of circles (integer)") )
			{ numberOfCircles = Integer.parseInt(result[1]); }
			else if( result[0].equals("Circle radius (integer)") )
			{ circleRadius = Integer.parseInt(result[1]); }
			else if( result[0].equals("Distance between circles and frame center (integer)") )
			{ centerDistance = Integer.parseInt(result[1]); }
			else if( result[0].equals("Random sequence generator (true/false)") )
			{ 
				String intendedBoolean = result[1].toLowerCase();
				
				if(intendedBoolean.equals("true") || intendedBoolean.equals("t"))
				{generateRandomSequence = true;}
				
				//False is the default value of "generateRandomSequence". If the application is unable to read a value, it will remain false.
			}
			else if( result[0].equals("Assigned UserID to person performing the evaluation") )
			{ informationFromCurrentTrial.changeUser( Integer.parseInt(result[1]) ); }
			else if( result[0].equals("Number of the device to be used") )
			{
				int value = Integer.parseInt(result[1]);
	
				if(value == 0) 
				{ activateLeapMotion = true; }
				else
				{ activateLeapMotion = false; }
				
				informationFromCurrentTrial.changeDevice(value);
			}
		}
		
		//Check if the acquired values are respect the established limits.
		if( !(numberOfCircles >= 2 && numberOfCircles <= 32) )
		{
			System.out.println("\"Number of circles\" can only vary between 2 and 32.");
			System.exit(3);
		}
		else if( !(circleRadius >= 1 && circleRadius <= 200) )
		{
			System.out.println("\"Circle radius\" can only vary between 1 and 200.");
			System.exit(4);
		}
		else if( !(centerDistance >= 0 && centerDistance <= 800) )
		{
			System.out.println("\"Distance between circles and frame center\" can only vary between 0 and 800.");
			System.exit(5);
		}
		
		//Initialize the required variables based on the values read  
		targetSizeX = (int) (0.75 * 2 * circleRadius);		//Warning: Processing receives the diameter and not the radius. Multiply by 2. 
		targetSizeY = (int) (0.125 * 2 * circleRadius);		//Note: 0.75 and 0.125 are resizing variables to assure that the "+" can fit inside a circle.
		
		circles = new Vector<Circle>(numberOfCircles);
		sequenceToPerform = new Sequence(numberOfCircles, generateRandomSequence);
	}
	
	/**
	 * Function that creates a listener that will store the mouse position over time.
	 * 
	 * Note:The listener is only created, not SET. The user must add the listener himself/herself.
	 * 
	 * @return The said listener.
	 */
	private MouseListener createMouseListener()
	{
		return new MouseListener() 
		{
			@Override
			public void mousePressed(MouseEvent e) { mouseButtonPressed(); }
			public void mouseClicked(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e){}
			public void mouseReleased(MouseEvent e){}
		};
	}
	
	/**
	 * Function that starts the Leap Motion device (and respective functions) and creates a new thread 
	 * for it to run exclusively.
	 */
	private void activateLeapMotion() 
	{
		leapMotionDevice = new LeapMotion(ControlMode.HANDS_WITH_KEYTAP_GESTURE, true);
		lmThread = new Thread("Leap Motion Listener") {
			public void run(){
				//Boolean represents if user is right handed or not.
				leapMotionDevice.initialize();
			};
		};
		lmThread.start();
	}
	
	/**
	 * Function that starts the Leap Motion device (and respective functions) and creates a new thread 
	 * for it to run exclusively.
	 */
	private void turnOffLeapMotion() 
	{
		leapMotionDevice.turnOff();
		leapMotionDevice = null;
		lmThread = null;
	}
	
	/**
	 * Function that creates a key listener in order to alter the application parameters.
	 * The keys are as follows:
	 * 
	 * - > "R" key - Changes if the sequence is randomly generated or follows the same pattern as Makenzie's.~
	 * - > ["0","9"] - Keys between 0 and 9 change the device number to the one pressed.
	 * - > "+" or "-" - Increases / decreases the current User ID.
	 * 
	 * Note: Remember, this function only generates the listener. It must be manually added!
	 * 
	 * @return THe said listener.
	 */
	private KeyListener createKeyListener()
	{
		return new KeyListener() 
		{
			@Override
			public void keyReleased(KeyEvent e) 
			{
				//"Space" key pressed. When prompted, this key acts as the "Yes" or positive input.
				if(e.getKeyCode() == 32)
				{
					if(acceptKeyInput)
					{
						//Restart experiment.
						acceptKeyInput = false;
						isEvaluationComplete = false;
						
						sequenceToPerform = new Sequence(numberOfCircles, generateRandomSequence);
						currentSequenceIndex = 0;
						informationFromCurrentTrial.increaseSequenceNumber();
						
						displayText = "Let's do it again!\nTo start press the + symbol!";
					}
				}
				//Change sequence generator from random to Mackenzie's paper and vice-versa when "R" key pressed.
				//"R" key pressed.
				else if(e.getKeyCode() == 32)
				{
					generateRandomSequence = !generateRandomSequence;
					
					String message = "The sequence generator was change to:\n";
					
					if(generateRandomSequence)
					{	message += "Random.";	}
					else
					{	message += "Mackenzie's style.";	}
					
					displayText = message;
				}
				//Changes the Device number when the "0", "1" or "2" keys are pressed.
				else if(e.getKeyCode() >= 96 && e.getKeyCode() <= 98)
				{ 
					/*
					 * The device number are as follow:
					 *    -> 0 = Leap Motion;
					 *    -> 1 = Mouse;
					 *    -> 2 = Touch Pad;
					 */
					int numberPressed = e.getKeyCode() - 96;
					
					//If the number pressed is the same as the current device, don't do anything.
					if(informationFromCurrentTrial.getDeviceID() == numberPressed)
					{return;}
					
					informationFromCurrentTrial.changeDevice(numberPressed);
					
					if(numberPressed == 0)
					{
						activateLeapMotion();
					}
					else
					{
						turnOffLeapMotion();
					}
					
					displayText = "The device number was changed to: " + informationFromCurrentTrial.getDeviceID(); 
				}
				
				
				switch( e.getKeyCode() )
				{
					
					/*//Change User ID when the following keys are pressed.
					//"+" key pressed.
					case 107:
						informationFromCurrentTrial.nextUser();
						displayText = "The User ID was changed to: " + informationFromCurrentTrial.getUserID();  
						break;
					
					//"-" key pressed.
					case 109:
						informationFromCurrentTrial.lastUser();
						displayText = "The User ID was changed to: " + informationFromCurrentTrial.getUserID();  
						break;	*/
				}
			}
			
			public void keyPressed(KeyEvent e){}

			public void keyTyped(KeyEvent arg0){}
		};
	}
	
	/**
	 * Function responsible for creating a Thread that, when allowed, will store the mouse position
	 * (x and y coordinates) in the respective variable.
	 * 
	 * Note: This function only creates a Thread, doesn't start it. That must be done manually by the
	 * 		 by the programmer.
	 * 
	 * Note2: To start storing the values the function "startStoringMousePosition()" must be called otherwise
	 * 		  no value will be stored.
	 * 
	 * @return Thread that stores the mouse position.
	 */
	private Thread createMouseMovementThread()
	{
		return new Thread("Mouse Movement Listener") 
		{	
			public void run()
			{
				//Set priority to max as the values read in the Thread are of special importance.
				setPriority(Thread.MAX_PRIORITY);

				try 
				{
					while(true)
					{
						//Sleep 10 milliseconds.
						sleep(10);
						
						//While changing between targets or writing values to a file, there's no need to store information.
						if(!makePause)
						{
							informationFromCurrentTrial.storeCursorPosition(mouseX, mouseY);
						}
					}
				}
				catch (InterruptedException e) 
				{ e.printStackTrace(); }
			}
		};
	}
	
	/**
	 * Function that will alter the behavior of the Thread responsible for storing the Mouse position over time, telling it
	 * to start saving values.
	 * 
	 * Note: To stop registering values, use the function "stopStoringMousePosition()". 
	 * 
	 * Note2: Calling this function again without changing the behavior won't produce any effects. 
	 */
	private void startStoringMousePosition() 
	{
		makePause = false;
	}
	
	/**
	 * Function that will alter the behavior of the Thread responsible for storing the Mouse position over time, telling it
	 * to stop saving values.
	 * 
	 * Note: To start registering values, use the function "startStoringMousePosition()". 
	 * 
	 * Note2: Calling this function again without changing the behavior won't produce any effects.
	 * 
	 * Note3: This is the default behavior of the Thread.
	 */
	private void stopStoringMousePosition() 
	{
		makePause = true;
	}
}