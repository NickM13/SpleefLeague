package com.spleefleague.core.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.core.listener.bungee.battle.BungeeListenerBattleSpectate;
import com.spleefleague.core.listener.bungee.battle.BungeeListenerBattleStart;
import com.spleefleague.core.listener.bungee.battle.BungeeListenerBattleChallenge;
import com.spleefleague.core.listener.bungee.chat.BungeeListenerChat;
import com.spleefleague.core.listener.bungee.chat.BungeeListenerChatTell;
import com.spleefleague.core.listener.bungee.connection.BungeeListenerConnection;
import com.spleefleague.core.listener.bungee.friend.BungeeListenerFriend;
import com.spleefleague.core.listener.bungee.refresh.*;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;
import com.spleefleague.coreapi.utils.packet.bungee.PacketBungeeBundleIn;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class BungeePluginListener implements PluginMessageListener {

    private Map<PacketType.Bungee, BungeeListener<?>> registeredListeners = new HashMap();

    public BungeePluginListener() {
        registeredListeners.put(PacketType.Bungee.BATTLE_SPECTATE, new BungeeListenerBattleSpectate());
        registeredListeners.put(PacketType.Bungee.BATTLE_START, new BungeeListenerBattleStart());
        registeredListeners.put(PacketType.Bungee.BATTLE_CHALLENGE, new BungeeListenerBattleChallenge());
        registeredListeners.put(PacketType.Bungee.CHAT, new BungeeListenerChat());
        registeredListeners.put(PacketType.Bungee.CONNECTION, new BungeeListenerConnection());
        registeredListeners.put(PacketType.Bungee.FRIEND, new BungeeListenerFriend());
        registeredListeners.put(PacketType.Bungee.PARTY, new com.spleefleague.core.listener.bungee.party.BungeeListenerParty());
        registeredListeners.put(PacketType.Bungee.REFRESH_ALL, new BungeeListenerRefreshAll());
        registeredListeners.put(PacketType.Bungee.REFRESH_QUEUE, new BungeeListenerRefreshQueue());
        registeredListeners.put(PacketType.Bungee.REFRESH_PARTY, new BungeeListenerParty());
        registeredListeners.put(PacketType.Bungee.REFRESH_SCORE, new BungeeListenerRefreshScore());
        registeredListeners.put(PacketType.Bungee.CHAT_TELL, new BungeeListenerChatTell());
        registeredListeners.put(PacketType.Bungee.REFRESH_SERVER_LIST, new BungeeListenerRefreshServerList());
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bundleData) {
        if (channel.equalsIgnoreCase("slcore:bungee")) {
            try {
                PacketBungeeBundleIn packetBundle = new PacketBungeeBundleIn();
                packetBundle.fromByteArray(bundleData);
                for (PacketBungee packet : packetBundle.packets) {
                    registeredListeners.get(packet.getBungeeTag()).receivePacket(player, packet);
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException exception) {
                CoreLogger.logError(exception);
            }
        }
    }

}
