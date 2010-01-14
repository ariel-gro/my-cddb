package controller;

import java.sql.ResultSet;
import java.util.PriorityQueue;

import model.SearchRequest;
import model.SqlStatement;
import model.SqlStatement.queryType;

public class queryHandler {
	private SearchRequest searchReq;
	private static PriorityQueue<ResultSet> theQueue = null;
	
	private String createQuery (){
		
		SqlStatement sqlStmt;
		
		switch (searchReq.getSearchType()){
		
		case ADVANCED:
			//TODO
		case REGULAR:
			//TODO:
		case TOP_10:
			switch (searchReq.getTop10Type()){
			case LATEST:
				sqlStmt = new SqlStatement(queryType.QUERY, "SELECT TOP 10 * FROM (SELECT * FROM Albums ORDERBY Albums.year)", null, searchReq.getId());
				connectionManager.insertToQueue(sqlStmt);
			case MOST_POPULAR:
				//TODO
			default:
				return "Error";
			}
		
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
