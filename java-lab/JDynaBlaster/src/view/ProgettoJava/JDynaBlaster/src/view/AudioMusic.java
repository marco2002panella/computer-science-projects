package view;

import java.io.File;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class AudioMusic implements AudioPlayer{
	private MediaPlayer music;
	private static AudioMusic single;
	private boolean playing;
	private AudioMusic() {
		playing=false;
	}
	
	public static AudioMusic getInstance() {
		if(single==null)single=new AudioMusic();
		return single;
	}
	
	@Override
	public void play() {
		Media media=new Media(getClass().getResource("/resources/Audios/691609__benderhover__retro-style-game-beat.mp3").toExternalForm());
		music=new MediaPlayer(media);
		if(music!=null) {
			music.play();
			playing=true;
			music.setCycleCount(MediaPlayer.INDEFINITE);
		}
		
	}
	@Override
	public void stop() {
		if(playing) {
			music.stop();
			music.dispose();
			playing=false;
		}
	}
}
