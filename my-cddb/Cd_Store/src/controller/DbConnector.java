package controller;
import java.sql.*;


import model.Result;
import model.ResultsQueue;
import model.SqlStatement;
import model.SqlStatement.QueryType;


public class DbConnector implements Runnable{

	private Connection connection;	// DB connection
	private SqlStatement stmt;
	
	private PreparedStatement ps;
	

	public DbConnector() {		
		this.connection = null;
		this.stmt = null;
		this.ps = null;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public void setStatement(SqlStatement stmt){
		this.stmt = stmt;
	}
	

	/**
	 * Execute the prepared statement.
	 */
	public ResultSet executeQuery() {
		try {
			return ps.executeQuery();
		} catch (SQLException e) {
			System.err.println("ERROR: (executeQuery). " + e);
		}

		return null;
	}

	
	public void run(){
		
		QueryType qt = stmt.getQueryType();
		ResultSet resultSet = null;
		
		switch (qt){
		case INSERT_BULK:
			if (executeBulkInsert(stmt) == -1)
				System.out.println("Error during bulk insert");
			break;
		case INSERT_SINGLE:
			if (executeSingleInsert(stmt) == -1)
				System.out.println("Error during single insert");
			break;
		case QUERY:
			resultSet = executeQuery(stmt);
			break;
		}
		
		Result result = new Result(this.stmt.getRequestId(), resultSet);
		ResultsQueue.addResult(result);
		connectionManager.insertToConnectionQueue(this.connection);
		this.connection = null;
	}

	private ResultSet executeQuery(SqlStatement stmt) {
		try
		{
			PreparedStatement ps = connection.prepareStatement(stmt.getStmt());
			return ps.executeQuery();
		}
		catch (SQLException e)
		{
			return null;
		}
	}

	private int executeSingleInsert(SqlStatement stmt) {
		try
		{
			PreparedStatement ps = connection.prepareStatement(stmt.getStmt());
			return ps.executeUpdate();
		}
		catch (SQLException e)
		{
			return -1;
		}
	}

	private int executeBulkInsert(SqlStatement stmt) {
		return 0;
		// TODO Auto-generated method stub
		
	}
}
