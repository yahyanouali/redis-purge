package com.monapp.redis.highlevel;

import com.monapp.model.User;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.hash.HashCommands;
import io.quarkus.redis.datasource.keys.KeyCommands;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;

@ApplicationScoped
public class GroupUserManagerDataSourceImperative {

    public static final String GROUP_USERS_KEY = "groups:%s";

    private final HashCommands<String, String, User> hashCommands;
    private final KeyCommands<String> keyCommands;

    public GroupUserManagerDataSourceImperative(RedisDataSource ds) {
        this.hashCommands = ds.hash(String.class, String.class, User.class);
        this.keyCommands = ds.key();
    }

    public boolean createUser(String groupId, User user) {
        boolean isSuccess = hashCommands.hset(GROUP_USERS_KEY.formatted(groupId), user.id(), user);
        boolean expirationSuccess = keyCommands.expire(GROUP_USERS_KEY.formatted(groupId), 36000);

        return isSuccess && expirationSuccess;
    }

    public int deleteUser(String groupId, String userId) {
        return hashCommands.hdel(GROUP_USERS_KEY.formatted(groupId), userId);
    }

    public User getUser(String groupId, String userId) {
        return hashCommands.hget(GROUP_USERS_KEY.formatted(groupId), userId);
    }

    public Map<String, User> getAllUsers(String groupId) {
        return hashCommands.hgetall(GROUP_USERS_KEY.formatted(groupId));
    }

    public Long getUserTTL(String groupId) {
        return keyCommands.ttl(GROUP_USERS_KEY.formatted(groupId));
    }
   
    public int deleteAllUsers(String groupId) {
        return keyCommands.del(GROUP_USERS_KEY.formatted(groupId));
    }
}
