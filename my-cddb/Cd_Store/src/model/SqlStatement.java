package model;

import model.RequestToQueryHandler.MapType;

public class SqlStatement
{
	public enum QueryType {INSERT_SINGLE, INSERT_BULK, QUERY}	
	
	private QueryType queryType;
	private MapType mapType;
	private String stmt;
	private String[][] tuples;
	private int requestId;
	
	public SqlStatement(QueryType queryType, RequestToQueryHandler.MapType mapType, String stmt, String[][] tuples, int requestId) {
		this.queryType = queryType;
		this.mapType = mapType;
		this.stmt = stmt;
		this.tuples = tuples;
		this.requestId = requestId;
	}

	public QueryType getQueryType() {
		return queryType;
	}

	public void setQueryType(QueryType queryType) {
		this.queryType = queryType;
	}

	public void setMapType(RequestToQueryHandler.MapType mapType) {
		this.mapType = mapType;
	}

	public RequestToQueryHandler.MapType getMapType() {
		return mapType;
	}

	public String getStmt() {
		return stmt;
	}

	public void setStmt(String stmt) {
		this.stmt = stmt;
	}

	public String[][] getTuples() {
		return tuples;
	}

	public void setTuples(String[][] tuples) {
		this.tuples = tuples;
	}

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}
	
	
}
