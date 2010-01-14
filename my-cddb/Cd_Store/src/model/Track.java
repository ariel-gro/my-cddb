package model;

public class Track
{
	private int num;
	private String title;
	
	public Track() {}
	
	public Track(int num, String title) 
	{
		this.num = num;
		this.title = title;
	}
	
	public int getNum()
	{
		return num;
	}
	public void setNum(int num)
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
	
	public String toString() 
	{
		return 	"Title: " + title + "\n" +
				"Number: " + num;
	}
}
