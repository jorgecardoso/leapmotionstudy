package datastructure;

public class Pixel 
{
	private int xCoordinate;
	private int yCoordinate;
	
	/**
	 * Constructor of the Pixel class.
	 * Creates a class that stores a pixel's X and Y position on the screen.
	 * 
	 * Note: Point (0,0) is located in the upper left corner of the screen.
	 * 
	 * @param x - The pixel's X coordinate in the screen.
	 * @param y - The pixel's Y coordinate in the screen.
	 */
	public Pixel(int x, int y)
	{
		this.xCoordinate = x;
		this.yCoordinate = y;
	}
	
	/**
	 * Function that returns the Pixel's X position on the screen.
	 * 
	 * @return Pixel's X position.
	 */
	public int getXCoordinate() 
	{ return xCoordinate; }
	
	/**
	 * Function that alters the X position of the Pixel.
	 * 
	 * @param xCoordinate - The new X coordinate of the pixel on the screen.
	 */
	public void setXCoordinate(int xCoordinate) 
	{ this.xCoordinate = xCoordinate; }
	
	/**
	 * Function that returns the Pixel's Y position on the screen.
	 * 
	 * @return Pixel's Y position.
	 */
	public int getYCoordinate() 
	{ return yCoordinate; }
	
	/**
	 * Function that alters the Y position of the Pixel.
	 * 
	 * @param yCoordinate - The new Y coordinate of the pixel on the screen.
	 */
	public void setYCoordinate(int yCoordinate) 
	{ this.yCoordinate = yCoordinate; }
	
	/**
	 * Function that returns a string with the Pixel's coordinates.
	 * The result follows this format: "X_Coordinate Y_Coordinate".
	 * 
	 * @return String containing the Pixel's X and Y coordinates.
	 */
	public String toString()
	{
		return "" + xCoordinate + " " + yCoordinate + "";		 
	}
}
