/*Homeflix-Base: VLCStream
 * 
 * Homeflix project for WKU CS496
 * Richie Davidson, Parker Kemp, Colin Page
 * Spring Semester 2014
 * 
 * 
 */

package com.thundercats.homeflix_base;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.internal.libvlc_instance_t;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;
import uk.co.caprica.vlcj.player.manager.MediaManager;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class VLCStream implements Runnable{

	private String filename;
	
	public VLCStream(String fileName){
		this.filename = fileName;
	}
	
	@Override
	public void run(){
		String recv;
		loadNative();
		
		TelnetRequest telnet = new TelnetRequest();
		
		telnet.connect(); //Hangs until a connection is made to telnet
		telnet.send("videolan");
		while(!(recv = telnet.receive()).equals("Welcome, Master"))
			HomeflixBase.echo(telnet.receive());
		telnet.send("new " + filename + " vod disabled");
		HomeflixBase.echo(telnet.receive());
		telnet.send("setup " + filename + " output #transcode{vcodec=mp4v,acodec=mp4a}:gather");
		HomeflixBase.echo(telnet.receive());
		telnet.send("setup " + filename + " input " + filename);//"/Users/iamparker/Desktop/Movies/django.avi");
		HomeflixBase.echo(telnet.receive());
		telnet.send("setup " + filename + " option sout-keep");
		HomeflixBase.echo(telnet.receive());
		telnet.send("setup " + filename + " option no-sout-rtp-sap");
		HomeflixBase.echo(telnet.receive());
		telnet.send("setup " + filename + " option no-sout-standard-sap");
		HomeflixBase.echo(telnet.receive());
		telnet.send("setup " + filename + " enabled");
		HomeflixBase.echo(telnet.receive());
		
		HomeflixBase.echo("Ready to go!");
	}

	private String formatRtspStream(String serverAddress, int serverPort, String id) {
		StringBuilder sb = new StringBuilder(60);
		sb.append(":sout=#rtp{sdp=rtsp://@");
		sb.append(serverAddress);
		sb.append(':');
		sb.append(serverPort);
		sb.append('/');
		sb.append(id);
		sb.append("}");
		return sb.toString();
	}
	
	public static void launchStream(String filename){
		TelnetRequest telnet = new TelnetRequest();
		
		telnet.connect();
		telnet.send("videolan");
		telnet.send("new " + filename + " vod disabled");
		telnet.send("setup " + filename + " output #transcode{vcodec=mp4v,acodec=mp4a}:gather");
		telnet.send("setup " + filename + " input /Users/iamparker/Desktop/Movies/django.avi");
		telnet.send("setup " + filename + " option sout-keep");
		telnet.send("setup " + filename + " option no-sout-rtp-sap");
		telnet.send("setup " + filename + " option no-sout-standard-sap");
		
/*		new Test vod disabled
		setup Test output #transcode{vcodec=mp4v,acodec=mp4a}:gather
		setup Test input /Users/iamparker/Desktop/Movies/django.avi
		setup Test option sout-keep
		setup Test option no-sout-rtp-sap
		setup Test option no-sout-standard-sap*/
	}
	
	public static void startTelnetServer(){
		String[] libVlcArgs = {"-vvv", "--intf=telnet", "--telnet-port=2465", "--telnet-password=videolan", "--rtsp-port=2464", "--sout-avcodec-strict=-2", "--rtsp-timeout=0"};
        
        libvlc_instance_t instance = LibVlc.SYNC_INSTANCE.libvlc_new(7, libVlcArgs);
        LibVlc.SYNC_INSTANCE.libvlc_add_intf(instance, "telnet");
        /*try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        
	}

	public static void loadNative(){
		new NativeDiscovery().discover();
		//NativeLibrary.addSearchPath(
		//		RuntimeUtil.getLibVlcLibraryName(), System.getProperty("user.dir") + "/LibVLC/lib"
		//		);
		//Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
	}
}
