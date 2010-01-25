package controller;

import java.net.Authenticator.RequestorType;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
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
import model.UserPassword;
import model.advanceSearchFieldValueBundle;
import model.SqlStatement.QueryType;

public class queryHandler implements Runnable
{

	private static boolean timeToQuit = false;
	private ExecutorService threadPool = Executors.newCachedThreadPool();;

	public synchronized void run()
	{
		queryHandler.createTables();

		while (!timeToQuit)
		{
			if (!SearchesPriorityQueue.isEmpty())
			{
				System.out.println("Got something from SearchesPriorityQueue !!!");
				RequestToQueryHandler req = SearchesPriorityQueue.getSearch();
				System.out.println("GOT NEW REQUEST FROM PRIORITY QUEUE:");
				System.out.println("ID = " + req.getId());
				System.out.println("Query Type = " + req.getTheQueryType());
				System.out.println("Search Type = " + req.getSearchType());
				System.out.println("Top 10 Type = " + req.getTop10Type());
				HandleQuery q = new HandleQuery(req);
				this.threadPool.execute(q);		
			}
			try
			{
				Thread.sleep(100);
			} catch (InterruptedException e)
			{}
			//System.out.println("Waiting to get something from SearchesPriorityQueue");
		}
		this.threadPool.shutdownNow();
	}

	public static synchronized void quit()
	{
		timeToQuit = true;
	}



	// this method checks if tables exist i the DB. if not - creates all tables.
	public static synchronized void createTables()
	{
		int tester = -1;
		SqlStatement sqlStmt = new SqlStatement(QueryType.QUERY, 
				null, 
				"SELECT count(table_name) FROM all_tables WHERE " +
				"(table_name = 'ARTISTS' OR table_name = 'ALBUMS' OR " +
				"table_name = 'TRACKS' OR table_name = 'SALES' OR " + 
				"table_name = 'GENRES' OR table_name = 'USERS')", null, 0);
		System.out.println("Inserting sqlStmt to queue");
		connectionManager.insertToQueryQueue(sqlStmt);
		boolean waitforanswer = false;
		Result myResult = null;
		while (!waitforanswer)
		{
			try {
				myResult = ResultsQueue.getResult();
				if (myResult != null)
				{
					myResult.getResultSet().next();
					tester = myResult.getResultSet().getInt(1);	
					waitforanswer = true;
				}

				if ((tester >= 0) && (tester < 6))
				{
					myResult = ResultsQueue.getResult();
					SqlStatement[] create_stmt = new SqlStatement[6];
					create_stmt[0] = new SqlStatement(QueryType.INSERT_SINGLE, RequestToQueryHandler.MapType.ALBUMS, "CREATE TABLE Albums(DiscId NUMBER, ArtistId INT, "
							+ "Title VARCHAR(500), Year SMALLINT, Genre INT, TotalTime SMALLINT, Price FLOAT, PRIMARY KEY(DiscId))", null, 0);
					create_stmt[1] = new SqlStatement(QueryType.INSERT_SINGLE, RequestToQueryHandler.MapType.TRACKS, "CREATE TABLE Tracks(TrackId INT, DiscID NUMBER, "
							+ "Num SMALLINT, TrackTitle VARCHAR(500), PRIMARY KEY(TrackId))", null, 0);
					create_stmt[2] = new SqlStatement(QueryType.INSERT_SINGLE, RequestToQueryHandler.MapType.ARTISTS, 
							"CREATE TABLE Artists(Name VARCHAR(300), ArtistId INT, PRIMARY KEY(ArtistId))", null, 0);
					create_stmt[3] = new SqlStatement(QueryType.INSERT_SINGLE, RequestToQueryHandler.MapType.GENRES, 
							"CREATE TABLE Genres(Genre VARCHAR(300), GenreId INT, PRIMARY KEY(GenreId))", null, 0);
					create_stmt[4] = new SqlStatement(QueryType.INSERT_SINGLE, null, 
							"CREATE TABLE Users(UserId INT, UserName VARCHAR(50), Password VARCHAR(50), PRIMARY KEY(UserId))", null, 0);
					create_stmt[5] = new SqlStatement(QueryType.INSERT_SINGLE, null, 
							"CREATE TABLE Sales(OrderId INT, UserId INT, DiscId NUMBER, PRIMARY KEY(OrderId, UserId))", null, 0);
					for (int i = 0; i < 6; i++)
					{
						connectionManager.insertToQueryQueue(create_stmt[i]);
					}
					waitforanswer = true;
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

	private class HandleQuery implements Runnable
	{

		private RequestToQueryHandler searchReq;
		private SqlStatement sqlStmt;
		private HashMap<String, String[]> map;
		//private int sizeOfBulk = 50000;
		private String[][] attributes;

		protected HandleQuery(RequestToQueryHandler searchReq)
		{
			this.searchReq = searchReq;
		}

		public synchronized void run()
		{
			this.createQuery();
			if (this.searchReq.getTheQueryType() != QueryType.QUERY)
			{
				// we are not expecting any results back so quit
				return;
			}

			// we are expecting results so sit back and wait for them
			Result myResult = null;
			while (!timeToQuit)
			{
				myResult = ResultsQueue.peek();
				if ((myResult != null) && (myResult.getId() == this.searchReq.getId()))
				{
					// ResultsQueue has a result and it's ours!!
					System.out.println("Got result ID=" + myResult.getId());
					myResult = ResultsQueue.getResult();
					ResultSet rs = myResult.getResultSet();
					String[][] table = resultSetInto2DStringArray(rs);
					if (table != null)
					{
						TableViewsMap.addTable(this.searchReq.getId(), table);
						return;
					} else
					{
						// an error occurred
						return;
					}
				}
			}
		}

		private synchronized void createQuery()
		{
			DecimalFormat df = new DecimalFormat("####.00");

			String query = "";
			if (searchReq.getTheQueryType() == SqlStatement.QueryType.QUERY)
			{
				System.out.println("QueryType.QUERY");
				switch (searchReq.getSearchType()) {			
				case TOP_10:
					switch (searchReq.getTop10Type()) {
					case LATEST:
						if (searchReq.getMusicGenre() != null)
						{
							sqlStmt = new SqlStatement(QueryType.QUERY,		
									searchReq.getMapType(), 
									"SELECT * FROM (select * from albums where genre = (SELECT genreid FROM genres where genres.genre='" 
									+ searchReq.getMusicGenre().toString().toLowerCase() + "') ORDER BY year desc,discid desc) where rownum<=10"
									, null, searchReq.getId());
							connectionManager.insertToQueryQueue(sqlStmt);
						} else
						{
							sqlStmt = new SqlStatement(QueryType.QUERY, searchReq.getMapType(), 
									"SELECT * FROM (select * from albums ORDER BY year desc) where rownum<=10",
									null, searchReq.getId());
							connectionManager.insertToQueryQueue(sqlStmt);
						}
						break;
					case MOST_POPULAR:
						sqlStmt = new SqlStatement(
								QueryType.QUERY,
								searchReq.getMapType(),
								"SELECT TOP 10 Albums.* FROM ALBUMS, (SELECT discid, COUNT(discid) "
								+"FROM SALES GROUP BY discid ORDER BY COUNT(discid) DESC) WHERE SALES.discid = ALBUMS.discid", null, 
								searchReq.getId());
						connectionManager.insertToQueryQueue(sqlStmt);
						break;
					default:
						break;
					}
				break;
				case REGULAR:
					sqlStmt = new SqlStatement(QueryType.QUERY, searchReq.getMapType(), 
							"SELECT distinct ALBUMS.title, ARTISTS.name, ALBUMS.year, GENRES.genre, ALBUMS.totaltime, ALBUMS.price " +
							"FROM ALBUMS, ARTISTS, GENRES WHERE " +
							"((ALBUMS.title LIKE '%"+ searchReq.getRegularSearchString() + "%') " +
							"OR (ARTISTS.name LIKE '%" + searchReq.getRegularSearchString()+ "%')) " +
							"AND (artists.artistid = albums.artistid) AND (genres.genreid = albums.genre) AND " +
							"(genres.genre = '" + searchReq.getMusicGenre().toString().toLowerCase() + "')", 
							null, searchReq.getId());
					connectionManager.insertToQueryQueue(sqlStmt);
					break;
				case ADVANCED:
					List<advanceSearchFieldValueBundle> params = searchReq.getAdvanceSearchParameters();
					query += "SELECT ALBUMS.title, ARTISTS.name, ALBUMS.year, GENRES.genre, ALBUMS.totaltime, ALBUMS.price " +
							"FROM ALBUMS, ARTISTS, GENRES, TRACKS WHERE ";

					for (Iterator<advanceSearchFieldValueBundle> iterator = params.iterator(); iterator.hasNext();)
					{
						advanceSearchFieldValueBundle advanceSearchFieldValue = (advanceSearchFieldValueBundle) iterator.next();
						
						switch (advanceSearchFieldValue.getSearchField()) {
						case ALBUM_TITLE:
							query += "ALBUMS.title LIKE '%" + advanceSearchFieldValue.getValue() + "%'";
							break;
						case ARTIST_NAME:
							query += "ARTISTS.name LIKE '%" + advanceSearchFieldValue.getValue() + "%' AND ALBUMS.artistId = ARTISTS.artistId ";
							break;
						case GENRE:
							if (advanceSearchFieldValue.getValue().equals("All_Music_Genres") == false)
								query += "GENRES.genre LIKE '%" + advanceSearchFieldValue.getValue() + "%' AND GENRES.genreId = ALBUMS.genreId ";
							break;
						case TRACK_TITLE:
							query += "TRACKS.title LIKE '%" + advanceSearchFieldValue.getValue() + "%' AND TRACKS.id = ALBUMS.discid ";
							break;
						case YEAR:
							query += "ALBUMS.year "
								+ (advanceSearchFieldValue.getRelation().equals(advanceSearchFieldValueBundle.Relation.GREATER) ? ">"
										: advanceSearchFieldValue.getRelation().equals(advanceSearchFieldValueBundle.Relation.EQUALS) ? "=" : "<")
										+ advanceSearchFieldValue.getValue() + " ";
							break;
						default:
							break;
						}
					}
					query += "GROUP BY ALBUMS.";
					sqlStmt = new SqlStatement(QueryType.QUERY, searchReq.getMapType(), query, null, searchReq.getId());
					connectionManager.insertToQueryQueue(sqlStmt);
				case GET_USERS:
					sqlStmt = new SqlStatement(QueryType.QUERY, searchReq.getMapType(), "SELECT * FROM USERS", null, searchReq.getId());
					connectionManager.insertToQueryQueue(sqlStmt);
					break;
				default:
					break;
				}
			} else if (searchReq.getTheQueryType() == SqlStatement.QueryType.INSERT_SINGLE)
			{
				System.out.println("QueryType.INSERT_SINGLE");
				System.out.println("Single inser type: " + searchReq.getSingleInsertType().toString());
				switch (searchReq.getSingleInsertType()) {
				case ADD_USER:
					System.out.println("In Case ADD_USER");
					String[] fields1 = searchReq.getDualFields();
					String UID1 = (System.currentTimeMillis()+"").substring(6);			
					System.out.println("After UniquId");
					sqlStmt = new SqlStatement(QueryType.INSERT_SINGLE, searchReq.getMapType(), "INSERT INTO USERS (UserId, UserName, Password) VALUES ('" + UID1 + "', '"
							+ fields1[0] + "', '" + fields1[1] + "')", null, searchReq.getId());
					System.out.println("Sending single insert to DBC from QH: ID="+searchReq.getId()+" User="+fields1[0] + " Password="+fields1[1]);
					System.out.println("query statment="+sqlStmt.getStmt());
					connectionManager.insertToQueryQueue(sqlStmt);
					break;
				case ADD_SALE:
					System.out.println("In Case ADD_SALE");
					String[] fields2 = searchReq.getDualFields();
					String UID2 = (System.currentTimeMillis()+"").substring(6);
					sqlStmt = new SqlStatement(QueryType.INSERT_SINGLE, searchReq.getMapType(), "INSERT INTO SALES (OrderId, UserId, DiscId) VALUES ('" + UID2 + "', '"
							+ fields2[0] + "', '" + fields2[1] + "')", null, searchReq.getId());
					connectionManager.insertToQueryQueue(sqlStmt);
					break;
				default:
					System.out.println("In Case Default");

				}
			} else
				// Insert Bulk
			{
				System.out.println("QueryType.INSERT_BULK");
				map = searchReq.getMap();
				int sizeOfMap = map.entrySet().size();
				attributes = new String[sizeOfMap][];
				int num = 0;

				switch (searchReq.getMapType()) {
				case ALBUMS:
					num = 0;
					for (Entry<String, String[]> e : map.entrySet())
					{
						attributes[num] = new String[6];
						attributes[num][0] = e.getKey();
						String[] values = e.getValue();
						for (int i = 1; i < values.length; i++)
						{
							attributes[num][i] = values[i - 1];
						}

						num++;
					}

					sqlStmt = new SqlStatement(QueryType.INSERT_BULK,
							searchReq.getMapType(), "INSERT INTO ALBUMS (DiscId, ArtistId, Title, Year, Genre, TotalTime, Price) " + "VALUES (?, ?, ?, ?, ?, ?, "
							+ df.format((5 + Math.random() * 10)) + ")", attributes, searchReq.getId());
					connectionManager.insertToQueryQueue(sqlStmt);

					break;
				case ARTISTS:
					num = 0;
					//sizeOfCurrentChunk = 0;
					for (Entry<String, String[]> e : map.entrySet())
					{
						/*if (createArray)
						{
							if (sizeOfMap > sizeOfBulk)
							{
								attributes = new String[sizeOfBulk][];
								sizeOfMap = sizeOfMap - sizeOfBulk;
								sizeOfCurrentChunk = sizeOfBulk;
							} else
							{
								attributes = new String[sizeOfMap][];
								sizeOfMap = 0;
								sizeOfCurrentChunk = sizeOfMap;
							}
							createArray = false;
						}*/

						attributes[num] = new String[2];
						attributes[num][0] = e.getKey();
						attributes[num][1] = e.getValue()[0];

						num++;

						/*if (num > sizeOfCurrentChunk)
						{
							sqlStmt = new SqlStatement(QueryType.INSERT_BULK, "INSERT INTO Artists (Name, ArtistId) " + "VALUES (?, ?)", attributes,
									searchReq.getId());
							connectionManager.insertToQueryQueue(sqlStmt);
							num = 0;
							//createArray = true;
						}
						 */
					}

					sqlStmt = new SqlStatement(QueryType.INSERT_BULK, searchReq.getMapType(), "INSERT INTO ARTISTS (Name, ArtistId) " + "VALUES (?, ?)",
							attributes, searchReq.getId());
					connectionManager.insertToQueryQueue(sqlStmt);

					break;
				case GENRES:
					num = 0;
					for (Entry<String, String[]> e : map.entrySet())
					{
						attributes[num] = new String[2];
						attributes[num][0] = e.getKey();
						attributes[num][1] = e.getValue()[0];

						num++;

					}

					sqlStmt = new SqlStatement(QueryType.INSERT_BULK, searchReq.getMapType(), "INSERT INTO GENRES (Genre, GenreId) " + "VALUES (?, ?)", attributes, searchReq
							.getId());
					connectionManager.insertToQueryQueue(sqlStmt);

					break;
				case TRACKS:
					num = 0;
					for (Entry<String, String[]> e : map.entrySet())
					{
						attributes[num] = new String[4];
						attributes[num][0] = e.getKey();
						String[] values = e.getValue();
						for (int i = 1; i < values.length; i++)
						{
							attributes[num][i] = values[i - 1];
						}

						num++;
					}

					sqlStmt = new SqlStatement(QueryType.INSERT_BULK, searchReq.getMapType(), "INSERT INTO TRACKS (TrackId, DiscID, TrackTitle, Num) "
							+ "VALUES (?, ?, ?, ?)", attributes, searchReq.getId());
					connectionManager.insertToQueryQueue(sqlStmt);

					break;
				default:
					break;
				}

			}
		}
	}
	
	public static synchronized String[][] resultSetInto2DStringArray(ResultSet rs)
	{
		int rowCount = getQueryResultRowCount(rs);
		System.out.println("Row Count: " + rowCount );
		String[][] theResultArray = new String[rowCount][];

		for (int row = 1; row <= rowCount; row++)
			theResultArray[row - 1] = getQueryResultWholeRow(rs, row);
		
		return theResultArray;
	}

	private static int getQueryResultRowCount(ResultSet queryResult)
	{
		int size = 0;
		try
		{
			if (queryResult.getType() == ResultSet.TYPE_FORWARD_ONLY)
			{
				size = queryResult.getRow();
				while (queryResult.next())
				{
					size++;
				}
			}
			else
			{
				if (queryResult.last())
					size = queryResult.getRow();
			}
		}
		catch (Exception sqle)
		{
			sqle.printStackTrace();
		}

		return size;
	}

	private static String[] getQueryResultWholeRow(ResultSet queryResult, int theRow)
	{
		String[] theOutput = new String[] {};

		int theColumnNum = 0;
	
		try
		{
			theColumnNum = getQueryResultColumnCount(queryResult);
			//System.out.println("theColumnNum: " + theColumnNum );
			theOutput = new String[theColumnNum];
			if (queryResult.absolute(theRow))
				for (int i = 1; i <= theColumnNum; i++)
				{
					theOutput[i - 1] = queryResult.getString(i);
					//System.out.println("i=" + i + " " + theOutput[i - 1]);
				}

		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}
		return theOutput;
	}

	private static int getQueryResultColumnCount(ResultSet queryResult)
	{
		try
		{
			ResultSetMetaData rsmd = queryResult.getMetaData();
			//  Get the number of columns in the result set
			return rsmd.getColumnCount();
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}

		return 0;
	}
}