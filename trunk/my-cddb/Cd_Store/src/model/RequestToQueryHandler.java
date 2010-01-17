package model;

import java.util.HashMap;
import java.util.List;

import model.SqlStatement.QueryType;

public class RequestToQueryHandler implements Comparable<RequestToQueryHandler>
{
	public enum Priority {HIGH_PRIORITY, LOW_PRIORITY}
	public enum SearchType {TOP_10, REGULAR, ADVANCED, GET_USERS}	
	public enum Top10Type {LATEST, MOST_POPULAR};	
	public enum MusicGenres {ALL, BLUES, CLASSICAL, COUNTRY, DATA, FOLK, JAZZ, NEWAGE, REGGAE, ROCK, SOUNDTRACK, MISC};
	public enum AdvancedSearchFields {ALBUM_TITLE, ARTIST_NAME, TRACK_TITLE, GENRE, YEAR};
	public enum SingleInsertType {ADD_USER, ADD_SALE};
	public enum MapType {ALBUMS, ARTISTS, GENRES, TRACKS};
	
	private int id;
	private Priority priority;
	private SearchType searchType;
	private Top10Type top10Type;
	private MusicGenres musicGenre;
	private String regularSearchString;
	private QueryType theQueryType;
	private List<advanceSearchFieldValueBundle> advanceSearchParameters;
	private HashMap<String,String[]> map;
	private MapType mapType;
	private SingleInsertType singleInsertType;
	private String[] dualFields; 
	
	
	
	// TOP 10 by music musicGenres
	public RequestToQueryHandler(int id, Priority priority, SearchType searchType, MusicGenres musicGenre) 
	{
		this.id = id;
		this.theQueryType = QueryType.QUERY;
		this.priority = priority;
		this.searchType = searchType;
		this.musicGenre = musicGenre;
		this.top10Type = null;
	}
	
	// TOP 10 by top10Type
	public RequestToQueryHandler(int id, Priority priority, SearchType searchType, Top10Type top10Type) 
	{
		this.id = id;
		this.theQueryType = QueryType.QUERY;
		this.priority = priority;
		this.searchType = searchType;
		this.top10Type = top10Type;	
	}
	
	// Regular search
	public RequestToQueryHandler(int id, Priority priority, SearchType searchType, MusicGenres musicGenre, String regularSearchString) 
	{
		this.id = id;
		this.theQueryType = QueryType.QUERY;
		this.priority = priority;
		this.searchType = searchType;
		this.musicGenre = musicGenre;
		this.regularSearchString = regularSearchString;
	}
	
	// Advances search with advanceSearchParameters
	public RequestToQueryHandler(int id, Priority priority, SearchType searchType, List<advanceSearchFieldValueBundle> advanceSearchParameters) 
	{
		this.id = id;
		this.theQueryType = QueryType.QUERY;
		this.priority = priority;
		this.searchType = searchType;
		this.advanceSearchParameters = advanceSearchParameters;
	}
	
	// Insert Singele
	public RequestToQueryHandler(int id, Priority priority, QueryType theQueryType, SingleInsertType singleInsertType, String[] dualFields) 
	{
		this.id = id;
		this.priority = priority;
		this.theQueryType = theQueryType;
		this.singleInsertType = singleInsertType;
		this.dualFields = dualFields;
	}
	
	// InsertBulk
	public RequestToQueryHandler(int id, Priority priority, QueryType theQueryType, HashMap<String,String[]> map, MapType mapType) 
	{
		this.id = id;
		this.priority = priority;
		this.theQueryType = theQueryType;
		this.map = map;
		this.mapType = mapType;
	}
	
	// For priority queue
	public int compareTo(RequestToQueryHandler obj)
	{
		RequestToQueryHandler that = (RequestToQueryHandler) obj; 

		Priority a = this.priority; 
		Priority b = that.priority; 
 
        if (a.ordinal()<b.ordinal()) return 1; 
        if (a.ordinal()>b.ordinal()) return -1; 
        return 0;
	}
	
	// Get Users 
	public RequestToQueryHandler(int id, Priority priority, SearchType searchType) 
	{
		this.id = id;
		this.theQueryType = QueryType.QUERY;
		this.priority = priority;
		this.searchType = searchType;	
	}
	
	public synchronized int getId()
	{
		return id;
	}

	public synchronized Priority getPriority()
	{
		return priority;
	}

	public synchronized SearchType getSearchType()
	{
		return searchType;
	}

	public synchronized Top10Type getTop10Type()
	{
		return top10Type;
	}
	
	public synchronized MusicGenres getMusicGenres()
	{
		return musicGenre;
	}

	public synchronized MusicGenres getMusicGenre()
	{
		return musicGenre;
	}

	public synchronized String getRegularSearchString()
	{
		return regularSearchString;
	}
	
	public synchronized QueryType getTheQueryType()
	{
		return theQueryType;
	}
	
	public synchronized SingleInsertType getSingleInsertType()
	{
		return singleInsertType;
	}

	public synchronized String[] getDualFields()
	{
		return dualFields;
	}

	public synchronized HashMap<String, String[]> getMap()
	{
		return map;
	}

	public synchronized MapType getMapType()
	{
		return mapType;
	}

	public List<advanceSearchFieldValueBundle> getAdvanceSearchParameters()
	{
		return advanceSearchParameters;
	}
}
