
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
import java.util.Vector;

import datastructure.*;
import device.LeapMotion;
import device.LeapMotion.ControlMode;
import other.*;
import processing.core.*;

public class EvaluationApp extends PApplet
{
	private static final long serialVersionUID = 1L;

	//Frame center coordinates.
	private int centralPositionX;
	private int centralPositionY;
	private int offsetX;
	private int offsetY; 
	
	//For drawing the purple "+" sign that shows on the target circle
	private int targetSizeX;
	private int targetSizeY;
	
	//Screen Resolution
	private int windowHeight;
	private int windowWidth;

	protected boolean redrawElements;
	
	//Variables containing color values for the application.
	private final int backgroundColor = 255; 			
	private final int HoverColorCircleRed = 183;
	private final int HoverColorCircleGreen = 228;
	private final int HoverColorCircleBlue = 240;

	//Variables related to the presentation of text on the application.
	private PFont font;
	private int displayFontSize;
	private String displayText;
	private String displayTextSequencesLeft; 

	//Information related to the circles drawn in the application
	Vector<Circle> circles;
	private int numberOfCircles;
	private float circleRadius;
	private float centerDistance;
	
	//Information related to the sequence to be generated.
	private Sequence sequenceToPerform;
	private boolean generateRandomSequence = false;
	private int sequenceIndex = 0;
	
	//Variables concerning the Leap Motion thread and device.
	private Thread lmThread;
	protected LeapMotion leapMotionDevice;
	private boolean activateLeapMotion;
	private boolean rightHanded = true;
	private ControlMode desiredControlMethod = ControlMode.HANDS_WITH_GRABBING_GESTURE;
	
	//Playing mode. The user can interact and experiment with application without sequence or block limitations. The results will not be stored.
	private boolean playingMode = false;
	
	//Variables where information is temporarily stored during experiment.
	private Vector<Sample> mouseSamples = new Vector<Sample>();
	private Vector<Long> timeForEachSequence = new Vector<Long>();
	private Vector<Integer> numberOfCliksPerTrial = new Vector<Integer>();
	
	//Information related to the block and sequence of the experiment.
	private int currentBlockNumber = 1;
	private int currentSequenceNumber = 0;
	private int numberOfSequencesPerBlock;
	private int numberOfBlocksPerExperiment;
	
	//Information related to the user and device ID being used on the experiment.
	private int deviceID;
	private int userID;
	
	//Information related to the mouse click thread.
	protected boolean ignoreMouseInput = false;
	private boolean mouseClicked = false;
	private boolean mouseClickedInsideTarget = false;
	private int numberOfClicksTrial = 0;
	
	private boolean debug = true;

	public static void main(String args[]) 
	{
	    PApplet.main(new String[] { "--present", "core.EvaluationApp" });
	}
	
	/**
	 * Extended function from Processing.
	 * <br>This function is only executed once.
	 * 
	 * <p>This function is responsible for setting the initial parameters of Processing, reading the configuration file 
	 * and starting the adequate listeners.
	 */
	public void setup()
	{
		//Loading the required values to the execution of the application from the configuration text file ("Config.txt").
		loadConfigurationFile();
		
		//Discover the screen resolution.
		windowHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		windowWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		
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
		displayTextSequencesLeft = determineActualSequence();
		
		//Window will occupy all the available screen area. Some OSes restrict this area.
		size(windowWidth,windowHeight);
		
		//Determine the center of the application frame.
		centralPositionX = (int) (windowWidth / 2);
		centralPositionY = (int) (windowHeight / 2);
		
		//Set text font size
		displayFontSize = ( 25 * windowWidth * windowHeight ) / ( 1280*960 );
		
		//Calculate, create and/or redraw the circles required for the evaluation of the device.
		double angle = 360.0 / ( (double) numberOfCircles);
		
		for(int i = 0; i < numberOfCircles; i++)
		{
			double currentAngle = Math.toRadians(angle * i);
						
			double coordinateX = centralPositionX + offsetX + centerDistance * Math.cos(currentAngle);
						
			double coordinateY = centralPositionY + offsetY + centerDistance * Math.sin(currentAngle);
					
			circles.add( 
				new Circle( ((int) coordinateX), ((int) coordinateY), ((int) circleRadius))
			);
		}
		
		//Listener responsible for recalculating certain elements when the window application is resized.
		this.addComponentListener( new ComponentAdapter() {
			public void componentResized(ComponentEvent e)
			{	redrawElements = true;	 }
		});
		
		//Create and start the thread that will save the mouse position over time
		createMouseMovementThread().start();
		
		//Listener responsible for storing the mouse position over time.
		this.addMouseListener( createMouseListener() );
		
		//Listener responsible for performing the correct action when certain keys are pressed.
		this.addKeyListener( createKeyListener() );
		
		//Paint the application background with desired color. Processing function.
		background(backgroundColor);
	}

	/**
	 * Extended function from Processing.
	 * <br>This function is executed ad aeternum by Processing. In other words, when it ends, it is executed again.
	 * 
	 * <p>This function is responsible for drawing and refreshing the Graphical User Interface.
	 */
	public void draw() 
	{  
		//When the application is resized, the center of the frame and respective circles must be recalculated.
		if(redrawElements)
		{ redrawElements(); }
		
		//If a resizing is still going, do nothing. The next "draw()" will deal with the resize.
		if(redrawElements)
		{ return ; }
		
		//Paint background with the selected color. Processing function.
		background(backgroundColor);
		
		//Draw any messages from the application on the screen.
		displayText();
		
		
		//If the evaluation is complete there is no need to draw the remaining elements.
		if(sequenceIndex > numberOfCircles)
		{
			return;
		}
		
		//Draw the current trial being performed VS total trials on the bottom right of the screen.
		displayNumberOfTrials();
		
		//To avoid errors due synchronization issues between the listener and the drawing function.
		int readCurrentSequenceIndex = sequenceIndex;
		
		//If the sequence is complete, there is no need to draw the remaning elements.
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
		
		//Draw "+" (target) symbol on the screen. 
		Circle targetCircle = circles.get(sequenceToPerform.get(readCurrentSequenceIndex));
		drawTargetSign(targetCircle.getCenterX(), targetCircle.getCenterY());
		
		//Depending on the device being used or the Leap Motion device control mode change the pointer image.
		//to the correct cursor.
		if(	desiredControlMethod == ControlMode.HANDS_WITH_KEYTAP_GESTURE   || desiredControlMethod == ControlMode.HAND_WITH_SCREENTAP_GESTURE ||
			desiredControlMethod == ControlMode.HANDS_WITH_GRABBING_GESTURE || desiredControlMethod == ControlMode.HAND_WITH_GRABBING_GESTURE ||
			deviceID != 0 )
			
		{ 
			//Default cursor image.
			cursor();	
		}
		else
		{ 
			//Remove the default cursor image...
			noCursor(); 
			
			//... and substitute it for a circle.
			try
			{
				strokeWeight(4.0f);
				
				if(!leapMotionDevice.isClickHappening())
				{
					//Depending on the distance to the touch zone, draw the cursor circle with a different color.
					float redValue = (float) ( (leapMotionDevice.getTouchZone() * 205.0f));
					float greenValue = (float) ( ( 1 - leapMotionDevice.getTouchZone() ) * 205.0f);
					
					stroke(redValue, greenValue, 0.0f);
				}
				else
				{
					//Draw the cursor circle black.
					stroke(0,0,0);
				}
				
				noFill();
				
				//The drawn circle radius will change according to the touch zone (the farther, the bigger).
				drawCircle(mouseX, mouseY, ((int) (10 + leapMotionDevice.getTouchZone() * 20)) );
				
				stroke(0);					//Processing function.
				strokeWeight(1.0f);			//Processing function.
			}
			catch(Exception e)
			{
				//If the application fails to initialize the Leap Motion when the try{} code happens,
				//use a pre-defined cursor.
				stroke(0, 255, 0);
				strokeWeight(2.0f);
				
				drawCircle(mouseX, mouseY, 10);
				
				strokeWeight(1.0f);
				noStroke();
			}
		}	
	}

	/**
	 * Function that prints, on the center of the application frame, any feedback messages to the user.
	 */
	private void displayText() 
	{
		textFont(font,displayFontSize);            
		fill(0);
		textAlign(CENTER,CENTER);
		text(displayText,centralPositionX + offsetX,centralPositionY + offsetY);
		noFill();
	}
	
	/**
	 * Function that prints, on the bottom right of the application frame, the number of the current trial
	 * and the number of trials that must be performed. 
	 */
	private void displayNumberOfTrials() 
	{
		textFont(font,displayFontSize);            
		fill(0);
		textAlign(LEFT);
		text(displayTextSequencesLeft,windowWidth - displayFontSize * 3 ,windowHeight -10);
		noFill();
	}
	
	/**
	 * Function responsible for calculating which sequence is currently being performed.
	 * <br>This function is used in conjunction with the display function. As such the
	 * result is a string. 
	 * 
	 * @return A string with the format "Current_sequence/Total_sequence"
	 */
	private String determineActualSequence() 
	{
		return ( (currentBlockNumber - 1 ) * numberOfSequencesPerBlock + currentSequenceNumber + 1) + "/" + numberOfSequencesPerBlock * numberOfBlocksPerExperiment;
	}

	/**
	 * Function responsible for recalculating certain values when a resize happens.
	 * 
	 * <P>(Since the application starts in fullscreen, this function is not used)
	 */
	private void redrawElements() 
	{
		//Stop function from re-invoking herself if there's no need.
		redrawElements = false;
		
		//In case the application gets resized again, stop it. The "draw()" function will invoke it again.
		if(redrawElements)
		{ return ; }
		
		//The space occupied is always less that the screens resolution (the window may be unable to overlap the tool bars).
		//For that reason, it may be need to update the application frame size.		
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
						
			double coordinateX = centralPositionX + offsetX +
					centerDistance * Math.cos(currentAngle);
						
			double coordinateY = centralPositionY + offsetY +
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
			reader = new BufferedReader( new FileReader( new File ("Config.txt")	) );
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
			
			//Store the read values in the respective variable.
			if( result[0].equals("Number of circles (integer)") )
			{
				try{  numberOfCircles = Integer.parseInt(result[1]);  }
				catch(Exception e)
				{ 
					System.err.println("It was not possible to read the \"Number of circles\" from Config.txt.\n"
									 + "(Missing or wrong value?)\nNow exiting");
					System.exit(0);
				}
			}
			else if( result[0].equals("Distance between circles and frame center (integer)") )
			{ 
				try{  centerDistance = Float.parseFloat(result[1]);  }
				catch(Exception e)
				{ 
					System.err.println("It was not possible to read \"Distance between circles and frame center\" from Config.txt.\n"
									 + "(Missing or wrong value?)\nNow exiting");
					System.exit(0);
				}
			}
			else if( result[0].equals("Circle radius (float)") )
			{ 
				try{  circleRadius = Float.parseFloat(result[1]);  }
				catch(Exception e)
				{ 
					System.err.println("It was not possible to read \"Circle radius\" from Config.txt.\n"
									 + "(Missing or wrong value?)\nNow exiting");
					System.exit(0);
				}
			}
			else if( result[0].equals("Assigned UserID to person performing the evaluation") )
			{ 
				try{  userID = Integer.parseInt(result[1]);  }
				catch(Exception e)
				{ 
					System.err.println("It was not possible to read \"Assigned UserID to person performing the evaluation\" from Config.txt.\n"
									 + "(Missing or wrong value?)\nNow exiting");
					System.exit(0);
				}
			}
			else if( result[0].equals("How many sequences does one block have? (integer)") )
			{ 
				try{  numberOfSequencesPerBlock = Integer.parseInt(result[1]);  }
				catch(Exception e)
				{ 
					System.err.println("It was not possible to read \"How many sequences does one block have?\" from Config.txt.\n"
									 + "(Missing or wrong value?)\nNow exiting");
					System.exit(0);
				}
				
				if(numberOfSequencesPerBlock <= 0)
				{
					System.err.println("Number of sequences per block must be greater than 0.");
					System.exit(0);
				}
			}	
			else if( result[0].equals("How many blocks does one experiment have? (integer)") )
			{ 
				try{  numberOfBlocksPerExperiment = Integer.parseInt(result[1]);  }
				catch(Exception e)
				{ 
					System.err.println("It was not possible to read \"How many blocks does one experiment have?\" from Config.txt.\n"
									 + "(Missing or wrong value?)\nNow exiting");
					System.exit(0);
				}
				
				if(numberOfBlocksPerExperiment <= 0)
				{
					System.err.println("Number of blocks per experiment must be greater than 0.");
					System.exit(0);
				}
			}
			else if( result[0].equals("Random sequence generator (true/false)") )
			{ 
				//False is the default value of "generateRandomSequence". If the application is unable to read a value, it will remain false.
				String intendedBoolean = result[1].toLowerCase();
				
				if(intendedBoolean.equals("true") || intendedBoolean.equals("t"))
				{generateRandomSequence = true;}
				else if(intendedBoolean.equals("false") || intendedBoolean.equals("f"))
				{generateRandomSequence = false;}
				else
				{ 
					System.err.println("It was not possible to read \"Random sequence generator\" from Config.txt.\n"
									 + "(Missing or wrong value?)\nUsing default value (false).");
				}
			}
			else if( result[0].equals("Number of the device to be used") )
			{
				int value = 99;
				
				try{  value = Integer.parseInt(result[1]);  }
				catch(Exception e)
				{ 
					System.err.println("It was not possible to read \"Number of the device to be used\" from Config.txt.\n"
									 + "(Missing or wrong value?)\nNow exiting");
					System.exit(0);
				}
				
				if(value == 0) 
				{ activateLeapMotion = true; }
				else
				{ activateLeapMotion = false; }
				
				deviceID = value;
			}
			else if( result[0].equals("Is user right-handed? (true/false)") )
			{ 
				//True is the default value of "rightHanded".
				String intendedBoolean = result[1].toLowerCase();
				
				if(intendedBoolean.equals("false") || intendedBoolean.equals("f"))
				{ rightHanded= false;}
				else if(intendedBoolean.equals("true") || intendedBoolean.equals("t"))
				{ rightHanded= true;}
				else
				{ 
					System.err.println("It was not possible to read \"Is user right-handed?\" from Config.txt.\n"
									 + "(Missing or wrong value?)\nUsing default value (true).");
				}
			}			
			else if( result[0].equals("Center offset X (integer)") )
			{ 
				try{  offsetX = Integer.parseInt(result[1]);  }
				catch(Exception e)
				{ 
					System.err.println("It was not possible to read \"Center offset X\" from Config.txt.\n"
									 + "(Missing or wrong value?)\nNow exiting");
					System.exit(0);
				}
			}
			
			else if( result[0].equals("Center offset Y (integer)") )
			{ 
				try{  offsetY = Integer.parseInt(result[1]);  }
				catch(Exception e)
				{ 
					System.err.println("It was not possible to read \"Center offset Y\" from Config.txt.\n"
									 + "(Missing or wrong value?)\nNow exiting");
					System.exit(0);
				}
			}
		}

		//Check if the acquired values are respect the established limits.
		if( !(numberOfCircles >= 2 && numberOfCircles <= 32) )
		{
			System.err.println("\"Number of circles\" can only vary between 2 and 32.");
			System.exit(3);
		}
		else if( !(circleRadius >= 1.0f && circleRadius <= 200.0f) )
		{
			System.err.println("\"Circle radius\" can only vary between 1 and 200.");
			System.exit(4);
		}
		else if( !(centerDistance >= 0.0f && centerDistance <= 800.0f) )
		{
			System.err.println("\"Distance between circles and frame center\" can only vary between 0 and 800.");
			System.exit(5);
		}
		
		//Initialize the required variables based on the values read  
		targetSizeX = (int) (0.75 * 2 * circleRadius);		//Warning: Processing receives the diameter and not the radius. Multiply by 2. 
		targetSizeY = (int) (0.125 * 2 * circleRadius);		//Note: 0.75 and 0.125 are resizing values to assure that the "+" can fit inside a circle.
		
		circles = new Vector<Circle>(numberOfCircles);
		sequenceToPerform = new Sequence(numberOfCircles, generateRandomSequence);
	}
	
	/**
	 * Function that starts the Leap Motion device (and respective functions) and creates a new thread 
	 * for it to run exclusively.
	 * 
	 * <p>If the Leap Motion is already started but has been stopped, this function will restart it.
	 */
	private void activateLeapMotion() 
	{
		if(leapMotionDevice == null)
		{
			leapMotionDevice = new LeapMotion(desiredControlMethod, rightHanded, windowWidth, windowHeight);
			lmThread = new Thread("Leap Motion Listener") {
				public void run(){
					//Boolean represents if user is right handed or not.
					leapMotionDevice.initialize();
				};
			};
			lmThread.start();
		}
		else
		{
			leapMotionDevice.turnOn();
		}
	}
	
	/**
	 * Function that stops the Leap Motion device.
	 * 
	 * <p><b>Note:</b> Stopping the Leap Motion does not kill the thread or terminates the Leap Motion. It just stops
	 * 		 the Leap Motion from moving the pointer.
	 * 		 By using a listener in the Leap Motion, it was not possible to terminate the device through code.
	 */
	private void turnOffLeapMotion() 
	{
		leapMotionDevice.turnOff();
	}

	/**
	 * Function responsible for creating a Thread that will collect the Mouse position over time and perform the logical action
	 * of the application.
	 * 
	 * <p><b>Note:</b> This function only creates a Thread, doesn't start it. That must be done manually by the
	 * 		           by the programmer.
	 * 
	 * <p><b>Note2:</b> The sample rate (the number of times a position is registered) is 40 samples per seconds.
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
			
				while(true)
				{
					//Sleep 25 milliseconds. This allows the sample rate to be 40 samples per second.
					try {sleep(25);}catch (InterruptedException e) { e.printStackTrace();}
					
					//Store the values read. This way they can be accessible anywhere in this listener 
					//without taking the risk of them changing during the process.
					
					int readPositionX = mouseX;
					int readPositionY = mouseY;
					
					if(sequenceIndex == numberOfCircles)
					{  
						//If a click happens at this time, ignore it.
						ignoreMouseInput();
						
						//Store information collected.
						Pixel lastSequencePixel = null;
						Information dataToStore = null;
						
						for(int i = 1; i < numberOfCircles && !playingMode; i++)
						{
							//Calculate and determine the values to be stored.
							int circleId = sequenceToPerform.get(i);		
							int lastCircleId = sequenceToPerform.get(i-1);
							
							Circle startingCircle = circles.get(lastCircleId);
							Circle targetCircle = circles.get(circleId);
							
							Pixel startingCircleCenter = startingCircle.getCenterPixel();
							Pixel targetCircleCenter = targetCircle.getCenterPixel();
							
							double distanceBetweenPoints = Information.calculateDistanceBetweenPoints(startingCircleCenter, targetCircleCenter);
							int numberOfClicks = numberOfCliksPerTrial.get(i);
							long elapsedTime = timeForEachSequence.get(i) - timeForEachSequence.get(i-1);
							
							//Fill the information common to all this sequence trials. 
							if(dataToStore == null)
							{
								int targetWidth = startingCircle.getRadius() * 2;
								
								dataToStore = new Information();
								
								dataToStore.setNumberOfCircles(numberOfCircles);
								dataToStore.setTargetWidth(targetWidth);
								dataToStore.changeDevice(deviceID);
								dataToStore.changeUser(userID);
							}
							
							dataToStore.resetInformation();
							
							//Fill the specific information to this trial. 
							dataToStore.setStartingCircleCenter(startingCircleCenter);
							dataToStore.setEndingCircleCenter(targetCircleCenter);
							
							dataToStore.setCircleID(i);	//Note: The internal CircleID differs from the real CircleID. The real follows the Mackenzie circle.
							
							dataToStore.setDistanceBetweenCircles(distanceBetweenPoints);
							dataToStore.setDistanceBetweenFrameAndCircleCenter(centerDistance);
							dataToStore.setNumberOfClicks(numberOfClicks);
							dataToStore.setElapsedTime(elapsedTime);
							dataToStore.setSequenceNumber(currentSequenceNumber);
							dataToStore.setBlockNumber(currentBlockNumber);
							
							//Add the last sequence pixel to the beginning of this one.
							if(lastSequencePixel != null)
							{
								dataToStore.getPath().add(lastSequencePixel);
							}
							
							//Collect all samples belonging to this trial.
							while(true)
							{
								//Make a copy of the first sample
								Sample currentSample = new Sample();
								currentSample = mouseSamples.get(0);
								
								//Add it to the data to be stored
								dataToStore.addToPath(currentSample.getPixel());
								
								//Remove it from all the samples collected.
								mouseSamples.remove(0);
								
								//If a click happens, it means that the next sequence has begun
								if( currentSample.didClickHappen() )
								{	
									//Update the last sequence pixel.
									lastSequencePixel = currentSample.getPixel();
									
									//This sequence samples are all collected. Go for the next sequence
									break;	
								}
							}
							
							dataToStore.storeInformationInFile();
						}
						
						//In playing mode the sequence should not be increased as there is no need for such.
						if(!playingMode)
						{
							currentSequenceNumber++;
							displayTextSequencesLeft = determineActualSequence();
						}
						
						//If the maximum number of trials have been performed, terminate the experiment.
						if( numberOfBlocksPerExperiment == currentBlockNumber && numberOfSequencesPerBlock == currentSequenceNumber)
						{
							displayText = "The experiment is complete.\nThank you so much for participating!";
							
							System.out.println("Experiment successfully completed!");
							
							//A little time out to make sure the system has time to present the displayText
							try {Thread.sleep(4000);}catch (InterruptedException e) {}
							
							System.exit(1);
						}	
						
						//Update the current Sequence and block number if needed.
						if(currentSequenceNumber == numberOfSequencesPerBlock)
						{
							currentBlockNumber++;
							currentSequenceNumber = 0;
							displayTextSequencesLeft = determineActualSequence();
						}
						
						//Generate a new sequence
						sequenceToPerform = new Sequence(numberOfCircles, generateRandomSequence);
						
						//Set the variables to their default values.
						mouseClicked = false;
						mouseSamples = new Vector<Sample>();
						timeForEachSequence = new Vector<Long>();
						numberOfCliksPerTrial = new Vector<Integer>();
						numberOfClicksTrial = 0;
						
						displayText = "You may rest a bit if you so wish.\nWhen ready, press the + symbol!";
						
						sequenceIndex = 0;
						
						acceptMouseInput();
					}
					
					Sample readSample = new Sample(readPositionX, readPositionY);
					
					if(mouseClicked)
					{
						//Acknowledge mouse click
						mouseClicked = false;
						
						//Check if the click occurred inside the target circle
						Circle targetCircle = circles.get(sequenceToPerform.get(sequenceIndex));
					
						if( targetCircle.doesPointBelongToCircle(readPositionX, readPositionY) )
						{
							//Sound.playSucessSound();
							
							//Acknowledge correct click.
							readSample.clickHappened();
							
							//In playing mode, no information is registered.
							if(!playingMode)
							{
								//Store the time elapsed and number of clicks performed during this trial.
								timeForEachSequence.add( System.currentTimeMillis() );
								numberOfCliksPerTrial.add(numberOfClicksTrial);
							}

							displayText = "";
							numberOfClicksTrial = 0;
							mouseClickedInsideTarget = true;
						}
						else
						{
							if(sequenceIndex > 0 || playingMode )
							{	Sound.playFailureSound();	}
						}
					}
					
					//Collect mouse sample.
					if(sequenceIndex > 0 && !playingMode)
					{
						mouseSamples.add( readSample );
					}
					
					//If the target was correctly pressed advance to the next trial
					if(mouseClickedInsideTarget)
					{
						mouseClickedInsideTarget = false;
						sequenceIndex++;
					}
				}	
			}
		};
	}
	
	
	/**
	 * Function that creates a listener that will check if a mouse click has occurred and 
	 * play the respective feedback sound.
	 * 
	 * <p><b>Note:</b> The listener is only created, not SET. The programmer must add the listener 
	 *                 himself/herself.
	 * 
	 * <p><b>Note2:</b> The behavior of this listener can be altered by using the functions 
	 *                  "ignoreMouseInput()" and "acceptMouseInput()"
	 * 
	 * @return The said listener.
	 */
	private MouseListener createMouseListener()
	{
		return new MouseListener() 
		{
			@Override
			public void mousePressed(MouseEvent e) 
			{
				if(!ignoreMouseInput)
				{
					//If the mouse click hasn't been processed, do nothing.
					if(mouseClicked)
					{return;}

					numberOfClicksTrial++;
					
					mouseClicked = true;
				}
			}
			
			//Not in use.
			public void mouseClicked(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e){}
			public void mouseReleased(MouseEvent e){}
		};
	}
	
	/**
	 * Function that alters the Mouse Listener behavior.
	 * The listeners is to do nothing even if a click is performed.
	 * 
	 * <p><b>Note:</b> To alter this behavior use "acceptMouseInput()" function. 
	 */
	private void ignoreMouseInput()
	{
		ignoreMouseInput = true;
	}
	
	/**
	 * Function that alters the Mouse Listener behavior.
	 * <br>The listeners is to register the mouse click when one happens.
	 * 
	 * <p><b>Note:</b> To alter this behavior use "ignoreMouseInput()" function.
	 * 
	 * <p><b>Note2:</b> This is the default behavior of the listener. 
	 */
	private void acceptMouseInput()
	{
		ignoreMouseInput = false;
	}
	
	/**
	 * Function that creates a key listener in order to alter the application parameters.
	 * The keys are as follows:
	 * <br> - > "R" key - Changes if the sequence is randomly generated or follows the same pattern as Makenzie's. (DEBUG only)
	 * <br> - > "G" key - Changes the gesture being used by the Leap Motion to interact with the application. (DEBUG only)
	 * <br> - > "H" key - Changes if the user is right-handed or left-handed. (DEBUG only)
	 * <br> - > "P" key - Enables/disables playing mode.		
	 * <br> - > ["0"-"9"] - Keys between 0 and 2 change the device number to the one pressed. (DEBUG only)
	 * <br> - > "Esc" key - exits the application (due to Processing).
	 * 
	 * <p><b>Note:</b> Remember, this function only generates the listener. It must be added manually!
	 * 
	 * @return The said listener.
	 */
	private KeyListener createKeyListener()
	{
		return new KeyListener() 
		{
			@Override
			public void keyReleased(KeyEvent e) 
			{
				//Change sequence generator from random to Mackenzie's paper and vice-versa.
				//"R" key pressed.
				if(e.getKeyCode() == 82 && debug)
				{
					generateRandomSequence = !generateRandomSequence;
					
					String message = "The sequence generator was change to:\n";
					
					if(generateRandomSequence)
					{	message += "Random.";	}
					else
					{	message += "Mackenzie's style.";	}
					
					displayText = message;
				}
				
				//Change the cursor movement and selection controls when using the Leap Motion.
				//"G" key pressed.
				else if(e.getKeyCode() == 71 && debug)
				{
					//If the Leap Motion isn't active, do nothing.
					if(deviceID != 0)
					{return;}
					
					desiredControlMethod = leapMotionDevice.changeControlMode();
					
					displayText = "Selection and movement were altered:\n" 
								  + LeapMotion.controlModeToString(desiredControlMethod);
				}
				
				//Changes the dominant hand of the user when using Leap Motion to control the cursor.
				//"H" key pressed
				else if(e.getKeyCode() == 72 && debug)
				{
					//If the Leap Motion isn't active, do nothing.
					if(deviceID != 0)
					{return;}
					
					rightHanded = !rightHanded;
					
					String message = "The user dominant is set to: ";
					
					if(rightHanded)
					{	message += " Right.";	}
					else
					{	message += " Left.";	}
					
					leapMotionDevice.changeDominantHand();
					
					displayText = message;
				}
				
				//Changes the application to playing mode. In other words, the user can practice, and no result will be saved.
				//"P" key pressed
				else if(e.getKeyCode() == 80)
				{
					playingMode = !playingMode;
					
					if(playingMode)
					{
						displayText = "Play mode is active";
					}
					else
					{
						displayText = "";
					}	
				}
				
				//Changes the Device number.
				//["0" - "9"] key pressed.
				else if(e.getKeyCode() >= 96 && e.getKeyCode() <= 105 && debug)
				{ 
					/*
					 * The device number are as follow:
					 *    -> 0 = Leap Motion;
					 *    -> 1 = Mouse;
					 *    -> 2 = Touch Pad;
					 */
					int numberPressed = e.getKeyCode() - 96;
					
					//If the number pressed is the same as the current device, don't do anything.
					if(deviceID == numberPressed)
					{return;}
					
					deviceID = numberPressed;
					
					if(numberPressed == 0)
					{
						activateLeapMotion();
					}
					else
					{
						turnOffLeapMotion();
					}
					
					String message = "The device was changed to:\n";					
					
					switch(deviceID)
					{
						case 0:
							message += "Leap Motion device";
							break;
							
						case 1:
							message += "Mouse";
							break;
							
						case 2:
							message += "Touch Pad";
							break;
							
						case 4:
							message += "Leap Motion + Touchless";
							break;
							
						default:
							message += "Device number " + deviceID;
					}
					
					displayText = message; 
				}
				
				//Changes the userID, incrementing it by one.
				//"+" key pressed.
				else if(e.getKeyCode() == 107 && debug)
				{
					userID++;
					displayText = "The User ID was changed to: " + userID;  
				}
				
				//Changes the userID, decrementing it by one. 
				//The ID cannot be less than 0.
				//"-" key pressed.
				else if(e.getKeyCode() == 109 && debug)
				{
					userID--;
					
					if(userID < 0)
					{
						userID = 0;
					}
					
					displayText = "The User ID was changed to: " + userID;  
				}
			}
			
			//Not in use.
			public void keyPressed(KeyEvent e){}
			public void keyTyped(KeyEvent arg0){}
		};
	}
	
}