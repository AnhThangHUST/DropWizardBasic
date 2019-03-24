package com.aj.userMongoJack.service;

import com.aj.userMongoJack.domain.User;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import io.dropwizard.jackson.Jackson;
import org.bson.Document;
import org.bson.codecs.BsonObjectIdCodec;
import org.bson.conversions.Bson;
import java.sql.Timestamp;

import org.bson.types.ObjectId;
import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class UserMongoJackService {
    public void insertOne(JacksonDBCollection<User, String> collection, User user) {
        collection.insert(user);
    }

    // find nay se co tac dung tra ve ham cho ham get, hoac post, hoac ca create
    // find co the find theo value la name hoac email
    public DBCursor<User> findByKey(JacksonDBCollection<User, String> collection, String key, String value) {
        if (key.equals("_id"))
            return collection.find(new BasicDBObject(key, new ObjectId(value))); // cai BasicDBObject nay chinh la 1 cai de query
        return collection.find(new BasicDBObject(key, value)); // cai BasicDBObject nay chinh la 1 cai de query
    }

    //
//    // API o tren mang: updateOne(Bson filter, Bson update): Update a single document in the collection according to the specified arguments
//    // Array List chua: key1 :name, key2: email, key3: password, key4: age
    public void updateOneUser(JacksonDBCollection<User, String> collection, String id, User newinfo) {
//        collection.updateOne(new Document(key1, user.getName()), new Document("$set", new Document(key2, user.getEmail())));

        //ham nay khong update?
//        DBCursor<User> documents = this.findByKey(collection, "_id",id);
        DBObject filter = new BasicDBObject("_id", new ObjectId(id));
        BasicDBObject query = new BasicDBObject();
        if (newinfo.getName() != null) query.append("$set", new BasicDBObject().append("name", newinfo.getName()));
        if (newinfo.getPassword() != null)
            query.append("$set", new BasicDBObject().append("password", newinfo.getPassword()));
        if (newinfo.getAge() != 0) query.append("$set", new BasicDBObject().append("age", newinfo.getAge()));
        if (newinfo.getEmail() != null) query.append("$set", new BasicDBObject().append("email", newinfo.getEmail()));
        if (newinfo.getRoles() != null) query.append("$set", new BasicDBObject().append("role", newinfo.getRoles()));
        collection.update(filter, query);
    }

    //
    public void deleteOne(JacksonDBCollection<User, String> collection, String key, String value) {
        if (key.equals("_id"))
            collection.remove(new BasicDBObject(key, new ObjectId(value)));
        else
            collection.remove(new BasicDBObject(key, value));
    }

    //validate and setToken
    public boolean validate(JacksonDBCollection<User, String> collection, String user_name, String user_password) {
        BasicDBObject filter = new BasicDBObject();
        filter.put("name", user_name);
        filter.put("password", user_password);
        DBCursor a = collection.find(filter);
        if (a.size() == 0) return false;
        return true;
    }

    // set Token and return Token
    public String setToken(JacksonDBCollection<User, String> collection, String user_name, String user_password) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String token = user_name + timestamp.getTime();
        DBObject query = new BasicDBObject("$set", new BasicDBObject("token", token));
        BasicDBObject filter = new BasicDBObject();
        filter.put("name", user_name);
        filter.put("password", user_password);
        collection.update(filter, query);
        return token;
    }
}
