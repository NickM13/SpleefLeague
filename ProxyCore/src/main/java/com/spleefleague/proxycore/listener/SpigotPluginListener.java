package com.spleefleague.proxycore.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.listener.spigot.battle.*;
import com.spleefleague.proxycore.listener.spigot.chat.SpigotListenerChat;
import com.spleefleague.proxycore.listener.spigot.chat.SpigotListenerChatTell;
import com.spleefleague.proxycore.listener.spigot.friend.SpigotListenerFriend;
import com.spleefleague.proxycore.listener.spigot.party.SpigotListenerParty;
import com.spleefleague.proxycore.listener.spigot.player.SpigotListenerPlayerRating;
import com.spleefleague.proxycore.listener.spigot.queue.SpigotListenerQueueJoin;
import com.spleefleague.proxycore.listener.spigot.queue.SpigotListenerQueueLeave;
import com.spleefleague.proxycore.listener.spigot.queue.SpigotListenerQueueRequeue;
import com.spleefleague.proxycore.listener.spigot.server.SpigotListenerServerDirect;
import com.spleefleague.proxycore.listener.spigot.server.SpigotListenerServerHub;
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

    private final Map<PacketType.Spigot, SpigotListener<?>> registeredListeners = new HashMap();

    public SpigotPluginListener() {
        ProxyCore.getInstance().getProxy().registerChannel("slcore:spigot");

        registeredListeners.put(PacketType.Spigot.BATTLE_END_RATED, new SpigotListenerBattleEndRated());
        registeredListeners.put(PacketType.Spigot.BATTLE_END_UNRATED, new SpigotListenerBattleEndUnrated());
        registeredListeners.put(PacketType.Spigot.BATTLE_SPECTATE, new SpigotListenerBattleSpectate());
        registeredListeners.put(PacketType.Spigot.CHALLENGE, new SpigotListenerBattleChallenge());
        registeredListeners.put(PacketType.Spigot.CHAT, new SpigotListenerChat());
        registeredListeners.put(PacketType.Spigot.FORCE_START, new SpigotListenerBattleForceStart());
        registeredListeners.put(PacketType.Spigot.FRIEND, new SpigotListenerFriend());
        registeredListeners.put(PacketType.Spigot.PARTY, new SpigotListenerParty());
        registeredListeners.put(PacketType.Spigot.QUEUE_JOIN, new SpigotListenerQueueJoin());
        registeredListeners.put(PacketType.Spigot.QUEUE_LEAVE, new SpigotListenerQueueLeave());
        registeredListeners.put(PacketType.Spigot.REQUEUE, new SpigotListenerQueueRequeue());
        registeredListeners.put(PacketType.Spigot.HUB, new SpigotListenerServerHub());
        registeredListeners.put(PacketType.Spigot.SERVER_CONNECT, new SpigotListenerServerDirect());
        registeredListeners.put(PacketType.Spigot.SET_RATING, new SpigotListenerPlayerRating());
        registeredListeners.put(PacketType.Spigot.TELL, new SpigotListenerChatTell());
    }

    @EventHandler
    public void onPluginMessageReceived(PluginMessageEvent event) {
        if (event.getTag().equalsIgnoreCase("slcore:spigot")) {
            try {
                ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
                PacketType.Spigot tag = PacketType.Spigot.values()[input.readInt()];
                PacketSpigot packet = tag.getClazz().getDeclaredConstructor().newInstance();
                packet.fromByteArray(input);
                registeredListeners.get(tag).receivePacket(event.getSender(), packet);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException exception) {
                exception.printStackTrace();
            }
        }
    }

}
