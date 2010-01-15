package controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class RunProcess
{
	private final static long DEFAULT_PROCESS_TIMEOUT = 65000000;
	
	/**
	 * Will hold the output written to stdout by the process
	 */
	private String myErrString = "";
	
	/**
	 * Will hold the output written to the stdee by the process
	 */
	private String myStdString = "";
	
	/**
	 * Will hold the process exit value.
	 */
	private int myExitValue = 0;
	
	
	private boolean	_stopThread	= false;
	
	/**
	 * Execute command and return the command result printed to stdout+stderr.
	 * The method is blocked until process ended or timeout reached.
	 * @param theCommand - the command to execute
	 * @return the result
	 * @throws IOException 
	 * @throws TimeoutExecption - if time out reached 
	 */
	public String blockedExecProcess(String theCommand) throws IOException
	{
		return blockedExecProcess(theCommand,DEFAULT_PROCESS_TIMEOUT);
	}
	
	/**
	 * Execute command and return the command result printed to stdout+stderr.
	 * The method is blocked until process ended or timeout reached. 
	 * @param theCommand - the command to execute
	 * @param theTimeout - waiting for process to end
	 * @return the result
	 * @throws IOException
	 * @throws TimeoutExecption - thrown if time out reached.
	 */
	public String blockedExecProcess(String theCommand,long theTimeout) throws IOException
	{
		return blockedExecProcess(theCommand,null,theTimeout);
	}
	
	/**
	 * Execute command and return the command result printed to stdout+stderr.
	 * The method is blocked until process ended or timeout reached. 
	 * @param theCommand - the command to execute
	 * @param theTimeout - waiting for process to end
	 * @param theDir - set the working dir of the process
	 * @return the result
	 * @throws IOException
	 * @throws TimeoutExecption - thrown if time out reached.
	 */
	public String blockedExecProcess(String theCommand,File theDir,long theTimeout) throws IOException
	{
		Process p = null;
		
		p = Runtime.getRuntime().exec(theCommand,null,theDir);
		
		InputStream std = p.getInputStream();
		InputStream err = p.getErrorStream();
		
		ProcessStreamReader errReader = new ProcessStreamReader(err);
		ProcessStreamReader stdReader = new ProcessStreamReader(std);
        
				
		long startTime = System.currentTimeMillis();
		
		while( (!errReader.isFinished() || !stdReader.isFinished()))
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{}
			if ((System.currentTimeMillis() - startTime > theTimeout) && _stopThread == false)
			{
				errReader.kill();
				stdReader.kill();
				p.destroy();
				myStdString = stdReader.getResult();
			    myErrString = errReader.getResult();
			    myExitValue = p.exitValue();
				System.out.println("Got timeout while executing process");
			}
		}
	    myStdString = stdReader.getResult();
	    myErrString = errReader.getResult();
	    myExitValue = p.exitValue();
	    try
		{
			p.waitFor();
		}
		catch (InterruptedException e)
		{}
		p.destroy();
		return myStdString + myErrString; 
	}

	/**
	 * @return the output written to the stdout by the process
	 */
	public String getErrString()
	{
		return myErrString;
	}
	
	/**
	 * @return the process exit value.
	 */
	public int getExitValue()
	{
		return myExitValue;
	}
	
	/**
	 * @return the output written to the stderr by the process
	 */
	public String getStdString()
	{
		return myStdString;
	}
	
	/**
	 * Kill the process. 
	 */
	public void kill()
	{
		_stopThread = true;	
	}
	
	
}
