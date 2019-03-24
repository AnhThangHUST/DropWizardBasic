package com.aj.userMongoJack.resource;

import com.aj.userMongoJack.domain.User;
import com.aj.userMongoJack.service.UserMongoJackService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.client.MongoCollection;
import io.dropwizard.auth.Auth;
import org.bson.Document;
import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

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

//    @RolesAllowed("User")
    // ham nay tam thoi chua can quan tam quyen
    @POST
    @Path("/createUser")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(@NotNull @Valid final User user) {
        if (user.getRoles() == null ) {
            Set<String> roles = new HashSet<>();
            roles.add("User");
            user.setRoles(roles);
        }
        mongoService.insertOne(coll, user);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User created successfully");
        return Response.ok(response).build();
    }

    //Done
    @RolesAllowed({"Admin"})
    @GET
    public Response getAllUSer(@Auth User user){
        DBCursor<User> documents = coll.find();
        return Response.ok(documents.toArray()).build();
    }
    // email o day se la unique value
    // Done

    @RolesAllowed({"User", "Admin"})
    @GET
    @Timed
    @Path("{_id}")
    //What this allows you to do is embed variable identification within the URIs of your resources.
    // ham nay se get user thong qua name
    // dung @HeaderParam de get dc token ra
    // phai co cai Auth de no lay dc user tu kia sang
    public Response getUser(@PathParam("_id") final String id, @Auth final User user) {
        Map<String, String> response = new HashMap<>();
        if (!user.get_id().equals(id) && !user.getRoles().contains("Admin")) {
            response.put("message", "you dont have the permission the get info from the other person ");
            return Response.ok(response).build();
        }
        DBCursor<User> documents = mongoService.findByKey(coll, "_id", id);
        return Response.ok(documents.toArray()).build();

    }
//
    @RolesAllowed({"User", "Admin"})
    @PUT
    @Timed
    @Path("{_id}")
    // user have the right to change the newinfo
    public Response editUser(@PathParam("_id")String id, @Auth final User user, @Valid User newinfo) {
        Map<String, String> response = new HashMap<>();
        if (!user.get_id().equals(id)) {
            response.put("message","You can only change your info even if you are the admin");
            return Response.ok(response).build();
        }

        mongoService.updateOneUser(coll, id, newinfo);
//        // cai nay khong nham la tra ve client
        response.put("message", "Person with id: " + user.get_id() + " updated successfully");
        return Response.ok(response).build();
    }

    @RolesAllowed({"User","Admin"})
//    // Done
    @DELETE
    @Timed
    @Path("{_id}")
    public Response deleteUser(@PathParam("_id") final String id, @Auth User user) {
        Map<String, String> response = new HashMap<>();
        if (user.getRoles().contains("Admin")){
            DBCursor<User> documents = mongoService.findByKey(coll, "_id", id);
            if (documents.count() == 0){
                response.put("message", "Your are admin but no user has id like this");
            }
            else {
                mongoService.deleteOne(coll, "_id", id);
                response.put("message", "Admin has deleted the User with id: " + id + " successfully");
            }
        }else {
            if (!user.get_id().equals(id)) {
                response.put("message", "you are user and you only have the permission to delete your info.");
            } else {
                mongoService.deleteOne(coll, "_id", id);
                response.put("message", "User no longer need this account: "+id );
            }
        }
        return Response.ok(response).build();
    }

    // ham nay tra lai token la ok roi - Done
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
