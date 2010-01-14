package controller;
import java.sql.*;

import model.DbConfiguration;


public class DbConnector {

	private Connection connection;	// DB connection

	private String query;
	private PreparedStatement ps;


	public DbConnector(String srv, int port, String db, String user, String passwd) {
		DbConfiguration.setIpAddress(srv);
		DbConfiguration.setPort(port);
		DbConfiguration.setDb(db);
		DbConfiguration.setUser(user);
		DbConfiguration.setPassword(passwd);		
		this.connection = null;
	}

	/**
	 * default constructor
	 */
	public DbConnector() {
		new DbConnector("@localhost", 1555, "csodb","hr_readonly","tiger");
		//TODO: change port to 1521 (default), username and password
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

	/**
	 * Execute the given INSERT query and returns LAST_INSERTED_ID. Just pass
	 * one query per call to this method because it will only return the ID of
	 * the first query anyway.
	 * @param query
	 * @return ID
	 * @deprecated
	 */
	public int insert(String query) {
		try {
			Statement stmt = connection.createStatement();

			// Execute the insert statement
			stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			stmt.close(); // Do this or it will _consume_ memory, lots of it!
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			System.err.println("ERROR insert: "+e);
		}
		return 0;
	}

	/**
	 * Execute the given SELECT query and returns a ResultSet based on the
	 * query.
	 * @param query
	 * @return ResultSet
	 * @deprecated
	 */
	public ResultSet select(String query) {
		ResultSet rs = null;
		try {
			// Create a result set containing all data from my_table
			Statement stmt = connection.createStatement();
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			System.err.println("ERROR: " + e);
		}
		return rs;
	}

	/**
	 * Wrapper for the ResultSet.setInt method.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param x the parameter value
	 * @throws SQLException if a database access error occurs
	 * @throws NullPointerException if the ResultSet is null
	 */
	public void setInt(int parameterIndex, int x) throws SQLException, NullPointerException {
		ps.setInt(parameterIndex, x);
	}

	/**
	 * Wrapper for the ResultSet.setString method.
	 *
	 * @param parameterIndex the first parameter is 1, the second is 2, ...
	 * @param x the parameter value
	 * @throws SQLException if a database access error occurs
	 * @throws NullPointerException if the ResultSet is null
	 */
	public void setString(int parameterIndex, String x) throws SQLException, NullPointerException {
		ps.setString(parameterIndex, x);
	}

	/**
	 * Set the query to be executed next.
	 *
	 * @param que Query to be used in a prepared statement
	 */
	public void setQuery(String que) {
		setQuery(que, false);
	}

	/**
	 * Set the query to be executed next.
	 *
	 * @param que Query to be used in a prepared statement
	 * @param requestKeys Used for inserts to get generated keys
	 */
	public void setQuery(String que, boolean requestKeys) {
		/*try {
			if (ps.isClosed()) {
			}else{
				ps.clearParameters();
				ps.close();
			}
		} catch (SQLException e1) {
			System.err.println("ERROR: (setQuery) unable to clear parameters. " + e1);
		} catch (NullPointerException e1) {
			// Ignore
		}*/
		this.query = new String(que); // Copy the string (future thread safety)

		try {
			if (requestKeys) {
				this.ps = connection.prepareStatement(this.query, PreparedStatement.RETURN_GENERATED_KEYS);
			}else{
				this.ps = connection.prepareStatement(this.query);
			}
		} catch (SQLException e) {
			System.err.println("ERROR: (setQuery) unable to create prepared statement. " + e);
		}
	}

	/**
	 * Get next row in ResultSet.
	 * @param rs The ResultSet to be advanced
	 * @return The same ResultSet as the one passed as argument but advanced
	 * to the next row in the set or null if no more rows was found in the set.
	 */
	public ResultSet fetch_row(ResultSet rs) {
		try {
			// Fetch next row from the result set
			if (rs.next()) {
				return rs;
			}else{
				return null;
			}
		} catch (SQLException e) {
			System.err.println("ERROR: (fetch_row) " + e);
		}
		return rs;
	}
}
