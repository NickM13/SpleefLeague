package com.spleefleague.core.listener;

import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.core.listener.bungee.battle.BungeeListenerBattleSpectate;
import com.spleefleague.core.listener.bungee.battle.BungeeListenerBattleStart;
import com.spleefleague.core.listener.bungee.battle.BungeeListenerBattleChallenge;
import com.spleefleague.core.listener.bungee.connection.BungeeListenerConnection;
import com.spleefleague.core.listener.bungee.friend.BungeeListenerFriend;
import com.spleefleague.core.listener.bungee.party.BungeeListenerParty;
import com.spleefleague.core.listener.bungee.player.BungeeListenerPlayerResync;
import com.spleefleague.core.listener.bungee.refresh.*;
import com.spleefleague.core.listener.bungee.server.BungeeListenerServerKill;
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
        registeredListeners.put(PacketType.Bungee.CONNECTION, new BungeeListenerConnection());
        registeredListeners.put(PacketType.Bungee.FRIEND, new BungeeListenerFriend());
        registeredListeners.put(PacketType.Bungee.PARTY, new BungeeListenerParty());
        registeredListeners.put(PacketType.Bungee.PLAYER_RESYNC, new BungeeListenerPlayerResync());
        registeredListeners.put(PacketType.Bungee.REFRESH_ALL, new BungeeListenerRefreshAll());
        registeredListeners.put(PacketType.Bungee.REFRESH_QUEUE, new BungeeListenerRefreshQueue());
        registeredListeners.put(PacketType.Bungee.REFRESH_PARTY, new BungeeListenerRefreshParty());
        registeredListeners.put(PacketType.Bungee.REFRESH_SCORE, new BungeeListenerRefreshScore());
        registeredListeners.put(PacketType.Bungee.REFRESH_SERVER_LIST, new BungeeListenerRefreshServerList());
        registeredListeners.put(PacketType.Bungee.SERVER_KILL, new BungeeListenerServerKill());
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bundleData) {
        if (channel.equalsIgnoreCase("slcore:bungee")) {
            try {
                PacketBungeeBundleIn packetBundle = new PacketBungeeBundleIn();
                packetBundle.fromByteArray(bundleData);
                for (PacketBungee packet : packetBundle.packets) {
                    if (registeredListeners.containsKey(packet.getBungeeTag())) {
                        registeredListeners.get(packet.getBungeeTag()).receivePacket(player, packet);
                    } else {
                        System.out.println("Received packet with no listener: " + packet.getBungeeTag());
                    }
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException exception) {
                CoreLogger.logError(exception);
            }
        }
    }

}
