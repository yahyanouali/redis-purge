package com.monapp;

import com.monapp.model.User;
import com.monapp.redis.GroupUserManager;
import jakarta.ws.rs.*;

import java.util.Map;

@Path("/hello")
public class GroupUserResource {

    private final GroupUserManager groupUserManager;

    public GroupUserResource(GroupUserManager groupUserManager) {
        this.groupUserManager = groupUserManager;
    }

    @POST
    @Path("/create/{groupId}")
    public String createUser(String groupId, User user) {
        groupUserManager.createUser(groupId, user);
        return "User created";
    }

    @GET
    @Path("/get/{groupId}/{userId}")
    public User getUser(String groupId, String userId) {
        return groupUserManager.getUser(groupId, userId);
    }

    @GET
    @Path("/get-all/{groupId}")
    public Map<String, User> getAllUsers(String groupId) {
        return groupUserManager.getAllUsers(groupId);
    }

    @DELETE
    @Path("/delete/{groupId}/{userId}")
    public String deleteUser(String groupId, String userId) {
        groupUserManager.deleteUser(groupId, userId);
        return "User deleted";
    }

    @DELETE
    @Path("/delete-all/{groupId}")
    public String deleteAllUsers(String groupId) {
        groupUserManager.deleteAllUsers(groupId);
        return "All users deleted";
    }

    @GET
    @Path("/get-ttl/{groupId}/{userId}")
    public Long getGroupTTL(String groupId, String userId) {
        return groupUserManager.getUserTTL(groupId, userId);
    }

}
