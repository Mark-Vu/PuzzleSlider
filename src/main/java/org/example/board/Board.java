package board;
import java.util.*;

/*
 * This class contains the structure of the board
 * Each state will be one Board 
 */
public class Board implements Comparable<Board> {
    private int[][] board;
    private int distanceFromStart;
    private int heuristicValue;
    private int[] blankPos;
    private String move;
    private Board parent;
    private int hashCode = 0;
    private int boardLength;

    public Board() {

    }

    public Board(int[][] board) {
        this.board = board;
        this.parent = null;
        this.boardLength = this.board.length;
        this.distanceFromStart = 0;
        this.move = "";
        this.blankPos = findBlankPos();
        this.heuristicValue = this.calculate();
        this.hashCode = generateHashCode();
    }

    public ArrayList<Board> generateChild() {
        int x = this.blankPos[0];
        int y = this.blankPos[1];
        int[][] moves = { { x, y - 1 }, { x, y + 1 }, { x - 1, y }, { x + 1, y } };
        //strMoves are in reverse order (D -> U and U ->D) because 0 move up means the upper one moves down
        String[] strMoves = { "R", "L", "D", "U" };
        ArrayList<Board> children = new ArrayList<>();
        int moveLength = moves.length;
        for (int i = 0; i < moveLength; i++) {
            int[][] child = this.shuffle(x, y, moves[i][0], moves[i][1]);
            if (child != null) {
                Board childVertex = new Board(child);
                String m = this.board[moves[i][0]][moves[i][1]] + " " + strMoves[i];
                childVertex.setMove(m);
                childVertex.setDist(this.distanceFromStart + 1);
                children.add(childVertex);
            }

        }

        return children;
    }

    public int[][] shuffle(int x1, int y1, int x2, int y2) {
        if (x2 >= 0 && x2 < this.boardLength && y2 >= 0 && y2 < this.boardLength) {
            int[][] temp_puz = new int[this.boardLength][this.boardLength];
            int temp;
            for (int i = 0; i < this.boardLength; i++) {
                temp_puz[i] = Arrays.copyOf(this.board[i], this.boardLength);
            }
            temp = temp_puz[x2][y2];
            temp_puz[x2][y2] = temp_puz[x1][y1];
            temp_puz[x1][y1] = temp;
            return temp_puz;
        } else {
            return null;
        }
    }

    public int generateHashCode() {
        final int prime = 31;
        int code = 3;
        for (int[] row : this.board) {
            for (int val : row) {
                code = prime * code + val;
            }
        }
        return code;
    }

    public int getHashCode() {
        return this.hashCode;
    }

    public int getDistanceFromStart() {
        return this.distanceFromStart;
    }

    public void setMove(String move) {
        this.move = move;
    }

    public String getMove() {
        return this.move;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

    public void setParent(Board parent) {
        this.parent = parent;
    }

    public Board getParent() {
        return this.parent;
    }

    public void setDist(int newDist) {
        this.distanceFromStart = newDist;
    }

    public void setHeuristicValue(int heuristicValue) {
        this.heuristicValue = heuristicValue;
    }

    public int getHeuristicValue() {
        return this.heuristicValue; // if we make constructor put this in.
    }

    private int getConflicts(int[][] rowConflict, int[][] columnConflict) {
        int rowConflicts = 0;
        int columnConflicts = 0;
        for (int i = 0; i < this.boardLength; i++) {
            for (int j = 0; j < this.boardLength - 1; j++) {
                //get row conflicts
                if (rowConflict[i][j] == i) {
                    for (int k = j + 1; k < this.boardLength; k++) {
                        if (this.board[i][j] > this.board[i][k] && rowConflict[i][k] == i) {
                            rowConflicts += 2;
                        }
                    }
                }
                //get column conflicts
                if (columnConflict[i][j] == j) {
                    for (int k = i + 1; k < this.boardLength; k++) {
                        if (this.board[i][j] > this.board[k][j] && columnConflict[k][j] == j) {
                            columnConflicts += 2;
                        }
                    }
                }
            }
        }
        return rowConflicts + columnConflicts;
    }

    public int misplacedTiles() {
        int count = 0;
        int goalValue = 1;

        // Loop through each tile on the board.
        for (int row = 0; row < this.boardLength; row++) {
            for (int col = 0; col < this.boardLength; col++) {
                // Check if the tile is in the correct position.
                if (this.board[row][col] != goalValue) {
                    count++;
                }
                goalValue++;
                if (goalValue == this.boardLength * this.boardLength) {
                    goalValue = 0;
                }
            }
        }

        // Subtract 1 from the count to exclude the empty tile.
        return count - 1;
    }

    public int calculate() {
        //indicates a tile which is not in goal tile
        if (this.boardLength <= 5) {
            return getManhattan();
        }
        int[][] rowConflict = new int[this.boardLength][this.boardLength];
        int[][] columnConflict = new int[this.boardLength][this.boardLength];
        for (int i = 0; i < this.boardLength; i++) {
            for (int j = 0; j < this.boardLength; j++) {
                //set goal row and column of tile
                if (this.board[i][j] != 0) {
                    rowConflict[i][j] = (this.board[i][j] - 1) / this.boardLength;
                    columnConflict[i][j] = (this.board[i][j] - 1) % this.boardLength;
                } else {
                    //indicates the blank tile with -1 to ensure blank tile is not counted as conflict
                    rowConflict[i][j] = -1;
                    columnConflict[i][j] = -1;
                }
            }
        }
        return getManhattan() + (2 * getConflicts(rowConflict, columnConflict)) + 2 * getECL() + misplacedTiles();

    }

    public int getECL() {
        //calculate Euclidean distance
        int distance = 0;

        for (int i = 0; i < this.boardLength; i++) {
            for (int j = 0; j < this.boardLength; j++) {
                int value = this.board[i][j];
                if (value != 0) {
                    int goal_row = (value - 1) / this.boardLength;
                    int goal_col = (value - 1) % this.boardLength;
                    distance += Math.sqrt(Math.pow(i - goal_row, 2) + Math.pow(j - goal_col, 2));
                }
            }
        }

        return distance;
    }

    public int getManhattan() {
        int distance = 0;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                int value = board[i][j];

                if (value != 0) {
                    int targetI = (value - 1) / board.length;
                    int targetJ = (value - 1) % board.length;
                    distance += Math.abs(i - targetI) + Math.abs(j - targetJ);
                }
            }
        }

        return distance;
    }

    @Override
    public boolean equals(Object other) {
        return other != null && ((Board) other).getHashCode() == this.getHashCode();
    }

    public int[] findBlankPos() {
        int[] pos = new int[2];
        for (int i = 0; i < this.boardLength; i++) {
            for (int j = 0; j < this.boardLength; j++) {
                if (this.board[i][j] == 0) {
                    pos[0] = i;
                    pos[1] = j;
                    break;
                }
            }
        }
        return pos;
    }

    public int compareTo(Board other) {
        if (other != null) {
            return Integer.compare(this.getHeuristicValue(), other.getHeuristicValue());
        }
        return 0;
    }

    public int[][] getBoard() {
        return this.board;
    }
}