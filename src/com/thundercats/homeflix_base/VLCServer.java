package com.thundercats.homeflix_base;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.internal.libvlc_instance_t;

public class VLCServer implements Runnable{

	public VLCServer(){
	}
	
	@Override
	public void run(){
		String[] libVlcArgs = {"-vvv", "--intf=telnet", "--telnet-port=2465", "--telnet-password=videolan", "--rtsp-port=2464", "--sout-avcodec-strict=-2", "--rtsp-timeout=0"};
        
        libvlc_instance_t instance = LibVlc.SYNC_INSTANCE.libvlc_new(7, libVlcArgs);
        LibVlc.SYNC_INSTANCE.libvlc_add_intf(instance, "telnet");
        
        try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
