package com.monapp.redis.highlevel;

import com.monapp.model.User;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.hash.ReactiveHashCommands;
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;

@ApplicationScoped
public class GroupUserManagerDataSourceReactive {

    public static final String GROUP_USERS_KEY = "groups:%s";

    private final ReactiveHashCommands<String, String, User> hashCommands;
    private final ReactiveKeyCommands<String> keyCommands;

    public GroupUserManagerDataSourceReactive(ReactiveRedisDataSource ds) {
        this.hashCommands = ds.hash(String.class, String.class, User.class);
        this.keyCommands = ds.key();
    }

    public Uni<Boolean> createUser(String groupId, User user) {
        return hashCommands.hset(GROUP_USERS_KEY.formatted(groupId), user.id(), user)
                .chain(() ->  keyCommands.expire(GROUP_USERS_KEY.formatted(groupId), 36000));
    }

    public Uni<Integer> deleteUser(String groupId, String userId) {
        return hashCommands.hdel(GROUP_USERS_KEY.formatted(groupId), userId);
    }

    public Uni<User> getUser(String groupId, String userId) {
        return hashCommands.hget(GROUP_USERS_KEY.formatted(groupId), userId);
    }

    public Uni<Map<String, User>> getAllUsers(String groupId) {
        return hashCommands.hgetall(GROUP_USERS_KEY.formatted(groupId));
    }

    public Uni<Long> getUserTTL(String groupId) {
        return keyCommands.ttl(GROUP_USERS_KEY.formatted(groupId));
    }

    public Uni<Integer> deleteAllUsers(String groupId) {
        return keyCommands.del(GROUP_USERS_KEY.formatted(groupId));
    }

}
