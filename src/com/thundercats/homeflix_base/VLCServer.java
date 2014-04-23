package com.thundercats.homeflix_base;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.internal.libvlc_instance_t;

public class VLCServer{

	//String filename;
	private libvlc_instance_t instance;
	
	public VLCServer(){
	}
	
	public void startVLCInstance(String filename){
		
		if(instance != null){
			LibVlc.SYNC_INSTANCE.libvlc_release(instance);
			instance = null;
		}
		
		generateConfigFile(filename);
		
		//String[] libVlcArgs = {"-vvv", "--intf=rc", "--telnet-port=2465", "--telnet-password=videolan", "--rtsp-port=2464", "--sout-avcodec-strict=-2", "--rtsp-timeout=0"};
		String[] libVlcArgs = {"-vvv", "--vlm-conf=vod.conf", "--rtsp-port=2464", "--sout-avcodec-strict=-2", "--rtsp-timeout=0"};//, "--packetizer=packetizer_mpeg4video"};
	    
		//LibVlc.SYNC_INSTANCE.libvlc_release(instance);
        instance = LibVlc.SYNC_INSTANCE.libvlc_new(5, libVlcArgs);
        //LibVlc.SYNC_INSTANCE.libvlc_add_intf(instance, "rc");
        
        //String[] vodArgs = {":sout=#transcode{vcodec=mp4v,acodec=mp4a}:gather", "--sout-keep", "--no-sout-rtp-sap", "--no-sout-standard-sap"};
        //LibVlc.SYNC_INSTANCE.libvlc_vlm_add_vod(instance, "Test", "/Users/iamparker/Desktop/Homeflix-vids/django.avi", 4, vodArgs, 1, "mp2t");
        //LibVlc.SYNC_INSTANCE.libvlc_release(instance);
        //if(instance == null)
        //	HomeflixBase.echo("Instance is null");
        //else
        //	HomeflixBase.echo("Instance is non-null");
       /* try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	}
	
	public void generateConfigFile(String filename){
		PrintWriter writer;
		try {
			writer = new PrintWriter("vod.conf", "UTF-8");
			
			String compliantName = filename.replace(' ','_');
			
			writer.println("new " + compliantName + " vod disabled");
			writer.println("setup " + compliantName + " output #transcode{vcodec=h264,acodec=mp4a}:gather");
			writer.println("setup " + compliantName + " input \"" + Llamabrarian.dir.toString() + "/" + filename + "\"");
			writer.println("setup " + compliantName + " option sout-keep");
			writer.println("setup " + compliantName + " option no-sout-rtp-sap");
			writer.println("setup " + compliantName + " option no-sout-standard-sap");
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
}