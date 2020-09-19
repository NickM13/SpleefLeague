package com.spleefleague.proxycore;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.spleefleague.coreapi.chat.Chat;
import com.spleefleague.coreapi.utils.packet.Packet;
import com.spleefleague.coreapi.utils.packet.PacketBungee;
import com.spleefleague.proxycore.game.arena.ArenaManager;
import com.spleefleague.proxycore.game.leaderboard.Leaderboards;
import com.spleefleague.proxycore.game.queue.QueueManager;
import com.spleefleague.proxycore.listener.BattleListener;
import com.spleefleague.proxycore.listener.ConnectionListener;
import com.spleefleague.proxycore.listener.SpigotPluginListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import com.spleefleague.proxycore.player.ProxyPlayerManager;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author NickM13
 * @since 6/6/2020
 */
public class ProxyCore extends Plugin {

    private static ProxyCore instance;

    private static final SortedMap<String, ServerInfo> lobbyServers = new TreeMap<>();
    private static final SortedMap<String, ServerInfo> minigameServers = new TreeMap<>();
    private static ScheduledTask serverPingTask;

    public static ProxyCore getInstance() {
        return instance;
    }

    private MongoClient mongoClient;
    private MongoDatabase database;

    private final ProxyPlayerManager playerManager = new ProxyPlayerManager();

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        instance = this;

        initMongo();

        getProxy().getPluginManager().registerListener(this, new BattleListener());
        getProxy().getPluginManager().registerListener(this, new ConnectionListener());
        getProxy().getPluginManager().registerListener(this, new SpigotPluginListener());

        playerManager.init();
        Leaderboards.init();
        QueueManager.init();
        ArenaManager.init();

        serverPingTask = ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), () -> {
            Set<String> toFindLobby = Sets.newHashSet(lobbyServers.keySet());
            Set<String> toFindMinigame = Sets.newHashSet(lobbyServers.keySet());
            for (ServerInfo server : getProxy().getServers().values()) {
                String name = server.getName();
                if (name.toLowerCase().startsWith("lobby")) {
                    server.ping((serverPing, throwable) -> {
                        if (serverPing != null) {
                            lobbyServers.put(name, server);
                        } else {
                            lobbyServers.remove(name);
                        }
                    });
                    toFindMinigame.remove(name);
                } else if (name.toLowerCase().startsWith("minigame")) {
                    server.ping((serverPing, throwable) -> {
                        if (serverPing != null) {
                            minigameServers.put(name, server);
                        } else {
                            minigameServers.remove(name);
                        }
                    });
                    toFindMinigame.remove(name);
                }
            }
            for (String name : toFindLobby) {
                lobbyServers.remove(name);
            }
            for (String name : toFindMinigame) {
                minigameServers.remove(name);
            }
            // TODO: Probably increase this value on release?
        }, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {
        playerManager.close();
        Leaderboards.close();
        QueueManager.close();
        serverPingTask.cancel();
    }

    public List<ServerInfo> getLobbyServers() {
        return Lists.newArrayList(lobbyServers.values());
    }

    public List<ServerInfo> getMinigameServers() {
        return Lists.newArrayList(minigameServers.values());
    }

    /**
     * Connect to the Mongo database based on the mongo.cfg file
     * that should be in the server's folder
     */
    public void initMongo() {
        try {
            Logger mongoLogger = Logger.getLogger("org.mongodb.driver.cluster");
            mongoLogger.setLevel(Level.SEVERE);

            Properties mongoProps = new Properties();
            String mongoPath = System.getProperty("user.dir") + "\\mongo.cfg";
            FileInputStream file = new FileInputStream(mongoPath);

            mongoProps.load(file);
            file.close();

            String mongoPrefix = mongoProps.getProperty("prefix", "mongodb://");
            String credentials = mongoProps.getProperty("credentials", "");
            if (!credentials.isEmpty()) credentials = credentials.concat("@");
            String host = mongoProps.getProperty("host", "localhost:27017") + "/";
            String defaultauthdb = mongoProps.getProperty("defaultauthdb", "admin") + "?";
            String options = mongoProps.getProperty("options", "");
            MongoClientURI uri = new MongoClientURI(mongoPrefix + credentials + host + defaultauthdb + options);
            mongoClient = new MongoClient(uri);
        } catch (IOException e) {
            mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017/"));
            getLogger().log(Level.WARNING, "mongo.cfg not found, using localhost");
        }
        database = mongoClient.getDatabase("SpleefLeague");
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public ProxyPlayerManager getPlayers() {
        return playerManager;
    }

    public void sendMessage(String text) {
        playerManager.getAll().forEach(pcp -> {
            if (pcp.getPlayer() != null) {
                pcp.getPlayer().sendMessage(new TextComponent(text));
            }
        });
    }

    public void sendMessage(TextComponent text) {
        playerManager.getAll().forEach(pcp -> pcp.getPlayer().sendMessage(text));
    }

    public static String getChatTag() {
        return Chat.TAG_BRACE + "[" + Chat.TAG + "SpleefLeague" + Chat.TAG_BRACE + "] " + Chat.DEFAULT;
    }

    public void sendMessage(ProxyCorePlayer pcp, String text) {
        pcp.getPlayer().sendMessage(new TextComponent(getChatTag() + text));
    }

    public void sendMessage(ProxyCorePlayer pcp, TextComponent text) {
        TextComponent textComp = new TextComponent(getChatTag());
        textComp.addExtra(text);
        pcp.getPlayer().sendMessage(textComp);
    }

    public void sendError(ProxyCorePlayer pcp, String text) {
        TextComponent text2 = new TextComponent(Chat.ERROR);
        text2.addExtra(text);
        pcp.getPlayer().sendMessage(text2);
    }

    public void sendError(ProxyCorePlayer pcp, TextComponent text) {
        TextComponent text2 = new TextComponent(Chat.ERROR);
        text2.addExtra(text);
        pcp.getPlayer().sendMessage(text2);
    }

    /**
     * Send a packet to all servers with 1 or more players
     *
     * @param output
     */
    public void sendPacket(Packet output) {
        for (Map.Entry<String, ServerInfo> server : ProxyCore.getInstance().getProxy().getServersCopy().entrySet()) {
            if (!server.getValue().getPlayers().isEmpty()) {
                server.getValue().sendData("slcore:bungee", output.toByteArray());
            }
        }
    }

    /**
     * Send a packet to a specific server
     *
     * @param server
     * @param packet
     */
    public void sendPacket(ServerInfo server, PacketBungee packet) {
        server.sendData("slcore:bungee", packet.toByteArray());
    }

}
