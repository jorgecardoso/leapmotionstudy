package other;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound 
{
	static private String sucessSound =  "../Resources/sound1.wav";		
	static private String failureSound = "../Resources/sound2.wav";
	
	private static boolean playSucessSound = true;
	private static boolean playFailureSound = true;
	
	private static Timer timer = new Timer();
	
	/**
	 * Function that plays the tune defined as the "Sucess sound".
	 * If this function is called again in less 200 milliseconds, no sound will be played.
	 */
	public static void playSucessSound()
	{
		//To avoid repeating the sound if the user continues to press the left mouse button or repeats the gesture too fast.  
		if(!playSucessSound)
		{return;}
		
		try
		{
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream( new File(sucessSound).getAbsoluteFile() );
			
			final Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);	
	        clip.start();
	        
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
	 * If this function is called again in less 200 milliseconds, no sound will be played.
	 */
	public static void playFailureSound()
	{
		//To avoid repeating the sound if the user continues to press the left mouse button or repeats the gesture too fast.  
		if(!playFailureSound)
		{return;}
		
		try
		{
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream( new File(failureSound).getAbsoluteFile() );
			
			final Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.start();
	        
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
	 * Function that prepares a default timer task that allows the "Failure sound" to be played again.
	 * Since the result is a TimerTask, it must be scheduled on a "Timer" in order for it to take effect. 
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

	/**
	 * Function that prepares a default timer task that allows the "Sucess sound" to be played again.
	 * Since the result is a TimerTask, it must be scheduled on a "Timer" in order for it to take effect. 
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
}
