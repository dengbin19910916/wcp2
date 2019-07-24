package io.xxx.wcp.provider;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class Main {

    public static void main(String[] args) {

        // Since 2.10.0, uses MongoClient
        MongoClient mongo = new MongoClient("localhost", 27017);

        // if database doesn't exists, MongoDB will create it for you
        DB db = mongo.getDB("www");

        // if collection doesn't exists, MongoDB will create it for you
        DBCollection table = db.getCollection("person");

        // create a document to store key and value
        BasicDBObject document;

        for (int i = 0; i < 100_000_000; i++) {
            document = new BasicDBObject();
            document.put("name", "mkyong" + i);
            document.put("age", 30);
            document.put("sex", "f");
            table.insert(document);
        }


        System.out.println("Done");
    }
}
