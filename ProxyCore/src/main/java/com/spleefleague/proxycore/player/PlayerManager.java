package com.spleefleague.proxycore.player;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import com.spleefleague.proxycore.ProxyCore;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.bson.Document;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author NickM13
 * @since 6/7/2020
 */
public class PlayerManager <P extends ProxyDBPlayer> {

    private final Map<UUID, P> onlinePlayers = new HashMap<>();
    private final Map<UUID, P> offlinePlayers = new HashMap<>();

    private final MongoCollection<Document> playerColl;
    protected final Class<P> playerClass;

    private final ScheduledTask autosaveTask;
    private final ScheduledTask offlineAutosaveTask;

    public PlayerManager(Class<P> playerClass, MongoCollection<Document> collection) {
        this.playerClass = playerClass;
        playerColl = collection;

        autosaveTask = ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), () -> {
            for (P pcp : onlinePlayers.values()) {
                save(pcp);
            }
        }, 5L, 5L, TimeUnit.SECONDS);
        offlineAutosaveTask = ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), () -> {
            Iterator<Map.Entry<UUID, P>> it = offlinePlayers.entrySet().iterator();
            while (it.hasNext()) {
                P pcp = it.next().getValue();
                save(pcp);
                if (pcp.getLastOfflineLoad() < System.currentTimeMillis() - 5000) {
                    it.remove();
                }
                System.out.println("Auto saving " + pcp.getName());
            }
        }, 30L, 30L, TimeUnit.SECONDS);
    }

    public void close() {
        autosaveTask.cancel();
        offlineAutosaveTask.cancel();
        for (P pcp : onlinePlayers.values()) {
            save(pcp);
        }
        for (P pcp : offlinePlayers.values()) {
            save(pcp);
        }
        onlinePlayers.clear();
    }

    public P onPlayerJoin(ProxiedPlayer pp) {
        return load(pp);
    }

    public P onPlayerQuit(UUID uuid) {
        P pcp = onlinePlayers.remove(uuid);
        pcp.close();
        save(pcp);
        return pcp;
    }

    private P load(ProxiedPlayer pp) {
        P pcp;
        if (offlinePlayers.containsKey(pp.getUniqueId())) {
            pcp = offlinePlayers.get(pp.getUniqueId());
        } else {
            Document doc = playerColl.find(new Document("identifier", pp.getUniqueId().toString())).first();
            try {
                pcp = playerClass.getDeclaredConstructor().newInstance();
                if (doc != null) {
                    pcp.load(doc);
                } else {
                    pcp.newPlayer(pp.getUniqueId(), pp.getName());
                    save(pcp);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                return null;
            }
        }
        pcp.init(pp);
        onlinePlayers.put(pp.getUniqueId(), pcp);
        return pcp;
    }

    /**
     * Player data should only be saved on the spigot servers where certain info such as collectibles are
     * properly managed by the Core plugin, P is just a small shell of the CorePlayer
     *
     * @param pcp Proxy Core Player
     */
    public void save(P pcp) {
        Document query = new Document("identifier", pcp.getUniqueId().toString());
        playerColl.replaceOne(query, pcp.toDocument(), new ReplaceOptions().upsert(true));
    }

    public void saveIfLoaded(UUID uuid) {
        if (offlinePlayers.containsKey(uuid)) {
            save(offlinePlayers.get(uuid));
        } else if (onlinePlayers.containsKey(uuid)) {
            save(onlinePlayers.get(uuid));
        }
    }

    public Collection<P> getAll() {
        return onlinePlayers.values();
    }

    public P get(UUID uuid) {
        return onlinePlayers.get(uuid);
    }

    public P getOffline(UUID uuid) {
        if (onlinePlayers.containsKey(uuid)) {
            return onlinePlayers.get(uuid);
        } else if (offlinePlayers.containsKey(uuid)) {
            offlinePlayers.get(uuid).initOffline();
            return offlinePlayers.get(uuid);
        } else {
            try {
                Document doc = playerColl.find(new Document("identifier", uuid.toString())).first();
                P pcp = playerClass.getDeclaredConstructor().newInstance();
                ProxiedPlayer pp = ProxyCore.getInstance().getProxy().getPlayer(uuid);
                if (doc != null) {
                    pcp.load(doc);
                } else {
                    pcp.newPlayer(pp.getUniqueId(), pp.getName());
                }
                pcp.initOffline();
                offlinePlayers.put(uuid, pcp);
                return pcp;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}
