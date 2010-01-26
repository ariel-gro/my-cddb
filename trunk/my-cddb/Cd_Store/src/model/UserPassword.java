package model;

public class UserPassword
{
	private static int id;
	private static String user;
	private static String password;
	private static boolean isLoggedIn;
	
	public static synchronized boolean isLoggedIn()
	{
		return isLoggedIn;
	}
	public static synchronized void setLoggedIn(boolean isLoggedIn)
	{
		UserPassword.isLoggedIn = isLoggedIn;
	}
	public static synchronized String getUser()
	{
		return user;
	}
	public static synchronized void setUser(String user)
	{
		UserPassword.user = user;
	}
	public static synchronized String getPassword()
	{
		return password;
	}
	public static synchronized void setPassword(String password)
	{
		UserPassword.password = password;
	}
	public static synchronized int getId()
	{
		return id;
	}
	public static synchronized void setId(int id)
	{
		UserPassword.id = id;
	}
}
