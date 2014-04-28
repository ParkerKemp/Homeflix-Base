/*Homeflix-Base: HomeflixBase
 * 
 * Homeflix project for WKU CS496
 * Richie Davidson, Parker Kemp, Colin Page
 * Spring Semester 2014
 * 
 * 
 */

package com.thundercats.homeflix_base;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
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
	
	//Tray Icon prep
	public static SystemTray tray = SystemTray.getSystemTray();
	public static TrayIcon trayIcon;
	public static Image HFicon = new ImageIcon("src/resources/HFiconLR.png").getImage();
	public static Image HFiconUD = new ImageIcon("src/resources/HFiconUpdateLR.png").getImage();
	public static Image HFiconPlay = new ImageIcon("src/resources/HFiconPlayingLR.png").getImage();
	
	//File Chooser prep
	public static int returnVal;
	public static final JFileChooser fc = new JFileChooser();
	public static Path myDir;
	
	public static void main(String[] args){
		Logger.setLogFile("log.txt");
		
		//String[] files = {"/Users/iamparker/Desktop/Movies/manfromearth.mp4"};
		
		JFrame frame = new JFrame();
		textArea = new JTextArea();
		sysTraySetup();
		
		//try {
		//	Process process = Runtime.getRuntime().exec("cd external-jars/MacOS;./vlc --ttl 12 -vvv --color -I telnet --telnet-port 2465 --telnet-password videolan --rtsp-port 2464");
		//} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		
		VLCStream.loadNative();
		//VLCStream.startTelnetServer();
		//new Thread(new VLCServer("/Users/iamparker/Desktop/Homeflix-vids/django.avi")).start();
		//new VLCServer().startVLCInstance("/Users/iamparker/Desktop/Homeflix-vids/django.avi");
		//new Thread(new VLCStream("Test")).start();
		//VLCStream.launchStream("Test");//"/Users/iamparker/Desktop/Movies/django.avi");
		
		new Thread(new CheckOwnIP()).start();
		
		chooseDirectory();//must choose directory BEFORE Llamabrarian is initialized
		
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
		
		echo("Home directory chosen: " + myDir);
		
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
				//echo("Checking next interface...");
			    NetworkInterface current = interfaces.nextElement();
			    //System.out.println(current);
			    if (!current.isUp() || current.isLoopback() || current.isVirtual()){
			    	//echo("Network interface is down, or is a loopback interface or virtual interface. Skipping.");
			    	continue;
			    }
			    Enumeration<InetAddress> addresses = current.getInetAddresses();
			    while (addresses.hasMoreElements()){
			    	//echo("Checking next address...");
			        InetAddress current_addr = addresses.nextElement();
			        if(current_addr.isLoopbackAddress()){
			        	//echo("Skipping loopback address.");
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
	
	public static void chooseDirectory(){
		echo("choose dir");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setDialogTitle("Directory Chooser");
		fc.setAcceptAllFileFilterUsed(false);
		//returnVal = fc.showDialog(textArea, "Select your video library directory");
		
		if (fc.showDialog(textArea, "Select") == JFileChooser.APPROVE_OPTION) { 
		      System.out.println("getCurrentDirectory(): " 
		         +  fc.getCurrentDirectory());
		      System.out.println("getSelectedFile() : " 
		         +  fc.getSelectedFile());
		      
		      myDir = fc.getCurrentDirectory().toPath();
		}
		else {
			System.out.println("No Selection");
		}
	}
	
	public static void sysTraySetup(){
		if (SystemTray.isSupported()) {
		    // create a action listener to listen for default action executed on the tray icon
		    ActionListener listener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            // execute default action of the application
		            // ...
		        }
		    };
		    // create a popup menu
		    PopupMenu popup = new PopupMenu();
		    // create menu item for the default action
		    /*
		    MenuItem defaultItem = new MenuItem(...);
		    defaultItem.addActionListener(listener);
		    popup.add(defaultItem);
		    */
		    /// ... add other items
		    // construct a TrayIcon
		    trayIcon = new TrayIcon(HFicon, "Homeflix Base", popup);
		    // set the TrayIcon properties
		    trayIcon.addActionListener(listener);
		    // ...
		    // add the tray image
		    try {
		        tray.add(trayIcon);
		    } catch (AWTException e) {
		        System.err.println(e);
		    }
		    // ...
		} else {
		    // disable tray option in your application or
		    // perform other actions
		    //...
		}
	}
	
	public static void sysTrayUpdate(){
		if (SystemTray.isSupported()) {
			tray.remove(trayIcon);
		    PopupMenu popup = new PopupMenu();
		    trayIcon = new TrayIcon(HFiconUD, "Updating Mobile app...", popup);
		    try {
		        tray.add(trayIcon);
		    } catch (AWTException e) {
		        System.err.println(e);
		    }
		} else {
		}
	}
	
	public static void sysTrayNormal(){
		if (SystemTray.isSupported()) {
			tray.remove(trayIcon);
		    PopupMenu popup = new PopupMenu();
		    trayIcon = new TrayIcon(HFicon, "Homeflix Base", popup);
		    try {
		        tray.add(trayIcon);
		    } catch (AWTException e) {
		        System.err.println(e);
		    }
		} else {
		}
	}
	
	public static void sysTrayPlaying(){
		if (SystemTray.isSupported()) {
			tray.remove(trayIcon);
		    PopupMenu popup = new PopupMenu();
		    trayIcon = new TrayIcon(HFiconPlay, "Streaming to Mobile", popup);
		    try {
		        tray.add(trayIcon);
		    } catch (AWTException e) {
		        System.err.println(e);
		    }
		} else {
		}
	}
}
