package ElementosGraficos;

import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import Controladores.LeapMotion;
import Controladores.LeapMotion.modoDeControlo;
import Outros.Cronometro;
import Som.*;
import EstruturaDados.*;
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
	
	protected boolean redesenharElementos;
	
	private final int corDeFundo = 255; 			// Branco
	private final int corDeFundoCirculoR = 183;
	private final int corDeFundoCirculoG = 228;
	private final int corDeFundoCirculoB = 240;
	
	private Sequencia sequenciaARealizar;
	private boolean sequenciaAleatoria = false;
	private int posicaoSequencia = 0;
	
	private Vector<InformacaoGeral> experiencias = new Vector<InformacaoGeral>();
	private InformacaoGeral experienciaActual = new InformacaoGeral();
	boolean criarNovaExperiencia = true;
	
	private final Cronometro cronometro = new Cronometro();
	
	LeapMotion dispositivo = new LeapMotion(modoDeControlo.MaosComGestoKeytap, true); // booleano indica se utilizador é destro ou não.
	
	MouseMotionListener ouvinteRato;
	
	private int teste = 0;
	private boolean debug = false;
	
	
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
		int alturaJanela = Toolkit.getDefaultToolkit().getScreenSize().height;
		int larguraJanela = Toolkit.getDefaultToolkit().getScreenSize().width;
		
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
		
		//Desenhar os circulos criados anteriormente.
		for(int i = 0; i < circulos.size(); i++)
		{
			Circulo alvo = circulos.get(i);
			
			//Se o cursor estiver posicionado sobre a circunferência, pintar o seu interior de outra cor.
			if( alvo.pontoPertenceCirculo(mouseX, mouseY) )
			{
				fill(corDeFundoCirculoR,corDeFundoCirculoG,corDeFundoCirculoB);
			}
		
			desenhaCircunfencia( alvo.getCentroX(), alvo.getCentroY(), alvo.getRaioCircunferencia() );
			
			noFill();
		}
		
		if( posicaoSequencia == sequenciaARealizar.size() )
		{
			System.out.println("Teste completado com sucesso!\nA sair...");
			
			InformacaoGeral.guardarInformacaoEmFicheiros(experiencias);
			System.exit(0);
		}
		
		Circulo circuloAlvo = circulos.get(sequenciaARealizar.get(posicaoSequencia));
		desenharSinalAlvo(circuloAlvo.getCentroX(), circuloAlvo.getCentroY());
		
		if( ( mousePressed || dispositivo.getBotaoPressionado() ) && ( posicaoSequencia > 0 ) )
		{
			cronometro.parar();
			this.removeMouseMotionListener(ouvinteRato);
			
			Circulo circuloSelecionado = circulos.get(sequenciaARealizar.get(posicaoSequencia));
			
			if( circuloSelecionado.pontoPertenceCirculo(mouseX, mouseY) )
			{
				Som.tocarSomSucesso();
				
				//Identificar o resultado como um sucesso.
				experienciaActual.setSeleccaoComSucesso(true);
			}	
			else
			{
				Som.tocarSomFracasso();
				
				//Identificar o resultado como um insucesso.
				experienciaActual.setSeleccaoComSucesso(false);
			}
			
			Circulo circuloAnterior = circulos.get(sequenciaARealizar.get(posicaoSequencia - 1));
			
			//Armazenar os dados comuns a situação de insucesso ou sucesso.
			experienciaActual.setNumeroDeCirculos(nCircunferencias);
			experienciaActual.setIndexCirculoFinal(sequenciaARealizar.get(posicaoSequencia));
			experienciaActual.setIndexCirculoInicial(sequenciaARealizar.get(posicaoSequencia - 1));
			experienciaActual.setLarguraAlvo(circuloAlvo.getRaioCircunferencia() * 2); 		//A largura do alvo é o diametro da circunferência.
			experienciaActual.setTempoDecorrido(cronometro.getTempoEmMilisegundos());
			experienciaActual.setDistanciaCentroReferencial(distanciaCentro);
			experienciaActual.setDistanciaEntreCirculos(
					InformacaoGeral.calcularDistanciaEntrePontos(
							circuloAnterior.getCentroX(), circuloAnterior.getCentroY(), 
							circuloAlvo.getCentroX(), circuloAlvo.getCentroY())
			);
			
			//Guardar a experência realizada...
			experiencias.add(experienciaActual);
			
			//... e passar para a próxima.
			experienciaActual = new InformacaoGeral();
			posicaoSequencia++;
			
			//No caso de se estiver a usar o LeapMotion, indicar que o botão foi levantado.
			dispositivo.resetBotaoPressiondado();
						
			//Uma pequena pausa para evitar que o programa avançe se utilizador manter o botão carregado. 
			try {Thread.sleep(200);}catch (InterruptedException e) {}
			
			//Reinicializar ouvinte do Rato de forma a armazenar a posição para a nova experiência
			ouvinteRato = funcaoOuvinteRato();
			this.addMouseMotionListener(ouvinteRato);
			
			//Reinicializar cronometro
			cronometro.reset();	cronometro.comecar();
		}
		else if( ( mousePressed || dispositivo.getBotaoPressionado() ) && ( posicaoSequencia == 0 ) )
		{
			//A experiência só deve ser inicializada quando o utilizador carregar com sucesso no primeiro círculo. caso contrário
			Circulo circuloSelecionado = circulos.get(sequenciaARealizar.get(posicaoSequencia));
			
			Boolean botaoCertoCarragado = circuloSelecionado.pontoPertenceCirculo(mouseX, mouseY);
			
			if( botaoCertoCarragado )
			{
				Som.tocarSomSucesso();
				posicaoSequencia++;
			}	
			
			//No caso de se estiver a usar o LeapMotion, indicar que o botão foi levantado.
			dispositivo.resetBotaoPressiondado();
			
			//Uma pequena pausa para evitar que o programa avançe se utilizador manter o botão carregado. 
			try {Thread.sleep(200);}catch (InterruptedException e) {}
			
			if( botaoCertoCarragado )
			{
				//Começar a armazenar as posições do rato ao longo do tempo
				ouvinteRato = funcaoOuvinteRato();
				this.addMouseMotionListener(ouvinteRato);
				
				cronometro.comecar();
			}
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
		//Nota: O Processing recebe o não raio mas o diametro da circunferência em X e Y. Como só passamos o raio é preciso multiplicar por 2.
		ellipse(posicaoCentroCircunferenciaX, posicaoCentroCircunferenciaY, raioDaCircunferencia * 2, raioDaCircunferencia * 2);
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
			System.out.println("Não foi encontrado o ficheiro \"Config.txt\".\nA encerrar...\n");
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
		
		//Verifica se os valores foram realmente lidos correctamente ou se ultrapassam valores considerados adequados
		if( !(nCircunferencias >= 2 && nCircunferencias <= 32) )
		{
			System.out.println("Valor de \"Nº de circunferências\" deve variar entre 2 e 32, inclusíve.");
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
		tamanhoAlvoXActual = (int) (0.75 * 2 * raioCircunferencia);		//Nota: Processing recebe o diâmetro e não o raio. É preciso multiplicar por dois para os valores estarem matematicamente correctos.
		tamanhoAlvoYActual = (int) (0.125 * 2 * raioCircunferencia);
		circulos = new Vector<Circulo>(nCircunferencias);
		sequenciaARealizar = new Sequencia(nCircunferencias, sequenciaAleatoria);
	}

	private MouseMotionListener funcaoOuvinteRato()
	{
		return new MouseMotionListener() 
		{
			public void mouseMoved(MouseEvent e) 
			{ experienciaActual.guardarPosicaoRato(mouseX, mouseY);}
			
			public void mouseDragged(MouseEvent e) {}
		};
	}
}