package EstruturaDados;

public class Pixel 
{
	private int coordenadaX;
	private int coordenadaY;
	
	public Pixel(int x, int y)
	{
		this.coordenadaX = x;
		this.coordenadaY = y;
	}
	
	public int getCoordenadaX() 
	{ return coordenadaX; }
	
	public void setCoordenadaX(int coordenadaX) 
	{ this.coordenadaX = coordenadaX; }
	
	public int getCoordenadaY() 
	{ return coordenadaY; }
	
	public void setCoordenadaY(int coordenadaY) 
	{ this.coordenadaY = coordenadaY; }
}
