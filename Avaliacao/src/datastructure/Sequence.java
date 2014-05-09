package datastructure;

import java.util.ArrayList;
import java.util.Random;

public class Sequence 
{
	ArrayList<Integer> sequenceOfCircles = new ArrayList<Integer>();
	
	/**
	 * Constructor of the Class Sequence.
	 * Creates an array of integers representing the order (or index) in which the circles should be traveled during the experiment.
	 * 
	 * @param numberOfCircles - Number of circles to be used in the experiment. 
	 * @param random - Boolean indicating if the sequence is to be randomly generated (true) or should if it should follow Mackenzie's Paper (false).
	 */
	public Sequence(int numberOfCircles, boolean random)
	{
		if(numberOfCircles <= 2)
		{
			System.err.println("It is not possible to create a sequence with one or less number of circles...\nShutting down.");
			System.exit(0);
		}
		
		//If the number of circles is equal to 16 and not random, return a sequence similar to the one described on Mackenzie's paper.
		if( (numberOfCircles == 16) && !random )
		{
			sequenceOfCircles.add(12); sequenceOfCircles.add(4);
			sequenceOfCircles.add(13); sequenceOfCircles.add(5);
			sequenceOfCircles.add(14); sequenceOfCircles.add(6);
			sequenceOfCircles.add(15); sequenceOfCircles.add(7);
			sequenceOfCircles.add(0);  sequenceOfCircles.add(8);
			sequenceOfCircles.add(1);  sequenceOfCircles.add(9);
			sequenceOfCircles.add(2);  sequenceOfCircles.add(10);
			sequenceOfCircles.add(3);  sequenceOfCircles.add(11);
			
			return;
		}
		
		//Trying to follow a pattern similiar to Mackenzie's if the number of circles is different than 16.
		if( (numberOfCircles != 16) && !random )
		{
			//NOTE: The sequence generated won't be ISO (ISO 9241-9:2000) complaint.
			int beginCounter, beginCounterIterator, endCounter, endCounterIterator;
			
			if( even(numberOfCircles) )
			{
				beginCounter = 0;
				beginCounterIterator = beginCounter;

				endCounter = (numberOfCircles / 2);
				endCounterIterator = endCounter;
			}
			else
			{
				beginCounter = 0;
				beginCounterIterator = beginCounter;

				endCounter = (numberOfCircles / 2) + 1;
				endCounterIterator = endCounter;
			}

			while( true )
			{
				if( (beginCounterIterator == endCounter) && (endCounterIterator == beginCounter) )
				{ break; }

				if( (beginCounterIterator == numberOfCircles) )
				{ beginCounterIterator = 0; }

				if( (endCounterIterator == numberOfCircles) )
				{ endCounterIterator = 0; }

				if(beginCounterIterator != endCounter)
				{ 
					sequenceOfCircles.add(beginCounterIterator);
					beginCounterIterator++;
				}

				if(endCounterIterator != beginCounter)
				{ 
					sequenceOfCircles.add(endCounterIterator);
					endCounterIterator++;
				}
			}
			
			return;
		}
		
		//If none of the last cases occurred (in other words, the generation is random) an arbitrary sequence with the requested number of circles will be created.
		for(int i = 0; i< numberOfCircles; i++)
		{
			sequenceOfCircles.add(i);
		}
		
		Random randomGenerator = new Random();
		int pos1, pos2, value1, value2;
		
		for(int i = 0; i < numberOfCircles; i++)
		{
			pos1 = randomGenerator.nextInt(numberOfCircles);
			pos2 = randomGenerator.nextInt(numberOfCircles);
			
			value1 = sequenceOfCircles.get(pos1);
			value2 = sequenceOfCircles.get(pos2);
			
			sequenceOfCircles.set(pos1,  value2);
			sequenceOfCircles.set(pos2,  value1);
		}
		
		return;
	}
	
	/**
	 * Function that returns the size of the sequence.
	 * 
	 * @return Integer value with the sequence size.
	 */
	public int size()
	{ return sequenceOfCircles.size();}
	
	/**
	 * Function that returns the number of the sequence stored on the requested position. 
	 * 
	 * @param index - Position to be read. 
	 * @return Integer representing the index of the circle stored on the requested position. 
	 */
	public int get(int index)
	{ return sequenceOfCircles.get(index);}
	
	/**
	 * Function that determines if a number is even (or odd).
	 * 
	 * @param number - The number to be analyzed 
	 * @return True if the number is even or False if the number is odd.
	 */
	public boolean even(int number)
	{
		if(number % 2 == 0) 
		{return true;}
		else 
		{return false;}
	}
}