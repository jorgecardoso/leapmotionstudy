package ElementosGraficos;

public class Circulo
{
	private int centroX;
	private int centroY;
	private int raioCircunferencia;
	
	Circulo(int coordenadasCentroX, int coordenadasCentroY, int raio)
	{
		centroX = coordenadasCentroX;
		centroY = coordenadasCentroY;
		raioCircunferencia = raio;
	}

	public int getCentroX() 
	{ return centroX; }

	public void setCentroX(int centroX) 
	{ this.centroX = centroX; }

	public int getCentroY() 
	{ return centroY; }

	public void setCentroY(int centroY) 
	{ this.centroY = centroY; }

	public int getRaioCircunferencia() 
	{ return raioCircunferencia; }

	public void setRaioCircunferencia(int raioCircunferencia) 
	{ this.raioCircunferencia = raioCircunferencia; }
	
	/**
	 * Fun��o que determina se o ponto, com as coordenadas passsadas em argumento,
	 * se encontra dentro do c�rculo especificado. 
	 * 
	 * @param pontoCoordenadaX
	 * @param pontoCoordenadaY
	 * @return true/false
	 */
	public boolean pontoPertenceCirculo(int pontoCoordenadaX, int pontoCoordenadaY)
	{
		/*
		 * Formula da �rea do circulo:
		 * (x - Cx)^2 + (y - Cy)^2 = raio^2
		 */
		double parte1 = Math.pow( (double) (pontoCoordenadaX - centroX) , 2.0);
		double parte2 = Math.pow( (double) (pontoCoordenadaY - centroY) , 2.0);
		double parte3 = Math.pow( (double) raioCircunferencia / 2, 2.0);
		
		parte1 = parte1 + parte2;
		
		//Vamos considerar que a circunfer�ncia tamb�m conta como dentro do circulo
		if( parte1 <= parte3)
		{ return true; }
		else
		{ return false; }
	}
}
