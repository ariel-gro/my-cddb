package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.*;

import view.views.View;

import model.DbConfiguration;
import model.Result;
import model.ResultsQueue;
import model.SqlStatement;
import model.SqlStatement.QueryType;

public class connectionManager implements Runnable{

	private static Executor connThreads = null;
	private static final int numOfThreads = 10;
	private static LinkedBlockingQueue<Connection> connQueue = null;
	private static Vector<Connection> conVector = null;
	private static LinkedBlockingQueue<SqlStatement> queryQueue = null;
	private static int numOfConnections = 0;
	
	private static boolean timeToQuit = false;

	public static synchronized void insertToQueryQueue (SqlStatement sqlStatment){
		if (queryQueue == null)
			queryQueue = new LinkedBlockingQueue<SqlStatement>();
		
		try {
			System.out.println("Really Inserting sqlStmt to queue");
			queryQueue.put(sqlStatment);
		} catch (InterruptedException e) {
			View.displayErroMessage("Error during connection maneger initialization:\n"+
					e.getMessage());
			timeToQuit = true;
			return;
		}
	}
		
	public static synchronized void insertToConnectionQueue (Connection con){
		if (connQueue == null)
			connQueue = new LinkedBlockingQueue<Connection>();
		
		try {
			connQueue.put(con);
		} catch (InterruptedException e) {
			View.displayErroMessage("Error during connection maneger initialization:\n"+
					e.getMessage());
			timeToQuit = true;
			return;
		}
	}
	
	private static void insertToConVector(int index, Connection con) {
		if (conVector == null)
			conVector = new Vector<Connection>(numOfThreads);
		
		conVector.add(index, con);
	}

	public synchronized void run() {
	
		System.out.println("connectionManager: Starting run() of connectionManager");
		//initialize class fields
		if (connThreads == null)
			connThreads = Executors.newFixedThreadPool(numOfThreads);

		System.out.println("connectionManager: after connThreads");
		
		if (queryQueue == null)
			queryQueue = new LinkedBlockingQueue<SqlStatement>();

		System.out.println("connectionManager: after queryQueue");
		
		if (connQueue == null){
			
			System.out.println("connectionManager: inside conQueue if");
			connQueue = new LinkedBlockingQueue<Connection>();
			conVector = new Vector<Connection>(numOfThreads);
			System.out.println("connectionManager: inside conQueue if after creating LinkedBlockingqueue and convector");
			
			boolean initConOpened = openConnection();
			if (initConOpened == false) {
				System.out.println("connectionManager: Exiting because conection failed");
				//initial connection to DB failed => kill thread
				return;
			}
		}
		
		int connectionReTries = 5;
		System.out.println("connectionManager: Before while of connectionManager");
		while (!timeToQuit){
			try {
				if ((numOfConnections < numOfThreads) && connQueue.isEmpty() && (connectionReTries > 0))
				{
					System.out.println("connectionManager: got inot first if");
					boolean conOpened = openConnection();
					if (conOpened == false) {
						connectionReTries--;
						continue;
					}
					else {
						connectionReTries = 5;
					}
				}
				System.out.println("connectionManager: taking stmt and connection from queues ");
				DbConnector con = new DbConnector(connQueue.take(), queryQueue.take());				
				connThreads.execute(con);
			}
			catch (InterruptedException e) {
//				View.displayErroMessage("Connection manager was intrrupted.\n"
//						+e.getMessage());
//				return;
			}
		}
		
		//close all connections to DB
		for (int i=0; i < numOfConnections; i++) {
			try {
				if (conVector.elementAt(i).isClosed() == false)
					conVector.elementAt(i).close();
				System.out.println("connection #"+i+"closed");
			}
			catch (SQLException e) {
				View.displayErroMessage("Couldn't close connection #"+i+" of "+numOfConnections+
						" to SQL-server.\n"+ e.getMessage());
			}
		}
	}
	
	public static synchronized void quit(){
		timeToQuit = true;
	}
	
	private static synchronized boolean openConnection()
	{
		// loading the driver
		try
		{
			System.out.println("connectionManager: inside openConnection");
			Class.forName("oracle.jdbc.OracleDriver");
		}
		catch (ClassNotFoundException e)
		{
			View.displayErroMessage("Unable to load the Oracle JDBC driver");
			timeToQuit = true;
			return false;
		}
		
		// creating the connection
		try
		{
			String jdbcURL =
				"jdbc:oracle:thin:@" + DbConfiguration.getIpAddress()+":" + DbConfiguration.getPort() +
				"/" + DbConfiguration.getDb();
			
			System.out.println("connectionManager: connecting...");
			Connection connection =
				DriverManager.getConnection(jdbcURL,
					DbConfiguration.getUser(), DbConfiguration.getPassword());
			
			System.out.println("connectionManager: inseting connection to queue");
			insertToConnectionQueue(connection);
			System.out.println("connectionManager: inseting connection to vecotr");
			insertToConVector(numOfConnections, connection);
			numOfConnections++;
		}
		catch (SQLException e)
		{
			View.displayErroMessage("An error occured while trying to connect to the DB.\n\n"+e.getMessage());
			timeToQuit = true;
			return false;
		}
		System.out.println("connected to DB");
		return true;
	}
	
//	for testing purposes only
//
//	public static void main (String[] args) {
//		
//		String create = "CREATE table DEMO (FNAME VARCHAR2(4000) NOT NULL, LNAME VARCHAR2(4000) NOT NULL, ID VARCHAR2(4000) NOT NULL, constraint DEMO_PK primary key (ID))";
//		SqlStatement s0 = new SqlStatement(QueryType.INSERT_SINGLE, create, null, 69);
//		SqlStatement s1 = new SqlStatement(QueryType.INSERT_SINGLE, "INSERT INTO DEMO(FNAME, LNAME, ID) VALUES('mmm', 'nnn', 'ooo')", null, 1);
//		String[][] tup = {{"aaa", "bbb", "ccc"}, {"hhh", "iii", "kkk"}, {"xxx", "yyy", "zzz"}};
//		SqlStatement s2 = new SqlStatement(QueryType.INSERT_BULK, 
//				"INSERT INTO DEMO(FNAME, LNAME, ID) VALUES(?, ?, ?)"
//				, tup, 42);
//		SqlStatement s3 = new SqlStatement(QueryType.QUERY, "SELECT * FROM DEMO", null, 1);
//		SqlStatement s4 = new SqlStatement(QueryType.INSERT_SINGLE, "DELETE FROM DEMO", null, 101);
//		SqlStatement s5 = new SqlStatement(QueryType.INSERT_SINGLE, "DROP TABLE DEMO", null, 101);
//		
//		insertToQueryQueue(s0);
//		insertToQueryQueue(s1);
//		insertToQueryQueue(s2);
//		
//		connectionManager cm = new connectionManager();
//		
//		Thread t = new Thread(cm);
//		t.start();
//		
//		if (t.isAlive() == false) {
//			//in case I change a variable's value such that the cm returns as if there was an error
//			cm = new connectionManager();
//			t = new Thread(cm);
//			t.start();
//		}
//		
//		try {
//			Thread.sleep(5000);
//			insertToQueryQueue(s3);
//			insertToQueryQueue(s3);
//			insertToQueryQueue(s3);
//			insertToQueryQueue(s3);
//			insertToQueryQueue(s3);
//			insertToQueryQueue(s3);
//			insertToQueryQueue(s3);
//			
//			Result res = ResultsQueue.getResult();
//			ResultSet r = res.getResultSet();
//			System.out.println("row: " + r.getRow());
//			r.next();
//			while (!r.isAfterLast()) {
//				System.out.println("row: " + r.getRow());
//				System.out.println(r.getString(1));
//				System.out.println(r.getString(2));
//				System.out.println(r.getString(3));
//				r.next();
//			}
//			insertToQueryQueue(s4);
//			insertToQueryQueue(s5);
//			Thread.sleep(5000);
//			cm.quit();
//			t.interrupt();
//			Thread.sleep(6000);
//			t.join(5000);
//		}
//		catch (InterruptedException e) {
//			System.out.println(e.toString());
//		}
//		catch (SQLException e) {
//			System.out.println(e.toString());
//		}
//	}
}
