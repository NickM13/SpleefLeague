package com.spleefleague.core;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.chat.ticket.Tickets;
import com.spleefleague.core.command.CommandManager;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.game.leaderboard.Leaderboards;
import com.spleefleague.core.listener.*;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.menu.hotbars.AfkHotbar;
import com.spleefleague.core.menu.hotbars.HeldItemHotbar;
import com.spleefleague.core.menu.hotbars.SLMainHotbar;
import com.spleefleague.core.menu.hotbars.main.credits.Credits;
import com.spleefleague.core.music.NoteBlockMusic;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.CorePlayerOptions;
import com.spleefleague.core.player.PlayerManager;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.infraction.Infraction;
import com.spleefleague.core.player.party.Party;
import com.spleefleague.core.player.rank.Ranks;
import com.spleefleague.core.player.scoreboard.PersonalScoreboard;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.queue.PlayerQueue;
import com.spleefleague.core.queue.QueueManager;
import com.spleefleague.core.request.RequestManager;
import com.spleefleague.core.util.PacketUtils;
import com.spleefleague.core.util.variable.Warp;
import com.spleefleague.core.vendor.Vendors;
import com.spleefleague.core.world.FakeWorld;
import com.spleefleague.core.world.build.BuildWorld;
import com.spleefleague.core.world.global.zone.GlobalZone;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.utils.packet.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.spigot.PacketHub;
import com.spleefleague.coreapi.utils.packet.spigot.PacketTellSpigot;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SpleefLeague's Core Plugin
 *
 * @author NickM13
 */
public class Core extends CorePlugin<CorePlayer> {

    private static Core instance;
    public static World DEFAULT_WORLD;
    private QueueManager queueManager;
    private Leaderboards leaderboards;
    private CommandManager commandManager;
    
    // For packet managing
    private static ProtocolManager protocolManager;
    
    /**
     * Called when the plugin is enabling
     */
    @Override
    public void init() {
        instance = this;
        DEFAULT_WORLD = Bukkit.getWorlds().get(0);
        protocolManager = ProtocolLibrary.getProtocolManager();
    
        CorePlugin.initMongo();
        initConfig();
        setPluginDB("SpleefLeague");

        Credits.init();
        Ranks.init();
        Chat.init();
        Warp.init();
        Infraction.init();
        Collectible.init();
        Vendors.init();
        Tickets.init();
        CorePlayerOptions.init();
        FakeWorld.init();
        Arenas.init();
        NoteBlockMusic.init();
        GlobalZone.init();
        PersonalScoreboard.init();

        // Initialize manager
        playerManager = new PlayerManager<>(this, CorePlayer.class, getPluginDB().getCollection("Players"));
        commandManager = new CommandManager();

        // Initialize listeners
        initListeners();

        // Initialize various things
        initQueues();
        initCommands();
        initMenus();
        initTabList();

        leaderboards = new Leaderboards();

        // TODO: Move this?
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (CorePlayer cp : getPlayers().getOnline()) {
                cp.checkAfk();
                cp.checkTempRanks();
                cp.checkGlobalSpectate();
            }
            RequestManager.checkTimeouts();
        }, 0L, 100L);
    }
    
    /**
     * Called when the plugin is disabling
     */
    @Override
    public void close() {
        for (BukkitTask task : taskList) {
            task.cancel();
        }
        BuildWorld.close();
        Warp.close();
        Infraction.close();
        Collectible.close();
        Vendors.close();
        Tickets.close();
        leaderboards.close();
        NoteBlockMusic.close();
        playerManager.close();
        running = false;
        protocolManager.removePacketListeners(Core.getInstance());
        ProtocolLibrary.getPlugin().onDisable();
        CorePlugin.closeMongo();
    }

    public static Core getInstance() {
        return instance;
    }

    @Override
    public void refreshPlayers(Set<UUID> players) {
        super.refreshPlayers(players);
        leaderboards.refresh(players);
    }

    private static List<BukkitTask> taskList = new ArrayList<>();

    public void addTask(BukkitTask task) {
        taskList.add(task);
    }
    
    /**
     * Initialize Bukkit event listener objects
     */
    private void initListeners() {
        Bukkit.getPluginManager().registerEvents(new AfkListener(), this);
        Bukkit.getPluginManager().registerEvents(new BattleListener(), this);
        Bukkit.getPluginManager().registerEvents(new BuildListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new ConnectionListener(), this);
        Bukkit.getPluginManager().registerEvents(new EnvironmentListener(), this);
        Bukkit.getPluginManager().registerEvents(new MenuListener(), this);

        getServer().getMessenger().registerOutgoingPluginChannel(this, "slcore:spigot");

        getServer().getMessenger().registerIncomingPluginChannel(this, "slcore:bungee", new BungeePluginListener());
    }
    
    /**
     * Initialize commands, uses a reflection library in Core to copy
     * all commands from the commands package
     *
     * Does not work with sub-plugins
     */
    private void initCommands() {
        List<ClassLoader> classLoadersList = new LinkedList<>();
        classLoadersList.add(ClasspathHelper.staticClassLoader());

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(true), new ResourcesScanner())
                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("com.spleefleague.core.command.commands"))));

        initCommands(reflections);
    }

    public void initCommands(Reflections reflections) {
        Set<Class<? extends CoreCommand>> subTypes = reflections.getSubTypesOf(CoreCommand.class);

        subTypes.forEach(st -> {
            try {
                addCommand(st.getConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException
                    | NoSuchMethodException | InvocationTargetException exception) {
                //CoreLogger.logError(exception);
            }
        });
    }

    public enum ServerType {
        LOBBY,
        MINIGAME
    }
    private ServerType serverType = ServerType.MINIGAME;

    /**
     * Parse plugin's config.yml file
     */
    public void initConfig() {
        FileConfiguration config = this.getConfig();
        if (config.getString("serverType") == null) {
            CoreLogger.logError("Null Server Type");
            config.set("serverType", serverType.name());
        } else {
            serverType = ServerType.valueOf(config.getString("serverType"));
        }
        config.options().copyDefaults(false);
        saveConfig();
    }

    public ServerType getServerType() {
        return serverType;
    }

    /**
     * Initialize menus
     */
    private void initMenus() {
        AfkHotbar.init();
        SLMainHotbar.init();
        HeldItemHotbar.init();
    }

    /**
     * Hide/show players based on in-game state and
     * vanished state
     *
     * @param cp Core Player
     */
    public void applyVisibilities(CorePlayer cp) {
        if (cp == null || cp.getOnlineState() != DBPlayer.OnlineState.HERE) return;
        if (cp.isVanished() || cp.isGhosting()) {
            cp.getPlayer().hidePlayer(Core.getInstance(), cp.getPlayer());
        } else {
            cp.getPlayer().showPlayer(Core.getInstance(), cp.getPlayer());
        }
        // See and become seen by all players outside of games
        // getOnline doesn't return vanished players
        for (CorePlayer cp2 : getPlayers().getOnline()) {
            if (!cp.equals(cp2)) {
                if (cp.getBattle() == cp2.getBattle() &&
                        cp.getBuildWorld() == cp2.getBuildWorld()) {
                    if (!cp2.isGhosting()) cp.getPlayer().showPlayer(this, cp2.getPlayer());
                    else                   cp.getPlayer().hidePlayer(this, cp2.getPlayer());
                    if (!cp.isVanished() && !cp.isGhosting()) cp2.getPlayer().showPlayer(this, cp.getPlayer());
                    else                                      cp2.getPlayer().hidePlayer(this, cp.getPlayer());
                } else {
                    cp.getPlayer().hidePlayer(this, cp2.getPlayer());
                    cp2.getPlayer().hidePlayer(this, cp.getPlayer());
                }
            }
        }
    }

    public void returnToHub(CorePlayer cp) {
        if (cp == null) return;
        /*
        for (CorePlugin<?> plugin : CorePlugin.getAllPlugins()) {
            plugin.getPlayers().saveForTransfer(cp.getUniqueId());
        }
        */
        Core.getInstance().sendPacket(new PacketHub(Lists.newArrayList(cp)));
    }

    public void onBungeeConnect(UUID uuid) {
        CorePlayer cp = getPlayers().get(uuid);
        if (cp == null || cp.isVanished() || cp.getOnlineState() == DBPlayer.OnlineState.OFFLINE) return;
        //Core.sendPacketAll(PacketUtils.createAddPlayerPacket(Lists.newArrayList(cp)));
        PersonalScoreboard.onPlayerJoin(cp);

    }

    public void onBungeeDisconnect(UUID uuid) {
        CorePlayer cp = getPlayers().get(uuid);
        if (cp == null || cp.getOnlineState() == DBPlayer.OnlineState.OFFLINE) {
            //Core.sendPacketAll(PacketUtils.createRemovePlayerPacket(Lists.newArrayList(uuid)));
            PersonalScoreboard.onPlayerQuit(uuid);
        }
    }

    /**
     * Initialize Tab List packet listener that prevents players from
     * being removed upon entering a game and becoming invisible, and
     * removes players from Tab List when they become vanished
     */
    public void initTabList() {
        addProtocolPacketAdapter(new PacketAdapter(Core.getInstance(), PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent pe) {
                pe.setCancelled(true);
                /*
                if (pe.getPacketType() == PacketType.Play.Server.PLAYER_INFO) {
                    PacketContainer packet = pe.getPacket();
                    switch (packet.getPlayerInfoAction().read(0)) {
                        case ADD_PLAYER: {
                            List<PlayerInfoData> newData = new ArrayList<>();
                            for (PlayerInfoData playerInfoData : packet.getPlayerInfoDataLists().read(0)) {
                                CorePlayer cp = Core.getInstance().getPlayers().get(playerInfoData.getProfile().getUUID());
                                if (!cp.isVanished()) {
                                    newData.add(new PlayerInfoData(
                                            playerInfoData.getProfile(),
                                            playerInfoData.getLatency(),
                                            playerInfoData.getGameMode(),
                                            WrappedChatComponent.fromText(cp.getDisplayName())));
                                }
                            }
                            if (newData.isEmpty()) {
                                pe.setCancelled(true);
                            } else {
                                packet.getPlayerInfoDataLists().write(0, newData);
                            }
                        }
                            break;
                        case REMOVE_PLAYER: {
                            List<PlayerInfoData> newData = new ArrayList<>();
                            for (PlayerInfoData playerInfoData : packet.getPlayerInfoDataLists().read(0)) {
                                CorePlayer cp = Core.getInstance().getPlayers().get(playerInfoData.getProfile().getUUID());
                                if (cp == null || cp.getOnlineState() == DBPlayer.OnlineState.OFFLINE || cp.isVanished()) {
                                    newData.add(playerInfoData);
                                }
                            }
                            if (newData.isEmpty()) {
                                pe.setCancelled(true);
                            } else {
                                packet.getPlayerInfoDataLists().write(0, newData);
                            }
                        }
                            break;
                        default: break;
                    }
                }
                 */
            }
        });
    }

    public static void addProtocolPacketAdapter(PacketAdapter packetAdapter) {
        protocolManager.addPacketListener(packetAdapter);
    }
    
    /**
     * Sends a packet to a single player
     *
     * @param p Player
     * @param packet Packet Container
     */
    public static void sendPacket(@NotNull Player p, PacketContainer packet) {
        if (packet == null) return;
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            try {
                protocolManager.sendServerPacket(p, packet);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
            }
        }, 1L);
    }
    
    /**
     * Sends a packet to a single player
     *
     * @param cp Core Player
     * @param packet Packet Container
     */
    public static void sendPacket(CorePlayer cp, PacketContainer packet) {
        if (packet == null) return;
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            try {
                if (cp.getPlayer() != null) protocolManager.sendServerPacket(cp.getPlayer(), packet);
            } catch (InvocationTargetException e) {
                CoreLogger.logError(e);
            }
        }, 1L);
    }

    public static void sendPacketSilently(@NotNull Player p, PacketContainer packet) {
        sendPacketSilently(p, packet, 1L);
    }
    public static void sendPacketSilently(@NotNull Player p, PacketContainer packet, long delay) {
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            try {
                protocolManager.sendServerPacket(p, packet, null, false);
            } catch (InvocationTargetException e) {
                CoreLogger.logError(e);
            }
        }, delay);
    }

    /**
     * Sends a packet to all online players
     *
     * @param packet Packet Container
     */
    public static void sendPacketAll(PacketContainer packet) {
        if (packet == null) return;
        for (CorePlayer cp : Core.getInstance().getPlayers().getOnline()) {
            sendPacket(cp, packet);
        }
    }

    /**
     * Initialize queue runnable thread
     */
    private void initQueues() {
        queueManager = new QueueManager();
        queueManager.initialize();

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            queueManager.getQueues().forEach(PlayerQueue::checkQueue);
        }, 0L, 20L);
    }

    /**
     * Add a queue to the Queue Manager
     *
     * @param queue Player Queue
     */
    public void addQueue(PlayerQueue queue) {
        queueManager.addQueue(queue);
    }

    /**
     * Unqueue a player from all Queues
     *
     * @param cp Core Player
     * @return Success
     */
    public boolean unqueuePlayerGlobally(CorePlayer cp) {
        boolean unqueued = false;
        for (PlayerQueue pq : queueManager.getQueues()) {
            unqueued = unqueued || pq.unqueuePlayer(cp);
        }
        return unqueued;
    }

    /**
     * Unqueue a party from all Queues
     * (Really just removes the lead player from the queues)
     *
     * @param party Party
     * @return If player was unqueued
     */
    public boolean unqueuePartyGlobally(Party party) {
        boolean unqueued = false;
        for (PlayerQueue pq : queueManager.getQueues()) {
            if (pq.isTeamQueue()) {
                unqueued = unqueued || pq.unqueuePlayer(party.getOwner());
            }
        }
        return unqueued;
    }

    /**
     * @return Queue Manager
     */
    public QueueManager getQueueManager() {
        return queueManager;
    }

    public Leaderboards getLeaderboards() {
        return leaderboards;
    }

    /**
     * Returns list of players that are less than maxDist
     * and further than minDist from location
     *
     * @param loc Location
     * @param minDist Minimum Distance
     * @param maxDist Maximum Distance
     * @return Player List
     */
    public List<CorePlayer> getPlayersInRadius(Location loc, Double minDist, Double maxDist) {
        List<CorePlayer> cpList = new ArrayList<>();

        for (CorePlayer cp1 : playerManager.getOnline()) {
            if (loc.getWorld() != null
                    && loc.getWorld().equals(cp1.getLocation().getWorld())
                    && loc.distance(cp1.getLocation()) >= minDist
                    && loc.distance(cp1.getLocation()) <= maxDist) {
                boolean inserted = false;
                for (int i = 0; i < cpList.size(); i++) {
                    CorePlayer cp2 = cpList.get(i);
                    if (loc.distance(cp1.getLocation()) < loc.distance(cp2.getLocation())) {
                        cpList.add(i, cp1);
                        inserted = true;
                        break;
                    }
                }
                if (!inserted) {
                    cpList.add(cp1);
                }
            }
        }
        
        return cpList;
    }

    /**
     * Register a command to Command Manager
     *
     * @param command CommandTemplate
     */
    public void addCommand(CoreCommand command) {
        commandManager.addCommand(command);
    }

    /**
     * @return Chat Prefix
     */
    @Override
    public String getChatPrefix() {
        return Chat.TAG_BRACE + "[" + Chat.TAG + "SpleefLeague" + Chat.TAG_BRACE + "] " + Chat.DEFAULT;
    }

    /**
     * Send a packet to all servers with 1 or more players
     *
     * @param output
     */
    public void sendPacket(PacketSpigot output) {
        Player sender = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        if (sender != null) {
            sender.sendPluginMessage(Core.getInstance(), "slcore:spigot", output.toByteArray());
        }
    }

}
