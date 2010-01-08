package model;

import java.util.List;

public class SearchRequest implements Comparable<SearchRequest>
{
	public enum Priority {HIGH_PRIORITY, LOW_PRIORITY}
	public enum SearchType {TOP_10, REGULAR, ADVANCED}	
	public enum Top10Type {LATEST, MOST_POPULAR};	
	public enum MusicGenres {ALL, BLUES, CLASSICAL, COUNTRY, DATA, FOLK, JAZZ, NEWAGE, REGGAE, ROCK, SOUNDTRACK, MISC};
	public enum AdvancedSearchFields {ALBUM_TITLE, ARTIST_NAME, TRACK_TITLE, GENRE, YEAR};
	
	private int id;
	private Priority priority;
	private SearchType searchType;
	private Top10Type top10Type;
	private MusicGenres musicGenre;
	private String regularSearchString;
	private List<advanceSearchFieldValueBundle> advanceSearchParameters;
	
	// TOP 10 by music musicGenres
	public SearchRequest(int id, Priority priority, SearchType searchType, MusicGenres musicGenre) 
	{
		this.id = id;
		this.priority = priority;
		this.searchType = searchType;
		this.musicGenre = musicGenre;
	}
	
	// TOP 10 by top20Type
	public SearchRequest(int id, Priority priority, SearchType searchType, Top10Type top10Type) 
	{
		this.id = id;
		this.priority = priority;
		this.searchType = searchType;
		this.top10Type = top10Type;
	}
	
	// Regular search with musicGenres and regularSearchString
	public SearchRequest(int id, Priority priority, SearchType searchType, MusicGenres musicGenre, String regularSearchString) 
	{
		this.id = id;
		this.priority = priority;
		this.searchType = searchType;
		this.musicGenre = musicGenre;
		this.regularSearchString = regularSearchString;
	}
	
	// Advances search with advanceSearchParameters
	public SearchRequest(int id, Priority priority, SearchType searchType, List<advanceSearchFieldValueBundle> advanceSearchParameters) 
	{
		this.id = id;
		this.priority = priority;
		this.searchType = searchType;
		this.advanceSearchParameters = advanceSearchParameters;
	}
	
	
	
	// For priority queue
	public int compareTo(SearchRequest obj)
	{
		SearchRequest that = (SearchRequest) obj; 

		Priority a = this.priority; 
		Priority b = that.priority; 
 
        if (a.ordinal()<b.ordinal()) return 1; 
        if (a.ordinal()>b.ordinal()) return -1; 
        return 0;
	}
	
	
	public int getId()
	{
		return id;
	}

	public Priority getPriority()
	{
		return priority;
	}

	public SearchType getSearchType()
	{
		return searchType;
	}

	public Top10Type getTop20Type()
	{
		return top10Type;
	}
	
	public MusicGenres getMusicGenres()
	{
		return musicGenre;
	}

	public MusicGenres getMusicGenre()
	{
		return musicGenre;
	}

	public String getRegularSearchString()
	{
		return regularSearchString;
	}

	public List<advanceSearchFieldValueBundle> getAdvanceSearchParameters()
	{
		return advanceSearchParameters;
	}

	
	//TEMP FOR TESTING
	public static void main(String [] args)
	{
		SearchRequest mySearch = new SearchRequest(0, SearchRequest.Priority.HIGH_PRIORITY, SearchRequest.SearchType.REGULAR, SearchRequest.MusicGenres.ALL);
	
		System.out.println(mySearch.getPriority());
		System.out.println(mySearch.getSearchType());
		System.out.println(mySearch.getMusicGenres());
	}
}
