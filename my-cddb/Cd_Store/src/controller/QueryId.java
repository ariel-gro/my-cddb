package controller;

public class QueryId
{
	private static int id = 0;

	public static synchronized int getId()
	{
		id++;
		
		return id;
	}
}
