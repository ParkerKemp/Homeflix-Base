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
import java.util.List;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.TrackInfo;
import uk.co.caprica.vlcj.player.TrackType;

public class MediaInfo{
	
	//Volatile to allow busy waiting code without being optimized out 
	private volatile long playbackLength = -2;
	private String path, filename, videoCodec, audioCodec;
	private boolean isValid = true;
	
	public MediaInfo(String path, String filename){
		this.path = path;
		this.filename = filename;
		parseFile();
	}
	
    public void parseFile() {
    	//Parse the file for metadata
    	
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
        
        List<TrackInfo> vidTracks = mediaPlayer.getTrackInfo(TrackType.VIDEO);
        List<TrackInfo> audTracks = mediaPlayer.getTrackInfo(TrackType.AUDIO);
        
        //Extract the video/audio codecs
        if(!vidTracks.isEmpty())
        	videoCodec = vidTracks.get(0).codecName();
        else
        	isValid = false;
        if(!audTracks.isEmpty())
        	audioCodec = audTracks.get(0).codecName();
        
        //Try to "play" the video in the background (this is necessary to get certain data)
        mediaPlayer.start();
        
        //Busy-wait until mediaPlayer updates playbackLength
        while(playbackLength == -2)
        	;
        
        mediaPlayer.release();
        factory.release();
    }
    
    public boolean isValid(){
    	//Return true if the file is actually a video file, false otherwise
    	
    	//Non-video files will have playbackLength <= 0
    	return isValid;//playbackLength > 1000;
    }

	public void setLength(long length){
		playbackLength = length;
	}

	public String getFilename(){
		return filename;
	}
	
	public long getPlaybackLength(){
		return playbackLength;
	}
	
	public String getVideoCodec(){
		return videoCodec;
	}
	
	public String getAudioCodec(){
		return audioCodec;
	}
}
