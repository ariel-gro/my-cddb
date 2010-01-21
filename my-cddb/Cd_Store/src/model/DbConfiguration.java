package model;

public class DbConfiguration
{
	private static String ipAddress = "";
	private static int port = 0;
	private static String user = "";
	private static String password = "";
	private static String db = "";
	private static String fileToImport = "";
	
	public static synchronized String getUser()
	{
		return user;
	}
	public static synchronized void setUser(String user)
	{
		DbConfiguration.user = user;
	}
	public static synchronized String getPassword()
	{
		return password;
	}
	public static synchronized void setPassword(String password)
	{
		DbConfiguration.password = password;
	}
	public static synchronized String getIpAddress()
	{
		return ipAddress;
	}
	public static synchronized void setIpAddress(String ipAddress)
	{
		DbConfiguration.ipAddress = ipAddress;
	}
	
	public static synchronized int getPort()
	{
		return port;
	}
	
	public static synchronized void setPort(int port)
	{
		DbConfiguration.port = port;
	}

	public static void setDb(String db) {
		DbConfiguration.db = db;
	}

	public static String getDb() {
		return db;
	}
	public static synchronized String getFileToImport()
	{
		return fileToImport;
	}
	public static synchronized void setFileToImport(String fileToImport)
	{
		DbConfiguration.fileToImport = fileToImport;
	}
}
