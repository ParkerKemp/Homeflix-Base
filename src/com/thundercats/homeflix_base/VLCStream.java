package com.thundercats.homeflix_base;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;
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
		
		//String media = "/Users/iamparker/Desktop/Movies/manfromearth.mp4";
		String options = formatRtspStream(
				//"127.0.0.1",
				hostName,
				port,
				fileName);

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
		NativeLibrary.addSearchPath(
				RuntimeUtil.getLibVlcLibraryName(), "/Applications/VLC.app/Contents/MacOS/lib"
				);
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
	}
}
