package com.spleefleague.splegg;

import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.PlayerManager;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.splegg.commands.SpleggCommand;
import com.spleefleague.splegg.game.SpleggGun;
import com.spleefleague.splegg.game.SpleggMode;
import com.spleefleague.splegg.game.classic.ClassicSpleggArena;
import com.spleefleague.splegg.game.multi.MultiSpleggArena;
import com.spleefleague.splegg.player.SpleggPlayer;
import org.bukkit.Material;

/**
 * @author NickM13
 * @since 4/16/2020
 */
public class Splegg extends CorePlugin<SpleggPlayer> {

    private static Splegg instance;

    private InventoryMenuItem spleggMenuItem;

    @Override
    protected void init() {
        instance = this;
        setPluginDB("Splegg");

        SpleggGun.init();

        playerManager = new PlayerManager<>(this, SpleggPlayer.class, getPluginDB().getCollection("Players"));

        SpleggMode.init();
        addBattleManager(SpleggMode.CLASSIC.getArenaMode());
        addBattleManager(SpleggMode.MULTI.getArenaMode());
        initMenu();
        initCommands();
    }

    @Override
    protected void close() {

    }

    public static Splegg getInstance() {
        return instance;
    }

    public InventoryMenuItem getSpleggMenu() {
        return spleggMenuItem;
    }

    public void initMenu() {
        spleggMenuItem = InventoryMenuAPI.createItem()
                .setName("Splegg")
                .setDescription("Imagine the following description included the word egg in it somewhere.\n\nA competitive gamemode in which you must knock your opponent into the water while avoiding a similar fate.\n\nThis is not with any ordinary weapon; the weapon of choice is a shovel, and you must destroy the blocks underneath your foe!")
                .setDisplayItem(Material.EGG)
                .createLinkedContainer("Splegg Menu");
        spleggMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createLockedMenuItem("Other"), 0, 2);
        spleggMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createLockedMenuItem("Other"), 1, 3);
        spleggMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createLockedMenuItem("Other"), 2, 2);
        spleggMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createLockedMenuItem("TeamSplegg"), 3, 3);
        ClassicSpleggArena.createMenu(4, 2);
        MultiSpleggArena.createMenu(5, 3);
        spleggMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createLockedMenuItem("Other"), 6, 2);
        spleggMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createLockedMenuItem("Other"), 7, 3);
        spleggMenuItem.getLinkedContainer().addMenuItem(InventoryMenuAPI.createLockedMenuItem("Other"), 8, 2);
        spleggMenuItem.getLinkedContainer().addStaticItem(SpleggGun.createMenu(), 4, 4);

        InventoryMenuAPI.getHotbarItem(InventoryMenuAPI.InvMenuType.SLMENU).getLinkedContainer().addMenuItem(spleggMenuItem, 5, 3);
    }

    protected void initCommands() {
        Core.getInstance().addCommand(new SpleggCommand());

        Core.getInstance().flushCommands();
    }

}
