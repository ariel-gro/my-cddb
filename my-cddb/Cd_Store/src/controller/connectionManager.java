package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.*;

import view.views.View;

import model.DbConfiguration;
import model.SqlStatement;

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
			
			if (openConnection() == false) {
				System.out.println("connectionManager: Exiting because conection failed");
				//initial connection to DB failed => kill thread
				return;
			}
		}
		
		SqlStatement stmt = null;
		int connectionReTries = 5;
		System.out.println("connectionManager: Before while of connectionManager");
		while (!timeToQuit){
			try {
				if ((numOfConnections < numOfThreads) && connQueue.isEmpty() && (connectionReTries > 0))
				{
					System.out.println("connectionManager: got inot first if");
					if (openConnection() == false) {
						connectionReTries--;
						break;
					}
					else {
						connectionReTries = 5;
					}
				}
				stmt = queryQueue.take();
				System.out.println("connectionManager: taking stmt from queue: " + stmt.getQueryType().toString());
				DbConnector con = new DbConnector(connQueue.take(), stmt);				
				connThreads.execute(con);
			}
			catch (InterruptedException e) {
				View.displayErroMessage("Connection manager was intrrupted.\n"
						+e.getMessage());
				return;
			}
		}
		
		//close all connections to DB
		for (int i=0; i < numOfConnections; i++) {
			try {
				conVector.elementAt(i).close();
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
//		// loading the driver
//		Class cc = null;
//		Connection connection = null;
//		try
//		{
//			cc = Class.forName("oracle.jdbc.OracleDriver");
//		}
//		catch (ClassNotFoundException e)
//		{
//			System.out.println("Unable to load the Oracle JDBC driver");
//			timeToQuit = true;
//		}
//		
//		System.out.println(cc.toString());
//
//		// creating the connection
//		boolean closed;
//		try
//		{
//			connection =
//				DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE","system","12341234");
//			
//			closed = connection.isClosed();
//			System.out.println(connection.isClosed());
//			insertToConnectionQueue(connection);
//			insertToConVector(numOfConnections, connection);
//			numOfConnections++;
//			if (!connection.isClosed())
//				connection.close();
//			closed = connection.isClosed();
//			System.out.println(closed);
//		}
//		catch (SQLException e)
//		{
//			System.out.println("An error occured while trying to connect to the DB.\n\n"+e.getMessage());
//			timeToQuit = true;
//		}
//		
//	}
}
