/*Homeflix-Base: TelnetRequest
 * 
 * Homeflix project for WKU CS496
 * Richie Davidson, Parker Kemp, Colin Page
 * Spring Semester 2014
 * 
 * 
 */

package com.thundercats.homeflix_base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TelnetRequest{
	private Socket socket;
	private BufferedReader bufferIn;
	private PrintWriter bufferOut;
	private String host = "127.0.0.1";
	private int port = 2465;
	private String filename;
	
	public TelnetRequest(String filename){
		this.filename = filename;
	}
	
	public void start(){
		connect();
		bufferOut.println("videolan");
		bufferOut.println("new " + filename + " vod enabled");
		bufferOut.println("setup " + filename + " input " + filename);
	}
	
	public void connect(){
		try {
			socket = new Socket(InetAddress.getByName(host), port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			bufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bufferOut = new PrintWriter(socket.getOutputStream(), true);
			//new Thread(new Reader(bufferIn)).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
