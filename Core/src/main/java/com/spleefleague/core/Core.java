package com.spleefleague.core;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.collect.Lists;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.chat.ticket.Tickets;
import com.spleefleague.core.command.CommandManager;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.game.leaderboard.Leaderboards;
import com.spleefleague.core.menu.hotbars.AfkHotbar;
import com.spleefleague.core.menu.hotbars.HeldItemHotbar;
import com.spleefleague.core.menu.hotbars.SLMainHotbar;
import com.spleefleague.core.menu.hotbars.main.credits.Credits;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.infraction.Infraction;
import com.spleefleague.core.listener.*;
import com.spleefleague.core.player.party.Party;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.CorePlayerOptions;
import com.spleefleague.core.player.PlayerManager;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.queue.PlayerQueue;
import com.spleefleague.core.queue.QueueManager;
import com.spleefleague.core.queue.QueueRunnable;
import com.spleefleague.core.request.RequestManager;
import com.spleefleague.core.util.variable.Warp;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.spleefleague.core.vendor.Vendors;
import com.spleefleague.core.world.FakeWorld;
import com.spleefleague.core.world.build.BuildWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.reflections.Reflections;

/**
 * SpleefLeague's Core Plugin
 *
 * @author NickM13
 */
public class Core extends CorePlugin<CorePlayer> {

    public static World DEFAULT_WORLD;

    private static Core instance;

    private QueueManager queueManager;
    private QueueRunnable qrunnable;

    // Command manager, contains list of all commands
    // and registers them to the server
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
        setPluginDB("SpleefLeague");

        Credits.init();
        Rank.init();
        Chat.init();
        Warp.init();
        Infraction.init();
        Collectible.init();
        Vendors.init();
        Tickets.init();
        CorePlayerOptions.init();
        FakeWorld.init();
        Arenas.init();

        // Initialize listeners
        initListeners();

        // Initialize manager
        playerManager = new PlayerManager<>(this, CorePlayer.class, getPluginDB().getCollection("Players"));
        commandManager = new CommandManager();

        // Initialize various things
        initQueues();
        initCommands();
        initMenus();
        initTabList();

        Leaderboards.init();

        // TODO: Move this?
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (CorePlayer cp : getPlayers().getAll()) {
                cp.checkAfk();
                cp.checkBiome();
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
        BuildWorld.close();
        Warp.close();
        Infraction.close();
        Collectible.close();
        Vendors.close();
        Tickets.close();
        Leaderboards.close();
        qrunnable.close();
        playerManager.close();
        running = false;
        ProtocolLibrary.getPlugin().onDisable();
        CorePlugin.closeMongo();
    }

    public static Core getInstance() {
        return instance;
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
    }
    
    /**
     * Initialize commands, uses a reflection library in Core to copy
     * all commands from the commands package
     *
     * Does not work with sub-plugins
     */
    private void initCommands() {
        Reflections reflections = new Reflections("com.spleefleague.core.command.commands");
        
        Set<Class<? extends CommandTemplate>> subTypes = reflections.getSubTypesOf(CommandTemplate.class);
        
        subTypes.forEach(st -> {
            try {
                addCommand(st.getConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException
                    | NoSuchMethodException | InvocationTargetException exception) {
                Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, exception);
            }
        });

        commandManager.flushRegisters();
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
    public void returnToWorld(CorePlayer cp) {
        if (cp.isVanished()) {
            cp.getPlayer().hidePlayer(Core.getInstance(), cp.getPlayer());
        } else {
            cp.getPlayer().showPlayer(Core.getInstance(), cp.getPlayer());
        }
        // TODO: Probably better way to do this, trying to avoid as much spigot as possible
        // See and become visible to all players outside of games
        // getOnline doesn't return vanished players
        for (CorePlayer cp2 : getPlayers().getAll()) {
            if (!cp.equals(cp2)) {
                if (cp.getBattle() == cp2.getBattle()
                        && cp.getBuildWorld() == cp2.getBuildWorld()) {
                    cp.getPlayer().showPlayer(this, cp2.getPlayer());
                    if (!cp.isVanished())   cp2.getPlayer().showPlayer(this, cp.getPlayer());
                    else                    cp2.getPlayer().hidePlayer(this, cp.getPlayer());
                } else {
                    cp.getPlayer().hidePlayer(this, cp2.getPlayer());
                    cp2.getPlayer().hidePlayer(this, cp.getPlayer());
                }
            }
        }
    }

    /**
     * Initialize Tab List packet listener that prevents players from
     * being removed upon entering a game and becoming invisible, and
     * removes players from Tab List when they become vanished
     */
    public void initTabList() {
        Core.getProtocolManager().addPacketListener(new PacketAdapter(Core.getInstance(), PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent pe) {
                if (pe.getPacketType() == PacketType.Play.Server.PLAYER_INFO) {
                    PacketContainer packet = pe.getPacket();
                    CorePlayer cp;
                    switch (packet.getPlayerInfoAction().read(0)) {
                        case ADD_PLAYER:
                            cp = Core.getInstance().getPlayers().get(packet.getPlayerInfoDataLists().read(0).get(0).getProfile().getUUID());
                            if (!cp.isVanished()) {
                                packet.getPlayerInfoDataLists().write(0, Lists.newArrayList(new PlayerInfoData(
                                        packet.getPlayerInfoDataLists().read(0).get(0).getProfile(),
                                        packet.getPlayerInfoDataLists().read(0).get(0).getLatency(),
                                        packet.getPlayerInfoDataLists().read(0).get(0).getGameMode(),
                                        WrappedChatComponent.fromText(cp.getDisplayName()))));
                            } else {
                                pe.setCancelled(true);
                            }
                            break;
                        case REMOVE_PLAYER:
                            cp = Core.getInstance().getPlayers().get(packet.getPlayerInfoDataLists().read(0).get(0).getProfile().getUUID());
                            if (cp != null && cp.isOnline() && !cp.isVanished()) {
                                pe.setCancelled(true);
                            }
                            break;
                        default: break;
                    }
                }
            }
        });
    }

    /**
     * @return Protocol Manager
     */
    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }
    
    /**
     * Sends a packet to a single player
     *
     * @param p Player
     * @param packet Packet Container
     */
    public static void sendPacket(Player p, PacketContainer packet) {
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
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
            try {
                if (cp.getPlayer() != null) protocolManager.sendServerPacket(cp.getPlayer(), packet);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
            }
        }, 1L);
    }

    /**
     * Sends a packet to all online players
     *
     * @param packet Packet Container
     */
    public static void sendPacketAll(PacketContainer packet) {
        for (CorePlayer cp : Core.getInstance().getPlayers().getAll()) {
            sendPacket(cp, packet);
        }
    }

    /**
     * Initialize queue runnable thread
     */
    private void initQueues() {
        queueManager = new QueueManager();
        queueManager.initialize();

        qrunnable = new QueueRunnable();
        Bukkit.getScheduler().runTaskTimer(getInstance(), qrunnable, 0L, qrunnable.getDelayTicks());
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

        for (CorePlayer cp1 : playerManager.getAll()) {
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
    public void addCommand(CommandTemplate command) {
        commandManager.addCommand(command);
    }

    /**
     * Push all newly registered commands through
     */
    public void flushCommands() {
        commandManager.flushRegisters();
    }

    /**
     * @return Chat Prefix
     */
    @Override
    public String getChatPrefix() {
        return Chat.TAG_BRACE + "[" + Chat.TAG + "SpleefLeague" + Chat.TAG_BRACE + "] " + Chat.DEFAULT;
    }

    /**
     * Secretly mute a player
     *
     * @param sender Name of punisher
     * @param target OfflinePlayer
     * @param millis Time in milliseconds
     * @param reason Reason
     */
    public void muteSecret(String sender, OfflinePlayer target, long millis, String reason) {
        Infraction infraction = new Infraction();
        infraction.setUuid(target.getUniqueId())
                .setPunisher(sender)
                .setType(Infraction.Type.MUTE_SECRET)
                .setDuration(millis)
                .setReason(reason);
        Infraction.create(infraction);
        
        Core.getInstance().sendMessage(ChatChannel.getChannel(ChatChannel.Channel.STAFF),
                "Secretly muted player " + target.getName() + " for " + infraction.getRemainingTimeString() + (reason.length() > 0 ? (": " + reason) : ""));
    }

    /**
     * Publicly mute a player
     *
     * @param sender Name of punisher
     * @param target OfflinePlayer
     * @param millis Time in milliseconds
     * @param reason Reason
     */
    public void mutePublic(String sender, OfflinePlayer target, long millis, String reason) {
        Infraction infraction = new Infraction();
        infraction.setUuid(target.getUniqueId())
                .setPunisher(sender)
                .setType(Infraction.Type.MUTE_PUBLIC)
                .setDuration(millis)
                .setReason(reason);
        Infraction.create(infraction);
        
        if (target.isOnline()) {
            sendMessage(Core.getInstance().getPlayers().get(target.getPlayer()), "Muted by " + sender + " for " + infraction.getRemainingTimeString() + ": " + reason);
        }
        Core.getInstance().sendMessage(ChatChannel.getChannel(ChatChannel.Channel.STAFF),
                "Public muted player " + target.getName() + " for " + infraction.getRemainingTimeString() + (reason.length() > 0 ? (": " + reason) : ""));
    }

    /**
     * Unmute a player
     *
     * @param sender Name of punisher
     * @param target OfflinePlayer
     * @param reason Reason
     */
    public void unmute(String sender, OfflinePlayer target, String reason) {
        Infraction infraction = new Infraction();
        infraction.setUuid(target.getUniqueId())
                .setPunisher(sender)
                .setType(Infraction.Type.MUTE_SECRET)
                .setDuration(0)
                .setReason(reason);
        Infraction.create(infraction);
        
        if (target.isOnline()) {
            sendMessage(Core.getInstance().getPlayers().get(target.getPlayer()), "You've been unmuted by " + sender);
        }
        Core.getInstance().sendMessage(ChatChannel.getChannel(ChatChannel.Channel.STAFF),
                "Unmuted player " + target.getName() + (reason.length() > 0 ? (": " + reason) : ""));
    }

    /**
     * Temporarily ban a player from the server
     *
     * @param sender Name of punisher
     * @param target OfflinePlayer
     * @param millis Time in milliseconds
     * @param reason Reason
     */
    public void tempban(String sender, OfflinePlayer target, long millis, String reason) {
        if (target == null) return;
        Infraction infraction = new Infraction();
        infraction.setUuid(target.getUniqueId())
                .setPunisher(sender)
                .setType(Infraction.Type.TEMPBAN)
                .setDuration(millis)
                .setReason(reason);
        Infraction.create(infraction);

        if (target.isOnline()) {
            Player player = (Player) target;
            if (player.getLocation().getWorld() != null) {
                player.getLocation().getWorld().strikeLightning(((Player) target).getLocation());
                player.kickPlayer("TempBan for " + infraction.getRemainingTimeString() + ": " + reason + "!");
            }
        }
        Core.getInstance().sendMessage(ChatChannel.getChannel(ChatChannel.Channel.STAFF),
                "TempBanned player " + target.getName() + " for " + infraction.getRemainingTimeString() + (reason.length() > 0 ? (": " + reason) : ""));
    }

    /**
     * Ban a player from the server
     *
     * @param sender Name of punisher
     * @param target OfflinePlayer
     * @param reason Reason
     */
    public void ban(String sender, OfflinePlayer target, String reason) {
        Infraction infraction = new Infraction();
        infraction.setUuid(target.getUniqueId())
                .setPunisher(sender)
                .setType(Infraction.Type.BAN)
                .setDuration(0)
                .setReason(reason);
        Infraction.create(infraction);
    
        Player player = (Player) target;
        if (player.getLocation().getWorld() != null) {
            player.getLocation().getWorld().strikeLightning(((Player) target).getLocation());
            player.kickPlayer("Banned: " + reason + "!");
        }
        Core.getInstance().sendMessage(ChatChannel.getChannel(ChatChannel.Channel.STAFF), "Banned player " + target.getName() + (reason.length() > 0 ? (": " + reason) : ""));
    }

    /**
     * Unban a player from the server
     *
     * @param sender Name of punisher
     * @param target OfflinePlayer
     * @param reason Reason
     */
    public void unban(String sender, OfflinePlayer target, String reason) {
        Infraction infraction = new Infraction();
        infraction.setUuid(target.getUniqueId())
                .setPunisher(sender)
                .setType(Infraction.Type.UNBAN)
                .setDuration(0)
                .setReason(reason);
        Infraction.create(infraction);

        Core.getInstance().sendMessage(ChatChannel.getChannel(ChatChannel.Channel.STAFF), "Unbanned player " + target.getName() + (reason.length() > 0 ? (": " + reason) : ""));
    }

    /**
     * Kick a player from the server
     *
     * @param sender Name of punisher
     * @param target OfflinePlayer
     * @param reason Reason
     */
    public void kick(String sender, OfflinePlayer target, String reason) {
        Infraction infraction = new Infraction();
        infraction.setUuid(target.getUniqueId())
                .setPunisher(sender)
                .setType(Infraction.Type.WARNING)
                .setDuration(0)
                .setReason(reason);
        Infraction.create(infraction);
        
        if (target.isOnline()) {
            Player player = (Player) target;
            if (player.getLocation().getWorld() != null) {
                player.getLocation().getWorld().strikeLightning(((Player) target).getLocation());
                player.kickPlayer("Kicked: " + reason + "!");
            }
        }
        Core.getInstance().sendMessage(ChatChannel.getChannel(ChatChannel.Channel.STAFF), "Kicked player " + target.getName() + (reason.length() > 0 ? (": " + reason) : ""));
    }

    /**
     * Send a warning to a player
     * Also used for post-ban information
     *
     * @param sender Name of punisher
     * @param target OfflinePlayer
     * @param reason Reason
     */
    public void warn(String sender, OfflinePlayer target, String reason) {
        Infraction infraction = new Infraction();
        infraction.setUuid(target.getUniqueId())
                .setPunisher(sender)
                .setType(Infraction.Type.WARNING)
                .setDuration(0)
                .setReason(reason);
        Infraction.create(infraction);
        
        if (target.isOnline()) {
            sendMessage(Core.getInstance().getPlayers().get(target.getPlayer()), "Warning from " + sender + ": " + reason);
        }
        Core.getInstance().sendMessage(ChatChannel.getChannel(ChatChannel.Channel.STAFF), "Warned player " + target.getName() + (reason.length() > 0 ? (": " + reason) : ""));
    }

    /**
     * Send a message from one player to another
     *
     * @param sender CorePlayer
     * @param target CorePlayer
     * @param msg Message
     */
    public void sendTell(CorePlayer sender, CorePlayer target, String msg) {
        sender.sendMessage(Chat.DEFAULT + "[me -> " + target.getDisplayName() + "] " + Chat.WHISPER + msg);
        target.sendMessage(Chat.DEFAULT + "[" + sender.getDisplayName() + " -> me] " + Chat.WHISPER + msg);
        target.setReply(sender.getPlayer());
    }

    /**
     * Send a title to all players, stay is based on how long message is
     * Used by /broadcast command
     *
     * @param msg Message
     */
    public void broadcast(String msg) {
        String title, subtitle;
        String[] msgs = msg.split("/");
        title = msgs[0];
        subtitle = msgs.length > 1 ? msgs[1] : "";
        Chat.sendTitle(ChatChannel.getDefaultChannel(), Chat.BROADCAST + title, Chat.BROADCAST + subtitle, 5, msg.length() * 2 + 10, 15);
    }

}
