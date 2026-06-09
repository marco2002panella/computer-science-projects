package view;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AudioLoose implements AudioPlayer {
    private MediaPlayer loose;
    private static AudioLoose single;
    private boolean playing;

    private AudioLoose() {
        playing = false;
    }

    public static AudioLoose getInstance() {
        if (single == null)
            single = new AudioLoose();
        return single;
    }

    @Override
    public void play() {
        Media media = new Media(getClass().getResource("/resources/Audios/76376__deleted_user_877451__game_over.wav").toExternalForm());
        loose = new MediaPlayer(media);
        if (loose != null) {
            loose.play();
            playing = true;
        }
    }

    @Override
    public void stop() {
        if (playing) {
            loose.stop();
            loose.dispose();
            playing = false;
        }
    }
}
