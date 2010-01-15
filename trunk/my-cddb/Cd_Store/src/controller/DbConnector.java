package controller;
import java.sql.*;
import java.util.concurrent.Callable;


import model.DbConfiguration;
import model.SqlStatement;
import model.SqlStatement.queryType;


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
		
		queryType qt = stmt.getQueryType();
		
		switch (qt){
		case INSERT_BULK:
			executeBulkInsert(stmt);
			break;
		case INSERT_SINGLE:
			executeSingleInsert(stmt);
			break;
		case QUERY:
			executeQuery(stmt);
			break;
		}
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
