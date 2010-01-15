package controller;
import java.sql.*;


import model.SqlStatement;
import model.SqlStatement.QueryType;


public class DbConnector implements Runnable{

	private Connection connection;	// DB connection

	private PreparedStatement ps;
	private SqlStatement stmt;


	public DbConnector() {		
		this.connection = null;
		this.stmt = null;
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
		ResultSet result = null;
		
		switch (qt){
		case INSERT_BULK:
			result = executeBulkInsert(stmt);
			break;
		case INSERT_SINGLE:
			result = executeSingleInsert(stmt);
			break;
		case QUERY:
			result = executeQuery(stmt);
			break;
		}
		
		connectionManager.insertToResultQueue(result);
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

	private ResultSet executeSingleInsert(SqlStatement stmt) {
		return null;
		// TODO Auto-generated method stub
		
	}

	private ResultSet executeBulkInsert(SqlStatement stmt) {
		return null;
		// TODO Auto-generated method stub
		
	}
}
