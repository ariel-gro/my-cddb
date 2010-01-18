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

	private static Executor connThreads;
	private static LinkedBlockingQueue<Connection> connQueue;
	private static Vector<Connection> conVector;
	private static LinkedBlockingQueue<SqlStatement> queryQueue = null;
	private static int numOfConnections = 0;
	
	private static boolean timeToQuit = false;

	public static synchronized void insertToQueryQueue (SqlStatement sqlStatment){
		if (queryQueue == null)
			queryQueue = new LinkedBlockingQueue<SqlStatement>();
		
		try {
			queryQueue.put(sqlStatment);
		} catch (InterruptedException e) {
			View.displayErroMessage("Error during connection maneger initialization:\n"+
					e.getMessage());
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
		}
	}

	public synchronized void run() {
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
		
		SqlStatement stmt = null;
		while (!timeToQuit){
			try {
				stmt = queryQueue.take();
				if ((numOfConnections < 10) && connQueue.isEmpty())
					openConnection();
				DbConnector con = new DbConnector(connQueue.take(), stmt);
				connThreads.execute(con);
			}
			catch (InterruptedException e) {
				View.displayErroMessage("Connection manager was intrrupted.\n"
						+e.getMessage());
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
	
	private static synchronized void openConnection()
	{

		// loading the driver
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
		}
		catch (ClassNotFoundException e)
		{
			View.displayErroMessage("Unable to load the Oracle JDBC driver");
		}

		// creating the connection
		try
		{
			String jdbcURL =
				"jdbc:oracle:thin:" + DbConfiguration.getIpAddress()+":" + DbConfiguration.getPort() +
				"/" + DbConfiguration.getDb();
			
			Connection connection =
				DriverManager.getConnection(jdbcURL,
					DbConfiguration.getUser(), DbConfiguration.getPassword());
			
			insertToConnectionQueue(connection);
			conVector.add(numOfConnections, connection);
			numOfConnections++;
		}
		catch (SQLException e)
		{
			View.displayErroMessage("An error occured while trying to connect to the DB.\n\n"+e.getMessage());
		}
	}
}
