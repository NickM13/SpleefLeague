package com.spleefleague.core.player.scoreboard;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.spleefleague.core.Core;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.settings.Settings;
import com.spleefleague.core.util.PacketUtils;
import net.minecraft.server.v1_15_R1.EnumGamemode;
import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo;
import org.bukkit.ChatColor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class PersonalTablist {

    private final CorePlayer owner;
    private final List<UUID> currentList = new ArrayList<>();
    private final Map<UUID, CorePlayer> targetList = new HashMap<>();
    private boolean changed = true;

    public PersonalTablist(CorePlayer owner) {
        this.owner = owner;
        updateHeaderFooter();
    }

    public void addPlayer(CorePlayer cp) {
        targetList.put(cp.getUniqueId(), cp);
        changed = true;
    }

    public void removePlayer(UUID uuid) {
        changed = true;
        Core.sendPacketSilently(owner.getPlayer(), PacketUtils.createRemovePlayerPacket(Lists.newArrayList(uuid)), 0L);
        //updatePlayerList();
    }

    public void clear() {
        if (currentList.isEmpty()) return;
        Core.sendPacketSilently(owner.getPlayer(), PacketUtils.createRemovePlayerPacket(currentList), 0L);
        currentList.clear();
    }

    public void updateHeaderFooter() {
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
        packetContainer.getChatComponents().write(0, WrappedChatComponent.fromText(
                        ChatColor.GOLD + "" + ChatColor.BOLD + "SpleefLeague\n" +
                        ChatColor.GRAY + "Online: " + ChatColor.GREEN + Core.getInstance().getPlayers().getAllHere().size() + "\n" +
                        ChatColor.GRAY + "Ping: " + owner.getPingFormatted() + "\n" +
                        ChatColor.GRAY + "==================="));
        packetContainer.getChatComponents().write(1, WrappedChatComponent.fromText(
                        ChatColor.GRAY + "===================" + "\n" +
                        ChatColor.BLUE + Settings.getDiscord().getUrl()));
                                //+ "\n" + ChatColor.GRAY + "Twitch: " + ChatColor.DARK_PURPLE + "twitch.tv/wired26"));
        Core.sendPacketSilently(owner.getPlayer(), packetContainer, 1L);
    }

    public void updatePlayerList() {
        if (!changed) return;

        updateHeaderFooter();
        if (true) return;

        clear();
        try {
            PacketPlayOutPlayerInfo nmsPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
            Field playerListField = PacketPlayOutPlayerInfo.class.getDeclaredField("b");
            playerListField.setAccessible(true);
            List playerList = (List) playerListField.get(nmsPacket);
            for (CorePlayer cp : targetList.values()) {
                currentList.add(cp.getUniqueId());
                playerList.add(PacketPlayOutPlayerInfo.class.getDeclaredClasses()[0].getDeclaredConstructor(PacketPlayOutPlayerInfo.class, GameProfile.class, int.class, EnumGamemode.class, IChatBaseComponent.class)
                        .newInstance(nmsPacket, new GameProfile(cp.getDisguise(), cp.getNickname()), 0, EnumGamemode.ADVENTURE, IChatBaseComponent.ChatSerializer.a(WrappedChatComponent.fromText(cp.getTabName()).getJson())));
            }
            PacketContainer playerInfoPacket = new PacketContainer(PacketType.Play.Server.PLAYER_INFO, nmsPacket);

            Core.sendPacketSilently(owner.getPlayer(), playerInfoPacket, 1L);
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException exception) {
            CoreLogger.logError(exception);
        }
        updateHeaderFooter();
        changed = false;
    }

    public void refreshPlayers() {
        targetList.clear();
        for (CorePlayer cp : Core.getInstance().getPlayers().getAllOnline()) {
            targetList.put(cp.getUniqueId(), cp);
        }
        changed = true;
    }
}
