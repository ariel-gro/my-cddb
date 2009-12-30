package model;

import java.util.Iterator;
import java.util.List;

public class Disk
{
	private String title;
	private String artist;
	private String genre;
	private String subGenre;
	private String year;
	private String totalTime;
	private String price;
	private List<Track> tracks;
	
	public Disk(String title, String artist, String genre, String subGenre, String year, String totalTime, String price, List<Track> tracks) 
	{
		this.title = title;
		this.artist = artist;
		this.genre = genre;
		this.subGenre = subGenre;
		this.year = year;
		this.totalTime = totalTime;
		this.price = price;
		this.tracks = tracks;
	}
	
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public String getArtist()
	{
		return artist;
	}
	public void setArtist(String artist)
	{
		this.artist = artist;
	}
	public String getGenre()
	{
		return genre;
	}
	public void setGenre(String genre)
	{
		this.genre = genre;
	}
	public String getSubGenre()
	{
		return subGenre;
	}
	public void setSubGenre(String subGenre)
	{
		this.subGenre = subGenre;
	}
	public String getYear()
	{
		return year;
	}
	public void setYear(String year)
	{
		this.year = year;
	}
	public String getTotalTime()
	{
		return totalTime;
	}
	public void setTotalTime(String totalTime)
	{
		this.totalTime = totalTime;
	}
	public String getPrice()
	{
		return price;
	}
	public void setPrice(String price)
	{
		this.price = price;
	}
	
	public List<Track> getTracks()
	{
		return tracks;
	}

	public void setTracks(List<Track> tracks)
	{
		this.tracks = tracks;
	}

	public String toString() 
	{
		String tracksTitles = "";
		for (Iterator<Track> iterator = tracks.iterator(); iterator.hasNext();) 
		{
			Track track = (Track) iterator.next();
			tracksTitles += track.getTitle() + "\n";
		}
		
		return 	"Title: " + title + "\n" +
				"Artist: " + artist + "\n" +
				"Genre: " + genre + "\n" +
				"Sub Genre: " + subGenre + "\n" +
				"Year: " + year + "\n" +
				"Total Time: " + totalTime + "\n" +
				"Price: " + price  + "\n" +
				"Tracks:\n" + tracksTitles;
	}
}
