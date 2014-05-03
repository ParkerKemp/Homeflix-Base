/*Homeflix-Base: ServerThread
 * 
 * Homeflix project for WKU CS496
 * Richie Davidson, Parker Kemp, Colin Page
 * Spring Semester 2014
 * 
 * Infinite loop that accepts incoming connections
 * and starts up a new thread for each one
 * 
 */

package com.thundercats.homeflix_base;

import java.io.*;
import java.net.*;
 
public class ServerThread implements Runnable{
	
	private int port;
	
	public ServerThread(int port){
		this.port = port;
	}
	
	@Override
	public void run(){
        ServerSocket s = null;
        Socket conn = null;
         
        try{
        	//Create a new server socket
            s = new ServerSocket(port);
            
            //Wait for incoming connections
            HomeflixBase.echo("Homeflix Base server online and waiting for incoming connections");
            while(true){
                
            	//Get a connection socket (accept() blocks until connection is received)
                conn = s.accept();
                 
                //Print connection information
                HomeflixBase.echo("Connection received from " + conn.getInetAddress().getHostName() + " : " + conn.getPort() + "\n");
                
                //Start a new thread to handle the client
                new ClientThread(conn).start();
            }
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
        
        try{
        	//Close the socket
            s.close();
        }
        catch(IOException ioException){
            System.err.println("Unable to close. IOexception");
        }
    }
}
 
