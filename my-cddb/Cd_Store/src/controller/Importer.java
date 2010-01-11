package controller;

import java.io.*;
import java.sql.*;
import java.util.HashMap;

public class Importer {

//		public static void main(String[] argv){
//			Importer imp = new Importer(new DbConnector());
//			try {
//				imp.run("C:\\test");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
	/**
	 * Private map for temporary saved genres. Used to lighten the burden on the
	 * SQL-server.
	 */
	private HashMap<String,Integer> tmpGenres = new HashMap<String,Integer>(100);
	private HashMap<String,Integer> tmpArtist = new HashMap<String,Integer>(100);
	private HashMap<String,Integer> tmpDiscTitle = new HashMap<String,Integer>(100);
	private HashMap<String,Integer> tmpYear = new HashMap<String,Integer>(100);
	private HashMap<String[],Integer> tmpDiscId = new HashMap<String[],Integer>(100);
	private HashMap<String,Integer> tmpTracksTitle = new HashMap<String,Integer>(100);

	/**
	 * Private connection to the SQL-server.
	 */
	private DbConnector connector;

	public Importer(DbConnector conn) {
		connector = conn;
	}

	/**
	 * Run the parser on all files in the directory given as argument.
	 *
	 * @param dir_path Path to the directory to parse
	 * @throws IOException 
	 */
	public void run(String dir_path) throws IOException {
		File f = new File(dir_path);

		connector.openConnection();

		if (f.isDirectory()) {
			File[] files = f.listFiles();
			File file;
			Parser par = null;

			// Go through every file in directory
			for (int i = 0; i < files.length; i++) {
				file = files[i];
				if (file.isDirectory())
					this.run(file.getPath());
				else{
					par = new Parser(file);
					if (par.isEligableFormat()) {
						// Run the parser for this particular file
						if (par.run()) {
							// Reset variables
							String sGenre = "unknown";
							int genre = 0;
							String disc = "0";

							// Get name of genre
							if (par.getDgenre().trim().length() > 0) {
								sGenre = par.getDgenre().toLowerCase();
							}else{
								sGenre = f.getName().toLowerCase();
							}

							//testing and print
							//								System.out.println(par.getDgenre());
							//								System.out.println(par.getDiscLength());
							//								System.out.println(par.getDtitle());
							//								System.out.println(par.getDyear());
							//								String[] discid = par.getDiscId();
							//								for (int j = 0; j<discid.length;j++)
							//									System.out.println(discid[j]);
							//								System.out.println(par.getTtitle());

							//								// Save data
							//								genre = saveGenre(sGenre);
							//								String[] discid = saveDiscIds(par.getDiscId());
							//								disc = saveDisc(par, genre);
							//								saveTracks(disc, par.getTtitle());
							//								saveDiscIdRef(discid, disc);
						}
					}
					par.close();
				}
			}
		}
		connector.close();
	} 

	private int saveGenre(String sGenre) {
		int genre = 0;

		try {
			genre = ((Integer)tmpGenres.get(sGenre)).intValue();
		} catch (NullPointerException e) {
			genre = 0;
			e.printStackTrace();
		}

		if (genre == 0) {
			// First try to look the genre up in the database (most common case)
			ResultSet rs = null;
			connector.setQuery("SELECT id FROM genres WHERE name=?");
			try {
				connector.setString(1, sGenre);
				rs = connector.executeQuery();
				if (rs.first()) {
					genre = rs.getInt(1);
				}
				connector.closeStatement();
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (genre == 0) {
				// May be a new genre, save it
				connector.setQuery("INSERT IGNORE INTO genres (name) VALUES (?)");
				try {
					connector.setString(1, sGenre);
					connector.executeUpdate();
				} catch (NullPointerException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				connector.closeStatement();
			}
			tmpGenres.put(sGenre, new Integer(genre));
		}

		return genre;
	}

	private String[] saveDiscIds(String[] arrDiscid) {
		int ins = 0;
		int discidCount = 0;

		// Save discid and get generated key from database for later use
		String[] discid = new String[arrDiscid.length];
		for (int j = 0; j < arrDiscid.length; j++) {
			if (arrDiscid[j].trim().length() > 0) {
				ins = 0;
				connector.setQuery("INSERT INTO discids SET discid=CONV(?,16,10)", true);
				try {
					connector.setString(1, arrDiscid[j]);
					connector.executeUpdate();

					ResultSet newID = connector.getGeneratedKeys();
					if (newID.first()) {
						ins = newID.getInt(1);
					}
					connector.closeStatement();
				} catch (NullPointerException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				if (ins == 0) {
					discid[discidCount] = "0";
				}else{
					discid[discidCount] = String.valueOf(ins);
				}
			}
			discidCount++;
		}
		return discid;
	}

	private String saveDisc(Parser par, int genre) {
		String disc = "0";
		// Save info in freedb.discs and get discs.disc
		connector.setQuery("INSERT INTO discs SET length=?, title=?, year=?, genre=?", true);

		try {
			connector.setInt(1, par.getDiscLength());
			connector.setString(2, par.getDtitle().trim());
			connector.setInt(3, par.getDyear());
			connector.setInt(4, genre);
			connector.executeUpdate();

			ResultSet newId = connector.getGeneratedKeys();
			if (newId.first()) {
				disc = String.valueOf(newId.getInt(1));
			}

			connector.closeStatement();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return disc;
	}

	private void saveTracks(String disc, String[] ttitles) {
		for (int j = ttitles.length - 1; j > 0; j--) {
			if (ttitles[j] != null) {
				connector.setQuery("INSERT INTO tracks SET disc=?, title=?");
				try {
					connector.setInt(1, Integer.parseInt(disc));
					if (ttitles[j].length() > 250) {
						connector.setString(2, ttitles[j].substring(0, 250));
					}else{
						connector.setString(2, ttitles[j]);
					}
					connector.executeUpdate();
				} catch (NullPointerException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				connector.closeStatement();
			}
		}
	}

	private void saveDiscIdRef(String[] discid, String disc) {
		for (int j = 0; j < discid.length; j++) {
			connector.setQuery("INSERT INTO discid_disc SET pri=?, disc=?");
			try {
				connector.setInt(1, Integer.parseInt(discid[j]));
				connector.setInt(2, Integer.parseInt(disc));
				connector.executeUpdate();
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			connector.closeStatement();
		}
	}
}

