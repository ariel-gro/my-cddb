package controller;
import java.sql.*;
import java.util.concurrent.Callable;

import javax.swing.text.StyleContext.SmallAttributeSet;

import model.DbConfiguration;
import model.SqlStatement;
import model.SqlStatement.queryType;


public class DbConnector implements Callable<ResultSet>{

	private Connection connection;	// DB connection

	private PreparedStatement ps;
	private SqlStatement stmt;


	public DbConnector() {		
		this.connection = null;
		this.stmt = null;
	}

	public void setStatement(SqlStatement stmt){
		this.stmt = stmt;
	}
	/**
	 * 
	 * @return the connection (null on error)
	 */
	public void openConnection()
	{

		// loading the driver
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("Unable to load the Oracle JDBC driver..");
			java.lang.System.exit(0); 
		}
		System.out.println("Driver loaded successfully");


		// creating the connection
		System.out.print("Trying to connect.. ");
		try
		{
			String jdbcurl =
				"jdbc:oracle:thin:" + DbConfiguration.getIpAddress()+":" + DbConfiguration.getPort() +
				"/" + DbConfiguration.getDb();
			
			connection =
				DriverManager.getConnection(jdbcurl, DbConfiguration.getUser(), DbConfiguration.getPassword());
		}
		catch (SQLException e)
		{
			System.out.println("Unable to connect - " + e.toString());
			java.lang.System.exit(0); 
		}
		catch (Exception e)
		{
			System.err.println("ERROR: " + e);
		}
		System.out.println("Connected!");

	}

	/**
	 * Closes the connection to the server.
	 */
	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			System.err.println("ERROR: Couldn't close connection to SQL-server. " + e);
		}
		System.out.println("connection closed successfully");
	}
	
	//TODO: create user method
	

	public void closeStatement() {
		try {
			this.ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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

	/**
	 * Execute the prepared statement. Must be an SQL INSERT, UPDATE or DELETE
	 * statement; or an SQL statement that returns nothing, such as a DDL statement.
	 */
	public int executeUpdate() {
		try {
			return ps.executeUpdate();
		} catch (SQLException e) {
			System.err.println("ERROR: (executeUpdate). " + e);
		}

		return -1;
	}

	/**
	 * Wrapper for the PreparedStatement.getGeneratedKeys() method.
	 */
	public ResultSet getGeneratedKeys() {
		try {
			return ps.getGeneratedKeys();
		} catch (SQLException e) {
			System.err.println("ERROR: (getGeneratedKeys). " + e);
		}

		return null;
	}
	
	public ResultSet call(){
		
		queryType qt = stmt.getQueryType();
		
		switch (qt){
		case INSERT_BULK:
			return executeBulkInsert(stmt);
		case INSERT_SINGLE:
			return executeSingleInsert(stmt);
		case QUERY:
			return executeQuery(stmt);
		default:
			return null;
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
