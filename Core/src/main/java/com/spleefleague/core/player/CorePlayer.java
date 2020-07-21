/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mongodb.lang.NonNull;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.game.battle.bonanza.BonanzaBattle;
import com.spleefleague.core.game.leaderboard.Leaderboards;
import com.spleefleague.core.io.converter.LocationConverter;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.menu.*;
import com.spleefleague.core.music.NoteBlockMusic;
import com.spleefleague.core.player.infraction.Infraction;
import com.spleefleague.core.player.party.Party;
import com.spleefleague.core.player.rank.PermRank;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.core.player.rank.TempRank;
import com.spleefleague.core.player.scoreboard.PersonalScoreboard;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.util.variable.Checkpoint;
import com.spleefleague.core.util.variable.TpCoord;
import com.spleefleague.core.util.variable.Warp;
import com.spleefleague.core.world.ChunkCoord;
import com.spleefleague.core.world.FakeWorld;
import com.spleefleague.core.world.build.BuildWorld;
import com.spleefleague.core.world.global.zone.GlobalZone;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.player.RatedPlayer;
import net.md_5.bungee.api.chat.*;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
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
public class CorePlayer extends RatedPlayer {

    /**
     * Database variables
     */
    
    @DBField(serializer=LocationConverter.class)
    private Location lastLocation;
    @DBField private Checkpoint checkpoint;

    @DBField private PermRank permRank;
    @DBField private List<TempRank> tempRanks;
    
    @DBField private Boolean vanished;
    @DBField private Integer coins;

    @DBField private String gameMode = org.bukkit.GameMode.SURVIVAL.name();
    @DBField private CorePlayerOptions options = new CorePlayerOptions();
    @DBField private CorePlayerCollectibles collectibles = new CorePlayerCollectibles();
    
    /**
     * Non-database variables
     */
    // Current party the player belongs to
    private Party party;
    // Expiration time for url access (/url <player>)
    private long urlTime;

    // Current inventory menu page
    private Map<String, Object> menuTags = new HashMap<>();
    // Current inventory menu
    private InventoryMenuContainer inventoryMenuContainer;
    // Current selected chat channel to send messages in
    private ChatChannel chatChannel;
    
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
    private final PregameState pregameState;
    private GlobalZone globalZone;

    /**
     * Constructor for CorePlayer
     */
    public CorePlayer() {
        this.chatChannel = ChatChannel.getDefaultChannel();
        this.lastLocation = null;
        this.checkpoint = null;
        this.permRank = new PermRank();
        this.tempRanks = new ArrayList<>();
        this.vanished = false;
        this.party = null;
        this.coins = 0;
        this.lastAction = System.currentTimeMillis();
        this.afk = false;
        this.afkWarned = false;
        this.battle = null;
        this.battleState = BattleState.NONE;
        this.pregameState = new PregameState(this);
        this.globalZone = null;
    }
    
    public CorePlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.username = player.getName();
        this.battleState = BattleState.NONE;
        this.pregameState = new PregameState(this);
    }
    
    /**
     * Runs when a player who has never joined before logs in
     * This function always calls before init()
     */
    @Override
    public void newPlayer(UUID uuid, String username) {
        super.newPlayer(uuid, username);
        permRank.setRank(Rank.getDefaultRank());
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
        permissions = getPlayer().addAttachment(Core.getInstance());
        setRank(permRank.getRank());
        PersonalScoreboard.initPlayerScoreboard(this);
        collectibles.setOwner(this);
        setGameMode(GameMode.valueOf(gameMode));
        refreshHotbar();
        FakeWorld.onPlayerJoin(getPlayer());
        FakeWorld.getGlobalFakeWorld().addPlayer(this);
        getPlayer().setGravity(true);
        getPlayer().getActivePotionEffects().forEach(pe -> getPlayer().removePotionEffect(pe.getType()));
        super.init();
    }
    
    @Override
    public void initOffline() {
        collectibles.setOwner(this);
        super.initOffline();
    }
    
    /**
     * Called after loading data from the database DBEntity::load
     */
    @Override
    public void afterLoad() {
        super.afterLoad();
        collectibles.setOwner(this);
    }
    
    /**
     * Called on player quit
     */
    @Override
    public void close() {
        if (battle != null) {
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
            if (lastAction + AFK_TIMEOUT < System.currentTimeMillis()) {
                setAfk(true);
            } else if (!afkWarned && lastAction + AFK_WARNING < System.currentTimeMillis()
                    && getRank().equals(Rank.DEFAULT)) {
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
            getStatistics().get("general").add("afkTime", System.currentTimeMillis() - lastAction);
        } else {
            getStatistics().get("general").add("playTime", System.currentTimeMillis() - lastAction);
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
            if (afk && getRank().equals(Rank.DEFAULT)) {
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
    
    /**
     * @return 0 if not muted, 1 if publicly muted, 2 if secretly muted
     */
    public int isMuted() {
        Infraction lastMute = Infraction.getMostRecent(getUniqueId(), Lists.newArrayList(Infraction.Type.MUTE_SECRET, Infraction.Type.MUTE_PUBLIC));
        
        if (lastMute != null && !lastMute.isExpired()) {
            switch (lastMute.getType()) {
                case MUTE_PUBLIC: return 1;
                case MUTE_SECRET: return 2;
                default: break;
            }
        }
        return 0;
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
     * Sets the player's party
     * Warning: This does not add the player to the party and should
     * not be called by anything outside of the Party class
     *
     * @param party Party
     */
    public void joinParty(Party party) {
        this.party = party;
    }

    /**
     * Sets the player's party to null
     * Warning: This does not add the player to the party and should
     * not be called by anything outside of the Party class
     */
    public void leaveParty() {
        if (party != null) {
            party = null;
        }
    }

    /**
     * @return Party the Player is contained in
     */
    public Party getParty() {
        return party;
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
     * @return Coins count
     */
    public int getCoins() {
        return coins;
    }

    /**
     * Sets player's total coins to coins
     *
     * @param coins Coins
     */
    public void setCoins(int coins) {
        this.coins = coins;
    }

    /**
     * Adds coins to player's coins
     * Can be positive or negative
     *
     * @param coins Coins
     */
    public void addCoins(int coins) {
        this.coins += coins;
    }

    /**
     * @return Skull of player
     */
    public ItemStack getSkull() {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta)skull.getItemMeta();
        if (skullMeta != null) skullMeta.setOwningPlayer(getPlayer());
        skull.setItemMeta(skullMeta);
        return skull;
    }

    /**
     * @return Display name of player including rank color, then resets color to default
     */
    public String getDisplayName() {
        if (getRank() != null)
            return getRank().getColor() + this.getName() + Chat.DEFAULT;
        return Chat.PLAYER_NAME + this.getName() + Chat.DEFAULT;
    }

    /**
     * @return Returns display name with an 's
     */
    public String getDisplayNamePossessive() {
        return getRank().getColor() + this.getName() + "'s" + Chat.DEFAULT;
    }

    /**
     * @return Name of player as TextComponent to allow for quick /tell
     */
    public TextComponent getChatName() {
        TextComponent text = new TextComponent(getName());
        
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to send a message").create()));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + getName()));
        text.setColor(getRank().getColor().asBungee());
        
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
        loadPregameState(null);
        pregameState.save(PregameState.PSFlag.INVENTORY);
        getPlayer().getInventory().setContents(target.getInventory());
    }

    /**
     * Always returns false if player is in a battle
     *
     * @return Whether player can build
     */
    public boolean canBuild() {
        return (getRank().hasPermission(Rank.BUILDER) && getPlayer().getGameMode().equals(GameMode.CREATIVE) && !isInBattle());
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

        PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME,
                ((CraftPlayer) getPlayer()).getHandle());


        PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO, packetPlayOutPlayerInfo);

        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);
        List<PlayerInfoData> pidList = Lists.newArrayList(new PlayerInfoData(
                WrappedGameProfile.fromPlayer(getPlayer()),
                1,
                EnumWrappers.NativeGameMode.fromBukkit(getPlayer().getGameMode()),
                WrappedChatComponent.fromText(getDisplayName())));

        //packet.getPlayerInfoDataLists().write(0, pidList);
        //Core.sendPacketAll(packet);

        permissions.getPermissions().clear();
        
        for (String p : CoreCommand.getAllPermissions()) {
            boolean has = false;
            if (getPermanentRank().hasPermission(p)) {
                has = true;
            } else {
                for (TempRank tr : tempRanks) {
                    if (tr.getRank().hasExclusivePermission(p)) {
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
    public void setRank(Rank rank) {
        permRank.setRank(rank);
        updateRank();
    }

    /**
     * Adds a temporary rank and calls updateRank()
     *
     * @param rank Rank
     * @param hours Hours
     * @return Whether rank was added
     */
    public boolean addTempRank(String rank, Integer hours) {
        TempRank tempRank = new TempRank(rank, hours * 60L * 60L * 1000L + System.currentTimeMillis());
        if (tempRank.getRank() != null) {
            Core.getInstance().sendMessage(this, "Added temp rank " + rank + " for " + hours + " hours");
            tempRanks.add(tempRank);
            updateRank();
            return true;
        }
        return false;
    }

    /**
     * Removes all temporary ranks and calls updateRank()
     */
    public void clearTempRank() {
        if (tempRanks.isEmpty()) return;
        Iterator<TempRank> it = tempRanks.iterator();
        while (it.hasNext()) {
            it.remove();
        }
        updateRank();
    }
    public Rank getPermanentRank() {
        return permRank.getRank();
    }

    /**
     * Returns highest TempRank, if no TempRanks available then Permanent Rank
     * This allows us to set Admins to Default for a set time
     *
     * @return Highest TempRank, if no TempRanks available then Permanent Rank
     */
    public Rank getRank() {
        if (tempRanks.size() > 0) {
            TempRank highestRank = null;
            for (TempRank tr : tempRanks) {
                if (highestRank == null ||
                        tr.getRank().getLadder() > highestRank.getRank().getLadder()) {
                    highestRank = tr;
                }
            }
            if (highestRank != null) {
                return highestRank.getRank();
            }
        }
        return permRank.getRank();
    }

    public void checkGlobalSpectate() {
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
            return this.lastLocation;
        } else {
            return p.getLocation();
        }
    }

    /**
     * Returns the coordinates of the chunk the player is in
     *
     * @return
     */
    public ChunkCoord getChunkCoord() {
        return new ChunkCoord(getLocation().getChunk().getX(), getLocation().getChunk().getZ());
    }

    /**
     * Sets back location
     */
    public void saveLastLocation() {
        lastLocation = getPlayer().getLocation();
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
        return lastLocation;
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

    private GlobalZone zoneChange = null;
    private boolean isChanging = false;
    public void setGlobalZone(GlobalZone globalZone) {
        if (!isInGlobal()) return;
        if (!globalZone.equals(this.globalZone)) {
            this.zoneChange = globalZone;
            if (!isChanging) {
                isChanging = true;
                //Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
                    if (this.zoneChange.equals(globalZone) && !isInGlobal()) {
                        this.globalZone = this.zoneChange;
                        sendTitle(globalZone.getName(), "", 15, 50, 15);
                        if (!globalZone.isWild()) {
                            //getPlayer().playSound(getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.25f, 1.5f);
                        }
                    }
                    zoneChange = null;
                    isChanging = false;
                    updateLeaves();
                //}, 80L);
            }
        }
        sendHotbarText(globalZone.getName());
    }

    public void updateLeaves() {
        if (globalZone == null || globalZone.getLeaves().isEmpty()) {
            getPlayer().sendExperienceChange(0, 0);
        } else {
            getPlayer().sendExperienceChange(Math.min(1.f, getCollectibles().getLeafCount(globalZone.getIdentifier()) / ((float) globalZone.getLeaves().size())), 0);
        }
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
        Chat.sendMessageToPlayer(this, "Chat Channel set to " + cc.getName());
    }

    /**
     * @return Current ChatChannel
     */
    public ChatChannel getChatChannel() {
        return chatChannel;
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
    public void sendMessage(BaseComponent message) {
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

    public int getInvSwap() {
        return invSwap;
    }
    
    public void addInvSwap() {
        invSwap++;
    }
    
    public void removeInvSwap() {
        invSwap--;
    }
    
    private int invSwap = 0;
    /**
     * Set player's current InventoryMenuContainer
     *
     * @param inventoryMenuChest InventoryMenuContainer
     * @param initialize Should Call OpenFunction
     */
    public void setInventoryMenuChest(InventoryMenuContainerChest inventoryMenuChest, boolean initialize) {
        if (inventoryMenuChest != null) {
            invSwap++;
            menuTags.put("page", 0);
            ItemStack item = getPlayer().getItemOnCursor();
            getPlayer().setItemOnCursor(null);
            if (initialize) inventoryMenuChest.open(this);
            else            inventoryMenuChest.refreshInventory(this);
            getPlayer().setItemOnCursor(item);
            this.inventoryMenuContainer = inventoryMenuChest;
        } else if (invSwap <= 0) {
            this.inventoryMenuContainer = null;
            this.menuTags.clear();
        } else {
            invSwap--;
        }
    }
    
    public void setInventoryMenuAnvil(InventoryMenuContainerAnvil inventoryMenuAnvil) {
        invSwap++;
        ItemStack item = getPlayer().getItemOnCursor();
        getPlayer().getInventory().addItem(item);
        getPlayer().setItemOnCursor(null);
        inventoryMenuAnvil.open(this);
        this.inventoryMenuContainer = inventoryMenuAnvil;
    }
    
    public void setInventoryMenuContainer(InventoryMenuContainer inventoryMenuContainer) {
        if (inventoryMenuContainer instanceof InventoryMenuContainerAnvil) {
            setInventoryMenuAnvil((InventoryMenuContainerAnvil) inventoryMenuContainer);
        } else if (inventoryMenuContainer instanceof InventoryMenuContainerChest) {
            setInventoryMenuChest((InventoryMenuContainerChest) inventoryMenuContainer, true);
        }
    }
    
    private InventoryMenuDialog inventoryMenuDialog = null;
    private int nextDialog;
    
    public void setInventoryMenuDialog(InventoryMenuDialog inventoryMenuDialog) {
        this.inventoryMenuDialog = inventoryMenuDialog;
        nextDialog = 0;
        openNextDialog();
    }

    public void openNextDialog() {
        if (inventoryMenuDialog != null) {
            if (inventoryMenuDialog.openNextContainer(this, nextDialog)) {
                nextDialog++;
            }
        }
    }
    
    /**
     * Set player's current InventoryMenuContainer based on linked container of item
     *
     * @param inventoryMenuItem InventoryMenuItem
     */
    public void setInventoryMenuItem(InventoryMenuItem inventoryMenuItem) {
        if (inventoryMenuItem != null) {
            invSwap++;
            menuTags.put("page", 0);
            if (inventoryMenuItem.hasLinkedContainer()) {
                ItemStack item = getPlayer().getItemOnCursor();
                getPlayer().setItemOnCursor(null);
                inventoryMenuItem.getLinkedChest().open(this);
                getPlayer().setItemOnCursor(item);
                inventoryMenuContainer = inventoryMenuItem.getLinkedChest();
            } else {
                getPlayer().closeInventory();
                inventoryMenuContainer = null;
            }
        } else {
            getPlayer().closeInventory();
            inventoryMenuContainer = null;
            menuTags.clear();
        }
    }

    /**
     * Updates the player's current inventoryMenuContainer
     * For refreshing/page change
     */
    public void refreshInventoryMenuContainer() {
        if (inventoryMenuContainer != null
                && inventoryMenuContainer instanceof InventoryMenuContainerChest) {
            invSwap++;
            InventoryMenuContainerChest container = (InventoryMenuContainerChest) inventoryMenuContainer;
            ItemStack itemStack = getPlayer().getItemOnCursor();
            getPlayer().setItemOnCursor(null);
            container.refreshInventory(this);
            getPlayer().setItemOnCursor(itemStack);
            inventoryMenuContainer = container;
        }
    }

    /**
     * @return Current InventoryMenuContainer
     */
    public InventoryMenuContainer getInventoryMenuContainer() {
        return inventoryMenuContainer;
    }
    
    /**
     * @param <T> ? extends Collectible
     * @param name Menu Tag Identifier
     * @param clazz Class of T
     * @return Current Menu Tags
     */
    public <T> T getMenuTag(String name, Class<T> clazz) {
        if (menuTags.containsKey(name)
                && clazz.isAssignableFrom(menuTags.get(name).getClass())) {
            return (T) menuTags.get(name);
        }
        return null;
    }
    
    public <T> void setMenuTag(String name, T obj) {
        menuTags.put(name, obj);
    }
    
    public boolean hasMenuTag(String name) {
        return menuTags.containsKey(name);
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
     * @return Banned?
     */
    public Infraction checkBan() {
        Infraction latestban = Infraction.getMostRecent(getUniqueId(), Lists.newArrayList(Infraction.Type.BAN, Infraction.Type.TEMPBAN, Infraction.Type.UNBAN));
        
        if (latestban == null)
            return null;
        
        switch (latestban.getType()) {
            case UNBAN:
                if (latestban.isExpired())
                    return null;
            case BAN:
                return latestban;
            case TEMPBAN:
            default:
                return null;
        }
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
        if (battleState != BattleState.SPECTATOR_GLOBAL) {
            savePregameState();
        }
        CorePlugin.addIngamePlayerName(this);
    }
    
    /**
     * Removes player from CorePlugins global player battles
     *
     * @param exitLocation Location to attempt to teleport player to
     */
    public final void leaveBattle(Location exitLocation) {
        if (battleState != BattleState.SPECTATOR_GLOBAL && Core.getInstance().getServerType().equals(Core.ServerType.LOBBY)) {
            loadPregameState(exitLocation);
        }
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
     * Used to get the inventory of a player, if they have a
     * pregameState saved (are ingame) then return their previous
     * inventory
     *
     * @return ItemStacks
     * @deprecated Under construction, pregameState is not a shared player variable
     */
    @Deprecated
    public final ItemStack[] getInventory() {
        if (pregameState.getInventory() != null) {
            return pregameState.getInventory();
        } else {
            return getPlayer().getInventory().getContents();
        }
    }

    /**
     * Returns item in main hand
     *
     * @return
     */
    public final ItemStack getHeldItem() {
        return getPlayer().getInventory().getItemInMainHand();
    }

    /**
     * Sets an item the player's main hand
     * 
     * @param itemStack
     */
    public final void setHeldItem(ItemStack itemStack) {
        getPlayer().getInventory().setItemInMainHand(itemStack);
    }
    
    /**
     * Save all current player variables to be loaded
     * back after a game has finished
     */
    public final void savePregameState() {
        pregameState.save(PregameState.PSFlag.ALL);
    }
    
    /**
     * Load saved variables, takes a location to teleport
     * player to if they have post-game Arena warp enabled
     *
     * @param arenaLoc Location
     */
    public final void loadPregameState(@Nullable Location arenaLoc) {
        pregameState.load(arenaLoc);
    }
    
}
