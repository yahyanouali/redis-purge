package com.monapp.redis;

import com.monapp.model.User;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.hash.HashCommands;
import io.quarkus.redis.datasource.keys.KeyCommands;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;

@ApplicationScoped
public class GroupUserManager {

    public static final String GROUP_USERS_KEY = "groups:%s";

    private final HashCommands<String, String, User> hashCommands;
    private final KeyCommands<String> keyCommands;

    public GroupUserManager(RedisDataSource ds) {
        this.hashCommands = ds.hash(String.class, String.class, User.class);
        this.keyCommands = ds.key();
    }

    public void createUser(String groupId, User user) {
        hashCommands.hset(GROUP_USERS_KEY.formatted(groupId), user.id(), user);
        keyCommands.expire(GROUP_USERS_KEY.formatted(groupId), 36000);
    }

    public void deleteUser(String groupId, String userId) {
        hashCommands.hdel(GROUP_USERS_KEY.formatted(groupId), userId);
    }

    public User getUser(String groupId, String userId) {
        return hashCommands.hget(GROUP_USERS_KEY.formatted(groupId), userId);
    }

    public Map<String, User> getAllUsers(String groupId) {
        return hashCommands.hgetall(GROUP_USERS_KEY.formatted(groupId));
    }

    public Long getUserTTL(String groupId, String userId) {
        return keyCommands.ttl(GROUP_USERS_KEY.formatted(groupId));
    }

    public void deleteAllUsers(String groupId) {
        keyCommands.del(GROUP_USERS_KEY.formatted(groupId));
    }
}
