package exercises09.StopwatchGUI;

import javax.swing.*;
import java.util.concurrent.TimeUnit;
/* This example is inspired by the StopWatch app in Head First. Android Development
   http://shop.oreilly.com/product/0636920029045.do
   Modified to Java, October 7, 2021 by Jørgen Staunstrup, ITU, jst@itu.dk
   Updated October 30, 2022*/

public class StopwatchMain {


    public StopwatchMain(JPanel f) {
        StopwatchUI myUI = new StopwatchUI(0, f);

        // Background Thread simulating a clock ticking every 1 second
        new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        TimeUnit.MILLISECONDS.sleep(100);
                        myUI.updateTime();
                    }
                } catch (InterruptedException e) {
                    System.out.println(e.toString());
                }
            }
        }.start();

    }
}
