package Controladores;

import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.Type;


public class LeapMotion extends Listener 
{
	private boolean destro = true;
	private Hand maoDominante = new Hand();
	private Hand maoAuxiliar = new Hand();
	
	private enum modoDeControlo{MAO, DEDO, DEDOS};
	private enum mapeamentoDoCursor{ DIRECTO, INTERSESSAO};
	
	modoDeControlo formaDeControlo = modoDeControlo.MAO;
	mapeamentoDoCursor mapeamento = mapeamentoDoCursor.DIRECTO; 
	
	int posicaoCursorX;
	float posicaoCursorY;
	
	int teste = 0;
	
	public LeapMotion()
	{
		Controller controlador = new Controller();
		controlador.addListener(this);	
	}
	
	public void onInit(Controller controller)
	{	
	}
	
	public void onConnect(Controller controller)
	{	
	}
	
	public void onDisconnect(Controller controller)
	{}
	
	public void onExit(Controller controller)
	{}
	
	public void onFrame(Controller controller)
	{		
		Frame imagemCaptada = controller.frame();
		
		if(!imagemCaptada.isValid())
		{return;}
		
		HandList maos = imagemCaptada.hands();
		
		if(maos.isEmpty())
		{return;}
		
		if(maos.count() == 1)
		{
			System.out.println("Entrou dentro de uma m�o.");
			
			maoDominante = maos.get(0);
			
			if(!maoDominante.isValid())
			{return;}
			
			System.out.println("Entrou dentro de uma m�o COMPLETO.");
			
		}
		else if(maos.count() == 2)
		{
			System.out.println("Entrou dentro de 2 m�o.");
			
			if( ( !maos.get(0).isValid() ) || ( !maos.get(1).isValid() ) )
			{ return; }
			
			//Descobrir qual a m�o esquerda e a m�o direita
			Hand maoEsquerda = maos.leftmost();
			Hand maoDireita = maos.rightmost();
			
			//Conforme as prefer�ncias do utilizador, definir qual a mao dominante e auxiliar
			if(maoEsquerda.equals(maoDireita))
			{return;}
			
			if(destro)
			{
				maoDominante = maoDireita;
				maoAuxiliar = maoEsquerda;
			}
			else
			{
				maoDominante = maoEsquerda;
				maoAuxiliar = maoDireita;
			}
			
			
			System.out.println("Entrou dentro de 2 m�o COMPLETO.");
		}
		else
		{
			//Ser� o dispositivo capaz de captar tanta m�o??
			return;
		}
		
		System.out.println(maoAuxiliar.fingers().count());
		
		//Tomar respectiva ac��o conforme o modo de controlo
		if(formaDeControlo == modoDeControlo.DEDO && !maoAuxiliar.equals(null))
		{ controlarComDedo(); }
		else if(formaDeControlo == modoDeControlo.DEDOS)
		{ controlarComDedos(); }
		else if(formaDeControlo == modoDeControlo.MAO)
		{ controlarComMao(); }
		
	}
	
	//A m�o dominante � utilizada para controlar o cursor e um dedo mao da mao auxiliar confirma a sele��o.
	//S�o necess�rias as duas m�os.
	public void controlarComMao()
	{
		//Vamos assumir que o dedo com que o utilizador � o que estiver mais a frente.
		//Escolher o que esta mais avan�ado resolver� conflitos quando o dispositivo detecta mais dedos do que os pretendidos
		Pointable dedoAPontador = maoAuxiliar.pointables().frontmost();
		
		Vector valor = maoDominante.palmPosition();
		posicaoCursorX = (int) valor.getX();
		posicaoCursorY = valor.getY();
		
		System.out.println(posicaoCursorX + " " + posicaoCursorY);
		System.out.println(teste++);
	}
	
	//Um dedo da m�o dominante controla o cursor e um dedo da m�o auxiliar faz a sele��o.
	//S�o necess�rias as duas m�os
	public void controlarComDedos()
	{
		
	}
	
	//Um dedo da m�o dominante controla o cursor e faz a sele��o.
	//S� � necess�ria uma m�o
	public void controlarComDedo()
	{
		
	}
	
	public void mapearCoordenadasDirectamente()
	{
		
	}
	
	public void mapearCoordenedasComIntersessao()
	{
		
	}
	
	public static void main(String[] args) 
	{
		LeapMotion lm = new LeapMotion();
		while(true)
		{}
	}
}
