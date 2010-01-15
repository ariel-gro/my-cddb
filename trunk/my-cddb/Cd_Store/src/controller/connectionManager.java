package controller;

import java.sql.ResultSet;
import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import model.SqlStatement;

public class connectionManager implements Runnable{
	
	private ExecutorService connThreads;
	private int threadNum;
	private static LinkedBlockingQueue<SqlStatement> theQueue = null;
	
	public static synchronized void insertToQueue (SqlStatement sqlStatment){
		if (theQueue == null)
			theQueue = new LinkedBlockingQueue<SqlStatement>();
		try {
			theQueue.put(sqlStatment);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		//initialize class fields
		if (this.connThreads == null){
			this.connThreads = Executors.newCachedThreadPool();
			this.threadNum = 0;
		}
				
		//main loop
		SqlStatement stmt;
		while (true){
			try {
				stmt = theQueue.take();				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	

	//TODO: handle threads, handle queue extraction
}
