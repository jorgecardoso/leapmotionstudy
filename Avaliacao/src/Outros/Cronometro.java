package Outros;

// O seguinte c—digo foi baseado em: http://silveiraneto.net/2008/03/15/simple-java-chronometer/

public class Cronometro 
{
	private long tempoInicio;
	private long tempoFim;
	boolean parado;
	
	public Cronometro()
	{
		reset();
	}
	
	public void comecar()
	{
		tempoInicio =  System.currentTimeMillis();
		parado = false;
	}
	
	public void parar()
	{
		tempoFim = System.currentTimeMillis();
		parado = true;
	}
	
	public long getTempoEmMilisegundos()
	{
		if(!parado)
		{parar();}
		
		return tempoFim - tempoInicio;
	}
	
	public long getTempoEmSegundos()
	{
		if(!parado)
		{parar();}
		
		return (tempoFim - tempoInicio) / 1000;
	}
	
	public void reset()
	{
		tempoInicio = 0;
		tempoFim = 0;
		parado = true;
	}
}