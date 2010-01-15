package controller;

import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.PriorityQueue;

import model.SearchRequest;
import model.SqlStatement;
import model.SearchRequest.MusicGenres;
import model.SqlStatement.queryType;

public class queryHandler {
	private SearchRequest searchReq;
	private static PriorityQueue<ResultSet> theQueue = null;
	
	private String createQuery (){
		
		SqlStatement sqlStmt;
		
		switch (searchReq.getSearchType()){
		// this.searchReq.getMusicGenre() - doing tostring, so need the enum to be lowercase?
		case TOP_10:
			switch (searchReq.getTop10Type()){
			case LATEST:
				if (searchReq.getMusicGenre() != null){
					sqlStmt = new SqlStatement(queryType.QUERY, "SELECT TOP 10 * FROM (SELECT * FROM Albums ORDERBY Albums.year) WHERE Albums.genre = " + this.searchReq.getMusicGenre().toString(), null, this.searchReq.getId());
					connectionManager.insertToQueue(sqlStmt);
				}
				else{
					sqlStmt = new SqlStatement(queryType.QUERY, "SELECT TOP 10 * FROM (SELECT * FROM Albums ORDERBY Albums.year)", null, this.searchReq.getId());
					connectionManager.insertToQueue(sqlStmt);
				}
			case MOST_POPULAR:
				if (searchReq.getMusicGenre() != null){
					sqlStmt = new SqlStatement(queryType.QUERY, "SELECT TOP 10 Albums.* FROM Albums, (SELECT id, COUNT(id) FROM Sales GROUP BY id ORDER BY COUNT(id) DESC) WHERE Sales.id = Albums.id AND Albums.genre = " + this.searchReq.getMusicGenre().toString(), null, this.searchReq.getId());
					connectionManager.insertToQueue(sqlStmt);
				}
				else{
					sqlStmt = new SqlStatement(queryType.QUERY, "SELECT TOP 10 Albums.* FROM Albums, (SELECT id, COUNT(id) FROM Sales GROUP BY id ORDER BY COUNT(id) DESC) WHERE Sales.id = Albums.id", null, this.searchReq.getId());
					connectionManager.insertToQueue(sqlStmt);
				}
			default:
				return "Error";
			}
		case REGULAR:
			sqlStmt = new SqlStatement(queryType.QUERY, "SELECT Albums.* FROM Albums, Artists WHERE (Albums.title LIKE '%"+this.searchReq.getRegularSearchString()+"%') OR (Artists.name LIKE '%"+this.searchReq.getRegularSearchString()+"%') GROUP BY Albums.id", null, this.searchReq.getId());
			connectionManager.insertToQueue(sqlStmt);
				
		case ADVANCED:
			//why use advanceSearchFieldValueBundle ? need just a list of string in a pre-determined order, and put them instead of the question marks.
			//what if some are null?
			sqlStmt = new SqlStatement(queryType.QUERY, "SELECT Albums.* FROM Albums, Artists, Genres, tracks WHERE (Albums.title LIKE ?) AND (Artists.name LIKE ? AND Albums.artistId = Artists.artistId) AND (Albums.year LIKE ?) AND (Genres.genre LIKE ? AND Genres.genreId = Albums.genreId) AND (Tracks.title LIKE ? AND Tracks.id = Albums.id) GROUP BY Albums.id", null, this.searchReq.getId());
			connectionManager.insertToQueue(sqlStmt);
			
			
		default:
			return "Error";
		}
	}
	
	public static synchronized void insertToQueue (ResultSet rs){
		if (theQueue == null)
			theQueue = new PriorityQueue<ResultSet>();
		
		theQueue.add(rs);
	}
	
	//TODO method to pop from theQueue and give back to Liron
	
}
