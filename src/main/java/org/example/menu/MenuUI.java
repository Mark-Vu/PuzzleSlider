package org.example.menu;


import javax.swing.*;

import org.example.game.GameUI;
import org.example.ranking.RankingUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuUI extends Container implements ActionListener {
    private final String GAME_NAME = "BroSlider";
    private JFrame frame;
    JPanel titlePanel;
    JPanel optionPanel;

    public MenuUI(JFrame frame) {
        this.frame = frame;
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setLayout(new BorderLayout());
        this.frame.setLocationRelativeTo(null);
        this.drawMenu();
        this.frame.setVisible(true);
    }

    private void drawMenu() {
        this.titlePanel = createTitlePanel();
        this.optionPanel = createOptionPanel();
        frame.add(this.titlePanel, BorderLayout.NORTH);
        frame.add(this.optionPanel, BorderLayout.CENTER);
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setPreferredSize(new Dimension(100, 100));
        JLabel title = new JLabel(GAME_NAME, SwingConstants.CENTER);
        Font titleFont = new Font("Serif", Font.PLAIN, 90);
        title.setFont(titleFont);
        titlePanel.add(title, BorderLayout.CENTER);
        return titlePanel;
    }

    private JPanel createOptionPanel() {
        JPanel optionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        //insets controls the padding of each buttons
        gbc.insets = new Insets(10, 0, 10, 0);

        addButton("3x3", optionPanel, gbc);
        addButton("4x4", optionPanel, gbc);
        addButton("5x5", optionPanel, gbc);
        addButton("6x6", optionPanel, gbc);
        addButton("Ranking", optionPanel, gbc);

        optionPanel.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight()));
        optionPanel.setBackground(Color.GRAY);
        return optionPanel;
    }

    private void addButton(String text, JPanel panel, GridBagConstraints constraints) {
        JButton button = new JButton(text);
        int button_width = 150;
        int button_height = 50;
        button.setPreferredSize(new Dimension(button_width, button_height));
        button.setMinimumSize(new Dimension(button_width, button_height)); // Set the minimum size of the button
        button.addActionListener(this);
        panel.add(button, constraints);
    }

    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();
        String buttonText = clickedButton.getText();
        frame.getContentPane().removeAll();
        frame.repaint();
        if (buttonText.equals("Ranking")) {
            RankingUI ranking = new RankingUI(frame);
        } else {
            int boardSize = Integer.parseInt(buttonText.substring(0, 1));
            GameUI game = new GameUI(boardSize, frame);
        }
    }

}