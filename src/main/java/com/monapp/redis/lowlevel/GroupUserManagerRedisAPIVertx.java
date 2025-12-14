package com.monapp.redis.lowlevel;

import com.monapp.model.User;
import com.monapp.redis.lowlevel.codec.JsonCodec;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.Response;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class GroupUserManagerRedisAPIVertx {

    public static final String GROUP_USERS_KEY = "groups:%s";
    private static final long DEFAULT_TTL = 36000;

    private final RedisAPI redisAPI;
    private final JsonCodec jsonCodec;

    public GroupUserManagerRedisAPIVertx(RedisAPI redisAPI, JsonCodec jsonCodec) {
        this.redisAPI = redisAPI;
        this.jsonCodec = jsonCodec;
    }

    /**
     * Crée un utilisateur dans le groupe (hash Redis)
     */
    public Future<@Nullable Boolean> createUser(String groupId, User user) {
        String key = GROUP_USERS_KEY.formatted(groupId);
        String userJson = jsonCodec.encode(user);

        return redisAPI.hset(List.of(key, user.id(), userJson))
                .andThen(newUser -> redisAPI.expire(List.of(key, String.valueOf(DEFAULT_TTL))))
                .map(Response::toBoolean);
    }

    /**
     * Supprime un utilisateur du groupe
     */
    public Future<@Nullable Integer> deleteUser(String groupId, String userId) {
        String key = GROUP_USERS_KEY.formatted(groupId);

        return redisAPI.hdel(List.of(key, userId))
                .map(Response::toInteger);
    }

    /**
     * Récupère un utilisateur spécifique
     */
    public Future<User> getUser(String groupId, String userId) {
        String key = GROUP_USERS_KEY.formatted(groupId);

        return redisAPI.hget(key, userId)
                .map(response -> {
                    if (response == null) {
                        return null;
                    }
                    return jsonCodec.decode(response.toString(), User.class);
                });
    }

    /**
     * Récupère tous les utilisateurs du groupe
     */
    public Future<Map<String, User>> getAllUsers(String groupId) {
        String key = GROUP_USERS_KEY.formatted(groupId);

        return redisAPI.hgetall(key)
                .map(response -> {
                    Map<String, User> users = new HashMap<>();

                    if (response == null) {
                        return users;
                    }

                    for (String responseKey : response.getKeys()) {
                        User user = jsonCodec.decode(response.get(responseKey).toString(), User.class);;
                        users.put(user.id(), user);
                    }

                    return users;
                });
    }

    /**
     * Récupère le TTL de la clé du groupe
     */
    public Future<@Nullable Long> getUserTTL(String groupId) {
        String key = GROUP_USERS_KEY.formatted(groupId);

        return redisAPI.ttl(key)
                .map(Response::toLong);
    }

    /**
     * Supprime tous les utilisateurs du groupe (supprime la clé)
     */
    public Future<@Nullable Integer> deleteAllUsers(String groupId) {
        String key = GROUP_USERS_KEY.formatted(groupId);

        return redisAPI.del(List.of(key))
                .map(Response::toInteger);
    }

}
