package other;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound 
{
	//Path to the music files to be played when a sucess or error occurs. 
	static private String sucessSound =  "Resources/sound1.wav";		
	static private String failureSound = "Resources/sound2.wav";
	
	//Booleans used to control the frequency in which the sound is played.
	private static boolean playSucessSound = true;
	private static boolean playFailureSound = true;
	
	private static Timer timer = new Timer();
	
	/**
	 * Function that plays the tune defined as the "Sucess sound".
	 * <br>If this function is called again in less 200 milliseconds, no sound will be played.
	 */
	public static void playSucessSound()
	{
		//Avoid repeating the same tune if clicks are performed too close to each other.  
		if(!playSucessSound)
		{return;}
		
		try
		{
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream( new File(sucessSound).getAbsoluteFile() );
			
			final Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);	
	        clip.start();
	        
	        //TimerTask responsible for allowing the sound to be played again.
	        timer.schedule( actionPlaySucessSound(), 200);
	
	        playSucessSound = false;
		}
		catch(Exception e)
		{
			System.err.println("Error trying to play \"Sucess sound\".");
	        e.printStackTrace();
		}
	}
	
	/**
	 * Function that plays the tune defined as the "Failure sound".
	 * <br>If this function is called again in less 200 milliseconds, no sound will be played.
	 */
	public static void playFailureSound()
	{
		//Avoid repeating the same tune if clicks are performed too close to each other.
		if(!playFailureSound)
		{return;}
		
		try
		{
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream( new File(failureSound).getAbsoluteFile() );
			
			final Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.start();
	        
	        //TimerTask responsible for allowing the sound to be played again.
	        timer.schedule( actionPlayFailureSound(), 200);
	        
	        playFailureSound = false;
		}
		catch(Exception e)
		{
			System.err.println("Error playing \"Failure sound\".");
	        e.printStackTrace();
		}
	}
	
	/**
	 * Function that prepares a default timer task that allows the "Sucess sound" to be played again.
	 * <br>Since the result is a TimerTask, it must be scheduled on a "Timer" in order for it to take effect. 
	 * 
	 * @return TimerTask allowing the repetition of the "Sucess sound".
	 */
	private static TimerTask actionPlaySucessSound() 
	{
		return new TimerTask() 
		{ 
			public void run() 
			{playSucessSound = true;}
		};
	}
	
	/**
	 * Function that prepares a default timer task that allows the "Failure sound" to be played again.
	 * <br>Since the result is a TimerTask, it must be scheduled on a "Timer" in order for it to take effect. 
	 * 
	 * @return TimerTask allowing the repetition of the "Failure sound".
	 */
	private static TimerTask actionPlayFailureSound() 
	{
		return new TimerTask() 
	    { 
	    	public void run() 
	    	{ playFailureSound = true; }
	    };
	}
}
