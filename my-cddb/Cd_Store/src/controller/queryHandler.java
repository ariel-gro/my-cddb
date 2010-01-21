package controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import view.views.View;

import model.RequestToQueryHandler;
import model.Result;
import model.ResultsQueue;
import model.SearchesPriorityQueue;
import model.SqlStatement;
import model.TableViewsMap;
import model.advanceSearchFieldValueBundle;
import model.SqlStatement.QueryType;



public class queryHandler implements Runnable
{

	private static boolean timeToQuit = false;
	private ExecutorService threadPool = Executors.newCachedThreadPool();;

	public synchronized void run() {

		while (!timeToQuit) {
			if (!SearchesPriorityQueue.isEmpty()) {
				RequestToQueryHandler req = SearchesPriorityQueue.getSearch();
				HandleQuery q = new HandleQuery(req);
				this.threadPool.execute(q);
			}
		}

		this.threadPool.shutdownNow();
	}

	public static synchronized void quit() {
		timeToQuit = true;
	}

	private synchronized String UniqueID() {
		Long current= System.currentTimeMillis();
		return current.toString();
	}

	//this method checks if tables exist i the DB. if not - creates all tables.
	public synchronized void createTables(){
		SqlStatement sqlStmt = new SqlStatement(null, "SELECT table_name FROM all_tables ORDER BY table_name", null, 0);
		connectionManager.insertToQueryQueue(sqlStmt);
		boolean waitforanswer = false;
		Result myResult = null;
		while (!waitforanswer) {
			myResult = ResultsQueue.peek();
			//this should mean that there are no tables in the DB. but it might also mean we didn't get a result yet?
			if ((myResult.getId() == 0) && (myResult == null)) { 
				myResult = ResultsQueue.getResult();
				//need to check the create succeeded? if yes - i'll change the code.
				SqlStatement[] create_stmt = new SqlStatement[6];
				create_stmt[0] = new SqlStatement(null, "CREATE TABLE Albums(DiscId BIGINT, ArtistId INT, " +
						"Title VARCHAR(50), Year SMALLINT, Genre VARCHAR(50), TotalTime SMALLINT, Price FLOAT)", null, 0);
				create_stmt[1] = new SqlStatement(null, "CREATE TABLE Tracks(TrackId INT, DiscID BIGINT, " +
						"Number TINYINT, TrackTitle VARCHAR(50))", null, 0);
				create_stmt[2] = new SqlStatement(null, "CREATE TABLE Artists(Name VARCHAR(50), ArtistId INT)", null, 0);
				create_stmt[3] = new SqlStatement(null, "CREATE TABLE Genres(Genre VARCHAR(50), GenreId INT)", null, 0);
				create_stmt[4] = new SqlStatement(null, "CREATE TABLE Users(UserId INT, UserName VARCHAR(20), Password VARCHAR(20))", null, 0);
				create_stmt[5] = new SqlStatement(null, "CREATE TABLE Sales(OrderId INT, UserId INT, DiscId BIGINT)", null, 0);
				for (int i=0;i<6;i++) {
					connectionManager.insertToQueryQueue(create_stmt[i]);
				}
				waitforanswer = true;
			}
			
		}
		

	}

	private class HandleQuery implements Runnable {

		private RequestToQueryHandler searchReq;
		private SqlStatement sqlStmt;
		private HashMap<String,String[]> map;
		private int sizeOfBulk = 50000;
		private String[][] attributes;

		protected HandleQuery(RequestToQueryHandler searchReq) {
			this.searchReq = searchReq;
		}

		public synchronized void run() {
			this.createQuery();
			if (this.searchReq.getTheQueryType() != QueryType.QUERY) {
				//we are not expecting any results back so quit
				return;
			}
			
			//we are expecting results so sit back and wait for them
			Result myResult = null;
			while (!timeToQuit) {
				myResult = ResultsQueue.peek();
				if ((myResult != null) && (myResult.getId() == this.searchReq.getId())) {
					//ResultsQueue has a result and it's ours!!
					
					myResult = ResultsQueue.getResult();
					ResultSet rs = myResult.getResultSet();
					String[][] table = resultSetInto2DStringArray(rs);
					if (table != null) {
						TableViewsMap.addTable(this.searchReq.getId(), table);
						return;
					}
					else {
						//an error occurred
						return;
					}
				}
			}
		}

		private synchronized void createQuery()
		{
			String query = "";
			if (searchReq.getTheQueryType() == SqlStatement.QueryType.QUERY)
			{
				switch (searchReq.getSearchType()) {

				case TOP_10:
					switch (searchReq.getTop10Type()) {
					case LATEST:
						if (searchReq.getMusicGenre() != null)
						{
							sqlStmt = new SqlStatement(QueryType.QUERY,
									"SELECT TOP 10 * FROM (SELECT * FROM Albums ORDERBY Albums.year) WHERE Albums.genre = "
									+ searchReq.getMusicGenre().toString(), null, searchReq.getId());
							connectionManager.insertToQueryQueue(sqlStmt);
						} else
						{
							sqlStmt = new SqlStatement(QueryType.QUERY, "SELECT TOP 10 * FROM (SELECT * FROM Albums ORDERBY Albums.year)", null,
									searchReq.getId());
							connectionManager.insertToQueryQueue(sqlStmt);
						}
						break;
					case MOST_POPULAR:
						sqlStmt = new SqlStatement(
								QueryType.QUERY,
								"SELECT TOP 10 Albums.* FROM Albums, (SELECT id, COUNT(id) FROM Sales GROUP BY id ORDER BY COUNT(id) DESC) WHERE Sales.id = Albums.id",
								null, searchReq.getId());
						connectionManager.insertToQueryQueue(sqlStmt);
						break;
					default:
						break;
					}
				case REGULAR:
					sqlStmt = new SqlStatement(QueryType.QUERY, "SELECT Albums.* FROM Albums, Artists WHERE (Albums.title LIKE '%"
							+ searchReq.getRegularSearchString() + "%') OR (Artists.name LIKE '%" + searchReq.getRegularSearchString()
							+ "%') GROUP BY Albums.id", null, searchReq.getId());
					connectionManager.insertToQueryQueue(sqlStmt);
					break;
				case ADVANCED:
					List<advanceSearchFieldValueBundle> params = searchReq.getAdvanceSearchParameters();
					query += "SELECT Albums.* FROM Albums, Artists, Genres, tracks WHERE ";

					for (Iterator iterator = params.iterator(); iterator.hasNext();)
					{
						advanceSearchFieldValueBundle advanceSearchFieldValue = (advanceSearchFieldValueBundle) iterator.next();

						switch (advanceSearchFieldValue.getSearchField()) {
						case ALBUM_TITLE:
							query += "Albums.title LIKE '%" + advanceSearchFieldValue.getValue() + "%' ";
							break;
						case ARTIST_NAME:
							query += "Artists.name LIKE '%" + advanceSearchFieldValue.getValue() + "%' AND Albums.artistId = Artists.artistId ";
							break;
						case GENRE:
							if (advanceSearchFieldValue.getValue().equals("All_Music_Genres") == false)
								query += "Genres.genre LIKE '%" + advanceSearchFieldValue.getValue() + "%' AND Genres.genreId = Albums.genreId ";
							break;
						case TRACK_TITLE:
							query += "Tracks.title LIKE '%" + advanceSearchFieldValue.getValue() + "%' AND Tracks.id = Albums.id ";
							break;
						case YEAR:
							query += "Albums.year "
								+ (advanceSearchFieldValue.getRelation().equals(advanceSearchFieldValueBundle.Relation.GREATER) ? ">"
										: advanceSearchFieldValue.getRelation().equals(advanceSearchFieldValueBundle.Relation.EQUALS) ? "=" : "<")
										+ advanceSearchFieldValue.getValue() + " ";
							break;
						default:
							break;
						}
					}
					query += "GROUP BY Albums.id";
					sqlStmt = new SqlStatement(QueryType.QUERY, query, null, searchReq.getId());
					connectionManager.insertToQueryQueue(sqlStmt);
				case GET_USERS:
					sqlStmt = new SqlStatement(QueryType.QUERY, "SELECT * FROM Users", null, searchReq.getId());
					connectionManager.insertToQueryQueue(sqlStmt);
					break;
				default:
					break;
				}
			} else if (searchReq.getTheQueryType() == SqlStatement.QueryType.INSERT_SINGLE)
			{
				switch (searchReq.getSingleInsertType()){
				case ADD_USER:
					String[] fields1 = searchReq.getDualFields();
					String UID1 = UniqueID();
					sqlStmt = new SqlStatement(QueryType.INSERT_SINGLE, "INSERT INTO Users (UserId, UserName, Password) VALUES (" 
							+ UID1 + ", " + fields1[0] + ", " + fields1[1] + ")", null, searchReq.getId());
					connectionManager.insertToQueryQueue(sqlStmt);
					break;
				case ADD_SALE:
					String[] fields2 = searchReq.getDualFields();
					String UID2 = UniqueID();
					sqlStmt = new SqlStatement(QueryType.INSERT_SINGLE, "INSERT INTO Sales (OrderId, UserId, DiscId) VALUES (" 
							+ UID2 + ", " + fields2[0] + ", " + fields2[1] + ")", null, searchReq.getId());
					connectionManager.insertToQueryQueue(sqlStmt);
					break;
				}
			} else // Insert Bulk
			{
				map = searchReq.getMap();
				int sizeOfMap = map.entrySet().size();
				int num=0;

				switch (searchReq.getMapType()) 
				{
				case ALBUMS:
					num=0;
					while(sizeOfMap>0)
					{
						if(sizeOfMap>sizeOfBulk)
						{
							attributes = new String[sizeOfBulk][];                        
							sizeOfMap =  sizeOfMap - sizeOfBulk;                    
						} else
						{
							attributes = new String[sizeOfMap][];                        
							sizeOfMap = 0;
						}
						for (Entry<String, String[]> e : map.entrySet())
						{
							attributes[num] = new String[7];
							attributes[num][0] = e.getKey();
							String[] values = e.getValue();
							for (int i = 1; i <= values.length; i++)
							{
								attributes[num][i] = values[i-1];
							}
							num++;
							if(num > sizeOfBulk)
							{
								sqlStmt = new SqlStatement(QueryType.INSERT_BULK, "INSERT INTO Albums (DiscId, ArtistId, Title, Year, Genre, TotalTime, Price) " +
										"VALUES (?, ?, ?, ?, ?, ?, "+ (5+Math.random()*10) +")", attributes, searchReq.getId());
								connectionManager.insertToQueryQueue(sqlStmt);
								num = 0;
							}
						}
					}
					break;
				case ARTISTS:
					num=0;
					while(sizeOfMap>0)
					{
						if(sizeOfMap>sizeOfBulk)
						{
							attributes = new String[sizeOfBulk][];                        
							sizeOfMap =  sizeOfMap - sizeOfBulk;                    
						} else
						{
							attributes = new String[sizeOfMap][];                        
							sizeOfMap = 0;
						}
						for (Entry<String, String[]> e : map.entrySet())
						{
							attributes[num] = new String[2]; 
							attributes[num][0] = e.getKey();
							attributes[num][1] = e.getValue()[0];
							num++;
							if(num > sizeOfBulk)
							{								
								sqlStmt = new SqlStatement(QueryType.INSERT_BULK, "INSERT INTO Artists (Name, ArtistId) " +
										"VALUES (?, ?)", attributes, searchReq.getId());
								connectionManager.insertToQueryQueue(sqlStmt);
								num = 0;

							}
						}
					}
					break;
				case GENRES:
					num=0;
					while(sizeOfMap>0)
					{
						if(sizeOfMap>sizeOfBulk)
						{
							attributes = new String[sizeOfBulk][];                        
							sizeOfMap =  sizeOfMap - sizeOfBulk;                    
						} else
						{
							attributes = new String[sizeOfMap][];                        
							sizeOfMap = 0;
						}
						for (Entry<String, String[]> e : map.entrySet())
						{
							attributes[num] = new String[2]; 
							attributes[num][0] = e.getKey();
							attributes[num][1] = e.getValue()[0];
							num++;
							if(num > sizeOfBulk)
							{								
								sqlStmt = new SqlStatement(QueryType.INSERT_BULK, "INSERT INTO Genres (Genre, GenreId) " +
										"VALUES (?, ?)", attributes, searchReq.getId());
								connectionManager.insertToQueryQueue(sqlStmt);
								num = 0;

							}
						}
					}
					break;
				case TRACKS:
					num=0;
					while(sizeOfMap>0)
					{
						if(sizeOfMap>sizeOfBulk)
						{
							attributes = new String[sizeOfBulk][];                        
							sizeOfMap =  sizeOfMap - sizeOfBulk;                    
						} else
						{
							attributes = new String[sizeOfMap][];                        
							sizeOfMap = 0;
						}
						for (Entry<String, String[]> e : map.entrySet())
						{
							attributes[num] = new String[4]; 
							attributes[num][0] = e.getKey();
							String[] values = e.getValue();
							for (int i = 1; i <= values.length; i++)
							{
								attributes[num][i] = values[i-1];
							}
							num++;
							if(num > sizeOfBulk)
							{						
								sqlStmt = new SqlStatement(QueryType.INSERT_BULK, "INSERT INTO Tracks (TrackId, DiscID, TrackTitle, Number) " +
										"VALUES (?, ?, ?, ?)", attributes, searchReq.getId());
								connectionManager.insertToQueryQueue(sqlStmt);
								num = 0;
							}
						}
					}
					break;
				default:
					break;
				}

			}
		}

		private synchronized String[][] resultSetInto2DStringArray(ResultSet rs) {
			try {
				rs.last();
				int rowCount = rs.getRow();
				int colCount = rs.getMetaData().getColumnCount();
				String[][] table = new String[rowCount][colCount];
				rs.first();
				while (!rs.isAfterLast()) {
					int row = rs.getRow()-1;
					for (int col=0; col<colCount; col++) {
						table[row][col] = rs.getString(col+1);
					}
					rs.next();
				}

				return table;
			}
			catch (SQLException e) {
				View.displayErroMessage("An error occurred while manipulating a result from the DB.\n\n" +
						e.toString());
				return null;
			}
		}
	}

}
