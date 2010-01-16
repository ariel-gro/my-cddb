package model;

public class SearchParameters
{
	private static String genre;
	private static String searchString;
	
	public static synchronized String getGenre()
	{
		return genre;
	}
	public static synchronized void setGenre(String genre)
	{
		SearchParameters.genre = genre;
	}
	public static synchronized String getSearchString()
	{
		return searchString;
	}
	public static synchronized void setSearchString(String searchString)
	{
		SearchParameters.searchString = searchString;
	}
}
