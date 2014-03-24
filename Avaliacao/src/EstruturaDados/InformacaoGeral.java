package EstruturaDados;

import java.util.Vector;

public class InformacaoGeral 
{
	private int numeroDeCirculos;
	private int indexCirculoInicial; 					//Nota: O circulo com index 0 é o círculo desenhado mais a esquerda.
	private int indexCirculoFinal;						//      O index aumenta no sentido horário.
	Vector<Pixel> percurso;
	private long tempoDecorrido;
	private boolean seleccaoComSucesso;
	private int distanciaEntreCirculosInicialFinal;	    //Por enquanto, em píxies.
	private int larguraAlvo; 							//= 2 * Raio da circunferência.
	
	public InformacaoGeral() 
	{
		this.numeroDeCirculos = 0;		this.percurso = new Vector<Pixel>();	this.indexCirculoFinal = 0;
		this.indexCirculoInicial = 0;	this.seleccaoComSucesso = false;		this.larguraAlvo = 0;
		this.tempoDecorrido = 0;		this.distanciaEntreCirculosInicialFinal = 0;
	}
	
	
}
