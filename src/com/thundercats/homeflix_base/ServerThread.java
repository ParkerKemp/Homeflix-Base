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
		init();
	}
	
    public void init(){
        ServerSocket s = null;
        Socket conn = null;
         
        try{
        	//creating a server socket - 1st parameter is port number and 2nd is the backlog
            s = new ServerSocket(port);
            //Wait for an incoming connection
            HomeflixBase.echo("Server socket created. Listening on port " + port + "...\n");
            while(true){
                //get the connection socket
                conn = s.accept();
                 
                //print the hostname and port number of the connection
                HomeflixBase.echo("Connection received from " + conn.getInetAddress().getHostName() + " : " + conn.getPort() + "\n");
                
                //create new thread to handle client
                new ClientThread(conn).start();
            }
        }
         
        catch(Exception e){
            System.out.println(e.toString());
        }
         
        //5. close the connections and stream
        try{
            s.close();
        }
         
        catch(IOException ioException){
            System.err.println("Unable to close. IOexception");
        }
    }
}
 
