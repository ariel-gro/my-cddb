package controller;
import java.sql.*;

import view.views.View;


import model.Result;
import model.ResultsQueue;
import model.SqlStatement;
import model.SqlStatement.QueryType;


public class DbConnector implements Runnable{

	private Connection connection;	// DB connection
	private SqlStatement stmt;

	private PreparedStatement ps;


	public DbConnector(Connection connection, SqlStatement statement) {		
		this.connection = connection;
		this.stmt = statement;
		this.ps = null;
	}

	public synchronized void setConnection(Connection connection) {
		this.connection = connection;
	}

	public synchronized void setStatement(SqlStatement stmt){
		this.stmt = stmt;
	}

	public synchronized void run(){

		QueryType qt = stmt.getQueryType();
		ResultSet resultSet = null;

		System.out.println("DbConnector: reading stmt type: " + qt.toString());

		switch (qt){
		case INSERT_BULK:
			if (executeBulkInsert(stmt) == PreparedStatement.EXECUTE_FAILED) {
				View.displayErroMessage("Error during bulk insert");
			}
			break;
		case INSERT_SINGLE:
			if (executeSingleInsert(stmt) == PreparedStatement.EXECUTE_FAILED) {
				View.displayErroMessage("Error during single insert");
			}
			return;
		case QUERY:
			resultSet = executeQuery(stmt);
			if (resultSet == null) {
				View.displayErroMessage("DB access error occurred while executing a query.\n\n");
			}
			else {
				Result result = new Result(this.stmt.getRequestId(), resultSet);
				ResultsQueue.addResult(result);
			}
			break;
		}

		giveBackConnection();

	}

	private synchronized void giveBackConnection() {
		connectionManager.insertToConnectionQueue(this.connection);
		this.connection = null;
	}


	private synchronized ResultSet executeQuery(SqlStatement stmt) {
		try
		{
			System.out.println("DbConnector: executing query of type:" + stmt.getQueryType().toString());
			ps = connection.prepareStatement(stmt.getStmt());
			return ps.executeQuery();
		}
		catch (SQLException e)
		{
			System.out.println(e.toString());
			return null;
		}
	}

	private synchronized int executeSingleInsert(SqlStatement stmt) {
		try
		{
			System.out.println("DbConnector: executing single isert of type:" + stmt.getQueryType().toString());
			ps = connection.prepareStatement(stmt.getStmt());
			return ps.executeUpdate();
		}
		catch (SQLException e)
		{
			return PreparedStatement.EXECUTE_FAILED;
		}
	}

	private synchronized int executeBulkInsert(SqlStatement stmt) {
		try {
			System.out.println("DbConnector: executing bulk single isert of type:" + stmt.getQueryType().toString());

			ps = connection.prepareStatement(stmt.getStmt());

			int tupleSize, bulkSize = stmt.getTuples().length;
			for(int row=0; row<bulkSize; row++) {
				String[] currentTuple = stmt.getTuples()[row];
				tupleSize = currentTuple.length;
				for (int col=0; col<tupleSize; col++) {
					ps.setString(col+1, currentTuple[col]);
				}
				ps.addBatch();
			}

			int[] returnedVals = ps.executeBatch();
			for (int i=0; i<returnedVals.length; i++) {
				if (returnedVals[i] == PreparedStatement.EXECUTE_FAILED)
					return PreparedStatement.EXECUTE_FAILED;
			}
			return 1;
		}
		catch (SQLException e) {
			System.out.println(e.toString());
			return PreparedStatement.EXECUTE_FAILED;
		}	
	}

//	unit test code
//	public static void main(String[] args) {
//		Connection con = null;
//		Thread t = null;
//		try
//		{
//			// loading the driver
//
//			Class.forName("oracle.jdbc.OracleDriver");
//
//
//			System.out.println("driver loaded successfully");
//
//			// creating the connection
//
//			String jdbcURL =
//				"jdbc:oracle:thin:@127.0.0.1:1521/XE";
//
//			con = DriverManager.getConnection(jdbcURL,"system", "12341234");
//
//			System.out.println("connected to DB");
//
//			SqlStatement s1 = new SqlStatement(QueryType.INSERT_SINGLE, "INSERT INTO DEMO(FNAME, LNAME, ID) VALUES('mmm', 'nnn', 'ooo')", null, 1);
//			String[][] tup = {{"aaa", "bbb", "ccc"}, {"hhh", "iii", "kkk"}, {"xxx", "yyy", "zzz"}};
//			SqlStatement s2 = new SqlStatement(QueryType.INSERT_BULK, 
//					"INSERT INTO DEMO(FNAME, LNAME, ID) VALUES(?, ?, ?)"
//					, tup, 42); 
//			SqlStatement s3 = new SqlStatement(QueryType.QUERY, "SELECT * FROM DEMO", null, 1);
//
//			Statement s	= con.createStatement();
//			s.executeUpdate("DELETE FROM DEMO");
//
//			t = new Thread(new DbConnector(con, s1));
//			t.start();
//			t.join();
//
//			t = new Thread(new DbConnector(con, s2));
//			t.start();
//			t.join();
//
//			t = new Thread(new DbConnector(con, s3));
//			t.start();
//			t.join();
//
//
//			ResultSet r = s.executeQuery("SELECT * FROM DEMO");
//
//			System.out.println("row: " + r.getRow());
//			r.next();
//			while (!r.isAfterLast()) {
//				System.out.println("row: " + r.getRow());
//				System.out.println(r.getString(1));
//				System.out.println(r.getString(2));
//				System.out.println(r.getString(3));
//				r.next();
//			}
//
//			s.close();
//		}
//
//		catch (InterruptedException e) {
//			System.out.println(e.getMessage());
//		}
//
//
//		catch (ClassNotFoundException e)
//		{
//			System.out.println("Unable to load the Oracle JDBC driver");
//		}
//		catch (SQLException e) {
//			System.out.println(e.getMessage());
//		}
//		finally {
//			try{
//				con.close();
//				System.out.println("connection closed");
//			}catch (SQLException e) {
//				System.out.println(e.getMessage());
//			}
//		}
//	}
}
