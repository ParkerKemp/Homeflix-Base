/*Homeflix-Base: ClientThread
 * 
 * Homeflix project for WKU CS496
 * Richie Davidson, Parker Kemp, Colin Page
 * Spring Semester 2014
 * 
 * Acts as client, receives messages from Mobile and interprets commands
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
	
    private Socket conn;
    private PrintStream out;
    private BufferedReader in;
    
    public int fileCount;//how many video files to pass to Mobile
    
    public String[] fileNames = new String[] {"Test1.MOV", "Test2.MOV", "test"};//file names to pass to Mobile. Test data used.
     
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
            	parseRequest(line);
            	
            	//parsedSentence = line.split(" ", 2);
            	//connectingIP = parsedSentence[0];
            	//line = parsedSentence[1];
            	HomeflixBase.echo("Client: " + line);
            	//if(line.length() == 0)
            	//	continue;
            	//if(line.charAt(0) == 'O' || line.charAt(0) == 'S' || line.charAt(0) == 'D' || line.charAt(0) == 'T' || line.charAt(0) == 'P')
            	//	parseRTSPRequest(line);
            	
            	//if(line.split(" ")[0].equalsIgnoreCase("play")){
            	//	HomeflixBase.echo("\nTrying to play " + line.split(" ")[1] + " in same directory as Homeflix-Base.jar...\n");
            		//new Thread(new LocalVideoPlayer(System.getProperty("user.dir") + File.separator + line.split(" ")[1])).start();
            	//	new Thread(new VLCStream(line.split(" ")[1], connectingIP, 2464)).start();
            	//}else{
            	
            	//reply with the same message, adding some text
            	//out.println("Server received: " + line);
                
               // }
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
    	
        String connectingIP;
        String [] parsedSentence;
        
        parsedSentence = line.split(" ", 2);
    	connectingIP = parsedSentence[0];
    	
    	line = parsedSentence[1];

    	//if(line.length() == 0)
    	//	return;
    	
    	String[] tokens = line.split(" ");
    	String command = tokens[0];
    	
    	//if message from Mobile is "play x" then make a stream for that file
    	if(command.equalsIgnoreCase("play") && tokens.length > 1){
    		String filename = tokens[1];
    		
    		//new Thread(new VLCStream(filename, connectingIP, 2464)).start();
    		return true;
    	}
    	
    	//if message from Mobile is 'Request File List" then send formatted info to Mobile
    	if(command.equalsIgnoreCase("RequestFileList")){
    		//HomeflixBase.echo("Ground COntrol receives");
    		//Tell Mobile how many files there are
    		fileCount = 3;//test code, assume 3 files
    		out.println(fileCount);
    		
    		//Then one by one, name each file
    		//later, may send other file info with it
    		for(int i=0; i<fileCount; i++)
    		{
                out.println(fileNames[i]);
            }
    		return true;
    	}
    	
    	return false;
    }
    
    public void parseRTSPRequest(String request){
    	String directive = firstWord(request);
    	if(directive.equals("OPTIONS"))
    		handleOptionsRequest(request);
    	else if(directive.equals("DESCRIBE"))
    		handleDescribeRequest(request);
    	//else if(request.split(" ")[0].equals())
    }
    
    public String firstWord(String line){
    	return line.split(" ")[0];
    }
    
    public void handleOptionsRequest(String request){
    	send("RTSP/1.0 200 OK\r\n"
    			+ "CSeq: 0\r\n"
    			+ "Public: PLAY, SETUP, TEARDOWN, PLAY, PAUSE\r\n");
    }
    
    public void handleDescribeRequest(String request){
    	int cseq = 1;
    	
    	String sdpString = /*"v=0\r\n"
    			+ "o=pk 1234567 2345678 IN IP4 " + externalIP + "\r\n"
    			+ "s=\r\n"
    			+ "t=0 0\r\n"
    			+ "m=video 0 RTP/AVP 26\r\n"
    			+ "a=control:trackID=0\r\n"
    			+ "c=IN IP4 " + conn.getRemoteSocketAddress() + "\r\n";*/
    	
    	"v=0\r\n"
    	//+ "o=- 819959427 819959427 IN IP4 " + externalIP + "\r\n"
    	//+ "s=BigBuckBunny_115k.mov\r\n"
    	//+ "c=IN IP4" + conn.getRemoteSocketAddress() + "\r\n"
    	//+ "t=0 0\r\n"
    	//+ "a=sdplang:en\r\n"
    	//+ "a=range:npt=0- 596.48\r\n"
    	//+ "a=control:*\r\n"
    	//+ "m=audio 0 RTP/AVP 96\r\n"
    	//+ "a=rtpmap:96 mpeg4-generic/12000/2\r\n"
    	//+ "a=fmtp:96 profile-level-id=1;mode=AAC-hbr;sizelength=13;indexlength=3;indexdeltalength=3;config=1490\r\n"
    	//+ "a=control:trackID=1\r\n"
    	+ "m=video 0 RTP/AVP 97\r\n"
    	+ "a=rtpmap:97 H264/90000\r\n"
    	+ "a=fmtp:97 packetization-mode=1;profile-level-id=42C01E;sprop-parameter-sets=Z0LAHtkDxWhAAAADAEAAAAwDxYuS,aMuMsg==\r\n"
    	//+ "a=cliprect:0,0,160,240\r\n"
    	//+ "a=framesize:97 240-160\r\n"
    	//+ "a=framerate:24.0\r\n"
    	+ "a=control:trackID=2"
    	+ "\r\n\r\n";
    	
    	send("RTSP/1.0 200 OK\r\n"
    			+ "CSeq: " + cseq + "\r\n"
    			+ "Content-Type: application/sdp\r\n"
    			+ "Content-Length: " + sdpString.length() + "\r\n\r\n"
    			+ sdpString);
    }
    
    public void send(String message){
    	out.println(message);
    	//HomeflixBase.echo(message);
    }
}
