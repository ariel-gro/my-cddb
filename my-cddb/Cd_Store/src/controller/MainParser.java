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
import java.util.HashMap;
import model.Disk;

public class MainParser extends Thread
{
	String fileToParse;
	private boolean status = true;
	String dTitle;
	
	static long tempNumOfDisks = 0;

	private File file;
	private BufferedReader br;
	String line = null;
	boolean jumpToNextDisk = false;

	Disk currentDisk = null;
	private String[] tracks;
	
	private HashMap<Long,String[]> diskMap = new HashMap<Long,String[]>(1200000, 0.95f);
	private HashMap<String,Integer> genresMap = new HashMap<String,Integer>(2000, 0.9f);
	private HashMap<String,Integer> artistMap = new HashMap<String,Integer>(150000, 0.9f);
	//private HashMap<Long,String[]> tracksMap = new HashMap<Long,String[]>(300000, 0.9f);
	
	public MainParser(String fileToParse)
	{
		this.fileToParse = fileToParse;
	}

	public void run()
	{
		try
		{
			file = new File(fileToParse);
			br = new BufferedReader(new FileReader(file));

			// Read the file
			int track_index = 0;
			while ((jumpToNextDisk && (line!=null)) || (line = br.readLine()) != null)
			{
				if(jumpToNextDisk)
					jumpToNextDisk = false;
					
				try
				{
					if (line.startsWith("# xmcd"))
					{
						if (currentDisk != null)
						{
							updateMaps(currentDisk);
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
						currentDisk.setTotalTime(Integer.parseInt(line.split("\\s")[3]));
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
							currentDisk.setYear(Integer.parseInt(line.substring(line.indexOf("=") + 1).trim()));
							if (currentDisk.getYear() > 10000)
							{
								currentDisk.setYear(0);
							}
						} else
						{
							currentDisk.setYear(0);
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

			updateMaps(currentDisk);
			
			System.out.println("Num Of Disks: " + diskMap.size());
			System.out.println("Num Of Artists: " + artistMap.size());
			System.out.println("Num Of Genres: " + genresMap.size());
			//System.out.println("Num Of Tracks: " + tracksMap.size());

		} catch (Exception e)
		{
			status = false;
		} finally
		{
			try
			{
				br.close();
			} catch (IOException e)
			{}
		}
	}

	private void updateMaps(Disk currentDisk)
	{
		long diskId;
		int artistId;
		int genreId;
		long trackId;
		
		diskId = Long.parseLong(currentDisk.getId(), 16); 
		if(diskMap.containsKey(diskId) == false)
		{
			if(artistMap.containsKey(currentDisk.getArtist()) == false)
			{
				artistId = artistMap.size();
				artistMap.put(currentDisk.getArtist(), artistId);
			}
			else
			{
				artistId = artistMap.get(currentDisk.getArtist());
			}
			
			if(genresMap.containsKey(currentDisk.getGenre()) == false)
			{
				genreId = genresMap.size();
				genresMap.put(currentDisk.getGenre(), genreId);
			}
			else
			{
				genreId = genresMap.get(currentDisk.getGenre());
			}
			
			diskMap.put(diskId, new String[]{artistId+"", currentDisk.getTitle(), currentDisk.getYear()+"", genreId+"", currentDisk.getTotalTime()+""/*, (5+Math.random()*10)+""*/});
			
		/*	for (int i = 0; i < tracks.length; i++)
			{
				if(tracks[i] != null)
				{
					trackId = tracksMap.size();
					tracksMap.put(trackId, new String[]{diskId+"", tracks[i], i+""});
				}
			}
		*/	
			
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
	
	public static void main (String args[])
	{
		MainParser myParser = new MainParser("C:\\FreeDB_Files_Temp\\allDisks.txt");
		myParser.start();
		
		try
		{
			myParser.join();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
