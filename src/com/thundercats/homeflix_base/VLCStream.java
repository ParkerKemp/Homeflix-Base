package com.thundercats.homeflix_base;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;
import uk.co.caprica.vlcj.player.manager.MediaManager;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class VLCStream implements Runnable{

	private String fileName;
	private String hostName;
	private int port;
	
	public VLCStream(String fileName, String hostName, int port){
		this.fileName = fileName;
		this.hostName = hostName;
		this.port = port;
	}
	
	@Override
	public void run(){
		loadNative();
		
		 String options = formatRtspStream(hostName, port, fileName);

	        System.out.println("Streaming '" + fileName + "' to '" + options + "'");

	        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
	        HeadlessMediaPlayer mediaPlayer = mediaPlayerFactory.newHeadlessMediaPlayer();
	        mediaPlayer.playMedia(fileName,
	            options,
	            ":no-sout-rtp-sap",
	            ":no-sout-standard-sap",
	            ":sout-all",
	            ":sout-keep"
	        );
	        while(true)
	        	;
	        // Don't exit
	        //Thread.currentThread().join();
		
		/*
		 // The host and port options are REQUIRED for video-on-demand
        MediaPlayerFactory factory = new MediaPlayerFactory("--rtsp-host=" + hostName, "--rtsp-port=" + port);

        MediaManager manager = factory.newMediaManager();

//        String vodMux = "mp2t";
        String mux = "ts";

        for(int i = 0; i < fileNames.length; i++){
        	manager.addVideoOnDemand("Movie" + i, fileNames[i], true, null);
        	//System.out.println(manager.getLength(fileNamespi[], arg1))
        	System.out.println("Movie" + i + ": " + manager.show(fileNames[i]));
            
        }
        while(true)
        	;*/
        // Client MRL: rtsp://@127.0.0.1:5004/Movie1
        //manager.addVideoOnDemand("Movie1", "/Users/iamparker/Desktop/Movies/manfromearth.mp4", true, vodMux);

        // Client MRL: rtsp://@127.0.0.1:5004/Movie2
       // manager.addVideoOnDemand("Movie2", "/Users/iamparker/Desktop/Movies/test.MOV", true, vodMux);

//        System.out.println("Movie1: " + manager.show("Movie1"));
        //System.out.println("Movie2: " + manager.show("Movie2"));
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

	public static void loadNative(){
		new NativeDiscovery().discover();
		//NativeLibrary.addSearchPath(
		//		RuntimeUtil.getLibVlcLibraryName(), System.getProperty("user.dir") + "/LibVLC/lib"
		//		);
		//Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
	}
}
