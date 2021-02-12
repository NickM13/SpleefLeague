package com.spleefleague.proxycore.game.session;

import com.mongodb.client.MongoCollection;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.droplet.Droplet;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author NickM13
 * @since 2/10/2021
 */
public class BattleSessionManager {

    private static MongoCollection<Document> battleSessionCol;

    private static final Map<UUID, BattleSession> sessionMap = new HashMap<>();
    private static final List<String> endedSessions = new ArrayList<>();

    private static ScheduledTask endSessionTask;
    private static ScheduledTask hangedSessionTask;

    public static void init() {
        battleSessionCol = ProxyCore.getInstance().getDatabase().getCollection("BattleSessions");
        battleSessionCol.drop();

        endSessionTask = ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), BattleSessionManager::clearEndedSessions, 15, 15, TimeUnit.SECONDS);
        hangedSessionTask = ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), BattleSessionManager::clearHangedSessions, 30, 30, TimeUnit.SECONDS);
    }

    public static void close() {
        endSessionTask.cancel();
        hangedSessionTask.cancel();
    }

    public static void createBattleSession(UUID battleId, String mode, Droplet droplet, List<UUID> players) {
        BattleSession battleSession = new BattleSession(battleId, mode, droplet, players);
        battleSessionCol.insertOne(battleSession.toDocument());
        sessionMap.put(battleId, battleSession);
    }

    public static boolean isOngoing(UUID battleId) {
        return sessionMap.containsKey(battleId) && sessionMap.get(battleId).getPlayers().size() > 1;
    }

    public static BattleSession destroyBattleSession(UUID battleId) {
        BattleSession session = sessionMap.remove(battleId);
        if (session != null) {
            session.end();
            endedSessions.add(battleId.toString());
        }
        return session;
    }

    public static BattleSession getSession(UUID battleId) {
        return sessionMap.get(battleId);
    }

    public static void clearEndedSessions() {
        battleSessionCol.deleteMany(new Document("identifier", new Document("$in", endedSessions)));
    }

    public static void onPing(UUID battleId) {
        if (sessionMap.containsKey(battleId)) {
            sessionMap.get(battleId).ping();
        }
    }

    public static void clearHangedSessions() {
        // Add functionality
        // Probably receive updates every 30 seconds or so from server to ensure a battle is still ongoing
        sessionMap.entrySet().removeIf(uuidBattleSessionEntry -> uuidBattleSessionEntry.getValue().isHanged());
    }

}
