package com.thundercats.homeflix_base;

import java.util.List;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.TrackInfo;
import uk.co.caprica.vlcj.player.TrackType;

public class VideoPackage {
	private String path, filename, videoCodec, audioCodec;
	
	public VideoPackage(String path, String filename){
		parseFile(path, filename);
	}
	
	public VideoPackage(){
	}
	
	public void parseFile(String path, String filename){
		path += "/";
		this.path = path;
		this.filename = filename;
		
		MediaPlayerFactory factory = new MediaPlayerFactory();
        MediaPlayer mediaPlayer = factory.newHeadlessMediaPlayer();

        mediaPlayer.prepareMedia(path + filename);

        mediaPlayer.parseMedia();
        List<TrackInfo> info;
        
        info = mediaPlayer.getTrackInfo(TrackType.VIDEO);
        if(!info.isEmpty())
        	videoCodec = info.get(0).codecName();
        
        info = mediaPlayer.getTrackInfo(TrackType.AUDIO);
        if(!info.isEmpty())
        	audioCodec = info.get(0).codecName();
        //System.out.println(path + filename);
        System.out.println(filename + ": (" + videoCodec + "/" + audioCodec + ").");
        
        mediaPlayer.release();
        factory.release();
	}
	
	public String getFilename(){
		return filename;
	}
	
	public String getVideoCodec(){
		return videoCodec;
	}
	
	public String getAudioCodec(){
		return audioCodec;
	}
}
