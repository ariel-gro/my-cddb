package model;

import java.util.PriorityQueue;

public class SearchesPriorityQueue
{
	private static PriorityQueue<SearchRequest> thePriorityQueus = null;

	protected SearchesPriorityQueue() {
		// Exists only to defeat instantiation.
	}

	public synchronized static void addSearch(SearchRequest theSearchRequest)
	{
		if (thePriorityQueus == null)
			thePriorityQueus = new PriorityQueue<SearchRequest>();

		thePriorityQueus.add(theSearchRequest);
	}

	public synchronized static SearchRequest getSearch()
	{
		if (thePriorityQueus == null)
			thePriorityQueus = new PriorityQueue<SearchRequest>();

		if (thePriorityQueus.isEmpty() == false)
			return thePriorityQueus.remove();
		else
			return null;
	}

	public synchronized static boolean isEmpty()
	{
		if (thePriorityQueus == null)
			thePriorityQueus = new PriorityQueue<SearchRequest>();

		return thePriorityQueus.isEmpty();
	}

}
