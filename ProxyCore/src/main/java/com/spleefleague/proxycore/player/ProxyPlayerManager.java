package com.spleefleague.proxycore.player;

import com.mongodb.client.MongoCollection;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.proxycore.ProxyCore;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bson.Document;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author NickM13
 * @since 6/7/2020
 */
public class ProxyPlayerManager {

    private final Map<UUID, ProxyCorePlayer> players = new HashMap<>();
    private MongoCollection<Document> playerCol;

    public void init() {
        playerCol = ProxyCore.getInstance().getDatabase().getCollection("Players");
    }

    public void close() {
        players.clear();
    }

    public void onPlayerJoin(ProxiedPlayer pp) {
        load(pp);
        ProxyCore.getInstance().sendMessage(ChatColor.YELLOW + pp.getDisplayName() + ChatColor.GRAY +  " is now online.");
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
        players.put(pp.getUniqueId(), pcp);
    }

    /**
     * Player data should only be saved on the spigot servers where certain info such as collectibles are
     * properly managed by the Core plugin, ProxyCorePlayer is just a small shell of the CorePlayer
     *
     * @param pcp
     */
    public void save(ProxyCorePlayer pcp) {

    }

    public void onPlayerQuit(ProxiedPlayer pp) {
        ProxyCore.getInstance().getPlayers().get(pp.getUniqueId()).setBattleContainer(null);
        ProxyCore.getInstance().sendMessage(ChatColor.YELLOW + pp.getDisplayName() + ChatColor.GRAY + " is now offline.");
        players.remove(pp.getUniqueId());
    }

    public Collection<ProxyCorePlayer> getAll() {
        return players.values();
    }

    public ProxyCorePlayer get(UUID uuid) {
        return players.get(uuid);
    }

    public ProxyCorePlayer getOffline(UUID uuid) {
        if (players.containsKey(uuid)) {
            return players.get(uuid);
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
