package ElementosGraficos;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JFrame;

import Controladores.LeapMotion;
import Controladores.LeapMotion.modoDeControlo;
import Som.Som;
import processing.core.*;

public class InterfaceGrafica extends PApplet
{
	private static final long serialVersionUID = 1L;
	
	private int posicaoCentralX;
	private int posicaoCentralY;

	Vector<Circulo> circulos;
	private int nCircunferencias;
	private int raioCircunferencia;
	private int distanciaCentro;
	
	private int tamanhoAlvoXActual;
	private int tamanhoAlvoYActual;
	
	boolean redesenharElementos;
	
	int corDeFundo = 255; 			// Branco
	int corDeFundoCirculoR = 183;
	int corDeFundoCirculoG = 228;
	int corDeFundoCirculoB = 240;
	
	
	private int teste = 0;
	private boolean debug = false;
	
	LeapMotion dispositivo = new LeapMotion(modoDeControlo.MaosComGestoKeytap, true); // booleano indica se utilizador é destro ou não.
	
	public void setup()
	{
		//Carregar os valores necessários a execução da aplicação do ficheiro de configuração
		carregarFicheiroConfiguracao();
				
		//Inicializar Leap Motion
		Thread lmThread = new Thread("Leap Motion Listener") {
			public void run(){
				dispositivo.inicializar();
			};
		};
		lmThread.start();

		//Algumas definições de desenho.
		rectMode(PConstants.CENTER); 			// As coordenadas passadas aos rectângulos são o seu centro
		stroke(0);								// As linhas são desenhadas a preto
		    
		//Descobrir resolução do ecrã.
		int alturaJanela = 800;//Toolkit.getDefaultToolkit().getScreenSize().height;
		int larguraJanela = 800;//Toolkit.getDefaultToolkit().getScreenSize().width;
		
		//Janela vai ocupar todo o espaço permitido no ecrã.
		size(larguraJanela,alturaJanela);
		
		//O espaço ocupado é sempre inferior a resolução (a janela pode não se sobrepor as barras de ferramentas / atalhos). No OSx não, em Windows sim.
		posicaoCentralX = (int) (larguraJanela / 2);
		posicaoCentralY = (int) (alturaJanela / 2);
		
		double angulo = 360.0 / ( (double) nCircunferencias);
		
		//Calcular, criar ou redesenhar o conjunto de círculos necessários a avaliação do dispositivo.
		for(int i = 0; i < nCircunferencias; i++)
		{
			double anguloActual = Math.toRadians(angulo * i);
						
			double coordenadaX = posicaoCentralX + 
					distanciaCentro * Math.cos(anguloActual);
						
			double coordenadaY = posicaoCentralY + 
					distanciaCentro * Math.sin(anguloActual);
					
			circulos.add( 
					new Circulo( ((int) coordenadaX),
								 ((int) coordenadaY), 
								 ((int) raioCircunferencia))
			);
		}
		
		this.addComponentListener( new ComponentAdapter() {
			public void componentResized(ComponentEvent e)
			{	redesenharElementos = true;	 }
		});
		
		//Pintar o fundo da janela de branco
		background(corDeFundo);
	}
	
	public void draw() 
	{  
		//Quando a aplicação é redimensionada os valores têm de ser recalculados.
		if(redesenharElementos)
		{ redesenharElementos(); }
		
		//Caso ocorra um redimensionamento... Encarregar a próxima execução de "draw()" de recalcular os elementos.
		if(redesenharElementos)
		{ return ; }
		
		//Pintar o fundo da janela de branco.
		background(corDeFundo);
		
		System.out.println(raioCircunferencia);
		
		desenhaCircunfencia(mouseX, mouseY, 10);
		//Desenhar os circulos criados anteriormente.
		for(int i = 0; i < circulos.size(); i++)
		{
			Circulo alvo = circulos.get(i);
			
			if( alvo.pontoPertenceCirculo(mouseX, mouseY) )
			{
				
				fill(corDeFundoCirculoR,corDeFundoCirculoG,corDeFundoCirculoB);
			}
		
			desenhaCircunfencia( 
					alvo.getCentroX(), 
					alvo.getCentroY(),
					alvo.getRaioCircunferencia()
			);
			
			noFill();
		}
		
		//Para teste
		Circulo cir = circulos.get(0);
		desenharSinalAlvo(cir.getCentroX(), cir.getCentroY());
		
		if(mousePressed || dispositivo.getBotaoPressionado())
		{
			boolean circuloEncontrado = false;
			
			for(int i = 0; i < circulos.size(); i++)
			{	
				if(circulos.get(i).pontoPertenceCirculo(mouseX, mouseY))
				{
					Som.tocarSomSucesso();
					circuloEncontrado = true;
					break;
				}	
			}
			
			if(!circuloEncontrado)
			{
				Som.tocarSomFracasso();
			}
			
			//try {Thread.sleep(200);}catch (InterruptedException e) {}
			dispositivo.resetBotaoPressiondado();
		}
	}
	
	private void redesenharElementos() 
	{
		//Impedir que a função se reinvoque caso não seja necessário.
		redesenharElementos = false;
		
		//Caso a aplicação volte a ser redimensionada imediatamente, algo que acontece enquanto se ajusta a janela.
		if(redesenharElementos)
		{ return ; }
		
		//O espaço ocupado é sempre inferior a resolução (a janela pode não se sobrepor as barras de ferramentas / atalhos). No OSx não, em Windows sim.
		//Pode ser, por isso, necessário actualizar as dimensões do ecrã		
		posicaoCentralX = (int) (this.width / 2.0);
		posicaoCentralY = (int) (this.height / 2.0);
				
		//Poupança de recursos.
		if(redesenharElementos)
		{ return ; }
		
		//Verificar se os circulos da avaliação já foram desenhados.
		//Em setup, esta acção não faz sentido, contudo esta função é invocada varias vezes em "draw()" caso exista um redimensionamento da janela. 
		if(!circulos.isEmpty())
		{ circulos.clear(); }
		
		
		double angulo = 360.0 / ( (double) nCircunferencias);
		
		//Calcular, criar ou redesenhar o conjunto de círculos necessários a avaliação do dispositivo.
		for(int i = 0; i < nCircunferencias; i++)
		{
			double anguloActual = Math.toRadians(angulo * i);
						
			double coordenadaX = posicaoCentralX + 
					distanciaCentro * Math.cos(anguloActual);
						
			double coordenadaY = posicaoCentralY + 
					distanciaCentro * Math.sin(anguloActual);
					
			circulos.add( 
					new Circulo( ((int) coordenadaX),
								 ((int) coordenadaY), 
								 ((int) raioCircunferencia))
			);
			
			//Poupança de recursos.
			if(redesenharElementos)
			{ return; }
		}
	}
	
	public void desenhaCircunfencia(int posicaoCentroCircunferenciaX, int posicaoCentroCircunferenciaY, int raioDaCircunferencia)
	{
		ellipse(posicaoCentroCircunferenciaX, posicaoCentroCircunferenciaY, raioDaCircunferencia, raioDaCircunferencia);
	}
	
	protected void desenharSinalAlvo(int coordanadaCentralX, int coordenadaCentralY)
	{
		noStroke();
		fill(137,42,139); 			//Roxo
		
		rect(coordanadaCentralX, coordenadaCentralY, tamanhoAlvoXActual, tamanhoAlvoYActual);
		rect(coordanadaCentralX, coordenadaCentralY, tamanhoAlvoYActual, tamanhoAlvoXActual);
		
		fill(255,255,255);			//Branco
		stroke(0);
	}
	
	private void carregarFicheiroConfiguracao() 
	{
		BufferedReader leitor = null;
		
		try 
		{
			leitor = new BufferedReader( new FileReader( new File ("../Config.txt")	) );
		} 
		catch (FileNotFoundException e) 
		{
			System.out.println("Não foi encontrado o ficheiro \"Config.txt\".\nA encerrar...\n");// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String linhaLida = "";
		
		while( linhaLida != null)
		{
			try 
			{
				linhaLida = leitor.readLine();
			} 
			catch (IOException e) 
			{
				System.out.println("Não foi possível ler o ficheiro.\nA terminar...");
			}
			
			if(linhaLida == null)
			{ continue; }
			
			String[] resultado = linhaLida.split(":");
			
			if(resultado.length <= 1)
			{continue;}
			
			//Retirar eventuais caracteres que possam existir
			resultado[1] = resultado[1].replace(" ", "");
			resultado[1] = resultado[1].replace(".", "");
			
			//Guarda os valores lidos nas variáveis adequadas.
			if( resultado[0].equals("Nº de circunferências") )
			{ nCircunferencias = Integer.parseInt(resultado[1]); }
			else if( resultado[0].equals("Raio da circunferência") )
			{ raioCircunferencia = Integer.parseInt(resultado[1]); }
			else if( resultado[0].equals("Distância das circunferências ao centro do ecrã") )
			{ distanciaCentro = Integer.parseInt(resultado[1]); }
		}
		
		System.out.println(posicaoCentralX >= 0 && posicaoCentralX <= 1920);
		
		//Verifica se os valores foram realmente lidos correctamente ou se ultrapassam valores considerados adequados
		if( !(nCircunferencias >= 1 && nCircunferencias <= 32) )
		{
			System.out.println("Valor de \"Nº de circunferências\" deve variar entre 1 e 32, inclusíve.");
			System.exit(3);
		}
		else if( !(raioCircunferencia >= 0 && raioCircunferencia <= 200) )
		{
			System.out.println("Não foi possível ler o valor de \"Raio da circunferência\".");
			System.exit(4);
		}
		else if( !(distanciaCentro >= 0 && distanciaCentro <= 800) )
		{
			System.out.println("Não foi possível ler o valor de \"Raio da circunferência\".");
			System.exit(5);
		}
		
		//Inicializar as restantes variáveis com os valores adquiridos 
		tamanhoAlvoXActual = (int) (0.75 * raioCircunferencia);
		tamanhoAlvoYActual = (int) (0.125 * raioCircunferencia);
		circulos = new Vector<Circulo>(nCircunferencias);
	}
}