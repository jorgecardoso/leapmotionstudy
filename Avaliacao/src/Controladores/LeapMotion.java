package Controladores;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.State;
import com.leapmotion.leap.Gesture.Type;

public class LeapMotion extends Listener
{
	public enum modoDeControlo{MaoSemGesto, MaosComGestoKeytap, MaoComGestoScreenTap, MaosComGestoSwipe};
	private modoDeControlo formaDeControlo;
	
	private boolean destro;
	private Hand maoDominante = new Hand();
	private Hand maoAuxiliar = new Hand();
	
	private int posicaoCursorX = 0;
	private int posicaoCursorY = 0;
	private double distanciaZonaDeToque = 0;
	private boolean botaoPressionado = false;
	
	//////<<<<<<<-----------------------------------------------******
	Robot cursor;
	//////<<<<<<<-----------------------------------------------******
	
	int teste = 0;
	final boolean debug = false;
	
	public LeapMotion(modoDeControlo modoControlo, boolean destro)
	{
		this.destro = destro;
		this.formaDeControlo = modoControlo;
	}
	
	public void inicializar()
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
		
		if(formaDeControlo == modoDeControlo.MaosComGestoKeytap)
		{
			//Extender o tempo de reconhecimento do gesto de forma a facilitar a sua execução.
			if(!controlador.config().setFloat("Gesture.KeyTap.HistorySeconds", 0.3f) )
			{
				if(debug){System.out.println("N‹o foi poss’vel as configura�oes de \"Gesture.KeyTap.HistorySeconds\"");}
				return;
			}
			controlador.config().save();
			
			controlador.enableGesture(Type.TYPE_KEY_TAP);
		}
		else if(formaDeControlo == modoDeControlo.MaoComGestoScreenTap)
		{
			//Extender o tempo de reconhecimento do gesto de forma a facilitar a sua execução.
			if(!controlador.config().setFloat("Gesture.ScreenTap.HistorySeconds", 0.3f) )
			{
				if(debug){System.out.println("N‹o foi poss’vel as configura�oes de \"Gesture.ScreenTap.HistorySeconds\"");}
				return;
			}
			
			controlador.enableGesture(Type.TYPE_SCREEN_TAP);
		}
		else if(formaDeControlo == modoDeControlo.MaosComGestoSwipe)
		{
			//Extender o tempo de reconhecimento do gesto de forma a facilitar a sua execução.
			if(!controlador.config().setFloat("Gesture.KeyTap.HistorySeconds", 0.3f) )
			{
				if(debug){System.out.println("N‹o foi poss’vel as configura�oes de \"Gesture.KeyTap.HistorySeconds\"");}
				return;
			}
			controlador.config().save();
			
			controlador.enableGesture(Type.TYPE_SWIPE);
		}
		
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
			if(debug){System.out.println("Imagem captada inadequada!");}
			return;
		}
		
		HandList maos = imagemCaptada.hands();
		
		if(maos.isEmpty())
		{
			if(debug) {System.out.println("Nao foi detectada nenhuma m‹o.");}
			return;
		}
		
		if(maos.count() == 1)
		{
			maoDominante = maos.get(0);
			
			if(!maoDominante.isValid())
			{
				if(debug){System.out.println("M‹o dominante detectada invalida...");}
				return;
			}
		}
		else if(maos.count() == 2)
		{
			if( ( !maos.get(0).isValid() ) || ( !maos.get(1).isValid() ) )
			{ 
				if(debug){System.out.println("Uma das m‹os detectadas n‹o Ž valida.");}
				return; 
			}
			
			//Descobrir qual a m‹o esquerda e qual a m‹o direita
			Hand maoEsquerda = maos.leftmost();
			Hand maoDireita = maos.rightmost();
			
			//Conforme as preferências do utilizador, definir qual a mao dominante e auxiliar
			if(maoEsquerda.equals(maoDireita))
			{
				if(debug){System.out.println("M‹o esquerda Ž igual a m‹o direita!!");}
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
			System.out.println("Foram detectadas tr�s m‹os. S— podem ser usadas uma ou duas m‹os.");
			return;
		}
		
		//Tomar respectiva ac�‹o conforme o modo de controlo
		if(formaDeControlo == modoDeControlo.MaoComGestoScreenTap)
		{ 
			tipoControlo3(controller); 
		}
		else if(formaDeControlo == modoDeControlo.MaosComGestoKeytap)
		{ 
			tipoControlo2(controller); 
		}
		else if(formaDeControlo == modoDeControlo.MaoSemGesto)
		{ 
			tipoControlo1(controller); 
		}
		else if(formaDeControlo == modoDeControlo.MaosComGestoSwipe)
		{
			tipoControlo4(controller);
		}
	}
	
	//Um dedo da m‹o dominante mais pr—ximo do ecr‹ (o mais distante do centro da m‹o) Ž utilizado para simular o movimento do cursor.
	//A m‹o auxiliar, n‹o importa qual o dedo, executa o gesto SWIPE (hum... chapada... r‡pida sobre o dispositivo) simulando um clique do bot‹o esquerdo do rato.
	//S‹o necess‡rias as duas m‹os.
	private void tipoControlo4(Controller controlador) 
	{
		ScreenList ecras = controlador.locatedScreens();

		if(ecras.isEmpty())
		{
			if(debug){System.out.println("N‹o foi detectado nenhum ecr‹.");}
			return;
		}

		Pointable dedoApontador = maoDominante.pointables().frontmost();

		if(!dedoApontador.isValid())
		{
			if(debug){System.out.println("Dedo apontador invalido!");}
			return;
		}

		Screen ecra = ecras.get(0);

		Vector interseccao = ecra.intersect(dedoApontador, true, 1.0f);

		posicaoCursorX = (int) ( ecra.widthPixels() * interseccao.getX() );
		posicaoCursorY = (int) ( ecra.heightPixels() * ( 1.0f - interseccao.getY() ) );

		//////<<<<<<<-----------------------------------------------******
		cursor.mouseMove(posicaoCursorX, posicaoCursorY);
		//////<<<<<<<-----------------------------------------------******
		
		Gesture gestoEfectuado = controlador.frame().gestures().get(0);
		
		if(!maoAuxiliar.isValid())
		{
			if(debug){System.out.println("Deve colocar a sua m‹o direita sobre o dispositivo.");}
			return;
		}
		
		if(!gestoEfectuado.isValid())
		{
			if(debug){System.out.println("O gesto efectuado n‹o Ž valido.");}
			return;
		}
		
		if(gestoEfectuado.hands().count() != 1)
		{
			if(debug){System.out.println("Mais que uma m‹o efectou o gesto SWIPE. Deve utilizar apenas a sua m‹o n‹o dominante.");}
			return;
		}
		
		if(!gestoEfectuado.hands().get(0).equals(maoAuxiliar))
		{
			if(debug){System.out.println("S— a m‹o auxiliar deve efectuar gestos. A m‹o dominante serve para controlar o cursor.");}
			return;
		}
		
		//O gesto swipe Ž continuo. Como Ž suposto ser s— um clique Ž necess‡rio verificar quando este termina.
		//Caso contr‡rio, ao longo da sua execu�‹o, ocorreriam varios cliques.
		if( !gestoEfectuado.state().equals(State.STATE_STOP) )
		{
			return;
		}
		
		//Se apenas um gesto estiver a ser reconhecido, este peda�o de c—digo pode ser removido
		if(!gestoEfectuado.type().equals(Type.TYPE_SWIPE))
		{
			//N‹o foi detectada o gesto pretendido.
			if(debug){System.out.println("O gesto efectuado n‹o foi do tipo SWIPE.");}
			return;
		}
		
		botaoPressionado = true;
		//////<<<<<<<---------------------------------------
		cursor.mousePress(InputEvent.BUTTON1_MASK);
		cursor.mouseRelease(InputEvent.BUTTON1_MASK);
		//////<<<<<<<---------------------------------------
	}

	//Um dedo da m‹o dominante mais pr—ximo do ecr‹ (o mais distante do centro da m‹o) Ž utilizado para simular o movimento do cursor e o clique do bot‹o esquerdo do rato.
	//ƒ apenas necessario uma m‹o.
	public void tipoControlo3(Controller controlador)
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
		
		//////<<<<<<<-----------------------------------------------******
		cursor.mouseMove(posicaoCursorX, posicaoCursorY);
		//////<<<<<<<-----------------------------------------------******
		
		Gesture gestoEfectuado = controlador.frame().gestures().get(0);
			
		if(!gestoEfectuado.isValid())
		{
			System.out.println("O gesto efectuado n‹o Ž valido.");
			return;
		}
		
		if(gestoEfectuado.hands().count() != 1)
		{
			System.out.println("Mais que uma m‹o efectou o gesto SCREEN_TAP. Deve utilizar apenas a sua m‹o dominante.");
			return;
		}
		
		if(!gestoEfectuado.hands().get(0).equals(maoDominante))
		{
			System.out.println("S— a m‹o dominante deve efectuar gestos.");
			return;
		}
		
		//Se apenas um gesto estiver a ser reconhecido, este peda�o de c—digo pode ser removido
		if(!gestoEfectuado.type().equals(Type.TYPE_SCREEN_TAP))
		{
			//N‹o foi detectada o gesto pretendido.
			if(debug){System.out.println("O gesto efectuado n‹o foi do tipo SCREEN_TAP.");}
			return;
		}
		
		botaoPressionado = true;
		//////<<<<<<<---------------------------------------
		cursor.mousePress(InputEvent.BUTTON1_MASK);
		cursor.mouseRelease(InputEvent.BUTTON1_MASK);
		//////<<<<<<<---------------------------------------	
	}

	//Um dedo da m‹o dominante mais pr—ximo do ecr‹ (o mais distante do centro da m‹o) Ž utilizado para simular o movimento do cursor.
	//Um dedo da m‹o auxiliar mais pr—ximo do ecr‹ executa o gesto KEYTAP (deslocamento  de um dedo na vertical, rapidamente) simulando um clique do bot‹o esquerdo do rato.
	//S‹o necess‡rias as duas m‹os.
	public void tipoControlo2(Controller controlador)
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
		
		//////<<<<<<<-----------------------------------------------******
		cursor.mouseMove(posicaoCursorX, posicaoCursorY);
		//////<<<<<<<-----------------------------------------------******
		
		Gesture gestoEfectuado = controlador.frame().gestures().get(0);
		
		if(!maoAuxiliar.isValid())
		{
			System.out.println("Deve colocar a sua m‹o direita sobre o dispositivo.");
			return;
		}
		
		if(!gestoEfectuado.isValid())
		{
			System.out.println("O gesto efectuado n‹o Ž valido.");
			return;
		}
		
		if(gestoEfectuado.hands().count() != 1)
		{
			System.out.println("Mais que uma m‹o efectou o gesto KEYTAP. Deve utilizar apenas a sua m‹o n‹o dominante.");
			return;
		}
		
		if(!gestoEfectuado.hands().get(0).equals(maoAuxiliar))
		{
			System.out.println("S— a m‹o auxiliar deve efectuar gestos. A m‹o dominante serve para controlar o cursor.");
			return;
		}
		
		//Se apenas um gesto estiver a ser reconhecido, este peda�o de c—digo pode ser removido
		if(!gestoEfectuado.type().equals(Type.TYPE_KEY_TAP))
		{
			//N‹o foi detectada o gesto pretendido.
			if(debug){System.out.println("O gesto efectuado n‹o foi do tipo KEYTAP.");}
			return;
		}
		
		botaoPressionado = true;
		//////<<<<<<<---------------------------------------
		cursor.mousePress(InputEvent.BUTTON1_MASK);
		cursor.mouseRelease(InputEvent.BUTTON1_MASK);
		//////<<<<<<<---------------------------------------
	}
	
	//Um dedo da m‹o dominante mais aproximado do ecr‹ (o mais distante do centro da m‹o) Ž utilizado para simular o movimento 
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
	
	public static void main(String[] args) 
	{
		@SuppressWarnings("unused")
		LeapMotion lm = new LeapMotion(modoDeControlo.MaosComGestoSwipe,true);
	}
	
	public int getPosicaoCursorX()
	{ return this.posicaoCursorX; }
	
	public int getPosicaoCursorY()
	{ return this.posicaoCursorY; }
	
	public boolean getBotaoPressionado()
	{ return this.botaoPressionado;	}
	
	public void resetBotaoPressiondado()
	{ this.botaoPressionado = false; }
}
