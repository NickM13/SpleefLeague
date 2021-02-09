package com.spleefleague.proxycore.party;

import com.spleefleague.coreapi.party.PartyAction;
import com.spleefleague.coreapi.party.PartyManager;
import com.spleefleague.coreapi.utils.packet.bungee.party.PacketBungeeParty;
import com.spleefleague.coreapi.utils.packet.bungee.refresh.PacketBungeeRefreshParty;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.chat.ProxyChat;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.*;
import java.util.logging.Logger;

/**
 * @author NickM13
 * @since 6/22/2020
 */
public class ProxyPartyManager extends PartyManager<ProxyParty> {

    // 5 minutes
    private static final long INVITATION_DURATION = 1000 * 60 * 5;

    private static class PartyInvitation {

        long expireTime;

        public PartyInvitation() {
            refresh();
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }

        public void refresh() {
            this.expireTime = System.currentTimeMillis() + INVITATION_DURATION;
        }

    }

    private final Map<UUID, Map<UUID, PartyInvitation>> outgoingInvites = new HashMap<>();

    private enum PartyError {
        IN_PARTY(new TextComponent("You are already in a party")),
        NOT_OWNER(new TextComponent("You are not the party owner")),
        NO_PARTY(new TextComponent("You are not in a party")),
        NO_INVITE(new TextComponent("No pending invitation from them"));

        public TextComponent component;

        PartyError(TextComponent component) {
            this.component = component;
        }
    }

    public void onConnect(UUID uuid) {
        if (!outgoingInvites.containsKey(uuid)) {
            outgoingInvites.put(uuid, new HashMap<>());
        }
    }

    public void onDisconnect(UUID sender) {
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().getOffline(sender);
        if (pcp == null) {
            Logger.getGlobal().severe("ProxyPartyManager::onDisconnect");
            return;
        }
        ProxyParty party = partyMap.remove(sender);
        TextComponent component;
        if (party != null) {
            if (party.removePlayer(sender)) {
                if (party.getOwner().equals(sender)) {
                    if (party.getPlayerList().size() > 1) {
                        party.setOwner(party.getPlayerList().get(0));
                    }
                }
                ProxyCore.getInstance().getQueueManager().leaveAllQueues(party);
                if (!checkSize(party)) {
                    party.sendPacket(new PacketBungeeParty(PartyAction.LEAVE, sender));
                }

                component = new TextComponent();
                component.addExtra(ProxyCore.getInstance().getPlayers().getOffline(sender).getChatName());
                component.addExtra(" has left the party");
                party.sendMessage(component);
                party.sendPacket(new PacketBungeeParty(PartyAction.LEAVE, sender));
            }
        }

        outgoingInvites.remove(sender);
    }

    public ProxyParty getParty(ProxyCorePlayer pcp) {
        return partyMap.get(pcp.getUniqueId());
    }

    public ProxyParty getParty(UUID uuid) {
        return partyMap.get(uuid);
    }

    public void onServerSwap(ProxyCorePlayer pcp) {
        ProxyParty party = partyMap.get(pcp.getUniqueId());

        if (party != null) {
            party.sendPacket(party.getPartyRefresh());
        }
    }

    public void onInvite(UUID sender, UUID target) {
        ProxyCorePlayer pcpSender = ProxyCore.getInstance().getPlayers().get(sender);
        ProxyCorePlayer pcpTarget = ProxyCore.getInstance().getPlayers().get(target);
        if (pcpSender == null || pcpTarget == null) {
            Logger.getGlobal().severe("Something went wrong with a party invite!");
            return;
        }

        ProxyParty senderParty = partyMap.get(sender);
        ProxyParty targetParty = partyMap.get(target);

        TextComponent component;
        if (targetParty != null) {
            component = new TextComponent();
            component.addExtra(pcpTarget.getChatName());
            component.addExtra(" is already in a party");
            ProxyCore.getInstance().sendMessageError(pcpSender, component);
        } else {
            // Store player-specific invites
            // Send invite
            //if (!outgoingInvites.get(sender).containsKey(target) ||
            //        outgoingInvites.get(sender).get(target).isExpired()) {
                component = new TextComponent();
                component.addExtra(pcpSender.getChatName());
                component.addExtra(" has invited you to their party");
                ProxyCore.getInstance().sendMessage(pcpTarget, component);
                ProxyChat.sendConfirmationButtons(pcpTarget, "/party accept " + pcpSender.getUniqueId().toString(), "/party decline " + pcpSender.getUniqueId().toString());
            //}
            component = new TextComponent();
            component.addExtra("You have invited ");
            component.addExtra(pcpTarget.getChatName());
            component.addExtra(" to your party");
            ProxyCore.getInstance().sendMessage(pcpSender, component);
            outgoingInvites.get(sender).put(target, new PartyInvitation());
        }
    }

    public void onKick(UUID sender, UUID target) {
        ProxyCorePlayer pcpSender = ProxyCore.getInstance().getPlayers().get(sender);
        ProxyCorePlayer pcpTarget = ProxyCore.getInstance().getPlayers().get(target);
        if (pcpSender == null || pcpTarget == null) {
            Logger.getGlobal().severe("Something went wrong with a party kick!");
            return;
        }

        ProxyParty party = partyMap.get(sender);

        TextComponent component;

        if (party != null) {
            if (party.getOwner().equals(sender)) {
                if (party.removePlayer(target)) {
                    if (!checkSize(party)) {
                        party.sendPacket(new PacketBungeeParty(PartyAction.LEAVE, target));
                    }

                    ProxyCore.getInstance().getQueueManager().leaveAllQueues(party);
                    component = new TextComponent("You were kicked from the party");
                    ProxyCore.getInstance().sendMessage(pcpTarget, component);

                    component = new TextComponent("You have kicked ");
                    component.addExtra(pcpTarget.getChatName());
                    component.addExtra(" from the party");
                } else {
                    component = new TextComponent();
                    component.addExtra(pcpTarget.getChatName());
                    component.addExtra(" is not in your party");
                }
                ProxyCore.getInstance().sendMessage(pcpSender, component);
            } else {
                ProxyCore.getInstance().sendMessage(pcpSender, PartyError.NOT_OWNER.component);
            }
        } else {
            ProxyCore.getInstance().sendMessage(pcpSender, PartyError.NO_PARTY.component);
        }
    }

    public void onTransfer(UUID sender, UUID target) {
        ProxyCorePlayer pcpSender = ProxyCore.getInstance().getPlayers().get(sender);
        ProxyCorePlayer pcpTarget = ProxyCore.getInstance().getPlayers().get(target);
        if (pcpSender == null || pcpTarget == null) {
            Logger.getGlobal().severe("Something went wrong with a party kick!");
            return;
        }

        ProxyParty senderParty = partyMap.get(sender);
        ProxyParty targetParty = partyMap.get(target);

        TextComponent component;

        if (senderParty == null) {
            ProxyCore.getInstance().sendMessageError(pcpSender, PartyError.NO_PARTY.component);
        } else if (!senderParty.getOwner().equals(sender)) {
            ProxyCore.getInstance().sendMessageError(pcpSender, PartyError.NOT_OWNER.component);
        } else {
            if (senderParty == targetParty) {
                ProxyCore.getInstance().getQueueManager().leaveAllQueues(senderParty);
                senderParty.setOwner(target);

                component = new TextComponent();
                component.addExtra(pcpTarget.getChatName());
                component.addExtra(" is now the party owner");
                senderParty.sendMessage(component);
                senderParty.sendPacket(senderParty.getPartyRefresh());
            } else {
                component = new TextComponent();
                component.addExtra(pcpTarget.getChatName());
                component.addExtra(" is not in your party");
                ProxyCore.getInstance().sendMessageError(pcpSender, component);
            }
        }
    }

    public void onLeave(UUID sender) {
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(sender);
        if (pcp == null) {
            Logger.getGlobal().severe("ProxyPartyManager::onLeave");
            return;
        }
        ProxyParty party = partyMap.remove(sender);
        TextComponent component;
        if (party != null) {
            if (party.removePlayer(sender)) {
                if (party.getOwner().equals(sender)) {
                    if (party.getPlayerList().size() > 1) {
                        party.setOwner(party.getPlayerList().get(0));
                    }
                }
                ProxyCore.getInstance().getQueueManager().leaveAllQueues(party);
                if (!checkSize(party)) {
                    party.sendPacket(new PacketBungeeParty(PartyAction.LEAVE, sender));
                }
                ProxyCore.getInstance().sendMessage(pcp, new TextComponent("You have left the party"));

                component = new TextComponent();
                component.addExtra(ProxyCore.getInstance().getPlayers().getOffline(sender).getChatName());
                component.addExtra(" has left the party");
                party.sendMessage(component);
                party.sendPacket(new PacketBungeeParty(PartyAction.LEAVE, sender), pcp);
            }
        } else {
            ProxyCore.getInstance().sendMessageError(pcp, PartyError.NO_PARTY.component);
        }
    }

    private boolean checkSize(ProxyParty party) {
        if (party.getPlayerList().size() == 1) {
            onDisband(party.getPlayerList().get(0));
            return true;
        }
        return false;
    }

    public void onAccept(UUID sender, UUID target) {
        if (!outgoingInvites.containsKey(target)) return;

        TextComponent component;

        ProxyCorePlayer pcpSender = ProxyCore.getInstance().getPlayers().get(sender);
        ProxyCorePlayer pcpTarget = ProxyCore.getInstance().getPlayers().get(target);

        if (partyMap.containsKey(sender)) {
            ProxyCore.getInstance().sendMessageError(pcpSender, PartyError.IN_PARTY.component);
            return;
        }

        PartyInvitation invitation = outgoingInvites.get(target).remove(sender);
        if (invitation != null) {
            ProxyParty party = partyMap.get(target);
            if (party == null) {
                party = new ProxyParty(target);
                partyMap.put(target, party);
            }
            ProxyCore.getInstance().getQueueManager().leaveAllQueues(party);
            party.addPlayer(sender);
            party.sendPacket(party.getPartyRefresh());
            partyMap.put(sender, party);
            component = new TextComponent("You have joined ");
            component.addExtra(pcpTarget.getChatNamePossessive());
            component.addExtra(" party");
            ProxyCore.getInstance().sendMessage(pcpSender, component);
        } else {
            ProxyCore.getInstance().sendMessageError(pcpSender, PartyError.NO_INVITE.component);
        }
    }

    public void onDecline(UUID sender, UUID target) {
        if (!outgoingInvites.containsKey(target)) return;

        ProxyCorePlayer pcpSender = ProxyCore.getInstance().getPlayers().get(sender);
        ProxyCorePlayer pcpTarget = ProxyCore.getInstance().getPlayers().get(target);

        PartyInvitation invitation = outgoingInvites.get(target).remove(sender);
        TextComponent component;
        if (invitation != null) {
            component = new TextComponent();
            component.addExtra(pcpSender.getChatName());
            component.addExtra(" has declined your party invite");
            ProxyCore.getInstance().sendMessage(pcpTarget, component);

            component = new TextComponent();
            component.addExtra("You have declined ");
            component.addExtra(pcpTarget.getChatNamePossessive());
            component.addExtra(" party invite");
            ProxyCore.getInstance().sendMessage(pcpSender, component);
        } else {
            ProxyCore.getInstance().sendMessageError(pcpSender, PartyError.NO_INVITE.component);
        }
    }

    public void onDisband(UUID sender) {
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(sender);

        ProxyParty party = partyMap.get(sender);

        if (party == null) {
            ProxyCore.getInstance().sendMessageError(pcp, PartyError.NO_PARTY.component);
        } else {
            party.sendMessage(new TextComponent("Your party has been disbanded"));
            for (UUID uuid : party.getPlayerList()) {
                partyMap.remove(uuid);
            }
            ProxyCore.getInstance().getQueueManager().leaveAllQueues(party);
            party.sendPacket(new PacketBungeeParty(PartyAction.DISBAND, sender));
        }
    }

}
