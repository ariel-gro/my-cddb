package model;

import java.util.List;

public class ShoppingCartContent
{
	private static List<Disk> content;

	public static List<Disk> getContent()
	{
		return content;
	}

	public static void setContent(List<Disk> content)
	{
		ShoppingCartContent.content = content;
	}

	public static void addToContent(Disk disk)
	{
		content.add(disk);
	}
	
	public static void removeFromContent(int index)
	{
		content.remove(index);
	}
	
	public static void clearContent()
	{
		content.clear();
	}
}
