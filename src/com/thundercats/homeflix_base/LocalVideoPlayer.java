package com.thundercats.homeflix_base;

import java.awt.image.BufferedImage;

import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;
import com.xuggle.xuggler.demos.VideoImage;

public class LocalVideoPlayer{
	
	private static VideoImage screen = null;
	
	public LocalVideoPlayer(String filename){

    IContainer container = IContainer.make();

    if (container.open(filename, IContainer.Type.READ, null) < 0)
    	throw new IllegalArgumentException("Unable to open file: " + filename);

    int numStreams = container.getNumStreams();

    int streamId = -1;
    IStreamCoder videoCoder = null;
    for(int i = 0; i < numStreams; i++){
    	
    	IStream stream = container.getStream(i);
    	IStreamCoder coder = stream.getStreamCoder();

    	if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO){
    		streamId = i;
    		videoCoder = coder;
    		break;
    	}
    }
    if(streamId == -1)
    	throw new RuntimeException("Could not find video stream in container: " + filename);

    if(videoCoder.open() < 0)
    	throw new RuntimeException("Could not open video decoder for container: " + filename);

    IVideoResampler resampler = null;
    if(videoCoder.getPixelType() != IPixelFormat.Type.BGR24){
    	resampler = IVideoResampler.make(videoCoder.getWidth(), videoCoder.getHeight(), IPixelFormat.Type.BGR24, videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
    	if(resampler == null)
    		throw new RuntimeException("could not create color space " + "resampler for: " + filename);
    }
    
    screen = new VideoImage();

    IPacket packet = IPacket.make();
    long firstTimeStamp = Global.NO_PTS;
    long startTime = 0;
    
    while(container.readNextPacket(packet) >= 0){
    	if (packet.getStreamIndex() == streamId){
    		IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(), videoCoder.getWidth(), videoCoder.getHeight());

    		int offset = 0;
    		
    		while(offset < packet.getSize()){
    			int decodedData = videoCoder.decodeVideo(picture, packet, offset);
    			if(decodedData < 0)
    				throw new RuntimeException("Error decoding video in: " + filename);
    			offset += decodedData;
    			if(picture.isComplete()){
    				IVideoPicture newPic = picture;
    				if(resampler != null){
    					newPic = IVideoPicture.make(resampler.getOutputPixelFormat(), picture.getWidth(), picture.getHeight());
    					if(resampler.resample(newPic, picture) < 0)
    						throw new RuntimeException("Unable to resample video from: " + filename);
    				}
    				if(newPic.getPixelType() != IPixelFormat.Type.BGR24)
    					throw new RuntimeException("Unable to decode video " + " as BGR 24 bit data in: " + filename);

    				if(firstTimeStamp == Global.NO_PTS){
    					firstTimeStamp = picture.getTimeStamp();
    					startTime = System.currentTimeMillis();
    				}else{
    					long currentTime = System.currentTimeMillis();
    					long timeSinceStartofVideo = currentTime - startTime;
    					long streamTimeSinceStartOfVideo = (picture.getTimeStamp() - firstTimeStamp)/1000;
    					final long tolerance = 50;
    					final long millisecondsToSleep = (streamTimeSinceStartOfVideo - (timeSinceStartofVideo + tolerance));
    					if(millisecondsToSleep > 0){
    						try{
    							Thread.sleep(millisecondsToSleep);
    						}
    						catch (InterruptedException e){
    							return;
    						}
    					}
    				}

    				BufferedImage javaImage = Utils.videoPictureToImage(newPic);

    				screen.setImage(javaImage);
    			}
    		}
    	}
    	else{
    		do {} while(false);
    	}	

    	}
    	if (videoCoder != null){
    		videoCoder.close();
    		videoCoder = null;
    	}
    	if (container !=null){
    		container.close();
    		container = null;
    	}
    	System.exit(0);

	}

	
}