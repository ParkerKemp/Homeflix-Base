package com.thundercats.homeflix_base;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaViewer;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;

public class Transcoder{

    private static final String inputFilename = "/Users/iamparker/Desktop/Movies/test.MOV";
    private static final String outputFilename = "/Users/iamparker/Desktop/Movies/TheDeparted/departed.avi";

    public Transcoder() {

    	System.out.println("Initializing transcoder.");
    	

        System.out.println("Getting input file.");
        
        // create a media reader
        IMediaReader mediaReader = 
               ToolFactory.makeReader(inputFilename);
        

        System.out.println("Getting output file.");
        
        // create a media writer
        IMediaWriter mediaWriter = 
               ToolFactory.makeWriter(outputFilename, mediaReader);
        
        System.out.println("Adding media writer as listener.");

        // add a writer to the reader, to create the output file
        //mediaReader.addListener(mediaWriter);
        
        System.out.println("Creating a viewer.");
        
        // create a media viewer with stats enabled
        IMediaViewer mediaViewer = ToolFactory.makeViewer(true);
        
        System.out.println("Adding viewer as listener.");
        
        // add a viewer to the reader, to see the decoded media
        mediaReader.addListener(mediaViewer);
        
        System.out.println("Beginning loop.");

        // read and decode packets from the source file and
        // and dispatch decoded audio and video to the writer
        while (mediaReader.readPacket() == null) 
        	System.out.println("Reading packet...");

    }

}