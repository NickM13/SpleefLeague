/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.player;

import com.spleefleague.core.database.variable.Checkpoint;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.database.annotation.DBLoad;
import com.spleefleague.core.database.annotation.DBSave;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.game.ArenaMode;
import com.spleefleague.core.game.Battle;
import com.spleefleague.core.menu.*;
import com.spleefleague.core.player.collectible.Holdable;
import com.spleefleague.core.player.infraction.Infraction;
import com.spleefleague.core.io.converter.LocationConverter;
import com.spleefleague.core.menu.hotbars.main.HeldItemMenu;
import com.spleefleague.core.player.party.Party;
import com.spleefleague.core.player.collectible.armor.CosmeticArmor;
import com.spleefleague.core.player.rank.PermRank;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.core.player.rank.TempRank;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.player.scoreboard.PersonalScoreboard;
import com.spleefleague.core.util.variable.TpCoord;
import com.spleefleague.core.util.variable.Warp;
import com.spleefleague.core.database.variable.DBPlayer;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.*;
import javax.annotation.Nullable;

import com.spleefleague.core.world.FakeWorld;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

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
    
    @DBField(serializer=LocationConverter.class)
    private Location lastLocation;
    @DBField private Checkpoint checkpoint;

    @DBField private PermRank permRank;
    @DBField private List<TempRank> tempRanks;
    
    @DBField protected Map<String, Integer> scores = new HashMap<>();
    @DBField private Boolean vanished;
    @DBField private Integer coins;
    
    // Non-afk time
    @DBField private Long playTime;
    @DBField private Long afkTime;

    @DBField private String gameMode = org.bukkit.GameMode.SURVIVAL.name();
    @DBField private CorePlayerOptions options;
    @DBField private CorePlayerCollectibles collectibles = new CorePlayerCollectibles();
    
    /**
     * Non-database variables
     */
    // Current party the player belongs to
    private Party party;
    // Expiration time for url access (/url <player>)
    private long urlTime;

    // Current inventory menu page
    private int inventoryPage;
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
    
    private LinkedHashSet<FakeWorld> fakeWorlds;
    private Battle<?> battle;
    private BattleState battleState;
    private final PregameState pregameState;
    
    /**
     * Constructor for CorePlayer
     */
    public CorePlayer() {
        super();
        this.chatChannel = ChatChannel.getDefaultChannel();
        this.lastLocation = null;
        this.checkpoint = null;
        this.permRank = new PermRank();
        this.tempRanks = new ArrayList<>();
        this.options = new CorePlayerOptions();
        this.vanished = false;
        this.party = null;
        this.coins = 0;
        this.playTime = 0L;
        this.afkTime = 0L;
        this.lastAction = System.currentTimeMillis();
        this.afk = false;
        this.afkWarned = false;
        this.fakeWorlds = new LinkedHashSet<>();
        this.battle = null;
        this.battleState = BattleState.NONE;
        this.pregameState = new PregameState(this);
    }
    
    public CorePlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.username = player.getName();
        this.battleState = BattleState.NONE;
        this.pregameState = new PregameState(this);
    }
    
    /**
     * Runs when a player who has never joined before logs in
     * This function always runs before init()
     */
    @Override
    public void newPlayer(Player player) {
        super.newPlayer(player);
        permRank.setRank(Rank.getDefaultRank());
    }

    /**
     * CorePlayer::init is run when a player comes online
     */
    @Override
    public void init() {
        permissions = getPlayer().addAttachment(Core.getInstance());
        setRank(permRank.getRank());
        updateArmor();
        PersonalScoreboard.initPlayerScoreboard(this);
        collectibles.setOwner(this);
        refreshHotbar();
    }

    /**
     * Get the options object that CorePlayer options are stored in,
     * accessed ingame via the MainMenu>Options menu
     *
     * @return CorePlayerOptions object
     */
    public CorePlayerOptions getOptions() {
        return options;
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
     * Print all saved elo stats of a player
     *
     * @param dbp DBPlayer
     * @deprecated Use inventory menus instead
     */
    @Deprecated
    @Override
    public void printStats(DBPlayer dbp) {

    }

    @Override
    public int getRating(ArenaMode mode) {
        return 0;
    }

    @Override
    public void addRating(ArenaMode mode, int rating) {

    }

    @Override
    public String getDisplayElo(ArenaMode mode) {
        return null;
    }

    public org.bukkit.GameMode getGameMode() {
        return org.bukkit.GameMode.valueOf(gameMode);
    }

    /**
     * @return Total Non-AFK time
     */
    public long getPlayTime() {
        return playTime;
    }

    /**
     *
     *
     * @return Total AFK time
     */
    public long getAfkTime() {
        return afkTime;
    }

    /**
     * Checks if player is or is about to become AFK
     */
    public void checkAfk() {
        if (lastAction + AFK_TIMEOUT < System.currentTimeMillis()) {
            setAfk(true);
        } else if (!afkWarned && lastAction + AFK_WARNING < System.currentTimeMillis()
                && getRank().equals(Rank.DEFAULT)) {
            Core.getInstance().sendMessage(this, "You will be kicked for AFK in 30 seconds!");
            afkWarned = true;
        }
    }

    /**
     * @return Was AFK
     */
    public boolean setLastAction() {
        boolean wasAfk = this.afk;
        if (this.afk) setAfk(false);
        afkTime += System.currentTimeMillis() - lastAction;
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
    
    /**
     * Get the collectibles object owned by the player
     *
     * @return Collectibles
     */
    public CorePlayerCollectibles getCollectibles() {
        return collectibles;
    }

    /**
     * On right click, run the activate function of VendorItems
     * e.g lets you shoot snowballs with a Splegg Gun active
     */
    public void activateHeldItem() {
    
    }

    /**
     * Run on player disconnect
     */
    @Override
    public void close() {
        Core.getInstance().unqueuePlayerGlobally(this);
        
        Team team = getPlayer().getScoreboard().getEntryTeam(getPlayer().getName());
        if (team != null) team.removeEntry(getPlayer().getName());
        Document leaveDoc = new Document("date", Date.from(Instant.now()));
        leaveDoc.append("type", "LEAVE");
        leaveDoc.append("uuid", getPlayer().getUniqueId().toString());

        try {
            Core.getInstance().getPluginDB().getCollection("PlayerConnections").insertOne(leaveDoc);
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        }
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
     * Updates the player's armor to match their selected cosmetics
     */
    public void updateArmor() {
        PlayerInventory inventory = getPlayer().getInventory();
        ItemStack helm, chest, legs, boots;
        helm = inventory.getHelmet();
        chest = inventory.getChestplate();
        legs = inventory.getLeggings();
        boots = inventory.getBoots();
        
        /*
        activeCosmetics.clear();
        
        CosmeticArmor cosmetic;
        if (helm != null && (cosmetic = CosmeticArmor.getArmor(helm)) != null) activeCosmetics.add(cosmetic);
        if (chest != null && (cosmetic = CosmeticArmor.getArmor(chest)) != null) activeCosmetics.add(cosmetic);
        if (legs != null && (cosmetic = CosmeticArmor.getArmor(legs)) != null) activeCosmetics.add(cosmetic);
        if (boots != null && (cosmetic = CosmeticArmor.getArmor(boots)) != null) activeCosmetics.add(cosmetic);
        */
    }

    /**
     * Updates effects that player's cosmetics cosmetics should be
     */
    public void updateArmorEffects() {
        if (!isInBattle()) {
            /*
            for (CosmeticArmor armor : activeCosmetics) {
                getPlayer().addPotionEffect(new PotionEffect(armor.getEffectType(), 39, armor.getAmplitude(), true, false));
            }
             */
        }
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
        return (getRank().hasPermission(Rank.BUILDER) && !getPlayer().getGameMode().equals(org.bukkit.GameMode.SURVIVAL) && !isInBattle());
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
        PersonalScoreboard.updatePlayerRank(this);
        getPlayer().setOp(getRank().getHasOp());
        
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);
        packet.getPlayerInfoDataLists().write(0, Lists.newArrayList(new PlayerInfoData(
                WrappedGameProfile.fromPlayer(getPlayer()),
                1,
                EnumWrappers.NativeGameMode.fromBukkit(getPlayer().getGameMode()),
                WrappedChatComponent.fromText(getDisplayName()))));
        Core.sendPacketAll(packet);

        permissions.getPermissions().clear();
        
        for (String p : CommandTemplate.getAllPermissions()) {
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
            TempRank tr = it.next();
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

    /**
     * @param name ScoreName
     * @return Whether score exists
     */
    public boolean hasScore(String name) {
        return scores.containsKey(name);
    }

    /**
     * Returns score of a player (Can refer to elo, points, time, ...)
     *
     * @param name ScoreName
     * @return Score
     */
    public Integer getScore(String name) {
        if (!scores.containsKey(name)) return 0;
        return scores.get(name);
    }

    /**
     * Sets score
     *
     * @param name ScoreName
     * @param score Score
     */
    public void setScore(String name, int score) {
        scores.put(name, score);
    }

    /**
     * Sets score if the new value is higher than the old
     *
     * @param name ScoreName
     * @param score Score
     */
    public void checkScore(String name, int score) {
        if (scores.containsKey(name) &&
                scores.get(name) < score) {
            scores.put(name, score);
        }
    }

    /**
     * Returns player's location, or if they're offline their lastLocation (/back)
     * Not really that useful for offline players atm
     *
     * @return Player Location
     */
    public Location getLocation() {
        if (!isOnline()) {
            return this.lastLocation;
        } else {
            return getPlayer().getLocation();
        }
    }

    /**
     * Sets back location
     */
    public void saveLastLocation() {
        lastLocation = getPlayer().getLocation();
    }
    /**
     * Teleports player to a warp location if they have permissions
     */
    public boolean warp(Warp warp) {
        if (getRank().hasPermission(warp.getMinRank())) {
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
        Location loc = getLocation().clone();
        
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
        if (getGameMode().equals(GameMode.CREATIVE)) return;
        InventoryMenuItemHotbar.fillHotbar(this);
    }

    /**
     * Returns player's spawn location
     * Uses warp folder called spawn, if it doesn't exist then SpawnLocation of the default world
     *
     * @return
     */
    public Location getSpawnLocation() {
        Set<Warp> warps = Warp.getWarps("spawn");
        if (warps != null && !warps.isEmpty()) {
            Random random = new Random();
            return ((Warp) warps.toArray()[(random.nextInt(warps.size()))]).getLocation();
        } else {
            return Core.DEFAULT_WORLD.getSpawnLocation().clone();
        }
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

    /**
     * Set player's current InventoryMenuContainer
     *
     * @param inventoryMenuContainer InventoryMenuContainer
     */
    public void setInventoryMenuContainer(InventoryMenuContainer inventoryMenuContainer) {
        if (inventoryMenuContainer != null) {
            inventoryPage = 0;
            ItemStack item = getPlayer().getItemOnCursor();
            getPlayer().setItemOnCursor(null);
            inventoryMenuContainer.openInventory(this);
            getPlayer().setItemOnCursor(item);
            this.inventoryMenuContainer = inventoryMenuContainer;
        } else {
            this.inventoryMenuContainer = null;
        }
    }

    /**
     * Set player's current InventoryMenuContainer based on linked container of item
     *
     * @param inventoryMenuItem InventoryMenuItem
     */
    public void setInventoryMenuItem(InventoryMenuItem inventoryMenuItem) {
        if (inventoryMenuItem != null) {
            inventoryPage = 0;
            if (inventoryMenuItem.hasLinkedContainer()) {
                ItemStack item = getPlayer().getItemOnCursor();
                getPlayer().setItemOnCursor(null);
                inventoryMenuItem.getLinkedContainer().openInventory(this);
                getPlayer().setItemOnCursor(item);
                inventoryMenuContainer = inventoryMenuItem.getLinkedContainer();
            } else {
                getPlayer().closeInventory();
                inventoryMenuContainer = null;
            }
        } else {
            getPlayer().closeInventory();
            inventoryMenuContainer = null;
        }
    }

    /**
     * Updates the player's current ivnentoryMenuContainer
     * For refreshing/page change
     */
    public void refreshInventoryMenuContainer() {
        if (inventoryMenuContainer != null) {
            InventoryMenuContainer im = inventoryMenuContainer;
            ItemStack item = getPlayer().getItemOnCursor();
            getPlayer().setItemOnCursor(null);
            inventoryMenuContainer.openInventory(this);
            getPlayer().setItemOnCursor(item);
            inventoryMenuContainer = im;
        }
    }

    /**
     * @return Current InventoryMenuContainer
     */
    public InventoryMenuContainer getInventoryMenuContainer() {
        return inventoryMenuContainer;
    }

    /**
     * @return Current Menu Page
     */
    public int getPage() {
        return inventoryPage;
    }
    public void nextPage() {
        inventoryPage++;
    }
    public void prevPage() {
        inventoryPage--;
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
        return !isInBattle() && !isAfk();
    }
    
    /**
     * Whether a player is in a fake world or not
     * Main purpose is to keep hotbar items from disappearing upon afk
     *
     * @return In Global World
     */
    public final boolean isInGlobal() {
       return !isInBattle();
    }
    
    public final LinkedHashSet<FakeWorld> getFakeWorlds() {
        return fakeWorlds;
    }
    
    public final void joinFakeWorld(FakeWorld fakeWorld) {
        fakeWorlds.add(fakeWorld);
    }
    
    public final void leaveFakeWorld(FakeWorld fakeWorld) {
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
        savePregameState();
        CorePlugin.addIngamePlayerName(this);
    }
    
    /**
     * Removes player from CorePlugins global player battles
     *
     * @param exitLocation Location to attempt to teleport player to
     */
    public final void leaveBattle(Location exitLocation) {
        loadPregameState(exitLocation);
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
     * @return BattleState
     */
    public final BattleState getBattleState() {
        return battleState;
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
