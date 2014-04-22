/*Homeflix-Base: Llamabrarian
 * 
 * Homeflix project for WKU CS496
 * Richie Davidson, Parker Kemp, Colin Page
 * Spring Semester 2014
 * 
 * Monitor a user-specified directory
 * Curates database to reflect this
 * Passes relevant info to ClientThread to update Mobile
 */

package com.thundercats.homeflix_base;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.*;
import java.util.List;


public class Llamabrarian implements Runnable{
	
	public Llamabrarian(){
		
	}

	@Override
	public void run() 
	{
		Path myDir = Paths.get(System.getProperty("user.dir"));
		String myDirS = ".";
		//define a folder root
		testForDirectoryChange(myDir, myDirS);
		//System.out.println(videoList(myDirS));
	}
	
	public static void testForDirectoryChange(Path myDir, String myDirS){
		String[] videoNames;

        try {
           WatchService watcher = myDir.getFileSystem().newWatchService();
           myDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
           StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

           WatchKey watckKey = watcher.take();

           while(true){ //watch for events and print out the kind of event
           List<WatchEvent<?>> events = watckKey.pollEvents();
           for (WatchEvent event : events) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    System.out.println("Created: " + event.context().toString());
                }
                if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                    System.out.println("Delete: " + event.context().toString());
                    
                    //test code
                    videoNames = videoList(myDirS);
                    for (int i = 0; i < videoNames.length; i++){
                    	System.out.println(videoNames[i]);
                    }
                    
                    //ClientThread.updateMobile(videoNames);
                    ///test code
                    
                }
                if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                    System.out.println("Modify: " + event.context().toString());
                }
            }
           }

        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
    }


	//Get list of video files in directory and pass as String[]
	public static String[] videoList(String myDirS) 
	{
		myDirS = ".";//debug
		String files;
		int j = 0;
	    File folder = new File(myDirS);
	    File[] listOfFiles = folder.listFiles();
	    
	    //System.out.println(listOfFiles.length);
	    
	    String[] vidFiles = new String[listOfFiles.length];
	    String[] vidFiles2;
	    
	    for (int i = 0; i < listOfFiles.length; i++){
	    	if (listOfFiles[i].isFile()) {
	    		files = listOfFiles[i].getName();
	    		if (files.endsWith(".mov") || files.endsWith(".MOV") || files.endsWith(".avi")){
	    			//System.out.println(files);//debug
	    			vidFiles[j] = files;
	    			j++;
	    		}
	        }
	    }
	    
	    vidFiles2 = new String[j];
	    for (int i = 0; i < j; i++){
	    	vidFiles2[i] = vidFiles[i];
	    }
	    
	    return vidFiles2;
	}

}