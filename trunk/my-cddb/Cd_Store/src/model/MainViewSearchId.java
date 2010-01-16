package model;

public class MainViewSearchId
{
	private static int id = -1;
	
	public static synchronized int getId()
	{
		return id;
	}
	public static synchronized void setId(int id)
	{
		MainViewSearchId.id = id;
	}
}
