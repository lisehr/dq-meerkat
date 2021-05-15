package dqm.jku.dqmeerkat.demos.architecture.dbconnections;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class TestMongoDB {

    public static void main(String args[]) {
        try {
            String DBUrl = "localhost";
            int DBPort = 27017;

            String DBUser = "admin";
            String DBPass = "adminpw";

            String DBName = "admin";
            String CollectionName = "system.users";

            // no auth
            //ConnectionString cs = new ConnectionString("mongodb://" + DBUrl + ":" + DBPort);
            ConnectionString cs = new ConnectionString("mongodb://" + DBUser + ":" + DBPass + "@" + DBUrl + ":" + DBPort);
            MongoClient mongoClient = MongoClients.create(cs);
            MongoDatabase database = mongoClient.getDatabase(DBName);
            MongoCollection<Document> collection = database.getCollection(CollectionName);

            System.out.println(collection.find().first().toJson());

            mongoClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
