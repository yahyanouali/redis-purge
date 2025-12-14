package com.monapp.redis.lowlevel.redis;

import com.monapp.model.User;
import com.monapp.redis.lowlevel.codec.JsonCodec;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import io.vertx.redis.client.Command;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.Request;
import io.vertx.redis.client.Response;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class GroupUserManagerRedisVertx {

    public static final String GROUP_USERS_KEY = "groups:%s";
    private static final long DEFAULT_TTL = 36000;

    private final Redis redis;
    private final JsonCodec jsonCodec;

    public GroupUserManagerRedisVertx(Redis redis, JsonCodec jsonCodec) {
        this.redis = redis;
        this.jsonCodec = jsonCodec;
    }

    /**
     * Crée un utilisateur dans le groupe (hash Redis)
     */
    public Future<@Nullable Boolean> createUser(String groupId, User user) {
        String key = GROUP_USERS_KEY.formatted(groupId);
        String userJson = jsonCodec.encode(user);

        return redis.connect()
                .compose(conn -> {
                    Future<Response> hsetFuture = conn.send(Request.cmd(Command.HSET, key, user.id(), userJson));
                    Future<Response> expireFuture = hsetFuture
                            .andThen(newUser -> conn.send(Request.cmd(Command.EXPIRE, key, String.valueOf(DEFAULT_TTL))));

                    return expireFuture
                            .map(Response::toBoolean)
                            .onComplete(ar -> conn.close());
                });
    }

    /**
     * Supprime un utilisateur du groupe
     */
    public Future<@Nullable Integer> deleteUser(String groupId, String userId) {
        String key = GROUP_USERS_KEY.formatted(groupId);

        return redis.connect()
                .compose(conn -> conn.send(Request.cmd(Command.HDEL, key, userId))
                        .map(Response::toInteger)
                        .onComplete(ar -> conn.close())
                );
    }

    /**
     * Récupère un utilisateur spécifique
     */
    public Future<User> getUser(String groupId, String userId) {
        String key = GROUP_USERS_KEY.formatted(groupId);

        return redis.connect()
                .compose(conn -> conn.send(Request.cmd(Command.HGET, key, userId))
                        .map(response -> {
                            if (response == null) {
                                return null;
                            }
                            return jsonCodec.decode(response.toString(), User.class);
                        })
                        .onComplete(ar -> conn.close())
                );
    }

    /**
     * Récupère tous les utilisateurs du groupe
     */
    public Future<Map<String, User>> getAllUsers(String groupId) {
        String key = GROUP_USERS_KEY.formatted(groupId);

        return redis.connect()
                .compose(conn -> conn.send(Request.cmd(Command.HGETALL, key))
                        .map(response -> {
                            Map<String, User> users = new HashMap<>();

                            if (response == null) {
                                return users;
                            }

                            for (String responseKey : response.getKeys()) {
                                User user = jsonCodec.decode(response.get(responseKey).toString(), User.class);
                                users.put(user.id(), user);
                            }

                            return users;
                        })
                        .onComplete(ar -> conn.close())
                );
    }

    /**
     * Récupère le TTL de la clé du groupe
     */
    public Future<@Nullable Long> getUserTTL(String groupId) {
        String key = GROUP_USERS_KEY.formatted(groupId);

        return redis.connect()
                .compose(conn -> conn.send(Request.cmd(Command.TTL, key))
                        .map(Response::toLong)
                        .onComplete(ar -> conn.close())
                );
    }

    /**
     * Supprime tous les utilisateurs du groupe (supprime la clé)
     */
    public Future<@Nullable Integer> deleteAllUsers(String groupId) {
        String key = GROUP_USERS_KEY.formatted(groupId);

        return redis.connect()
                .compose(conn -> conn.send(Request.cmd(Command.DEL, key))
                        .map(Response::toInteger)
                        .onComplete(ar -> conn.close())
                );
    }

}