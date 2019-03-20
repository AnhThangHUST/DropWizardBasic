package com.aj.userMongoJack.resource;

import com.aj.userMongoJack.domain.User;
import com.aj.userMongoJack.service.UserMongoJackService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/usersMongoJack")
@Produces(MediaType.APPLICATION_JSON)
public class UserMongoJackResource {
    // mongoservice dung de sua vao database mongodb
    private UserMongoJackService mongoService;
    //this collection have to include User, map User into MongoObjectDocument
    private JacksonDBCollection<User, String> coll;

    public UserMongoJackResource(DBCollection collection, UserMongoJackService mongoService) {
        this.coll = JacksonDBCollection.wrap(collection, User.class, String.class);
        this.mongoService = mongoService;
    }

    @POST
    @Path("/createUser")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(@NotNull @Valid final User user) {

//        user.set_id(Long.toString(coll.count()+1));
//        System.out.println(user.get_id());
//        coll.insert(user);
        mongoService.insertOne(coll, user);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User created successfully");
        return Response.ok(response).build();
    }

    //Done
    @GET
    public Response getAllUSer(){
        DBCursor<User> documents = coll.find();
        return Response.ok(documents.toArray()).build();
    }
    // email o day se la unique value
    // Done
    @GET
    @Timed
    @Path("{_id}")
    //What this allows you to do is embed variable identification within the URIs of your resources.
    // ham nay se get user thong qua name
    public Response getUser(@PathParam("_id") final String id, final User user) {
        DBCursor<User> documents = mongoService.findByKey(coll, "_id", id);
        Map<String, String> response = new HashMap<>();
        if (documents.count() == 0 ){
            response.put("message", "There is no user has id like this");
            return Response.ok(response).build();
        }
        else {
            System.out.println("GO HERE");
            System.out.println(user.getToken());
            System.out.println(user.getAge());
            System.out.println(documents.toArray().get(0).getToken());
            if (!documents.toArray().get(0).getToken().equals(user.getToken())) {
                response.put("message", "Your token is not correct");
                return Response.ok(response).build();
            }
            return Response.ok(documents.toArray()).build();
        }
    }
//
    @PUT
    @Timed
    @Path("{_id}")
    public Response editUser(@PathParam("_id")String id, @NotNull @Valid final User user) {
        DBCursor<User> documents = mongoService.findByKey(coll, "_id",id);
        Map<String, String> response = new HashMap<>();
        if (documents.count() == 0){
            response.put("message", "There is no user has id like this");
        }
        else {
            String[] fields = {"_id", "name", "email", "password", "age"};
            mongoService.updateOneUser(coll, fields, user);
            // cai nay khong nham la tra ve client
            response.put("message", "User with id: " + user.get_id() + " updated successfully");
        }
        return Response.ok(response).build();
    }

//    // Done
    @DELETE
    @Timed
    @Path("{_id}")
    public Response deleteUser(@PathParam("_id") final String id) {
        DBCursor<User> documents = mongoService.findByKey(coll, "_id", id);
        Map<String, String> response = new HashMap<>();
        if (documents.count() == 0){
            response.put("message", "There is no user has id like this");
        }
        else {
            mongoService.deleteOne(coll, "_id", id);
            // cai nay de tra nguoc ve client
            response.put("message", "User with id: " + id + " deleted successfully");
        }
        return Response.ok(response).build();
    }

//    @POST
//    @Path("/test")
//    public Response testAPI(){
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "Test Done");
//        return Response.ok(response).build();
//    }
    @POST
    @Timed
    @Path("/login")
    public Response loginUser(final User user){
        String name = user.getName();
        String password = user.getPassword();
        Map<String, String> response = new HashMap<>();
        boolean validation = mongoService.validate(coll, name, password);
        if (validation){
            String token = mongoService.setToken(coll, name, password);
            response.put("message", "Dear "+ name +", you have login successfully. Here is your token: "+ token);
        }
        else{
            response.put("message", "Your password or Your user name is not correct");
        }
        return Response.ok(response).build();
    }
}
