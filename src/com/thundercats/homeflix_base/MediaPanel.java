package com.thundercats.homeflix_base;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.IOException;
import java.net.URL;

import javax.media.CannotRealizeException;
import javax.media.Manager;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.swing.JPanel;

public class MediaPanel extends JPanel{

	private static final long serialVersionUID = -3599002959660472362L;

	public MediaPanel(URL mediaURL){
		setLayout(new BorderLayout());
	
	
		try {
			Player mediaPlayer = Manager.createRealizedPlayer(mediaURL);
			
			Component video = mediaPlayer.getVisualComponent();
			Component controls = mediaPlayer.getControlPanelComponent();
		
			if(video != null)
				add(video, BorderLayout.CENTER);
			if(controls != null)
				add(controls, BorderLayout.SOUTH);
		
			mediaPlayer.start();
		} catch (NoPlayerException e) {
			System.err.println("No media player found");
			e.printStackTrace();
		} catch (CannotRealizeException e) {
			System.err.println("Couldn't realize the media player");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error reading from the source");
			e.printStackTrace();
		}
	}
}
