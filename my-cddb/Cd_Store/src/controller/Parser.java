package controller;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class Parser {

	private File f;

	private BufferedReader br;

	private int length;

	private String[] discid = new String[1];

	private String dtitle;

	private int dyear;

	private String dgenre;

	private String[] ttitle = new String[200];

	/**
	 * Constructs a parser for the given file.
	 * 
	 * @param file
	 */
	public Parser(File file) {
		f = file;
	}

	/**
	 * Close and clean up the parser.
	 * @throws IOException 
	 */
	public void close() throws IOException {
			br.close();
			ttitle = null;
			dgenre = null;
			dtitle = null;
			discid = null;
			f = null;
	}

	/**
	 * Returns the disc length in seconds.
	 * @return seconds
	 */
	public int getDiscLength() {
		return length;
	}

	/**
	 * Return an array with strings representing all found disc IDs found.
	 * @return String array
	 */
	public String[] getDiscId() {
		return discid;
	}

	/**
	 * Return the title of the disc.
	 * @return Title
	 */
	public String getDtitle() {
		return dtitle;
	}

	/**
	 * Return the year of the disc release.
	 * @return year
	 */
	public int getDyear() {
		return dyear;
	}

	/**
	 * Return the genre of the disc.
	 * @return genre
	 */
	public String getDgenre() {
		return dgenre;
	}

	/**
	 * Returns all titles found in an array of strings.
	 * @return titles
	 */
	public String[] getTtitle() {
		return ttitle;
	}

	/**
	 * Determine if the file parsed is a CDDB file or not.
	 * @return True if it is a CDDB file, False otherwise
	 */
	public boolean isEligableFormat() {
		if (f.isFile()) {
			// Open file and check first row
			try {
				br = new BufferedReader(new FileReader(f));
				if (br.readLine().startsWith("# xmcd"))
					return true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

		}
		return false;
	}
	
	public boolean isPureAscii(String v) {
		byte bytearray []  = v.getBytes();
		CharsetDecoder d = Charset.forName("US-ASCII").newDecoder();
		    try {
		      CharBuffer r = d.decode(ByteBuffer.wrap(bytearray));
		      r.toString();
		    }
		    catch(CharacterCodingException e) {
		      return false;
		    }
		    return true;
		  }


	/**
	 * Run parser on the given file using the definition from
	 * http://www.freedb.org/modules.php?name=Sections&sop=viewarticle&artid=29
	 * 
	 * @return Returns <code>true</code> on success and <code>false</code>
	 *         on failure.
	 */
	public boolean run() {
		String line;
		try {
			// Read the file
			int title_index = 0;
			while ((line = br.readLine()) != null) {
				if (!isPureAscii(line)){
					return false;
				}
				if (line.startsWith("# Disc length: ")) {
					length = Integer.parseInt(line.split("\\s")[3]);
				} else if (line.startsWith("DISCID=")) {
					if (line.indexOf(",") == -1) {
						discid[0] = line.substring(line.indexOf("=")+1);
					}else{
						discid = line.substring(line.indexOf("=")+1).split(","); // Deprecated in definition
					}
				} else if (line.startsWith("DTITLE=")) {
					dtitle = line.substring(line.indexOf("=")+1);
				} else if (line.startsWith("DYEAR=")) {
					int len = line.substring(6).trim().length();
					if (len == 4) {
						dyear = Integer.parseInt(line.substring(line.indexOf("=")+1).trim());
						if (dyear > 10000) {
							dyear = 0;
						}
					} else {
						dyear = 0;
					}
				} else if (line.startsWith("DGENRE=")) {
					dgenre = line.substring(line.indexOf("=")+1);
				} else if (line.startsWith("TTITLE")) {
					title_index = Integer.parseInt(line.substring(6, line.indexOf("=")));
					if (ttitle[title_index] == null) {
						ttitle[title_index] = line.substring(line.indexOf("=")+1);
					} else {
						ttitle[title_index] += line.substring(line.indexOf("=")+1);
					}
				}
				line = null;
			}
		} catch (NumberFormatException e) {
			return false;
		} catch (IOException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		return true;
	}

}
