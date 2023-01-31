package exercises09;

import exercises09.stopwatchUI;

import javax.swing.*;
import java.util.concurrent.TimeUnit;
/* This example is inspired by the StopWatch app in Head First. Android Development
   http://shop.oreilly.com/product/0636920029045.do
   Modified to Java, October 7, 2021 by Jørgen Staunstrup, ITU, jst@itu.dk
   Updated October 30, 2022*/

public class Stopwatch {
    public static void main(String[] args) {
        new Stopwatch();
    }

    final static JFrame f = new JFrame("Stopwatch");

    //Setting up the three streams for the Buttons and the display
    final static stopwatchUI myUI = new stopwatchUI(0, f);

    public Stopwatch() {
        f.setBounds(0, 0, 220, 220);
        f.setLayout(null);
        f.setVisible(true);

        // Background Thread simulating a clock ticking every 1 second
        new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        TimeUnit.MILLISECONDS.sleep(100);
                        myUI.updateTime();
                    }
                } catch (java.lang.InterruptedException e) {
                    System.out.println(e.toString());
                }
            }
        }.start();

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
