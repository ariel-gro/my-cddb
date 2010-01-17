package controller;
import java.sql.*;


import model.Result;
import model.ResultsQueue;
import model.SqlStatement;
import model.SqlStatement.QueryType;


public class DbConnector implements Runnable{

	private Connection connection;	// DB connection
	private SqlStatement stmt;
	
	private PreparedStatement ps;
	

	public DbConnector() {		
		this.connection = null;
		this.stmt = null;
		this.ps = null;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public void setStatement(SqlStatement stmt){
		this.stmt = stmt;
	}
		
	public void run(){
		
		QueryType qt = stmt.getQueryType();
		ResultSet resultSet = null;
		
		switch (qt){
		case INSERT_BULK:
			if (executeBulkInsert(stmt) == PreparedStatement.EXECUTE_FAILED)
				System.out.println("Error during bulk insert");
			break;
		case INSERT_SINGLE:
			if (executeSingleInsert(stmt) == PreparedStatement.EXECUTE_FAILED)
				System.out.println("Error during single insert");
			break;
		case QUERY:
			resultSet = executeQuery(stmt);
			break;
		}
		
		Result result = new Result(this.stmt.getRequestId(), resultSet);
		ResultsQueue.addResult(result);
		connectionManager.insertToConnectionQueue(this.connection);
		this.connection = null;
	}

	private ResultSet executeQuery(SqlStatement stmt) {
		try
		{
			PreparedStatement ps = connection.prepareStatement(stmt.getStmt());
			ResultSet retVal = ps.executeQuery();
			ps.close();
			return retVal;
		}
		catch (SQLException e)
		{
			return null;
		}
	}

	private int executeSingleInsert(SqlStatement stmt) {
		try
		{
			PreparedStatement ps = connection.prepareStatement(stmt.getStmt());
			int retVal = ps.executeUpdate();
			ps.close();
			return retVal;
		}
		catch (SQLException e)
		{
			return PreparedStatement.EXECUTE_FAILED;
		}
	}

	private int executeBulkInsert(SqlStatement stmt) {
		try {
			PreparedStatement ps = connection.prepareStatement(stmt.getStmt());
			
			int bulkSize = stmt.getTuples().length;
			int tupleSize = stmt.getTuples()[0].length;
			for(int row=0; row<bulkSize; row++) {
				String[] currentTuple = stmt.getTuples()[row];
				for (int col=0; col<tupleSize; col++) {
					ps.setString(col, currentTuple[col]);
				}
				ps.addBatch();
			}
			
			int[] returnedVals = ps.executeBatch();
			ps.close();
			for (int i=0; i<returnedVals.length; i++) {
				if (returnedVals[i] == PreparedStatement.EXECUTE_FAILED)
					return PreparedStatement.EXECUTE_FAILED;
			}
			return 1;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return PreparedStatement.EXECUTE_FAILED;
		}	
	}
}
