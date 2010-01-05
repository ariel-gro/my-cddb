package model;

public class SearchRequest implements Comparable<SearchRequest>
{
	final public static int HIGH_PRIORITY = 2;
	final public static int LOW_PRIORITY = 1;
	
	private int priority;
	
	public SearchRequest(int priority) {
		this.priority = priority;
	}


	@Override
	public int compareTo(SearchRequest obj)
	{
		SearchRequest that = (SearchRequest) obj; 

        int a = this.priority; 
        int b = that.priority; 
 
        if (a<b) return 1; 
        if (a>b) return -1; 
        return 0;
	}
	
	
}
