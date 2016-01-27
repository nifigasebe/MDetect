package Start;
import Sound.PlaySound;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {

        System.out.println("1");

        PlaySound playSound = new PlaySound();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(playSound);
        executorService.shutdown();

        System.out.println("2");
    }
}