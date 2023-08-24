package game;

import board.Board;
import board.BoardGen;

import java.util.*;
import java.util.PriorityQueue;

public class Solver {
	public int SIZE; //the dimension of the board
	public int[][] goalBoard; //goal board
    private int[][] startBoard;
	private BoardGen boardGen;

    public Solver(int[][] startBoard, int SIZE) {
        this.startBoard = startBoard;
		this.SIZE = SIZE;
		this.boardGen = new BoardGen(SIZE);
    }
	
	public ArrayList<String> returnResult() {
		ArrayList<String> result = new ArrayList<>();
		this.goalBoard = this.boardGen.generateGoalBoard();

		Board resultState = this.solve(this.startBoard);
		while (resultState.getParent() != null) {
			result.add(resultState.getMove());
			resultState = resultState.getParent();
		}

		Collections.reverse(result); // Reverse the order to match the expected result
		return result;
	}
	public Board solve(int[][] start) {
		this.goalBoard = this.boardGen.generateGoalBoard();
		HashMap<Integer,Board> closed = new HashMap<>();
		//Hashset is there to quick loop up element in the queue
		HashSet<Integer> inQueue = new HashSet<>();
		PriorityQueue<Board> q = new PriorityQueue<>();
		Board startState = new Board(start);
		q.add(startState);
		Board goalState = new Board(this.goalBoard);
		inQueue.add(startState.getHashCode());

		while (!q.isEmpty()) {
			Board node = q.remove();
			inQueue.remove(node.getHashCode());

			for (Board neighbor : node.generateChild()) {
				if (neighbor.getHashCode() == goalState.getHashCode()) {
					neighbor.setParent(node);
					return neighbor;
				}

				Board closedNeighbor = null;
				Board openNeighbor = null;
				if (inQueue.contains(neighbor.getHashCode())) {
					//Check if the neighbor is already in the queue
					openNeighbor = q.stream().filter(n -> n.equals(neighbor)).findFirst().get();
					if (openNeighbor.getDistanceFromStart() > neighbor.getDistanceFromStart()) {
						//If it is, check if the one in the queue have higher g value
						q.remove(openNeighbor);
						neighbor.setParent(node);
						q.add(neighbor);
					}
				}
				else {
					int neighborCode = neighbor.getHashCode();
					if (closed.containsKey(neighborCode)) {
						//Check if we already visited the state
						closedNeighbor = closed.get(neighbor.getHashCode());
						if(closedNeighbor.getDistanceFromStart() > neighbor.getDistanceFromStart()) {
							//If we already did, we don't visit it anymore
							closed.put(neighborCode, neighbor);
						}
					}

				}
				if (openNeighbor == null && closedNeighbor == null) {
					q.add(neighbor);
					neighbor.setParent(node);
					inQueue.add(neighbor.getHashCode());
				}

			}
			closed.put(node.getHashCode(), node); // this line is moving node to the closed set
		}
		return null;
	}
}
