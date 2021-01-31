package com.spleefleague.core.player.party;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.party.PartyManager;
import com.spleefleague.coreapi.utils.packet.spigot.PacketParty;
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

    public void onRefresh(CorePlayer owner, List<UUID> players) {
        if (partyMap.containsKey(owner.getUniqueId())) {
            partyMap.get(owner.getUniqueId()).refresh(players);
        } else {
            super.create(new CoreParty(owner.getUniqueId()), owner.getUniqueId());
            CoreParty party = partyMap.get(owner.getUniqueId());
            if (party != null) {
                for (int i = 1; i < players.size(); i++) {
                    party.addPlayer(players.get(i));
                    partyMap.put(players.get(i), party);
                }
            } else {
                CoreLogger.logError("Error: Could not create a party for " + owner.getName() + " on refresh");
            }
        }
    }

    public void removeParty(CoreParty party) {
        for (CorePlayer cp : party.getPlayerSet()) {
            partyMap.remove(cp.getUniqueId());
            cp.leaveParty();
        }
    }

    public void onCreate(UUID owner) {
        if (super.create(new CoreParty(owner), owner)) {
            TextComponent text = new TextComponent("You have joined a party!");
            Core.getInstance().sendMessage(Core.getInstance().getPlayers().get(owner), text);
        }
    }

    public void onTransfer(UUID sender, UUID target) {
        super.transfer(sender, target);
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

    public void onKick(UUID sender) {
        CoreParty party = partyMap.remove(sender);

        if (party != null) {
            party.kick(sender);
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
            Core.getInstance().sendPacket(new PacketParty(PacketParty.PartyType.LEAVE, sender.getUniqueId()));
            return true;
        }
        return false;
    }

}
