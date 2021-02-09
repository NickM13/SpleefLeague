package com.spleefleague.core.player.party;

import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.party.PartyAction;
import com.spleefleague.coreapi.party.PartyManager;
import com.spleefleague.coreapi.utils.packet.spigot.party.PacketSpigotParty;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 */
public class CorePartyManager extends PartyManager<CoreParty> {

    public void onConnect(CorePlayer cp) {
        CoreParty party = partyMap.get(cp.getUniqueId());
        if (party != null) {
            party.addLocal(cp);
        }
    }

    public void onDisconnect(CorePlayer cp) {
        CoreParty party = partyMap.get(cp.getUniqueId());
        if (party != null) {
            party.removeLocal(cp);
        }
    }

    public CoreParty getParty(CorePlayer cp) {
        return partyMap.get(cp.getUniqueId());
    }

    public void onRefresh(CorePlayer owner, List<UUID> players) {
        if (partyMap.containsKey(owner.getUniqueId())) {
            for (UUID uuid : partyMap.get(owner.getUniqueId()).getPlayerList()) {
                partyMap.remove(uuid);
            }
        }
        CoreParty party = new CoreParty(owner.getUniqueId(), players);
        for (UUID player : players) {
            partyMap.put(player, party);
        }
    }

    public void removeParty(CoreParty party) {
        for (CorePlayer cp : party.getPlayerSet()) {
            partyMap.remove(cp.getUniqueId());
        }
    }

    public void onDisband(UUID sender) {
        if (partyMap.containsKey(sender)) {
            removeParty(partyMap.get(sender));
        }
    }

    public void onLeave(UUID sender) {
        CoreParty party = partyMap.remove(sender);

        if (party != null) {
            party.leave(sender);
        }
    }

    @Deprecated
    public void onKick(UUID sender) {
        CoreParty party = partyMap.remove(sender);

        if (party != null) {
            party.leave(sender);
        }
    }

    public boolean leave(CorePlayer sender) {
        if (sender.getParty() != null) {
            sender.getParty().removePlayer(sender.getUniqueId());
            Core.getInstance().sendPacket(new PacketSpigotParty(PartyAction.LEAVE, sender.getUniqueId()));
            return true;
        }
        return false;
    }

}
