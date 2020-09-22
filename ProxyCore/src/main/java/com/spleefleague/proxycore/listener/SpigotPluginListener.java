package com.spleefleague.proxycore.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.spleefleague.coreapi.utils.packet.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.listener.spigot.listener.*;
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

    private Map<PacketType.Spigot, SpigotListener<?>> registeredListeners = new HashMap();

    public SpigotPluginListener() {
        ProxyCore.getInstance().getProxy().registerChannel("slcore:spigot");

        registeredListeners.put(PacketType.Spigot.BATTLE_END_RATED, new BattleEndRatedSpigotListener());
        registeredListeners.put(PacketType.Spigot.BATTLE_END_UNRATED, new BattleEndUnratedSpigotListener());
        registeredListeners.put(PacketType.Spigot.BATTLE_SPECTATE, new BattleSpectateSpigotListener());
        registeredListeners.put(PacketType.Spigot.CHALLENGE, new ChallengeSpigotListener());
        registeredListeners.put(PacketType.Spigot.CHAT, new ChatSpigotListener());
        registeredListeners.put(PacketType.Spigot.FORCE_START, new ForceStartSpigotListener());
        registeredListeners.put(PacketType.Spigot.PARTY_CREATE, new PartyCreateSpigotListener());
        registeredListeners.put(PacketType.Spigot.PARTY_JOIN, new PartyJoinSpigotListener());
        registeredListeners.put(PacketType.Spigot.PARTY_LEAVE, new PartyLeaveSpigotListener());
        registeredListeners.put(PacketType.Spigot.QUEUE_JOIN, new QueueJoinSpigotListener());
        registeredListeners.put(PacketType.Spigot.QUEUE_LEAVE, new QueueLeaveSpigotListener());
        registeredListeners.put(PacketType.Spigot.REQUEUE, new RequeueSpigotListener());
        registeredListeners.put(PacketType.Spigot.HUB, new HubSpigotListener());
        registeredListeners.put(PacketType.Spigot.SET_RATING, new SetRatingSpigotListener());
        registeredListeners.put(PacketType.Spigot.TELL, new TellSpigotListener());
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
