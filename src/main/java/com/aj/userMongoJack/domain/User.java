package com.aj.userMongoJack.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.mongojack.ObjectId;

import java.security.Principal;
import java.util.Collection;
import java.util.Set;

public class User implements Principal {
    private String id;
    private String name;
    private String email;
    private String password;
    private int age;
    private String token;
    private Set<String> roles;
//    private Timer latest_login;

    // constructor nay de cho phan code sau khi create roi
//    public User() {
//    }

    // constructor nay de cho phan code lan dau create user
    //Constructor duong nhu ko co y nghia
//    public User(String name, String email, String password, int age) {
//        this.name = name;
//        this.email = email;
//        this.password = password;
//        this.age = age;
////        this.roles.add(role);
//    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    //     Framework jersey se tu map 1 file json sang object, vay nen ta can ham
    @ObjectId
    @JsonProperty("_id")
    public String get_id() {
        return this.id;
    }

    public String getPassword() {
        return this.password;
    }

    public int getAge() {
        return this.age;
    }
    @ObjectId
    @JsonProperty("_id")
    public void set_id(String id) {
        this.id = id;
    }

    // Khi ignore field nay, thi ko the get ra tu file json. Tuy nhien khi dat vao json property thi no lai hieu.
    @JsonIgnore
    @JsonProperty("token")
    public String getToken() {
        return token;
    }
    public void setToken(String token){
        this.token = token;
    }

    @JsonProperty("role")
    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles){
        this.roles = roles;
    }
}