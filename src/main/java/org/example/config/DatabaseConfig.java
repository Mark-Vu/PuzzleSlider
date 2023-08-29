package org.example.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static final String CONFIG_FILE = "./src/main/java/org/example/config/config.properties";
    
    public static MongoDatabase getDatabase() {
        String databaseUri = getDBUri();
        System.out.println(databaseUri);
        MongoClient client = MongoClients.create(databaseUri);
        MongoDatabase db = client.getDatabase("BroSlider");
        return db;
    }
    
    public static MongoCollection getUsersCollection() {
        MongoDatabase db = getDatabase();
        return db.getCollection("users");
    }

    private static String getDBUri() {
        String dbUri = null;
        try (FileInputStream input = new FileInputStream(CONFIG_FILE)) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);
                dbUri = prop.getProperty("mongodb.uri");
            } else {
                throw new FileNotFoundException("Property file '" + CONFIG_FILE + "' not found in the classpath");
            }
        } catch (IOException e) {
            System.out.println("Exception: " + e);
        }
        return dbUri;
    }
}
