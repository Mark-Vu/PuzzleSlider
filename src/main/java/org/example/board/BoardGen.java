package org.example.board;
import java.util.Random;

public class BoardGen {
    private int boardSize;
    private int[][] goalBoard;
    private Random random;

    public BoardGen(int boardSize) {
        this.boardSize = boardSize;
        this.goalBoard = generateGoalBoard();
        this.random = new Random();
    }

    public int[][] generateGoalBoard() {
		int[][] board = new int[boardSize][boardSize];
		int index = 1;

		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (i == boardSize - 1 && j == boardSize - 1) {
					board[i][j] = 0;
					break;
				}
				board[i][j] = index;
				index++;
			}
		}
        
		return board;
	}

    public int[][] generateRandomBoard(int moves) {
        int[][] randomBoard = new int[boardSize][boardSize];
        // Copy the goal board to start with
        for (int i = 0; i < boardSize; i++) {
            System.arraycopy(goalBoard[i], 0, randomBoard[i], 0, boardSize);
        }

        // Perform random moves
        int emptyRow = boardSize - 1;
        int emptyCol = boardSize - 1;

        for (int i = 0; i < moves; i++) {
            // Generate a random direction (0 = up, 1 = down, 2 = left, 3 = right)
            int direction = random.nextInt(4);

            switch (direction) {
                case 0: // Up
                    if (emptyRow > 0) {
                        randomBoard[emptyRow][emptyCol] = randomBoard[emptyRow - 1][emptyCol];
                        randomBoard[emptyRow - 1][emptyCol] = 0;
                        emptyRow--;
                    }
                    break;
                case 1: // Down
                    if (emptyRow < boardSize - 1) {
                        randomBoard[emptyRow][emptyCol] = randomBoard[emptyRow + 1][emptyCol];
                        randomBoard[emptyRow + 1][emptyCol] = 0;
                        emptyRow++;
                    }
                    break;
                case 2: // Left
                    if (emptyCol > 0) {
                        randomBoard[emptyRow][emptyCol] = randomBoard[emptyRow][emptyCol - 1];
                        randomBoard[emptyRow][emptyCol - 1] = 0;
                        emptyCol--;
                    }
                    break;
                case 3: // Right
                    if (emptyCol < boardSize - 1) {
                        randomBoard[emptyRow][emptyCol] = randomBoard[emptyRow][emptyCol + 1];
                        randomBoard[emptyRow][emptyCol + 1] = 0;
                        emptyCol++;
                    }
                    break;
            }
        }

        return randomBoard;
    }

}