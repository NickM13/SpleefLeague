package com.spleefleague.core.game.arena;

import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainerChest;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.util.variable.Dimension;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * @author NickM13
 * @since 5/1/2020
 */
public class ArenaBuilder {
    
    public static InventoryMenuItem createNewItem(String modeName) {
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName("New Arena")
                .setDescription("Click to Create a New Arena")
                .setDisplayItem(Material.STICKY_PISTON)
                .setAction(cp2 -> {
                    cp2.setInventoryMenuAnvil(InventoryMenuAPI.createAnvil()
                            .setTitle("Arena Name")
                            .setFailText("Name in use or invalid!")
                            .setSuccessFunc(str -> Arenas.get(str) == null)
                            .setAction((cp3, str) -> {
                                Arena arena = Arenas.createArena(str);
                                Arenas.addArenaMode(arena.getName(), BattleMode.get(modeName));
                            })
                            .setParentContainer((InventoryMenuContainerChest) cp2.getInventoryMenuContainer()));
                })
                .setCloseOnAction(false);
        
        return menuItem;
    }
    
    public static InventoryMenuItem createExistingItem(String modeName) {
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName("Add Existing Arena")
                .setDescription("Click to add an existing arena to this mode")
                .setDisplayItem(Material.STICKY_PISTON)
                .setAction(cp -> {
                    cp.setInventoryMenuContainer(InventoryMenuAPI.createContainer()
                            .setTitle("Add Arena: " + modeName)
                            .setOpenAction((container, cp2) -> {
                                container.clearUnsorted();
                                for (Arena arena : Arenas.getAll().values()) {
                                    container.addMenuItem(InventoryMenuAPI.createItem()
                                            .setName(arena.getName())
                                            .setDisplayItem(Material.SNOW_BLOCK)
                                            .setDescription("Click to add this arena")
                                            .setAction(cp3 -> {
                                                Arenas.addArenaMode(arena.getName(), BattleMode.get(modeName));
                                            })
                                            .setCloseOnAction(false)
                                            .setParentContainer((InventoryMenuContainerChest) cp2.getInventoryMenuContainer()));
                                }
                            })
                            .setParentContainer((InventoryMenuContainerChest) cp.getInventoryMenuContainer()));
                })
                .setCloseOnAction(false);
        
        return menuItem;
    }
    
    public static InventoryMenuItem createEditItem(Arena arena) {
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName(arena.getDisplayName() + " (" + arena.getName() + ")")
                .setDescription("Click to Edit")
                .setDisplayItem(Material.DIAMOND)
                .createLinkedContainer("Editing: " + arena.getName());
        
        menuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createItem()
                .setName("Rename")
                .setDescription("Click to Rename Arena")
                .setDisplayItem(Material.STICKY_PISTON)
                .setAction(cp2 -> {
                    cp2.setInventoryMenuAnvil(InventoryMenuAPI.createAnvil()
                            .setTitle("Rename " + arena.getDisplayName())
                            .setFailText("Name in use or invalid!")
                            .setSuccessFunc(str -> Arenas.get(str) == null)
                            .setAction((cp3, str) -> {
                                Arenas.renameArena(arena.getName(), str);
                            })
                            .setParentContainer((InventoryMenuContainerChest) cp2.getInventoryMenuContainer()));
                })
                .setCloseOnAction(false));
    
        menuItem.getLinkedContainer()
                .addMenuItem(InventoryMenuAPI.createItem()
                        .setName("Remove Borders")
                        .setDescription("")
                        .setDisplayItem(Material.PAPER)
                        .setAction(cp -> {
                            cp.setInventoryMenuChest(InventoryMenuAPI.createContainer()
                                    .setTitle(arena.getDisplayName() + " Borders")
                                    .setRefreshAction((container, cp2) -> {
                                        container.clearUnsorted();
                                        int i = 0;
                                        for (Dimension border : arena.getBorders()) {
                                            Integer id = i;
                                            container.addMenuItem(InventoryMenuAPI.createItem()
                                                    .setName("Remove Border")
                                                    .setDisplayItem(Material.SPRUCE_LOG)
                                                    .setDescription(border.getLow() + "\n" + border.getHigh())
                                                    .setAction(cp3 -> {
                                                        arena.removeBorder(id);
                                                    })
                                                    .setCloseOnAction(false));
                                            i++;
                                        }
                                    })
                                    .setParentContainer(menuItem.getLinkedContainer()), true);
                        })
                        .setCloseOnAction(false));
    
        menuItem.getLinkedContainer()
                .addMenuItem(InventoryMenuAPI.createItem()
                        .setName("Remove Spawns")
                        .setDescription("")
                        .setDisplayItem(Material.OAK_BOAT)
                        .setAction(cp -> {
                            cp.setInventoryMenuChest(InventoryMenuAPI.createContainer()
                                    .setTitle(arena.getDisplayName() + " Spawns")
                                    .setRefreshAction((container, cp2) -> {
                                        container.clearUnsorted();
                                        int i = 0;
                                        for (Location loc : arena.getSpawns()) {
                                            Integer id = i;
                                            container.addMenuItem(InventoryMenuAPI.createItem()
                                                    .setName("Remove Spawn")
                                                    .setDisplayItem(Material.SPRUCE_LOG)
                                                    .setDescription(loc.toString())
                                                    .setAction(cp3 -> {
                                                        arena.removeSpawn(id);
                                                    })
                                                    .setCloseOnAction(false));
                                            i++;
                                        }
                                    })
                                    .setParentContainer(menuItem.getLinkedContainer()), true);
                        })
                        .setCloseOnAction(false));
    
        menuItem.getLinkedContainer()
                .addMenuItem(InventoryMenuAPI.createItem()
                        .setName("Remove Structures")
                        .setDescription("")
                        .setDisplayItem(Material.DARK_PRISMARINE_STAIRS)
                        .setAction(cp -> {
                            cp.setInventoryMenuChest(InventoryMenuAPI.createContainer()
                                    .setTitle(arena.getDisplayName() + " Structures")
                                    .setRefreshAction((container, cp2) -> {
                                        container.clearUnsorted();
                                        for (String structure : arena.getStructureNames()) {
                                            container.addMenuItem(InventoryMenuAPI.createItem()
                                                    .setName("Remove Spawn")
                                                    .setDisplayItem(Material.SPRUCE_LOG)
                                                    .setDescription(structure)
                                                    .setAction(cp3 -> {
                                                        arena.removeStructure(structure);
                                                    })
                                                    .setCloseOnAction(false));
                                        }
                                    })
                                    .setParentContainer(menuItem.getLinkedContainer()), true);
                        })
                        .setCloseOnAction(false));
        
        return menuItem;
    }
    
}
