package com.monapp.redis;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.hash.HashCommands;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class HashExpirationService {

    private static final Logger LOG = Logger.getLogger(HashExpirationService.class);

    private final RedisDataSource ds;

    // Définition des noms des clés principales
    private static final String KEY_DATA_HASH = "app:users:data";
    private static final String KEY_EXP_HASH = "app:users:exp";
    private static final String PERM_PREFIX = "user:%s:perms";

    // Commandes typées
    // Pour les données : Clé String -> Valeur String (ex: JSON)
    private final HashCommands<String, String, String> dataCommands;
    // Pour l'expiration : Clé String (userId) -> Valeur Long (timestamp)
    // C'EST ICI QUE TA PROPOSITION PREND TOUT SON SENS : LE TYPAGE FORT.
    private final HashCommands<String, String, Long> expCommands;
    // Pour supprimer des clés génériques (le SET de permissions)
    private final KeyCommands<String> keyCommands;

    // Injection du client RedisDataSource via le constructeur
    public HashExpirationService(RedisDataSource ds) {
        this.ds = ds;
        this.dataCommands = ds.hash(String.class);
        // On précise que la valeur dans ce HASH sera traitée comme un Long Java
        this.expCommands = ds.hash(String.class, String.class, Long.class);
        this.keyCommands = ds.key();
    }

    /**
     * Créer un utilisateur avec une durée de vie.
     * @param userId L'identifiant unique
     * @param userData Les données métier (ex: JSON)
     * @param ttlSeconds Durée de vie en secondes à partir de maintenant
     */
    public void createUserWithTTL(String userId, String userData, long ttlSeconds) {
        long expirationTimestamp = System.currentTimeMillis() + (ttlSeconds * 1000);

        // 1. Stocker la donnée métier dans le premier HASH
        dataCommands.hset(KEY_DATA_HASH, userId, userData);

        // 2. Stocker la règle d'expiration dans le second HASH
        expCommands.hset(KEY_EXP_HASH, userId, expirationTimestamp);

        // (Optionnel) Créer des fausses données associées pour l'exemple
        keyCommands.del(String.format(PERM_PREFIX, userId)); // cleanup avant ajout
        ds.set(String.class).sadd(String.format(PERM_PREFIX, userId), "READ", "WRITE");

        LOG.infof("Utilisateur %s créé, expire à : %d", userId, expirationTimestamp);
    }

    /**
     * Tâche planifiée qui s'exécute toutes les 10 secondes pour nettoyer.
     */
    @Scheduled(every = "10h", delay = 10, concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void purgeExpiredUsersJob() {
        long now = System.currentTimeMillis();
        List<String> expiredIds = new ArrayList<>();

        LOG.debug("Début du scan d'expiration...");

        // --- ÉTAPE CRITIQUE : SCAN ---
        // On charge TOUT le hash d'expiration en mémoire pour vérifier les dates.
        // C'est propre car les valeurs sont déjà des Longs, pas de parsing de String.
        Map<String, Long> allExpirations = expCommands.hgetall(KEY_EXP_HASH);

        for (Map.Entry<String, Long> entry : allExpirations.entrySet()) {
            // Si le timestamp stocké est inférieur à "maintenant", c'est expiré
            if (now > entry.getValue()) {
                expiredIds.add(entry.getKey());
            }
        }

        if (expiredIds.isEmpty()) {
            LOG.debug("Rien à nettoyer.");
            return;
        }

        LOG.infof("Détection de %d utilisateurs expirés. Début du nettoyage.", expiredIds.size());

        // --- ÉTAPE DE NETTOYAGE ---
        for (String userId : expiredIds) {
            // Idéalement, utiliser un pipeline/transaction ici pour la production
            // 1. Supprimer de l'index d'expiration
            expCommands.hdel(KEY_EXP_HASH, userId);
            // 2. Supprimer les données métier
            dataCommands.hdel(KEY_DATA_HASH, userId);
            // 3. Supprimer les données associées (SET)
            keyCommands.del(String.format(PERM_PREFIX, userId));

            LOG.infof("Utilisateur %s purgé avec succès.", userId);
        }
    }
}