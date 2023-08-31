package org.example.ranking;

import org.example.config.ApplicationConfig;
import org.example.dao.UserDAO;
import org.example.menu.MenuUI;
import org.example.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;

public class RankingUI implements ActionListener {
    private JFrame frame;
    private JButton backButton;
    private JPanel topPanel;
    private JScrollPane rankingPane;
    private JPanel loadingPanel;
    private JTable rankingsTable;
    private DefaultTableModel tableModel;


    private JButton[] rankingButtons = new JButton[4];

    private HashMap<Integer, List<User>> usersRankings = new HashMap<>();
    public RankingUI(JFrame frame) {
        this.frame = frame;
        this.drawGame(this.frame.getWidth(), this.frame.getHeight());
    }

    public void drawGame(int WIDTH, int HEIGHT) {
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(WIDTH, HEIGHT);


        this.topPanel = this.createTopPanel();
        this.frame.add(topPanel, BorderLayout.NORTH);


        this.rankingPane = createRankingPanel();
        this.loadingPanel = this.createLoadingPanel();
        frame.add(this.loadingPanel, BorderLayout.CENTER);
        SwingUtilities.invokeLater(() -> {
            // Call methods that require loading here
            this.usersRankings = UserDAO.getUsersByAllBoardSizesRanked();
            this.loadTopRankings(3);
            this.loadingPanel.setVisible(false);
            this.rankingPane.setVisible(true);
            frame.add(this.rankingPane, BorderLayout.CENTER);
        });

        this.frame.setVisible(true);
    }

    public JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(this.frame.getWidth(), 100));
        this.backButton = this.createButtons("back", "backToMenu");

        //To store buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,25));
        buttonPanel.add(this.backButton);
        String[] boardSizes = {"3x3", "4x4", "5x5", "6x6"};
        for (int i = 0; i < 4; i++) {
            rankingButtons[i] = this.createButtons(boardSizes[i], "ranking" + boardSizes[i]);
            buttonPanel.add(rankingButtons[i]);
        }
        JPanel reloadPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 25));
        JButton reloadButton = this.createButtons("Reload", "reloadRankings");
        reloadPanel.add(reloadButton);
        topPanel.add(buttonPanel, BorderLayout.WEST);
        topPanel.add(reloadPanel, BorderLayout.EAST);
        return topPanel;
    }

    public JScrollPane createRankingPanel() {
        String[] columnNames = {"Rank", "Name", "Country", "Score", "Time", "Moves"};
        tableModel = new DefaultTableModel(columnNames, 0);
        rankingsTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(rankingsTable);
        return scrollPane;
    }

    public static JPanel createLoadingPanel() {
        JPanel loadingPanel = new JPanel(new BorderLayout()); // Use BorderLayout for vertical alignment

        JLabel loadingLabel = new JLabel("Loading...", SwingConstants.CENTER);
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the text horizontally

        loadingPanel.add(loadingLabel, BorderLayout.NORTH); // Place the label at the top

        return loadingPanel;
    }

    public JButton createButtons(String text, String command) {
        JButton createdButton = new JButton(text);
        createdButton.setPreferredSize(new Dimension(ApplicationConfig.BUTTON_WIDTH, ApplicationConfig.BUTTON_HEIGHT));
        createdButton.setActionCommand(command);
        createdButton.addActionListener(this);
        return createdButton;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();

        if (clickedButton.getActionCommand().equals("backToMenu")) {
            this.destroy();
            MenuUI menu = new MenuUI(this.frame);
        } else if (clickedButton.getActionCommand().equals("reloadRankings")) {
            this.rankingPane.setVisible(false);
            this.loadingPanel.setVisible(true);
            SwingUtilities.invokeLater(() -> {
                this.usersRankings = UserDAO.getUsersByAllBoardSizesRanked();
                this.loadTopRankings(3);
                this.loadingPanel.setVisible(false);
                this.rankingPane.setVisible(true);
            });
        }
        else {
            String buttonText = clickedButton.getText();
            int boardSize = Integer.parseInt(buttonText.substring(0, 1));
            loadTopRankings(boardSize);
        }
    }
    public void destroy() {
        this.frame.getContentPane().removeAll();
        this.frame.repaint();
    }

    private void loadTopRankings(int boardSize) {
        // Retrieve the list of users for the specified board size from the usersRankings map
        List<User> topRankedUsers = usersRankings.get(boardSize);

        // Clear the table data before adding new rows
        tableModel.setRowCount(0);
        rankingPane.setBackground(Color.BLACK);
        if (topRankedUsers.size() > 0) {
            for (int i = 0; i < topRankedUsers.size(); i++) {
                User user = topRankedUsers.get(i);
                Object[] rowData = {
                        (i + 1),
                    user.getName(),
                    user.getCountry(),
                    user.getScores().get(boardSize).getScore(),
                    user.getScores().get(boardSize).getTime(),
                    user.getScores().get(boardSize).getMoves()
                };
                tableModel.addRow(rowData);
            }
        } else {
            // Handle the case when no users are found for the specified board size
            Object[] noRecordData = {"No record", "", "", "", "", ""};
            tableModel.addRow(noRecordData);
        }
    }
}