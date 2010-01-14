package model;

import java.util.Hashtable;

public class SqlStatement
{
	public enum SearchType {INSERT_SINGLE, INSERT_BULK, QUERY}	
	
	private SearchType sType;
	private String stmt;
	private Hashtable<String,String[]> tuples;
	
	public SqlStatement(SearchType sType, String stmt, Hashtable<String, String[]> tuples) {
		this.sType = sType;
		this.stmt = stmt;
		this.tuples = tuples;
	}

	public SearchType getsType() {
		return sType;
	}

	public void setsType(SearchType sType) {
		this.sType = sType;
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
	
	
}
