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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ClientThread extends Thread{
	
	private final String externalIP = "172.31.95.94";
	
	VLCServer serverInstance = new VLCServer();
    private Socket conn;
    private PrintStream out;
    private BufferedReader in;
    
    //public String[] fileNames = new String[] {"Test1.MOV", "Test2.MOV", "test"};//file names to pass to Mobile. Test data used.
     
    ClientThread(Socket conn){
        this.conn = conn;
    }
 
    public void run(){
        String line;
        String connectingIP;
        String [] parsedSentence;
         
        try{
            //get socket writing and reading streams
            //DataInputStream in = new DataInputStream(conn.getInputStream());
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            out = new PrintStream(conn.getOutputStream());
 
            //Send welcome message to client
            //out.println("\r\n" + " Welcome to the Server!");
            //HomeflixBase.echo("\r\n" + " Welcome to the Server!");
 
            //LocalVideoPlayer player; 
            //Now start reading input from client
            while((line = in.readLine()) != null && !line.equals(".")){
            	//HomeflixBase.echo("Client: " + line);
            	parseRequest(line);

            }
            
            HomeflixBase.echo("Connection closed.");
            
            //client disconnected, so close socket
            conn.close();
        } 
       
        catch (IOException e){
            HomeflixBase.echo("IOException on socket : " + e);
            e.printStackTrace();
        }
    }
    
    public boolean parseRequest(String line){
    	//Return true if a command was processed, or false if it's just arbitrary text data
    	String[] tokens = line.split(" ");
    	String command = tokens[0];
    	
    	//if message from Mobile is "play x" then make a stream for that file
    	if(command.equalsIgnoreCase("play") && tokens.length > 1){
    		HomeflixBase.sysTraySet(HomeflixBase.HFiconPlay, "Playing to Mobile");
    		String filename = line.substring(5);
    		HomeflixBase.echo("Trying to play " + filename);
    		//new VLCStream(filename).init();
    		serverInstance.startVLCInstance(filename);
    		out.println("READY " + filename);
    		return true;
    	}
    	
    	//if message from Mobile is 'Request File List" then send formatted info to Mobile
    	if(command.equalsIgnoreCase("RequestFileList")){
    		HomeflixBase.sysTraySet(HomeflixBase.HFiconUD, "Updating Mobile...");//change tray icon to 'updating'
    		updateMobile();
    		HomeflixBase.sysTraySet(HomeflixBase.HFicon, "Homeflix Base");//change tray icon back to normal
    		return true;
    	}
    	
    	return false;
    }
    
    public void updateMobile(){
    	//HomeflixBase.echo("Ground Control receives");
		//Tell Mobile how many files there are
    	String[][] myFileInfo = Llamabrarian.videoList();//get list of files
		out.println("FILE " + myFileInfo.length);
		//Then one by one, name each file and file play length
		for(int i=0; i<myFileInfo.length; i++)
		{
            out.println("FILE " + myFileInfo[i][0] + ";" + myFileInfo[i][1]);
        }
    }
}
