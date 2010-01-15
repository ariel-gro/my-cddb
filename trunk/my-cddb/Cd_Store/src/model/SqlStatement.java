package model;

public class SqlStatement
{
	public enum QueryType {INSERT_SINGLE, INSERT_BULK, QUERY}	
	
	private QueryType queryType;
	private String stmt;
	private String[][] tuples;
	private int requestId;
	
	public SqlStatement(QueryType queryType, String stmt, String[][] tuples, int requestId) {
		this.queryType = queryType;
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
