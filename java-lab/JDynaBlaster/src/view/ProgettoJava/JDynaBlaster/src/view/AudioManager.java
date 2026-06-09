package view;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class AudioManager {
	//singleton pattern
	private static AudioManager instance;
	//strategy pattern
	private ArrayList<AudioPlayer> player;
	
	public static AudioManager getInstance() {
		if (instance == null)
		instance = new AudioManager();
		return instance;
	}
	
	private AudioManager() {
		player=new ArrayList<AudioPlayer>();
		addAudio(AudioBomb.getInstance());
		addAudio(AudioMusic.getInstance());
		addAudio(AudioMusicMenu.getInstance());
		addAudio(AudioLoose.getInstance());
		addAudio(AudioMusicWin.getInstance());
	}
	
	public void addAudio(){
		
	}
	
	/**metodo che riproduce gli audio con javafx, javafx supporta la riproduzione di file audio mp3, avremmo potuto
	 * utilizzare gli stream e usare la Java Sound Api*/
	
	private void addAudio(AudioPlayer audio){
		player.add(audio);
	}
	
	/**metodo che riproduce un audio*/
	public void Play(AudioPlayer audio){
		if(player.contains(audio))audio.play();
	}
	
	/**metodo che stoppa un audio*/
	public void stop(AudioPlayer audio) {
		if(player.contains(audio))audio.stop();
	}
	
	public void stopAll() {
		player.forEach(x->x.stop());
	}
	
	
}
	

