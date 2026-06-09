package view;

import java.io.File;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class AudioMusicMenu implements AudioPlayer{
	private MediaPlayer music;
	private static AudioMusicMenu single;
	private boolean playing;
	private AudioMusicMenu() {
		playing=false;
	}
	
	public static AudioMusicMenu getInstance() {
		if(single==null)single=new AudioMusicMenu();
		return single;
	}
	
	@Override
	public void play() {
		
		Media media=new Media(getClass().getResource("/resources/Audios/661248__magmadiverrr__video-game-menu-music.mp3").toExternalForm());
		music=new MediaPlayer(media);
		music.play();
		playing=true;
		music.setCycleCount(MediaPlayer.INDEFINITE);
		music.setVolume(0.1);
		
	}
	@Override
	public void stop() {
		if(playing) {
			playing=false;
			music.stop();
			music.dispose();
		}
	}
}
