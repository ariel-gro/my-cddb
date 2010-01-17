package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.*;

import model.DbConfiguration;
import model.SqlStatement;

public class connectionManager {

	private static Executor connThreads;
	private static LinkedBlockingQueue<Connection> connQueue;
	private static Vector<Connection> conVector;
	private static LinkedBlockingQueue<SqlStatement> queryQueue = null;
	private static int numOfConnections = 0;
	
	private static boolean timeToQuit = false;

	public static synchronized void insertToQueue (SqlStatement sqlStatment){
		if (queryQueue == null)
			queryQueue = new LinkedBlockingQueue<SqlStatement>();
		
		try {
			queryQueue.put(sqlStatment);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
		
	public static synchronized void insertToConnectionQueue (Connection con){
		if (connQueue == null)
			connQueue = new LinkedBlockingQueue<Connection>();
		
		try {
			connQueue.put(con);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static synchronized void start() {
		//initialize class fields
		if (connThreads == null)
			connThreads = Executors.newFixedThreadPool(10);

		if (queryQueue == null)
			queryQueue = new LinkedBlockingQueue<SqlStatement>();

		if (connQueue == null){
			connQueue = new LinkedBlockingQueue<Connection>();
			conVector = new Vector<Connection>(10);
			openConnection();
		}
		
		SqlStatement stmt;
		while (!timeToQuit){
			try {
				stmt = queryQueue.take();
				if ((numOfConnections < 10) && connQueue.isEmpty())
					openConnection();
				DbConnector con = new DbConnector(connQueue.take(), stmt);
				connThreads.execute(con);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//close all connections to DB
		for (int i=0; i < numOfConnections; i++) {
			try {
				conVector.elementAt(i).close();
			}
			catch (SQLException e) {
				System.err.println("ERROR: Couldn't close connection to SQL-server. " + e);
			}
		}
	}
	
	public static synchronized void quit(){
		timeToQuit = true;
	}
	
	private static synchronized void openConnection()
	{

		// loading the driver
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("Unable to load the Oracle JDBC driver..");
		}
		System.out.println("Driver loaded successfully");


		// creating the connection
		System.out.print("Trying to connect.. ");
		try
		{
			String jdbcurl =
				"jdbc:oracle:thin:" + DbConfiguration.getIpAddress()+":" + DbConfiguration.getPort() +
				"/" + DbConfiguration.getDb();
			
			Connection connection =
				DriverManager.getConnection(jdbcurl, DbConfiguration.getUser(), DbConfiguration.getPassword());
			
			insertToConnectionQueue(connection);
			conVector.add(numOfConnections, connection);
			numOfConnections++;
		}
		catch (SQLException e)
		{
			System.out.println("Unable to connect - " + e.toString());
		}
		catch (Exception e)
		{
			System.err.println("ERROR: " + e);
		}
		System.out.println("Connected!");
	}
}
