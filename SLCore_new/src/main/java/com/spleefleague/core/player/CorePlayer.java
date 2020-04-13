/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.player;

import com.spleefleague.core.util.database.Checkpoint;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.annotation.DBField;
import com.spleefleague.core.annotation.DBLoad;
import com.spleefleague.core.annotation.DBSave;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.infraction.Infraction;
import com.spleefleague.core.io.converter.LocationConverter;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainer;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuItemHotbar;
import com.spleefleague.core.menus.HeldItemMenu;
import com.spleefleague.core.party.Party;
import com.spleefleague.core.player.cosmetics.CosmeticArmor;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.request.Request;
import com.spleefleague.core.scoreboard.PersonalScoreboard;
import com.spleefleague.core.util.TpCoord;
import com.spleefleague.core.util.Warp;
import com.spleefleague.core.util.database.DBPlayer;
import com.spleefleague.core.vendor.KeyItem;
import com.spleefleague.core.vendor.VendorItem;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
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
 * @author NickM13
 */
public class CorePlayer extends DBPlayer {
    
    private static Map<UUID, PermissionAttachment> perms = new HashMap<>();
    
    /**
     * Database variables
     */
    
    @DBField(serializer=LocationConverter.class)
    private Location lastLocation;
    @DBField
    private Checkpoint checkpoint;
    
    private Rank rank;
    private final List<TempRank> tempRanks;
    
    @DBField
    protected Map<String, Integer> scores = new HashMap<>();
    
    @DBField
    private Boolean vanished;
    
    @DBField
    private Integer coins;
    @DBField
    private Set<String> keys = new HashSet<>();
    
    // Non-afk time
    @DBField
    private Long playTime;
    
    private Map<Integer, Request> requests = new HashMap<>();

    // Options
    @DBField
    private String gameMode = GameMode.SURVIVAL.name();
    
    @DBField
    private CorePlayerOptions options;
    
    /**
     * Non-database variables
     */
    
    private Party party;
    
    private long urlTime;
    
    private int inventoryPage;
    private InventoryMenuContainer inventoryMenu;
    private ChatChannel chatChannel;
    
    // 5 min, sets player to afk
    private static final long AFK_WARNING = 1000L * 60 * 4 + 30;
    private static final long AFK_TIMEOUT = 1000L * 60 * 5;
    private long lastAction;
    private boolean afk;
    
    private VendorItem heldItem = null;
    // Used for cosmetic reasons
    private Map<String, String> selectedItems = new HashMap<>();
    
    private final Set<CosmeticArmor> activeCosmetics = new HashSet<>();
    
    private Player replyPlayer = null;
    
    public CorePlayer() {
        super();
        chatChannel = ChatChannel.getDefaultChannel();
        lastLocation = null;
        checkpoint = null;
        tempRanks = new ArrayList<>();
        options = new CorePlayerOptions();
        vanished = false;
        party = null;
        coins = 0;
        playTime = 0L;
        lastAction = System.currentTimeMillis();
        afk = false;
    }
    
    @Override
    public void init() {
        if (!perms.containsKey(getPlayer().getUniqueId())) {
            PermissionAttachment attachment = getPlayer().addAttachment(Core.getInstance());
            perms.put(getPlayer().getUniqueId(), attachment);
        }
        setRank(rank);
        updateArmor();
        PersonalScoreboard.initPlayerScoreboard(this);
    }
    
    public CorePlayerOptions getOptions() {
        return options;
    }
    
    @DBSave(fieldname="heldItem")
    protected Document saveHeldItem() {
        if (heldItem == null) {
            return null;
        }
        return new Document("type", heldItem.getType()).append("identifier", heldItem.getIdentifier());
    }
    
    @DBLoad(fieldname="heldItem")
    protected void loadHeldItem(Document doc) {
        if (doc != null) {
            Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
                VendorItem vendorItem = VendorItem.getVendorItem(doc.get("type", String.class), doc.get("identifier", String.class));
                if (vendorItem != null)
                    this.heldItem = vendorItem;
                refreshHotbar();
            }, 20L);
        }
    }
    
    @Override
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode.name();
        getPlayer().setGameMode(gameMode);
    }
    public GameMode getGameMode() {
        return GameMode.valueOf(gameMode);
    }
    
    public long getPlayTime() {
        return playTime;
    }
    
    public void checkAfk() {
        if (lastAction + AFK_TIMEOUT < System.currentTimeMillis()) {
            setAfk(true);
        } else if (lastAction + AFK_WARNING < System.currentTimeMillis()
                && getRank().equals(Rank.DEFAULT)) {
            Core.sendMessageToPlayer(this, "You will be kicked for AFK in 30 seconds!");
        }
    }
    public boolean setLastAction() {
        if (this.afk) {
            setAfk(false);
            lastAction = System.currentTimeMillis();
            return true;
        } else {
            playTime += System.currentTimeMillis() - lastAction;
            lastAction = System.currentTimeMillis();
        }
        return false;
    }
    public void setAfk(boolean state) {
        if (afk != state) {
            if (state == true) {
                if (getRank().equals(Rank.DEFAULT)) {
                    getPlayer().kickPlayer("Kicked for AFK!");
                    return;
                } else {
                    getPlayer().getInventory().setItemInOffHand(InventoryMenuAPI.createCustomItem("AFK", Material.DIAMOND_HOE, 253));
                }
            } else {
                getPlayer().getInventory().setItemInOffHand(null);
            }
            afk = state;
            Core.sendMessageToPlayer(this, "You are " + (afk ? "now afk" : "no longer afk"));
        }
    }
    public boolean isAfk() {
        return afk;
    }
    
    /**
     * Returns 0 if not muted
     * 1 if public muted
     * 2 if secret muted
     * 
     * @return 
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
    
    public void setSelectedItem(String type, String id) {
        if (selectedItems.containsKey(type) &&
                heldItem == VendorItem.getVendorItem(type, selectedItems.get(type))) {
            heldItem = VendorItem.getVendorItem(type, id);
        }
        selectedItems.put(type, id);
        refreshHotbar();
    }
    public Map<String, String> getSelectedItems() {
        return selectedItems;
    }
    
    public void addKey(KeyItem key) {
        keys.add(key.getIdentifier());
        Core.getInstance().sendMessage(this, "You have received " + key.getDisplayName());
    }
    public void removeKey(KeyItem key) {
        keys.remove(key.getIdentifier());
        Core.getInstance().sendMessage(this, "You have lost " + key.getDisplayName());
    }
    public Set<String> getKeys() {
        return keys;
    }
    
    public boolean hasSelectedHeldItem() {
        return heldItem != null;
    }
    public void setHeldItem(VendorItem item) {
        heldItem = item;
        refreshHotbar();
    }
    public VendorItem getHeldItem() {
        if (heldItem == null)
            return HeldItemMenu.getDefault();
        return heldItem;
    }
    
    public void activateHeldItem() {
        if (heldItem != null)
            heldItem.activate(this);
    }
    
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
            
        }
    }
    
    public void setVanished(boolean vanished) {
        this.vanished = vanished;
    }
    
    @Override
    public boolean isVanished() {
        return vanished;
    }
    
    public void joinParty(Party party) {
        this.party = party;
    }
    
    public void leaveParty() {
        if (party != null) {
            party = null;
        }
    }
    
    public Party getParty() {
        return party;
    }
    
    @Override
    protected void newPlayer() {
        rank = Rank.getDefaultRank();
    }
    
    public boolean canSendUrl() {
        if (urlTime > System.currentTimeMillis()) {
            urlTime = 0;
            return true;
        }
        return false;
    }
    public void allowUrl() {
        urlTime = System.currentTimeMillis() + 30 * 1000;
    }
    
    public int getCoins() {
        return coins;
    }
    public void setCoins(int coins) {
        this.coins = coins;
    }
    public void addCoins(int coins) {
        this.coins += coins;
    }
    
    public void updateArmor() {
        PlayerInventory inventory = getPlayer().getInventory();
        ItemStack helm, chest, legs, boots;
        helm = inventory.getHelmet();
        chest = inventory.getChestplate();
        legs = inventory.getLeggings();
        boots = inventory.getBoots();
        
        activeCosmetics.clear();
        
        CosmeticArmor cosmetic;
        if (helm != null && (cosmetic = CosmeticArmor.getArmor(helm)) != null) {
            activeCosmetics.add(cosmetic);
        }
        if (chest != null && (cosmetic = CosmeticArmor.getArmor(chest)) != null) {
            activeCosmetics.add(cosmetic);
        }
        if (legs != null && (cosmetic = CosmeticArmor.getArmor(legs)) != null) {
            activeCosmetics.add(cosmetic);
        }
        if (boots != null && (cosmetic = CosmeticArmor.getArmor(boots)) != null) {
            activeCosmetics.add(cosmetic);
        }
    }
    public void updateArmorEffects() {
        if (!CorePlugin.isInBattleGlobal(getPlayer())) {
            for (CosmeticArmor armor : activeCosmetics) {
                getPlayer().addPotionEffect(new PotionEffect(armor.getEffectType(), 39, armor.getAmplitude(), true, false), true);
            }
        }
    }
    
    public ItemStack getSkull() {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullmeta = (SkullMeta)skull.getItemMeta();
        skullmeta.setOwningPlayer(getPlayer());
        skull.setItemMeta(skullmeta);
        return skull;
    }
    
    @Override
    public String getDisplayName() {
        return getRank().getColor() + this.getName() + Chat.DEFAULT;
    }
    
    @Override
    public String getDisplayNamePossessive() {
        return getRank().getColor() + this.getName() + "'s" + Chat.DEFAULT;
    }
    
    public TextComponent getChatName() {
        TextComponent text = new TextComponent(getName());
        
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to send a message").create()));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + getName()));
        text.setColor(getRank().getColor().asBungee());
        
        return text;
    }
    
    @DBSave(fieldname="tempRanks")
    protected List<Document> saveTempRanks() {
        List<Document> docs = new ArrayList<>();
        
        for (TempRank tr : tempRanks) {
            docs.add(new Document("rank", tr.rank.getName()).append("expireTime", tr.expireTime));
        }
        
        return docs;
    }
    @DBLoad(fieldname="tempRanks")
    protected void loadTempRanks(List<Document> ranks) {
        for (Document d : ranks) {
            tempRanks.add(new TempRank(d.get("rank", String.class), d.get("expireTime", Long.class)));
        }
    }

    public void invsee(CorePlayer target) {
        getPlayer().openInventory(target.getPlayer().getInventory());
    }
    public void invcopy(CorePlayer target) {
        loadPregameState(null);
        pregameState.save(PregameState.PSFlag.INVENTORY);
        getPlayer().getInventory().setContents(target.getInventory());
    }

    public boolean canBuild() {
        return (rank.hasPermission(Rank.BUILDER) && !getPlayer().getGameMode().equals(GameMode.SURVIVAL) && !CorePlugin.isInBattleGlobal(getPlayer()));
    }
    
    public boolean canBreak() {
        return canBuild() && !(getPlayer().getInventory().getItemInMainHand().getType().toString().toLowerCase().contains("sword"));
    }

    @DBLoad(fieldname="rank")
    protected void loadRank(String str) {
        rank = Rank.getRank(str);
    }
    @DBSave(fieldname="rank")
    protected String saveRank() {
        return rank.getName();
    }

    public void updateRank() {
        Iterator<TempRank> it = tempRanks.iterator();
        while (it.hasNext()) {
            TempRank tr = it.next();
            if (tr.expireTime < System.currentTimeMillis()) {
                it.remove();
            }
        }
        
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
        
        PermissionAttachment pperms = perms.get(getPlayer().getUniqueId());
        pperms.getPermissions().clear();
        
        for (String p : CommandTemplate.getAllPermissions()) {
            boolean has = false;
            if (rank.hasPermission(p)) {
                has = true;
            } else {
                for (TempRank tr : tempRanks) {
                    if (tr.rank.hasExclusivePermission(p)) {
                        has = true;
                        break;
                    }
                }
            }
            pperms.setPermission(p, has);
        }
        
        getPlayer().updateCommands();
    }
    public void setRank(Rank rank) {
        this.rank = rank;
        
        updateRank();
    }
    public boolean addTempRank(String rank, Integer hours) {
        TempRank tempRank = new TempRank(rank, hours * 60L * 60L * 1000L + System.currentTimeMillis());
        if (tempRank.rank != null) {
            Core.getInstance().sendMessage(this, "Added temp rank " + rank + " for " + hours + " hours");
            tempRanks.add(tempRank);
            updateRank();
            return true;
        }
        return false;
    }
    public void clearTempRank() {
        Iterator<TempRank> it = tempRanks.iterator();
        while (it.hasNext()) {
            TempRank tr = it.next();
            it.remove();
        }
        updateRank();
    }
    public Rank getPermanentRank() {
        return rank;
    }
    public Rank getRank() {
        if (tempRanks.size() > 0) {
            TempRank highestRank = null;
            Iterator<TempRank> it = tempRanks.iterator();
            while (it.hasNext()) {
                TempRank tr = it.next();
                if (highestRank == null ||
                        tr.rank.getLadder() > highestRank.rank.getLadder()) {
                    highestRank = tr;
                }
            }
            if (highestRank != null) {
                return highestRank.rank;
            }
        }
        return rank;
    }
    
    public boolean hasScore(String name) {
        return scores.containsKey(name);
    }
    public Integer getScore(String name) {
        if (!scores.containsKey(name)) return 0;
        return scores.get(name);
    }
    public void setScore(String name, int score) {
        scores.put(name, score);
    }
    public void checkScore(String name, int score) {
        if (scores.containsKey(name) &&
                scores.get(name) < score) {
            scores.put(name, score);
        }
    }

    public Location getLocation() {
        if (!isOnline()) {
            return this.lastLocation;
        } else {
            return getPlayer().getLocation();
        }
    }

    public void saveLastLocation() {
        lastLocation = getPlayer().getLocation();
    }
    public boolean warp(Warp warp) {
        if (getRank().hasPermission(warp.getMinRank())) {
            teleport(warp.getLocation());
            return true;
        }
        return false;
    }
    public void teleport(Vector vec) {
        teleport(vec.getX(), vec.getY(), vec.getZ());
    }
    public void teleport(Location loc) {
        saveLastLocation();
        getPlayer().teleport(loc, PlayerTeleportEvent.TeleportCause.COMMAND);
    }
    public void teleport(double x, double y, double z) {
        Location loc = new Location(getLocation().getWorld(), x, y, z, getLocation().getYaw(), getLocation().getPitch());
        teleport(loc);
    }
    public void teleport(TpCoord x, TpCoord y, TpCoord z, @Nullable Double pitch, @Nullable Double yaw) {
        Location loc = getLocation().clone();
        
        TpCoord.apply(loc, x, y, z);
        if (pitch != null) loc.setPitch(pitch.floatValue());
        if (yaw != null) loc.setYaw(yaw.floatValue());
        
        teleport(loc);
    }
    
    public void refreshHotbar() {
        if (getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
        getPlayer().getInventory().clear();
        for (InventoryMenuItemHotbar item : InventoryMenuAPI.getHotbarItems()) {
            if (item.isVisible(this))
                getPlayer().getInventory().setItem(item.getSlot(), item.createItem(this));
        }
    }
    public Location getSpawnLocation() {
        Warp spawn = Warp.getWarp("spawn");
        if (spawn != null) {
            return spawn.getLocation();
        } else {
            return Core.DEFAULT_WORLD.getSpawnLocation().clone().add(0, 0, 0);
        }
    }
    public void gotoSpawn() {
        refreshHotbar();
        teleport(getSpawnLocation());
    }
    
    public void setCheckpoint(String warp, int duration) {
        checkpoint = new Checkpoint(warp, duration);
    }
    
    public void checkpoint() {
        if (checkpoint != null && checkpoint.isActive()) {
            teleport(checkpoint.getLocation());
        }
    }

    public Location getLastLocation() {
        return lastLocation;
    }
    public Checkpoint getCheckpoint() {
        return checkpoint;
    }
    
    public void setReply(Player player) {
        replyPlayer = player;
    }
    public Player getReply() {
        return replyPlayer;
    }

    public void setChatChannel(ChatChannel cc) {
        chatChannel = cc;
        Chat.sendMessageToPlayer(this, "Chat Channel set to " + cc.getName());
    }

    public ChatChannel getChatChannel() {
        return chatChannel;
    }

    public void sendMessage(String string) {
        getPlayer().sendMessage(string);
    }
    
    public void sendMessage(BaseComponent message) {
        getPlayer().spigot().sendMessage(message);
    }

    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        getPlayer().sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    public void setInventoryMenuContainer(InventoryMenuContainer inventoryMenuContainer) {
        if (inventoryMenuContainer != null) {
            inventoryPage = 0;
            ItemStack item = getPlayer().getItemOnCursor();
            getPlayer().setItemOnCursor(null);
            inventoryMenuContainer.openInventory(this);
            getPlayer().setItemOnCursor(item);
            inventoryMenu = inventoryMenuContainer;
        } else {
            inventoryMenu = null;
        }
    }
    public void setInventoryMenuItem(InventoryMenuItem inventoryMenuItem) {
        if (inventoryMenuItem != null) {
            inventoryPage = 0;
            if (inventoryMenuItem.hasLinkedContainer()) {
                ItemStack item = getPlayer().getItemOnCursor();
                getPlayer().setItemOnCursor(null);
                inventoryMenuItem.getLinkedContainer().openInventory(this);
                getPlayer().setItemOnCursor(item);
                inventoryMenu = inventoryMenuItem.getLinkedContainer();
            } else {
                getPlayer().closeInventory();
                inventoryMenu = null;
            }
        } else {
            getPlayer().closeInventory();
            inventoryMenu = null;
        }
    }
    public void refreshInventoryMenuContainer() {
        if (inventoryMenu != null) {
            InventoryMenuContainer im = inventoryMenu;
            ItemStack item = getPlayer().getItemOnCursor();
            getPlayer().setItemOnCursor(null);
            inventoryMenu.openInventory(this);
            getPlayer().setItemOnCursor(item);
            inventoryMenu = im;
        }
    }

    public InventoryMenuContainer getInventoryMenu() {
        return inventoryMenu;
    }
    
    public int getPage() {
        return inventoryPage;
    }
    public void nextPage() {
        inventoryPage++;
    }
    public void prevPage() {
        inventoryPage--;
    }
    public void savePage() {
        //inventoryMenu.saveEdit();
    }
    
    public int getPing() {
        try {
            Object entityPlayer = getPlayer().getClass().getMethod("getHandle").invoke(getPlayer());
            return (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
            return -1;
        }
    }
    
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
    
}
