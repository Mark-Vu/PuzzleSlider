package org.example.game;

public class dfsSolver{

    public int boardSize = 0 ; 
    public int[][] goalBoard ; 
    public int[][] initialBoard;




    private int getBoardSize() { 
        return boardSize; 
    }
    private  int [][] getGoalBoard() { 
        return goalBoard;
    }

    private int [][] getInitialBoard() { 
        return initialBoard;
    }
    
    public void setBoardSize(int boardSize) { 
        this.boardSize  = boardSize ; 
    }

    public void setGoalBoard (int boardSize) {
        int[][] temp = new int [boardSize][boardSize];
		int num = 1;
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				temp[i][j] = num ;
                num++ ; 
			}
		}
        temp [boardSize-1][boardSize-1]  = 0 ; 
    
        this.goalBoard = temp ; 
        return ; 
    }  
}