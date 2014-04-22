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
import java.nio.file.*;
import java.util.List;


public class Llamabrarian implements Runnable{
	
	public Llamabrarian(){
		
	}

	@Override
	public void run() 
	{
		Path myDir = Paths.get(System.getProperty("user.dir")); 
		//define a folder root
		testForDirectoryChange(myDir);
	}
	
	public static void testForDirectoryChange(Path myDir){

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

}