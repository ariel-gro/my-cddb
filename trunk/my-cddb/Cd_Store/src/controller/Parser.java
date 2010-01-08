package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import model.Disk;
import model.Track;


public class Parser {
	
	private Disk newDisk;
	private BufferedReader br;
	private File inputDB;
	
	public Parser (File inputFile){
		if (this.isEligableCDDB(inputFile))
		{
			this.inputDB = inputFile;
			this.createNewDisc();
		}
	}
	
	private void createNewDisc() {
		String line;
		try {
			while ((line = br.readLine()) != null) {
				if (line.startsWith("# Disc length: ")) {
					newDisk.setTotalTime(line.split("\\s")[3]);
				}  
				if (line.startsWith("DISCID=")) {
						newDisk.setId(line.substring(line.indexOf("=")+1)); // Deprecated in definition
					} 
				if (line.startsWith("DTITLE=")) {
					newDisk.setTitle(line.substring(line.indexOf("=")+1));
				} else if (line.startsWith("DYEAR=")) {
					int len = line.substring(6).trim().length();
					if (len == 4) {
						newDisk.setYear(line.substring(line.indexOf("=")+1).trim());
						if (Integer.parseInt(newDisk.getYear()) > 10000) {
							newDisk.setYear("0");
						}
					} else {
						newDisk.setYear("0");
					}
				} else if (line.startsWith("DGENRE=")) {
					newDisk.setGenre(line.substring(line.indexOf("=")+1));
				} else if (line.startsWith("TTITLE")) {
					Track track = new Track();
					track.setNum(line.substring(6, line.indexOf("=")));
					track.setTitle(line.substring(line.indexOf("=")+1));
					newDisk.insertToTrackList(track);
					}
			}
				line = null;
		} catch (NumberFormatException e) {
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}
		
	
	private boolean isEligableCDDB(File inputDB){
		try {
			this.br = new BufferedReader(new FileReader(inputDB));
			if (br.readLine().startsWith("# xmcd"))
				return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public Disk getNewDisk() {
		return newDisk;
	}

}
