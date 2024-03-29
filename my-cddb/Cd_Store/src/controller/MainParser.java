package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import view.views.View;
import model.DbConfiguration;
import model.Disk;
import model.RequestToQueryHandler;
import model.SearchesPriorityQueue;
import model.SqlStatement;
import model.RequestToQueryHandler.MapType;
import model.RequestToQueryHandler.Priority;
import model.SqlStatement.QueryType;

public class MainParser extends Thread
{
	String fileToParse;
	boolean updateFile = false;
	private boolean status = true;
	String dTitle;
	
	static long tempNumOfDisks = 0;

	private File file;
	private BufferedReader br;
	String line = null;
	boolean jumpToNextDisk = false;

	Disk currentDisk = null;
	private String[] tracks;
	
	private HashMap<String,String[]> diskMap = new HashMap<String,String[]>(80000, 0.9f);
	private HashMap<String,String[]> genresMap = new HashMap<String,String[]>(3000, 0.9f);
	private HashMap<String,String[]> artistMap = new HashMap<String,String[]>(100000, 0.8f);
	private HashMap<String,String[]> tracksMap = new HashMap<String,String[]>(80000, 0.9f);
	HashMap<String,String> oldArtistMap;
	HashMap<String,String> oldGenresMap;
	long genreId = 0, artistsId = 0, tracksId = 0, maxDiskId=0;
	
	public MainParser(String fileToParse, boolean updateFile)
	{
		this.fileToParse = fileToParse;
		this.updateFile = updateFile;

	}

	public void run()
	{		
		try
		{
			System.out.println("running parser");
			file = new File(fileToParse);
			br = new BufferedReader(new FileReader(file));
			if (updateFile == true)
			{
				this.oldGenresMap = new HashMap<String,String>(3000, 0.9f);
				this.oldArtistMap = new HashMap<String,String>(100000, 0.8f);
				
				System.out.println("fetching old tables");
				String[][] reqGenres = getStringResults("SELECT Genre, GenreId FROM GENRES");
				String[][] reqArtists = getStringResults("SELECT Name, ArtistId FROM ARTISTS");
				
				System.out.println("Got GENRES table from DB with size of: " + reqGenres.length);
				System.out.println("Got ARTISTS table from DB with size of: " + reqArtists.length);
				
				if (reqArtists == null || reqGenres == null)
				{
					System.out.println("error fetching old tables");
					return;
				}
				
				for (int i = 0; i<reqArtists.length; i++)
				{
					oldArtistMap.put(reqArtists[i][0], reqArtists[i][1]);
				}
				
				for (int i = 0; i<reqGenres.length; i++)
				{
					oldGenresMap.put(reqGenres[i][0], reqGenres[i][1]);
				}
				
				genreId = this.getId("GENRES", "genreid")+1;
				artistsId = this.getId("ARTISTS", "artistid")+1;
				tracksId = this.getId("TRACKS", "trackid")+1;
				maxDiskId = this.getId("ALBUMS", "discid")+1;
				
				System.out.println("GENRES ID = " + genreId);
				System.out.println("ARTISTS ID = " + artistsId);
				System.out.println("TRACKS ID = " + tracksId);
				System.out.println("DISC ID = " + maxDiskId);
			}

			// Read the file
			System.out.println("reading file");
			int track_index = 0;
			while ((jumpToNextDisk && (line!=null)) || (line = br.readLine()) != null)
			{
				if(jumpToNextDisk)
					jumpToNextDisk = false;
					
				try
				{
					if (line.startsWith("# xmcd"))
					{
						if ((currentDisk != null))
						{
							updateMaps(currentDisk);
							if (diskMap.size() >= 50000)
							{
								sendDiskBulk();
								sendTracksBulk();
							}
						}

						currentDisk = new Disk();
						tracks =  new String[100];
					}

					if (!isEligableCoding(line))
					{
						currentDisk = null;
						jumpToNextDisc();
						continue;
					}

					if (line.startsWith("# Disc length: "))
					{
						currentDisk.setTotalTime(Integer.parseInt(line.split("\\s")[3]) + "");
						
					} else if (line.startsWith("DISCID="))
					{
						currentDisk.setId(line.substring(line.indexOf("=") + 1).split(",")[0]);
					} else if (line.startsWith("DTITLE="))
					{
						dTitle = line.substring(line.indexOf("=") + 1).trim().toLowerCase();
						if(dTitle.split("/").length==1)
						{
							currentDisk.setTitle(dTitle);
							currentDisk.setArtist(dTitle);
						} else if (dTitle.split("/").length==2)
						{
							currentDisk.setArtist(dTitle.split("/")[0]);
							currentDisk.setTitle(dTitle.split("/")[1]);
							
						} else
						{
							currentDisk.setArtist("various");
							currentDisk.setTitle(dTitle.split("/")[dTitle.split("/").length-1]);
						}	
										
					} else if (line.startsWith("DYEAR="))
					{
						int len = line.substring(6).trim().length();
						if (len == 4)
						{
							int year = Integer.parseInt(line.substring(line.indexOf("=") + 1).trim());
							currentDisk.setYear(year+"");
							if (year > 3000)
							{
								currentDisk.setYear(0+"");
							}
						} else
						{
							currentDisk.setYear(0+"");
						}
					} else if (line.startsWith("DGENRE="))
					{
						currentDisk.setGenre(line.substring(line.indexOf("=") + 1).trim().toLowerCase());
					} else if (line.startsWith("TTITLE"))
					{		
						track_index = Integer.parseInt(line.substring(6, line.indexOf("=")));
						if (tracks[track_index] == null) {
							tracks[track_index] = line.substring(line.indexOf("=")+1).trim().toLowerCase();
						} else {
							tracks[track_index] += line.substring(line.indexOf("=")+1).trim().toLowerCase();
						}
					}
					line = null;
				} catch (Exception e)
				{
					currentDisk = null;
					jumpToNextDisc();
				}
			}
			
			System.out.println("last call");
			updateMaps(currentDisk);
			
			sendDiskBulk();
			sendTracksBulk();
			sendOtherBulk();
			
		} catch (Exception e)
		{
			status = false;
		} finally
		{
			try
			{
				br.close();
				file.delete();
			} catch (IOException e)
			{}
		}
	}

	private void sendTracksBulk() {
		System.out.println("sending tracks");
		RequestToQueryHandler trackReq = new RequestToQueryHandler(QueryId.getId(), 
				RequestToQueryHandler.Priority.LOW_PRIORITY, 
				QueryType.INSERT_BULK, 
				this.tracksMap, 
				MapType.TRACKS);
		SearchesPriorityQueue.addSearch(trackReq);
		tracksMap = new HashMap<String,String[]>(80000, 0.9f);
	}

	private void sendOtherBulk() 
	{
		System.out.println("sending artists");
		RequestToQueryHandler artistReq = new RequestToQueryHandler(QueryId.getId(), 
				RequestToQueryHandler.Priority.LOW_PRIORITY, 
				QueryType.INSERT_BULK, 
				this.artistMap, 
				MapType.ARTISTS);
		SearchesPriorityQueue.addSearch(artistReq);
		
		System.out.println("sending genres");
		RequestToQueryHandler genreReq = new RequestToQueryHandler(QueryId.getId(), 
				RequestToQueryHandler.Priority.LOW_PRIORITY, 
				QueryType.INSERT_BULK, 
				this.genresMap, 
				MapType.GENRES);
		SearchesPriorityQueue.addSearch(genreReq);
	}

	private void sendDiskBulk() {
		System.out.println("sending discs");
		RequestToQueryHandler diskReq = new RequestToQueryHandler(QueryId.getId(), 
																RequestToQueryHandler.Priority.HIGH_PRIORITY, 
																QueryType.INSERT_BULK, 
																this.diskMap, 
																MapType.ALBUMS);
		SearchesPriorityQueue.addSearch(diskReq);
		diskMap = new HashMap<String,String[]>(80000, 0.9f);
	}

	private void updateMaps(Disk currentDisk)
	{
		String diskId;
		long diskIdL;
		String artistId = this.artistsId+"";
		String genreId = this.genreId+"";
		
		diskIdL = Long.parseLong(currentDisk.getId(), 16);
		diskId = diskIdL+""; 
		if(this.updateFile == false || diskIdL>this.maxDiskId)
			if(diskMap.containsKey(diskId) == false)
			{
				if(artistMap.containsKey(currentDisk.getArtist()) == false)
				{
					if (this.updateFile == false || this.oldArtistMap.containsKey(currentDisk.getArtist()) == false)
					{
						artistMap.put(currentDisk.getArtist(), new String[]{artistId+""});
						this.artistsId++;
					}
					else
					{
						artistId = oldArtistMap.get(currentDisk.getArtist())+"";
					}
				}
				else
				{
					
					artistId = artistMap.get(currentDisk.getArtist())[0]+"";
				}
				
				if(genresMap.containsKey(currentDisk.getGenre()) == false)
				{
					if (this.updateFile == false || this.oldGenresMap.containsKey(currentDisk.getGenre()) == false)
					{
						genresMap.put(currentDisk.getGenre(), new String[]{genreId+""});
						this.genreId++;
					}
					else
					{
						genreId = oldGenresMap.get(currentDisk.getGenre())+"";
					}		
				}
				else
				{
					genreId = genresMap.get(currentDisk.getGenre())[0]+"";
				}
				
				
				diskMap.put(diskId, new String[]{artistId+"", currentDisk.getTitle(), currentDisk.getYear(), genreId+"", currentDisk.getTotalTime()+""/*, (5+Math.random()*10)+""*/});
				
				for (int i = 0; i < tracks.length; i++)
				{
					if(tracks[i] != null)
					{
						tracksMap.put(this.tracksId+"", new String[]{diskId+"", tracks[i], i+""});
						this.tracksId++;
					}
				}	
				
				tempNumOfDisks++;
				if(tempNumOfDisks%1000 == 0)
				{	
					System.out.println("Num Of Disks = " + tempNumOfDisks);
				}
			}
	}
	
	private void jumpToNextDisc()
	{
		try
		{
			while (line != null && line.startsWith("# xmcd") == false)
				line = br.readLine();
			
			jumpToNextDisk = true;
			
		} catch (IOException e)
		{}
	}
	
	/**
	 * Determine if the file parsed is a CDDB file or not.
	 * 
	 * @return True if it is a CDDB file, False otherwise
	 */
	public boolean isEligableFormat()
	{
		if (file.isFile())
		{
			// Open file and check first row
			try
			{
				br = new BufferedReader(new FileReader(file));
				if (br.readLine().startsWith("# xmcd"))
					return true;
			} catch (FileNotFoundException e)
			{
				return false;
			} catch (IOException e)
			{
				return false;
			}

		}
		return false;
	}

	public boolean checkFormat(String line, String format)
	{
		byte bytearray[] = line.getBytes();
		CharsetDecoder d = Charset.forName(format).newDecoder();
		try
		{
			CharBuffer r = d.decode(ByteBuffer.wrap(bytearray));
			r.toString();
		} catch (CharacterCodingException e)
		{
			return false;
		}
		return true;
	}

	public boolean isEligableCoding(String line)
	{
		return (checkFormat(line, "US-ASCII"));
	}

	public synchronized boolean getStatus()
	{
		return status;
	}
	
	private String[][] getStringResults(String query)
	{
		String[][] results = new String[2][];
		Connection connection = bypassConnect();	// DB connection
		PreparedStatement ps;
		ResultSet queryResult;
		
		try
		{
			ps = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			queryResult = ps.executeQuery();
			queryResult.next();
		}
		catch (SQLException e)
		{
			View.displayErroMessage("error retriving data from DB.\n"
					+e.getMessage());
			return null;
		}
		
		results = queryHandler.resultSetInto2DStringArray(queryResult);
		
		return results;
	}
	
	private long getId (String table, String key)
	{
		Connection connection = bypassConnect();	// DB connection
		PreparedStatement ps;
		
		try
		{
			ps = connection.prepareStatement("SELECT max(" + table + "." + key + ") from " + table, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet result = ps.executeQuery();
			result.next();
			
			return result.getLong(1);
		}
		catch (SQLException e)
		{
			View.displayErroMessage("error retriving data from DB.\n"
					+e.getMessage());
			return 0;
		}
	}
	
	private Connection bypassConnect()
	{
		// loading the driver
		try
		{
			Class.forName("oracle.jdbc.OracleDriver");
		}
		catch (ClassNotFoundException e)
		{
			View.displayErroMessage("Unable to load the Oracle JDBC driver");
			e.printStackTrace();
			return null;
		}
		
		// creating the connection
		try
		{
			String jdbcURL =
				"jdbc:oracle:thin:@" + DbConfiguration.getIpAddress()+":" + DbConfiguration.getPort() +
				"/" + DbConfiguration.getDb();
			
			System.out.println("bypassed connection");
			
			return DriverManager.getConnection(jdbcURL,
					DbConfiguration.getUser(), DbConfiguration.getPassword());
			
		}
		catch (SQLException e)
		{
			View.displayErroMessage("An error occured while trying to connect to the DB.\n\n"+e.getMessage());
			return null;
		}
	}
	
/*	public static void main (String args[])
	{
		connectionManager cm = new connectionManager();
		queryHandler qh = new queryHandler();
		Thread t2 = new Thread(qh);
		Thread t = new Thread(cm);
		t2.start();
		t.start();
		queryHandler.createTables();
		MainParser myParser = new MainParser("C:\\test\\allDisks.txt", false);
		myParser.start();
		
		try
		{
			myParser.join();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		cm.quit();
		qh.quit();
		try {
			t.join();
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}*/
}
