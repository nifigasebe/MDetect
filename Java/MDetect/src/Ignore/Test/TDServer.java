package Test;

import javafx.application.Platform;

/**
 * Created by Chizhov-AS on 19.10.2015.
 */
public class TDServer implements Runnable {

    @Override
    public void run() {
        test();
    }

    private void test(){
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                }catch(InterruptedException iEX) {
                    iEX.printStackTrace();
                }
            }
        };
    }
}
