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
		
		//close ps and give connection back to connectionManager
		try {
			if (!this.ps.isClosed())
				this.ps.close();
		}
		catch (SQLException e) {
			View.displayErroMessage("DB access error occurred while closing a statement.\n\n"+
					e.getMessage());
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
			ps = connection.prepareStatement(stmt.getStmt());
			return ps.executeQuery();
		}
		catch (SQLException e)
		{
			return null;
		}
	}

	private synchronized int executeSingleInsert(SqlStatement stmt) {
		try
		{
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
			ps = connection.prepareStatement(stmt.getStmt());
			
			int tupleSize, bulkSize = stmt.getTuples().length;
			for(int row=0; row<bulkSize; row++) {
				String[] currentTuple = stmt.getTuples()[row];
				tupleSize = currentTuple.length;
				for (int col=0; col<tupleSize; col++) {
					ps.setString(col, currentTuple[col]);
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
			return PreparedStatement.EXECUTE_FAILED;
		}	
	}
}
