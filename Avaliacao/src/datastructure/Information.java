package datastructure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Vector;

public class Information
{
	private int numberOfCircles;
	private Pixel startingCircleCenter;
	private Pixel endingCircleCenter;
	private int targetWidth; 							//In other words, the circles' diameter, in pixels. 
	private int circleId;
	
	private float distanceBetweenCirclesAndFrameCenter;	//In pixels.
	private double distanceBetweenCircles;				//In pixels.

	private int numberOfClicks;							//In pixels.

	private Vector<Pixel> path;							//Sequence (and order) of pixels to be traveled during the experiment.

	private long elapsedTime;

	private int device;									//Integer symbolizing the device being used during the experiment. 
	private int userId;										
	private int sequenceNumber;							
	private int blockNumber;							

	private String fileToStoreInfoPath;  					

	/**
	 * Constructor of the Class Information.
	 * <br>Creates a structure that stores all the information obtained during a trial.
	 * 
	 * <p>It also contains some static auxiliary functions that calculate the distance between two points.
	 *
	 * <p><b>Note:</b> This class only stores information. Altering values in this class won't alter
	 * 		 the application parameters. For that, the "Config.txt" file should be altered.
	 */
	public Information() 
	{
		//Default values
		this.device = 0;			this.userId = 0;		
		this.sequenceNumber = 0;	this.blockNumber = 0;   
		
		this.fileToStoreInfoPath = "";
		
		this.numberOfCircles = 0;
		this.targetWidth = 0;
		this.distanceBetweenCirclesAndFrameCenter = 0;	

		//Default values for the variables whose information is altered more frequently.
		resetInformation();
		
		//Create a folder, named "Results", where the results from the experiment will be stored. 
		createResultFolder();
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Function that makes a copy of all the values of this class.
	 * <br>A solution to avoid arguments passed by reference.
	 * 
	 * @return A copy of the class.
	 */
	public Information copy()
	{
		Information copyOfOriginal = new Information();
		
		copyOfOriginal.device = this.device;
		copyOfOriginal.userId = this.userId;
		copyOfOriginal.circleId = this.circleId;
		copyOfOriginal.elapsedTime = this.elapsedTime;
		copyOfOriginal.targetWidth = this.targetWidth;
		copyOfOriginal.blockNumber = this.blockNumber;
		copyOfOriginal.sequenceNumber = this.sequenceNumber;
		copyOfOriginal.numberOfClicks = this.numberOfClicks;
		copyOfOriginal.fileToStoreInfoPath = this.fileToStoreInfoPath;
		copyOfOriginal.numberOfCircles = this.numberOfCircles;
		copyOfOriginal.path = (Vector<Pixel>) this.path.clone();
		copyOfOriginal.endingCircleCenter = this.endingCircleCenter;
		copyOfOriginal.startingCircleCenter = this.startingCircleCenter;
		copyOfOriginal.distanceBetweenCirclesAndFrameCenter = this.distanceBetweenCirclesAndFrameCenter;
		
		return copyOfOriginal;
	}
		
	/**
	 * Function that resets all the more frequently changed class' variables to their default values.
	 * 
	 * <p><b>Note:</b> Device number, User ID, block number and path to where the results are stored WILL NOT suffer any change.
	 */
	public void resetInformation()
	{
			
		this.setNumberOfClicks(0);			this.setStartingCircleCenter(new Pixel(0,0));		this.path = new Vector<Pixel>();
		this.setElapsedTime(0);				this.setEndingCircleCenter(new Pixel(0,0));			this.setDistanceBetweenCircles(0.0);
		this.circleId = 0;
	}
	
	/**
	 * Function that returns the number of circles drawn in the application.
	 * 
	 * @return Number of circles drawn as integer value.
	 */
	public int getNumberOfCircles() 
	{ return numberOfCircles; }

	/**
	 * Function that sets the number of circles drawn in the application.
	 * 
	 * <p><b>Note:</b> This function is used by the application to inform this class of the number of circles drawn.
	 * 		 <br>The inverse is not possible. To alter the number of circles drawn, 
	 * 		 one must alter the "Config.txt" file.
	 * 
	 * @param numberOfCircles - Number of circles drawn on the application.
	 */
	public void setNumberOfCircles(int numberOfCircles) 
	{ this.numberOfCircles = numberOfCircles; }

	/**
	 * Function that returns this trial's starting Circle's center. 
	 * <br>The starting circle is the last trial's target (or end) circle. 
	 * 
	 * <p><b>Note:</b>The result is a value of the Class Pixel.
	 * 
	 * @return Pixel with the coordinates of the Circle's center.
	 */
	public Pixel getStartingCircleCenter() 
	{ return startingCircleCenter; }

	/**
	 * Functions that sets this trial's starting Circle's center. 
	 * 
	 * <p><b>Note:</b> This function can't be used to create a circle on the application. It should
	 * 		 only be used by the application to fill this class' variables. 
	 * 
	 * @param StartingCircleCenter - Pixel where the starting circle's center is.
	 */
	public void setStartingCircleCenter(Pixel StartingCircleCenter) 
	{ this.startingCircleCenter = StartingCircleCenter; }

	/**
	 * Function that returns this trial's ending Circle's center. The ending circle is also the next trial's starting circle. 
	 * 
	 * <p><b>Note:</b> The result is a value of the Class Pixel.
	 * 
	 * @return Pixel with the coordinates of the Circle's center.
	 */
	public Pixel getEndingCircleCenter() 
	{ return endingCircleCenter; }

	/**
	 * Functions that sets this trial's target (or ending) Circle's center. 
	 * 
	 * <p><b>Note:</b> This function can't be used to create a circle on the application. It should
	 * 		 only be used by the application to fill this class variables. 
	 * 
	 * @param endingCircleCenter - Pixel where the ending circle's center is.
	 */
	public void setEndingCircleCenter(Pixel endingCircleCenter) 
	{ this.endingCircleCenter = endingCircleCenter; }

	/**
	 * Function that returns this trial's circles' width.
	 * 
	 * <p><b>Note:</b> The width is the circle radius * 2.
	 * 
	 * <p><b>Note2:</b> The width is the same for every circle on the experiment.
	 * 
	 * @return A integer with the circle's radius.
	 */
	public int getTargetWidth() 
	{ return targetWidth; }

	/**
	 * Function that sets this trial's circles' width.
	 * 
	 * <p><b>Note:</b> This function is used by the application to inform this class of the target width.
	 * 		 To change the radius, and consequently the width, the "Config.txt" should be altered.
	 * 
	 * @param targetWidth - The circles' width.
	 */
	public void setTargetWidth(int targetWidth) 
	{ this.targetWidth = targetWidth; }
	
	/**
	 * Function that returns the current circle ID.
	 * 
	 * @return The actual circle ID on the current trial.
	 */
	public int getCircleID() 
	{return circleId;}

	/**
	 * Function that sets the current circle ID.
	 * 
	 * @param circleID - The intended new circle ID.
	 */
	public void setCircleID(int circleID) 
	{this.circleId = circleID ;}
	
	/**
	 * Function that increases the current circle ID by one.
	 */
	public void increaseCircleID()
	{circleId++;}
	
	/**
	 * Function that returns the distance between the center of one circle and the application's
	 * frame center, in pixels.
	 * 
	 * <p><b>Note:</b> This distance is the same for all circles.
	 * 
	 * @return Integer value with the distance between the circle and the frame/window center.
	 */
	public float getDistanceBetweenFrameAndCircleCenter() 
	{ return distanceBetweenCirclesAndFrameCenter; }

	/**
	 * Function that sets the distance between one circle's center and the application's frame/window center.
	 *
	 * <p><b>Note:</b> This distance is the same for all circles. 
	 * 
	 * @param distance - An integer with the distance between both centers.
	 */
	public void setDistanceBetweenFrameAndCircleCenter(float distance) 
	{ this.distanceBetweenCirclesAndFrameCenter = distance; }
	
	/**
	 * Function that returns this trial's distance between the starting and ending 
	 * circles' center.
	 * 
	 * @return A Double with the distance between both circles' center.
	 */
	public double getDistanceBetweenCircles() 
	{ return distanceBetweenCircles; }

	/**
	 * Function that sets this trial's distance between the starting and ending circles' center.
	 * 
	 * @param distanceBetweenCircles - A Double with the distance between both circles' center.
	 */
	public void setDistanceBetweenCircles(double distanceBetweenCircles) 
	{ this.distanceBetweenCircles = distanceBetweenCircles; }
	
	/**
	 * Function that returns the number of clicks performed by user to during the current trial.
	 * 
	 * <p><b>Note:</b> A successful trial only takes one click to perform. If more than one, the trial
	 * 		 should be considered unsuccessful.
	 * 
	 * @return An integer with number of clicks performed by the user.
	 */
	public int getNumberOfClicks() 
	{ return numberOfClicks; }

	/**
	 * Function that sets the number of clicks the user performed during the trial.
	 * 
	 * <p><b>Note:</b> A successful trial only takes one click to perform. If more, the trial
	 * 		 should be considered unsuccessful.
	 * 
	 * @param numberOfClicks - Number of clicks performed by the user.
	 */
	public void setNumberOfClicks(int numberOfClicks) 
	{ this.numberOfClicks = numberOfClicks; }

	/**
	 * Function that informs this class that a click occurred, incrementing the number of
	 * clicks by one.
	 * 
	 * <p>A possible substitute to a would be "setNumberOfClicks(...)" function.
	 */
	public void clickedOccurred()
	{ this.numberOfClicks++; }
	
	/**
	 * Function that returns the path traveled by the pointer during the trial.
	 * 
	 * <p>The path is the successive pixels occupied by the pointer when moving from the starting
	 * circle to the target circle.
	 * 
	 * @return The path performed by the cursor.
	 */
	public Vector<Pixel> getPath() 
	{ return this.path; }
	
	/**
	 * Function that alters current path, vector of pixels, to the intended one.
	 * 
	 * @param intendedPath - The new path.
	 */
	public void setPath(Vector<Pixel> intendedPath) 
	{ this.path = intendedPath; }
	
	/**
	 * Function that adds a single Pixel to the path.
	 * 
	 * <p><b>Note:</b> The pixel added is considered the last pixel traveled by the pointer.
	 * 
	 * @param value - The pixel to be add.
	 */
	public void addToPath(Pixel value)
	{	this.path.add(value);	}
	
	/**
	 * Function that returns the time it took the user to perform this trial.
	 * 
	 * @return A Long value with time elapsed in milliseconds.
	 */
	public long getElapsedTime() 
	{ return elapsedTime; }

	/**
	 * Function that sets the time it took the user to perform this trial.
	 * 
	 * @param elapsedTime - The time the user took to perform the trial.
	 */
	public void setElapsedTime(long elapsedTime) 
	{ this.elapsedTime = elapsedTime; }

	/**
	 * Function returns the number (or ID) of the device being used.
	 * 
	 * <p><b>Note:</b> Internally, the device ID has no meaning. This can be anything the user defines.
	 * 		 <br>The only exception is 0, which is attributed to the Leap Motion + Grabbing gesture.
	 * 
	 * @return The ID of the device used to perform this trial.
	 */
	public int getDeviceID() 
	{ return this.device; }

	/**
	 * Function that changes the Device ID to the one desired.
	 *
	 * <p><b>Note:</b> Internally, the device ID has no meaning. This can be anything the user defines.
	 * 		 <br>The only exception is 0, which is attributed to the Leap Motion + Grabbing gesture.
	 * 
	 * @param deviceNumber - The desired device ID.
	 */
	public void changeDevice(int deviceNumber)
	{ this.device = deviceNumber; }

	/**
	 * Function that returns the userID for this trial . 
	 * 
	 * <p>The ID represents the user that performed / is performing the trial.
	 * 
	 * @return The current experiment's User ID.
	 */
	public int getUserID()
	{ return userId; }
	
	/**
	 * Function that changes the User ID to the next available. 
	 * <br>Basically, the function increases the ID by one.
	 */
	public void nextUser() 
	{ userId++;	}

	/**
	 * Function that changes the User to the the last one.
	 * <br>It's possible to return to the first User ID by using this function
	 * several times.
	 * 
	 * <p><b>Note:</b> The first User ID is 0.
	 * 
	 * <p><b>Note2:</b> If used when the User ID is 0, nothing happens.
	 */
	public void lastUser() 
	{
		userId--;
	
		if(userId < 0)
		{
			System.err.println("The UserId must not be less than 0.");
			userId = 0;
		}
	}

	/**
	 * Functions that switches the UserId to a specific one.
	 * 
	 * <p><b>Note:</b> User Id cannot be less than 0.
	 * 
	 * @param userId - The desired User ID.
	 */
	public void changeUser(int userId)
	{
		if(userId < 0)
		{
			this.userId = 0;
		}
		else
		{
			this.userId = userId;
		}
	}
	
	/**
	 * Function that returns the actual sequence number.
	 * <br>In other words, the ID of the current target circle.
	 * 
	 * @return A number (or ID) of the current sequence number.
	 */
	public int getSequenceNumber() 
	{return sequenceNumber;}

	/**
	 * Function that sets the current sequence number.
	 * 
	 * @param sequenceNumber - The intended sequence number.
	 */	
	public void setSequenceNumber(int sequenceNumber) 
	{this.sequenceNumber = sequenceNumber;}
	
	/**
	 * Function that sets the current sequence number to 0.
	 */
	public void resetSequenceNumber() 
	{this.sequenceNumber = 0;}
	
	/**
	 * Function that changes the sequence number to the next available.
	 * <br>In other words, sequence number is increased by one.
	 */
	public void increaseSequenceNumber()
	{sequenceNumber++;}
	
	/**
	 * Function that returns the actual block number.
	 * <br>Remembering, a block symbolizes a set of one or more sequences.
	 * 
	 * @return The actual block number.
	 */
	public int getBlockNumber() 
	{ return blockNumber; }

	/**
	 * Function that changes the block number to the given one.
	 * 
	 * <p>In other words, the block number is increased by one.
	 */
	public void setBlockNumber(int block)
	{ blockNumber = block; }
	
	/**
	 * Function that changes the block number to the next available.
	 * 
	 * <p>In other words, the block number is increased by one.
	 */
	public void increaseBlockNumber()
	{ blockNumber++; }

	/**
	 * Function that prints all the class variables to the console, except the path traveled by
	 * the pointer during the trial.
	 */
	public void printInfoWithoutPath()
	{
		double distancePoints;
		
		if(path.size() == 0)
		{
			distancePoints = 0.0;
		}
		else
		{
			distancePoints = calculateDistanceBetweenPoints(path.firstElement(), path.lastElement());
		}
		
		System.out.println(
			"The device number used was: " + device + ".\n" +
			"The User ID was: " + userId + ".\n" +
			"The current sequence number is: " + sequenceNumber + "\n" +
			"and belongs to block number: " + blockNumber + ".\n\n" + 
			
			"In this trial " + numberOfClicks + " click(s) were performed.\n" +
			"There were " + numberOfCircles + " circles,\n" +
			"and were " + distanceBetweenCirclesAndFrameCenter + " pixels away from the center of the frame.\n" + 
			"The starting circle's center was in pixel: <" + getStartingCircleCenter().getXCoordinate() + "," + getStartingCircleCenter().getYCoordinate()  + ">\n" +
			"and the ending one was in pixel: <" + getEndingCircleCenter().getXCoordinate() + "," + getEndingCircleCenter().getYCoordinate() + ">\n" +
			"(whose ID is: " + circleId + " ).\n" +
			"As such, they were: " + distanceBetweenCircles + " pixels away from each other.\n" +
			"Both had a diameter of " + targetWidth + " pixels, which means " +  ((double) targetWidth) / 2.0 + " pixels as radius.\n" +
			
			"This trial took " + elapsedTime + " milisegundos to perform.\n" +
			"The first and last point recovered were " + distancePoints + " pixels away from each other.\n"
		);	
	}

	/**
	 * Function that prints all the class variables to the console, including the path traveled
	 * by the pointer during the trial.
	 */
	public void printInfoWithPath()
	{
		printInfoWithoutPath();

		System.out.println("The path was as follows:");
		for(int i = 0; i < path.size(); i++)
		{
			Pixel ponto = path.get(i);
			System.out.println( "< " + ponto.getXCoordinate() + " , " + ponto.getYCoordinate() + " > ->" );
		}
	}
	
	/**
	 * Function that creates a folder, named "Results", where all the results from the evaluation will be stored.
	 */
	private void createResultFolder()
	{
		//Check if the "Results" folder (the folder where all results will be stored) already exists.
		String nameMainFolder = "Results";
		File mainFolder = new File(nameMainFolder);

		if ( !mainFolder.exists() ) 
		{
			//If it does not exist create it.
			mainFolder.mkdir();
		}
	}
	
	/**
	 * Function that creates a file where the results (from the experiment) saved in this class will be stored.
	 * 
	 * <p><b>Note:</b> The filename is (UserID) + ".txt".
	 * 
	 * @return A string containing the path of the created file.
	 */
	private String createStoreFile()
	{
		//Discover the path where the file will be stored.
		String nameStoreFile = "Results"+ "/" + this.userId + ".txt";
		File storeFile = new File(nameStoreFile);

		//If the file doesn't exist, create it. 
		if(	!storeFile.exists() )
		{
			//Create the file.
			try 
			{
				storeFile.createNewFile();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				System.err.println("It was not possible to create a file where to store the results.");
				System.exit(0);
			}
	
			try 
			{
				//Write the header.
				Writer writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(storeFile,true), "UTF8"));
	
				//Create the file Header.
				writer.write(
						"NumberDevice"       + " " + "UserId" 			  + " " + 
						"Block"              + " " + "Sequence" 		  + " " +
						"NumberClicks" 		 + " " + "NumberCircles"      + " " + 
						"CircleID" 			 + " " + "DistanceCenter" 	  + " " +
						"PixelStartCircleX"  + " " + "PixelStartCircleY"  + " " +
						"PixelEndCircleX"    + " " + "PixelEndCircleY"    + " " +  
						"TargetWidth" 		 + " " + "ElapsedTime"        + " " +  
						"MouseX"             + " " + "MouseY" 			  + "\n"
				);
	
				writer.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				System.err.println("It was not possible to generate the header.\nNow exitting.");
				System.exit(0);
			}
		}
		
		return nameStoreFile;
	}

	/**
	 * Function that stores the trial's results on the correct file.
	 */
	public void storeInformationInFile()
	{
		//Check to see if the file where the results will be stored already exists.
		if( fileToStoreInfoPath.equals("") )
		{
			//Create file
			fileToStoreInfoPath = createStoreFile();
		}
		
		File fileToWrite = new File(fileToStoreInfoPath);

		if ( !fileToWrite.exists() ) 
		{
			System.err.println("File where information was to be stored does not exist.\nAs such no results will be saved.");
			return;
		}
		
		try 
		{
			Writer writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(fileToWrite,true), "UTF8"));
			
			//Fill the columns with the respective information
			for(int w = 0; w < path.size(); w++)
			{
				int mousePositionX = path.get(w).getXCoordinate();
				int mousePositionY = path.get(w).getYCoordinate();
				
				writer.write(
					device							 + " " + userId 								+ " " + 
					blockNumber						 + " " + (sequenceNumber + 1) 					+ " " +
					numberOfClicks 					 + " " + numberOfCircles 				 		+ " " + 
					circleId 						 + " " + distanceBetweenCirclesAndFrameCenter 	+ " " +
					startingCircleCenter.toString()  + " " + endingCircleCenter.toString()	 		+ " " + 
					targetWidth 					 + " " + elapsedTime 					 		+ " " +  			
					mousePositionX 					 + " " + mousePositionY							+ "\n"
				);
			}
			writer.close();
		}
		catch(Exception e)
		{
			System.err.println("It was not possible to write to the inteded file.\nResults have been lost.");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * Function that stores the pointer position (coordinates) on the application.
	 * 
	 * @param xCoordinate - The current mouse X (width) coordinates. 
	 * @param yCoordinate - The current mouse Y (height) coordinates.
	 */
	public void storeCursorPosition(int xCoordinate, int yCoordinate)
	{ path.add(new Pixel(xCoordinate, yCoordinate) ); }

	/**
	 * Auxiliary function that calculates the distance between two points.
	 * 
	 * @param x1 - X coordinate of the first point.
	 * @param y1 - Y coordinate of the first point.
	 * @param x2 - X coordinate of the last point.
	 * @param y2 - Y coordinate of the last point.
	 * 
	 * @return The distance, in pixels, between the two given points.
	 */
	public static double calculateDistanceBetweenPoints(int x1, int y1, int x2, int y2)
	{
		/**
		 * Distance's formula:
		 *
		 * distance  = SquareRoot(  (x1 - x2)^2 + (y1 - y2)^2  )
		 */
		return Math.sqrt( Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) );
	}

	/**
	 * Auxiliary function that calculates the distance between two points.
	 * 
	 * @param p1 - One pixel.
	 * @param p2 - Other pixel.
	 * 
	 * @return The distance, in pixels, between the two given points.
	 */
	public static double calculateDistanceBetweenPoints(Pixel p1, Pixel p2)
	{
		return calculateDistanceBetweenPoints(p1.getXCoordinate(), p1.getYCoordinate(), p2.getXCoordinate(), p2.getYCoordinate());
	}
}
