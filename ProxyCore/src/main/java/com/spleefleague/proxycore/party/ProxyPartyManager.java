package com.spleefleague.proxycore.party;

import com.spleefleague.coreapi.party.PartyAction;
import com.spleefleague.coreapi.party.PartyManager;
import com.spleefleague.coreapi.utils.packet.bungee.party.PacketBungeeParty;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.player.ProxyCorePlayer;

import java.util.*;

/**
 * @author NickM13
 * @since 6/22/2020
 */
public class ProxyPartyManager extends PartyManager<ProxyParty> {

    public void onDisconnect(UUID uuid) {
        ProxyParty party = partyMap.remove(uuid);

        if (party != null) {
            party.onDisconnect(uuid);
        }
    }

    public void onServerSwap(ProxyCorePlayer pcp) {
        ProxyParty party = partyMap.get(pcp.getUniqueId());

        if (party != null) {
            party.onServerSwap(pcp);
        }
    }

    public boolean onCreate(UUID owner) {
        if (super.create(new ProxyParty(owner), owner)) {
            ProxyCore.getInstance().getPacketManager().sendPacket(owner, new PacketBungeeParty(PartyAction.CREATE, owner));
            return true;
        }
        return false;
    }

    public void onJoin(UUID sender, UUID target) {
        ProxyParty senderParty = partyMap.get(sender);
        ProxyParty targetParty = partyMap.get(target);

        if (senderParty == null && targetParty != null) {
            if (targetParty.join(sender, target)) {
                partyMap.put(sender, targetParty);
            }
        }
    }

    public void onInvite(UUID sender, UUID target) {
        ProxyParty senderParty = partyMap.get(sender);
        ProxyParty targetParty = partyMap.get(target);

        if (senderParty != null && targetParty == null) {
            if (senderParty.invite(sender, target)) {
                partyMap.put(target, senderParty);
            }
        }
    }

    public void onKick(UUID sender, UUID target) {
        ProxyParty party = partyMap.remove(sender);

        if (party != null) {
            party.kick(target);
        }
    }

    public void onTransfer(UUID sender, UUID target) {
        super.transfer(sender, target);
    }

    public void onLeave(UUID sender) {
        ProxyParty party = partyMap.remove(sender);
        if (party != null) {
            party.leave(sender);
        }
    }

    public void onForceJoin(UUID sender, UUID target) {
        ProxyParty senderParty = partyMap.get(sender);
        ProxyParty targetParty = partyMap.get(target);

        if (targetParty != null && senderParty == null) {
            targetParty.addPlayer(sender);
            partyMap.put(sender, targetParty);
        }
    }

    public void onForceInvite(UUID sender, UUID target) {
        ProxyParty senderParty = partyMap.get(sender);
        ProxyParty targetParty = partyMap.get(target);

        if (senderParty != null && targetParty == null) {
            senderParty.addPlayer(target);
            partyMap.put(target, senderParty);
        }
    }

}
