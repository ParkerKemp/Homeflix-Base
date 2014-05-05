/*Homeflix-Base: Llamabrarian
 * 
 * Homeflix project for WKU CS496
 * Richie Davidson, Parker Kemp, Colin Page
 * Spring Semester 2014
 * 
 * Monitors a user-specified directory
 * Curates database to reflect this
 * Passes relevant info to ClientThread to update Mobile
 */

package com.thundercats.homeflix_base;

import java.io.File;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class Llamabrarian implements Runnable {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost";
	static final String USER = "root";
	static final String PASS = "";

	static Connection conn = null;
	static Statement stmt = null;

	public static Path dir;

	public Llamabrarian() {

	}

	@Override
	public void run() {
		watchDirectory();
	}
	
	public static void setDirectory(Path directory){
		dir = directory;
		connectToDatabase();
		syncDatabase();
	}

	private static void connectToDatabase() {
		//Establish an SQL connection and create the database
		//if it's not already there.
		
		//In theory, this only needed to be done once.
		if(conn != null)
			return;
		
		try {
			String query;

			// Connect to SQL server
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();

			// Make sure the database exists
			stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS HomeflixBase");

			// Use the database
			query = "USE HomeflixBase";
			stmt.executeQuery(query);

			// Make sure the Library table exists
			query = "CREATE TABLE IF NOT EXISTS Library(filename VARCHAR(255) PRIMARY KEY, playbackTime VARCHAR(16), videoCodec VARCHAR(16), audioCodec VARCHAR(16))";
			stmt.executeUpdate(query);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void syncDatabase() {
		//Ensure that the database contains all of and only the 
		//video files currently present in the library directory
		
		deleteDeadFilesFromDB();
		insertNewFiles();
	}

	private static void deleteDeadFilesFromDB() {
		//Delete records in the database for which the files no longer exist in the directory
		
		String[] dbList = getSqlFileList();
		
		//Database is empty, so do nothing
		if (dbList == null)
			return;
		
		for (int i = 0; i < dbList.length; i++)
			if (!fileExistsOnDisk(dbList[i]))
				deleteFromDB(dbList[i]);
	}

	private static void insertNewFiles() {
		//Insert any video files which aren't already in the database
		
		File[] listOfFiles = new File(dir.toString()).listFiles();
		for (int i = 0; i < listOfFiles.length; i++)
			if (!fileExistsInDB(listOfFiles[i].getName()))
				analyzeAndInsert(listOfFiles[i].getName());
	}

	public static String[] getSqlFileList() {
		//Get a list of filenames currenty stored in the database
		
		String[] ret = new String[getSqlRowCount()];
		int i = 0;
		
		String query = "SELECT filename FROM Library";
		
		try {
			
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next())
				ret[i++] = rs.getString("filename");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	private static int getSqlRowCount() {
		//Get the number of records in the database
		
		String query = "SELECT COUNT(*) FROM Library";
		try {
			
			ResultSet rs = stmt.executeQuery(query);
			rs.first();
			return rs.getInt("COUNT(*)");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//This code is only reached upon SQLException
		return 0;
	}

	private static void analyzeAndInsert(String filename) {
		//Extract metadata from a video file and (if it is 
		//actually a video file) insert it into the database
		
		MediaInfo temp = new MediaInfo(dir.toString(), filename);
		if (temp.isValid())
			insertIntoDB(temp.getFilename(), temp.getPlaybackLength(), temp.getVideoCodec(), temp.getAudioCodec());
	}

	private static void insertIntoDB(String filename, long playbackTime, String videoCodec, String audioCodec) {
		//Insert a new row into the database
		
		try {
			String query = "INSERT INTO Library (filename, playbackTime, videoCodec, audioCodec) VALUES ('"
					+ filename
					+ "', '"
					+ timeString(playbackTime)
					+ "', '"
					+ videoCodec
					+ "', '"
					+ audioCodec
					+ "') ON DUPLICATE KEY UPDATE "
					+ "filename = '"
					+ filename
					+ "'";
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static String infoString(String filename){
		String query = "SELECT * FROM Library WHERE filename = '" + filename + "'";
		String ret = "";
		try {
			ResultSet rs = stmt.executeQuery(query);
			rs.first();
			ret += rs.getString("filename") + ";";
			ret += rs.getString("playbackTime") + ";";
			ret += rs.getString("videoCodec") + ";";
			ret += rs.getString("audioCodec");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	private static String timeString(long milli){
		int totalSeconds = (int)milli / 1000;
		
		int hours = totalSeconds / 3600;
		int minutes = (totalSeconds % 3600) / 60;
		int seconds = totalSeconds % 60;
		
		String ret = "" + hours + ":" + minutes + ":" + seconds;
		return ret;
	}

	private static void deleteFromDB(String filename) {
		//Delete a row from the database
		
		String query = "DELETE FROM Library WHERE filename = '" + filename
				+ "'";
		try {
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static boolean fileExistsOnDisk(String filename) {
		//Return true if the file exists in the library directory
		
		File file = new File(dir + File.separator + filename);
		return file.exists() && !file.isDirectory();
	}

	private static boolean fileExistsInDB(String filename) {
		//Return true if the filename matches a record in the database
		
		String query = "SELECT * FROM Library WHERE filename = '" + filename
				+ "'";
		try {
			ResultSet rs = stmt.executeQuery(query);
			return rs.first();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//This code is only reached upon SQLException
		return false;
	}
	private static void watchDirectory() {
		//Watch the library directory until the end of time, or until
		//Homeflix Base stops (whichever comes first). This method contains
		//an infinite loop, so it must be the last thing executed on this thread
		
		try {
			
			//Register a new WatchService with the library directory
			WatchService watcher = dir.getFileSystem().newWatchService();
			dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_DELETE,
					StandardWatchEventKinds.ENTRY_MODIFY);

			WatchKey watckKey = watcher.take();

			//Watch for created/deleted files and update the database accordingly
			while (true) { 
				List<WatchEvent<?>> events = watckKey.pollEvents();
				for (WatchEvent<?> event : events) {
					
					//File created, analyze it and insert into the database if necessary
					if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE)
						analyzeAndInsert(event.context().toString());
					
					//File deleted, delete it from the database if it already exists
					if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE)
						if (fileExistsInDB(event.context().toString()))
							deleteFromDB(event.context().toString());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}