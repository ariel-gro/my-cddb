package controller;
import java.sql.*;
import java.text.DecimalFormat;

import view.views.View;


import model.RequestToQueryHandler;
import model.Result;
import model.ResultsQueue;
import model.SqlStatement;
import model.RequestToQueryHandler.MapType;
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

	public synchronized Connection getConnection() {
		return this.connection;
	}
	
	public synchronized void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	public synchronized void setStatement(SqlStatement stmt){
		this.stmt = stmt;
	}

	public synchronized void run(){

		QueryType qt = stmt.getQueryType();

		System.out.println("DbConnector: reading stmt type: " + qt.toString());

		switch (qt){
		case INSERT_BULK:
			if (executeBulkInsert(stmt) == PreparedStatement.EXECUTE_FAILED) {
				View.displayErroMessage("Error during bulk insert.\nPlease try to reconnect to DB\n\n");
			}
			break;
		case INSERT_SINGLE:
			if (executeSingleInsert(stmt) == PreparedStatement.EXECUTE_FAILED) {
				View.displayErroMessage("Error during single insert.\nPlease try to reconnect to DB\n\n");
			}
			return;
		case QUERY:
			ResultSet resultSet = executeQuery(stmt);
			if (resultSet == null) {
				View.displayErroMessage("DB access error occurred while executing a query.\n" +
						"Please try to reconnect to DB\n\n");
			}
			else {
				Result result = new Result(this.stmt.getRequestId(), resultSet);
				ResultsQueue.addResult(result);
			}
			break;
		}

		giveBackConnection();
		return;
	}

	private synchronized void giveBackConnection() {
		connectionManager.insertToConnectionQueue(this.connection);
		this.connection = null;
	}


	private synchronized ResultSet executeQuery(SqlStatement stmt) {
		try
		{
			System.out.println("DbConnector: executing query of type:" + stmt.getQueryType().toString());
			System.out.println("DbConnector: executing query:" + stmt.getStmt());
			ps = connection.prepareStatement(stmt.getStmt(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
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
			MapType mapType = stmt.getMapType();
			int bulkSize = stmt.getTuples().length;
			DecimalFormat df = new DecimalFormat("####.00");
			
			switch (mapType) {
			case ALBUMS:
				for(int row=0; row<bulkSize; row++) {
					try
					{
						String[] currentTuple = stmt.getTuples()[row];
						ps.setLong(1, Long.parseLong(currentTuple[0]));		//DISCID
						ps.setLong(2, Long.parseLong(currentTuple[1]));		//ARTISTID
						ps.setString(3, currentTuple[2]);					//TITLE
						ps.setLong(4, Long.parseLong(currentTuple[3]));		//YEAR
						ps.setLong(5, Long.parseLong(currentTuple[4]));		//GENRE
						ps.setLong(6, Long.parseLong(currentTuple[5]==null?"0":currentTuple[5])); 	//TOTALTIME
						ps.setFloat(7, Float.parseFloat(df.format((5 + Math.random() * 10)))); 	//PRICE
						ps.addBatch();
					}
					catch (Exception e) {}
				}
				break;
			case ARTISTS:
				for(int row=0; row<bulkSize; row++) {
					String[] currentTuple = stmt.getTuples()[row];
					ps.setLong(2, Long.parseLong(currentTuple[1]));	//NAME
					ps.setString(1, currentTuple[0]);				//ARTISTID
					ps.addBatch();
				}
				break;
			case GENRES:
				for(int row=0; row<bulkSize; row++) {
					String[] currentTuple = stmt.getTuples()[row];
					ps.setLong(2, Long.parseLong(currentTuple[1]));	//GENRE
					ps.setString(1, currentTuple[0]);				//GENREID
					ps.addBatch();
				}
				break;
			case TRACKS:
				for(int row=0; row<bulkSize; row++) {
					String[] currentTuple = stmt.getTuples()[row];
					ps.setLong(1, Long.parseLong(currentTuple[0]));		//TRACKID
					ps.setLong(2, Long.parseLong(currentTuple[1]));		//DISCID
					ps.setString(3, currentTuple[2]);					//TRACKTITLE
					ps.setLong(4,Long.parseLong( currentTuple[3]==null?"0":currentTuple[3]));	//NUM
					ps.addBatch();
				}
				break;
			default:
				//all info is Strings, for testing purposes
				for(int row=0; row<bulkSize; row++) {
					String[] currentTuple = stmt.getTuples()[row];
					int tupleSize = currentTuple.length;
					for (int col=0; col<tupleSize; col++) {
						ps.setString(col+1, currentTuple[col]);
					}
					ps.addBatch();
				}
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
//	
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
//			String jdbcURLatTAU =
//				"jdbc:oracle:thin:@nova.cs.tau.ac.il:1555/csodb";
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
//			s.executeUpdate("CREATE table DEMO (" +
//					"FNAME VARCHAR2(4000) NOT NULL, " +
//					"LNAME VARCHAR2(4000) NOT NULL, " +
//					"ID VARCHAR2(4000) NOT NULL, " +
//					"constraint DEMO_PK primary key (ID))");
//			
//			System.out.println("Table created");
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
//			s.executeUpdate("DELETE FROM DEMO");
//			System.out.println("Table deleted");
//			s.execute("DROP TABLE DEMO");
//			System.out.println("Table dropped");
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
