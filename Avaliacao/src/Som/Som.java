package Som;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;

public class Som 
{
	static private String somSucesso =  "../Recursos/Som1.wav";
	static private String somFracasso = "../Recursos/Som2.wav";
	
	public static void tocarSomSucesso()
	{
		try
		{
			AudioInputStream audioInputStream = 
	        	AudioSystem.getAudioInputStream(
	        		new File(somSucesso).getAbsoluteFile()
	        	);
			
			final Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.start();
	        
	        clip.addLineListener(new LineListener() {
				@Override
				public void update(LineEvent event) 
				{
					if (event.getType() != Type.STOP) 
					{ clip.close(); }
		        }
			});
		}
		catch(Exception e)
		{
			System.out.println("Erro ao tentar tocar \"Som Sucesso\".");
	        e.printStackTrace();
		}
	}
	
	public static void tocarSomFracasso()
	{
		try
		{
			AudioInputStream audioInputStream = 
	        	AudioSystem.getAudioInputStream(
	        		new File(somFracasso).getAbsoluteFile()
	        	);
			
			final Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.start();
	        
	        clip.addLineListener(new LineListener() {
				@Override
				public void update(LineEvent event) 
				{
					if (event.getType() != Type.STOP) 
					{ clip.close(); }
		        }
			});
		}
		catch(Exception e)
		{
			System.out.println("Erro ao tentar tocar \"Som Fracasso\".");
	        e.printStackTrace();
		}
	}
}
