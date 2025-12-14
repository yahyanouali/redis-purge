package com.monapp.resource.lowlevel.redis;

import com.monapp.model.User;
import com.monapp.redis.lowlevel.redis.GroupUserManagerRedisMutiny;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Map;

@Path("/redis/mutiny")
@Tag(name = "1.2- Redis Mutiny (Quarkus)")
public class GroupUserResourceRedisMutiny {

    private final GroupUserManagerRedisMutiny redisMutiny;

    public GroupUserResourceRedisMutiny(GroupUserManagerRedisMutiny redisMutiny) {
        this.redisMutiny = redisMutiny;
    }

    @POST
    @Path("/create/{groupId}")
    public Uni<Boolean> createUser(String groupId, User user) {
        return redisMutiny.createUser(groupId, user);
    }

    @GET
    @Path("/get/{groupId}/{userId}")
    public Uni<User> getUser(String groupId, String userId) {
        return redisMutiny.getUser(groupId, userId);
    }

    @GET
    @Path("/get-all/{groupId}")
    public Uni<Map<String, User>> getAllUsers(String groupId) {
        return redisMutiny.getAllUsers(groupId);
    }

    @DELETE
    @Path("/delete/{groupId}/{userId}")
    public Uni<Integer> deleteUser(String groupId, String userId) {
        return redisMutiny.deleteUser(groupId, userId);
    }

    @DELETE
    @Path("/delete-all/{groupId}")
    public Uni<Integer> deleteAllUsers(String groupId) {
        return redisMutiny.deleteAllUsers(groupId);
    }

    @GET
    @Path("/get-ttl/{groupId}")
    public Uni<Long> getGroupTTL(String groupId) {
        return redisMutiny.getUserTTL(groupId);
    }

}
