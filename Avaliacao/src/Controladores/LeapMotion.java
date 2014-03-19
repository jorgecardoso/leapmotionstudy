package Controladores;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.Timer;
import java.util.TimerTask;

import com.leapmotion.leap.*;

/***
 * Tempo: 55
 * @author manuelcesar
 *
 */

public class LeapMotion extends Listener 
{
	private enum modoDeControlo{MaoSemGesto, MaosComGestoKeytap, MaoComGestoScreenTap};
	
	private boolean destro = false;
	private Hand maoDominante = new Hand();
	private Hand maoAuxiliar = new Hand();
	
	private modoDeControlo formaDeControlo = modoDeControlo.MaosComGestoKeytap;
	
	private int posicaoCursorX = 0;
	private int posicaoCursorY = 0;
	private double distanciaZonaDeToque = 0;
	private boolean botaoPressionado = false;
	
	int teste = 0;
	Robot cursor;
	
	public LeapMotion()
	{
		//////<<<<<<<-----------------------------------------------******
		try 
		{
			cursor = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//////<<<<<<<-----------------------------------------------******

		Controller controlador = new Controller();
		controlador.addListener(this);	
		
		//////<<<<<<<-----------------------------------------------******
        while(true)
        {}
		//////<<<<<<<-----------------------------------------------******

	}
	
	public void onInit(Controller controller)
	{	
		System.out.println("Leap Motion inicializado.");
	}
	
	public void onConnect(Controller controller)
	{
		System.out.println("Leap Motion ligado!");
	}
	
	public void onDisconnect(Controller controller)
	{
		System.out.println("Leap Motion desligado...");
	}
	
	public void onExit(Controller controller)
	{
		System.out.println("A sair...");
		controller.removeListener(this);
		controller.delete();
	}
	
	public void onFrame(Controller controller)
	{		
		Frame imagemCaptada = controller.frame();
		
		if(!imagemCaptada.isValid())
		{
			System.out.println("Imagem captada inadequada!");
			return;
		}
		
		HandList maos = imagemCaptada.hands();
		
		if(maos.isEmpty())
		{
			System.out.println("Nao foi detectada nenhuma m‹o.");
			return;
		}
		
		if(maos.count() == 1)
		{
			System.out.println("Uma m‹o detectada.");
			
			maoDominante = maos.get(0);
			
			if(!maoDominante.isValid())
			{
				System.out.println("M‹o dominante detectada invalida...");
				return;
			}
		}
		else if(maos.count() == 2)
		{
			System.out.println("Duas m‹os detectadas.");
			
			if( ( !maos.get(0).isValid() ) || ( !maos.get(1).isValid() ) )
			{ 
				System.out.println("Uma das m‹os detectadas n‹o Ž valida.");
				return; 
			}
			
			//Descobrir qual a m‹o esquerda e qual a m‹o direita
			Hand maoEsquerda = maos.leftmost();
			Hand maoDireita = maos.rightmost();
			
			//Conforme as preferências do utilizador, definir qual a mao dominante e auxiliar
			if(maoEsquerda.equals(maoDireita))
			{
				System.out.println("M‹o esquerda Ž igual a m‹o direita!!");
				return;
			}
			
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
		}
		else
		{
			//Ser‡ o dispositivo capaz de captar tanta m‹o??
			System.out.println("Foram detectadas trs m‹os. S— podem ser usadas uma ou duas m‹os.");
			return;
		}
		
		System.out.println(maoAuxiliar.fingers().count());
		
		//Tomar respectiva acção conforme o modo de controlo
		if(formaDeControlo == modoDeControlo.MaoComGestoScreenTap)
		{ controlarComDedo(controller); }
		else if(formaDeControlo == modoDeControlo.MaosComGestoKeytap)
		{ controlarComUmaMaoMaisGestoKeyTap(controller); }
		else if(formaDeControlo == modoDeControlo.MaoSemGesto)
		{ tipoControlo1(controller); }
	}
	
	//Um dedo da m\ao dominante mais aproximado do ecr‹ (o mais distante do centro da m‹o) Ž utilizado para simular o movimento 
	//do cursor e um clique do rato esquerdo.
	//S— necessita de uma m‹o.
	public void tipoControlo1(Controller controlador)
	{
		ScreenList ecras = controlador.locatedScreens();
		
		if(ecras.isEmpty())
		{
			System.out.println("N‹o foi detectado nenhum ecr‹.");
			return;
		}
		
		Pointable dedoApontador = maoDominante.pointables().frontmost();
		
		if(!dedoApontador.isValid())
		{
			System.out.println("Dedo apontador invalido!");
			return;
		}
		
		Screen ecra = ecras.get(0);
		
		Vector interseccao = ecra.intersect(dedoApontador, true, 1.0f);
		
		posicaoCursorX = (int) ( ecra.widthPixels() * interseccao.getX() );
		posicaoCursorY = (int) ( ecra.heightPixels() * ( 1.0f - interseccao.getY() ) );
		distanciaZonaDeToque = dedoApontador.touchDistance();
		
		//////<<<<<<<-----------------------------------------------******
		cursor.mouseMove(posicaoCursorX, posicaoCursorY);
		//////<<<<<<<-----------------------------------------------******
		
		if(distanciaZonaDeToque < 0.0)
		{
			System.out.println("Bot‹o carregado.");
			botaoPressionado = true;
			//////<<<<<<<-----------------------------------------------******
			cursor.mousePress(InputEvent.BUTTON1_MASK);
			//////<<<<<<<-----------------------------------------------******
		}
		else
		{
			System.out.println("Bot‹o liberto.");
			botaoPressionado = false;
			//////<<<<<<<-----------------------------------------------******
			cursor.mouseRelease(InputEvent.BUTTON1_MASK);
			//////<<<<<<<-----------------------------------------------******
		}
	}
	
	//Um dedo da m‹o dominante mais aproximado do ecr‹ (o mais distante do centro da m‹o) Ž utilizado para simular o movimento do cursor.
	//Um dedo da m‹o auxiliar mais pr—ximo do ecr‹ executa o gesto KEYTAP (deslocamento  de um dedo na vertical, rapidamente) simula um clique do bot‹o esquerdo do rato.
	//S‹o necess‡rias as duas m‹os.
	public void controlarComUmaMaoMaisGestoKeyTap(Controller controlador)
	{
		
	}
	
	//Um dedo da mão dominante controla o cursor e faz a seleção.
	//Só é necessária uma mão
	public void controlarComDedo(Controller controlador)
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
	}
	
	public int getPosicaoCursorX()
	{ return this.posicaoCursorX; }
	
	public int getPosicaoCursorY()
	{ return this.posicaoCursorY; }
	
	public boolean getBotaoPressionado()
	{ return this.botaoPressionado;	}
}
