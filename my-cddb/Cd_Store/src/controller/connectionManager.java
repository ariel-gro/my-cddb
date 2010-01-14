package controller;

import java.util.PriorityQueue;

import model.SqlStatement;

public class connectionManager {
	
	private DbConnector[] connections;
	private Thread[] connThreads;
	private static PriorityQueue<SqlStatement> theQueue = null;
	private static int requestId = 0;
	
	public static synchronized void insertToQueue (SqlStatement sqlStatment){
		if (theQueue == null)
			theQueue = new PriorityQueue<SqlStatement>();
		
		theQueue.add(sqlStatment);
	}

	//TODO: handle threads, handle queue extraction
}
