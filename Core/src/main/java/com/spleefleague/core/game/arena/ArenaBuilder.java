package com.spleefleague.core.game.arena;

import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainerChest;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.util.variable.Position;
import org.bukkit.Material;

/**
 * @author NickM13
 * @since 5/1/2020
 */
public class ArenaBuilder {
    
    public static InventoryMenuItem createNewItem(String modeName) {
        return InventoryMenuAPI.createItem()
                .setName("New Arena")
                .setDescription("Click to Create a New Arena")
                .setDisplayItem(Material.STICKY_PISTON)
                .setAction(cp2 -> cp2.getMenu().setInventoryMenuAnvil(InventoryMenuAPI.createAnvil()
                        .setTitle("Arena Name")
                        .setFailText("Name in use or invalid!")
                        .setSuccessFunc(str -> Arenas.get(str) == null)
                        .setAction((cp3, str) -> {
                            Arena arena = Arenas.createArena(str, str);
                            Arenas.addArenaMode(arena.getName(), BattleMode.get(modeName));
                        })
                        .setParentContainer((InventoryMenuContainerChest) cp2.getMenu().getInventoryMenuContainer())))
                .setCloseOnAction(false);
    }
    
    public static InventoryMenuItem createExistingItem(String modeName) {
        return InventoryMenuAPI.createItem()
                .setName("Add Existing Arena")
                .setDescription("Click to add an existing arena to this mode")
                .setDisplayItem(Material.STICKY_PISTON)
                .setAction(cp -> cp.getMenu().setInventoryMenuContainer(InventoryMenuAPI.createContainer()
                        .setTitle("Add Arena: " + modeName)
                        .setOpenAction((container, cp2) -> {
                            container.clearUnsorted();
                            for (Arena arena : Arenas.getAll().values()) {
                                container.addMenuItem(InventoryMenuAPI.createItem()
                                        .setName(arena.getName())
                                        .setDisplayItem(Material.SNOW_BLOCK)
                                        .setDescription("Click to add this arena")
                                        .setAction(cp3 -> Arenas.addArenaMode(arena.getName(), BattleMode.get(modeName)))
                                        .setCloseOnAction(false)
                                        .setParent((InventoryMenuContainerChest) cp2.getMenu().getInventoryMenuContainer()));
                            }
                        })
                        .setParent((InventoryMenuContainerChest) cp.getMenu().getInventoryMenuContainer())))
                .setCloseOnAction(false);
    }
    
    public static InventoryMenuItem createEditItem(Arena arena) {
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName(arena.getName() + " (" + arena.getName() + ")")
                .setDescription("Click to Edit")
                .setDisplayItem(Material.DIAMOND)
                .createLinkedContainer("Editing: " + arena.getName());
        
        menuItem.getLinkedChest().addMenuItem(InventoryMenuAPI.createItem()
                .setName("Rename")
                .setDescription("Click to Rename Arena")
                .setDisplayItem(Material.STICKY_PISTON)
                .setAction(cp2 -> cp2.getMenu().setInventoryMenuAnvil(InventoryMenuAPI.createAnvil()
                        .setTitle("Rename " + arena.getName())
                        .setFailText("Name in use or invalid!")
                        .setSuccessFunc(str -> Arenas.get(str) == null)
                        .setAction((cp3, str) -> {
                            Arenas.renameArena(arena.getName(), str);
                        })
                        .setParentContainer((InventoryMenuContainerChest) cp2.getMenu().getInventoryMenuContainer())))
                .setCloseOnAction(false));
    
        menuItem.getLinkedChest()
                .addMenuItem(InventoryMenuAPI.createItem()
                        .setName("Remove Borders")
                        .setDescription("")
                        .setDisplayItem(Material.PAPER)
                        .setAction(cp -> cp.getMenu().setInventoryMenuChest(InventoryMenuAPI.createContainer()
                                .setTitle(arena.getName() + " Borders")
                                .setRefreshAction((container, cp2) -> {
                                    container.clearUnsorted();
                                    int i = 0;
                                    for (Dimension border : arena.getBorders()) {
                                        int id = i;
                                        container.addMenuItem(InventoryMenuAPI.createItem()
                                                .setName("Remove Border")
                                                .setDisplayItem(Material.SPRUCE_LOG)
                                                .setDescription(border.getLow() + "\n" + border.getHigh())
                                                .setAction(cp3 -> arena.removeBorder(id))
                                                .setCloseOnAction(false));
                                        i++;
                                    }
                                })
                                .setParent(menuItem.getLinkedChest()), true))
                        .setCloseOnAction(false));
    
        menuItem.getLinkedChest()
                .addMenuItem(InventoryMenuAPI.createItem()
                        .setName("Remove Goals")
                        .setDescription("")
                        .setDisplayItem(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)
                        .setAction(cp -> cp.getMenu().setInventoryMenuChest(InventoryMenuAPI.createContainer()
                                .setTitle(arena.getName() + " Goals")
                                .setRefreshAction((container, cp2) -> {
                                    container.clearUnsorted();
                                    int i = 0;
                                    for (Dimension goal : arena.getGoals()) {
                                        int id = i;
                                        container.addMenuItem(InventoryMenuAPI.createItem()
                                                .setName("Remove Goal")
                                                .setDisplayItem(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)
                                                .setDescription(goal.getLow() + "\n" + goal.getHigh())
                                                .setAction(cp3 -> arena.removeGoal(id))
                                                .setCloseOnAction(false));
                                        i++;
                                    }
                                })
                                .setParent(menuItem.getLinkedChest()), true))
                        .setCloseOnAction(false));
    
        menuItem.getLinkedChest()
                .addMenuItem(InventoryMenuAPI.createItem()
                        .setName("Remove Checkpoints")
                        .setDescription("")
                        .setDisplayItem(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)
                        .setAction(cp -> cp.getMenu().setInventoryMenuChest(InventoryMenuAPI.createContainer()
                                .setTitle(arena.getName() + " Checkpoints")
                                .setRefreshAction((container, cp2) -> {
                                    container.clearUnsorted();
                                    int i = 0;
                                    for (Position pos : arena.getCheckpoints()) {
                                        int id = i;
                                        container.addMenuItem(InventoryMenuAPI.createItem()
                                                .setName("Remove Checkpoint")
                                                .setDisplayItem(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)
                                                .setDescription(pos.toString())
                                                .setAction(cp3 -> arena.removeCheckpoint(id))
                                                .setCloseOnAction(false));
                                        i++;
                                    }
                                })
                                .setParent(menuItem.getLinkedChest()), true))
                        .setCloseOnAction(false));
    
        menuItem.getLinkedChest()
                .addMenuItem(InventoryMenuAPI.createItem()
                        .setName("Remove Structures")
                        .setDescription("")
                        .setDisplayItem(Material.DARK_PRISMARINE_STAIRS)
                        .setAction(cp -> cp.getMenu().setInventoryMenuChest(InventoryMenuAPI.createContainer()
                                .setTitle(arena.getName() + " Structures")
                                .setRefreshAction((container, cp2) -> {
                                    container.clearUnsorted();
                                    for (String structure : arena.getStructureNames()) {
                                        container.addMenuItem(InventoryMenuAPI.createItem()
                                                .setName("Remove Structure")
                                                .setDisplayItem(Material.SPRUCE_LOG)
                                                .setDescription(structure)
                                                .setAction(cp3 -> arena.removeStructure(structure))
                                                .setCloseOnAction(false));
                                    }
                                })
                                .setParent(menuItem.getLinkedChest()), true))
                        .setCloseOnAction(false));
    
        menuItem.getLinkedChest()
                .addMenuItem(InventoryMenuAPI.createItem()
                        .setName("Remove Spawns")
                        .setDescription("")
                        .setDisplayItem(Material.RED_BED)
                        .setAction(cp -> cp.getMenu().setInventoryMenuChest(InventoryMenuAPI.createContainer()
                                .setTitle(arena.getName() + " Spawns")
                                .setRefreshAction((container, cp2) -> {
                                    container.clearUnsorted();
                                    int i = 0;
                                    for (Position pos : arena.getSpawns()) {
                                        int id = i;
                                        container.addMenuItem(InventoryMenuAPI.createItem()
                                                .setName("Remove Spawn")
                                                .setDisplayItem(Material.RED_BED)
                                                .setDescription(pos.toString())
                                                .setAction(cp3 -> arena.removeSpawn(id))
                                                .setCloseOnAction(false));
                                        i++;
                                    }
                                })
                                .setParent(menuItem.getLinkedChest()), true))
                        .setCloseOnAction(false));
        
        return menuItem;
    }
    
}
