package model;

import model.SqlStatement.QueryType;

public class QueryRequest
{
	private int id;
	private QueryType qType; 
	private String query;
	private String[][] attributes = null;
	
	public QueryRequest(int id, QueryType qType, String query, String[][] attributes)
	{
		this.id = id;
		this.qType = qType;
		this.query = query;
		this.attributes = attributes;
	}

	public synchronized int getId()
	{
		return id;
	}

	public synchronized QueryType getqType()
	{
		return qType;
	}

	public synchronized String getQuery()
	{
		return query;
	}

	public synchronized String[][] getAttributes()
	{
		return attributes;
	}
}
