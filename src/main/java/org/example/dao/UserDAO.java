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
    /*
    Data Access Object for the users collection:
    Schema:
    {
  "name": "",
  "country": "",
  "scores": {
  	"3": {
  	  "ranking": 1,
  	  "score": 9,
  	  "moves":3,
  	  "time": {
  	  	"$numberLong":"8"
  	  }
  	}
    "5": {
      "ranking": 3,
      "score": 8,
      "moves": 8,
      "time": {
        "$numberLong": "8"
      }
    }
  }
}
     */
    private static final MongoCollection collection = DatabaseConfig.getUsersCollection();

    public static void insertUser(User user, int boardSize) {
        Document scoresDoc = new Document();
        for (Map.Entry<Integer, ScoreRecord> entry : user.getScores().entrySet()) {
            //Create a board score, default ranking 0 when insert user to db
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
        User user = new User(name, country, scores);
        return user;
    }

    public static void updateRankings(int boardSize) {
        /*
        Update rankings of all users
         */
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

    public static User getUserByRankingAndBoardSize(int ranking, int boardSize) {
        Document query = new Document("scores." + boardSize + ".ranking", ranking);
        Document result = (Document) collection.find(query).first();
        if (result != null) {
            return documentToUser(result);
        }
        return null; // User not found with the specified ranking
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
    public static List<User> getUsersByBoardSizeRanked(int boardSize) {
        List<User> users = new ArrayList<>();
        FindIterable<Document> userDocs = collection.find();

        PriorityQueue<User> userPriorityQueue = new PriorityQueue<>(new UserComparator(boardSize));

        for (Document doc : userDocs) {
            User user = documentToUser(doc);
            if (user.getScores().containsKey(boardSize)) {
                userPriorityQueue.add(user);
            }
        }

        int ranking = 1;
        while (!userPriorityQueue.isEmpty() && ranking <= 50) {
            User user = userPriorityQueue.poll();
            user.getScores().get(boardSize).setRanking(ranking);
            users.add(user);
            ranking++;
        }

        return users;
    }

    public static HashMap<Integer, List<User>> getUsersByAllBoardSizesRanked() {
        HashMap<Integer, List<User>> usersByBoardSize = new HashMap<>();

        for (int boardSize = 3; boardSize <= 6; boardSize++) {
            List<User> users = getUsersByBoardSizeRanked(boardSize);
            usersByBoardSize.put(boardSize, users);
        }

        return usersByBoardSize;
    }
}
