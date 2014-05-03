/*Homeflix-Base: MediaInfo
 * 
 * Homeflix project for WKU CS496
 * Richie Davidson, Parker Kemp, Colin Page
 * Spring Semester 2014
 * 
 * Opens and analyzes a video file, retrieving
 * and storing useful information about it
 */

package com.thundercats.homeflix_base;

import java.io.File;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;

public class MediaInfo{
	
	//Volatile to allow busy waiting code without being optimized out 
	private volatile long playbackLength = -2;
	private String path, filename;
	
	public MediaInfo(String path, String filename){
		this.path = path;
		this.filename = filename;
		getLength();
	}
	
	public String getFilename(){
		return filename;
	}
	
    public long getLength() {
    	//If playbackLength has already been determined, then return it.
    	//Otherwise, extract it from the video file
    	
    	if(playbackLength > 0)
    		return playbackLength;
    	
    	//Start a temporary VLC instance (different technique than was used in VLCServer.java)
    	MediaPlayerFactory factory = new MediaPlayerFactory("--vout", "dummy");
    	
    	//Get a media player from VLC
        MediaPlayer mediaPlayer = factory.newHeadlessMediaPlayer();
        
        //Add an event listener to extract the playback time once the video file is ready
        mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void mediaStateChanged(MediaPlayer mediaPlayer, int newState){
            	//Set the volatile variable with length of video (zero for non-video files)
            	setLength(mediaPlayer.getLength());
            }
        });
        
        //Try to load the file
        mediaPlayer.prepareMedia(path + File.separator + filename);
        
        //Parse the file
        mediaPlayer.parseMedia();
        
        //Try to "play" the video in the background (this is necessary to get certain data)
        mediaPlayer.start();
        
        //Busy-wait until mediaPlayer updates playbackLength
        while(playbackLength == -2)
        	;
        
        mediaPlayer.release();
        factory.release();
        
        return playbackLength;
    }
    
    public boolean isValid(){
    	//Return true if the file is actually a video file, false otherwise
    	
    	//Non-video files will have playbackLength <= 0
    	return playbackLength > 1000;
    }

	public void setLength(long length){
		playbackLength = length;
	}
}
