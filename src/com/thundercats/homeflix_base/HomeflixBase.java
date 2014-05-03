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
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
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

public class HomeflixBase {
	
	private static final int port = 2463;
	private static JFrame frame;
	public static JTextArea textArea;
	
	//Tray Icon prep
	public static SystemTray tray = SystemTray.getSystemTray();
	public static TrayIcon trayIcon;
	public static Image HFicon = new ImageIcon("src/resources/HFiconLR.png").getImage();
	public static Image HFiconUD = new ImageIcon("src/resources/HFiconUpdateLR.png").getImage();
	public static Image HFiconPlay = new ImageIcon("src/resources/HFiconPlayingLR.png").getImage();
	
	//File Chooser prep
	public static int returnVal;
	public static final JFileChooser fc = new JFileChooser();
	//public static Path myDir;//Directory of the video library, NOT the working dir
	
	public static void main(String[] args){
		//Create system tray icon
		sysTraySet(HFicon, "Homeflix Base");
		
		//Load VLC native library
		VLCServer.loadNative();

		//Directory setup
		//Check if user has already established settings, if not, create them.
		directoryConnect();
		
		//Wake up Llamabrarian with a bucket of water, yell at him to get back to work
		new Thread(new Llamabrarian()).start();
		
		createServerWindow();
		
		greetUser();
		
		//Start the server thread
		new Thread(null, new ServerThread(port), "Server-Thread").start();
	}
	
	private static void createServerWindow(){

		frame = new JFrame();
		textArea = new JTextArea();
		
		//Create a scroll pane, allowing the user to scroll up and down
		//once the text fills up the window
		JScrollPane scrollPane = new JScrollPane(textArea);
		
		textArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		frame.add(scrollPane);
		
		frame.setSize(600,400);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private static void greetUser(){
		echo("Starting Homeflix Base.\n");
		
		echo("Home directory chosen: " + Llamabrarian.dir + "\n");
		
		echo("Homeflix server started.\n");
		HomeflixBase.echo("Make sure your mobile device is on the same Wifi network as your home computer, then connect to Base.\n");
        HomeflixBase.showAddresses();
        
		showInstructions();
	}
	
	public static void showAddresses(){
		//Retrieve a list of IPv4 addresses and display them in the server window
		
		ArrayList<InetAddress> addresses = getLocalAddresses();
		
		if(addresses.size() == 0)
			echo("No IPv4 addresses found!");
		else
			echo("Type one of the following into the top field on Homeflix Mobile, then press Send: ");
		
		for(int i = 0; i < addresses.size(); i++)
			echo(addresses.get(i).getHostAddress());
	}
	
	public static void showInstructions(){
		//Display some instructions on how to use Homeflix
		
		echo("Once connected, Homeflix Mobile will display a list of the playable files in your chosen folder.");
		echo("To change your folder, right click the HF system tray icon and select 'Change Video Folder'.");
		echo("");
		echo("Play a video from your home library by tapping its name on Homeflix Mobile's screen.");
		echo("You can scroll down the list by dragging it with your finger if there are more files than will fit on your screen.");
		echo("You can update your list of files at any time by pressing 'Refresh File List'");
		echo("");
	}
	
	public static void echo(String msg){
		//Print a string in System.out and the server window
		
		System.out.println(msg);
		textArea.append(msg + "\n");
	}
	
	public static ArrayList<InetAddress> getLocalAddresses(){
		//Iterate through all available network interfaces and try
		//to find valid outgoing IPv4 addresses
		
		ArrayList<InetAddress> inet4Addresses = new ArrayList<InetAddress>();
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()){
				NetworkInterface current = interfaces.nextElement();
			    if (!current.isUp() || current.isLoopback())
			    	continue;
			    Enumeration<InetAddress> addresses = current.getInetAddresses();
			    while (addresses.hasMoreElements()){
			    	InetAddress current_addr = addresses.nextElement();
			        if(current_addr.isLoopbackAddress())
			        	continue;
			        if(current_addr instanceof Inet4Address)
			        	inet4Addresses.add(current_addr);
			    }
			}
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		return inet4Addresses;
	}
	
	public static void chooseDirectory(){
		//Open a dialog window allowing the user to browse their computer
		//and pick a directory as their video library directory
		
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setDialogTitle("Directory Chooser");
		fc.setAcceptAllFileFilterUsed(false);
		//returnVal = fc.showDialog(textArea, "Select your video library directory");
		
		if (fc.showDialog(textArea, "Select") == JFileChooser.APPROVE_OPTION) { 
			System.out.println("getCurrentDirectory(): " + fc.getCurrentDirectory());
			System.out.println("getSelectedFile() : " + fc.getSelectedFile());
			//myDir = fc.getSelectedFile().toPath();
			Llamabrarian.setDirectory(fc.getSelectedFile().toPath());
			
			//write prefs file
			try {
				PrintWriter writer = new PrintWriter(System.getProperty("user.dir") + File.separator + "prefs.txt", "UTF-8");
				writer.println(Llamabrarian.dir.toString());
				//Any other persistent data should be written here
				writer.close();
			}
			catch (IOException ex) {
				//Error handling schmerror schmandling
			}
		}
		else {
			echo("No Selection. Defaulting to working directory.");
			//System.exit(0);//Originally exited here
			Llamabrarian.setDirectory(Paths.get(System.getProperty("user.dir")));
		}
	}
	
	public static void sysTraySet(Image icon, String altText){
		//Set the system tray icon
		
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
	            @Override
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
		//Check if the user had previously chosen a library directory;
		//if not, open a dialog window prompting them to choose one.
		
		//Check if preference file exists, use it/create it
		File prefFile = new File(System.getProperty("user.dir") + File.separator + "prefs.txt");
		if (prefFile.isFile() && prefFile.canRead()){//if the file exists
			//Read prefs and use them
			BufferedReader br = null;
			try {
				//Read file
				br = new BufferedReader(new FileReader(prefFile));
				Llamabrarian.setDirectory(Paths.get(br.readLine()));//first line in file will be the user's chosen library path
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
