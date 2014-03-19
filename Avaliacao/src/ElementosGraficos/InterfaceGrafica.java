package ElementosGraficos;

import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Vector;

import processing.core.*;

public class InterfaceGrafica extends PApplet
{
	private static final long serialVersionUID = 1L;
	
	private double posicaoCentralX;
	private double posicaoCentralY;

	private int raioCircunferenciaDesejado = 40;
	private int raioCircunferencia;
	private int distanciaCentro;
	
	private int tamanhoAlvoDesejadoX = 30;
	private int tamanhoAlvoDesejadoY = 5;
	private int tamanhoAlvoXActual;
	private int tamanhoAlvoYActual;
	
	protected boolean redesenharElementos = false;
	private int teste = 0;
	
	Vector<Circulo> circulos = new Vector<Circulo>(16);
	
	public void setup() 
	{
		//Algumas definições de desenho.
		rectMode(PConstants.CENTER); 			// As coordenadas passadas aos rectângulos são o seu centro
		stroke(0);								// As linhas são desenhadas a preto
		
		//Descobrir resolução do ecrã.
		int alturaJanela = Toolkit.getDefaultToolkit().getScreenSize().height;
		int larguraJanela = Toolkit.getDefaultToolkit().getScreenSize().width;
		
		//Janela vai ocupar todo o espaço permitido no ecrã.
		size(larguraJanela,alturaJanela);
		
		//Com base na tamanho ocupado realmente pela aplicação, recalcular as dimensões de todos os elementos
		redesenharElementos = true;
		redesenharElementos();
		
		//Incluir um "ouvinte" que altere os valores anteriores sempre que verifique que a aplicação é redimensionada.
		this.addComponentListener( new ComponentAdapter() {
			public void componentResized(ComponentEvent e)
			{
				redesenharElementos = true;
			}
		});

		//Pintar o fundo da janela de branco
		background(255);
		
		//Falta inicializar o Leap Motion <<<<<<------------
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
		background(255);
		
		//Desenhar os circulos criados anteriormente.
		for(int i = 0; (i < circulos.size()); i++)
		{
			Circulo alvo = circulos.get(i);
				
			desenhaCircunfencia( 
					alvo.getCentroX(), 
					alvo.getCentroY(),
					alvo.getRaioCircunferencia()
			);	
			
			//Poupança de recursos.
			if(redesenharElementos)
			{ return ; }
		}
		
		//Para teste
		Circulo cir = circulos.get(0);
		desenharSinalAlvo(cir.getCentroX(), cir.getCentroY());
		
		/*
		//C�digo Experimental
		 
		if(mousePressed)
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
		}
		*/
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
		posicaoCentralX = this.width / 2.0;
		posicaoCentralY = this.height / 2.0;
		
		//Vamos admitir que a distância ao centro da circunferência é 1/2 da "posicaoCentral" anterior mais pequena (podia ser outro valor qualquer).
		//Nota: inicialmente a altura é sempre a mais pequena para qualquer das resoluções, logo "posicaoCentralY".
		if(posicaoCentralX > posicaoCentralY)
		{distanciaCentro = (int) (posicaoCentralY / 2.0);}
		else
		{distanciaCentro = (int) (posicaoCentralX / 2.0);}
		
		//Poupança de recursos.
		if(redesenharElementos)
		{ return ; }
		
		//Para outras resoluções, calcular a alteração do raio da circunferência.
		raioCircunferencia = ((this.width * this.height) * raioCircunferenciaDesejado) / (1280*1024);
		
		//Poupança de recursos.
		if(redesenharElementos)
		{ return; }
		
		//Para outras resoluções, calcular a alteração do tamanho do alvo.
		tamanhoAlvoXActual = ((this.width * this.height) * tamanhoAlvoDesejadoX) / (1280*1024);
		tamanhoAlvoYActual = ((this.width * this.height) * tamanhoAlvoDesejadoY) / (1280*1024);
		
		//Poupança de recursos.
		if(redesenharElementos)
		{ return; }
		
		//Verificar se os circulos da avaliação já foram desenhados.
		//Em setup, esta acção não faz sentido, contudo esta função é invocada varias vezes em "draw()" caso exista um redimensionamento da janela. 
		if(!circulos.isEmpty())
		{ circulos.clear(); }
		
		//Calcular, criar ou redesenhar o conjunto de círculos necessários a avaliação do dispositivo.
		//São 16 círculos no total.
		for(int i = 0; i < 16; i++)
		{
			double angulo = Math.toRadians(22.5 * i);
				
			double coordenadaX = posicaoCentralX + 
					distanciaCentro * Math.cos(angulo);
				
			double coordenadaY = posicaoCentralY + 
					distanciaCentro * Math.sin(angulo);
			
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
}