package com.monapp.resource.highlevel;

import com.monapp.model.User;
import com.monapp.redis.highlevel.GroupUserManagerImperative;
import jakarta.ws.rs.*;

import java.util.Map;

@Path("/imperative")
public class GroupUserResourceImperative {

    private final GroupUserManagerImperative groupUserManagerImperative;

    public GroupUserResourceImperative(GroupUserManagerImperative groupUserManagerImperative) {
        this.groupUserManagerImperative = groupUserManagerImperative;
    }

    @POST
    @Path("/create/{groupId}")
    public boolean createUser(String groupId, User user) {
        return groupUserManagerImperative.createUser(groupId, user);
    }

    @GET
    @Path("/get/{groupId}/{userId}")
    public User getUser(String groupId, String userId) {
        return groupUserManagerImperative.getUser(groupId, userId);
    }

    @GET
    @Path("/get-all/{groupId}")
    public Map<String, User> getAllUsers(String groupId) {
        return groupUserManagerImperative.getAllUsers(groupId);
    }

    @DELETE
    @Path("/delete/{groupId}/{userId}")
    public int deleteUser(String groupId, String userId) {
        return groupUserManagerImperative.deleteUser(groupId, userId);
    }

    @DELETE
    @Path("/delete-all/{groupId}")
    public int deleteAllUsers(String groupId) {
        return groupUserManagerImperative.deleteAllUsers(groupId);
    }

    @GET
    @Path("/get-ttl/{groupId}")
    public Long getGroupTTL(String groupId) {
        return groupUserManagerImperative.getUserTTL(groupId);
    }

}
