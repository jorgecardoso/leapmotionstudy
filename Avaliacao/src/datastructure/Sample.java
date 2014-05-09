package datastructure;

public class Sample 
{
	Pixel readPosition;
	boolean clickedHappened = false;

	/**
	 * Sample class default constructor without arguments.
	 */
	public Sample()
	{}
	
	/**
	 * Sample class default constructor with one argument.
	 * 
	 * @param position - Pixel of the position read.
	 */
	public Sample(Pixel position)
	{
		this.readPosition = position;
	}
	
	/**
	 * Sample class default constructor with two arguments.
	 * An alternative to the default constructor with one argument. 
	 * In this, instead of a Pixel, containing the position coordinates, the X and Y coordinates are passed as arguments.
	 * 
	 * @param coordinateX - The read position X coordinate.
	 * @param coordinateY - The read position X coordinate.
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
	 * @return A boolean with true or false.
	 */
	public boolean didClickHappen()
	{
		return this.clickedHappened;
	}
	
	/**
	 * Function that that sets the sample clickHappened to true.
	 * 
	 */
	public void clickHappened()
	{
		this.clickedHappened = true;
	}
}
