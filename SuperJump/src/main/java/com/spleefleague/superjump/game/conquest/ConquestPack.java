/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.conquest;

import com.mongodb.client.MongoCursor;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import com.spleefleague.superjump.SuperJump;
import com.spleefleague.superjump.game.SJMode;
import com.spleefleague.superjump.player.SuperJumpPlayer;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bson.Document;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class ConquestPack extends DBEntity {
    
    private static final Map<String, ConquestPack> conquestPacks = new HashMap<>();
    
    public static void init() {
        MongoCursor<Document> mc = SuperJump.getInstance().getPluginDB().getCollection("ConquestPacks").find().iterator();
        while (mc.hasNext()) {
            Document doc = mc.next();
            ConquestPack pack = new ConquestPack();
            pack.load(doc);
            conquestPacks.put(pack.getName(), pack);
        }
    }
    
    public static Set<String> getPackNames() {
        return conquestPacks.keySet();
    }
    
    public static ConquestPack getPack(String name) {
        return conquestPacks.get(name);
    }
    
    public static Collection<ConquestPack> getAllPacks() {
        return conquestPacks.values();
    }
    
    @DBField
    private String name;
    @DBField
    private String description;
    @DBField
    private List<String> arenas;
    
    public String getName() {
        return name;
    }
    
    public InventoryMenuItem createMenu() {
        InventoryMenuItem menu = InventoryMenuAPI.createItemDynamic()
                .setName("Pack: " + name)
                .setDescription(description)
                .setDisplayItem(Material.DIAMOND_AXE, 20)
                .createLinkedContainer("CQ Pack: " + name);
        
        for (String arenaName : arenas) {
            Arena arena = Arenas.get(arenaName, SJMode.CONQUEST.getBattleMode());
            if (arena != null) {
                menu.getLinkedChest()
                        .addMenuItem(InventoryMenuAPI.createItemDynamic()
                                .setName(arena.getName())
                                .setDescription(cp -> {
                                    String desc = arena.getDescription() + "\n";
                                    SuperJumpPlayer sjp = (SuperJumpPlayer) SuperJump.getInstance().getPlayers().get(cp);
                                    //desc += sjp.getConquestStats().getDescription(arena);
                                    return desc;
                                })
                                .setDisplayItem(cp -> {
                                    SuperJumpPlayer sjp = (SuperJumpPlayer) SuperJump.getInstance().getPlayers().get(cp);
                        /*
                        switch (sjp.getConquestStats().getStars(arena)) {
                            case 3: return InventoryMenuUtils.createCustomItem(Material.DIAMOND_AXE, 16);
                            case 2: return InventoryMenuUtils.createCustomItem(Material.DIAMOND_AXE, 17);
                            case 1: return InventoryMenuUtils.createCustomItem(Material.DIAMOND_AXE, 18);
                            default: return InventoryMenuUtils.createCustomItem(Material.DIAMOND_AXE, 19);
                        }
                         */
                                    return InventoryMenuUtils.createCustomItem(Material.DIAMOND_AXE, 16);
                                }))
                        .setAction(cp -> SuperJump.getInstance().queuePlayer(SJMode.CONQUEST.getBattleMode(), cp, arena));
            }
        }
        
        return menu;
    }
    
}
