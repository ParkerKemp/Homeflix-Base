/*Homeflix-Base: Llamabrarian
 * 
 * Homeflix project for WKU CS496
 * Richie Davidson, Parker Kemp, Colin Page
 * Spring Semester 2014
 * 
 * Monitor a user-specified directory
 * Curates database to reflect this
 * Passes relevant info to ClientThread to update Mobile
 */

package com.thundercats.homeflix_base;

import java.io.File;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

import com.mysql.jdbc.Driver;

public class Llamabrarian implements Runnable {

	static ArrayList<MediaInfo> mediaInfo = new ArrayList<MediaInfo>();
	
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost";///homeflixbase";
	static final String USER = "root";
	static final String PASS = "password";
	
	static Connection conn = null;
	static Statement stmt = null;

	public static Path dir = HomeflixBase.myDir;

	// dir = Paths.get(setHFDir());

	public Llamabrarian() {

	}

	@Override
	public void run() {
		// Path myDir =
		// Paths.get("/Users/iamparker/Desktop/Homeflix-vids");//System.getProperty("user.dir"));
		// String myDirS = ".";
		// define a folder root
		connectToDatabase();
		scanDirectory();
		testForDirectoryChange(dir);// , myDirS);
		// System.out.println(videoList(myDirS));
	}
	
	public static void connectToDatabase() {
		try {
			String sql;
			
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			
			//Make sure the database exists
			stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS HomeflixBase");
			sql = "USE HomeflixBase";
			stmt.executeQuery(sql);
			
			//Make sure the Library table exists
			sql = "CREATE TABLE IF NOT EXISTS Library(filename VARCHAR(256) PRIMARY KEY, playbackTime INT)";
			stmt.executeUpdate(sql);
			//stmt.close();
			//conn.close();
		}

		catch (SQLException se) {
			se.printStackTrace();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void scanDirectory(){
		File folder = new File(dir.toString());
		File[] listOfFiles = folder.listFiles();
		mediaInfo.clear();
		for(int i = 0; i < listOfFiles.length; i++)
			mediaInfo.add(new MediaInfo(dir.toString(), listOfFiles[i].getName()));
		for(int i = 0; i < mediaInfo.size(); i++)
			if(mediaInfo.get(i).isValid())
				sqlInsert(mediaInfo.get(i).getFilename(), mediaInfo.get(i).getLength());
	}
	
	public static void sqlInsert(String filename, long playbackTime){
		try {
			String query = "INSERT INTO Library (filename, playbackTime) VALUES ('"
					+ filename +"', '" + playbackTime + "') ON DUPLICATE KEY UPDATE "
					+ "filename = '" + filename + "'";
			System.out.println(query);
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void testForDirectoryChange(Path myDir) {// , String myDirS){
		String[] videoNames;
		System.out.println(myDir.toString());
		try {
			WatchService watcher = myDir.getFileSystem().newWatchService();
			myDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_DELETE,
					StandardWatchEventKinds.ENTRY_MODIFY);

			WatchKey watckKey = watcher.take();

			while (true) { // watch for events and print out the kind of event
				List<WatchEvent<?>> events = watckKey.pollEvents();
				for (WatchEvent event : events) {
					if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
						System.out.println("Created: "
								+ event.context().toString());
					}
					if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
						System.out.println("Delete: "
								+ event.context().toString());

						// test code
						videoNames = videoList();// myDir.toString());
						for (int i = 0; i < videoNames.length; i++) {
							System.out.println(videoNames[i]);
						}
						// /test code

					}
					if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
						System.out.println("Modify: "
								+ event.context().toString());
					}
				}
			}

		} catch (Exception e) {
			System.out.println("Error: " + e.toString());
		}
	}

	// Get list of video files in directory and pass as String[]
	public static String[] videoList()// String myDirS)
	{
		// Reorganize this to take advantage of db
		String files;
		int j = 0;
		File folder = new File(dir.toString());

		// listOfFiles here gets info from directory. Replace this with db
		// access
		File[] listOfFiles = folder.listFiles();

		// System.out.println(listOfFiles.length);

		String[] vidFiles = new String[listOfFiles.length];
		String[] vidFiles2;

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				files = listOfFiles[i].getName();
				if (files.endsWith(".mov") || files.endsWith(".MOV")
						|| files.endsWith(".avi") || files.endsWith(".mp4")) {
					// System.out.println(files);//debug
					vidFiles[j] = files;
					j++;
				}
			}
		}

		vidFiles2 = new String[j];
		for (int i = 0; i < j; i++) {
			vidFiles2[i] = vidFiles[i];
		}

		return vidFiles2;
	}

	// new function required, like videoList but sends play times IN ORDER
	// current setup will basically be total crap
	public static String[] playTimeList() {
		String files;
		int j = 0;
		File folder = new File(dir.toString());

		File[] listOfFiles = folder.listFiles();

		String[] playTimes = new String[listOfFiles.length];
		String[] playTimes2;

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				files = listOfFiles[i].getName();
				if (files.endsWith(".mov") || files.endsWith(".MOV")
						|| files.endsWith(".avi") || files.endsWith(".mp4")) {
					playTimes[j] = files;
					j++;
				}
			}
		}

		playTimes2 = new String[j];
		for (int i = 0; i < j; i++) {
			playTimes2[i] = "00:00:10";
		}

		return playTimes2;
	}

	public static String setHFDir() {

		return "C:\\Users\\Richie\\TestVids";// default dummy directory
	}

}