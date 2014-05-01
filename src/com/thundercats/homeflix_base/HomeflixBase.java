/*Homeflix-Base: HomeflixBase
 * 
 * Homeflix project for WKU CS496
 * Richie Davidson, Parker Kemp, Colin Page
 * Spring Semester 2014
 * 
 * This is the main class of Homeflix Base code.
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
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	public static Path myDir;//Directory of the video library, NOT the working dir
	
	public static void main(String[] args){
		Logger.setLogFile("log.txt");
		
		//String[] files = {"/Users/iamparker/Desktop/Movies/manfromearth.mp4"};
		
		
		JFrame frame = new JFrame();
		textArea = new JTextArea();
		sysTraySet(HFicon, "Homeflix Base");
		
		//try {
		//	Process process = Runtime.getRuntime().exec("cd external-jars/MacOS;./vlc --ttl 12 -vvv --color -I telnet --telnet-port 2465 --telnet-password videolan --rtsp-port 2464");
		//} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		
		VLCStream.loadNative();

		//System.out.println(new MediaInfo("/Users/iamparker/Desktop/Homeflix-vids/django.avi").getLength());

		//VLCStream.startTelnetServer();
		//new Thread(new VLCServer("/Users/iamparker/Desktop/Homeflix-vids/django.avi")).start();
		//new VLCServer().startVLCInstance("/Users/iamparker/Desktop/Homeflix-vids/django.avi");
		//new Thread(new VLCStream("Test")).start();
		//VLCStream.launchStream("Test");//"/Users/iamparker/Desktop/Movies/django.avi");
		
		new Thread(new CheckOwnIP()).start();
		
		//Directory setup
		//Check if user has already established settings, if not, create them.
		directoryConnect();
		//chooseDirectory();//must choose directory BEFORE Llamabrarian is initialized
		
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
		
		echo("Home directory chosen: " + myDir + "\n");
		
		echo("Homeflix server started.\n");
		HomeflixBase.echo("Make sure your mobile device is on the same Wifi network as your home computer, then connect to Base.\n");
        HomeflixBase.showAddresses();
        
		showInstructions();
		
		new Thread(null, new ServerThread(port), "Server-Thread").start();
	}
	
	public static void showAddresses(){
		ArrayList<InetAddress> addresses = getLocalAddresses();
		if(addresses.size() == 0)
			echo("No IPv4 addresses found!");
		else
			echo("Type one of the following into the top field on Homeflix Mobile, then press Send: ");
		for(int i = 0; i < addresses.size(); i++)
			echo(addresses.get(i).getHostAddress());
		echo("");
	}
	
	public static void showInstructions(){
		//echo("To play a video, first connect with Homeflix Mobile. In the message textbox on the app, send \"play <filename>\", where <filename> is the name of a file in the same directory as Homeflix-Base.jar.\n");
		echo("Once connected, Homeflix Mobile will display a list of the playable files in your chosen folder.");
		echo("To change your folder, right click the HF system tray icon and select 'Change Video Folder'.");
		echo("");
		echo("Play a video from your home library by tapping its name on Homeflix Mobile's screen.");
		echo("You can scroll down the list by dragging it with your finger if there are more files than will fit on your screen.");
		echo("You can update your list of files at any time by pressing 'Refresh File List'");
		echo("");
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
			    if (!current.isUp() || current.isLoopback()){// || current.isVirtual()){
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
			        	//echo("Found a valid address at " + current_addr.getHostAddress());
			        	inet4Addresses.add(current_addr);
			        }
			        	//return current_addr;
			    }
			}
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//echo("");
		return inet4Addresses;
	}
	
	public static void chooseDirectory(){
		//echo("choose dir");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setDialogTitle("Directory Chooser");
		fc.setAcceptAllFileFilterUsed(false);
		//returnVal = fc.showDialog(textArea, "Select your video library directory");
		
		if (fc.showDialog(textArea, "Select") == JFileChooser.APPROVE_OPTION) { 
			System.out.println("getCurrentDirectory(): " + fc.getCurrentDirectory());
			System.out.println("getSelectedFile() : " + fc.getSelectedFile());
			myDir = fc.getSelectedFile().toPath();
			
			//write prefs file
			try {
				PrintWriter writer = new PrintWriter(System.getProperty("user.dir") + File.separator + "prefs.txt", "UTF-8");
				writer.println(myDir.toString());
				//Any other persistent data should be written here
				writer.close();
			}
			catch (IOException ex) {
				//Error handling schmerror schmandling
			}
		}
		else {
			System.out.println("No Selection. Exiting.");
			System.exit(0);
		}
	}
	
	public static void sysTraySet(Image icon, String altText){
		if (SystemTray.isSupported()) {
		    //delete any old trayIcons before altering
		    tray.remove(trayIcon);
		    // create a popup menu
		    PopupMenu popup = new PopupMenu();
		    
		    trayIcon = new TrayIcon(icon, altText, popup);
		    
		    MenuItem changeDir = new MenuItem("Change Video Folder");
		    popup.add(changeDir);
		    
		    try {
		        tray.add(trayIcon);
		    } catch (AWTException e) {
		        System.err.println(e);
		    }
		    
		    changeDir.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	chooseDirectory();
	            	System.out.println("User click Change directory.");
	            }
	        });
		}
	}
	
	//This was redundant code for testing and is to-be-deleted once sysTraySet is finished absorbing their uses
	/*
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
		} 
		else {
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
		} 
		else {
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
		} 
		else {
		}
	}
	*/
	
	public static void directoryConnect(){
		//Check if preference file exists, use it/create it
		File prefFile = new File(System.getProperty("user.dir") + File.separator + "prefs.txt");
		if (prefFile.isFile() && prefFile.canRead()){//if the file exists
			//Read prefs and use them
			BufferedReader br = null;
			try {
				//Read file
				br = new BufferedReader(new FileReader(prefFile));
				myDir = Paths.get(br.readLine());//first line in file will be the user's chosen library path
				//Any other persistent preferences should be retrieved here, collect them at this point
			}
			catch (IOException ex) {
			    //Error handling?
			} finally {
				try {
					br.close();//close the file
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		else
		{
			//Set up file with new preferences
			//Have user choose directory
			chooseDirectory();
		}
	}
}
