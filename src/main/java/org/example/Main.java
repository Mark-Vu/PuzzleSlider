package org.example;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.example.config.Config;
import org.example.menu.MenuUI;

class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame();
        MenuUI mainFrame = new MenuUI(Config.FRAME_WIDTH, Config.FRAME_HEIGHT, frame);
    }
}