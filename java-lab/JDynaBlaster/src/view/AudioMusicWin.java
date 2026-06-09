package view;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AudioMusicWin implements AudioPlayer {
    private MediaPlayer music;
    private static AudioMusicWin single;
    private boolean playing;

    private AudioMusicWin() {
        playing = false;
    }

    public static AudioMusicWin getInstance() {
        if (single == null)
            single = new AudioMusicWin();
        return single;
    }

    @Override
    public void play() {
        Media media = new Media(getClass().getResource("/resources/Audios/113989__kastenfrosch__gewonnen.mp3").toExternalForm());
        music = new MediaPlayer(media);
        if (music != null) {
            music.play();
            playing = true;
        }
    }

    @Override
    public void stop() {
        if (playing) {
            music.stop();
            music.dispose();
            playing = false;
        }
    }
}
