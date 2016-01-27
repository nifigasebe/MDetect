package Sound;

import java.io.File;
import java.net.URL;

/**
 * Created by Chizhov-AS on 02.11.2015.
 */
public class PlaySound implements Runnable{

    private URL location = getClass().getResource("/Sound/AlarmSound.wav");
    private File wavFile = new File(location.getFile());

    @Override
    public void run() {
        Sound sound = new Sound(wavFile);
        sound.play();
        sound.join(); // Ждем пока завершиться воспроизведение звука
    }
}