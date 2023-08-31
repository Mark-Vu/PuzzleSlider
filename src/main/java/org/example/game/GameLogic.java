package org.example.game;
import org.example.board.BoardGen;
import org.example.config.ApplicationConfig;
import org.example.dao.UserDAO;
import org.example.menu.MenuUI;
import org.example.model.User;

import org.example.ranking.ScoreRecord;
import org.w3c.dom.UserDataHandler;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public  class GameLogic implements ActionListener{
    Color tileColor = Color.LIGHT_GRAY;

    private JFrame frame;
    private int boardSize;

    private  JButton[] tiles ; //Contains all the tiles
    private  int countMove;
    private JLabel countMoveLabel;

    private  int[][] currentBoard;

    private  Solver solver;
    private  boolean isShowingHint = false;

    private Stopwatch stopWatch;
    private GridLayout buttonGrid ;
    private JPanel boardPanel;

    private boolean usedHint = false;
    private boolean usedSolver = false ;

    private MouseListener emptyMouseListener = new MouseAdapter() {};

    private JTextArea textArea;
    private JComboBox<String> countryComboBox;

    /*
     * use to track the coordinate of button with number and empty button 
     */
    private String numberedRow ; 
    private String  numberedColumn;
    private String  emptyRow ; 
    private String  emptyColumn ; 

    public GameLogic(int boardSize, JLabel countMoveLabel, Stopwatch stopWatch, JFrame frame) {
        this.frame = frame;
        this.boardSize = boardSize;
        countMove = 0;
        this.stopWatch = new Stopwatch();
        this.initializeBoard();
        this.countMoveLabel = countMoveLabel;
        this.stopWatch = stopWatch;
    }
    public void initializeBoard() {
        /*
        * Initialize the board
        */
        BoardGen boardGen = new BoardGen(boardSize);

        //This will determine the difficulty of the board, the higher -> the harder the board
        this.currentBoard = boardGen.generateRandomBoard(5);
    }

    public static int[] flatten2DArray(int[][] arr) {
        /*
* 2D array to 1D array
*/
        int rows = arr.length;
        int cols = arr[0].length;

        int[] flattened = new int[rows * cols];
        int index = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                flattened[index] = arr[i][j];
                index++;
            }
        }

        return flattened;
    }

    public JPanel createBoard() {
        // creating a board

        buttonGrid  = new GridLayout(boardSize, boardSize ) ; 
        boardPanel = new JPanel(buttonGrid);
        tiles = new JButton[boardSize * boardSize];
        int[] currentBoard1D = flatten2DArray(this.currentBoard);
        int temp = boardSize * boardSize;
        JButton emptyButton = new JButton("");

        for (int i = 0; i < temp; i++) {
            JButton button;
            if (currentBoard1D[i] == 0) {
                button = emptyButton;
            } else {
                button = new JButton(String.valueOf(currentBoard1D[i])); // Create button with element value
            }
            button.setActionCommand("puzzleButton");
            tiles[i] = button;
            tiles[i].setFont(new Font("Monospaced", Font.PLAIN, 40));
            this.boardPanel.add(button);
        }

        //Set padding of the middle panel 
        this.boardPanel.setBorder(new LineBorder(Color.GRAY, 10));

        for (JButton button : tiles) {
            button.addActionListener(this);
            button.setBackground(Color.lightGray);
        }

        this.currentBoard = this.updateBoard();
        this.boardPanel.setBounds(200, 65, 500, 500);
        return this.boardPanel;
    }

    public  int[][] updateBoard() {
        /*
* updates the board from tiles to 2d array int[][] board
* Called when create the board and whenever player make changes on board
*/
        int[][] updatedBoard = new int[boardSize][boardSize];
        int index = 0;
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                String buttonText = tiles[index].getText();
                if (buttonText.equals("")) {
                    updatedBoard[row][col] = 0; // Assuming an empty cell is represented by 0
                } else {
                    updatedBoard[row][col] = Integer.parseInt(buttonText);
                }
                index++;
            }
        }

        return updatedBoard;
    }

    public  int extractNumber(String input) {
        /*
*  result element is "8D" -> return 8
*/
        Pattern pattern = Pattern.compile("(\\d+)\\s");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1; // or handle the case when no match is found
    }

    public  char extractMove(String input) {
        /*
*  result element is "8D" -> return D
*/
        Pattern pattern = Pattern.compile("\\s(\\D)");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1).charAt(0);
        }
        return '\0'; // or handle the case when no match is found
    }


    public  void showHint() {
        ArrayList<String> result = solveBoard();
        if (!result.isEmpty()) {
            String firstElement = result.get(0);

            int number = extractNumber(firstElement);
            char move = extractMove(firstElement);
            showHintAnimation(number, move );
        }

    }

    private JButton hintTile;
    public  void showHintAnimation(int number, char move) {
        /*
* Change color when player press hint
*/
        for (int i = 0; i < this.tiles.length; i++) {
            if (!this.tiles[i].getText().isEmpty()) {
                int tileNumber = Integer.parseInt(this.tiles[i].getText());
                if (tileNumber == number) {
                    isShowingHint = true;
                    hintTile = this.tiles[i];
                    //Changing colors
                    Timer timer = new Timer(200, new ActionListener() {
                        private int counter;
                        private Color[] colors = {tileColor, ApplicationConfig.HINT_COLOR};

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (isShowingHint) {
                                if (counter < colors.length) {
                                    hintTile.setBackground(colors[counter]);
                                    counter++;

                                } else {
                                    counter = 0;
                                }
                            } else {
                                ((Timer) e.getSource()).stop();
                            }
                        }
                    });
                    timer.start();
                }
            }
        }
    }

    public  void stopHintAnimation(){
        if (isShowingHint == true) {
            hintTile.setBackground(tileColor);
            isShowingHint = false;
        }
    }



    public  ArrayList<String> solveBoard() {
        /*
* Return the result list
* ex: {"8 U", "5 D", ...}
*/
        currentBoard = this.updateBoard();
        solver = new Solver(currentBoard, boardSize);
        ArrayList<String> result = solver.returnResult();
        return result;

    }


    public  boolean isAdjacent(JButton button1, JButton button2) {


        int button1Index = -1;
        int button2Index = -1;

        for (int i = 0; i < this.tiles.length; i++) {
            if (this.tiles[i] == button1) {
                button1Index = i;
            } else if (this.tiles[i] == button2) {
                button2Index = i;
            }
        }

        int rowDiff = Math.abs(button1Index / boardSize - button2Index / boardSize);
        int colDiff = Math.abs(button1Index % boardSize - button2Index % boardSize);

        return (rowDiff == 0 && colDiff == 1) || (rowDiff == 1 && colDiff == 0);
    }

    public  boolean isComplete (){ 
        for(int i = 0 ; i < this.tiles.length-2 ; i++ ){
            int temp1  = 0 ;  
            int temp2 = 0 ; 
            if (this.tiles[i].getText().equals("") == false){
                temp1 = Integer.parseInt(this.tiles[i].getText()) ;
            }
            if(this.tiles[i+1].getText().equals("") == false){
                temp2 = Integer.parseInt(this.tiles[i+1].getText()) ;
            }
            if(temp1 >temp2)    
                return false ;
        }
        return true ; 

    }

    private void updateCountMoveLabel() {
        countMoveLabel.setText("total move : " + countMove);
    }

    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();
        // Check if the button clicked is the "board"
        if (clickedButton.getActionCommand().equals("puzzleButton")) {

            JButton swappedButton = null;
            // Find the empty space button
            for (JButton button : this.tiles) {
                if (button.getText().equals("")) {
                    swappedButton = button;
                    break;
                }
            }

            if (this.isAdjacent(clickedButton, swappedButton)) {
                this.stopHintAnimation();
                String tempText = clickedButton.getText();
                clickedButton.setText(swappedButton.getText());
                swappedButton.setText(tempText);
                ++countMove;
                updateCountMoveLabel();
            }


            if (  (stopWatch.isRunning() == false && this.isComplete() == false) || (stopWatch.isRunning() == false && this.isComplete() == true) ){ 
                stopWatch.start() ;
            }
            if (countMove > 0 && stopWatch.isRunning() == true && this.isComplete() == true ){
                /*
* Check if player wins
* If wins -> popup message and get back to the menu
*/
                stopWatch.stop();
                this.showPopUp();

            }

            this.currentBoard = this.updateBoard();

        }


        if (clickedButton.getActionCommand().equals("showHint") ) {
            usedHint = true ;
            this.showHint();
        }

        if (clickedButton.getActionCommand().equals("solveBoard")) {
            stopWatch.stop();
            usedSolver = true;
            ArrayList<String> result = this.solveBoard();

            Timer timer = new Timer(400, new ActionListener() {
                private int currentIndex = 0;
                private int blinkCount = 0;
                private Color[] colors = {Color.YELLOW, tileColor};

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (currentIndex < result.size()) {
                        String move = result.get(currentIndex);
                        int number = extractNumber(move);
                        char direction = extractMove(move);
                        JButton targetTile = findTileWithNumber(number);
                        JButton emptyTile = findEmptyTile();


                        if (blinkCount < 5) { 
                            targetTile.setBackground(colors[blinkCount % colors.length]);
                            blinkCount++;
                        } else {
                            targetTile.setBackground(Color.LIGHT_GRAY); 
                            performSwapping(targetTile, emptyTile, direction);
                            currentIndex++;
                            blinkCount = 0; 
                        }

                    } else {
                        ((Timer) e.getSource()).stop();
                        setButtonsAndTilesClickable(true);
                        showPopUp() ; 
                    }
                }
            });
            setButtonsAndTilesClickable(false); 
            timer.start();
        }

    }

    private JButton findTileWithNumber(int number) {
        for (JButton tile : tiles) {
            if (tile.getText().equals(String.valueOf(number))) {
                return tile;
            }
        }
        return null;
    }
    private void performSwapping (JButton targetTile , JButton emptyTile , char direction){ 
        if (direction == 'U' && isAdjacent(targetTile, emptyTile)) {
            swapTiles(targetTile, emptyTile);
        } else if (direction == 'D' && isAdjacent(targetTile, emptyTile)) {
            swapTiles(targetTile, emptyTile);
        } else if (direction == 'L' && isAdjacent(targetTile, emptyTile)) {
            swapTiles(targetTile, emptyTile);
        } else if (direction == 'R' && isAdjacent(targetTile, emptyTile)) {
            swapTiles(targetTile, emptyTile);
        }
    }
    private JButton findEmptyTile() {
        for (JButton button : tiles) {
            if (button.getText().equals("")) {
                return button;
            }
        }
        return null; 
    }


    private void swapTiles(JButton tile1, JButton tile2) {
        String tempText = tile1.getText();
        tile1.setText(tile2.getText());
        tile2.setText(tempText);
        ++countMove;
        updateCountMoveLabel();
    }
    public void victoryPopUp() {
        String message1 = "total move : " + countMove;
        String message2 = "total time : " + stopWatch.getText();
        String completeMessage = message1 + "\n" + message2;
        /*
* change the condition to determine display or not display the name box and the country box
*/
        long time = stopWatch.getElapsedTime();

        //ScoreRecord also calculated the score for us
        ScoreRecord scoreRecord = new ScoreRecord(time, countMove, boardSize);

        //If the user is better than the user ranked 50 then ask for name and country
        if (UserDAO.isBetterThanTop50(boardSize, scoreRecord.getScore(), time, countMove)) {
            JPanel inputPanel = panelForTop50Players();

            Object[] message = { completeMessage, inputPanel };
            int result = JOptionPane.showConfirmDialog(
                    null, message, "Congratulations!",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {

                String enteredName = textArea.getText();
                String selectedCountry = (String) countryComboBox.getSelectedItem();

                this.frame.getContentPane().removeAll();
                this.frame.repaint();

                HashMap<Integer, ScoreRecord> scoreRecordHashMap = new HashMap<>();
                scoreRecordHashMap.put(boardSize, scoreRecord);
                User user = new User(enteredName, selectedCountry, scoreRecordHashMap);
                UserDAO.insertUser(user, boardSize);
                MenuUI menu = new MenuUI(this.frame);
            }

            
        }
        else{

            JOptionPane.showMessageDialog(null, completeMessage, "Congratulations!", JOptionPane.PLAIN_MESSAGE);
            this.frame.getContentPane().removeAll();
            this.frame.repaint();
            MenuUI menu = new MenuUI(this.frame);
        }
    
        
    

    }

    private JPanel panelForTop50Players() {
        JLabel nameLabel = new JLabel("Enter your name:");
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        textArea = new JTextArea(1, 20);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
    
        JLabel countryLabel = new JLabel("Select your country:");
        countryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        String[] countries = Locale.getISOCountries();
        countryComboBox = new JComboBox<>();
    
        for (String country : countries) {
            Locale obj = new Locale("", country);
            countryComboBox.addItem(obj.getDisplayCountry());
        }
    
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.add(nameLabel);
        inputPanel.add(scrollPane);
        inputPanel.add(countryLabel);
        inputPanel.add(countryComboBox);
    
        return inputPanel;
    }


    /*
     * precondition : solve button is clicked or hint button is clicked
     * postcondition : pop up appear 
     * description: if the user use the solve button or the hint to solve the board, the user are not allow to store the result
     *              in the database
     */
    public void normalPopUp(){ 

        String message1 = "total move : " + countMove;
        String message2 = "Result cannot be saved due to the use of hint or solver functions."; 
        String completeMessage = message1 + "\n" + message2;
        JOptionPane.showMessageDialog(null, completeMessage, "Puzzle completed", JOptionPane.PLAIN_MESSAGE);
        this.frame.getContentPane().removeAll();
        this.frame.repaint();
        MenuUI menu = new MenuUI(this.frame);

    }


    /*
    *description : different situation show different pop up : 
    * if the user click on the hint button or the victoruPopUp button , the it will display the normal pop up 
    * if the user complete the puzzle without any hint or using the solve button, the victory pop up 
    
    */ 
    private void showPopUp(){ 

        if (usedHint || usedSolver)
            normalPopUp();
        else
            victoryPopUp();
    }

    
    /*
     * description : make the hint button , solve button and the tiles non clickable while performing the solve animation
     * 
     */
    private void setButtonsAndTilesClickable(boolean clickable) {
        GameUI.hintButton.setEnabled(clickable);
        GameUI.solveButton.setEnabled(clickable);
    
        for (JButton tile : tiles) {
            if (clickable) {
                tile.addActionListener(this);
                tile.addMouseListener(emptyMouseListener); // Remove the empty mouse listener
            } else {
                tile.removeActionListener(this);
                tile.addMouseListener(emptyMouseListener); // Add the empty mouse listener
            }
        }
    }
}