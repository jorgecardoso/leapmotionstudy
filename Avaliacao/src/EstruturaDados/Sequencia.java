package EstruturaDados;

import java.util.ArrayList;
import java.util.Random;

public class Sequencia 
{
	ArrayList<Integer> sequenciaDeCircunferencias = new ArrayList<Integer>();
	
	public Sequencia(int numeroCircunferencias, boolean aleatorio)
	{
		System.out.println("A gerar sequência. Aguarde um pouco...");
		if(numeroCircunferencias <= 1)
		{
			System.out.println("Impossível criar um sequência com 0 ou menos circunferências...\nA encerrar.");
			System.exit(100);
		}
		
		//Se o número de circunferências for igual a 16 e não aleatório, retornar uma sequência em conformidade com o documento de Mackenzie.
		if( (numeroCircunferencias == 16) && !aleatorio )
		{
			sequenciaDeCircunferencias.add(12); sequenciaDeCircunferencias.add(4);
			sequenciaDeCircunferencias.add(13); sequenciaDeCircunferencias.add(5);
			sequenciaDeCircunferencias.add(14); sequenciaDeCircunferencias.add(6);
			sequenciaDeCircunferencias.add(15); sequenciaDeCircunferencias.add(7);
			sequenciaDeCircunferencias.add(0);  sequenciaDeCircunferencias.add(8);
			sequenciaDeCircunferencias.add(1);  sequenciaDeCircunferencias.add(9);
			sequenciaDeCircunferencias.add(2);  sequenciaDeCircunferencias.add(10);
			sequenciaDeCircunferencias.add(3);  sequenciaDeCircunferencias.add(11);
			
			System.out.println("Sequência de Mackenzie gerada com sucesso...");
			
			return;
		}
		
		//Tentar seguir um padrão semelhante ao de Mackenzie mas para mais circulos.
		if( (numeroCircunferencias != 16) && !aleatorio )
		{
			int contadorInicial, iteradorContadorInicial, contadorFinal, iteradorContadorFinal;
			
			if( par(numeroCircunferencias) )
			{
				contadorInicial = 0;
				iteradorContadorInicial = contadorInicial;

				contadorFinal = (numeroCircunferencias / 2);
				iteradorContadorFinal = contadorFinal;
			}
			else
			{
				contadorInicial = 0;
				iteradorContadorInicial = contadorInicial;

				contadorFinal = (numeroCircunferencias / 2) + 1;
				iteradorContadorFinal = contadorFinal;
			}

			while( true )
			{
				if( (iteradorContadorInicial == contadorFinal) && (iteradorContadorFinal == contadorInicial) )
				{ break; }

				if( (iteradorContadorInicial == numeroCircunferencias) )
				{ iteradorContadorInicial = 0; }

				if( (iteradorContadorFinal == numeroCircunferencias) )
				{ iteradorContadorFinal = 0; }

				if(iteradorContadorInicial != contadorFinal)
				{ 
					sequenciaDeCircunferencias.add(iteradorContadorInicial);
					iteradorContadorInicial++;
				}

				if(iteradorContadorFinal != contadorInicial)
				{ 
					sequenciaDeCircunferencias.add(iteradorContadorFinal);
					iteradorContadorFinal++;
				}
			}
			
			return;
		}
		
		//Caso nunhum dos casos anteriores tenha ocorrido, criar uma sequência aleatória para o número de circulos.

		for(int i = 0; i< numeroCircunferencias; i++)
		{
			sequenciaDeCircunferencias.add(i);
		}
		
		Random randomGenerator = new Random();
		int pos1, pos2, valor1, valor2;
		
		for(int i = 0; i < numeroCircunferencias; i++)
		{
			pos1 = randomGenerator.nextInt(numeroCircunferencias);
			pos2 = randomGenerator.nextInt(numeroCircunferencias);
			
			valor1 = sequenciaDeCircunferencias.get(pos1);
			valor2 = sequenciaDeCircunferencias.get(pos2);
			
			sequenciaDeCircunferencias.set(pos1,  valor2);
			sequenciaDeCircunferencias.set(pos2,  valor1);
		}
		
		return;
	}
	
	public int size()
	{ return sequenciaDeCircunferencias.size();}
	
	public int get(int index)
	{ return sequenciaDeCircunferencias.get(index);}
	
	public boolean par(int numero)
	{
		if(numero % 2 == 0) 
		{return true;}
		else 
		{return false;}
	}
}