package org.example.ranking;

public class ScoreRecord {
    private int ranking;
    private int score;
    private long time; //In seconds
    private int moves;
    
    public ScoreRecord(int score, long time, int moves) {
        this.score = score;
        this.time = time;
        this.moves = moves;
    }
    public ScoreRecord(long time, int moves, int boardSize) {
        this.time = time;
        this.moves = moves;
        this.score = this.calculateScore(boardSize);
    }
    public ScoreRecord(int ranking, int score, long time, int moves) {
        this.ranking = ranking;
        this.score=  score;
        this.time = time;
        this.moves = moves;
    }
    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public int getMoves() {
        return moves;
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
    
    public int calculateScore(int boardSize) {
        double timeWeight = 0.3;
        double moveWeight;
        if (boardSize == 4) {
            timeWeight = 0.2;
        } else if (boardSize == 5) {
            timeWeight = 0.1;
        }
        moveWeight = 1 - timeWeight;
        int score = (int) (timeWeight * time + moveWeight * moves);
        return score;
    }
}