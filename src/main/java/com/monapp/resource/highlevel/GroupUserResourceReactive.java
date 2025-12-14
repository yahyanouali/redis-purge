package com.monapp.resource.highlevel;

import com.monapp.model.User;
import com.monapp.redis.highlevel.GroupUserManagerReactive;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import java.util.Map;

@Path("/reactive")
public class GroupUserResourceReactive {

    private final GroupUserManagerReactive groupUserManagerReactive;

    public GroupUserResourceReactive(GroupUserManagerReactive groupUserManagerReactive) {
        this.groupUserManagerReactive = groupUserManagerReactive;
    }

    @POST
    @Path("/create/{groupId}")
    public Uni<Boolean> createUser(String groupId, User user) {
        return groupUserManagerReactive.createUser(groupId, user);
    }

    @GET
    @Path("/get/{groupId}/{userId}")
    public Uni<User> getUser(String groupId, String userId) {
        return groupUserManagerReactive.getUser(groupId, userId);
    }

    @GET
    @Path("/get-all/{groupId}")
    public Uni<Map<String, User>> getAllUsers(String groupId) {
        return groupUserManagerReactive.getAllUsers(groupId);
    }

    @DELETE
    @Path("/delete/{groupId}/{userId}")
    public Uni<Integer> deleteUser(String groupId, String userId) {
        return groupUserManagerReactive.deleteUser(groupId, userId);
    }

    @DELETE
    @Path("/delete-all/{groupId}")
    public Uni<Integer> deleteAllUsers(String groupId) {
        return groupUserManagerReactive.deleteAllUsers(groupId);
    }

    @GET
    @Path("/get-ttl/{groupId}")
    public Uni<Long> getGroupTTL(String groupId) {
        return groupUserManagerReactive.getUserTTL(groupId);
    }

}
