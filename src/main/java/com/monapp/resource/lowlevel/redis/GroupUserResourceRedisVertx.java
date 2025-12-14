package com.monapp.resource.lowlevel.redis;

import com.monapp.model.User;
import com.monapp.redis.lowlevel.redis.GroupUserManagerRedisVertx;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import java.util.Map;
import java.util.concurrent.CompletionStage;

@Path("/redis/vertx")
public class GroupUserResourceRedisVertx {

    private final GroupUserManagerRedisVertx redisVertx;

    public GroupUserResourceRedisVertx(GroupUserManagerRedisVertx redisVertx) {
        this.redisVertx = redisVertx;
    }

    @POST
    @Path("/create/{groupId}")
    public CompletionStage<Boolean> createUser(String groupId, User user) {
        return redisVertx.createUser(groupId, user).toCompletionStage();
    }

    @GET
    @Path("/get/{groupId}/{userId}")
    public CompletionStage<User> getUser(String groupId, String userId) {
        return redisVertx.getUser(groupId, userId).toCompletionStage();
    }

    @GET
    @Path("/get-all/{groupId}")
    public CompletionStage<Map<String, User>> getAllUsers(String groupId) {
        return redisVertx.getAllUsers(groupId).toCompletionStage();
    }

    @DELETE
    @Path("/delete/{groupId}/{userId}")
    public CompletionStage<Integer> deleteUser(String groupId, String userId) {
        return redisVertx.deleteUser(groupId, userId).toCompletionStage();
    }

    @DELETE
    @Path("/delete-all/{groupId}")
    public CompletionStage<Integer> deleteAllUsers(String groupId) {
        return redisVertx.deleteAllUsers(groupId).toCompletionStage();
    }

    @GET
    @Path("/get-ttl/{groupId}")
    public CompletionStage<Long> getGroupTTL(String groupId) {
        return redisVertx.getUserTTL(groupId).toCompletionStage();
    }

}