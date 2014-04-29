package com.thundercats.homeflix_base;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;

public class MediaInfo{
	
	private volatile long playbackLength = -2;
	private String filename;
	
	public MediaInfo(String filename){
		this.filename = filename;
	}
	
    public long getLength() {
    	if(playbackLength > 0)
    		return playbackLength;
    	
    	MediaPlayerFactory factory = new MediaPlayerFactory();//"--intf", "macosx");
        MediaPlayer mediaPlayer = factory.newHeadlessMediaPlayer();
        mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void videoOutput(MediaPlayer mediaPlayer, int newCount) {
                setLength(mediaPlayer.getLength());
            }
        });
        mediaPlayer.prepareMedia(filename);
 
        mediaPlayer.parseMedia();
        mediaPlayer.start();
        
        while(playbackLength == -2)
        	;

        mediaPlayer.release();
        factory.release();
        
        return playbackLength;
    }

	public void setLength(long length){
		playbackLength = length;
	}
}
