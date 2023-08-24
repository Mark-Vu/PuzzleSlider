package src.main.java.org.example;

import config.Config;
import menu.MenuUI;

import javax.swing.JFrame;
import javax.swing.UIManager;

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