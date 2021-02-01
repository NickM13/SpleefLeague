package com.spleefleague.proxycore.player;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import com.spleefleague.proxycore.ProxyCore;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author NickM13
 * @since 6/7/2020
 */
public class ProxyPlayerManager {

    private final Map<UUID, ProxyCorePlayer> onlinePlayers = new HashMap<>();
    private final Map<UUID, ProxyCorePlayer> offlinePlayers = new HashMap<>();

    private MongoCollection<Document> playerCol;

    private ScheduledTask autosaveTask, offlineAutosaveTask;

    public void init() {
        playerCol = ProxyCore.getInstance().getDatabase().getCollection("Players");
        autosaveTask = ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), () -> {
            for (ProxyCorePlayer pcp : onlinePlayers.values()) {
                save(pcp);
            }
        }, 5L, 5L, TimeUnit.SECONDS);
        offlineAutosaveTask = ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), () -> {
            Iterator<Map.Entry<UUID, ProxyCorePlayer>> it = offlinePlayers.entrySet().iterator();
            while (it.hasNext()) {
                ProxyCorePlayer pcp = it.next().getValue();
                it.remove();
                save(pcp);
            }
        }, 10L, 10L, TimeUnit.SECONDS);
    }

    public void close() {
        autosaveTask.cancel();
        offlineAutosaveTask.cancel();
        for (ProxyCorePlayer pcp : onlinePlayers.values()) {
            save(pcp);
        }
        for (ProxyCorePlayer pcp : offlinePlayers.values()) {
            save(pcp);
        }
        onlinePlayers.clear();
    }

    public void onPlayerJoin(ProxiedPlayer pp) {
        load(pp);
    }

    private void load(ProxiedPlayer pp) {
        Document doc = playerCol.find(new Document("identifier", pp.getUniqueId().toString())).first();
        ProxyCorePlayer pcp = new ProxyCorePlayer();
        if (doc != null) {
            pcp.load(doc);
        } else {
            pcp.newPlayer(pp.getUniqueId(), pp.getName());
        }
        pcp.init();
        onlinePlayers.put(pp.getUniqueId(), pcp);
    }

    /**
     * Player data should only be saved on the spigot servers where certain info such as collectibles are
     * properly managed by the Core plugin, ProxyCorePlayer is just a small shell of the CorePlayer
     *
     * @param pcp Proxy Core Player
     */
    public void save(ProxyCorePlayer pcp) {
        Document query = new Document("identifier", pcp.getUniqueId().toString());
        playerCol.replaceOne(query, pcp.toDocument(), new ReplaceOptions().upsert(true));
    }

    public void onPlayerQuit(ProxiedPlayer pp) {
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(pp.getUniqueId());
        pcp.setBattleContainer(null);
        onlinePlayers.remove(pp.getUniqueId());
        save(pcp);
    }

    public Collection<ProxyCorePlayer> getAll() {
        return onlinePlayers.values();
    }

    public ProxyCorePlayer get(UUID uuid) {
        return onlinePlayers.get(uuid);
    }

    public ProxyCorePlayer getOffline(UUID uuid) {
        if (offlinePlayers.containsKey(uuid)) {
            return offlinePlayers.get(uuid);
        } else if (onlinePlayers.containsKey(uuid)) {
            return onlinePlayers.get(uuid);
        } else {
            Document doc = playerCol.find(new Document("identifier", uuid.toString())).first();
            ProxyCorePlayer pcp = new ProxyCorePlayer();
            ProxiedPlayer pp = ProxyCore.getInstance().getProxy().getPlayer(uuid);
            if (doc != null) {
                pcp.load(doc);
            } else {
                pcp.newPlayer(pp.getUniqueId(), pp.getName());
            }
            pcp.initOffline();
            return pcp;
        }
    }

}
