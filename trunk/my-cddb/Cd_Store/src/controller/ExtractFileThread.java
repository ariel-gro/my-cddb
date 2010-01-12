package controller;


import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import view.Activator;

public class ExtractFileThread extends Thread
{
	String firstFileToExtract;
	String pathToExtractTo;
	URL fileUrl = null;

	public ExtractFileThread(String firstFileToExtract, String pathToExtractTo) 
	{
		this.firstFileToExtract = firstFileToExtract;
		this.pathToExtractTo = pathToExtractTo;

		Bundle bundle = Activator.getDefault().getBundle();
		Path path = new Path("util/7za.exe");
		URL url = FileLocator.find(bundle, path, Collections.EMPTY_MAP);	
		try {
			fileUrl = FileLocator.toFileURL(url);
		} catch (IOException e) {
			// Will happen if the file cannot be read for some reason
			e.printStackTrace();
		}
	}

	public void run()
	{
		String theCommand = fileUrl.getPath() + " x " + firstFileToExtract + " -o" + pathToExtractTo;
		
		RunProcess proc = new RunProcess();
		try 
		{
			String commandOutput = proc.blockedExecProcess(theCommand);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
