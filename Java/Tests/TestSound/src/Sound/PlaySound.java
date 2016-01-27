package Sound;

import java.io.File;
import java.net.URL;

/**
 * Created by Chizhov-AS on 02.11.2015.
 */
public class PlaySound implements Runnable {

    private File wavFile = new File(getClass().getResource("/Sound/AlarmSound.wav").getPath());

// передать сразу AudioInputStream



    @Override
    public void run() {

        System.out.println("3");

        Sound sound = new Sound(wavFile);

        sound.play();
        sound.join(); // Ждем пока завершиться воспроизведение звука

        System.out.println("4");
    }
}