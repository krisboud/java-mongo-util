package com.krisboudreau.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * A Mongo utility class that essentially wraps the mongo-java-driver library in favor of abstraction of data.
 * Created by krisboud on 2016-09-14.
 */
public class MongoDataUtil {

    private String host = "localhost";
    private int port = 27017;
    private String dbName;

    public MongoDataUtil(String dbName) {
        this.dbName = dbName;
    }

    public MongoDataUtil(String host, int port, String dbName) {
        this.host = host;
        this.port = port;
        this.dbName = dbName;
    }

    /**
     * Get the DB Collection by type.
     * @param type The java.lang.Class of the object to get collection for.
     * @return com.mongodb.DBCollection The Mongo collection.
     */
    public DBCollection getCollection(Class type) {

        MongoClient mongo;
        DB db;

        try {
            mongo = new MongoClient(host, port);
            db = mongo.getDB(dbName);
        } catch (UnknownHostException e) {
            throw new MongoException("MongoDataUtil: Unable to connect to Mongo host. " + e.getMessage());
        }

        if (db == null) {
            throw new MongoException("MongoDataUtil: Unable to connect to Mongo Database.");
        }

        return db.getCollection(type.getSimpleName());
    }

    /**
     * Get the object representation of from a Mongo Collection.
     * @param type The java.lang.Class to return.
     * @param where A HasMap of key value pairs to use as parameters for the mongo query.
     * @return A Object of type specified
     */
    public Object getObjectFromCollection(Class type, HashMap where) {

        DBCollection collection = getCollection(type);
        BasicDBObject whereQuery = new BasicDBObject();

        for ( Object ent : where.entrySet()) {
            Map.Entry entry = (Map.Entry) ent;
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            whereQuery.put(key, value);
        }

        DBCursor cursor = collection.find(whereQuery);
        DBObject obj = cursor.next();

        String str = obj.toString();
        Gson gson = new Gson();

        return gson.fromJson(str, type);
    }

    /**
     * Insert Object into a Mongo Database.
     * @param object Object to be put in the Mongo database.
     * @throws IOException Thrown if unable to convert Object to Json.
     */
    public void insertObjectInMongo(Object object) throws IOException {

        DBCollection collection = this.getCollection(object.getClass());

        String json = convertObjectToJson(object);

        DBObject dbObject = (DBObject) JSON.parse(json);

        collection.insert(dbObject);

    }

    /**
     * Converts Object to Json format.
     * @param object Object to convertt o Json
     * @return Returns Json representation of object.
     * @throws IOException Thrown if unable to convert Object to Json.
     */
    public String convertObjectToJson(Object object) throws IOException {
        String formattedJson = null;
        Gson gson = new Gson();
        JsonNode tree;
        ObjectMapper mapper = new ObjectMapper();

        formattedJson = gson.toJson(object, object.getClass());
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        try {
            tree = mapper.readTree(formattedJson);
            formattedJson = mapper.writeValueAsString(tree);
        } catch (IOException e) {
            throw new IOException("Unable to read Json tree: " + e.getMessage());
        }

        return formattedJson;
    }

}
