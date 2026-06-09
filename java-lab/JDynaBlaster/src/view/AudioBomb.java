package view;

import java.io.File;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class AudioBomb implements AudioPlayer{
	private MediaPlayer bomb;
	private static AudioBomb single;
	private boolean playing;
	private AudioBomb() {
		playing=false;
	}
	public static AudioBomb getInstance() {
		if(single==null)single=new AudioBomb();
		return single;
	}
	
	@Override
	public void play() {
		Media media=new Media(getClass().getResource("/resources/Audios/500673__simoneyoh3998__moderate-bomb-explosion.mp3").toExternalForm());
		bomb=new MediaPlayer(media);
		if(bomb!=null) {
			bomb.play();
			playing=true;
			Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {
                stop();
            }));
            timeline.setCycleCount(1);
            timeline.play();
		}
		
	}
	@Override
	public void stop() {
		if(playing) {
			bomb.stop();
			bomb.dispose();
			playing=false;
		}
	}
}
