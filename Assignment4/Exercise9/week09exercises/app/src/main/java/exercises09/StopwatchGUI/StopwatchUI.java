package exercises09.StopwatchGUI;

import exercises09.SecCounter;

import java.awt.event.*;
import javax.swing.*;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
// User interface for Stopwatch, October 7, 2021 by Jørgen Staunstrup, ITU, jst@itu.dk
// Updated October 30, 2022

public class StopwatchUI {
    private static JPanel lf;
    final private JButton startButton = new JButton("Start");
    final private JButton stopButton = new JButton("Stop");
    final private JButton resetButton = new JButton("Reset");
    final private JTextField tf = new JTextField();

    final private String allzero = "0:00:00:0";
    private SecCounter lC = new SecCounter(0, false, tf);

    public synchronized void updateTime() {
        int milliseconds = lC.incr();
        // Potentila race condition !!!
        if (milliseconds >= 0) {
            int ms = milliseconds *100;
            String time = String.format(Locale.getDefault(), "%d:%02d:%02d:%d",
                    TimeUnit.MILLISECONDS.toHours(ms) -
                            TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(ms)),
                    TimeUnit.MILLISECONDS.toMinutes(ms) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
                    TimeUnit.MILLISECONDS.toSeconds(ms) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)),
                    (milliseconds %10)
            );
            tf.setText(time);
        }
    }

    public StopwatchUI(int x, JPanel jF) {
        int lx = x + 50;
        lf = jF;
        tf.setBounds(lx, 10, 100, 20);
        tf.setText(allzero);

        startButton.setBounds(lx, 50, 95, 25);
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lC.setRunning(true);
            }
        });

        stopButton.setBounds(lx, 90, 95, 25);
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lC.setRunning(false);
            }
        });

        resetButton.setBounds(lx, 130, 95, 25);
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lC.reset();
            }
        });

        // set up user interface
        lf.add(tf);
        lf.add(startButton);
        lf.add(stopButton);
        lf.add(resetButton);
    }
}