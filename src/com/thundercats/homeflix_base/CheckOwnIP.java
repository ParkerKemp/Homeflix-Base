package com.thundercats.homeflix_base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

public class CheckOwnIP implements Runnable{
	private String ip = "";
	public CheckOwnIP(){
		
	}
	
	@Override
	public void run(){
		String newIP;
		while(true){
			Calendar calendar = Calendar.getInstance();
			if(Calendar.getInstance().after(calendar)){
				calendar.add(Calendar.SECOND, 5);
				newIP = currentIP();
				if(!newIP.equals(ip)){
					ip = newIP;
					//new Thread(new StaticConnect("mc.spinalcraft.com", 5000, ip)).start();
				}
			}
		}
	}
	
	private String currentIP(){
		String newIP = "";
		
		URL whatismyip;
		try {
			whatismyip = new URL("http://checkip.amazonaws.com/");
			BufferedReader in;
			in = new BufferedReader(new InputStreamReader(
	                whatismyip.openStream()));
			newIP = in.readLine();
			//System.out.println(newIP);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newIP;
	}
}