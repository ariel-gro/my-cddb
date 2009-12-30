package model;

public class Track
{
	private String num;
	private String title;
	private String length;
	
	public Track(String num, String title, String length) 
	{
		this.num = num;
		this.title = title;
		this.length = length;
	}
	
	public String getNum()
	{
		return num;
	}
	public void setNum(String num)
	{
		this.num = num;
	}
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public String getLength()
	{
		return length;
	}
	public void setLength(String length)
	{
		this.length = length;
	}
	
	public String toString() 
	{
		return 	"Title: " + title + "\n" +
				"Number: " + num + "\n" +
				"Length: " + length;
	}
}
