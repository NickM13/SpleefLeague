package com.spleefleague.proxycore.listener;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigotBundleIn;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.listener.spigot.battle.*;
import com.spleefleague.proxycore.listener.spigot.chat.*;
import com.spleefleague.proxycore.listener.spigot.friend.SpigotListenerFriend;
import com.spleefleague.proxycore.listener.spigot.party.SpigotListenerParty;
import com.spleefleague.proxycore.listener.spigot.player.*;
import com.spleefleague.proxycore.listener.spigot.queue.SpigotListenerQueueJoin;
import com.spleefleague.proxycore.listener.spigot.queue.SpigotListenerQueueLeave;
import com.spleefleague.proxycore.listener.spigot.queue.SpigotListenerQueueRequeue;
import com.spleefleague.proxycore.listener.spigot.removal.SpigotListenerRemovalCollectible;
import com.spleefleague.proxycore.listener.spigot.server.SpigotListenerServerDirect;
import com.spleefleague.proxycore.listener.spigot.server.SpigotListenerServerHub;
import com.spleefleague.proxycore.listener.spigot.server.SpigotListenerServerPing;
import com.spleefleague.proxycore.listener.spigot.ticket.SpigotListenerTicketClose;
import com.spleefleague.proxycore.listener.spigot.ticket.SpigotListenerTicketOpen;
import com.spleefleague.proxycore.listener.spigot.ticket.SpigotListenerTicketReply;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SpigotPluginListener implements Listener {

    private final Map<PacketType.Spigot, SpigotListener<?>> registeredListeners = new HashMap<>();

    public SpigotPluginListener() {
        ProxyCore.getInstance().getProxy().registerChannel("slcore:spigot");

        registeredListeners.put(PacketType.Spigot.BATTLE_END, new SpigotListenerBattleEnd());
        registeredListeners.put(PacketType.Spigot.BATTLE_SPECTATE, new SpigotListenerBattleSpectate());
        registeredListeners.put(PacketType.Spigot.BATTLE_CHALLENGE, new SpigotListenerBattleChallenge());
        registeredListeners.put(PacketType.Spigot.BATTLE_CHALLENGE_CONFIRM, new SpigotListenerBattleChallengeConfirm());
        registeredListeners.put(PacketType.Spigot.BATTLE_FORCE_START, new SpigotListenerBattleForceStart());
        registeredListeners.put(PacketType.Spigot.BATTLE_PING, new SpigotListenerBattlePing());
        registeredListeners.put(PacketType.Spigot.BATTLE_REJOIN, new SpigotListenerBattleRejoin());
        registeredListeners.put(PacketType.Spigot.CHAT_CHANNEL_JOIN, new SpigotListenerChatChannelJoin());
        registeredListeners.put(PacketType.Spigot.CHAT_CONSOLE, new SpigotListenerChatConsole());
        registeredListeners.put(PacketType.Spigot.CHAT_FRIEND, new SpigotListenerChatFriend());
        registeredListeners.put(PacketType.Spigot.CHAT_GROUP, new SpigotListenerChatGroup());
        registeredListeners.put(PacketType.Spigot.CHAT_PLAYER, new SpigotListenerChatPlayer());
        registeredListeners.put(PacketType.Spigot.CHAT_TELL, new SpigotListenerChatTell());
        registeredListeners.put(PacketType.Spigot.CHAT_BROADCAST, new SpigotListenerChatBroadcast());
        registeredListeners.put(PacketType.Spigot.FRIEND, new SpigotListenerFriend());
        registeredListeners.put(PacketType.Spigot.PARTY, new SpigotListenerParty());
        registeredListeners.put(PacketType.Spigot.PLAYER_COLLECTIBLE, new SpigotListenerPlayerCollectible());
        registeredListeners.put(PacketType.Spigot.PLAYER_COLLECTIBLE_SKIN, new SpigotListenerPlayerCollectibleSkin());
        registeredListeners.put(PacketType.Spigot.PLAYER_CRATE, new SpigotListenerPlayerCrate());
        registeredListeners.put(PacketType.Spigot.PLAYER_CURRENCY, new SpigotListenerPlayerCurrency());
        registeredListeners.put(PacketType.Spigot.PLAYER_INFRACTION, new SpigotListenerPlayerInfraction());
        registeredListeners.put(PacketType.Spigot.PLAYER_OPTIONS, new SpigotListenerPlayerOptions());
        registeredListeners.put(PacketType.Spigot.PLAYER_RANK, new SpigotListenerPlayerRank());
        registeredListeners.put(PacketType.Spigot.PLAYER_RATING, new SpigotListenerPlayerRating());
        registeredListeners.put(PacketType.Spigot.PLAYER_STATISTICS, new SpigotListenerPlayerStatistics());
        registeredListeners.put(PacketType.Spigot.QUEUE_JOIN, new SpigotListenerQueueJoin());
        registeredListeners.put(PacketType.Spigot.QUEUE_LEAVE, new SpigotListenerQueueLeave());
        registeredListeners.put(PacketType.Spigot.QUEUE_REQUEUE, new SpigotListenerQueueRequeue());
        registeredListeners.put(PacketType.Spigot.REMOVAL_COLLECTIBLE, new SpigotListenerRemovalCollectible());
        registeredListeners.put(PacketType.Spigot.SERVER_HUB, new SpigotListenerServerHub());
        registeredListeners.put(PacketType.Spigot.SERVER_DIRECT, new SpigotListenerServerDirect());
        registeredListeners.put(PacketType.Spigot.SERVER_PING, new SpigotListenerServerPing());
        registeredListeners.put(PacketType.Spigot.TICKET_CLOSE, new SpigotListenerTicketClose());
        registeredListeners.put(PacketType.Spigot.TICKET_OPEN, new SpigotListenerTicketOpen());
        registeredListeners.put(PacketType.Spigot.TICKET_REPLY, new SpigotListenerTicketReply());
    }

    @EventHandler
    public void onPluginMessageReceived(PluginMessageEvent event) {
        if (event.getTag().equalsIgnoreCase("slcore:spigot")) {
            try {
                PacketSpigotBundleIn packetBundle = new PacketSpigotBundleIn();
                packetBundle.fromByteArray(event.getData());
                for (PacketSpigot packet : packetBundle.packets) {
                    if (registeredListeners.containsKey(packet.getSpigotTag())) {
                        registeredListeners.get(packet.getSpigotTag()).receivePacket(event.getSender(), packet);
                    } else {
                        System.out.println("Received packet with no listener: " + packet.getSpigotTag());
                    }
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException exception) {
                exception.printStackTrace();
            }
        }
    }

}
