package core;

import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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
	boolean createNewExperience = true;
	
	private final Chronometer chronometer = new Chronometer();
	
	LeapMotion leapMotionDevice = new LeapMotion(ControlMode.HANDS_WITH_KEYTAP_GESTURE, true); //Boolean represents if user is right handed or not.
	boolean useLeapMotion = false;
	
	//Variables related to the presentation of text on the application.
	PFont font;
	int displayFontSize;
	String displayText;
	
	MouseMotionListener mouseMotionList;
	
	private int teste = 0;
	private boolean debug = false;
	
	/**
	 * Extended function from Processing.
	 * This function is only executed once.
	 */
	public void setup()
	{
		//Loading the required values to the execution of the application from the configuration text file ("Config.txt").
		loadConfigurationFile();
		
		//Star Leap Motion device in its own thread

		Thread lmThread = new Thread("Leap Motion Listener") {
			public void run(){
				leapMotionDevice.initialize();
			};
		};
		lmThread.start();

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
						
			double coordinateX = centralPositionX + 
					centerDistance * Math.cos(currentAngle);
						
			double coordinateY = centralPositionY + 
					centerDistance * Math.sin(currentAngle);
					
			circles.add( 
				new Circle( ((int) coordinateX), ((int) coordinateY), ((int) circleRadius))
			);
		}
		
		this.addComponentListener( new ComponentAdapter() {
			public void componentResized(ComponentEvent e)
			{	redrawElements = true;	 }
		});
		
		this.addMouseListener( createMouseListener() );
		
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
		
		if( currentSequenceIndex == sequenceToPerform.size() )
		{	
			System.out.println("Evaluation successfully completed!\nExiting...");
			
			//A little pause so the user can see the goodbye message. 
			try {Thread.sleep(2000);}catch (InterruptedException e) {}
			
			System.exit(1);
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
		
		Circle targetCircle = circles.get(sequenceToPerform.get(currentSequenceIndex));
		drawTargetSign(targetCircle.getCenterX(), targetCircle.getCenterY());
	}

	/**
	 * Handler to be called when the mouse button is pressed.
	 */
	protected void mouseButtonPressed() 
	{
		Circle targetCircle = circles.get(sequenceToPerform.get(currentSequenceIndex));
		
		if( currentSequenceIndex > 0 )
		{
			Circle selectedCircle = circles.get(sequenceToPerform.get(currentSequenceIndex));
			
			informationFromCurrentTrial.clickedOccurred();
			
			boolean isSelectionRight = selectedCircle.doesPointBelongToCircle(mouseX, mouseY);
			
			if( isSelectionRight  )
			{
				chronometer.stop();
				this.removeMouseMotionListener(mouseMotionList);
				
				Sound.playSucessSound();
					
				Circle lastCircle = circles.get(sequenceToPerform.get(currentSequenceIndex - 1));
				
				//Save collected data.
				informationFromCurrentTrial.setNumberOfCircles(numberOfCircles);
				informationFromCurrentTrial.setStartingCircleCenter(lastCircle.getCenterPixel());
				informationFromCurrentTrial.setEndingCircleCenter(targetCircle.getCenterPixel());
				informationFromCurrentTrial.setTargetWidth(targetCircle.getRadius() * 2); 		//A largura do alvo é o diametro da circunferência.
				informationFromCurrentTrial.setElapsedTime(chronometer.getTimeInMilliseconds());
				informationFromCurrentTrial.setDistanceBetweenFrameAndCircleCenter(centerDistance);
				informationFromCurrentTrial.setDistanceBetweenCircles(
						Information.calculateDistanceBetweenPoints(
								lastCircle.getCenterX(), lastCircle.getCenterY(), 
								targetCircle.getCenterX(), targetCircle.getCenterY())
				);
				
				//Store trial results...
				informationFromCurrentTrial.storeInformationInFile();
				
				//...and start a new one.
				informationFromCurrentTrial.resetInformation();
				
				//Print goodbye message
				if( (currentSequenceIndex + 1) == sequenceToPerform.size() )
				{	
					displayText = "Thank you very much for your time and help!\nGoodbye.";
					
					//A little time out to make sure the system has time to present the displayText
					try {Thread.sleep(200);}catch (InterruptedException e) {}
					
					//Force to display the message earlier otherwise it might not show up. 
					displayText();
				}
				
				currentSequenceIndex++;
			}	
			else
			{
				Sound.playFailureSound();
			}
			
			//A little pause to avoid several "button presses" if the user remains with the buttom pressed. 
			try {Thread.sleep(200);}catch (InterruptedException e) {}
			
			if( isSelectionRight )
			{
				//Restart listener.
				mouseMotionList = createMouseMotionListener();
				this.addMouseMotionListener(mouseMotionList);
				
				//Restart chronometer.
				chronometer.reset();	chronometer.start();
			}
			
		}
		else if( currentSequenceIndex == 0 )
		{
			//The experience should only be started when the user presses with sucess the first default target.
			Circle selectedCircle = circles.get(sequenceToPerform.get(currentSequenceIndex));
			
			Boolean wasRightCirclePressed = selectedCircle.doesPointBelongToCircle(mouseX, mouseY);
			
			if( wasRightCirclePressed )
			{
				Sound.playSucessSound();
				currentSequenceIndex++;
			}	
			
			//A little pause to avoid several "button presses" if the user remains with the buttom pressed. 
			try {Thread.sleep(200);}catch (InterruptedException e) {}
			
			if( wasRightCirclePressed )
			{
				//Don't print anything in the screen.
				displayText = "";
				
				//Start listener that will store the mouse positions over the time.
				mouseMotionList = createMouseMotionListener();
				this.addMouseMotionListener(mouseMotionList);
				
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
		
		//Processing funtion. Draws a rectangle.
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
			
			//Guarda os valores lidos nas variáveis adequadas.
			if( result[0].equals("Number of circles") )
			{ numberOfCircles = Integer.parseInt(result[1]); }
			else if( result[0].equals("Circle radius") )
			{ circleRadius = Integer.parseInt(result[1]); }
			else if( result[0].equals("Distance between circles and frame center") )
			{ centerDistance = Integer.parseInt(result[1]); }
		}
		
		//Check if the acquired values are respect the estabilished limits.
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
	private MouseMotionListener createMouseMotionListener()
	{
		return new MouseMotionListener() 
		{
			public void mouseMoved(MouseEvent e) 
			{ informationFromCurrentTrial.storeCursorPosition(mouseX, mouseY);}
			
			public void mouseDragged(MouseEvent e) {}
		};
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
}