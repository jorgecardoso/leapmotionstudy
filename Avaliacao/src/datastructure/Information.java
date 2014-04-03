package datastructure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class Information 
{
	private int numberOfCircles;
	private Pixel startingCircleCenter;
	private Pixel endingCircleCenter;
	private int targetWidth; 							//In other words, the circles' diameter. In pixels. 
	
	private int distanceBetweenCirclesAndFrameCenter;	//In pixels.
	private double distanceBetweenCircles;				//In pixels.
	
	private int numberOfClicks;							//In pixels.
	
	private Vector<Pixel> path;							//Sequence of pixels traveled by the cursor.
	
	private long elapsedTime;
	
	private int device;									
	private int userId;									//To be used when storing the information.	
	private int blockNumber;								//To be used when storing the information.
	
	private String fileToStoreInfo;  ///---------------- Fala implementar
	
	
	/**
	 * Constructor of the Class Information.
	 * Creates a structure that stores all the information obtained during a trial.
	 * 
	 * It also contains some static auxiliary functions that calculate the distance between two points.
	 *
	 * Note: This class only stores information. Altering values in this class won't alter
	 * 		 the application parameters. To do this, change the values in "Config.txt".
	 *
	 */
	public Information() 
	{
		this.setNumberOfCircles(0);			this.setDistanceBetweenFrameAndCircleCenter(0);		this.setTargetWidth(0);
		this.setNumberOfClicks(0);			this.setStartingCircleCenter(new Pixel(0,0));			this.path = new Vector<Pixel>();
		this.setElapsedTime(0);				this.setEndingCircleCenter(new Pixel(0,0));				this.setDistanceBetweenCircles(0.0);
		
		this.device = 0;		this.userId = 0; 		this.blockNumber = 0;
		
		fileToStoreInfo = null; /// ------- Falta implementar
	}
	
	/**
	 * Function that returns the number of circles drawn in the application.
	 * 
	 * @return NUmber of circles drawn as integer value.
	 */
	public int getNumberOfCircles() 
	{ return numberOfCircles; }

	/**
	 * Function that sets the number of circles drawn in the application.
	 * 
	 * Note: This function is used by the application to inform this class of the number of circles drawn.
	 * 		 The inverse is not possible. To alter the number of circles drawn, 
	 * 		 one must alter the "Config.txt" file.
	 * 
	 * @param numberOfCircles - Number of circles drawn on the application.
	 */
	public void setNumberOfCircles(int numberOfCircles) 
	{ this.numberOfCircles = numberOfCircles; }

	/**
	 * Function that returns this trial's starting Circle's center. The starting circle is the last trial's target (or end) circle. 
	 * 
	 * Note: The result is a value of the Class Pixel.
	 * 
	 * @return Pixel with the coordinates of the Circle's center.
	 */
	public Pixel getStartingCircleCenter() 
	{ return startingCircleCenter; }

	/**
	 * Functions that sets this trial's starting Circle's center. 
	 * 
	 * Note: This function can't be used to create a circle on the application. It's should
	 * 		 only be used by the application to fill this class variables. 
	 * 
	 * @param StartingCircleCenter - Pixel where the starting circle's center is.
	 */
	public void setStartingCircleCenter(Pixel StartingCircleCenter) 
	{ this.startingCircleCenter = StartingCircleCenter; }

	/**
	 * Function that returns this trial's ending Circle's center. The ending circle is also this trial's target circle. 
	 * 
	 * Note: The result is a value of the Class Pixel.
	 * 
	 * @return Pixel with the coordinates of the Circle's center.
	 */
	public Pixel getEndingCircleCenter() 
	{ return endingCircleCenter; }

	/**
	 * Functions that sets this trial's ending (or target) Circle's center. 
	 * 
	 * Note: This function can't be used to create a circle on the application. It's should
	 * 		 only be used by the application to fill this class variables. 
	 * 
	 * @param endingCircleCenter - Pixel where the ending circle's center is.
	 */
	public void setEndingCircleCenter(Pixel endingCircleCenter) 
	{ this.endingCircleCenter = endingCircleCenter; }

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
	 * Function that returns the number of clicks performed by user to perform this trial.
	 * 
	 * Note: A successful trial only takes one click to perform. If more, the trial
	 * 		 is considered unsuccessful.
	 * 
	 * @return An integer with number of clicks performed by the user.
	 */
	public int getNumberOfClicks() 
	{ return numberOfClicks; }

	/**
	 * Function that sets the number of clicks the user performed during the trial.
	 * 
	 * Note: A successful trial only takes one click to perform. If more, the trial
	 * 		 is considered unsuccessful.
	 * 
	 * @param numberOfClicks - Number of clicks performed by the user.
	 */
	public void setNumberOfClicks(int numberOfClicks) 
	{ this.numberOfClicks = numberOfClicks; }
	
	/**
	 * Function that informs this class that a click occurred, incrementing the number of
	 * clicks by one.
	 * 
	 * A possible substitute for the "setNumberOfClicks(...)" function.
	 */
	public void clickedOccurred()
	{ this.numberOfClicks++; }

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
	 * Function that returns this trial's circles' width.
	 * 
	 * Note: The width is the circle radius * 2.
	 * 
	 * @return A integer with the circles' radius.
	 */
	public int getTargetWidth() 
	{ return targetWidth; }

	/**
	 * Function that sets this trial's circles' width.
	 * 
	 * Note: This function is used by the application to inform this class of the target width.
	 * 		 To change the radius, and consequently the width, the "Config.txt" should be altered.
	 * 
	 * @param targetWidth - The circles' width.
	 */
	public void setTargetWidth(int targetWidth) 
	{ this.targetWidth = targetWidth; }
	
	/**
	 * Function that stores the current cursor position (coordinates) on the application.
	 * 
	 * @param xCoordinate - The current mouse X coordinates. 
	 * @param yCoordinate - The current mouse Y coordinates.
	 */
	public void storeCursorPosition(int xCoordinate, int yCoordinate)
	{ path.add(new Pixel(xCoordinate, yCoordinate) ); }
		
	/**
	 * Function that returns the path traveled by the cursor during the trial.
	 * 
	 * The path is the successive pixels occupied by the mouse when moving from the starting
	 * circle to the ending circle.
	 * 
	 * @return The path performed by the cursor.
	 */
	public Vector<Pixel> getPath() 
	{ return this.path; }
	
	/**
	 * Function that returns the distance between the center of one circle and the application's
	 * frame center.
	 * 
	 * Note: This distance is the same for all circles.
	 * 
	 * @return Integer value with the distance between the circle and application.
	 */
	public int getDistanceBetweenFrameAndCircleCenter() 
	{ return distanceBetweenCirclesAndFrameCenter; }

	/**
	 * Function that sets the distance between one circle's center and the application's frame center.
	 *
	 * Note: This distance is the same for all circles. 
	 * 
	 * @param distance - An integer with the distance between both centers.
	 */
	public void setDistanceBetweenFrameAndCircleCenter(int distance) 
	{ this.distanceBetweenCirclesAndFrameCenter = distance; }

	/**
	 * Function that returns the trial's User ID. 
	 * 
	 * The ID represents the user that's performing the trial.
	 * 
	 * @return The trial's User ID.
	 */
	public int getUserID()
	{ return userId; }
	
	/**
	 * Function that changes the User ID to the next available. 
	 * 
	 * Basically, the function increases the ID by one.
	 */
	public void nextUser() 
	{ userId++;	}
	
	/**
	 * Function that changes the User to the the last one.
	 * It's possible to return to the first User ID by using this function
	 * several times.
	 * 
	 * Note: The first User ID is 0.
	 * Note2: If used when the User ID is 0, nothing happens.
	 */
	public void lastUser() 
	{
		userId--;
		
		if(userId < 0);
		{
			System.err.println("The UserId must not be less than 0.");
			userId = 0;
		}
	}
	
	/**
	 * Functions that switches the UserId to a specific one.
	 * 
	 * Note: User Id cannot be less than 0.
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
	 * Function returns the number (or ID) of the device being used.
	 * 
	 * Note: The ID alone means nothing. It represents a device, but its up to the user
	 * 		 to give it meaning. For example, 0 could mean a mouse and a 1 could mean a Touchpad.
	 
	 * @return The ID of the device used to perform this trial.
	 */
	private int getDeviceID() 
	{ return this.device; }

	/**
	 * Function that changes the Device ID to the one desired.
	 * 
	 * @param deviceNumber - The desired device ID.
	 */
	public void changeDevice(int deviceNumber)
	{ this.device = deviceNumber; }
	
	/**
	 * Function that returns the actual block number.
	 * 
	 * A block symbolizes all the trials performed during a sequence.
	 * 
	 * @return The actual block number.
	 */
	public int getBlockNumber() 
	{ return blockNumber; }
	
	/**
	 * Function that changes the block number to the next available.
	 * 
	 * In other words, the block number increases by one.
	 */
	public void increaseBlockNumber()
	{ blockNumber++; }
	
	/**
	 * Function that prints all the class variables to the console, except the path traveled by
	 * the cursor during the cursor.
	 */
	public void printInfoWithoutPath()
	{
		double distancePoints = calculateDistanceBetweenPoints(path.firstElement(), path.lastElement());
		
		System.out.println(
			"The device number used was: " + this.device + ".\n" +
			"The User ID was: " + userId + ".\n" +
			"This trial belonged to block: " + blockNumber + ".\n\n" +
			"In this trial " + numberOfClicks + " click(s) were performed.\n" +
			"There were " + numberOfCircles + " circles,\n" +
			"and were " + distanceBetweenCirclesAndFrameCenter + " pixels away from the center of the frame.\n" + 
			"The starting circle's center was in pixel: <" + getStartingCircleCenter().getXCoordinate() + "," + getStartingCircleCenter().getYCoordinate()  + ">\n" +
			"and the ending one was in pixel: <" + getEndingCircleCenter().getXCoordinate() + "," + getEndingCircleCenter().getYCoordinate() + ">.\n" +
			"As such, they were: " + distanceBetweenCircles + " pixels away from each other.\n" +
			"Both had a diameter of " + targetWidth + " pixels, which means " +  ((double) targetWidth) / 2.0 + " pixels as radius.\n" +
			"This trial took " + elapsedTime + " milisegundos to perform.\n" +
			"The first and last point recovered were " + distancePoints + "pixels away from each other.\n"
		);	
	}
	
	/**
	 * Function that prints all the class variables to the console, including the path traveled
	 * by the cursor during the trial.
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
	 * Auxiliary function that calculates the distance between to points.
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
	 * Auxiliary function that calculates the distance between to pixels.
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
	
	
	
	
	
	
	public static void storeInformationInFile(Vector<Information> trialResults)
	{
		//Create folder where all results will be stored.
		String nameMainFolder = "../Resultados";
		File mainFolder = new File(nameMainFolder);
		
		//Check to see if this directory already exists
		if ( !mainFolder.exists() ) 
		{
			mainFolder.mkdir();
		}
		
		//Create another folder, inside the last one, where the results of trial will be stored
		DateFormat dateHourFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
		Date dateHour = new Date();
		
		String nameNewFolder = nameMainFolder+ "/" + dateHourFormat.format(dateHour).toString();
		File newFolder = new File(nameNewFolder);
		
		//Check if directory already exists, which is unlikely ...
		if ( !newFolder.exists() ) 
		{
			newFolder.mkdir();
		}
		
		//Write the results from each trial
		for(int i = 0; i < trialResults.size(); i++)
		{
			Information values = trialResults.get(i);
			
			String nameResultingFile = nameNewFolder + "/trial" + i + ".txt";
			File resultingFile = new File(nameResultingFile);
			
			if ( !resultingFile.exists() ) 
			{
				try 
				{
					resultingFile.createNewFile();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
					System.err.println("It was not possible to store the intended information. Error on function \"storeInformationInFile\".");
					return;
				}
			}

			try 
			{
				PrintWriter writer;
				writer = new PrintWriter(nameResultingFile, "UTF-8");
				
				//Create the file Header.
				writer.println(
						"NumberDevice"            + " " +  "UserId" 			   + " " + 
						"Block"                   + " " + "NumberClicks" 		   + " " + 
						"NumberCircles"           + " " + "DistanceCenter" 		   + " " +
						"PixelStartCircleX"       + " " + "PixelStartCircleY" 	   + " " +
						"PixelEndCircleX"         + " " + "PixelEndCircleY"   	   + " " +  
						"DistanceStartEndCircles" + " " + "TargerWidth" 		   + " " +
						"ElapsedTime"             + " " + "DistanceFirstLastPixel" + " " +
						"MouseX"                  + " " + "MouseY"
				);
				
				//Fill the collumns with the respective information
				Vector<Pixel> traversedPath = values.getPath();
				
				for(int w = 0; w < traversedPath.size(); w++)
				{
					Pixel temp = values.getStartingCircleCenter();
					int startingCircleX = temp.getXCoordinate();
					int startingCircleY = temp.getYCoordinate();
					
					temp = values.getEndingCircleCenter();
					int endingCircleX = temp.getXCoordinate();
					int endingCircleY = temp.getYCoordinate();
					
					double distanceBetweenFirstLastPixel = calculateDistanceBetweenPoints(traversedPath.firstElement(), traversedPath.lastElement());
					int mousePositionX = traversedPath.get(w).getXCoordinate();
					int mousePositionY = traversedPath.get(w).getYCoordinate();
					
					writer.println(
						values.getDeviceID() 				+ " " + values.getUserID() + " " + 
						values.blockNumber 					+ " " + values.getNumberOfClicks() + " " +
						values.getNumberOfCircles() 		+ " " + values.getDistanceBetweenFrameAndCircleCenter() + " " +
						startingCircleX 					+ " " + startingCircleY + " " +
						endingCircleX 						+ " " + endingCircleY + " " +
						values.getDistanceBetweenCircles()  + " " + values.getTargetWidth() + " " +
						values.getElapsedTime() 			+ " " + distanceBetweenFirstLastPixel  + " " +
						mousePositionX 						+ " " + mousePositionY
					);
				}
				
				writer.close();
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			} 
			catch (UnsupportedEncodingException e) 
			{
				e.printStackTrace();
			}
		}
		return;
	}
	
	
	
	
	
	
	
	/**
	 * Auxiliary function that writes all the values contained in the parameter
	 * to a text file.
	 * 
	 * @param trialResults - Vector containing the Information to be written. 
	 */
	/*public static void storeInformationInFile(Vector<Information> trialResults)
	{
		//Create folder where all results will be stored.
		String nameMainFolder = "../Resultados";
		File mainFolder = new File(nameMainFolder);
		
		//Check to see if this directory already exists
		if ( !mainFolder.exists() ) 
		{
			mainFolder.mkdir();
		}
		
		//Create another folder, inside the last one, where the results of trial will be stored
		DateFormat dateHourFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
		Date dateHour = new Date();
		
		String nameNewFolder = nameMainFolder+ "/" + dateHourFormat.format(dateHour).toString();
		File newFolder = new File(nameNewFolder);
		
		//Check if directory already exists, which is unlikely ...
		if ( !newFolder.exists() ) 
		{
			newFolder.mkdir();
		}
		
		//Write the results from each trial
		for(int i = 0; i < trialResults.size(); i++)
		{
			Information values = trialResults.get(i);
			
			String nameResultingFile = nameNewFolder + "/trial" + i + ".txt";
			File resultingFile = new File(nameResultingFile);
			
			if ( !resultingFile.exists() ) 
			{
				try 
				{
					resultingFile.createNewFile();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
					System.err.println("It was not possible to store the intended information. Error on function \"storeInformationInFile\".");
					return;
				}
			}

			try 
			{
				PrintWriter writer;
				writer = new PrintWriter(nameResultingFile, "UTF-8");
				
				writer.println("NDevice: " + values.getDeviceID() );
				writer.println("UserId: " + values.getUserID() );
				writer.println("Block: " + values.blockNumber );
				writer.println("NClicks: " + values.getNumberOfClicks() );
				writer.println("NCircles: " + values.getNumberOfCircles() );
				writer.println("DistCenter: " + values.getDistanceBetweenFrameAndCircleCenter() );
				writer.println("DistCenter: " + values.getDistanceBetweenFrameAndCircleCenter() );
				
				Pixel temp = values.getStartingCircleCenter();
				writer.println("PixelBeginCircle: " + temp.getXCoordinate() + " " + temp.getYCoordinate() );
				
				temp = values.getEndingCircleCenter();
				writer.println("PixelEndCircle: " + temp.getXCoordinate() + " " + temp.getYCoordinate() );
				
				writer.println("DistCircles: " + values.getDistanceBetweenCircles() );
				writer.println("TargerWidth(Pixels): " + values.getTargetWidth() );
				writer.println("ElapsedTime: " +  values.getElapsedTime() );
				
				Vector<Pixel> traversedPath = values.getPath();
				writer.println("DistanceFirstLastPixel: " + calculateDistanceBetweenPoints(traversedPath.firstElement(), traversedPath.lastElement()) );
				
				writer.println("Path: ");
				for(int w = 0; w < traversedPath.size(); w++)
				{
					writer.println("<" + traversedPath.get(w).getXCoordinate() + "," + traversedPath.get(w).getYCoordinate() + ">");
				}
				
				writer.close();
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			} 
			catch (UnsupportedEncodingException e) 
			{
				e.printStackTrace();
			}
		}
		return;
	}*/
}
