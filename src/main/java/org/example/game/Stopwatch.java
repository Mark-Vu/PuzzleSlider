package org.example.game;


import javax.swing.*;

import org.example.config.ApplicationConfig;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Stopwatch extends JLabel {
    private Timer timer;
    private long startTime;
    private  boolean isRunning; 

    public Stopwatch() {
        setFont(new Font(ApplicationConfig.FONT, Font.PLAIN, ApplicationConfig.FONT_SIZE));
        setText("00:00:00");
        // setBorder(new LineBorder(Color.black, 2));
        setPreferredSize(new Dimension(ApplicationConfig.BUTTON_WIDTH, 50));

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long currentTime = System.currentTimeMillis();
                long elapsedTime = (currentTime - startTime) / 1000; // in seconds
                updateTimerLabel(elapsedTime);
            }
        });

        isRunning =  false ; 
    }

    private void updateTimerLabel(long elapsedTime) {
        long hours = elapsedTime / 3600;
        long minutes = (elapsedTime % 3600) / 60;
        long seconds = elapsedTime % 60;
        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        setText(timeString);
    }

    public void start() {
        startTime = System.currentTimeMillis();
        if(isRunning == false ){
            timer.start();
            isRunning = true ;
        }
    }

    public void stop() {
        if (isRunning == true ) {
            timer.stop();
            isRunning = false;
        }
    }

    public boolean isRunning() { 
        return isRunning;
    }
}
