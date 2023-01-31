package exercises09;

import exercises09.StopwatchGUI.StopwatchMain;

import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class StopwatchN {
    public static void main(String[] args) {
        new StopwatchN();
    }
    final private JButton newStopWatch = new JButton("Create new stop watch");
    final static JFrame fs = new JFrame("StopwatchMain");

    public StopwatchN() {
        JPanel mainPanel=new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        // set up user interface
        newStopWatch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JPanel  newP=new JPanel();
                newP.setLayout(null);
                new StopwatchMain(newP);
                newP.validate();

                mainPanel.add(newP);
                mainPanel.validate();
            }
        });

        fs.setSize(400, 400);

        mainPanel.add(newStopWatch);
        mainPanel.validate();

        fs.add(mainPanel);

        fs.setLocationRelativeTo(null);
        fs.setVisible(true);

        fs.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
