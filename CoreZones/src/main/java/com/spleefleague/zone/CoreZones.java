package com.spleefleague.zone;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.menu.hotbars.main.HeldItemMenu;
import com.spleefleague.core.menu.hotbars.main.ProfileMenu;
import com.spleefleague.core.player.CoreDBPlayer;
import com.spleefleague.core.player.CorePlayerCollectibles;
import com.spleefleague.core.player.PlayerManager;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.zone.command.*;
import com.spleefleague.zone.fragments.FragmentManager;
import com.spleefleague.zone.gear.Gear;
import com.spleefleague.zone.gear.GearMenu;
import com.spleefleague.zone.listener.EditorListener;
import com.spleefleague.zone.listener.EnvironmentListener;
import com.spleefleague.zone.monuments.MonumentManager;
import com.spleefleague.zone.monuments.MonumentMenu;
import com.spleefleague.zone.player.ZonePlayer;
import com.spleefleague.zone.player.world.HarvestWorld;
import com.spleefleague.zone.zones.ZoneManager;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

/**
 * @author NickM13
 * @since 2/11/2021
 */
public class CoreZones extends CorePlugin {

    private static CoreZones instance;

    private final ZoneManager zoneManager = new ZoneManager();
    private final FragmentManager fragmentManager = new FragmentManager();
    private final MonumentManager monumentManager = new MonumentManager();

    private PlayerManager<ZonePlayer, CoreDBPlayer> playerManager;

    @Override
    protected void init() {
        instance = this;

        setPluginDB("Adventure");

        playerManager = new PlayerManager<>(this, ZonePlayer.class, CoreDBPlayer.class, getPluginDB().getCollection("Players"));
        playerManager.enableSaving();

        zoneManager.init();
        fragmentManager.init();
        monumentManager.init();

        Gear.init();
        HarvestWorld.init();

        HeldItemMenu.getItem().getLinkedChest().addMenuItem(GearMenu.getItem(), 3, 0);
        HeldItemMenu.getItem().getLinkedChest().addMenuItem(CorePlayerCollectibles.createActiveMenuItem(Gear.class), 3, 1);
        HeldItemMenu.getItem().getLinkedChest().addMenuItem(CorePlayerCollectibles.createToggleMenuItem(Gear.class), 3, 2);

        initListeners();
        initMenu();

        Bukkit.getScheduler().runTaskTimer(this, () -> playerManager.getAllLocal().forEach(ZonePlayer::updateWeather), 1, 1);
    }

    public static CoreZones getInstance() {
        return instance;
    }

    @Override
    protected void close() {
        Gear.close();

        zoneManager.close();
        fragmentManager.close();
        monumentManager.close();
    }

    public PlayerManager<ZonePlayer, CoreDBPlayer> getPlayers() {
        return playerManager;
    }

    private void initListeners() {
        Bukkit.getPluginManager().registerEvents(new EditorListener(), this);
        Bukkit.getPluginManager().registerEvents(new EnvironmentListener(), this);

        Core.getInstance().addCommand(new FragmentCommand());
        Core.getInstance().addCommand(new GearCommand());
        Core.getInstance().addCommand(new MonumentCommand());
        Core.getInstance().addCommand(new PlayerOptionsCommand());
        Core.getInstance().addCommand(new ZoneCommand());

        getServer().getMessenger().registerOutgoingPluginChannel(this, "slcore:zones");
    }

    private void initMenu() {
        ProfileMenu.getItem().getLinkedChest().addStaticItem(MonumentMenu.createMenu(), 5, 4);
    }

    public ZoneManager getZoneManager() {
        return zoneManager;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public MonumentManager getMonumentManager() {
        return monumentManager;
    }

    @Override
    public TextComponent getChatPrefix() {
        return new TextComponent(Chat.TAG_BRACE + "[" + Chat.TAG + "SL Zones" + Chat.TAG_BRACE + "] " + Chat.DEFAULT);
    }

}
