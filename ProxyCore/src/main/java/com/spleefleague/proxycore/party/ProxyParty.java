package com.spleefleague.proxycore.party;

import com.spleefleague.coreapi.party.Party;
import com.spleefleague.coreapi.party.PartyAction;
import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;
import com.spleefleague.coreapi.utils.packet.bungee.party.PacketBungeeParty;
import com.spleefleague.coreapi.utils.packet.bungee.refresh.PacketBungeeRefreshParty;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.chat.ChatChannel;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.*;
import java.util.logging.Logger;

/**
 * @author NickM13
 * @since 6/22/2020
 */
public class ProxyParty extends Party {

    private final Set<ProxyCorePlayer> playerSet = new HashSet<>();

    public ProxyParty(UUID uuid) {
        super(uuid);
        addPlayer(uuid);
    }

    public void sendMessage(TextComponent text) {
        for (ProxyCorePlayer pcp : playerSet) {
            if (ChatChannel.PARTY.isActive(pcp)) {
                ProxyCore.getInstance().sendMessage(pcp, text);
            }
        }
    }

    @Override
    public void setOwner(UUID owner) {
        UUID prevOwner = this.owner;
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(owner);
        if (playerSet.contains(pcp)) {
            super.setOwner(owner);
        }
    }

    public boolean isOccupied(ServerInfo serverInfo) {
        for (ProxyCorePlayer pcp : playerSet) {
            ServerInfo serverInfo2 = pcp.getCurrentServer();
            if (!serverInfo.getName().equals(serverInfo2.getName())) {
                return true;
            }
        }
        return false;
    }

    public void sendPacket(PacketBungee packet, ProxyCorePlayer... additional) {
        Set<String> used = new HashSet<>();
        for (ProxyCorePlayer pcp : playerSet) {
            ServerInfo serverInfo = pcp.getCurrentServer();
            if (!used.contains(serverInfo.getName())) {
                used.add(serverInfo.getName());
                ProxyCore.getInstance().getPacketManager().sendPacket(serverInfo, packet);
            }
        }
        for (ProxyCorePlayer pcp : additional) {
            ServerInfo serverInfo = pcp.getCurrentServer();
            if (!used.contains(serverInfo.getName())) {
                used.add(serverInfo.getName());
                ProxyCore.getInstance().getPacketManager().sendPacket(serverInfo, packet);
            }
        }
    }

    public PacketBungeeRefreshParty getPartyRefresh() {
        return new PacketBungeeRefreshParty(playerList);
    }

    public Set<ProxyCorePlayer> getPlayerSet() {
        return playerSet;
    }

    public void kick(UUID uuid) {
        if (removePlayer(uuid)) {
            ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(uuid);
            /*
            TextComponent text = new TextComponent("You were kicked from the party");
            ProxyCore.getInstance().sendMessage(pcp, text);
            */
        }
    }

    public void leave(UUID uuid) {
        if (removePlayer(uuid)) {
            ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(uuid);
            /*
            TextComponent text = new TextComponent("You have left the party");
            ProxyCore.getInstance().sendMessage(pcp, text);
             */
            if (playerList.isEmpty()) return;
            if (uuid.equals(owner)) {
                setOwner(playerList.get(0));
            }
        }
    }

    public void onDisconnect(UUID uuid) {
        if (removePlayer(uuid)) {
            /*
            TextComponent text = new TextComponent("You have left the party");
            ProxyCore.getInstance().sendMessage(pcp, text);
             */
            if (playerList.isEmpty()) return;
            if (uuid.equals(owner)) {
                setOwner(playerList.get(0));
            }
        }
    }

    public void onServerSwap(ProxyCorePlayer pcp) {
        ServerInfo serverInfo = pcp.getCurrentServer();
        for (ProxyCorePlayer partyPlayer : playerSet) {
            if (pcp.equals(partyPlayer)) {
                continue;
            }
            if (partyPlayer.getCurrentServer().getName().equals(serverInfo.getName())) {
                return;
            }
        }
    }

    @Override
    public void addPlayer(UUID uuid) {
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(uuid);
        if (pcp == null) {
            Logger.getGlobal().severe("Error adding player to party! ProxyParty::addPlayer");
            return;
        }
        TextComponent component = new TextComponent();
        component.addExtra(pcp.getChatName());
        component.addExtra(" has joined the party");
        sendMessage(component);
        super.addPlayer(uuid);
        playerSet.add(pcp);
    }

    @Override
    public boolean removePlayer(UUID uuid) {
        if (super.removePlayer(uuid)) {
            if (uuid.equals(owner)) {
                if (playerList.size() > 1) {
                    owner = playerList.get(0);
                } else {

                }
            }
            ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().getOffline(uuid);
            playerSet.remove(pcp);
            return true;
        }
        return false;
    }

}