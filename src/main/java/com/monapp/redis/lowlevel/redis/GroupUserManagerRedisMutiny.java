package com.monapp.redis.lowlevel.redis;

import com.monapp.model.User;
import com.monapp.redis.lowlevel.codec.JsonCodec;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.redis.client.Command;
import io.vertx.mutiny.redis.client.Redis;
import io.vertx.mutiny.redis.client.Request;
import io.vertx.mutiny.redis.client.Response;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class GroupUserManagerRedisMutiny {

    public static final String GROUP_USERS_KEY = "groups:%s";
    private static final long DEFAULT_TTL = 36000;

    private final Redis redis;
    private final JsonCodec jsonCodec;

    public GroupUserManagerRedisMutiny(Redis redis, JsonCodec jsonCodec) {
        this.redis = redis;
        this.jsonCodec = jsonCodec;
    }

    /**
     * Crée un utilisateur dans le groupe (hash Redis)
     */
    public Uni<Boolean> createUser(String groupId, User user) {
        String key = GROUP_USERS_KEY.formatted(groupId);
        String userJson = jsonCodec.encode(user);

        // 1. Préparation de la requête HSET
        // Commande: HSET key field value
        Request hsetRequest = Request.cmd(Command.HSET)
                .arg(key) // key of the hash
                .arg(user.id()) // field name
                .arg(userJson); // field value

        // 2. Préparation de la requête EXPIRE
        // Commande: EXPIRE key seconds
        Request expireRequest = Request.cmd(Command.EXPIRE)
                .arg(key)
                .arg(DEFAULT_TTL);

        // 3. Exécution séquentielle
        return redis.send(hsetRequest)
                .chain(response -> redis.send(expireRequest))
                .map(response -> {
                    // Redis EXPIRE retourne l'entier 1 si le timeout a été défini, 0 sinon.
                    return response != null && response.toInteger() == 1;
                });
    }

    /**
     * Supprime un utilisateur du groupe (HDEL)
     */
    public Uni<Integer> deleteUser(String groupId, String userId) {
        String key = GROUP_USERS_KEY.formatted(groupId);

        return redis.send(Request.cmd(Command.HDEL).arg(key).arg(userId))
                .map(Response::toInteger);
    }

    /**
     * Récupère un utilisateur spécifique (HGET)
     */
    public Uni<User> getUser(String groupId, String userId) {
        String key = GROUP_USERS_KEY.formatted(groupId);

        return redis.send(Request.cmd(Command.HGET).arg(key).arg(userId))
                .map(response -> {
                    // Redis renvoie null si la clé/champ n'existe pas
                    if (response == null) {
                        return null;
                    }
                    return jsonCodec.decode(response.toString(), User.class);
                });
    }

    /**
     * Récupère tous les utilisateurs du groupe (HGETALL)
     * Note : HGETALL renvoie une liste plate [key1, val1, key2, val2...]
     */
    public Uni<Map<String, User>> getAllUsers(String groupId) {
        String key = GROUP_USERS_KEY.formatted(groupId);

        return redis.send(Request.cmd(Command.HGETALL).arg(key))
                .map(response -> {
                    Map<String, User> users = new HashMap<>();

                    // Si la réponse est vide ou nulle
                    if (response == null || response.size() == 0) {
                        return users;
                    }

                    // La réponse Vert.x pour HGETALL est un tableau [K, V, K, V]
                    // On itère par pas de 2
                    for (int i = 0; i < response.size(); i += 2) {
                        // response.get(i) est la clé (userId)
                        // response.get(i+1) est la valeur (JSON)
                        String json = response.get(i + 1).toString();
                        User user = jsonCodec.decode(json, User.class);
                        users.put(user.id(), user);
                    }

                    return users;
                });
    }

    /**
     * Récupère le TTL de la clé du groupe (TTL)
     */
    public Uni<Long> getUserTTL(String groupId) {
        String key = GROUP_USERS_KEY.formatted(groupId);

        return redis.send(Request.cmd(Command.TTL).arg(key))
                .map(Response::toLong);
    }


    /**
     * Supprime tous les utilisateurs du groupe (DEL)
     */
    public Uni<Integer> deleteAllUsers(String groupId) {
        String key = GROUP_USERS_KEY.formatted(groupId);

        return redis.send(Request.cmd(Command.DEL).arg(key))
                .map(Response::toInteger);
    }

}