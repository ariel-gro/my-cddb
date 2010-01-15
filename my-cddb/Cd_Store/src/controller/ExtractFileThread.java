package controller;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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
	boolean extractToTextFile;
	String textFile;
	URL fileUrl = null;
	String theCommand;
	File tempBatch;

	public ExtractFileThread(String firstFileToExtract, String pathToExtractTo, boolean extractToTextFile, String textFile) 
	{
		this.firstFileToExtract = firstFileToExtract;
		this.pathToExtractTo = pathToExtractTo;
		this.extractToTextFile = extractToTextFile;
		this.textFile = textFile;

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
		String pathAndCommand = fileUrl.getPath();
		if(extractToTextFile)
		{
			pathAndCommand = pathAndCommand.substring(1);
			theCommand = "cd \"" + pathAndCommand.substring(0, pathAndCommand.lastIndexOf("/")) + "\"\n7za.exe x " + firstFileToExtract + " -so > " + pathToExtractTo + textFile;
			theCommand = theCommand.replace("/", "\\");
			
			tempBatch = new File(pathToExtractTo + "batch.bat");
			try {
				setContents(tempBatch, theCommand);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			theCommand = tempBatch.getPath();
		}
		else
			theCommand = fileUrl.getPath() + " x " + firstFileToExtract + " -o" + pathToExtractTo;
	
			
		RunProcess proc = new RunProcess();
		try 
		{
			System.out.println("Running: " + theCommand);
			String commandOutput = proc.blockedExecProcess(theCommand);
			System.out.println(commandOutput);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if(extractToTextFile)
			tempBatch.delete();
	}

	static public void setContents(File file, String content) throws IOException
	{
		Writer output = new BufferedWriter(new FileWriter(file));
		try {
			output.write(content);
		} finally {
			output.close();
		}
	}
}
