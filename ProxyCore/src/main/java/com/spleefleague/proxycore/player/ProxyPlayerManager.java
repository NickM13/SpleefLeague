package com.spleefleague.proxycore.player;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import com.spleefleague.proxycore.ProxyCore;
import net.md_5.bungee.api.chat.TextComponent;
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
                pcp.updateTempRanks();
                save(pcp);
            }
        }, 5L, 5L, TimeUnit.SECONDS);
        offlineAutosaveTask = ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), () -> {
            Iterator<Map.Entry<UUID, ProxyCorePlayer>> it = offlinePlayers.entrySet().iterator();
            while (it.hasNext()) {
                ProxyCorePlayer pcp = it.next().getValue();
                it.remove();
                save(pcp);
                System.out.println("Auto saving " + pcp.getName());
            }
        }, 30L, 30L, TimeUnit.SECONDS);
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

    public ProxyCorePlayer onPlayerJoin(ProxiedPlayer pp) {
        ProxyCorePlayer pcp = load(pp);

        TextComponent text = new TextComponent();
        text.addExtra(pcp.getChatName());
        text.addExtra(" has logged in");
        for (UUID uuid : pcp.getFriends().getAll()) {
            ProxyCorePlayer friend = ProxyCore.getInstance().getPlayers().get(uuid);
            if (friend != null) {
                if (friend.getOptions().getBoolean("Friend:Connection")) {
                    ProxyCore.getInstance().sendMessage(friend, text);
                }
                friend.getFriends().onPlayerJoin(pcp);
            }
        }

        return pcp;
    }

    public void onPlayerQuit(ProxiedPlayer pp) {
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(pp.getUniqueId());
        pcp.close();
        onlinePlayers.remove(pcp.getUniqueId());
        save(pcp);

        TextComponent text = new TextComponent();
        text.addExtra(pcp.getChatName());
        text.addExtra(" has logged out");

        for (UUID uuid : pcp.getFriends().getAll()) {
            ProxyCorePlayer friend = ProxyCore.getInstance().getPlayers().get(uuid);
            if (friend != null) {
                if (friend.getOptions().getBoolean("Friend:Connection")) {
                    ProxyCore.getInstance().sendMessage(friend, text);
                }
                friend.getFriends().onPlayerLeave(pcp);
            }
        }
    }

    private ProxyCorePlayer load(ProxiedPlayer pp) {
        ProxyCorePlayer pcp;
        if (offlinePlayers.containsKey(pp.getUniqueId())) {
            pcp = offlinePlayers.get(pp.getUniqueId());
        } else {
            Document doc = playerCol.find(new Document("identifier", pp.getUniqueId().toString())).first();
            pcp = new ProxyCorePlayer();
            if (doc != null) {
                pcp.load(doc);
            } else {
                pcp.newPlayer(pp.getUniqueId(), pp.getName());
                save(pcp);
            }
        }
        pcp.init();
        onlinePlayers.put(pp.getUniqueId(), pcp);
        return pcp;
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

    public void saveIfLoaded(UUID uuid) {
        if (offlinePlayers.containsKey(uuid)) {
            save(offlinePlayers.get(uuid));
        } else if (onlinePlayers.containsKey(uuid)) {
            save(onlinePlayers.get(uuid));
        }
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
            offlinePlayers.put(uuid, pcp);
            return pcp;
        }
    }

}
