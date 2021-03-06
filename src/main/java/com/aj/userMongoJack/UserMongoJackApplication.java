package com.aj.userMongoJack;

//import com.aj.userMongoJack.resource.UserDeploymentResource;
//import com.aj.userMongoJack.service.MongoService;
import com.aj.userMongoJack.auth.AppBasicAuthenticator;
import com.aj.userMongoJack.auth.AppBasicAuthorizer;
import com.aj.userMongoJack.domain.User;
import com.aj.userMongoJack.resource.UserMongoJackResource;
import com.aj.userMongoJack.service.UserMongoJackService;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestFilter;

//slf4j Simple logging Facade for Java serve as a simple facade or abstraction for various logging framework

public class UserMongoJackApplication extends Application<UserMongoJackConfiguration> {
    private static final Logger logger = LoggerFactory.getLogger(UserMongoJackApplication.class);

    public static void main(String[] args) throws Exception {
        new UserMongoJackApplication().run("server", args[0]);
    }

    @Override
    public void initialize(Bootstrap<UserMongoJackConfiguration> b) {
    }

    @Override
    public void run(UserMongoJackConfiguration config, Environment env) throws Exception {
        MongoClient mongoClient = new MongoClient(config.getMongoHost(), config.getMongoPort());
//        MongoManaged mongoManaged = new MongoManaged(mongoClient);
//        env.lifecycle().manage(mongoManaged);
        DB db = mongoClient.getDB(config.getMongoDB());
        // Luc nao de get collection cung co dong nay
        DBCollection collection = db.getCollection(config.getCollectionName());
//        collection.createIndex(Indexes.ascending(config.getNameUniqueField()), new IndexOptions().unique(true));

        logger.info("Registering RESTful API resources");
        env.jersey().register(new UserMongoJackResource(collection, new UserMongoJackService()));
        env.jersey().register(new AuthDynamicFeature(
                new OAuthCredentialAuthFilter.Builder<User>()
                .setAuthenticator(new AppBasicAuthenticator(collection, new UserMongoJackService()))
                .setPrefix("Bearer")
                .setAuthorizer(new AppBasicAuthorizer())
                .buildAuthFilter()));
        env.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
        env.jersey().register(RolesAllowedDynamicFeature.class);
    }
}