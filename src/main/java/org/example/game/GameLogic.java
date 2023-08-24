package game;
import board.BoardGen;
import config.Config;
import menu.MenuUI;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Timer;




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
                        private Color[] colors = {tileColor, Config.HINT_COLOR};

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
                this.victoryPopUp();
               
            }

                this.currentBoard = this.updateBoard();
            
        }


        if (clickedButton.getActionCommand().equals("showHint") ) {
            if (this.isComplete()) {
                System.out.println("DUMA its solved");
            } else {
                this.showHint();
            }
        }

        if (clickedButton.getActionCommand().equals("solveBoard")){
            ArrayList<String> result = this.solveBoard();
            while (result.isEmpty() == false) { 
                System.out.println("testing the code");

                int number = this.extractNumber(result.get(0));

                System.out.println("number : " + number ) ;

                
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
    
                result.remove(0) ;                

            }
            
           
        }

    
    }

    public void victoryPopUp() {
        String message1 = "total move : " + countMove;
        String message2 = "total time : " + stopWatch.getText() ; 
        String completeMessage = message1 + "\n" + message2;
        JOptionPane.showMessageDialog(null, completeMessage, "Congratulations!", JOptionPane.PLAIN_MESSAGE);
        this.frame.getContentPane().removeAll();
        this.frame.repaint();
        MenuUI menu = new MenuUI(this.frame.getWidth(), this.frame.getHeight(), this.frame);
    }
//    private void getCoordinateOfNumberedButton (String label){

//         for (Component component : boardPanel.getComponents()) {
//             if (component instanceof JButton && ((JButton) component).getText().equals(label)) {
//                 int x = component.getX();
//                 int y = component.getY();
//                 int row = (y / component.getHeight());
//                 int col = (x / component.getWidth());
//                 this.numberedRow = Integer.toString(row);
//                 this.numberedColumn = Integer.toString(col) ; 
//                 break;
//             }
//         }

//    }

//    private void getCoordinateOfEmptyButton (){
//     String label = "" ;

//     for (Component component : boardPanel.getComponents()) {
//         if (component instanceof JButton && ((JButton) component).getText().equals(label)) {
//             int x = component.getX();
//             int y = component.getY();
//             int row = (y / component.getHeight());
//             int col = (x / component.getWidth());
//             this.emptyColumn = Integer.toString(col) ; 
//             break;
//         }
//     }

}


