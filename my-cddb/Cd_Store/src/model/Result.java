package model;

import java.sql.ResultSet;

public class Result
{
	private int id;
	private ResultSet results;
	
	public Result(int id, ResultSet results)
	{
		this.id = id;
		this.results = results;
	}

	public synchronized int getId()
	{
		return id;
	}

	public synchronized ResultSet getResultSet()
	{
		return results;
	}
}
