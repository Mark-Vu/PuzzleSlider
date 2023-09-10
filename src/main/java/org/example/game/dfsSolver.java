package org.example.game;

import org.example.board.Board;
import org.example.board.BoardGen;

import java.util.*;


public class dfsSolver {
    public int SIZE;
    public int[][] goalBoard;
    public int[][] startBoard;
    private BoardGen boardGen;
    private HashSet<Integer> visited;

    public dfsSolver(int[][] startBoard, int SIZE) {
        this.startBoard = startBoard;
        this.SIZE = SIZE;
        this.boardGen = new BoardGen(SIZE);
        this.visited = new HashSet<>();
    }

    public ArrayList<String> returnResult() {
        ArrayList<String> result = new ArrayList<>();
        this.goalBoard = this.boardGen.generateGoalBoard();

        Board resultState = this.solve(this.startBoard);
        while (resultState.getParent() != null) {
            result.add(resultState.getMove());
            resultState = resultState.getParent();
        }
        System.out.println("I am here" );
        Collections.reverse(result);
        return result;
    }

    public Board solve(int[][] start) {
        this.goalBoard = this.boardGen.generateGoalBoard();
        Stack<Board> stack = new Stack<>();
        Board startState = new Board(start);
        stack.push(startState);
        Board goalState = new Board(this.goalBoard);

        while (!stack.isEmpty()) {
            Board node = stack.pop();

            if (node.getHashCode() == goalState.getHashCode()) {
                return node;
            }

            visited.add(node.getHashCode()); // Mark the current board configuration as visited

            for (Board neighbor : node.generateChild()) {
                if (!visited.contains(neighbor.getHashCode())) {
                    stack.push(neighbor);
                    neighbor.setParent(node);
                }
            }
        }

        return null; // No solution found
    }
}
