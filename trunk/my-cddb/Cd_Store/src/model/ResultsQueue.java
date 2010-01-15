package model;

import java.util.LinkedList;
import java.util.Queue;

public class ResultsQueue
{
	private static Queue<Result> theResultsQueue = null;

	protected ResultsQueue() {
		// Exists only to defeat instantiation.
	}

	public synchronized static void addSearch(Result theResult)
	{
		if (theResultsQueue == null)
			theResultsQueue = new LinkedList<Result>();

		theResultsQueue.add(theResult);
	}

	public synchronized static Result getSearch()
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
}
