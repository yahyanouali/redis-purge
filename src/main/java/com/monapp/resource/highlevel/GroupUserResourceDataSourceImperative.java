package com.monapp.resource.highlevel;

import com.monapp.model.User;
import com.monapp.redis.highlevel.GroupUserManagerDataSourceImperative;
import jakarta.ws.rs.*;

import java.util.Map;

@Path("/imperative")
public class GroupUserResourceDataSourceImperative {

    private final GroupUserManagerDataSourceImperative redisDataSourceImperative;

    public GroupUserResourceDataSourceImperative(GroupUserManagerDataSourceImperative redisDataSourceImperative) {
        this.redisDataSourceImperative = redisDataSourceImperative;
    }

    @POST
    @Path("/create/{groupId}")
    public boolean createUser(String groupId, User user) {
        return redisDataSourceImperative.createUser(groupId, user);
    }

    @GET
    @Path("/get/{groupId}/{userId}")
    public User getUser(String groupId, String userId) {
        return redisDataSourceImperative.getUser(groupId, userId);
    }

    @GET
    @Path("/get-all/{groupId}")
    public Map<String, User> getAllUsers(String groupId) {
        return redisDataSourceImperative.getAllUsers(groupId);
    }

    @DELETE
    @Path("/delete/{groupId}/{userId}")
    public int deleteUser(String groupId, String userId) {
        return redisDataSourceImperative.deleteUser(groupId, userId);
    }

    @DELETE
    @Path("/delete-all/{groupId}")
    public int deleteAllUsers(String groupId) {
        return redisDataSourceImperative.deleteAllUsers(groupId);
    }

    @GET
    @Path("/get-ttl/{groupId}")
    public Long getGroupTTL(String groupId) {
        return redisDataSourceImperative.getUserTTL(groupId);
    }

}
