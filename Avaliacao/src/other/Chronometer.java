package other;

//The following code was based on: http://silveiraneto.net/2008/03/15/simple-java-chronometer/
public class Chronometer 
{
	private long startTime;
	private long endTime;
	boolean stopped;
	
	/**
	 * Constructor of the class Chronometer. 
	 * As the name implies, it creates a Chronometer.
	 * 
	 * Note: In order to start the chronometer the "start" function should be used.
	 * 		 otherwise, the time returned will be 0.
	 * 
	 * Note2: In order to reset the chronometer, the function "reset" should be used.
	 */
	public Chronometer()
	{
		reset();
	}
	
	/**
	 * Function that starts the chronometer.
	 * 
	 * Note: This function must be used in order to start the chronometer, otherwise the value obtained 
	 * 		 when the time is requested will always be 0.
	 */
	public void start()
	{
		startTime = System.currentTimeMillis();
		stopped = false;
	}
	
	/**
	 * Function that stops the chronometer.
	 * if the chronometer hasn't been started, nothing will happen.
	 */
	public void stop()
	{
		// If the chronometer hasn't been started yet, there's no need to do anything.
		if(stopped)
		{ return; }
		
		endTime = System.currentTimeMillis();
		stopped = true;
	}
	
	/**
	 * Function that returns the ellapsed time in milliseconds.
	 * This function also stops the chronometer in case it was still running.
	 * 
	 * @return A Long value with the time elapsed, in milliseconds.
	 */
	public long getTimeInMilliseconds()
	{
		if(!stopped)
		{stop();}
		
		return endTime - startTime;
	}
	
	/**
	 * Function that returns the elapsed time in seconds.
	 * This function also stops the chronometer in case it was still running.
	 * 
	 * @return A Long value with the time elapsed, in seconds.
	 */
	public long getTimeInSeconds()
	{
		return ( getTimeInMilliseconds() ) / 1000;
	}
	
	/**
	 * Function that resets the chronometer.
	 */
	public void reset()
	{
		startTime = 0;
		endTime = 0;
		stopped = true;
	}
}