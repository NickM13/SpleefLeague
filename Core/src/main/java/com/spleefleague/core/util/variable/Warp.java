/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.util.variable;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.menu.*;
import com.spleefleague.core.player.CoreOfflinePlayer;
import com.spleefleague.core.player.rank.CoreRank;

import java.util.*;
import java.util.stream.Collectors;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class Warp extends DBEntity {

    private static final SortedMap<String, Warp> WARPS = new TreeMap<>();
    private static MongoCollection<Document> warpCollection;
    private static final SortedMap<String, SortedSet<String>> FOLDERS = new TreeMap<>();
    private static final String DEFAULT_FOLDER = ".";

    /**
     * Loads all warps from the Warps collection
     */
    public static void init() {
        warpCollection = Core.getInstance().getPluginDB().getCollection("Warps");
        refresh();
    }

    public static void refresh() {
        FOLDERS.clear();
        WARPS.clear();
        FOLDERS.put(DEFAULT_FOLDER, new TreeSet<>());
        for (Document doc : warpCollection.find()) {
            Warp warp = new Warp();
            warp.load(doc);
            WARPS.put(warp.getIdentifier(), warp);
        }
    }

    public static InventoryMenuContainerChest createMenuContainer(String folderName) {
        InventoryMenuContainerChest menuContainer = InventoryMenuAPI.createContainer()
                .setTitle(cp -> " Folder " + cp.getMenu().getMenuTag("folderName", String.class))
                .setOpenAction((container, cp) -> {
                    cp.getMenu().setMenuTag("folderName", folderName == null ? DEFAULT_FOLDER : folderName);
                    cp.getMenu().setMenuTag("warpPage", 0);
                })
                .setRefreshAction((container, cp) -> {
                    container.clearUnsorted();
                    SortedSet<String> warpNames = FOLDERS.get(cp.getMenu().getMenuTag("folderName", String.class));
                    for (String warpName : warpNames) {
                        Warp warp = WARPS.get(warpName);
                        container.addMenuItem(InventoryMenuAPI.createItemDynamic()
                                .setName(ChatColor.YELLOW + "" + ChatColor.BOLD + warp.getName())
                                .setDisplayItem(warp.getDisplayItem())
                                .setAction(cp2 -> cp2.warp(warp)));
                    }
                });

        menuContainer.addStaticItem(InventoryMenuAPI.createItemDynamic()
                        .setName(ChatColor.RED + "" + ChatColor.BOLD + "Previous Page")
                        .setDescription("")
                        .setDisplayItem(InventoryMenuUtils.MenuIcon.PREVIOUS_GRAY.getIconItem())
                        .setVisibility(cp -> cp.getMenu().getMenuTag("warpPage", Integer.class) > 0)
                        .setAction(cp -> cp.getMenu().setMenuTag("warpPage", cp.getMenu().getMenuTag("warpPage", Integer.class) - 1))
                        .setCloseOnAction(false),
                2, 4);

        menuContainer.addStaticItem(InventoryMenuAPI.createItemDynamic()
                        .setName(ChatColor.GREEN + "" + ChatColor.BOLD + "Next Page")
                        .setDescription("")
                        .setDisplayItem(InventoryMenuUtils.MenuIcon.NEXT_GRAY.getIconItem())
                        .setVisibility(cp -> cp.getMenu().getMenuTag("warpPage", Integer.class) < FOLDERS.get(cp.getMenu().getMenuTag("folderName", String.class)).size() / menuContainer.getPageItemTotal())
                        .setAction(cp -> cp.getMenu().setMenuTag("warpPage", cp.getMenu().getMenuTag("warpPage", Integer.class) + 1))
                        .setCloseOnAction(false),
                6, 4);

        return menuContainer;
    }

    public static void close() {

    }

    /**
     * Returns all warps available to a player, used for command tab completes
     *
     * @param pi Prior Info
     * @return Warp Name Set
     */
    public static Set<String> getWarpNames(CoreCommand.PriorInfo pi) {
        Set<String> warpNames = new HashSet<>();
        for (Warp warp : WARPS.values()) {
            if (warp.isAvailable(pi.getCorePlayer())) {
                warpNames.add(warp.getIdentifier());
            }
        }
        return warpNames;
    }

    /**
     * Return all warp names, used in command tab completes
     *
     * @return Warp Name Set
     */
    public static Set<Warp> getWarps(String folderName) {
        if (!FOLDERS.containsKey(folderName)) return Collections.EMPTY_SET;
        return FOLDERS.get(folderName).stream().map(WARPS::get).collect(Collectors.toSet());
    }

    /**
     * Returns all warps in TextComponent format to allow
     * clicking on links names to warp instead of having to type the warp name
     *
     * @return Text Component
     */
    public static TextComponent getWarpsFormatted() {
        TextComponent message = new TextComponent("");
        TextComponent warpstr;

        Iterator<HashMap.Entry<String, Warp>> wit = WARPS.entrySet().iterator();

        boolean first = true;
        while (wit.hasNext()) {
            Warp warp = wit.next().getValue();
            if (!first) {
                message.addExtra(new TextComponent(", "));
            } else first = false;
            warpstr = new TextComponent(warp.getIdentifier());
            warpstr.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to warp to '" + warpstr.getText() + "'").create()));
            warpstr.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/warp " + warp.getIdentifier()));
            warpstr.setColor(ChatColor.valueOf(Chat.getColor("DEFAULT").name()).asBungee());
            message.addExtra(warpstr);
        }

        return message;
    }

    /**
     * Gets a warp by name
     *
     * @param name Warp Name
     * @return Warp
     */
    public static Warp getWarp(String name) {
        return WARPS.get(name);
    }

    /**
     * Creates a warp by a name at a location
     *
     * @param identifier Warp Name
     * @param loc        Location
     */
    public static int setWarp(String identifier, Location loc) {
        if (identifier.contains(":")) {
            String[] args = identifier.split(":", 2);
            if (args[0].isEmpty() || args[1].isEmpty()) {
                return 1;
            }
        }
        if (WARPS.containsKey(identifier)) {
            delWarp(identifier);
            return 2;
        }
        Warp warp = new Warp(identifier, loc);
        WARPS.put(warp.getIdentifier(), warp);
        warp.save(warpCollection);
        return 0;
    }

    /**
     * Deletes a warp by name
     *
     * @param identifier Warp Name
     * @return Success
     */
    public static boolean delWarp(String identifier) {
        if (WARPS.containsKey(identifier)) {
            Warp warp = WARPS.remove(identifier);
            warp.unsave(warpCollection);
            FOLDERS.get(warp.getFolder()).remove(identifier);
            if (FOLDERS.get(warp.getFolder()).isEmpty() && !warp.getFolder().equals(DEFAULT_FOLDER)) {
                FOLDERS.remove(warp.getFolder());
            }
            return true;
        }
        return false;
    }

    @DBField public CoreLocation location;
    @DBField private Material material = Material.SAND;
    @DBField private Integer customModelData = 0;
    private ItemStack displayItem;
    protected String warpName;
    protected String folderName = null;

    public Warp() {

    }

    public Warp(String identifier, Location location) {
        this.identifier = identifier;
        this.location = new CoreLocation(location);
        initFolder();
        updateDisplayItem();
    }

    @Override
    public void afterLoad() {
        initFolder();
        updateDisplayItem();
    }

    public void updateDisplayItem() {
        displayItem = InventoryMenuUtils.createCustomItem(material, customModelData);
    }

    public void setDisplayItem(Material material, int customModelData) {
        this.material = material;
        this.customModelData = customModelData;
        updateDisplayItem();
    }

    private void initFolder() {
        if (identifier.contains(":")) {
            String[] args = identifier.split(":", 2);
            if (!args[0].isEmpty() && !args[1].isEmpty()) {
                folderName = args[0];
                warpName = args[1];
                if (!FOLDERS.containsKey(folderName)) {
                    FOLDERS.put(folderName, new TreeSet<>());
                }
                FOLDERS.get(folderName).add(identifier);
                return;
            }
        }
        warpName = identifier;
        folderName = DEFAULT_FOLDER;
        FOLDERS.get(folderName).add(identifier);
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public String getName() {
        return warpName;
    }

    public String getFolder() {
        return folderName;
    }

    public Location getLocation() {
        return location.toLocation();
    }

    public boolean isAvailable(CoreOfflinePlayer cp) {
        CoreRank rank = Core.getInstance().getRankManager().getRank(folderName);
        if (rank == null) {
            return cp.getRank().hasPermission(CoreRank.TEMP_MOD, Lists.newArrayList(CoreRank.BUILDER));
        } else {
            return cp.getRank().hasPermission(rank);
        }
    }

}
