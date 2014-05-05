/*Homeflix-Base: VLCServer
 * 
 * Homeflix project for WKU CS496
 * Richie Davidson, Parker Kemp, Colin Page
 * Spring Semester 2014
 * 
 * Create a new VLC instance to stream a video.
 * Only one at a time per client (see
 * ClientThread implementation).
 * 
 */

package com.thundercats.homeflix_base;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.internal.libvlc_instance_t;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class VLCServer {

	private libvlc_instance_t instance = null;

	public VLCServer() {
	}

	public void startVLCInstance(String filename) {
		//Destroy the previous VLC instance if necessary, and
		//start a new one that streams the given file
		
		//Stop the previous instance (if it exists)
		stopVLC();

		//Generate a config file specifying how the video should be streamed
		generateConfigFile(filename);

		//Arguments to pass to VLC
		String[] libVlcArgs = { "-vvv", "--vlm-conf=vod.conf",
				"--rtsp-port=2464", "--sout-avcodec-strict=-2",
				"--rtsp-timeout=0" };

		//Start VLC
		instance = LibVlc.SYNC_INSTANCE.libvlc_new(5, libVlcArgs);

	}

	public void stopVLC() {
		//If instance is non-null, then destroy it
		
		if (instance != null) {
			LibVlc.SYNC_INSTANCE.libvlc_release(instance);
			instance = null;
		}
	}

	private void generateConfigFile(String filename) {
		//Create a config file called vod.conf. This file conforms 
		//to a format specified by VLC, and defines parameters for
		//the outgoing stream
		
		PrintWriter writer;
		try {
			//Create/overwrite vod.conf
			writer = new PrintWriter("vod.conf", "UTF-8");

			//Replace spaces in filename with underscores (spaces are problematic)
			String compliantName = filename.replace(' ', '_');

			//Write the configurations
			writer.println("new " + compliantName + " vod disabled");
			writer.println("setup " + compliantName
					+ " output #transcode{vcodec=h264,acodec=mp4a}:gather");
			writer.println("setup " + compliantName + " input \""
					+ Llamabrarian.dir.toString() + "/" + filename + "\"");
			writer.println("setup " + compliantName + " option sout-keep");
			writer.println("setup " + compliantName + " option no-sout-rtp-sap");
			writer.println("setup " + compliantName
					+ " option no-sout-standard-sap");
			writer.println("setup " + compliantName + " enabled");

			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void loadNative() {
		//Load the VLC native library
		if(isWindows32()){
			NativeLibrary.addSearchPath(
					RuntimeUtil.getLibVlcLibraryName(), System.getProperty("user.dir") + "\\VLC-WINDOWS-32\\VLC"
					);
		}
		else if(isWindows64()){
			NativeLibrary.addSearchPath(
					RuntimeUtil.getLibVlcLibraryName(), System.getProperty("user.dir") + "\\VLC-WINDOWS-64\\VLC"
					);
		}
		else if(isMac()){
			NativeLibrary.addSearchPath(
					RuntimeUtil.getLibVlcLibraryName(), System.getProperty("user.dir") + "/VLC-OSX/lib"
					);
		}
		else
			System.out.println("Unable to identify platform!!!");
	}
	
	private static boolean isWindows32(){
		return System.getProperty("os.name").toLowerCase().startsWith("win") && !is64Bit();
	}
	
	private static boolean isWindows64(){
		return System.getProperty("os.name").toLowerCase().startsWith("win") && is64Bit();
	}
	
	private static boolean isMac(){
		return System.getProperty("os.name").toLowerCase().startsWith("mac");
	}

	private static boolean is64Bit(){
		return System.getProperty("os.arch").toLowerCase().contains("64");
	}

}
