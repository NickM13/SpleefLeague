/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mongodb.lang.NonNull;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.chat.ChatGroup;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.battle.bonanza.BonanzaBattle;
import com.spleefleague.core.menu.InventoryMenuItemHotbar;
import com.spleefleague.core.menu.InventoryMenuSkullManager;
import com.spleefleague.core.music.NoteBlockMusic;
import com.spleefleague.core.player.crates.CorePlayerCrates;
import com.spleefleague.core.player.friends.CorePlayerFriends;
import com.spleefleague.core.player.options.CorePlayerOptions;
import com.spleefleague.core.player.party.CoreParty;
import com.spleefleague.core.player.purse.CorePlayerPurse;
import com.spleefleague.core.player.rank.CorePermanentRank;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.player.rank.CoreTempRank;
import com.spleefleague.core.player.scoreboard.PersonalScoreboard;
import com.spleefleague.core.player.statistics.CorePlayerStatistics;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.util.variable.*;
import com.spleefleague.core.world.ChunkCoord;
import com.spleefleague.core.world.FakeWorld;
import com.spleefleague.core.world.build.BuildWorld;
import com.spleefleague.core.world.global.zone.GlobalZone;
import com.spleefleague.core.world.global.zone.GlobalZones;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.player.PlayerRatings;
import com.spleefleague.coreapi.player.PlayerStatistics;
import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChatChannelJoin;
import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerRank;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;

/**
 * CorePlayer is an instance of DBPlayer that maintains various options and stats
 * unrelated to game modes, such as afk, ranking, and environment keys
 *
 * @author NickM13
 */
public class CorePlayer extends DBPlayer {

    /**
     * Database variables
     */

    private CoreLocation lastLocation;
    private Checkpoint checkpoint;

    @DBField private String nickname = null;
    @DBField private UUID disguise = null;

    @DBField private CorePermanentRank permRank = new CorePermanentRank();
    @DBField private List<CoreTempRank> tempRanks = new ArrayList<>();

    @DBField private Boolean vanished;
    private Boolean ghosting = false;

    @DBField private String gameMode = "ADVENTURE";
    @DBField private final CorePlayerOptions options = new CorePlayerOptions(this);
    @DBField private final CorePlayerCollectibles collectibles = new CorePlayerCollectibles(this);
    @DBField private final PlayerRatings ratings = new PlayerRatings();
    @DBField private Long lastOnline = -1L;
    @DBField private CorePlayerPurse purse = new CorePlayerPurse(this);
    @DBField private CorePlayerCrates crates = new CorePlayerCrates(this);

    @DBField private CorePlayerStatistics statistics = new CorePlayerStatistics();

    @DBField private CorePlayerFriends friends = new CorePlayerFriends(this);

    /**
     * Non-database variables
     */
    // Current party the player belongs to
    private CoreParty party;
    // Expiration time for url access (/url <player>)
    private long urlTime;

    // Current selected chat channel to send messages in
    @DBField private ChatChannel chatChannel;
    
    // 5 min, sets player to afk
    private static final long AFK_WARNING = 1000L * 60 * 4 + 30;
    private static final long AFK_TIMEOUT = 1000L * 60 * 5;
    // Last action millis
    private long lastAction;
    private boolean afk;
    private boolean afkWarned;
    
    private Player replyPlayer = null;

    private PermissionAttachment permissions;
    
    private final Set<FakeWorld<?>> fakeWorlds = new TreeSet<>((left, right) -> right.getPriority() - left.getPriority());
    private Battle<?> battle;
    private BattleState battleState;
    private GlobalZone globalZone;

    private final Map<Integer, ChatGroup> chatGroups = new HashMap<>();

    private final CorePlayerMenu menu = new CorePlayerMenu(this);

    private GameProfile gameProfile;

    /**
     * Constructor for CorePlayer
     */
    public CorePlayer() {
        super();
        this.chatChannel = ChatChannel.GLOBAL;
        this.lastLocation = null;
        this.checkpoint = null;
        this.vanished = false;
        this.party = null;
        this.lastAction = System.currentTimeMillis();
        this.afk = false;
        this.afkWarned = false;
        this.battle = null;
        this.battleState = BattleState.NONE;
        this.globalZone = GlobalZones.getWilderness();
    }
    
    public CorePlayer(Player player) {
        super();
        this.uuid = player.getUniqueId();
        this.username = player.getName();
        this.battleState = BattleState.NONE;
    }
    
    /**
     * Runs when a player who has never joined before logs in
     * This function always calls before init()
     */
    @Override
    public void newPlayer(UUID uuid, String username) {
        super.newPlayer(uuid, username);
        permRank.setRank(CoreRank.DEFAULT);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    /**
     * CorePlayer::init is called when a player comes online
     */
    @Override
    public void init() {
        username = getPlayer().getName();
        if (nickname == null || disguise == null) {
            nickname = username;
        }
        gameProfile = ((CraftPlayer) getPlayer()).getHandle().getProfile();
        updateDisguise();
        permissions = getPlayer().addAttachment(Core.getInstance());
        updateRank();
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> PersonalScoreboard.initPlayerScoreboard(this), 2L);
        setGameMode(GameMode.valueOf(gameMode));
        refreshHotbar();
        FakeWorld.onPlayerJoin(getPlayer());
        FakeWorld.getGlobalFakeWorld().addPlayer(this);
        getPlayer().setGravity(true);
        getPlayer().getActivePotionEffects().forEach(pe -> getPlayer().removePotionEffect(pe.getType()));
        GlobalZones.onPlayerJoin(this);
    }
    
    @Override
    public void initOffline() {
        if (nickname == null) nickname = username;

        gameProfile = new GameProfile(getUniqueId(), nickname);

        gameProfile.getProperties().clear();

        InventoryMenuSkullManager.Texture texture = InventoryMenuSkullManager.getTexture(disguise != null ? disguise : getUniqueId());

        gameProfile.getProperties().put("textures", new Property("textures",
                texture.value,
                texture.signature));

        super.initOffline();
    }
    
    /**
     * Called after loading data from the database DBEntity::load
     */
    @Override
    public void afterLoad() {
        super.afterLoad();
    }
    
    /**
     * Called on player quit
     */
    @Override
    public void close() {
        if (battle != null) {
            System.out.println("We are here!");
            battle.leavePlayer(this);
        }

        List<FakeWorld<?>> fakeWorldList = new ArrayList<>(fakeWorlds);
        for (FakeWorld<?> fakeWorld : fakeWorldList) {
            fakeWorld.removePlayer(this);
        }
        Core.getInstance().unqueuePlayerGlobally(this);

        NoteBlockMusic.stopSong(this);
        
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

        PersonalScoreboard.closePlayerScoreboard(this);
    }

    public void updateDisguise() {
        if (disguise != null) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(disguise);
            nickname = op.getName();
        }
        if (disguise == null || nickname == null) {
            nickname = getName();
        }

        try {
            Field field = GameProfile.class.getDeclaredField("name");
            field.setAccessible(true);
            field.set(gameProfile, nickname);
        } catch (Exception e) {
            e.printStackTrace();
        }

        gameProfile.getProperties().clear();

        InventoryMenuSkullManager.Texture texture = InventoryMenuSkullManager.getTexture(disguise != null ? disguise : getUniqueId());

        gameProfile.getProperties().put("textures", new Property("textures",
                texture.value,
                texture.signature));

        for (CorePlayer cp2 : Core.getInstance().getPlayers().getAllHere()) {
            cp2.getPlayer().hidePlayer(Core.getInstance(), getPlayer());
            cp2.getPlayer().showPlayer(Core.getInstance(), getPlayer());
        }
    }

    public void setDisguise(@Nullable UUID uuid) {
        if (uuid.equals(getUniqueId())) {
            disguise = null;
        } else {
            disguise = uuid;
        }
        updateDisguise();
    }

    public UUID getDisguise() {
        return disguise != null ? disguise : uuid;
    }

    public GameProfile getGameProfile() {
        return gameProfile;
    }

    public String getNickname() {
        return nickname;
    }

    @Deprecated
    public void addElo(String mode, int season, int amt) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF(mode);
        output.writeInt(season);
        output.writeInt(1);
        output.writeUTF(getUniqueId().toString());
        output.writeInt(getRatings().getElo(mode, season) + amt);
        Objects.requireNonNull(Iterables.getFirst(Bukkit.getOnlinePlayers(), null)).sendPluginMessage(Core.getInstance(), "slcore:score", output.toByteArray());
    }

    /**
     * Get all ratings object of a Core Player
     *
     * @return Core Player Ratings
     * @see PlayerRatings
     */
    public PlayerRatings getRatings() {
        return ratings;
    }

    /**
     * Sets the gamemode of a player
     *
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
     * Get the collectibles object owned by the player
     *
     * @return Collectibles
     */
    public CorePlayerCollectibles getCollectibles() {
        return collectibles;
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
            statistics.add("general", "playTime", System.currentTimeMillis() - lastAction);
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
                //getPlayer().kickPlayer("Kicked for AFK!");
                //return;
            }
            Core.getInstance().sendMessage(this, "You are " + (afk ? "now afk" : "no longer afk"));
            refreshHotbar();
        }
    }
    public boolean isAfk() {
        return afk;
    }

    public boolean isFlying() {
        return getPlayer().isFlying()
                || getPlayer().isGliding();
    }

    /**
     * @param vanished Vanished state
     */
    public void setVanished(boolean vanished) {
        this.vanished = vanished;
    }

    /**
     * @return Vanished state
     */
    public boolean isVanished() {
        return vanished;
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
     * @return Party the Player is contained in
     */
    public CoreParty getParty() {
        return Core.getInstance().getPartyManager().getParty(this);
    }
    
    /**
     * @return In Party
     */
    public boolean isInParty() {
        return party != null;
    }

    /**
     * Calling this function sets the player's URL timeout to 0
     *
     * @return Whether player can send a URL
     */
    public boolean canSendUrl() {
        if (urlTime > System.currentTimeMillis()) {
            urlTime = 0;
            return true;
        }
        return false;
    }

    /**
     * Sets player's URL timeout to current time + allowed time
     */
    public void allowUrl() {
        urlTime = System.currentTimeMillis() + 30 * 1000;
    }

    /**
     * @return Purse Object
     */
    public CorePlayerPurse getPurse() {
        return purse;
    }

    public CorePlayerCrates getCrates() {
        return crates;
    }

    public PlayerStatistics getStatistics() {
        return statistics;
    }

    public CorePlayerFriends getFriends() {
        return friends;
    }

    /**
     * @return Skull of player
     */
    public ItemStack getSkull() {
        return InventoryMenuSkullManager.getPlayerSkull(getUniqueId());
    }

    public String getTabName() {
        return getRankedDisplayName();
    }

    public String getRankedDisplayName() {
        String message = "";
        if (getRank() != null && getRank().getDisplayNameUnformatted().length() > 0) {
            message += Chat.TAG_BRACE + "[" + Chat.RANK + getRank().getDisplayName() + Chat.TAG_BRACE + "] ";
        }
        message += getDisplayName();
        return message;
    }

    public String getMenuName() {
        return Chat.PLAYER_NAME + ChatColor.BOLD + nickname;
    }

    /**
     * @return Display name of player including rank color, then resets color to default
     */
    public String getDisplayName() {
        if (getRank() != null)
            return getRank().getColor() + nickname;
        return Chat.PLAYER_NAME + nickname;
    }

    /**
     * @return Returns display name with an 's
     */
    public String getDisplayNamePossessive() {
        return getRank().getColor() + nickname + "'s";
    }

    /**
     * @return Name of player as TextComponent to allow for quick /tell
     */
    public TextComponent getChatName() {
        TextComponent text = new TextComponent(getRank().getColor() + nickname);

        text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + nickname));

        return text;
    }

    /**
     * @return Name of player as TextComponent to allow for quick /tell
     */
    public TextComponent getChatNamePossessive() {
        TextComponent text = new TextComponent(getRank().getColor() + nickname + "'s");

        text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + nickname));

        return text;
    }

    /**
     * @return Name of player as TextComponent to allow for quick /tell
     */
    public TextComponent getChatNameRanked() {
        TextComponent text = new TextComponent(getRank().getChatTag() + getRank().getColor() + nickname);

        text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + nickname));

        return text;
    }

    /**
     * Open the target player's inventory
     *
     * @param target Target Player
     */
    public void invsee(CorePlayer target) {
        getPlayer().openInventory(target.getPlayer().getInventory());
    }

    /**
     * Copies the target player's inventory and stores
     * Saves previous inventory to pregameState
     *
     * @param target Target Player
     */
    @Deprecated
    public void invcopy(CorePlayer target) {
        getPlayer().getInventory().setContents(target.getPlayer().getInventory().getContents());
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

    /**
     * Updates all online player scoreboards of this player's rank
     * Updates tab list via sending packets
     * Updates all available permissions
     */
    public void updateRank() {
        if (getPlayer() == null) return;
        PersonalScoreboard.updatePlayerRank(this);
        getPlayer().setOp(getRank().getHasOp());

        getPlayer().setPlayerListName(getTabName());

        permissions.getPermissions().clear();

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
     * Removes expired TempRanks, calls updateRank() if any ranks were removed
     */
    public void checkTempRanks() {
        if (tempRanks.removeIf(tr -> tr.getExpireTime() < System.currentTimeMillis())) {
            updateRank();
        }
    }

    /**
     * Set permanent rank and calls updateRank()
     *
     * @param rank Rank
     */
    public void setRank(CoreRank rank) {
        permRank.setRank(rank);
        updateRank();
        PacketSpigotPlayerRank packet = new PacketSpigotPlayerRank(getUniqueId(), rank.getIdentifier(), 0L);
        Core.getInstance().sendPacket(packet);
    }

    /**
     * Adds a temporary rank and calls updateRank()
     *
     * @param rankName Rank Name
     * @param duration Duration
     * @return Whether rank was added
     */
    public boolean addTempRank(String rankName, long duration) {
        CoreRank rank = Core.getInstance().getRankManager().getRank(rankName);
        if (rank != null) {
            CoreTempRank tempRank = new CoreTempRank(rank, duration + System.currentTimeMillis());
            tempRanks.add(tempRank);
            updateRank();
            Core.getInstance().sendPacket(new PacketSpigotPlayerRank(getUniqueId(), rank.getIdentifier(), duration));
            return true;
        }
        return false;
    }

    /**
     * Removes all temporary ranks and calls updateRank()
     */
    public void clearTempRank() {
        if (tempRanks.isEmpty()) return;
        tempRanks.clear();
        updateRank();
        Core.getInstance().sendPacket(new PacketSpigotPlayerRank(getUniqueId(), "", 0));
    }
    public CoreRank getPermanentRank() {
        return permRank.getRank();
    }

    /**
     * Returns highest TempRank, if no TempRanks available then Permanent Rank
     * This allows us to set Admins to Default for a set time
     *
     * @return Highest TempRank, if no TempRanks available then Permanent Rank
     */
    public CoreRank getRank() {
        if (tempRanks.size() > 0) {
            CoreTempRank highestRank = null;
            for (CoreTempRank ctr : tempRanks) {
                if (highestRank == null ||
                        ctr.getRank().getLadder() > highestRank.getRank().getLadder()) {
                    highestRank = ctr;
                }
            }
            if (highestRank != null) {
                return highestRank.getRank();
            }
        }
        return permRank.getRank();
    }

    public int getRankLadder() {
        return getRank().getLadder();
    }

    public void checkGlobalSpectate() {
        /*
        if (isInGlobal()) {
            for (BattleMode arenaMode : BattleMode.getAllModes()) {
                for (Battle<?> battle : arenaMode.getOngoingBattles()) {
                    if (battle.isInGlobalSpectatorBorder(this)) {
                        battle.addGlobalSpectator(this);
                        return;
                    }
                }
            }
        }
         */
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
     * @param x TpCoord
     * @param y TpCoord
     * @param z TpCoord
     * @param pitch Double
     * @param yaw Double
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
    }

    /**
     * Sets a player's checkpoint for a certain duration of seconds, or 0 for no expire time
     *
     * @param warp WarpName
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

    public GlobalZone getGlobalZone() {
        return globalZone;
    }

    public void setGlobalZone(GlobalZone globalZone) {
        if (!isInGlobal()) return;
        if (!globalZone.equals(this.globalZone)) {
            this.globalZone = globalZone;
            updateLeaves();
        }
    }

    public void showGlobalZone() {
        sendHotbarText(this.globalZone.getName());
    }

    public void updateLeaves() {
        /*
        if (globalZone == null || globalZone.getLeaves().isEmpty()) {
            getPlayer().sendExperienceChange(0, 0);
        } else {
            getPlayer().sendExperienceChange(Math.min(1.f, getCollectibles().getLeafCount(globalZone.getIdentifier()) / ((float) globalZone.getLeaves().size())), 0);
        }
        */
    }

    /**
     * @param player Player to reply to (/reply)
     */
    public void setReply(Player player) {
        replyPlayer = player;
    }

    /**
     * @return Player to reply to (/reply)
     */
    public Player getReply() {
        return replyPlayer;
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

    @Override
    public void setOnline(OnlineState state) {
        super.setOnline(state);
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
     * @param title Title
     * @param subtitle Subtitle
     * @param fadeIn Ticks
     * @param stay Ticks
     * @param fadeOut Ticks
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
        
        if (ping <= 30)          str += ChatColor.GREEN;
        else if (ping <= 100)    str += ChatColor.DARK_GREEN;
        else if (ping <= 250)    str += ChatColor.GOLD;
        else                    str += ChatColor.RED;
        
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
    
    /**
     * Returns a reversed list of the FakeWorlds for prioritizing interactions
     *
     * @return Sorted FakeWorld List
     */
    public final Iterator<FakeWorld<?>> getFakeWorlds() {
        return fakeWorlds.iterator();
    }
    
    public final void joinFakeWorld(FakeWorld<?> fakeWorld) {
        fakeWorlds.add(fakeWorld);
    }
    
    public final void leaveFakeWorld(FakeWorld<?> fakeWorld) {
        fakeWorlds.remove(fakeWorld);
    }
    
    /**
     * Sets the current battle of a Core Player for quick referencing later
     *
     * @param battle Battle
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

    public final boolean isOnline() {
        return onlineState != OnlineState.OFFLINE;
    }

    public final boolean isLocal() {
        return onlineState == OnlineState.HERE;
    }

    public final long getLastOnline() {
        if (onlineState != OnlineState.OFFLINE) {
            return System.currentTimeMillis();
        }
        return lastOnline;
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

    @Override
    public String toString() {
        return "CorePlayer{" +
                "uuid=" + getUniqueId() +
                ", username=" + getName() +
                '}';
    }
}
