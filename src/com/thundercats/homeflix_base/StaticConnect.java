/*Homeflix-Base: StaticConnect
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

public class StaticConnect implements Runnable{
	Socket socket;
	String host, message;
	int port;
	
	public StaticConnect(String host, int port, String message){
		socket = new Socket();
		this.host = host;
		this.port = port;
		this.message = message;
	}
	
	@Override
	public void run(){
		while(!socket.isConnected())
			connect();
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println(message);
			System.out.println("Sent " + message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		
		/*try {
			bufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bufferOut = new PrintWriter(socket.getOutputStream(), true);
			new Thread(new Reader(bufferIn)).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
