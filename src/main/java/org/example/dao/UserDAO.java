package org.example.dao;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.config.DatabaseConfig;
import org.example.model.User;
import org.example.ranking.ScoreRecord;
import org.example.ranking.UserComparator;

import java.util.*;


public class UserDAO {
    private static final MongoCollection collection = DatabaseConfig.getUsersCollection();

    public static void insertUser(User user, int boardSize) {
        Document scoresDoc = new Document();
        for (Map.Entry<Integer, ScoreRecord> entry : user.getScores().entrySet()) {
            Document boardScoreDoc = new Document("ranking", 0)
                    .append("score", entry.getValue().getScore())
                    .append("moves", entry.getValue().getMoves())
                    .append("time", entry.getValue().getTime());
            scoresDoc.append(Integer.toString(entry.getKey()), boardScoreDoc);
        }
        Document doc = new Document("name", user.getName())
                            .append("country", user.getCountry())
                            .append("scores", scoresDoc);
        collection.insertOne(doc);
        updateRankings(boardSize);
    }


    private static User documentToUser(Document doc) {
        String name = doc.getString("name");
        String country = doc.getString("country");
        HashMap<Integer, ScoreRecord> scores = new HashMap<>();

        Document scoresDoc = (Document) doc.get("scores");
        for (String boardSize : scoresDoc.keySet()) {
            Document boardScoreDoc = (Document) scoresDoc.get(boardSize);
            int ranking = boardScoreDoc.getInteger("ranking");
            int score = boardScoreDoc.getInteger("score");
            long time = boardScoreDoc.getLong("time");
            int moves = boardScoreDoc.getInteger("moves");
            ScoreRecord record = new ScoreRecord(ranking, score, time, moves);
            scores.put(Integer.parseInt(boardSize), record);
        }

        return new User(name, country, scores);
    }

    public static void updateRankings(int boardSize) {
        PriorityQueue<User> userPriorityQueue = new PriorityQueue<>(new UserComparator(boardSize));
        FindIterable<Document> users = collection.find();
        for (Document doc : users) {
            User user = documentToUser(doc);
            if (user.getScores().containsKey(boardSize)) {
                userPriorityQueue.add(user);
            }
        }

        // Reassign rankings based on priority queue order
        int newRanking = 1;
        while (!userPriorityQueue.isEmpty()) {
            User user = userPriorityQueue.poll();
            user.getScores().get(boardSize).setRanking(newRanking++);
            // Update rankings of all users in db
            collection.updateOne(
                    new Document("name", user.getName()),
                    new Document("$set", new Document("scores." + boardSize + ".ranking", user.getScores().get(boardSize).getRanking()))
            );
        }
    }

    public static List<User> getTopRankedUsersByBoardSize(int boardSize) {
        List<User> topRankedUsers = new ArrayList<>();

        MongoCursor<Document> cursor = collection.find()
            .sort(new Document("scores." + boardSize + ".ranking", 1))
            .limit(50)
            .iterator();

        while (cursor.hasNext()) {
            Document doc = cursor.next();
            User user = documentToUser(doc);
            topRankedUsers.add(user);
        }

        return topRankedUsers;
    }

    public static User getUserByRankingAndBoardSize(int ranking, int boardSize) {
        Document query = new Document("scores." + boardSize + ".ranking", ranking);
        Document result = (Document) collection.find(query).first();
        if (result != null) {
            return documentToUser(result);
        }
        return null; // User not found with the specified ranking
    }

    public static int calculateRanking(int boardSize,
                                       int score,
                                       long time,
                                       int moves) {
        List<User> rankedUsers = getTopRankedUsersByBoardSize(boardSize);

        User newUser = new User("", "", new HashMap<>()); // Create a temporary user with empty values
        newUser.getScores().put(boardSize, new ScoreRecord(score, time, moves)); // Set the provided values

        rankedUsers.add(newUser);

        rankedUsers.sort(new UserComparator(boardSize));

        int userRanking = rankedUsers.indexOf(newUser) + 1;
        return userRanking;
    }

    public static boolean isBetterThanTop50(int boardSize, int score, long time, int moves) {
        User top50Player = getUserByRankingAndBoardSize(50, boardSize);

        if (top50Player == null) {
            return true; // If no player at ranking 50, input player is better
        }

        ScoreRecord top50Record = top50Player.getScores().get(boardSize);

        if (score > top50Record.getScore()) {
            return true; // Higher score is better
        } else if (score == top50Record.getScore()) {
            if (moves < top50Record.getMoves()) {
                return true; // Fewer moves are better
            } else if (moves == top50Record.getMoves()) {
                if (time < top50Record.getTime()) {
                    return true; // Less time is better
                }
            }
        }

        return false; // Input player is not better than the top 50 player
    }

}
