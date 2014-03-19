package Controladores;

import com.leapmotion.leap.*;


public class LeapMotion extends Listener 
{
	public LeapMotion()
	{
		Controller controlador = new Controller();
		controlador.addListener(this);	
	}
	
	public void onInit(Controller controller)
	{}
	
	public void onConnect(Controller controller)
	{}
	
	public void onDisconnect(Controller controller)
	{}
	
	public void onExit(Controller controller)
	{}
	
	public void onFrame(Controller controller)
	{
		System.out.println("Passou aqui");
	}
	
	public static void main(String[] args) 
	{
		LeapMotion lm = new LeapMotion();
		while(true)
		{}
	}
}
