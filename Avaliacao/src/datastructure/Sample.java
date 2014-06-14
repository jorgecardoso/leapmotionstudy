package datastructure;

public class Sample 
{
	//This class is used to store the pointer's position and if a click as happened.
	Pixel readPosition;
	boolean clickedHappened = false;

	/**
	 * Sample class constructor without arguments.
	 */
	public Sample()
	{}
	
	/**
	 * Sample class constructor with one argument.
	 * 
	 * @param position - Pixel of the position read.
	 */
	public Sample(Pixel position)
	{
		this.readPosition = position;
	}
	
	/**
	 * Sample class constructor with two arguments.
	 * <br>An alternative to the constructor with one argument. 
	 * <br>In this, instead of a Pixel, containing the position coordinates, the X and Y coordinates are passed as arguments.
	 * 
	 * @param coordinateX - The read position, X (width) coordinate.
	 * @param coordinateY - The read position, Y (height) coordinate.
	 */
	public Sample(int coordinateX, int coordinateY)
	{
		this.readPosition = new Pixel(coordinateX, coordinateY);
	}
	
	/**
	 * Function that returns the coordinates of stored in the sample.
	 * 
	 * @return Pixel, containing the X and Y coordinates.
	 */
	public Pixel getPixel()
	{
		return this.readPosition;
	}
	
	/**
	 * Function that alters the position stored.
	 * 
	 * @param position - Pixel with X and Y coordinates.
	 */
	public void setPixel(Pixel position)
	{
		this.readPosition = position;
	}
	
	/**
	 * Function that informs if a click had occurred when this sample was saved.
	 * 
	 * @return A boolean with the answer. 
	 */
	public boolean didClickHappen()
	{
		return this.clickedHappened;
	}
	
	/**
	 * Function that informs this class that a click happened.
	 */
	public void clickHappened()
	{
		this.clickedHappened = true;
	}
}
