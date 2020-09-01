package com.spleefleague.splegg;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.menu.hotbars.SLMainHotbar;
import com.spleefleague.core.player.PlayerManager;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.splegg.commands.SpleggCommand;
import com.spleefleague.splegg.commands.SpleggGunCommand;
import com.spleefleague.splegg.game.SpleggGun;
import com.spleefleague.splegg.game.SpleggMode;
import com.spleefleague.splegg.game.classic.ClassicSpleggArena;
import com.spleefleague.splegg.game.multi.MultiSpleggArena;
import com.spleefleague.splegg.player.SpleggPlayer;
import org.bukkit.ChatColor;
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
        addBattleManager(SpleggMode.CLASSIC.getBattleMode());
        //addBattleManager(SpleggMode.MULTI.getArenaMode());
        initMenu();
        initCommands();
    }

    @Override
    protected void close() {

    }
    
    /**
     * @return Chat Prefix
     */
    public String getChatPrefix() {
        return Chat.TAG_BRACE + "[" + Chat.TAG + "Splegg" + Chat.TAG_BRACE + "] " + Chat.DEFAULT;
    }
    
    public static Splegg getInstance() {
        return instance;
    }

    public InventoryMenuItem getSpleggMenu() {
        return spleggMenuItem;
    }

    private int getCurrentlyPlaying() {
        int playing = 0;
        for (SpleggMode mode : SpleggMode.values()) {
            for (Battle<?> battle : mode.getBattleMode().getOngoingBattles()) {
                playing += battle.getBattlers().size();
            }
        }
        return playing;
    }

    public void initMenu() {
        spleggMenuItem = InventoryMenuAPI.createItem()
                .setName(ChatColor.GOLD + "" + ChatColor.BOLD + "Splegg")
                .setDescription("Imagine the following description included the word egg in it somewhere." +
                        "\n\nA competitive gamemode in which you must knock your opponent into the water while avoiding a similar fate." +
                        "\n\nThis is not with any ordinary weapon; the weapon of choice is a shovel, and you must destroy the blocks underneath your foe!" +
                        "\n\n&7&lCurrently Playing: &6" + getCurrentlyPlaying())
                .setDisplayItem(Material.EGG)
                .createLinkedContainer("Splegg Menu");
        spleggMenuItem.getLinkedChest().addMenuItem(InventoryMenuUtils.createLockedMenuItem("Coming Soon!"), 0, 2);
        spleggMenuItem.getLinkedChest().addMenuItem(InventoryMenuUtils.createLockedMenuItem("Coming Soon!"), 1, 3);
        spleggMenuItem.getLinkedChest().addMenuItem(InventoryMenuUtils.createLockedMenuItem("Coming Soon!"), 2, 2);
        spleggMenuItem.getLinkedChest().addMenuItem(InventoryMenuUtils.createLockedMenuItem("TeamSplegg"), 3, 3);
        ClassicSpleggArena.createMenu(4, 2);
        spleggMenuItem.getLinkedChest().addMenuItem(InventoryMenuUtils.createLockedMenuItem("Multispleef!"), 5, 3);
        spleggMenuItem.getLinkedChest().addMenuItem(InventoryMenuUtils.createLockedMenuItem("Coming Soon!"), 6, 2);
        spleggMenuItem.getLinkedChest().addMenuItem(InventoryMenuUtils.createLockedMenuItem("Coming Soon!"), 7, 3);
        spleggMenuItem.getLinkedChest().addMenuItem(InventoryMenuUtils.createLockedMenuItem("Coming Soon!"), 8, 2);
        spleggMenuItem.getLinkedChest().addStaticItem(SpleggGun.createMenu(), 4, 4);
    
        SLMainHotbar.getItemHotbar().getLinkedChest().addMenuItem(spleggMenuItem, 5, 3);
    }

    protected void initCommands() {
        Core.getInstance().addCommand(new SpleggCommand());
        Core.getInstance().addCommand(new SpleggGunCommand());
    }

}
