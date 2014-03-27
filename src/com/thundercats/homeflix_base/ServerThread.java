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
                new client_handler(conn).start();
            }
        }
         
        catch(IOException e){
            System.err.println(e.toString());
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
 
class client_handler extends Thread{
    private Socket conn;
     
    client_handler(Socket conn){
        this.conn = conn;
    }
 
    public void run(){
        String line;
         
        try{
            //get socket writing and reading streams
            //DataInputStream in = new DataInputStream(conn.getInputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            PrintStream out = new PrintStream(conn.getOutputStream());
 
            //Send welcome message to client
            out.println("Server: Welcome to the Server!");
            HomeflixBase.echo("Server: Welcome to the Server!");
 
            LocalVideoPlayer player; 
            //Now start reading input from client
            while((line = in.readLine()) != null && !line.equals(".")){
            	HomeflixBase.echo("Client: " + line);
            	if(line.split(" ")[0].equalsIgnoreCase("play")){
            		HomeflixBase.echo("\nTrying to play " + line.split(" ")[1] + " in same directory as Homeflix-Base.jar...\n");
            		new Thread(new LocalVideoPlayer(System.getProperty("user.dir") + File.separator + line.split(" ")[1])).start();
            	}else{
            	//reply with the same message, adding some text
            	out.println("Server received: " + line);
                
                }
            }
            
            HomeflixBase.echo("Done listening");
            
            //client disconnected, so close socket
            conn.close();
        } 
       
        catch (IOException e){
            HomeflixBase.echo("IOException on socket : " + e);
            e.printStackTrace();
        }
    }
}