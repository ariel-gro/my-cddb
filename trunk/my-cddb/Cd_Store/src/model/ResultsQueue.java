package model;

import java.util.LinkedList;
import java.util.Queue;

public class ResultsQueue
{
	private static Queue<Result> theResultsQueue = null;

	protected ResultsQueue() {
		// Exists only to defeat instantiation.
	}

	public synchronized static void addResult(Result theResult)
	{
		if (theResultsQueue == null)
			theResultsQueue = new LinkedList<Result>();

		theResultsQueue.add(theResult);
	}

	public synchronized static Result getResult()
	{
		if (theResultsQueue == null)
			theResultsQueue = new LinkedList<Result>();

		if (theResultsQueue.isEmpty() == false)
			return theResultsQueue.remove();
		else
			return null;
	}

	public synchronized static boolean isEmpty()
	{
		if (theResultsQueue == null)
			theResultsQueue = new LinkedList<Result>();

		return theResultsQueue.isEmpty();
	}
	
	public synchronized static Result peek()
	{
		if (theResultsQueue == null)
			theResultsQueue = new LinkedList<Result>();
		
		return theResultsQueue.peek();
	}
}
