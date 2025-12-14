package com.monapp.resource.highlevel;

import com.monapp.model.User;
import com.monapp.redis.highlevel.GroupUserManagerDataSourceReactive;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Map;

@Path("/datasource/reactive")
@Tag(name = "3.2- DataSource Reactive and ReactiveRedisCommands (Quarkus)")
public class GroupUserResourceDataSourceReactive {

    private final GroupUserManagerDataSourceReactive redisDataSourceReactive;

    public GroupUserResourceDataSourceReactive(GroupUserManagerDataSourceReactive redisDataSourceReactive) {
        this.redisDataSourceReactive = redisDataSourceReactive;
    }

    @POST
    @Path("/create/{groupId}")
    public Uni<Boolean> createUser(String groupId, User user) {
        return redisDataSourceReactive.createUser(groupId, user);
    }

    @GET
    @Path("/get/{groupId}/{userId}")
    public Uni<User> getUser(String groupId, String userId) {
        return redisDataSourceReactive.getUser(groupId, userId);
    }

    @GET
    @Path("/get-all/{groupId}")
    public Uni<Map<String, User>> getAllUsers(String groupId) {
        return redisDataSourceReactive.getAllUsers(groupId);
    }

    @DELETE
    @Path("/delete/{groupId}/{userId}")
    public Uni<Integer> deleteUser(String groupId, String userId) {
        return redisDataSourceReactive.deleteUser(groupId, userId);
    }

    @DELETE
    @Path("/delete-all/{groupId}")
    public Uni<Integer> deleteAllUsers(String groupId) {
        return redisDataSourceReactive.deleteAllUsers(groupId);
    }

    @GET
    @Path("/get-ttl/{groupId}")
    public Uni<Long> getGroupTTL(String groupId) {
        return redisDataSourceReactive.getUserTTL(groupId);
    }

}
