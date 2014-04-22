/*Homeflix-Base: HomeflixBase
 * 
 * Homeflix project for WKU CS496
 * Richie Davidson, Parker Kemp, Colin Page
 * Spring Semester 2014
 * 
 * 
 */

package com.thundercats.homeflix_base;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import uk.co.caprica.vlcj.player.manager.MediaManager;

import com.xuggle.xuggler.Converter;

public class HomeflixBase {
	
	private static final int port = 2463;
	private static final int rtspPort = 2464;
	public static JTextArea textArea;
	MediaManager manager;
	
	public static void main(String[] args){
		Logger.setLogFile("log.txt");
		
		String[] files = {"/Users/iamparker/Desktop/Movies/manfromearth.mp4"};
		
		JFrame frame = new JFrame();
		textArea = new JTextArea();
		
		//try {
		//	Process process = Runtime.getRuntime().exec("cd external-jars/MacOS;./vlc --ttl 12 -vvv --color -I telnet --telnet-port 2465 --telnet-password videolan --rtsp-port 2464");
		//} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		
		VLCStream.loadNative();
		new Thread(new CheckOwnIP()).start();
		
		new Thread(new Llamabrarian()).start();
		
		//new Thread(new VLCStream("/Users/iamparker/Desktop/Movies/manfromearth.mp4", "172.31.77.246", rtspPort)).start();
		
		JScrollPane scrollPane = new JScrollPane(textArea);
		
		textArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		frame.add(scrollPane);
		
		frame.setSize(600,400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		echo("Starting Homeflix Base.\n");
		
		HomeflixBase.echo("Make sure your mobile device is on the same Wifi network before connecting.\n");
        HomeflixBase.showAddresses();
        
		showInstructions();
		
		new Thread(null, new ServerThread(port), "Server-Thread").start();
	}
	
	public static void showAddresses(){
		ArrayList<InetAddress> addresses = getLocalAddresses();
		if(addresses.size() == 0)
			echo("No IPv4 addresses found!");
		else
			echo("Connect with IP address: ");
		for(int i = 0; i < addresses.size(); i++)
			echo(addresses.get(i).getHostAddress());
		echo("");
	}
	
	public static void showInstructions(){
		echo("To play a video, first connect with Homeflix Mobile. In the message textbox on the app, send \"play <filename>\", where <filename> is the name of a file in the same directory as Homeflix-Base.jar.\n");
	}
	
	public static void echo(String msg){
		System.out.println(msg);
		textArea.append(msg + "\n");
	}
	
	public static ArrayList<InetAddress> getLocalAddresses(){
		ArrayList<InetAddress> inet4Addresses = new ArrayList<InetAddress>();
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()){
				echo("Checking next interface...");
			    NetworkInterface current = interfaces.nextElement();
			    //System.out.println(current);
			    if (!current.isUp() || current.isLoopback() || current.isVirtual()){
			    	echo("Network interface is down, or is a loopback interface or virtual interface. Skipping.");
			    	continue;
			    }
			    Enumeration<InetAddress> addresses = current.getInetAddresses();
			    while (addresses.hasMoreElements()){
			    	echo("Checking next address...");
			        InetAddress current_addr = addresses.nextElement();
			        if(current_addr.isLoopbackAddress()){
			        	echo("Skipping loopback address.");
			        	continue;
			        }
			        if(current_addr instanceof Inet4Address){
			        	echo("Found a valid address at " + current_addr.getHostAddress());
			        	inet4Addresses.add(current_addr);
			        }
			        	//return current_addr;
			    }
			}
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		echo("");
		return inet4Addresses;
	}
	
}
