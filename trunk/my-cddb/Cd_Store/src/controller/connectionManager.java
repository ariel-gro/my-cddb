package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.*;


import model.DbConfiguration;
import model.SqlStatement;

public class connectionManager {

	private Executor connThreads;
	private LinkedBlockingQueue<Connection> connQueue;
	private Vector<Connection> conVector;
	private static LinkedBlockingQueue<SqlStatement> queryQueue = null;
	private static LinkedBlockingQueue<ResultSet> resultQueue = null;
	private int numOfConnections = 0;
	
	private boolean timeToQuit = false;

	public static synchronized void insertToQueue (SqlStatement sqlStatment){
		if (queryQueue == null)
			queryQueue = new LinkedBlockingQueue<SqlStatement>();
		
		try {
			queryQueue.put(sqlStatment);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized void insertToResultQueue (ResultSet result){
		if (resultQueue == null)
			resultQueue = new LinkedBlockingQueue<ResultSet>();
		
		try {
			resultQueue.put(result);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public synchronized void start() {
		//initialize class fields
		if (this.connThreads == null)
			this.connThreads = Executors.newFixedThreadPool(10);

		if (queryQueue == null)
			queryQueue = new LinkedBlockingQueue<SqlStatement>();

		if (connQueue == null){
			connQueue = new LinkedBlockingQueue<Connection>();
			conVector = new Vector<Connection>(10);
			this.openConnection();
		}
		
		Thread queryListner = new Thread(new HandleQueryQueue());
		queryListner.start();
		
		while (!timeToQuit){
			//threads are working
		}
		
		//end work and threads
		for (int i=0; i < numOfConnections; i++) {
			try {
				conVector.elementAt(i).close();
			}
			catch (SQLException e) {
				System.err.println("ERROR: Couldn't close connection to SQL-server. " + e);
			}
		}
	}
	
	public synchronized void quit(){
		this.timeToQuit = true;
	}
	
	private synchronized void openConnection()
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
			
			Connection connection =
				DriverManager.getConnection(jdbcurl, DbConfiguration.getUser(), DbConfiguration.getPassword());
			
			this.connQueue.put(connection);
			this.conVector.add(numOfConnections, connection);
			numOfConnections++;
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

	private class HandleQueryQueue implements Runnable {

		public synchronized void run() {
			SqlStatement stmt;
			DbConnector con = new DbConnector();
			while (!timeToQuit){
				try {
					stmt = queryQueue.take();
					if ((numOfConnections < 10) && connQueue.isEmpty())
						openConnection();
					con.setConnection(connQueue.take());
					con.setStatement(stmt);
					connThreads.execute(con);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//TODO: handle threads, handle queue extraction
}
