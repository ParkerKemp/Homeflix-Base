package com.thundercats.homeflix_base;

enum RTSPType{OPTIONS, DESCRIBE, SETUP, PLAY, PAUSE};

public class RTSPRequest {
	int cseq;
	RTSPType type;
	
	public RTSPRequest(String line){
		//if(firstWord(line).equals("")
		if(firstWord(line).equals("PLAY"))
			type = RTSPType.PLAY;
	}
	
	public String firstWord(String line){
    	return line.split(" ")[0];
    }
}
