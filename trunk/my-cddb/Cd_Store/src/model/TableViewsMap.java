package model;

import java.util.HashMap;
import java.util.Map;

public class TableViewsMap
{
	private static Map<Integer, String[][]> TableViewMap = null;
	private static boolean[] updates = new boolean[1000];
	
	protected TableViewsMap() {
		// Exists only to defeat instantiation.
	}

	public synchronized static void addTable(int id, String [][] data)
	{
		if (TableViewMap == null)
			TableViewMap = new HashMap<Integer, String[][]>();

		TableViewMap.put(id, data);
		
		updates[id] = !updates[id];
	}

	public synchronized static String[][] getData(int id)
	{
		if (TableViewMap == null)
			TableViewMap = new HashMap<Integer, String[][]>();

		if (TableViewMap.isEmpty() == false)
			return TableViewMap.get(id);
		else
			return null;
	}

	public synchronized static boolean isEmpty()
	{
		if (TableViewMap == null)
			TableViewMap = new HashMap<Integer, String[][]>();

		return TableViewMap.isEmpty();
	}

	public static synchronized boolean getUpdate(int id)
	{
		return updates[id];
	}
}
