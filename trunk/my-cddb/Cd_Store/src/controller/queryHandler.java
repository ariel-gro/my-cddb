package controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import model.RequestToQueryHandler;
import model.SqlStatement;
import model.advanceSearchFieldValueBundle;
import model.SqlStatement.QueryType;

// ***************** ADD Break AND INSERTtoque *********************//
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
			// this.searchReq.getMusicGenre() - doing tostring, so need the enum
			// to be lowercase?

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
					if (searchReq.getMusicGenre() != null)
					{
						sqlStmt = new SqlStatement(
								QueryType.QUERY,
								"SELECT TOP 10 Albums.* FROM Albums, (SELECT id, COUNT(id) FROM Sales GROUP BY id ORDER BY COUNT(id) DESC) WHERE Sales.id = Albums.id AND Albums.genre = "
										+ this.searchReq.getMusicGenre().toString(), null, this.searchReq.getId());
						connectionManager.insertToQueryQueue(sqlStmt);
					} else
					{
						sqlStmt = new SqlStatement(
								QueryType.QUERY,
								"SELECT TOP 10 Albums.* FROM Albums, (SELECT id, COUNT(id) FROM Sales GROUP BY id ORDER BY COUNT(id) DESC) WHERE Sales.id = Albums.id",
								null, this.searchReq.getId());
						connectionManager.insertToQueryQueue(sqlStmt);
					}
					break;
				default:
					break;
				}
			case REGULAR:
				sqlStmt = new SqlStatement(QueryType.QUERY, "SELECT Albums.* FROM Albums, Artists WHERE (Albums.title LIKE '%"
						+ this.searchReq.getRegularSearchString() + "%') OR (Artists.name LIKE '%" + this.searchReq.getRegularSearchString()
						+ "%') GROUP BY Albums.id", null, this.searchReq.getId());
				connectionManager.insertToQueryQueue(sqlStmt);

			case ADVANCED:
				// why use advanceSearchFieldValueBundle ? need just a list of
				// string in a pre-determined order, and put them instead of the
				// question marks.
				// what if some are null?
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

			default:
				break;
			}
		} else if (searchReq.getTheQueryType() == SqlStatement.QueryType.INSERT_SINGLE)
		{
			
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
							// need to send it send it to ariel.
							num = 0;
							// NEED TO COMPLETE
						}
							
						
						
					}
				}
				break;
			case ARTISTS:
				
				break;
			case GENRES:
	
				break;
			case TRACKS:
	
				break;
			default:
				break;
			}
			
		}
	}

	public void passQueryToConnectionManager()
	{
		connectionManager.insertToQueryQueue(sqlStmt);
	}
}
