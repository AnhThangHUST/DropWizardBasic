package com.aj.userMongoJack.auth;
import com.aj.userMongoJack.domain.User;
import com.mongodb.DBCollection;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import com.aj.userMongoJack.service.UserMongoJackService;


import java.util.Map;
import java.util.Optional;
import java.util.Set;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.basic.BasicCredentials;
import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;

public class AppBasicAuthenticator implements Authenticator<String, User>{
    private UserMongoJackService mongoService;
    private JacksonDBCollection<User, String> coll;
    public AppBasicAuthenticator(DBCollection collection, UserMongoJackService mongoService){
        this.mongoService = mongoService;
        this.coll = JacksonDBCollection.wrap(collection, User.class, String.class);;
    }

    // Kiem tra user = token
    @Override
    public Optional<User> authenticate(String token) throws AuthenticationException {
        //check xem token co dung ko
        DBCursor<User> documents = mongoService.findByKey(coll,"token", token);
            // Document nay se chua 1 user duy nhat.
        if (documents.size() == 1){
            User user = documents.toArray().get(0);
            return Optional.of(documents.toArray().get(0));
        }
        return Optional.empty();
    }
}
