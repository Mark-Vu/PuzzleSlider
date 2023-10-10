package org.example.game;
import org.example.Solver.SolveType;
import org.example.Solver.Solver;
import org.example.config.ApplicationConfig;
import org.example.menu.MenuUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;


public class GameUI implements ActionListener {
    Color tileColor = Color.LIGHT_GRAY;
    
    private JFrame frame;
    private JPanel topPanel;
    private JPanel midPanel;
    private JPanel bottomPanel;

    private JPanel board;
    public int boardSize;
    public JButton[] tiles; // represent the board 
    public JLabel countMoveLabel;

    public JButton backButton;

    public static JButton hintButton; //static calls in GameLogic
    public static JButton solveButton;
    public JLabel timerLabel; 

    GameLogic gameLogic;

    MenuUI menu;

    public Stopwatch stopWatch;

    public static SolveType selectedSolveType = SolveType.BFS; // for storing value of algorithm drop down menu
    private Map<String, SolveType> algorithmMap = new HashMap<>();
    
    public GameUI(int size, JFrame frame) {
        this.frame = frame;
        boardSize = size;
        this.bottomPanel = this.createBottomPanel();
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.topPanel = this.createTopPanel();
        this.board = gameLogic.createBoard();
        this.midPanel = this.createMidPanel();
        algorithmMap.put("BFS", SolveType.BFS);
        algorithmMap.put("A*", SolveType.A_STAR);
        algorithmMap.put("DFS", SolveType.DFS);
        drawGame(this.frame.getWidth(), this.frame.getHeight());
        this.frame.setVisible(true);
    }
    
    private JComboBox<String> createAlgoDropDownMenu() {
        String[] algorithms = { "BFS", "A*", "DFS" };
        JComboBox<String> algorithmComboBox = new JComboBox<>(algorithms);
    
        // Set the default selection to "BFS"
        algorithmComboBox.setSelectedItem("BFS");
        this.selectedSolveType = SolveType.BFS;
    
        // Add an ActionListener to the dropdown menu to update the selected algorithm
        algorithmComboBox.addActionListener(e -> {
            String selected = (String) algorithmComboBox.getSelectedItem();
            if (selected != null) {
                SolveType selectedType = algorithmMap.get(selected);
                if (selectedType != null) {
                    this.selectedSolveType = selectedType;
                }
            }
        });
    
        return algorithmComboBox;
    }
    

    private JPanel createTopPanel() {
            JPanel topPanel = new JPanel(new BorderLayout());
            // topPanel.setBackground(Color.blue);
            topPanel.setPreferredSize(new Dimension(this.frame.getWidth(), 100));


        this.backButton = this.createBackButton();
        this.solveButton = this.createSolveButton();
        this.hintButton = this.createHintButton();
        

        //To store backButton, solveButton and hintButton
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,25));
        // buttonPanel.setBackground(Color.BLUE);
        buttonPanel.add(this.backButton);
        buttonPanel.add(this.hintButton);
        buttonPanel.add(this.solveButton);
        buttonPanel.add(this.createAlgoDropDownMenu());
        
        topPanel.add(buttonPanel, BorderLayout.WEST);
        // topPanel.add(this.solveButton, BorderLayout.EAST);
        return topPanel;
    }


    public JPanel createMidPanel() {
        JPanel midPanel = new JPanel(new BorderLayout());
        midPanel.setBackground(Color.blue);
        int midPanelHeight = this.frame.getHeight() - topPanel.getHeight() - bottomPanel.getHeight();
        midPanel.setPreferredSize(new Dimension(this.frame.getWidth(), midPanelHeight));

        midPanel.add(this.board);
        return midPanel;
    }

    public JPanel createBottomPanel() {
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 200 , 0));
        // bottomPanel.setBackground(Color.blue);
        // bottomPanel.setBackground(Color.red);
        bottomPanel.setPreferredSize(new Dimension(this.frame.getWidth(),60));
        this.countMoveLabel = this.createLabel("Total move : 0");
        this.stopWatch = new Stopwatch();
        
        bottomPanel.add(this.countMoveLabel);
        bottomPanel.add(this.stopWatch);

        return bottomPanel;
    }

    public void drawGame(int WIDTH, int HEIGHT) {
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setLayout(new BoxLayout(this.frame.getContentPane(), BoxLayout.Y_AXIS));
        this.frame.pack();
        this.frame.setSize(WIDTH, HEIGHT);
        this.frame.add(topPanel, BorderLayout.NORTH);
        this.frame.add(midPanel, BorderLayout.CENTER);
        this.frame.add(bottomPanel, BorderLayout.SOUTH);
        this.frame.setVisible(true);
    }

    public JButton createBackButton() {
        backButton = new JButton("back");
        backButton.setPreferredSize(new Dimension(ApplicationConfig.BUTTON_WIDTH, ApplicationConfig.BUTTON_HEIGHT));
        backButton.setActionCommand("backToMenu");
        backButton.addActionListener(this);
        return backButton;
    }

    public JButton createHintButton() {
        hintButton = new JButton("hint !");
        hintButton.setPreferredSize(new Dimension(ApplicationConfig.BUTTON_WIDTH, ApplicationConfig.BUTTON_HEIGHT));
        hintButton.setActionCommand("showHint");
        hintButton.addActionListener(gameLogic);
        return hintButton;
    }


    public JButton createSolveButton() {
        solveButton = new JButton("Solve");
        solveButton.setPreferredSize(new Dimension(ApplicationConfig.BUTTON_WIDTH, ApplicationConfig.BUTTON_HEIGHT));
        solveButton.setActionCommand("solveBoard");
        gameLogic = new GameLogic(boardSize, countMoveLabel, this.stopWatch,this.frame);
        solveButton.addActionListener(gameLogic);

        return solveButton;
    }

    public JLabel createLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(ApplicationConfig.FONT, Font.PLAIN, ApplicationConfig.FONT_SIZE));
        // label.setBorder(new LineBorder(Color.black, 2, true));
        label.setPreferredSize(new Dimension(ApplicationConfig.BUTTON_WIDTH + 50, 50));
        return label;
    }


    public void destroy() {
        this.frame.getContentPane().removeAll();
        this.frame.repaint();
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();

        if (clickedButton.getActionCommand().equals("backToMenu")) {
            this.destroy();
            menu = new MenuUI(this.frame);
        }
    
    }

}