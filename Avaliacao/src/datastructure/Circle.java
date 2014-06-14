package datastructure;

public class Circle
{
	private int centerX;
	private int centerY;
	private int radius;
	
	/**
	 * Constructor of the Circle class.
	 * This class stores the circle's center coordinates and radius.
	 * 
	 * @param circleCenterCoordinatesX - The X (width) axis coordinate of the circle's center. 
	 * @param circleCenterCoordinatesY - The Y (height) axis coordinate of the circle's center. 
	 * @param radius - The circle's radius. 
	 */
	public Circle(int circleCenterCoordinatesX, int circleCenterCoordinatesY, int circleRadius)
	{
		centerX = circleCenterCoordinatesX;
		centerY = circleCenterCoordinatesY;
		radius = circleRadius;
	}

	/**
	 * Function that returns the circle's center X (width) coordinate.
	 *
	 * @return The X (width) axis coordinate of the circle's center.
	 */
	public int getCenterX() 
	{ return centerX; }

	/**
	 * Function that alters the circle's center X (width) coordinate.
	 * 
	 * @param centerX - The circle's center new X (width) coordinate.
	 */
	public void setCenterX(int centerX) 
	{ this.centerX = centerX; }

	/**
	 * Function that returns the circle's center Y (height) coordinate.
	 * 
	 * @return The Y (height) axis coordinate of the circle's center.
	 */
	public int getCenterY() 
	{ return centerY; }

	/**
	 * Function that alters the circle's center Y (height) coordinate.
	 * 
	 * @param centerY - The circle's center new Y (height) coordinate.
	 */
	public void setCenterY(int centerY) 
	{ this.centerY = centerY; }

	/**
	 * Function that returns the circle radius.
	 * 
	 * @return The circle radius
	 */
	public int getRadius() 
	{ return radius; }

	/**
	 * Function that alters the circle radius.
	 * 
	 * @param circleRadius - The intended new circle radius.
	 */
	public void setRadius(int circleRadius) 
	{ this.radius = circleRadius; }
	
	/**
	 * Function that returns the circle center coordinates formatted according to the Pixel class
	 * 
	 * @return Pixel with circle's center coordinates
	 */
	public Pixel getCenterPixel()
	{
		return new Pixel(centerX, centerY);
	}
	
	/**
	 * Function that determines if a point belongs (is inside) to the circle.
	 * To belong to the circle, the point must either be inside or coincide with the circle's circumference. 
	 * 
	 * @param pointCoordinatesX - The X coordinate of the point.
	 * @param pointCoordinatesY - The Y coordinate of the point.
	 * 
	 * @return true/false
	 */
	public boolean doesPointBelongToCircle(int pointCoordinatesX, int pointCoordinatesY)
	{
		//Circumference's formula:
		//  (x - Cx)^2 + (y - Cy)^2 = radius^2
		
		double part1 = Math.pow( (double) (pointCoordinatesX - centerX) , 2.0);
		double part2 = Math.pow( (double) (pointCoordinatesY - centerY) , 2.0);
		double part3 = Math.pow( (double) radius, 2.0);        
		
		part1 = part1 + part2;
		
		if( part1 <= part3)
		{ return true; }
		else
		{ return false; }
	}
	
	/**
	 * Function that determines if a pixel belongs (is inside) to the circle.
	 * <br>To belong to the circle, the pixel must either be inside or coincide with the circle's circumference. 
	 * 
	 * @param pointCoordinatesX - The X coordinate of the point.
	 * @param pointCoordinatesY - The Y coordinate of the point.
	 * 
	 * @return true/false
	 */
	public boolean doesPointBelongToCircle(Pixel pixel)
	{
		return doesPointBelongToCircle(pixel.getXCoordinate(), pixel.getYCoordinate());
	}
}
