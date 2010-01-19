package controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import model.RequestToQueryHandler;
import model.ResultTableContentProvider;
import model.ResultTableLabelProvider;
import model.SqlStatement;
import model.TableViewsMap;
import model.advanceSearchFieldValueBundle;
import model.SqlStatement.QueryType;

// ***************** ADD Break AND INSERTtoqueue put in the end *********************//
// ***************** Search how to add index automaticaly for users and sales *****************/

public class queryHandler
{
	private RequestToQueryHandler searchReq;
	private SqlStatement sqlStmt;
	private HashMap<String,String[]> map;
	private int sizeOfBulk = 50000;
	private String[][] attributes;

	public queryHandler(RequestToQueryHandler searchReq)
	{
		this.searchReq = searchReq;
	}

	public void createQuery()
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
										+ this.searchReq.getMusicGenre().toString(), null, this.searchReq.getId());
						connectionManager.insertToQueryQueue(sqlStmt);
					} else
					{
						sqlStmt = new SqlStatement(QueryType.QUERY, "SELECT TOP 10 * FROM (SELECT * FROM Albums ORDERBY Albums.year)", null,
								this.searchReq.getId());
						connectionManager.insertToQueryQueue(sqlStmt);
					}
					break;
				case MOST_POPULAR:
					sqlStmt = new SqlStatement(
							QueryType.QUERY,
							"SELECT TOP 10 Albums.* FROM Albums, (SELECT id, COUNT(id) FROM Sales GROUP BY id ORDER BY COUNT(id) DESC) WHERE Sales.id = Albums.id",
							null, this.searchReq.getId());
					connectionManager.insertToQueryQueue(sqlStmt);
					break;
				default:
					break;
				}
			case REGULAR:
				sqlStmt = new SqlStatement(QueryType.QUERY, "SELECT Albums.* FROM Albums, Artists WHERE (Albums.title LIKE '%"
						+ this.searchReq.getRegularSearchString() + "%') OR (Artists.name LIKE '%" + this.searchReq.getRegularSearchString()
						+ "%') GROUP BY Albums.id", null, this.searchReq.getId());
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
				sqlStmt = new SqlStatement(QueryType.QUERY, query, null, this.searchReq.getId());
				connectionManager.insertToQueryQueue(sqlStmt);
			case GET_USERS:
				sqlStmt = new SqlStatement(QueryType.QUERY, "SELECT * FROM Users", null, this.searchReq.getId());
				connectionManager.insertToQueryQueue(sqlStmt);
				break;
			default:
				break;
			}
		} else if (searchReq.getTheQueryType() == SqlStatement.QueryType.INSERT_SINGLE)
		{
			switch (searchReq.getSingleInsertType()){
			case ADD_USER:
				String[] fields1 = this.searchReq.getDualFields();
				String UID1 = UniqueID();
				sqlStmt = new SqlStatement(QueryType.INSERT_SINGLE, "INSERT INTO Users (UserId, UserName, Password) VALUES (" 
						+ UID1 + ", " + fields1[0] + ", " + fields1[1] + ")", null, this.searchReq.getId());
				connectionManager.insertToQueryQueue(sqlStmt);
				break;
			case ADD_SALE:
				String[] fields2 = this.searchReq.getDualFields();
				String UID2 = UniqueID();
				sqlStmt = new SqlStatement(QueryType.INSERT_SINGLE, "INSERT INTO Sales (OrderId, UserId, DiscId) VALUES (" 
						+ UID2 + ", " + fields2[0] + ", " + fields2[1] + ")", null, this.searchReq.getId());
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
									"VALUES (?, ?, ?, ?, ?, ?, "+ (5+Math.random()*10) +")", attributes, this.searchReq.getId());
							connectionManager.insertToQueryQueue(sqlStmt);
							num = 0;
						}
					}
				}
				break;
			case ARTISTS:
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
						{								//fix artistId - send query Select count(*) from Artists
							sqlStmt = new SqlStatement(QueryType.INSERT_BULK, "INSERT INTO Artists (Name, ArtistId) " +
									"VALUES (?, ?)", attributes, this.searchReq.getId());
							connectionManager.insertToQueryQueue(sqlStmt);
							num = 0;
							
						}
					}
				}
				break;
			case GENRES:
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
						{								//fix genreId - send query Select count(*) from Genres
							sqlStmt = new SqlStatement(QueryType.INSERT_BULK, "INSERT INTO Genres (Genre, GenreId) " +
									"VALUES (?, ?)", attributes, this.searchReq.getId());
							connectionManager.insertToQueryQueue(sqlStmt);
							num = 0;
							
						}
					}
				}
				break;
			case TRACKS:
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
						{						//fix trackId - send query Select count(*) from Tracks 
							sqlStmt = new SqlStatement(QueryType.INSERT_BULK, "INSERT INTO Tracks (TrackId, DiscID, Number, TrackTitle) " +
									"VALUES (?, ?, ?, ?)", attributes, this.searchReq.getId());
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
	//i will use this later
	public void passQueryToConnectionManager()
	{
		connectionManager.insertToQueryQueue(sqlStmt);
	}
	
	public static String[][] getAllArtistsName() 
	{
		String[][] results = null;;
		
		// send request here
		
		new Thread() {
			public void run()
			{
				while (true)
				{
				 // wait for result here
					
					
					
					try
					{
						Thread.sleep(200);
					} catch (InterruptedException e)
					{}
				}
			}
		}.start();
		
		//convert to String[][]
		
		return results;
	}

	public static String[][] getAllGenresName() 
	{
		String[][] results = null;;
		
		// send request here
		
		new Thread() {
			public void run()
			{
				while (true)
				{
				 // wait for result here
					
					
					
					try
					{
						Thread.sleep(200);
					} catch (InterruptedException e)
					{}
				}
			}
		}.start();
		
		//convert to String[][]
		
		return results;
	}
	
	public String UniqueID() {
		  Long current= System.currentTimeMillis();
		  return current.toString();
		}

	
	public void createTables(){
		//TODO query to get table names, then checking if each table exists. if not, create it.
		sqlStmt = new SqlStatement(null, "SELECT * FROM all_tables", null, 0);
		connectionManager.insertToQueryQueue(sqlStmt);
		String AlbumsTable = "CREATE TABLE Albums(DiscId BIGINT, ArtistId INT, " +
				"Title VARCHAR(50), Year SMALLINT, Genre VARCHAR(50), TotalTime SMALLINT, Price FLOAT)";
		String TracksTable = "CREATE TABLE Tracks(TrackId INT, DiscID BIGINT, Number TINYINT, TrackTitle VARCHAR(50))";
		String ArtistsTable = "CREATE TABLE Artists(Name VARCHAR(50), ArtistId INT)";
		String GenresTable = "CREATE TABLE Genres(Genre VARCHAR(50), GenreId INT)";
		String UsersTable = "CREATE TABLE Users(UserId INT, UserName VARCHAR(20), Password VARCHAR(20))";
		String SalesTable = "CREATE TABLE Sales(OrderId INT, UserId INT, DiscId BIGINT)";
		
	      
	    
	}
	
}
