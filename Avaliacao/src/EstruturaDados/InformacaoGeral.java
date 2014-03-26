package EstruturaDados;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import Controladores.LeapMotion;
import Controladores.LeapMotion.modoDeControlo;

public class InformacaoGeral 
{
	private int numeroDeCirculos;
	private int indexCirculoInicial; 					//Nota: O circulo com index 0 é o círculo desenhado mais a esquerda.
	private int indexCirculoFinal;						//      O index aumenta no sentido horário.
	private Vector<Pixel> percurso;
	private long tempoDecorrido;
	private boolean seleccaoComSucesso;
	private double distanciaEntreCirculosInicialFinal;	//Por enquanto, em píxies.
	private int larguraAlvo; 							//= 2 * Raio da circunferência.
	
	public InformacaoGeral() 
	{
		this.setNumeroDeCirculos(0);		this.percurso = new Vector<Pixel>();	this.setIndexCirculoFinal(0);
		this.setIndexCirculoInicial(0);	this.setSeleccaoComSucesso(false);		this.setLarguraAlvo(0);
		this.setTempoDecorrido(0);		this.setDistanciaEntreCirculos(0.0);
	}
	
	public int getNumeroDeCirculos() 
	{
		return numeroDeCirculos;
	}

	public void setNumeroDeCirculos(int numeroDeCirculos) 
	{
		this.numeroDeCirculos = numeroDeCirculos;
	}

	public int getIndexCirculoInicial() 
	{
		return indexCirculoInicial;
	}

	public void setIndexCirculoInicial(int indexCirculoInicial) 
	{
		this.indexCirculoInicial = indexCirculoInicial;
	}

	public int getIndexCirculoFinal() 
	{
		return indexCirculoFinal;
	}

	public void setIndexCirculoFinal(int indexCirculoFinal) 
	{
		this.indexCirculoFinal = indexCirculoFinal;
	}

	public long getTempoDecorrido() 
	{
		return tempoDecorrido;
	}

	public void setTempoDecorrido(long tempoDecorrido) 
	{
		this.tempoDecorrido = tempoDecorrido;
	}

	public boolean isSeleccaoComSucesso() 
	{
		return seleccaoComSucesso;
	}

	public void setSeleccaoComSucesso(boolean seleccaoComSucesso) 
	{
		this.seleccaoComSucesso = seleccaoComSucesso;
	}

	public double getDistanciaEntreCirculos() 
	{
		return distanciaEntreCirculosInicialFinal;
	}

	public void setDistanciaEntreCirculos(double distanciaEntreCirculos) 
	{
		this.distanciaEntreCirculosInicialFinal = distanciaEntreCirculos;
	}

	public int getLarguraAlvo() 
	{
		return larguraAlvo;
	}

	public void setLarguraAlvo(int larguraAlvo) 
	{
		this.larguraAlvo = larguraAlvo;
	}
	
	public void guardarPosicaoRato(int coordenadaX, int coordenadaY)
	{
		percurso.add(new Pixel(coordenadaX, coordenadaY) );
	}
	
	public void imprimirInformacaoNoEcraSemPercurso()
	{
		System.out.print("Nesta ronda a selecção foi efectuada ");
		
		if(!seleccaoComSucesso)
		{System.out.print("sem ");}	
		
		System.out.println(
			"sucesso." + "\n" +
			"Número de ciruculos existentes na experiência: " + numeroDeCirculos + "\n" +
			"Index do círculo onde começou esta ronda: " + indexCirculoInicial + "\n" +
			"acabando no círculo com index: " + indexCirculoFinal + ",\n" +
			"distando entre si: " + distanciaEntreCirculosInicialFinal + " píxeis.\n" +
			"Ambos tinham um diametro de " + larguraAlvo + " píxeis, logo " +  ((double) larguraAlvo) / 2.0 + " píxeis de raio.\n" +
			"Demorou " + tempoDecorrido + " milisegundos a efectuar a ronda."
		);
	}
	
	public void imprimirInformacaoNoEcraComPercurso()
	{
		imprimirInformacaoNoEcraSemPercurso();
		
		System.out.println("O percurso foi o seguinte:");
		
		for(int i = 0; i < percurso.size(); i++)
		{
			Pixel ponto = percurso.get(i);
			System.out.println( "( " + ponto.getCoordenadaX() + " , " + ponto.getCoordenadaY() + " ) ->" );
		}
	}
	
	public static double calcularDistanciaEntrePontos(int x1, int y1, int x2, int y2)
	{
		/**
		 * Formula da distância:
		 *
		 * distância  = raizQuadrada(  (x1 - x2)^2 + (y1 - y2)^2  )
		 */
		
		return Math.sqrt( Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) );
	}
	
	public static void guardarInformacaoEmFicheiros(Vector<InformacaoGeral> resultadosExperiencias)
	{
		
		//Criar o directório onde serão armazenados os resultados
		File pastaPrincipal = new File("Resultados");
		
		//Verificar se o directório já existe
		if ( !pastaPrincipal.exists() ) 
		{
			pastaPrincipal.mkdir();
		}
		
		System.out.println("Passou aqui??");
	 
		//Criar outro directório, dentro do anterior, onde serão armazenados os resultados da experiência
		//O nome deste será o data e hora a que este código é executado.
		DateFormat formatoDataHora = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
		Date DataHora = new Date();
		
		String nomeNovaPasta = "Resultados/" + formatoDataHora.format(DataHora).toString();
		File novaPasta = new File(nomeNovaPasta);
		
		//Verificar se o directório já existe. Pouco provável...
		if ( !novaPasta.exists() ) 
		{
			novaPasta.mkdir();
		}
		
		//Escrever o resultado de cada ronda da experiência realizada
		for(int i = 0; i < resultadosExperiencias.size(); i++)
		{
			InformacaoGeral valores = resultadosExperiencias.get(i);
			
			String nomeFicheiroResultado = nomeNovaPasta + "/" + valores.indexCirculoInicial + "->" + valores.indexCirculoFinal + ".txt";
			File ficheiroResultado = new File(nomeFicheiroResultado);
			
			if ( !ficheiroResultado.exists() ) 
			{
				try 
				{
					ficheiroResultado.createNewFile();
				} 
				catch (IOException e) 
				{
					System.err.println("Não foi possível armazenar a informação pretendida. Função \"guardarInformacaoEmFicheiros\".");
					return;
				}
			}

			try 
			{
				PrintWriter writer;
				writer = new PrintWriter(nomeFicheiroResultado, "UTF-8");
				writer.println("Sucesso: " + valores.isSeleccaoComSucesso() + "\n");
				writer.println("NCirculos: " + valores.getNumeroDeCirculos() + "\n");
				writer.println("IndexCirculoInicio: " + valores.getIndexCirculoInicial() + "\n");
				writer.println("IndexCirculoFim: " + valores.getIndexCirculoFinal() + "\n");
				writer.println("DistanciaCirculos: " + valores.getDistanciaEntreCirculos() + "\n");
				writer.println("LarguraAlvo(diametro/Pixel): " + valores.getLarguraAlvo() + "\n");
				writer.println("Tempo: " +  valores.getTempoDecorrido()+ "\n");

				writer.close();
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			} 
			catch (UnsupportedEncodingException e) 
			{
				e.printStackTrace();
			}
		}
		
		return;
	}
}
