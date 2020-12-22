package com.spleefleague.core.player.scoreboard;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.mojang.authlib.GameProfile;
import com.spleefleague.core.Core;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.PacketUtils;
import com.spleefleague.coreapi.utils.packet.PacketUtil;
import net.minecraft.server.v1_15_R1.EnumGamemode;
import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PersonalTablist {

    private static final int SIZE = 10;

    private final CorePlayer owner;
    private final List<UUID> tabUuidList = new ArrayList<>();

    public PersonalTablist(CorePlayer owner) {
        this.owner = owner;
        //update();
    }

    public void addPlayer(CorePlayer cp) {
        tabUuidList.add(cp.getUniqueId());
        update();
    }

    public void removePlayer(UUID uuid) {
        if (tabUuidList.remove(uuid)) {
            update();
        }
    }

    public void clear() {
        if (tabUuidList.isEmpty()) return;
        Core.sendPacketSilently(owner.getPlayer(), PacketUtils.createRemovePlayerPacket(tabUuidList), 0L);
        tabUuidList.clear();
    }

    public void update() {
        clear();
        try {
            PacketPlayOutPlayerInfo nmsPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
            Field playerListField = PacketPlayOutPlayerInfo.class.getDeclaredField("b");
            playerListField.setAccessible(true);
            List playerList = (List) playerListField.get(nmsPacket);
            for (CorePlayer cp : Core.getInstance().getPlayers().getOnline()) {
                tabUuidList.add(cp.getUniqueId());
                playerList.add(PacketPlayOutPlayerInfo.class.getDeclaredClasses()[0].getDeclaredConstructor(PacketPlayOutPlayerInfo.class, GameProfile.class, int.class, EnumGamemode.class, IChatBaseComponent.class)
                        .newInstance(nmsPacket, new GameProfile(cp.getUniqueId(), cp.getName()), 0, EnumGamemode.ADVENTURE, IChatBaseComponent.ChatSerializer.a(WrappedChatComponent.fromText(cp.getTabName()).getJson())));
            }
            PacketContainer playerInfoPacket = new PacketContainer(PacketType.Play.Server.PLAYER_INFO, nmsPacket);

            Core.sendPacketSilently(owner.getPlayer(), playerInfoPacket, 1L);
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException exception) {
            CoreLogger.logError(exception);
        }
    }

}
