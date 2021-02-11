package com.spleefleague.proxycore;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.spleefleague.coreapi.chat.Chat;
import com.spleefleague.coreapi.utils.packet.bungee.refresh.PacketBungeeRefreshServerList;
import com.spleefleague.proxycore.chat.ChatChannel;
import com.spleefleague.proxycore.chat.ProxyChat;
import com.spleefleague.proxycore.command.DebugCommand;
import com.spleefleague.proxycore.command.PurchaseCommand;
import com.spleefleague.proxycore.droplet.DropletManager;
import com.spleefleague.proxycore.game.BattleSessionManager;
import com.spleefleague.proxycore.game.arena.ArenaManager;
import com.spleefleague.proxycore.game.leaderboard.LeaderboardManager;
import com.spleefleague.proxycore.game.queue.QueueManager;
import com.spleefleague.proxycore.infraction.ProxyInfractionManager;
import com.spleefleague.proxycore.listener.BattleListener;
import com.spleefleague.proxycore.listener.ConnectionListener;
import com.spleefleague.proxycore.listener.SpigotPluginListener;
import com.spleefleague.proxycore.packet.PacketManager;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import com.spleefleague.proxycore.party.ProxyPartyManager;
import com.spleefleague.proxycore.player.ProxyPlayerManager;
import com.spleefleague.proxycore.player.ranks.ProxyRankManager;
import com.spleefleague.proxycore.season.SeasonManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private final ProxyPartyManager partyManager = new ProxyPartyManager();

    private final LeaderboardManager leaderboardManager = new LeaderboardManager();
    private final ProxyInfractionManager infractionManager = new ProxyInfractionManager();
    private final ArenaManager arenaManager = new ArenaManager();
    private final QueueManager queueManager = new QueueManager();
    private final ProxyRankManager rankManager = new ProxyRankManager();
    private final PacketManager packetManager = new PacketManager();
    private final ProxyChat chat = new ProxyChat();
    private final SeasonManager seasonManager = new SeasonManager();

    private final DropletManager dropletManager = new DropletManager();

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        instance = this;

        initMongo();
        initCommands();

        getProxy().getPluginManager().registerListener(this, new BattleListener());
        getProxy().getPluginManager().registerListener(this, new ConnectionListener());
        getProxy().getPluginManager().registerListener(this, new SpigotPluginListener());

        BattleSessionManager.init();

        dropletManager.init();
        seasonManager.init();

        playerManager.init();
        rankManager.init();
        leaderboardManager.init();
        infractionManager.init();
        arenaManager.init();
        queueManager.init();
        packetManager.init();

        serverPingTask = ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), () -> {
            Set<String> toFindLobby = Sets.newHashSet(lobbyServers.keySet());
            Set<String> toFindMinigame = Sets.newHashSet(minigameServers.keySet());

            for (ServerInfo server : getProxy().getServersCopy().values()) {
                String name = server.getName();
                if (name.toLowerCase().startsWith("lobby")) {
                    server.ping((serverPing, throwable) -> {
                        if (serverPing != null) {
                            lobbyServers.put(name, server);
                            onServerConnect(server);
                        } else {
                            lobbyServers.remove(name);
                        }
                    });
                    toFindLobby.remove(name);
                } else if (name.toLowerCase().startsWith("minigame")) {
                    server.ping((serverPing, throwable) -> {
                        if (serverPing != null) {
                            minigameServers.put(name, server);
                            onServerConnect(server);
                        } else {
                            minigameServers.remove(name);
                        }
                    });
                    toFindMinigame.remove(name);
                }
            }
            for (String name : toFindLobby) {
                lobbyServers.remove(name);
                onServerDisconnect(name);
            }
            for (String name : toFindMinigame) {
                minigameServers.remove(name);
                onServerDisconnect(name);
            }
            packetManager.sendPacket(new PacketBungeeRefreshServerList(Lists.newArrayList(lobbyServers.keySet()), Lists.newArrayList(minigameServers.keySet())));
            // TODO: Probably increase this value on release?
        }, 0, 10, TimeUnit.SECONDS);
    }

    public void onServerConnect(ServerInfo serverInfo) {
        packetManager.connect(serverInfo);
    }

    public void onServerDisconnect(String name) {
        packetManager.disconnect(name);
    }

    @Override
    public void onDisable() {
        BattleSessionManager.close();

        playerManager.close();
        leaderboardManager.close();
        arenaManager.close();
        queueManager.close();
        rankManager.close();
        infractionManager.close();
        packetManager.close();
        seasonManager.close();

        serverPingTask.cancel();
    }

    public ProxyInfractionManager getInfractions() {
        return infractionManager;
    }

    public LeaderboardManager getLeaderboards() {
        return leaderboardManager;
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public ProxyRankManager getRankManager() {
        return rankManager;
    }

    public ProxyChat getChat() {
        return chat;
    }

    public List<ServerInfo> getLobbyServers() {
        return Lists.newArrayList(lobbyServers.values());
    }

    public List<ServerInfo> getMinigameServers() {
        return Lists.newArrayList(minigameServers.values());
    }

    public ServerInfo getServerByName(String name) {
        if (lobbyServers.containsKey(name)) {
            return lobbyServers.get(name);
        }
        if (minigameServers.containsKey(name)) {
            return minigameServers.get(name);
        }
        return null;
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
            String mongoPath = System.getProperty("user.dir") + "\\..\\mongo.cfg";
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

    private void initCommands() {
        getProxy().getPluginManager().registerCommand(this, new DebugCommand());
        getProxy().getPluginManager().registerCommand(this, new PurchaseCommand());
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public ProxyPlayerManager getPlayers() {
        return playerManager;
    }

    public ProxyPartyManager getPartyManager() {
        return partyManager;
    }

    public DropletManager getDropletManager() {
        return dropletManager;
    }

    public SeasonManager getSeasonManager() {
        return seasonManager;
    }

    public void sendMessage(String text) {
        playerManager.getAll().forEach(pcp -> {
            if (pcp.getPlayer() != null) {
                pcp.getPlayer().sendMessage(new TextComponent(text));
            }
        });
    }

    public void sendMessage(TextComponent text) {
        sendMessage(ChatChannel.GLOBAL, text);
    }

    public void sendMessage(ChatChannel channel, TextComponent text) {
        TextComponent component = new TextComponent(getChatTag());
        component.setColor(ChatColor.GRAY);
        component.addExtra(text);
        playerManager.getAll().forEach(pcp -> {
            if (channel.isActive(pcp)) {
                pcp.getPlayer().sendMessage(component);
            }
        });
    }

    public static String getChatTag() {
        return Chat.TAG_BRACE + "[" + Chat.TAG + "SpleefLeague" + Chat.TAG_BRACE + "] " + Chat.DEFAULT;
    }

    public void sendMessage(ProxyCorePlayer pcp, String text) {
        if (pcp.getPlayer() == null) return;
        pcp.getPlayer().sendMessage(new TextComponent(getChatTag() + text));
    }

    public void sendMessage(ProxyCorePlayer pcp, TextComponent text) {
        if (pcp.getPlayer() == null) return;
        TextComponent textComp = new TextComponent(getChatTag());
        text.setColor(ChatColor.GRAY);
        textComp.addExtra(text);
        pcp.getPlayer().sendMessage(textComp);
    }

    public void sendMessageError(ProxyCorePlayer pcp, TextComponent text) {
        if (pcp.getPlayer() == null) return;
        TextComponent text2 = new TextComponent(getChatTag());
        text.setColor(ChatColor.RED);
        text2.addExtra(text);
        text2.addExtra(ChatColor.RED + "!");
        pcp.getPlayer().sendMessage(text2);
    }

    public void sendMessageSuccess(ProxyCorePlayer pcp, TextComponent text) {
        if (pcp.getPlayer() == null) return;
        TextComponent text2 = new TextComponent(getChatTag());
        text.setColor(ChatColor.GREEN);
        text2.addExtra(text);
        pcp.getPlayer().sendMessage(text2);
    }

    public void sendMessageInfo(ProxyCorePlayer pcp, TextComponent text) {
        if (pcp.getPlayer() == null) return;
        TextComponent text2 = new TextComponent(getChatTag());
        text.setColor(ChatColor.YELLOW);
        text2.addExtra(text);
        pcp.getPlayer().sendMessage(text2);
    }

    public PacketManager getPacketManager() {
        return packetManager;
    }

}
