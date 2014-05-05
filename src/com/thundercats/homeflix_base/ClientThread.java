/*Homeflix-Base: ClientThread
 * 
 * Homeflix project for WKU CS496
 * Richie Davidson, Parker Kemp, Colin Page
 * Spring Semester 2014
 * 
 * Acts as client, receives messages from Mobile and interprets commands
 * Returns relevant info to Mobile when necessary
 */

package com.thundercats.homeflix_base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ClientThread extends Thread {

	private VLCServer serverInstance = new VLCServer();
	private Socket conn;
	private PrintStream out;
	private BufferedReader in;

	ClientThread(Socket conn) {
		this.conn = conn;
	}

	@Override
	public void run() {
		String line;
		try {
			// get socket writing and reading streams
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			out = new PrintStream(conn.getOutputStream());

			// Continuously read the socket for incoming messages.
			// readLine() blocks until data is received.
			while ((line = in.readLine()) != null && !line.equals("."))
				parseRequest(line);

			// client disconnected, so close socket
			HomeflixBase.echo("Connection closed.");
			conn.close();
		}

		catch (IOException e) {
			HomeflixBase.echo("IOException on socket : " + e);
			e.printStackTrace();
		}
	}

	public boolean parseRequest(String line) {
		//Interpret and handle a request from Mobile.
		//Return true if a command was processed, false otherwise

		String[] tokens = line.split(" ");
		String command = tokens[0];
		
		//If message from Mobile starts with "info", then return information about that video
		if (command.equalsIgnoreCase("info")){
			String filename = line.substring(5);
			out.println("INFO " + Llamabrarian.infoString(filename));
			return true;
		}
		
		// if message from Mobile is "play x" then make a stream for that file
		if (command.equalsIgnoreCase("play") && tokens.length > 1) {
			HomeflixBase.sysTraySet(HomeflixBase.HFiconPlay,
					"Playing to Mobile");
			String filename = line.substring(5);
			serverInstance.startVLCInstance(filename);
			out.println("READY " + filename);
			return true;
		}

		// if message from Mobile is 'Request File List" then send formatted
		// info to Mobile
		if (command.equalsIgnoreCase("RequestFileList")) {
			
			//Change tray icon to 'updating'
			HomeflixBase.sysTraySet(HomeflixBase.HFiconUD, "Updating Mobile...");
			
			sendFileListToMobile();
			
			//Change tray icon back to normal
			HomeflixBase.sysTraySet(HomeflixBase.HFicon, "Homeflix Base");
			return true;
		}

		//If message from mobile is "stop", then stop the VLC process
		//to save CPU power (it runs pretty hot)
		if (command.equalsIgnoreCase("stop")) {
			serverInstance.stopVLC();
			HomeflixBase.sysTraySet(HomeflixBase.HFicon, "Homeflix Base");
			return true;
		}

		return false;
	}

	public void sendFileListToMobile() {
		// Tell Mobile how many files there are

		String[] myFileInfo = Llamabrarian.getSqlFileList();
		out.println("FILE " + myFileInfo.length);
		// Then one by one, name each file and file play length
		for (int i = 0; i < myFileInfo.length; i++)
			out.println("FILE " + myFileInfo[i]);
	}
}
