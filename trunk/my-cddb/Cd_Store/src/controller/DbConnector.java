package controller;
import java.sql.*;

public class DbConnector {
	
	/** Private representation of the URL to the server. */
	private String server = new String();
	
	/** Private representation of the username, password and database. */
	private String username = new String();
	private String password = new String();
	private String database = new String();

	private String query = new String();
	private PreparedStatement ps;
	
	private Connection connection = null;
	
	/**
	 * Initializes a newly created Mysql object with a new connection to a server.
	 */
	public DbConnector(String srv, String db, String user, String passwd) {
		server = srv;
		username = user;
		password = passwd;
		database = db;
		String jdbcurl = "jdbc:mysql://"+server+":3306/"+database;

		try {
			// Load the JDBC driver
			DriverManager.registerDriver((Driver)Class.forName("com.mysql.jdbc.Driver").newInstance());
        
			// Create a connection to the database
			connection = DriverManager.getConnection(jdbcurl, username, password);
		} catch (ClassNotFoundException e) {
			System.err.println("ERROR: MySQL MM JDBC driver not found. " + e);
		} catch (SQLException e) {
			System.err.println("ERROR: " + e);
		} catch (IllegalAccessException e) {
			System.err.println("ERROR: " + e);
		} catch (InstantiationException e) {
			System.err.println("ERROR: " + e);
		}
	}

	/**
	 * Closes the connection to the MySQL server.
	 *
	 */
	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			System.err.println("ERROR: Couldn't close connection to SQL-server. " + e);
		}
	}
	
	public void closeStatement() {
		try {
			this.ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
			System.err.println("MySQL ERROR: (executeQuery). " + e);
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
			System.err.println("MySQL ERROR: (executeUpdate). " + e);
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
			System.err.println("MySQL ERROR: (getGeneratedKeys). " + e);
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
	    		System.err.println("MySQL ERROR: " + e);
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
			System.err.println("MySQL ERROR: (setQuery) unable to clear parameters. " + e1);
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
			System.err.println("MySQL ERROR: (setQuery) unable to create prepared statement. " + e);
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
	    		System.err.println("MySQL ERROR: (fetch_row) " + e);
	    }
	    return rs;
	}
	
}
