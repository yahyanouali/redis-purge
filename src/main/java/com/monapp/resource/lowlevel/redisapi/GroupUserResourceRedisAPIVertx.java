package com.monapp.resource.lowlevel.redisapi;

import com.monapp.model.User;
import com.monapp.redis.lowlevel.redisapi.GroupUserManagerRedisAPIVertx;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import java.util.Map;
import java.util.concurrent.CompletionStage;

@Path("/redisapi/vertx")
public class GroupUserResourceRedisAPIVertx {

    private final GroupUserManagerRedisAPIVertx redisAPIVertx;

    public GroupUserResourceRedisAPIVertx(GroupUserManagerRedisAPIVertx redisAPIVertx) {
        this.redisAPIVertx = redisAPIVertx;
    }

    @POST
    @Path("/create/{groupId}")
    public CompletionStage<Boolean> createUser(String groupId, User user) {
        return redisAPIVertx.createUser(groupId, user).toCompletionStage();
    }

    @GET
    @Path("/get/{groupId}/{userId}")
    public CompletionStage<User> getUser(String groupId, String userId) {
        return redisAPIVertx.getUser(groupId, userId).toCompletionStage();
    }

    @GET
    @Path("/get-all/{groupId}")
    public CompletionStage<Map<String, User>> getAllUsers(String groupId) {
        return redisAPIVertx.getAllUsers(groupId).toCompletionStage();
    }

    @DELETE
    @Path("/delete/{groupId}/{userId}")
    public CompletionStage<Integer> deleteUser(String groupId, String userId) {
        return redisAPIVertx.deleteUser(groupId, userId).toCompletionStage();
    }

    @DELETE
    @Path("/delete-all/{groupId}")
    public CompletionStage<Integer> deleteAllUsers(String groupId) {
        return redisAPIVertx.deleteAllUsers(groupId).toCompletionStage();
    }

    @GET
    @Path("/get-ttl/{groupId}")
    public CompletionStage<Long> getGroupTTL(String groupId) {
        return redisAPIVertx.getUserTTL(groupId).toCompletionStage();
    }

}