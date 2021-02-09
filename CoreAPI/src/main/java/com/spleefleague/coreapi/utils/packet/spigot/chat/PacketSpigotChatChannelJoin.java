package com.spleefleague.coreapi.utils.packet.spigot.chat;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author NickM13
 * @since 2/1/2021
 */
public class PacketSpigotChatChannelJoin extends PacketSpigot {

    public UUID uuid;
    public String channelName;

    public PacketSpigotChatChannelJoin() { }

    public PacketSpigotChatChannelJoin(UUID uuid, String channelName) {
        this.uuid = uuid;
        this.channelName = channelName;
    }

    @Nonnull
    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.CHAT_CHANNEL_JOIN;
    }

}
