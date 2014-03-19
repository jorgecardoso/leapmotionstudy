package ElementosGraficos;

import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import processing.core.*;



public class InterfaceGrafica extends PApplet
{
	private static final long serialVersionUID = 1L;
	
	double posicaoCentralX;
	double posicaoCentralY;

	int raioCircunferenciaDesejado = 40;
	int raioCircunferencia;
	int distanciaCentro;
	
	public void setup() 
	{
		//Descobrir resolução do ecrã.
		int alturaJanela = Toolkit.getDefaultToolkit().getScreenSize().height;
		int larguraJanela = Toolkit.getDefaultToolkit().getScreenSize().width;
		
		//Janela vai ocupar todo os espaço permitido no ecrã.
		size(larguraJanela,alturaJanela);
		
		//O espaço ocupado é sempre inferior a resolução (a janela não se sobrepõe as barras de ferramentas / atalhos).
		//É, por isso, necessário actualizar as dimensões do Ecrã		
		posicaoCentralX = this.width / 2.0;
		posicaoCentralY = this.height / 2.0;
		
		//Vamos admitir que a distãncia ao centro de todas as circunferências é 1/2 da posição anterior mais pequena (podia ser outro valor qualquer).
		//Inicialmente, a altura é sempre a mais pequena para todas as resoluções, logo "posicaoCentralY".
		distanciaCentro = (int) (posicaoCentralY / 2.0);
		
		//Admitimos também que para a resolução 1280x1024 o raio das circunferências são de 40 pixeis.
		//Alterar caso a resolução seja diferente.
		raioCircunferencia = ((this.width * this.height) * raioCircunferenciaDesejado) / (1280*1024);
		
		//Incluir um "ouvinte" que altere os valores anteriores sempre que verifique que a aplicação é redimensionada.
		this.addComponentListener(
				new ComponentAdapter() 
				{
					public void componentResized(ComponentEvent e)
					{
						reajustarValores();
					}
				}
		);
		
		//Pintar o fundo da janela de branco
		background(255);
		
		//Forçar reajuste manual. Apesar da janela não poder ocupar o ecrã de forma completa, os valores anteriores não são actualizados. 
		reajustarValores();
		
		//Falta inicializar o Leap Motion <<<<<<------------
	}

	
	
	public void draw() 
	{   
		//Pintar o fundo da janela de branco.
		background(255);
		
		//Desenhar o conjunto de circunferências necessários a avaliação do dispositivo.
		//São 16 círculos no total.
		for(int i = 0; i < 16; i++)
		{
			double angulo = Math.toRadians(22.5 * i);
			
			double coordenadaX = posicaoCentralX + 
					distanciaCentro * Math.cos(angulo);
			
			double coordenadaY = posicaoCentralY + 
					distanciaCentro * Math.sin(angulo);
			
			desenhaCircunfencia((int) coordenadaX , (int) coordenadaY , raioCircunferencia);
		}
	}
	
	public void desenhaCircunfencia(int coordenadaX, int coordenadaY, int raio)
	{
		ellipse(coordenadaX, coordenadaY, raio, raio);
	}
	
	public void desenhaSinalPartida()
	{}
	
	public void desenhaSinalObjectivo()
	{}
	
	protected void reajustarValores()
	{
		//Reajustar os valores caso a dimensão da janela seja alterada dependendo do tamanho da aplicação.
		posicaoCentralX = this.width / 2.0;
		posicaoCentralY = this.height / 2.0;
		
		if(posicaoCentralX > posicaoCentralY)
		{distanciaCentro = (int) (posicaoCentralY / 2.0);}
		else
		{distanciaCentro = (int) (posicaoCentralX / 2.0);}
		
		raioCircunferencia = ((this.width * this.height) * 40) / (1280*1024);
	}
}
