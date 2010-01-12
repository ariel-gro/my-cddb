package controller;


import java.io.IOException;
import java.io.InputStream;

import model.BigString;

/**
 * Class for reading process stream.
 * The class running in a different thread and reads the stream until end of stream
 * reached ( should be stdout or stderr stream ).
 * 
 */
public class ProcessStreamReader extends Thread
{

	private InputStream			myIn		= null;

	private volatile String		myResult	= "";

	private boolean				_stopThread	= false;

	private volatile boolean	isFinished	= false;

	/**
	 * The Stream to read from 
	 * @param theIn - opened stream
	 */
	public ProcessStreamReader(InputStream theIn)
	{
		myIn = theIn;
		this.start();
	}

	/**
	 * Run until end of stream or stopThread and saving all data in the 
	 * Reasult String
	 */
	public void run()
	{
		int input = 0;
		BigString bigStr = new BigString(5000);
		int index = 0;

		try
		{
			while ((input = myIn.read()) != -1 && !_stopThread)
			{
				char c = (char) input;
				bigStr.addChar(c);
			}
			myResult = bigStr.getAsString();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		isFinished = true;
		//System.out.println("reading thread exited");
	}

	/**
	 * Kill the thread on the spot. 
	 */
	public void kill()
	{
		_stopThread = true;
		for (int i = 0; i < 5; i++)
		{
			if (isFinished) break;
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{}
		}
	}
	
	/**
	 * @return true if stream ended or killed
	 */
	public boolean isFinished()
	{
		return isFinished;
	}

	/**
	 * get the result
	 */
	public String getResult()
	{
		return myResult;
	}
}