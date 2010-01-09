package model;

public class UserPassword
{
	private static String user;
	private static String password;
	
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
}
