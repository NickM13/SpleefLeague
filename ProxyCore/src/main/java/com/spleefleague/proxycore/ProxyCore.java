package com.spleefleague.proxycore;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.spleefleague.coreapi.chat.Chat;
import com.spleefleague.proxycore.game.arena.ArenaManager;
import com.spleefleague.proxycore.game.leaderboard.Leaderboards;
import com.spleefleague.proxycore.game.queue.QueueManager;
import com.spleefleague.proxycore.listener.BattleListener;
import com.spleefleague.proxycore.listener.ConnectionListener;
import com.spleefleague.proxycore.listener.CoreListener;
import com.spleefleague.proxycore.listener.PartyListener;
import com.spleefleague.proxycore.listener.QueueListener;
import com.spleefleague.proxycore.listener.RefreshListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import com.spleefleague.proxycore.player.ProxyPlayerManager;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author NickM13
 * @since 6/6/2020
 */
public class ProxyCore extends Plugin {

    private static ProxyCore instance;

    private static final List<ServerInfo> lobbyServers = new ArrayList<>();
    private static final List<ServerInfo> minigameServers = new ArrayList<>();
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
        getProxy().getPluginManager().registerListener(this, new CoreListener());
        getProxy().getPluginManager().registerListener(this, new ConnectionListener());
        getProxy().getPluginManager().registerListener(this, new PartyListener());
        getProxy().getPluginManager().registerListener(this, new QueueListener());
        getProxy().getPluginManager().registerListener(this, new RefreshListener());

        playerManager.init();
        Leaderboards.init();
        QueueManager.init();
        ArenaManager.init();

        serverPingTask = ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), () -> {
            lobbyServers.clear();
            minigameServers.clear();
            for (ServerInfo server : getProxy().getServers().values()) {
                if (server.getName().toLowerCase().startsWith("lobby")) {
                    server.ping((serverPing, throwable) -> {
                        if (serverPing != null) {
                            lobbyServers.add(server);
                        }
                    });
                } else if (server.getName().toLowerCase().startsWith("minigame")) {
                    server.ping((serverPing, throwable) -> {
                        if (serverPing != null) {
                            minigameServers.add(server);
                        }
                    });
                }
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
        return lobbyServers;
    }

    public List<ServerInfo> getMinigameServers() {
        return minigameServers;
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

    public static void sendMessage(String text) {
        getInstance().getPlayers().getAll().forEach(pcp -> pcp.getPlayer().sendMessage(new TextComponent(text)));
    }

    public static void sendMessage(TextComponent text) {
        getInstance().getPlayers().getAll().forEach(pcp -> pcp.getPlayer().sendMessage(text));
    }

    public static String getChatTag() {
        return Chat.TAG_BRACE + "[" + Chat.TAG + "SpleefLeague" + Chat.TAG_BRACE + "] " + Chat.DEFAULT;
    }

    public static void sendMessage(ProxyCorePlayer pcp, String text) {
        pcp.getPlayer().sendMessage(new TextComponent(getChatTag() + text));
    }

    public static void sendMessage(ProxyCorePlayer pcp, TextComponent text) {
        TextComponent textComp = new TextComponent(getChatTag());
        textComp.addExtra(text);
        pcp.getPlayer().sendMessage(textComp);
    }

    public static void sendError(ProxyCorePlayer pcp, String text) {
        TextComponent text2 = new TextComponent(Chat.ERROR);
        text2.addExtra(text);
        pcp.getPlayer().sendMessage(text2);
    }

    public static void sendError(ProxyCorePlayer pcp, TextComponent text) {
        TextComponent text2 = new TextComponent(Chat.ERROR);
        text2.addExtra(text);
        pcp.getPlayer().sendMessage(text2);
    }

}
