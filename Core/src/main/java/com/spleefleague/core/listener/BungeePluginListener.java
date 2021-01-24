package com.spleefleague.core.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.core.listener.bungee.listener.*;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.coreapi.utils.packet.PacketBungee;
import com.spleefleague.coreapi.utils.packet.PacketType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class BungeePluginListener implements PluginMessageListener {

    private Map<PacketType.Bungee, BungeeListener<?>> registeredListeners = new HashMap();

    public BungeePluginListener() {
        registeredListeners.put(PacketType.Bungee.BATTLE_SPECTATE, new BattleSpectateBungeeListener());
        registeredListeners.put(PacketType.Bungee.BATTLE_START, new BattleStartBungeeListener());
        registeredListeners.put(PacketType.Bungee.CHALLENGE, new ChallengeBungeeListener());
        registeredListeners.put(PacketType.Bungee.CHAT, new ChatBungeeListener());
        registeredListeners.put(PacketType.Bungee.CONNECTION, new ConnectionBungeeListener());
        registeredListeners.put(PacketType.Bungee.FRIEND, new FriendBungeeListener());
        registeredListeners.put(PacketType.Bungee.REFRESH_ALL, new RefreshAllBungeeListener());
        registeredListeners.put(PacketType.Bungee.REFRESH_QUEUE, new RefreshQueueBungeeListener());
        registeredListeners.put(PacketType.Bungee.REFRESH_SCORE, new RefreshScoreBungeeListener());
        registeredListeners.put(PacketType.Bungee.TELL, new TellBungeeListener());
        registeredListeners.put(PacketType.Bungee.SERVER_LIST, new ServerListBungeeListener());
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if (channel.equalsIgnoreCase("slcore:bungee")) {
            try {
                ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
                PacketType.Bungee tag = PacketType.Bungee.values()[input.readInt()];
                PacketBungee packet = tag.getClazz().getDeclaredConstructor().newInstance();
                packet.fromByteArray(input);
                registeredListeners.get(tag).receivePacket(player, packet);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException exception) {
                CoreLogger.logError(exception);
            }
        }
    }

}
