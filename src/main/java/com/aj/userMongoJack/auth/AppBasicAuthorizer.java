package com.aj.userMongoJack.auth;

import com.aj.userMongoJack.domain.User;
import io.dropwizard.auth.Authorizer;

public class AppBasicAuthorizer implements Authorizer<User> {
    @Override
    // Kiem tra user co role nhu minh muon khong
    // @RoleAllow se la role trong nay, User thi lay tu phan authentication sang
    public boolean authorize(User user, String role) {
        //System.out.println("USER ROLE: " + user.getRoles());
        //System.out.println(role);
        return user.getRoles() != null && user.getRoles().contains(role);
    }
}
