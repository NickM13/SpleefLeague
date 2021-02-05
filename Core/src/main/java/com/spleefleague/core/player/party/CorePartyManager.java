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
        System.out.println("Clearing");
        for (CorePlayer cp : party.getPlayerSet()) {
            System.out.println("Removing player " + cp.getName());
            partyMap.remove(cp.getUniqueId());
        }
    }

    public void onDisband(UUID sender) {
        System.out.println("Disbanding party of " + Core.getInstance().getPlayers().get(sender));
        if (partyMap.containsKey(sender)) {
            removeParty(partyMap.get(sender));
        }
    }

    public void onLeave(UUID sender) {
        CoreParty party = partyMap.remove(sender);

        if (party != null) {
            TextComponent text = new TextComponent();
            text.addExtra(Core.getInstance().getPlayers().getOffline(sender).getChatName());
            text.addExtra(" has left the party");
            party.sendMessage(text);

            party.leave(sender);
        }
    }

    public void onAdd(UUID sender, UUID target) {
        CoreParty party = partyMap.get(sender);

        if (party != null) {
            TextComponent text = new TextComponent("You have joined a party!");
            Core.getInstance().sendMessage(Core.getInstance().getPlayers().get(target), text);

            party.addPlayer(target);
            partyMap.put(target, party);
        }
    }

    @Deprecated
    public void onKick(UUID sender) {
        CoreParty party = partyMap.remove(sender);

        if (party != null) {
            party.leave(sender);
        }
    }

    public void onJoin(UUID sender, UUID target) {
        CorePlayer cpTarget = Core.getInstance().getPlayers().get(target);
        CorePlayer cpSender = Core.getInstance().getPlayers().get(sender);
        if (cpTarget.getOnlineState() == DBPlayer.OnlineState.HERE) {
            TextComponent text = new TextComponent();
            text.addExtra(cpSender.getChatName());
            text.addExtra(" would like to join the party");
            Core.getInstance().sendMessage(cpTarget, text);

            text = new TextComponent();
            text.addExtra("To confirm, type /party invite " + cpSender.getName());
            Core.getInstance().sendMessage(cpTarget, text);
        }

        if (cpSender.getOnlineState() == DBPlayer.OnlineState.HERE) {
            TextComponent text = new TextComponent();
            text.addExtra("Requested to join ");
            text.addExtra(cpTarget.getChatNamePossessive());
            text.addExtra(" party");
            Core.getInstance().sendMessage(cpSender, text);
        }
    }

    public void onInvite(UUID sender, UUID target) {
        CorePlayer cpTarget = Core.getInstance().getPlayers().get(target);
        CorePlayer cpSender = Core.getInstance().getPlayers().get(sender);
        if (cpSender.getOnlineState() == DBPlayer.OnlineState.HERE) {
            TextComponent text = new TextComponent();
            text.addExtra("Party invite sent to ");
            text.addExtra(cpTarget.getChatName());
            Core.getInstance().sendMessage(cpSender, text);
        }

        if (cpTarget.getOnlineState() == DBPlayer.OnlineState.HERE) {
            TextComponent text = new TextComponent();
            text.addExtra("Invite to ");
            text.addExtra(cpSender.getChatNamePossessive());
            text.addExtra(" party");
            Core.getInstance().sendMessage(cpTarget, text);

            text = new TextComponent();
            text.addExtra("To join, type /party join " + cpSender.getName());
            Core.getInstance().sendMessage(cpTarget, text);
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
