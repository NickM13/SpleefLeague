package com.spleefleague.core.player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mongodb.lang.NonNull;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.chat.ChatGroup;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.battle.bonanza.BonanzaBattle;
import com.spleefleague.core.infraction.CoreInfractionManager;
import com.spleefleague.core.menu.InventoryMenuItemHotbar;
import com.spleefleague.core.music.NoteBlockMusic;
import com.spleefleague.core.player.crates.CorePlayerCrates;
import com.spleefleague.core.player.friends.CoreFriendsList;
import com.spleefleague.core.player.options.CorePlayerOptions;
import com.spleefleague.core.player.party.CoreParty;
import com.spleefleague.core.player.purse.CorePlayerPurse;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.player.rank.CoreTempRank;
import com.spleefleague.core.player.ratings.CorePlayerRatings;
import com.spleefleague.core.player.scoreboard.PersonalScoreboard;
import com.spleefleague.core.player.statistics.CorePlayerStatistics;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.util.variable.Checkpoint;
import com.spleefleague.core.util.variable.CoreLocation;
import com.spleefleague.core.util.variable.TpCoord;
import com.spleefleague.core.util.variable.Warp;
import com.spleefleague.core.world.ChunkCoord;
import com.spleefleague.core.world.FakeWorld;
import com.spleefleague.core.world.build.BuildWorld;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.infraction.Infraction;
import com.spleefleague.coreapi.infraction.InfractionManager;
import com.spleefleague.coreapi.infraction.InfractionType;
import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChatChannelJoin;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;

/**
 * @author NickM13
 * @since 2/19/2021
 */
public class CorePlayer extends CoreOfflinePlayer {

    /**
     * Non-database variables
     */
    // Current party the player belongs to
    private Boolean ghosting = false;

    @DBField private String gameMode = "ADVENTURE";
    @DBField private final CorePlayerPurse purse = new CorePlayerPurse(this);
    @DBField private final CorePlayerCollectibles collectibles = new CorePlayerCollectibles(this);
    @DBField private final CorePlayerCrates crates = new CorePlayerCrates(this);
    @DBField private final CoreFriendsList friends = new CoreFriendsList(this);
    @DBField private final CorePlayerRatings ratings = new CorePlayerRatings(this);
    @DBField private final CorePlayerStatistics statistics = new CorePlayerStatistics(this);
    @DBField private final CorePlayerOptions options = new CorePlayerOptions(this);

    // Current selected chat channel to send messages in
    @DBField private ChatChannel chatChannel;

    // 5 min, sets player to afk
    private static final long AFK_WARNING = 1000L * 60 * 4 + 30;
    private static final long AFK_TIMEOUT = 1000L * 60 * 5;
    // Last action millis
    private long lastAction;
    private boolean afk;
    private boolean afkWarned;

    private CoreLocation lastLocation;
    private Checkpoint checkpoint;

    private Infraction mute = null;

    private PermissionAttachment permissions;

    private final List<FakeWorld<?>> fakeWorlds = new ArrayList<>();
    private Battle<?> battle;
    private BattleState battleState;

    private final Map<Integer, ChatGroup> chatGroups = new HashMap<>();

    private final CorePlayerMenu menu = new CorePlayerMenu(this);

    public CorePlayer() {
        this.lastAction = System.currentTimeMillis();
        this.chatChannel = ChatChannel.GLOBAL;
        this.lastAction = System.currentTimeMillis();
        this.afk = false;
        this.afkWarned = false;
        this.lastLocation = null;
        this.checkpoint = null;
        this.battle = null;
        this.battleState = BattleState.NONE;
    }

    @Override
    public void newPlayer(UUID uuid, String username) {
        super.newPlayer(uuid, username);
    }

    @Override
    public void init() {
        getPlayer().addAttachment(Core.getInstance());
        permissions = getPlayer().addAttachment(Core.getInstance());
        super.init();
        FakeWorld.getGlobalFakeWorld().addPlayer(this);
        getPlayer().setCollidable(false);
        getPlayer().setGravity(true);
        getPlayer().getActivePotionEffects().forEach(pe -> getPlayer().removePotionEffect(pe.getType()));
        setGameMode(GameMode.valueOf(gameMode));
        refreshHotbar();
        updateMute();
    }

    @Override
    public void preInit() {
        super.init();
    }

    @Override
    public void initOffline() {
        super.initOffline();
    }

    @Override
    public void close() {
        if (battle != null) {
            battle.onDisconnect(this);
        }

        List<FakeWorld<?>> fakeWorldList = new ArrayList<>(fakeWorlds);
        for (FakeWorld<?> fakeWorld : fakeWorldList) {
            fakeWorld.removePlayer(this);
        }

        PersonalScoreboard.closePlayerScoreboard(this);

        Core.getInstance().unqueuePlayerGlobally(this);

        NoteBlockMusic.stopSong(getUniqueId());

        Team team = getPlayer().getScoreboard().getEntryTeam(getPlayer().getName());
        if (team != null) team.removeEntry(getPlayer().getName());

        Document leaveDoc = new Document("date", Date.from(Instant.now()));
        leaveDoc.append("type", "LEAVE");
        leaveDoc.append("uuid", getPlayer().getUniqueId().toString());
        try {
            Core.getInstance().getPluginDB().getCollection("PlayerConnections").insertOne(leaveDoc);
        } catch (NoClassDefFoundError | IllegalAccessError exception) {
            Core.getInstance().getLogger().log(Level.WARNING, "Couldn't save PlayerConnection for " + getPlayer().getName());
        }
    }

    public void updateMute() {
        mute = Core.getInstance().getInfractionManager().getMute(getUniqueId());
    }

    public Infraction getMute() {
        return mute;
    }

    public boolean isMuted() {
        return mute != null && mute.getType() != InfractionType.UNMUTE && mute.getRemainingTime() > 0;
    }

    public CoreParty getParty() {
        return Core.getInstance().getPartyManager().getParty(getUniqueId());
    }

    public Boolean getGhosting() {
        return ghosting;
    }

    public void setGhosting(Boolean ghosting) {
        this.ghosting = ghosting;
    }

    public long getLastAction() {
        return lastAction;
    }

    public void setLastAction(long lastAction) {
        this.lastAction = lastAction;
    }

    public CorePlayerPurse getPurse() {
        return purse;
    }

    public CorePlayerCollectibles getCollectibles() {
        return collectibles;
    }

    public CorePlayerCrates getCrates() {
        return crates;
    }

    public CoreFriendsList getFriends() {
        return friends;
    }

    public CorePlayerRatings getRatings() {
        return ratings;
    }

    /**
     * Checks if player is or is about to become AFK
     */
    public void checkAfk() {
        if (getPlayer() != null) {
            long time = System.currentTimeMillis();
            if (lastAction + AFK_TIMEOUT < time) {
                setAfk(true);
            } else if (!afkWarned && lastAction + AFK_WARNING < time
                    && getRank().equals(CoreRank.DEFAULT)) {
                Core.getInstance().sendMessage(this, "You will be kicked for AFK in 30 seconds!");
                afkWarned = true;
            }
        }
    }

    /**
     * @return Was AFK
     */
    public boolean setLastAction() {
        boolean wasAfk = this.afk;
        if (this.afk) {
            setAfk(false);
            statistics.add("general", "afkTime", System.currentTimeMillis() - lastAction);
        } else {
            statistics.add("general", "activeTime", System.currentTimeMillis() - lastAction);
            if (battleState == BattleState.BATTLER) {
                statistics.add("general", "gameTime", System.currentTimeMillis() - lastAction);
            }
        }
        lastAction = System.currentTimeMillis();
        afkWarned = false;
        return wasAfk;
    }

    /**
     * If the player is too low rank they are kicked for AFK,
     * otherwise they are set to AFK and given an AFK sign
     *
     * @param state AFK State
     */
    public void setAfk(boolean state) {
        if (afk != state) {
            afk = state;
            if (afk && getRank().equals(CoreRank.DEFAULT)) {
                // TODO: Removed temporarily to keep Sudofox's alts from being kicked
                getPlayer().kickPlayer("Kicked for AFK!");
                return;
            }
            Core.getInstance().sendMessage(this, "You are " + (afk ? "now afk" : "no longer afk"));
            refreshHotbar();
        }
    }

    public boolean isAfk() {
        return afk;
    }

    public CorePlayerStatistics getStatistics() {
        return statistics;
    }

    /**
     * Sets the channel that player sent messages are sent to
     *
     * @param cc ChatChannel
     */
    public void setChatChannel(ChatChannel cc) {
        chatChannel = cc;
        Core.getInstance().sendPacket(new PacketSpigotChatChannelJoin(getUniqueId(), cc.name()));
    }

    /**
     * @return Current ChatChannel
     */
    public ChatChannel getChatChannel() {
        return chatChannel;
    }

    public void joinChatGroup(ChatGroup chatGroup) {
        chatGroups.put(chatGroup.getChatId(), chatGroup);
    }

    public void leaveChatGroup(ChatGroup chatGroup) {
        chatGroups.remove(chatGroup.getChatId());
    }

    public Collection<ChatGroup> getChatGroups() {
        return chatGroups.values();
    }

    public CorePlayerMenu getMenu() {
        return menu;
    }

    /**
     * @return Player's ping
     */
    public int getPing() {
        try {
            EntityPlayer entityPlayer = (EntityPlayer) getPlayer().getClass().getMethod("getHandle").invoke(getPlayer());
            return entityPlayer.ping;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return -1;
        }
    }

    /**
     * @return Player's ping as a string with color based on latency
     */
    public String getPingFormatted() {
        int ping = getPing();
        String str = "";

        if (ping <= 30) str += ChatColor.GREEN;
        else if (ping <= 100) str += ChatColor.DARK_GREEN;
        else if (ping <= 250) str += ChatColor.GOLD;
        else str += ChatColor.RED;

        str += ping + "ms";
        return str;
    }

    /**
     * Whether a player is available to join a fake world, returns
     * false if they are either currently in a battle, build world,
     * or afk
     *
     * @return Can Join
     */
    public final boolean isAvailable() {
        return !isInGlobal() && !isAfk();
    }

    /**
     * Whether a player is in a fake world or not
     * Main purpose is to keep hotbar items from disappearing upon afk
     *
     * @return In Global World
     */
    public final boolean isInGlobal() {
        return !isInBattle() && !isInBuildWorld();
    }

    public final boolean canJoinBattle() {
        return (battle == null || battleState != BattleState.BATTLER || battle instanceof BonanzaBattle);
    }

    public final boolean isMenuAvailable() {
        return (!isInBattle() || getBattleState() != BattleState.BATTLER);
    }

    public boolean canSendUrl() {
        return getRank().hasPermission(CoreRank.TEMP_MOD);
    }

    /**
     * Returns a reversed list of the FakeWorlds for prioritizing interactions
     *
     * @return Sorted FakeWorld List
     */
    public final Iterator<FakeWorld<?>> getFakeWorlds() {
        return fakeWorlds.iterator();
    }

    public final void joinFakeWorld(FakeWorld<?> fakeWorld) {
        for (FakeWorld<?> fw : fakeWorlds) {
            if (fw.getWorldId() == fakeWorld.getWorldId()) return;
        }
        fakeWorlds.add(fakeWorld);
        fakeWorlds.sort(Comparator.comparingInt(FakeWorld::getPriority));
    }

    public final void leaveFakeWorld(FakeWorld<?> fakeWorld) {
        fakeWorlds.removeIf(fw -> fw.getWorldId() == fakeWorld.getWorldId());
    }

    /**
     * Sets the current battle of a Core Player for quick referencing later
     *
     * @param battle      Battle
     * @param battleState Battle State
     */
    public final void joinBattle(Battle<?> battle, BattleState battleState) {
        this.battle = battle;
        this.battleState = battleState;
        CorePlugin.addIngamePlayerName(this);
    }

    /**
     * Removes player from CorePlugins global player battles
     *
     * @param exitLocation Location to attempt to teleport player to
     */
    public final void leaveBattle(Location exitLocation) {
        BattleState temp = battleState;
        this.battle = null;
        this.battleState = BattleState.NONE;
        CorePlugin.removeIngamePlayerName(this);
    }

    /**
     * Checks if a player is in a battle
     *
     * @return In Battle
     */
    public final boolean isInBattle() {
        return battle != null;
    }

    /**
     * Returns current battle the player is in, or null if not
     * in a battle
     *
     * @return Battle
     */
    public final Battle<?> getBattle() {
        return battle;
    }

    /**
     * @return Battle State
     */
    public final BattleState getBattleState() {
        return battleState;
    }

    public final boolean isBattler() {
        return battle != null && battleState == BattleState.BATTLER;
    }

    /**
     * Sets the gamemode of a player
     * <p>
     * This is an override of the default setGameMode because there
     * were issues with MultiVerse plugin resetting GameModes on world tps
     *
     * @param gameMode GameMode
     */
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode.name();
        getPlayer().setGameMode(gameMode);
    }

    /**
     * Get the options object that CorePlayer options are stored in,
     * accessed ingame via the MainMenu Options menu
     *
     * @return CorePlayerOptions object
     */
    public CorePlayerOptions getOptions() {
        return options;
    }

    /**
     * Gets the current gamemode of a player, used for preventing gamemode change on world hopping
     *
     * @return Current GameMode
     */
    public org.bukkit.GameMode getGameMode() {
        return org.bukkit.GameMode.valueOf(gameMode);
    }

    /**
     * @param ghosting Ghosting state
     */
    public void setGhosting(boolean ghosting) {
        this.ghosting = ghosting;
    }

    /**
     * @return Ghosting state
     */
    public boolean isGhosting() {
        return ghosting;
    }

    /**
     * Returns the coordinates of the chunk the player is in
     *
     * @return Chunk Coordinate
     */
    public ChunkCoord getChunkCoord() {
        return new ChunkCoord(getLocation().getChunk().getX(), getLocation().getChunk().getZ());
    }

    /**
     * Sets back location
     */
    public void saveLastLocation() {
        lastLocation = new CoreLocation(getPlayer().getLocation());
    }

    /**
     * Teleports player to a warp location if they have permissions
     *
     * @param warp Warp
     * @return Success
     */
    public boolean warp(@NonNull Warp warp) {
        if (warp.isAvailable(this)) {
            teleport(warp.getLocation());
            return true;
        }
        return false;
    }

    /**
     * Teleports player to a Vector using player's world
     *
     * @param vec Position
     */
    public void teleport(Vector vec) {
        teleport(vec.getX(), vec.getY(), vec.getZ());
    }

    /**
     * Teleports player to a Location
     *
     * @param loc Location
     */
    public void teleport(Location loc) {
        saveLastLocation();
        getPlayer().teleport(loc, PlayerTeleportEvent.TeleportCause.COMMAND);
    }

    /**
     * Teleports player to a position using player's world
     *
     * @param x Double
     * @param y Double
     * @param z Double
     */
    public void teleport(double x, double y, double z) {
        Location loc = new Location(getLocation().getWorld(), x, y, z, getLocation().getYaw(), getLocation().getPitch());
        teleport(loc);
    }

    /**
     * Teleports a player using TpCoords (allows for relative/directional coordinates)
     *
     * @param x     TpCoord
     * @param y     TpCoord
     * @param z     TpCoord
     * @param pitch Double
     * @param yaw   Double
     */
    public void teleport(TpCoord x, TpCoord y, TpCoord z, @Nullable Double pitch, @Nullable Double yaw) {
        Location loc = getPlayer().getLocation().clone();

        TpCoord.apply(loc, x, y, z);
        if (pitch != null) loc.setPitch(pitch.floatValue());
        if (yaw != null) loc.setYaw(yaw.floatValue());

        teleport(loc);
    }

    /**
     * If the player is not in creative mode, clears inventory
     * and fills it with base hotbar items
     */
    public void refreshHotbar() {
        InventoryMenuItemHotbar.fillHotbar(this);
    }

    /**
     * Returns player's spawn location
     * Uses warp folder called spawn, if it doesn't exist then SpawnLocation of the default world
     *
     * @return Location
     */
    public Location getSpawnLocation() {
        /*
        Set<Warp> warps = Warp.getWarps("spawn");
        if (!warps.isEmpty()) {
            Random random = new Random();
            return ((Warp) warps.toArray()[(random.nextInt(warps.size()))]).getLocation();
        } else {
            return Core.DEFAULT_WORLD.getSpawnLocation().clone();
        }
        */
        return Core.DEFAULT_WORLD.getSpawnLocation().clone();
    }

    /**
     * Resets inventory and teleports player to spawn
     */
    public void gotoSpawn() {
        refreshHotbar();
        teleport(getSpawnLocation());
        getPlayer().setHealth(20);
    }

    /**
     * Sets a player's checkpoint for a certain duration of seconds, or 0 for no expire time
     *
     * @param warp     WarpName
     * @param duration Seconds (0=no expire)
     */
    public void setCheckpoint(String warp, int duration) {
        checkpoint = new Checkpoint(warp, duration);
    }

    /**
     * Teleport player to their checkpoint
     */
    public void checkpoint() {
        if (checkpoint != null && checkpoint.isActive()) {
            teleport(checkpoint.getLocation());
        }
    }

    /**
     * @return Back location
     */
    public Location getLastLocation() {
        return lastLocation.toLocation();
    }

    /**
     * @return Checkpoint
     */
    public Checkpoint getCheckpoint() {
        return checkpoint;
    }

    /**
     * Returns player's location, or if they're offline their lastLocation (/back)
     * Not really that useful for offline players atm
     *
     * @return Player Location
     */
    public Location getLocation() {
        Player p = Bukkit.getPlayer(uuid);
        if (p == null) {
            return lastLocation.toLocation();
        } else {
            return p.getLocation();
        }
    }

    /**
     * Returns whether player is in a Build World or not
     *
     * @return In Build World
     */
    public final boolean isInBuildWorld() {
        return BuildWorld.getPlayerBuildWorld(this) != null;
    }

    /**
     * Returns a player's Build World if they are in one
     *
     * @return Build World
     */
    public final BuildWorld getBuildWorld() {
        return BuildWorld.getPlayerBuildWorld(this);
    }

    /**
     * Always returns false if player is in a battle
     *
     * @return Whether player can build
     */
    public boolean canBuild() {
        return (getRank().hasPermission(CoreRank.BUILDER) && getPlayer().getGameMode().equals(GameMode.CREATIVE) && !isInBattle());
    }

    /**
     * Always returns false if player is holding a sword
     *
     * @return Whether player can break
     */
    public boolean canBreak() {
        return canBuild() && !(getPlayer().getInventory().getItemInMainHand().getType().toString().toLowerCase().contains("sword"));
    }

    private static final Set<String> disabledPerms = Sets.newHashSet(
            "minecraft.command.me",
            "minecraft.command.tell"
    );

    /**
     * Updates all online player scoreboards of this player's rank
     * Updates tab list via sending packets
     * Updates all available permissions
     */
    @Override
    public void updateRank() {
        if (getPlayer() == null) return;

        super.updateRank();
        PersonalScoreboard.updatePlayerRank(this);
        getPlayer().setOp(getRank().getHasOp());

        getPlayer().setPlayerListName(getTabName());

        permissions.getPermissions().clear();

        for (String perm : disabledPerms) {
            permissions.setPermission(perm, false);
        }

        for (String p : CoreCommand.getAllPermissions()) {
            boolean has = false;
            if (getPermanentRank().hasPermission(p)) {
                has = true;
            } else {
                for (CoreTempRank ctr : tempRanks) {
                    if (ctr.getRank().hasExclusivePermission(p)) {
                        has = true;
                        break;
                    }
                }
            }
            permissions.setPermission(p, has);
        }

        getPlayer().updateCommands();
    }

    /**
     * Send unformatted message to player
     *
     * @param string Message
     */
    public void sendMessage(String string) {
        getPlayer().sendMessage(string);
    }

    /**
     * Send BaseComponent message to player
     *
     * @param message BaseComponent
     */
    public void sendMessage(BaseComponent... message) {
        getPlayer().spigot().sendMessage(message);
    }

    /**
     * Send a title to player (Large text in middle of screen)
     *
     * @param title    Title
     * @param subtitle Subtitle
     * @param fadeIn   Ticks
     * @param stay     Ticks
     * @param fadeOut  Ticks
     */
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        getPlayer().sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    public void sendHotbarText(String text) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.CHAT);
        packet.getChatTypes().write(0, EnumWrappers.ChatType.GAME_INFO);
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(text));
        Core.sendPacket(this, packet);
    }

    /**
     * Used to get the inventory of a player, if they have a
     * pregameState saved (are ingame) then return their previous
     * inventory
     *
     * @return ItemStacks
     * @deprecated Under construction, pregameState is not a shared player variable
     */
    @Deprecated
    public final ItemStack[] getInventory() {
        return getPlayer().getInventory().getContents();
    }

    /**
     * Returns item in main hand
     *
     * @return ItemStack
     */
    public final ItemStack getHeldItem() {
        return getPlayer().getInventory().getItemInMainHand();
    }

    /**
     * Sets an item the player's main hand
     *
     * @param itemStack Held Item
     */
    public final void setHeldItem(ItemStack itemStack) {
        getPlayer().getInventory().setItemInMainHand(itemStack);
    }


}
