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
		//Descobrir resolu��o do ecr�.
		int alturaJanela = Toolkit.getDefaultToolkit().getScreenSize().height;
		int larguraJanela = Toolkit.getDefaultToolkit().getScreenSize().width;
		
		//Janela vai ocupar todo os espa�o permitido no ecr�.
		size(larguraJanela,alturaJanela);
		
		//O espa�o ocupado � sempre inferior a resolu��o (a janela n�o se sobrep�e as barras de ferramentas / atalhos).
		//�, por isso, necess�rio actualizar as dimens�es do Ecr�		
		posicaoCentralX = this.width / 2.0;
		posicaoCentralY = this.height / 2.0;
		
		//Vamos admitir que a dist�ncia ao centro de todas as circunfer�ncias � 1/2 da posi��o anterior mais pequena (podia ser outro valor qualquer).
		//Inicialmente, a altura � sempre a mais pequena para todas as resolu��es, logo "posicaoCentralY".
		distanciaCentro = (int) (posicaoCentralY / 2.0);
		
		//Admitimos tamb�m que para a resolu��o 1280x1024 o raio das circunfer�ncias s�o de 40 pixeis.
		//Alterar caso a resolu��o seja diferente.
		raioCircunferencia = ((this.width * this.height) * raioCircunferenciaDesejado) / (1280*1024);
		
		//Incluir um "ouvinte" que altere os valores anteriores sempre que verifique que a aplica��o � redimensionada.
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
		
		//For�ar reajuste manual. Apesar da janela n�o poder ocupar o ecr� de forma completa, os valores anteriores n�o s�o actualizados. 
		reajustarValores();
		
		//Falta inicializar o Leap Motion <<<<<<------------
	}

	
	
	public void draw() 
	{   
		//Pintar o fundo da janela de branco.
		background(255);
		
		//Desenhar o conjunto de circunfer�ncias necess�rios a avalia��o do dispositivo.
		//S�o 16 c�rculos no total.
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
		//Reajustar os valores caso a dimens�o da janela seja alterada dependendo do tamanho da aplica��o.
		posicaoCentralX = this.width / 2.0;
		posicaoCentralY = this.height / 2.0;
		
		if(posicaoCentralX > posicaoCentralY)
		{distanciaCentro = (int) (posicaoCentralY / 2.0);}
		else
		{distanciaCentro = (int) (posicaoCentralX / 2.0);}
		
		raioCircunferencia = ((this.width * this.height) * 40) / (1280*1024);
	}
}
