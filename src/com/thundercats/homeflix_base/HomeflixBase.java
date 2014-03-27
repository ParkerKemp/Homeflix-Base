package com.thundercats.homeflix_base;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

public class HomeflixBase {
	public static JTextArea textArea;
	public static void main(String[] args){
		
		//new Thread(new LocalVideoPlayer(System.getProperty("user.dir") + "test.MOV");
		//new Thread(new LocalVideoPlayer("/Users/iamparker/Desktop/Movies/TheDeparted/departed.mp4")).start();
		//System.out.println(System.getProperty("user.dir") +"test.MOV");
		JFrame frame = new JFrame();
		textArea = new JTextArea();
		//echo("Working directory: " + System.getProperty("user.dir") + "/test.MOV");
		
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
		//showAddresses();
		
		new Thread(null, new ServerThread(6000), "Server-Thread").start();
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
			    NetworkInterface current = interfaces.nextElement();
			    //System.out.println(current);
			    if (!current.isUp() || current.isLoopback() || current.isVirtual()) continue;
			    Enumeration<InetAddress> addresses = current.getInetAddresses();
			    while (addresses.hasMoreElements()){
			        InetAddress current_addr = addresses.nextElement();
			        if(current_addr.isLoopbackAddress())
			        	continue;
			        if(current_addr instanceof Inet4Address)
			        	inet4Addresses.add(current_addr);
			        	//return current_addr;
			    }
			}
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return inet4Addresses;
	}
	
}
