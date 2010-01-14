package model;

import java.util.Hashtable;

public class SqlStatement
{
	public enum queryType {INSERT_SINGLE, INSERT_BULK, QUERY}	
	
	private queryType queryType;
	private String stmt;
	private Hashtable<String,String[]> tuples;
	private int requestId;
	
	public SqlStatement(queryType queryType, String stmt, Hashtable<String, String[]> tuples, int requestId) {
		this.queryType = queryType;
		this.stmt = stmt;
		this.tuples = tuples;
		this.requestId = requestId;
	}

	public queryType getQueryType() {
		return queryType;
	}

	public void setQueryType(queryType queryType) {
		this.queryType = queryType;
	}

	public String getStmt() {
		return stmt;
	}

	public void setStmt(String stmt) {
		this.stmt = stmt;
	}

	public Hashtable<String, String[]> getTuples() {
		return tuples;
	}

	public void setTuples(Hashtable<String, String[]> tuples) {
		this.tuples = tuples;
	}

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}
	
	
}
