package com.spleefleague.proxycore.player;

import com.spleefleague.coreapi.party.Party;
import com.spleefleague.coreapi.party.PartyAction;
import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;
import com.spleefleague.coreapi.utils.packet.bungee.party.PacketBungeeParty;
import com.spleefleague.coreapi.utils.packet.bungee.refresh.PacketBungeeRefreshParty;
import com.spleefleague.proxycore.ProxyCore;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.*;

/**
 * @author NickM13
 * @since 6/22/2020
 */
public class ProxyParty extends Party {

    private final Set<ProxyCorePlayer> playerSet = new HashSet<>();
    private final Set<UUID> inviteRequests, joinRequests;

    public ProxyParty(UUID uuid) {
        super(uuid);

        this.inviteRequests = new HashSet<>();
        this.joinRequests = new HashSet<>();
    }

    @Override
    public void setOwner(UUID owner) {
        UUID prevOwner = owner;
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(owner);
        if (playerSet.contains(pcp)) {
            super.setOwner(owner);
            sendPacket(new PacketBungeeParty(PartyAction.TRANSFER, prevOwner, owner));
        }
    }

    public void sendPacket(PacketBungee packet, ProxyCorePlayer... additional) {
        Set<String> used = new HashSet<>();
        for (ProxyCorePlayer pcp : playerSet) {
            ServerInfo serverInfo = pcp.getCurrentServer();
            if (!used.contains(serverInfo.getName())) {
                used.add(serverInfo.getName());
                ProxyCore.getInstance().sendPacket(serverInfo, packet);
            }
        }
        for (ProxyCorePlayer pcp : additional) {
            ServerInfo serverInfo = pcp.getCurrentServer();
            if (!used.contains(serverInfo.getName())) {
                used.add(serverInfo.getName());
                ProxyCore.getInstance().sendPacket(serverInfo, packet);
            }
        }
    }

    public void sendPartyRefresh(ServerInfo serverInfo) {
        PacketBungeeRefreshParty packet = new PacketBungeeRefreshParty(playerList);
        ProxyCore.getInstance().sendPacket(serverInfo, packet);
    }

    public Set<ProxyCorePlayer> getPlayerSet() {
        return playerSet;
    }

    public boolean join(UUID sender, UUID target) {
        if (inviteRequests.contains(sender)) {
            addPlayer(sender);
            inviteRequests.remove(sender);
            return true;
        } else if (!joinRequests.contains(sender)) {
            joinRequests.add(sender);
            ProxyCore.getInstance().sendPacket(new PacketBungeeParty(PartyAction.JOIN, sender, target), sender, target);
        }
        return false;
    }

    public boolean invite(UUID sender, UUID target) {
        if (joinRequests.contains(target)) {
            addPlayer(target);
            joinRequests.remove(target);
            return true;
        } else if (!inviteRequests.contains(target)) {
            inviteRequests.add(target);
            ProxyCore.getInstance().sendPacket(new PacketBungeeParty(PartyAction.INVITE, sender, target), sender, target);
        }
        return false;
    }

    public void kick(UUID uuid) {
        if (removePlayer(uuid)) {
            ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(uuid);
            pcp.setParty(null);
            /*
            TextComponent text = new TextComponent("You were kicked from the party");
            ProxyCore.getInstance().sendMessage(pcp, text);
            */
            sendPacket(new PacketBungeeParty(PartyAction.KICK, uuid), pcp);
        }
    }

    public void leave(UUID uuid) {
        if (removePlayer(uuid)) {
            ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(uuid);
            pcp.setParty(null);
            /*
            TextComponent text = new TextComponent("You have left the party");
            ProxyCore.getInstance().sendMessage(pcp, text);
             */
            sendPacket(new PacketBungeeParty(PartyAction.LEAVE, uuid), pcp);
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
            sendPacket(new PacketBungeeParty(PartyAction.LEAVE, uuid));
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
        sendPartyRefresh(serverInfo);
    }

    @Override
    public void addPlayer(UUID uuid) {
        super.addPlayer(uuid);
        playerSet.add(ProxyCore.getInstance().getPlayers().get(uuid));
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(uuid);
        pcp.setParty(this);
        sendPacket(new PacketBungeeParty(PartyAction.ADD, owner, uuid));
    }

    @Override
    public boolean removePlayer(UUID uuid) {
        if (super.removePlayer(uuid)) {
            playerSet.remove(ProxyCore.getInstance().getPlayers().get(uuid));
            return true;
        }
        return false;
    }

}
