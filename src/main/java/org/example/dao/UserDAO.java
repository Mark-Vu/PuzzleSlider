package org.example.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.example.config.DatabaseConfig;
import org.example.model.User;


public class UserDAO {
    private static final MongoCollection collection = DatabaseConfig.getUsersCollection();
    
    public static void insertUser(User user) {
        Document doc = new Document("name", user.getName())
                            .append("country", user.getCountry())
                            .append("ranking", user.getRanking());
        collection.insertOne(doc);
    }
}