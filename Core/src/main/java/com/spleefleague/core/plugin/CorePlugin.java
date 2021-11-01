/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.plugin;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.manager.BattleManager;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.logger.CoreLoggerFilter;
import com.spleefleague.core.music.NoteBlockMusic;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.PlayerManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import com.spleefleague.core.player.scoreboard.PersonalScoreboard;
import com.spleefleague.coreapi.utils.packet.spigot.battle.PacketSpigotBattleChallenge;
import com.spleefleague.coreapi.utils.packet.spigot.battle.PacketSpigotBattleForceStart;
import com.spleefleague.coreapi.utils.packet.spigot.battle.PacketSpigotBattleSpectate;
import com.spleefleague.coreapi.utils.packet.spigot.queue.PacketSpigotQueueJoin;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.logging.log4j.LogManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * CorePlugin is the base class for all SpleefLeague plugins,
 * all plugins that use this are stored in a plugins master list
 * allowing for synced battle states
 *
 * @author NickM13
 */
public abstract class CorePlugin extends JavaPlugin {

    private static final List<CorePlugin> plugins = new ArrayList<>();
    private static final List<PlayerManager<?, ?>> playerManagers = new ArrayList<>();

    protected static Map<BattleMode, BattleManager> battleManagers = new HashMap<>();

    // For quick referencing in tab command auto completes
    protected static Set<String> ingamePlayerNames = new HashSet<>();

    private static MongoClient mongoClient;
    private MongoDatabase pluginDb;

    protected boolean running = false;

    /**
     * Initialize plugin (and online players for /reload)
     */
    @Override
    public final void onEnable() {
        init();

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        running = true;
        plugins.add(this);
    }

    protected abstract void init();

    public static void registerPlayerManager(PlayerManager<?, ?> playerManager) {
        playerManagers.add(playerManager);
    }

    /**
     * Connect to the Mongo database based on the mongo.cfg file
     * that should be in the server's folder
     */
    public static void initMongo() {
        org.apache.logging.log4j.core.Logger coreLogger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        coreLogger.addFilter(new CoreLoggerFilter());
        try {
            Properties mongoProps = new Properties();
            String mongoPath = System.getProperty("user.dir") + "\\..\\..\\mongo.cfg";
            System.out.println("MONGO PATH " + mongoPath);
            FileInputStream file = new FileInputStream(mongoPath);

            mongoProps.load(file);
            file.close();

            String mongoPrefix = mongoProps.getProperty("prefix", "mongodb://");
            String credentials = mongoProps.getProperty("credentials", "");
            System.out.println(credentials);
            if (!credentials.isEmpty()) credentials = credentials.concat("@");
            String host = mongoProps.getProperty("host", "localhost:27017") + "/";
            String defaultauthdb = mongoProps.getProperty("defaultauthdb", "admin") + "?";
            String options = mongoProps.getProperty("options", "");
            MongoClientURI uri = new MongoClientURI(mongoPrefix + credentials + host + defaultauthdb + options);
            mongoClient = new MongoClient(uri);
        } catch (IOException e) {
            mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017/"));
            Core.getInstance().getLogger().log(Level.WARNING, "mongo.cfg not found, using localhost");
        }
    }

    /**
     * Disables the plugin and some common managers
     */
    @Override
    public final void onDisable() {
        for (BattleManager bm : battleManagers.values()) {
            bm.close();
        }
        battleManagers.clear();
        close();
    }

    protected abstract void close();

    public void reloadCollectibles() {

    }

    public void reloadSettings() {

    }

    public void reloadArenas() {

    }

    /**
     * Add a battle manager to the registry
     *
     * @param mode Arena Mode
     */
    public final void addBattleManager(BattleMode mode) {
        battleManagers.put(mode, BattleManager.createManager(mode));
    }

    /**
     * Get the battle manager for an arena mode from the registry
     *
     * @param mode Arena Mode
     * @return Battle Manager
     */
    public final BattleManager getBattleManager(BattleMode mode) {
        return battleManagers.get(mode);
    }

    public final void forceStart(BattleMode mode, List<CorePlayer> corePlayers, Arena arena) {
        Core.getInstance().sendPacket(new PacketSpigotBattleForceStart(mode.getName(), "arena:" + arena.getIdentifierNoTag(), corePlayers));
    }

    /**
     * Queue a player for a battle
     *
     * @param mode Arena Mode
     * @param cp   Core Player
     */
    public final void queuePlayer(BattleMode mode, CorePlayer cp) {
        Core.getInstance().sendPacket(new PacketSpigotQueueJoin(cp.getUniqueId(), mode.getName(), "arena:*"));
    }

    /**
     * Queue a player for a specific arena
     *
     * @param mode  Arena Mode
     * @param cp    Core Player
     * @param arena Arena
     */
    public final void queuePlayer(BattleMode mode, CorePlayer cp, Arena arena) {
        if (arena == null) {
            queuePlayer(mode, cp);
            return;
        }
        Core.getInstance().sendPacket(new PacketSpigotQueueJoin(cp.getUniqueId(), mode.getName(), "arena:" + arena.getIdentifierNoTag()));
    }

    /**
     * Sets the main database used by this plugin
     * mongoClient must be initialized before calling
     *
     * @param databaseName Database Name
     */
    protected final void setPluginDB(String databaseName) {
        pluginDb = mongoClient.getDatabase(databaseName);
    }

    /**
     * Gets the main database used by this plugin
     *
     * @return Mongo Database
     */
    public final MongoDatabase getPluginDB() {
        return pluginDb;
    }

    public static void refreshPlayers(Set<UUID> players) {
        for (PlayerManager<?, ?> playerManager : playerManagers) {
            playerManager.refresh(players);
        }
        for (CorePlugin plugin : plugins) {
            plugin.refresh(players);
        }
    }

    protected void refresh(Set<UUID> players) {

    }

    public static void onBungeeConnect(OfflinePlayer offlinePlayer) {
        for (PlayerManager<?, ?> playerManager : playerManagers) {
            playerManager.onBungeeConnect(offlinePlayer);
        }
    }

    public static void onBungeeDisconnect(UUID uuid) {
        for (PlayerManager<?, ?> playerManager : playerManagers) {
            playerManager.onBungeeDisconnect(uuid);
        }
    }

    public static void onPlayerJoin(Player player) {
        for (PlayerManager<?, ?> playerManager : playerManagers) {
            playerManager.onPlayerJoin(player);
        }

        CorePlayer cp = Core.getInstance().getPlayers().get(player);
        cp.refreshHotbar();
        cp.gotoSpawn();
        PersonalScoreboard.initPlayerScoreboard(cp);
        Core.getInstance().applyVisibilities(cp);
        Core.getInstance().getPartyManager().onConnect(cp);
        NoteBlockMusic.onPlayerJoin(player.getUniqueId());
    }

    public static void onPlayerQuit(UUID uuid) {
        for (PlayerManager<?, ?> playerManager : playerManagers) {
            playerManager.onPlayerQuit(uuid);
        }
    }

    /**
     * Adds a player to the quick ingame player name
     * references list
     *
     * @param cp Core Player
     */
    public static void addIngamePlayerName(CorePlayer cp) {
        ingamePlayerNames.add(cp.getName());
    }

    /**
     * Removes a player name from the quick ingame player name
     * refrences  list
     *
     * @param cp Core Player
     */
    public static void removeIngamePlayerName(CorePlayer cp) {
        ingamePlayerNames.remove(cp.getName());
    }

    /**
     * Returns a set of the names of all ingame players
     * for quick autocompletion
     *
     * @return Ingame Names Set
     */
    public static Set<String> getIngamePlayerNames() {
        return ingamePlayerNames;
    }

    /**
     * Returns a set of all plugins that use CorePlugin and
     * are enabled
     *
     * @return CorePlugins Set
     */
    public static List<CorePlugin> getAllPlugins() {
        return plugins;
    }

    /**
     * Remove a spectator from a battle
     *
     * @param spectator Spectator DBPlayer
     */
    public static void unspectatePlayer(CorePlayer spectator) {
        if (spectator.getBattleState().equals(BattleState.SPECTATOR)) {
            Battle battle = spectator.getBattle();
            battle.removeSpectator(spectator);
        }
    }

    /**
     * Find a player's battle and put the spectator into it, fails
     * if the target player isn't in a battle
     *
     * @param spectator Spectator DBPlayer
     * @param target    Target DBPlayer
     * @return Success
     */
    public static boolean spectatePlayerGlobal(CorePlayer spectator, CorePlayer target) {
        if (target == null) return false;
        /*
        switch (target.getOnlineState()) {
            case OFFLINE:
                Core.getInstance().sendMessage(spectator, Chat.PLAYER_NAME + target.getDisplayName() + Chat.ERROR + " is not online");
                break;
            case HERE:
                if (!spectator.isInBattle() && target.isInBattle()) {
                    target.getBattle().addSpectator(spectator, target);
                    return true;
                } else {
                    Core.getInstance().sendMessage(spectator, Chat.PLAYER_NAME + target.getDisplayNamePossessive() + Chat.ERROR + " game cannot be spectated");
                }
                break;
            case OTHER:
                break;
        }
        */
        Core.getInstance().sendPacket(new PacketSpigotBattleSpectate(spectator, target));
        return false;
    }

    /**
     * Disconnect the Mongo database, catches an error
     * if a jar file was forcibly changed while in use
     * through the windows xcopy command (test reasons)
     */
    public static void closeMongo() {
        if (mongoClient == null) return;
        try {
            mongoClient.close();
        } catch (NoClassDefFoundError e) {
            CoreLogger.logInfo("DEBUG: Jar files updated, unable to close MongoDB");
        }
    }

    public abstract TextComponent getChatPrefix();

    public void challengePlayer(CorePlayer sender, CorePlayer target, BattleMode battleMode, String arenaName) {
        Arena arena = Arenas.get(arenaName, battleMode);
        /*
        Chat.sendRequest(target,
                sender,
                (r, s) -> getBattleManager(battleMode).startMatch(Lists.newArrayList(r, s), arenaName),
                new ComponentBuilder().append(sender.getChatName())
                .append(" has challenged you to a game of ")
                .append(Chat.GAMEMODE + battleMode.getDisplayName())
                .append(" on ")
                .append(Chat.GAMEMAP + arena.getName()).create());
        */
        Core.getInstance().sendPacket(new PacketSpigotBattleChallenge(
                sender.getUniqueId(),
                target.getUniqueId(),
                battleMode.getName(),
                "arena:" + arena.getIdentifierNoTag()));
    }

    /**
     * Send a message to the global channel
     *
     * @param msg Message
     */
    public final void sendMessage(String msg) {
        TextComponent textComponent = getChatPrefix();
        textComponent.addExtra(Chat.colorize(msg));
        Chat.sendMessage(ChatChannel.GLOBAL, textComponent);
    }

    public final void sendMessage(TextComponent text) {
        TextComponent textComponent = getChatPrefix();
        textComponent.addExtra(text);
        Chat.sendMessage(ChatChannel.GLOBAL, textComponent);
    }

    public final void sendMessageFriends(TextComponent text, ChatChannel channel, Set<UUID> players) {
        TextComponent textComponent = getChatPrefix();
        textComponent.addExtra(text);
        Chat.sendMessageFriends(channel, text, players);
    }

    /**
     * Send a message to the global channel with a list of players that can't receive it
     *
     * @param msg Message
     */
    public final void sendMessageBlacklisted(String msg, Set<UUID> blacklist) {
        TextComponent textComponent = getChatPrefix();
        textComponent.addExtra(Chat.colorize(msg));
        Chat.sendMessage(ChatChannel.GLOBAL, textComponent, blacklist);
    }

    /**
     * Send a message to the global channel with a list of players that can't receive it
     *
     * @param msg Message
     */
    public final void sendMessageBlacklisted(TextComponent msg, Set<UUID> blacklist) {
        TextComponent textComponent = getChatPrefix();
        textComponent.addExtra(msg);
        Chat.sendMessage(ChatChannel.GLOBAL, textComponent, blacklist);
    }

    /**
     * Send a message to a specific player
     *
     * @param cp  Core Player
     * @param msg Message
     */
    public final void sendMessage(CorePlayer cp, String msg) {
        TextComponent message = getChatPrefix();
        message.addExtra(Chat.colorize(msg));
        Chat.sendMessageToPlayer(cp, message);
    }

    public final void sendMessage(CorePlayer cp, TextComponent text) {
        TextComponent message = new TextComponent(getChatPrefix());
        message.addExtra(text);
        Chat.sendMessageToPlayer(cp, message);
    }

    /**
     * Send a message to a CommandSender (Block, Console, etc)
     *
     * @param cs  CommandSender
     * @param msg Message
     */
    public final void sendMessage(CommandSender cs, String msg) {
        TextComponent message = new TextComponent(getChatPrefix());
        message.addExtra(msg);
        cs.sendMessage(message.toPlainText());
    }

    /**
     * Doesn't use default chat prefix
     * <br>Send message to a ChatChannel
     *
     * @param cc  Chat Channel
     * @param msg Message
     */
    public final void sendMessage(ChatChannel cc, String msg) {
        TextComponent textComponent = getChatPrefix();
        textComponent.addExtra(Chat.colorize(msg));
        Chat.sendMessage(cc, textComponent);
    }

    /**
     * Doesn't use default chat prefix
     * <br>Send message to a ChatChannel with a list of players that can't receive it
     *
     * @param cc        Chat Channel
     * @param msg       Message
     * @param blacklist Blacklist of Players
     */
    public final void sendMessageBlacklisted(ChatChannel cc, String msg, Set<UUID> blacklist) {
        TextComponent textComponent = getChatPrefix();
        textComponent.addExtra(Chat.colorize(msg));
        Chat.sendMessage(cc, textComponent, blacklist);
    }

}
