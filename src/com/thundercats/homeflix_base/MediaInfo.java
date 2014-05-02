package com.thundercats.homeflix_base;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;

public class MediaInfo{
	
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
    	if(playbackLength > 0)
    		return playbackLength;
    	
    	MediaPlayerFactory factory = new MediaPlayerFactory("--vout", "dummy");
    	
        MediaPlayer mediaPlayer = factory.newHeadlessMediaPlayer();
        mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void mediaStateChanged(MediaPlayer mediaPlayer, int newState){
            	setLength(mediaPlayer.getLength());
            }
        });
        mediaPlayer.prepareMedia(path + File.separator + filename);
        mediaPlayer.parseMedia();
        
        mediaPlayer.start();
        
        while(playbackLength == -2)
        	;
        
        mediaPlayer.release();
        factory.release();
        
        return playbackLength;
    }
    
    public boolean isValid(){
    	return playbackLength > 0;
    }

	public void setLength(long length){
		playbackLength = length;
	}
}
