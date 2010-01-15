package controller;

import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import org.eclipse.ui.internal.SwitchToWindowMenu;

import model.SearchRequest;
import model.SqlStatement;
import model.advanceSearchFieldValueBundle;
import model.SearchRequest.AdvancedSearchFields;
import model.SearchRequest.MusicGenres;
import model.SqlStatement.queryType;

public class queryHandler {
	private SearchRequest searchReq;
	private static PriorityQueue<ResultSet> theQueue = null;
	
	private String createQuery (){
		
		SqlStatement sqlStmt;
		String query = "";
		
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
			List<advanceSearchFieldValueBundle> params = searchReq.getAdvanceSearchParameters();
			query += "SELECT Albums.* FROM Albums, Artists, Genres, tracks WHERE ";
			
			for (Iterator iterator = params.iterator(); iterator.hasNext();)
			{
				advanceSearchFieldValueBundle advanceSearchFieldValue = (advanceSearchFieldValueBundle) iterator.next();

				switch (advanceSearchFieldValue.getSearchField()) 
				{
				case ALBUM_TITLE:
					query += "Albums.title LIKE '%" + advanceSearchFieldValue.getValue() + "%' ";
					break;
				case ARTIST_NAME:
					query += "Artists.name LIKE '%" + advanceSearchFieldValue.getValue() + "%' AND Albums.artistId = Artists.artistId ";
					break;
				case GENRE:
					query += "Genres.genre LIKE '%" + advanceSearchFieldValue.getValue() + "%' AND Genres.genreId = Albums.genreId ";
					break;
				case TRACK_TITLE:
					query += "Tracks.title LIKE '%" + advanceSearchFieldValue.getValue() + "%' AND Tracks.id = Albums.id ";
					break;
				case YEAR:
					query += "Albums.year " + (advanceSearchFieldValue.getRelation().equals(advanceSearchFieldValueBundle.Relation.GREATER)?">":advanceSearchFieldValue.getRelation().equals(advanceSearchFieldValueBundle.Relation.EQUALS)?"=":"<") + advanceSearchFieldValue.getValue() + " ";
					break;
				default:
					break;
				}
			}
			
			query += "GROUP BY Albums.id";
			
			sqlStmt = new SqlStatement(queryType.QUERY, query, null, this.searchReq.getId());
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
