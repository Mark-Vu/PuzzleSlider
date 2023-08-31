package org.example;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.example.config.ApplicationConfig;
import org.example.config.DatabaseConfig;
import org.example.menu.MenuUI;

import com.mongodb.client.MongoDatabase;

class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        MongoDatabase db = DatabaseConfig.getDatabase();

        JFrame frame = new JFrame();
        frame.setSize(ApplicationConfig.FRAME_WIDTH, ApplicationConfig.FRAME_HEIGHT);
        MenuUI mainFrame = new MenuUI(frame);
    }
}