package com.monapp.resource.lowlevel;

import com.monapp.model.User;
import com.monapp.redis.lowlevel.GroupUserManagerRedisAPIMutiny;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import java.util.Map;

@Path("/redisapi/mutiny")
public class GroupUserResourceRedisAPIMutiny {

    private final GroupUserManagerRedisAPIMutiny groupUserManagerRedisAPIMutiny;

    public GroupUserResourceRedisAPIMutiny(GroupUserManagerRedisAPIMutiny groupUserManagerRedisAPIMutiny) {
        this.groupUserManagerRedisAPIMutiny = groupUserManagerRedisAPIMutiny;
    }

    @POST
    @Path("/create/{groupId}")
    public Uni<Boolean> createUser(String groupId, User user) {
        return groupUserManagerRedisAPIMutiny.createUser(groupId, user);
    }

    @GET
    @Path("/get/{groupId}/{userId}")
    public Uni<User> getUser(String groupId, String userId) {
        return groupUserManagerRedisAPIMutiny.getUser(groupId, userId);
    }

    @GET
    @Path("/get-all/{groupId}")
    public Uni<Map<String, User>> getAllUsers(String groupId) {
        return groupUserManagerRedisAPIMutiny.getAllUsers(groupId);
    }

    @DELETE
    @Path("/delete/{groupId}/{userId}")
    public Uni<Integer> deleteUser(String groupId, String userId) {
        return groupUserManagerRedisAPIMutiny.deleteUser(groupId, userId);
    }

    @DELETE
    @Path("/delete-all/{groupId}")
    public Uni<Integer> deleteAllUsers(String groupId) {
        return groupUserManagerRedisAPIMutiny.deleteAllUsers(groupId);
    }

    @GET
    @Path("/get-ttl/{groupId}")
    public Uni<Long> getGroupTTL(String groupId) {
        return groupUserManagerRedisAPIMutiny.getUserTTL(groupId);
    }

}
