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

    public PersonalTablist(CorePlayer owner) {
        this.owner = owner;
        updateHeaderFooter();
    }

    public void addPlayer(CorePlayer cp) {
        targetList.put(cp.getUniqueId(), cp);
    }

    public void removePlayer(UUID uuid) {
        Core.sendPacketSilently(owner.getPlayer(), PacketUtils.createRemovePlayerPacket(Lists.newArrayList(uuid)), 0L);
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

}
