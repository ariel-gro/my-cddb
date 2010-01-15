package model;

import java.util.PriorityQueue;

public class SearchesPriorityQueue
{
	private static PriorityQueue<RequestToQueryHandler> thePriorityQueus = null;

	protected SearchesPriorityQueue() {
		// Exists only to defeat instantiation.
	}

	public synchronized static void addSearch(RequestToQueryHandler theSearchRequest)
	{
		if (thePriorityQueus == null)
			thePriorityQueus = new PriorityQueue<RequestToQueryHandler>();

		thePriorityQueus.add(theSearchRequest);
	}

	public synchronized static RequestToQueryHandler getSearch()
	{
		if (thePriorityQueus == null)
			thePriorityQueus = new PriorityQueue<RequestToQueryHandler>();

		if (thePriorityQueus.isEmpty() == false)
			return thePriorityQueus.remove();
		else
			return null;
	}

	public synchronized static boolean isEmpty()
	{
		if (thePriorityQueus == null)
			thePriorityQueus = new PriorityQueue<RequestToQueryHandler>();

		return thePriorityQueus.isEmpty();
	}

}
