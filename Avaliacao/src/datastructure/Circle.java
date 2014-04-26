package datastructure;

public class Circle
{
	private int centerX;
	private int centerY;
	private int radius;
	
	/**
	 * Constructor of the Circle class.
	 * This class stores the center coordinates and radius of a circle.
	 * 
	 * @param circleCenterCoordinatesX - The X axis coordinate of the circle's center. 
	 * @param circleCenterCoordinatesY - The Y axis coordinate of the circle's center. 
	 * @param radius - The circle's radius. 
	 */
	public Circle(int circleCenterCoordinatesX, int circleCenterCoordinatesY, int circleRadius)
	{
		centerX = circleCenterCoordinatesX;
		centerY = circleCenterCoordinatesY;
		radius = circleRadius;
	}

	/**
	 * Function that returns the circle's center X coordinate.
	 *
	 * @return The X axis coordinate of the circle's center.
	 */
	public int getCenterX() 
	{ return centerX; }

	/**
	 * Function that alters the circle's center X coordinate.
	 * 
	 * @param centerX - The circle's center new X coordinate.
	 */
	public void setCenterX(int centerX) 
	{ this.centerX = centerX; }

	/**
	 * Function that returns the circle's center Y coordinate.
	 * 
	 * @return The Y axis coordinate of the circle's center.
	 */
	public int getCenterY() 
	{ return centerY; }

	/**
	 * Function that alters the circle's center Y coordinate.
	 * 
	 * @param centerY - The circle's center new Y coordinate.
	 */
	public void setCenterY(int centerY) 
	{ this.centerY = centerY; }

	/**
	 * Function that returns the circle's radius.
	 * 
	 * @return The circle's radius
	 */
	public int getRadius() 
	{ return radius; }

	/**
	 * Function that alters the circle's radius.
	 * 
	 * @param circleRadius - The intended new circle's radius.
	 */
	public void setRadius(int circleRadius) 
	{ this.radius = circleRadius; }
	
	/**
	 * Function that returns the circle's center coordinates formatted according to the Pixel class
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
}
