package model;

public class Result
{
	private int id;
	private String[][] resultsTable;
	
	public Result(int id, String[][] resultsTable)
	{
		this.id = id;
		this.resultsTable = resultsTable;
	}

	public synchronized int getId()
	{
		return id;
	}

	public synchronized String[][] getResultsTable()
	{
		return resultsTable;
	}
}
