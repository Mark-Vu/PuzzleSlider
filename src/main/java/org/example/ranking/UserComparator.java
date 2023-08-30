package org.example.ranking;
import org.example.model.User;

import java.util.Comparator;

public class UserComparator implements Comparator<User> {
    private final Integer boardSize;

    public UserComparator(Integer boardSize) {
        this.boardSize = boardSize;
    }

    @Override
    public int compare(User user1, User user2) {
        ScoreRecord record1 = user1.getScores().get(boardSize);
        ScoreRecord record2 = user2.getScores().get(boardSize);

        if (record1.getScore() != record2.getScore()) {
            return Integer.compare(record1.getScore(), record2.getScore()); // Higher score is better
        } else if (record1.getMoves() != record2.getMoves()) {
            return Integer.compare(record2.getMoves(), record1.getMoves()); // Fewer moves are better
        } else {
            return Long.compare(record2.getTime(), record1.getTime()); // Less time is better
        }
    }
}
