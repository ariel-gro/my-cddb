package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;

import view.Activator;
import view.views.DbImportWizardPageTwo;

public class MainExtractFileThread extends Thread
{
	String firstFileToExtract;
	String secondFileToextract;
	String mainPath;
	URL fileUrl = null;

	public MainExtractFileThread(String firstFileToExtract) 
	{
		this.firstFileToExtract = firstFileToExtract;

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
		mainPath = firstFileToExtract.substring(0, firstFileToExtract.lastIndexOf("\\"));
		ExtractFileThread myExtractFile1 = new ExtractFileThread(firstFileToExtract, mainPath);
		myExtractFile1.start();
		
		DbImportWizardPageTwo.updateProgress("Starting to extract first file", 1);
		
		int i = 0;
		while(myExtractFile1.isAlive())
		{
			if(i%9==0 && i<300)
				DbImportWizardPageTwo.updateProgress("", (i/9)+1 );
			
			try {Thread.sleep(1000);} catch (InterruptedException e) {}
			
			i++;
		}
		DbImportWizardPageTwo.updateProgress("Finished extracting first file", 34);
		
		File tempPath = new File(mainPath + "\\temp");
		tempPath.mkdir();
		
		ExtractFileThread myExtractFile2 = new ExtractFileThread(firstFileToExtract.substring(0, firstFileToExtract.lastIndexOf(".")), mainPath + "\\temp");
		myExtractFile2.start();
		
		DbImportWizardPageTwo.updateProgress("Starting to extract music folders", 1);
		
		i = 0;
		int numOfBars=1;
		Map<String, Boolean> dirMap = new HashMap<String, Boolean>();
		while(myExtractFile2.isAlive())
		{
			File[] files = tempPath.listFiles();
			for (int j = 0; j < files.length; j++) 
			{
				if(files[j].isDirectory())
					if(dirMap.containsKey(files[j].getName()) == false)
					{
						DbImportWizardPageTwo.updateProgress("Extracting " + files[j].getName() + " folder", (j*3)+1);
						dirMap.put(files[j].getName(), true);
						numOfBars = (j*3)+1;
						i=0;
					}
			}
			
			try {Thread.sleep(5000);} catch (InterruptedException e) {}
			
			i++;
			if(i%120==0 && i<360)
			{
				numOfBars+=1;
				DbImportWizardPageTwo.updateProgress("", numOfBars);
			}
				
		}
		DbImportWizardPageTwo.updateProgress("Finished extracting music directories", 34);
	}
}
