package Som;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

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
	private static boolean tocarSomSucesso = true;
	private static boolean tocarSomFracasso = true;
	private static Timer temporizador = new Timer();
	
	public static void tocarSomSucesso()
	{
		//Para evitar a repetição do som caso o utilizador repita o gesto ou o botão pressionado
		if(!tocarSomSucesso)
		{return;}
		
		try
		{
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream( new File(somSucesso).getAbsoluteFile() );
			
			final Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);	
	        clip.start();
	        
	        temporizador.schedule( accaoTocarSomSucesso(), 200);
	
	        tocarSomSucesso = false;
		}
		catch(Exception e)
		{
			System.out.println("Erro ao tentar tocar \"Som Sucesso\".");
	        e.printStackTrace();
		}
	}
	
	public static void tocarSomFracasso()
	{
		//Para evitar a repetição do som caso o utilizador repita o gesto ou o botão pressionado
		if(!tocarSomFracasso)
		{return;}
		
		try
		{
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream( new File(somFracasso).getAbsoluteFile() );
			
			final Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.start();
	        
	        temporizador.schedule( accaoTocarSomFracasso(), 200);
	        
	        tocarSomFracasso = false;
		}
		catch(Exception e)
		{
			System.out.println("Erro ao tentar tocar \"Som Fracasso\".");
	        e.printStackTrace();
		}
	}
	
	private static TimerTask accaoTocarSomFracasso() 
	{
		return new TimerTask() 
	    { 
	    	public void run() 
	    	{ tocarSomFracasso = true; }
	    };
	}

	private static TimerTask accaoTocarSomSucesso() 
	{
		return new TimerTask() 
		{ 
			public void run() 
			{tocarSomSucesso = true;}
		};
	}
}
