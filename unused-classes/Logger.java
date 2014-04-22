package com.thundercats.homeflix_base;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {
	private static String logFile;
	
	public static <T> void log(T message){
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(logFile, true));
			writer.println(message);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void log(long num){
		log(new Long(num));
	}
	
	public static void setLogFile(String filename){
		logFile = filename;
		PrintWriter writer;
		try {
			writer = new PrintWriter(filename);
			writer.print("");
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String dateAndTime(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
	}
}
