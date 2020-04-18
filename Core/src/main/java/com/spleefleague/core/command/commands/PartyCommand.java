/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.ChatUtils;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.player.party.Party;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.request.PlayerRequest;

/**
 * @author NickM13
 */
public class PartyCommand extends CommandTemplate {
    
    public PartyCommand() {
        super(PartyCommand.class, "party", Rank.DEFAULT);
        setUsage("/party");
        setDescription("Party commands");
        
    }
    
    @CommandAnnotation
    public void party(CorePlayer sender) {
        sender.sendMessage(ChatUtils.centerChat("[ Party Commands ]"));
        sender.sendMessage(Chat.DEFAULT + "/party join <party>: Request to join a party");
        sender.sendMessage(Chat.DEFAULT + "/party create: Create a party");
        sender.sendMessage(Chat.DEFAULT + "/party invite <player>: Invite to your party");
        sender.sendMessage(Chat.DEFAULT + "/party kick <player>: Kick a player from your party");
        sender.sendMessage(Chat.DEFAULT + "/party leave: Leave your current party");
        sender.sendMessage(Chat.DEFAULT + "/party owner <player>: Transfer party ownership");
        sender.sendMessage(Chat.DEFAULT + "/party list : Get a list of players in your party");
    }
    
    @CommandAnnotation
    public void partyJoin(CorePlayer sender,
            @LiteralArg("join") String join,
            CorePlayer receiver) {
        Party party = receiver.getParty();
        if (party != null &&
                sender.getParty() == null) {
            success(sender, "You requested to join " + receiver.getDisplayName() + "'s party");
            /*
            ChatRequest.createRequest(sender.getName() + " wants to join your party", "Click to accept join request", "/party accept ", sender, receiver, (s, r) -> {
                party.add(s);
            }, 120);
            */
            Chat.sendRequest(sender.getDisplayName() + " wants to join your party!", receiver, sender, (r, s) -> {
                r.getParty().add(s);
            });
        }
    }
    
    @CommandAnnotation
    public void partyCreate(CorePlayer sender,
            @LiteralArg("create") String create) {
        Party party = sender.getParty();
        if (party == null) {
            Party.createParty(sender);
        } else {
            error(sender, "You're already in a party!");
        }
    }
    
    @CommandAnnotation
    public void partyInvite(CorePlayer sender,
            @LiteralArg("invite") String invite,
            CorePlayer receiver) {
        if (sender.equals(receiver)) {
            error(sender, "You can't invite yourself!");
            return;
        }
        if (sender.getParty() == null) {
            partyCreate(sender, "create");
        }
        Party party = sender.getParty();
        if (party.isOwner(sender)) {
            if (receiver.getParty() == null) {
                success(sender, "You send a party invite to " + receiver.getDisplayName());
                Chat.sendRequest(sender.getDisplayName() + " has invited you to a party!", receiver, sender, (r, s) -> {
                    s.getParty().add(r);
                });
            } else {
                error(sender, receiver.getDisplayName() + " is already in a party");
            }
        } else {
            error(sender, CoreError.PARTY_OWNER);
        }
    }
    
    @CommandAnnotation(minRank="DEVELOPER")
    public void partyForceInvite(CorePlayer sender,
            @LiteralArg(value="forceinvite") String invite,
            CorePlayer receiver) {
        if (sender.equals(receiver)) {
            error(sender, "You can't invite yourself!");
            return;
        }
        if (sender.getParty() == null) {
            partyCreate(sender, "create");
        }
        Party party = sender.getParty();
        if (party.isOwner(sender)) {
            if (receiver.getParty() == null) {
                success(sender, "You send a party invite to " + receiver.getDisplayName());
                party.add(receiver);
            } else {
                error(sender, receiver.getDisplayName() + " is already in a party");
            }
        } else {
            error(sender, CoreError.PARTY_OWNER);
        }
    }
    
    @CommandAnnotation
    public void partyKick(CorePlayer sender,
            @LiteralArg(value="kick") String kick,
            CorePlayer receiver) {
        if (sender.equals(receiver)) {
            error(sender, "You can't kick yourself!");
            return;
        }
        Party party = sender.getParty();
        if (party != null) {
            if (party.getOwner().equals(sender)) {
                if (!party.kick(receiver)) {
                    error(sender, receiver.getDisplayName() + " isn't in your party!");
                }
            } else {
                error(sender, CoreError.PARTY_OWNER);
            }
        } else {
            error(sender, CoreError.PARTY_NONE);
        }
    }
    
    @CommandAnnotation
    public void partyLeave(CorePlayer sender,
            @LiteralArg(value="leave") String leave) {
        Party party = sender.getParty();
        if (party != null) {
            party.leave(sender);
        } else {
            error(sender, CoreError.PARTY_NONE);
        }
    }
    
    @CommandAnnotation
    public void partyOwner(CorePlayer sender,
            @LiteralArg(value="owner") String owner,
            CorePlayer receiver) {
        Party party = sender.getParty();
        if (party != null) {
            if (party == receiver.getParty()) {
                if (party.isOwner(sender)) {
                    party.transferOwnership(receiver);
                } else {
                    error(sender, CoreError.PARTY_OWNER);
                }
            } else {
                error(sender, receiver.getDisplayName() + " isn't in the same party!");
            }
        } else {
            error(sender, CoreError.PARTY_NONE);
        }
    }
    
    @CommandAnnotation
    public void partyList(CorePlayer sender,
            @LiteralArg(value="list") String list) {
        Party party = sender.getParty();
        if (party != null) {
            sender.sendMessage(party.getPlayersFormatted());
        }
    }

}
