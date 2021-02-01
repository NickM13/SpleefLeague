/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.CorePlayerArg;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.menu.hotbars.main.profile.PartyMenu;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.coreapi.party.PartyAction;
import com.spleefleague.coreapi.utils.packet.spigot.party.PacketSpigotParty;

/**
 * @author NickM13
 */
public class PartyCommand extends CoreCommand {
    
    public PartyCommand() {
        super("party", CoreRank.DEFAULT);
        setUsage("/party");
        setDescription("Party commands");
    }
    
    @CommandAnnotation
    public void partyJoin(CorePlayer sender,
            @LiteralArg("join") String l,
            @CorePlayerArg(allowSelf = false) CorePlayer target) {
        Core.getInstance().sendPacket(new PacketSpigotParty(PartyAction.JOIN, sender.getUniqueId(), target.getUniqueId()));
    }
    
    @CommandAnnotation
    public void partyCreate(CorePlayer sender,
            @LiteralArg("create") String l) {
        Core.getInstance().sendPacket(new PacketSpigotParty(PartyAction.CREATE, sender.getUniqueId()));
    }
    
    @CommandAnnotation
    public void partyInvite(CorePlayer sender,
            @LiteralArg("invite") String l,
            @CorePlayerArg(allowSelf = false) CorePlayer target) {
        Core.getInstance().sendPacket(new PacketSpigotParty(PartyAction.INVITE, sender.getUniqueId(), target.getUniqueId()));
    }

    @CommandAnnotation(minRank = "DEVELOPER")
    public void partyForceInvite(CorePlayer sender,
                                 @LiteralArg("forceinvite") String l,
                                 CorePlayer target) {
        Core.getInstance().sendPacket(new PacketSpigotParty(PartyAction.INVITE_FORCE, sender.getUniqueId(), target.getUniqueId()));
    }

    @CommandAnnotation(minRank = "DEVELOPER")
    public void partyForceJoin(CorePlayer sender,
                               @LiteralArg("forcejoin") String l,
                               CorePlayer target) {
        Core.getInstance().sendPacket(new PacketSpigotParty(PartyAction.JOIN_FORCE, sender.getUniqueId(), target.getUniqueId()));
    }
    
    @CommandAnnotation
    public void partyKick(CorePlayer sender,
                          @LiteralArg("kick") String l,
                          @CorePlayerArg(allowSelf = false) CorePlayer target) {
        Core.getInstance().sendPacket(new PacketSpigotParty(PartyAction.KICK, sender.getUniqueId(), target.getUniqueId()));
    }
    
    @CommandAnnotation
    public void partyLeave(CorePlayer sender,
                           @LiteralArg("leave") String l) {
        Core.getInstance().sendPacket(new PacketSpigotParty(PartyAction.LEAVE, sender.getUniqueId()));
    }
    
    @CommandAnnotation
    public void partyOwner(CorePlayer sender,
            @LiteralArg("transfer") String l,
            @CorePlayerArg(allowSelf = false) CorePlayer receiver) {
        Core.getInstance().sendPacket(new PacketSpigotParty(PartyAction.TRANSFER, sender.getUniqueId()));
    }
    
    @CommandAnnotation
    public void partyList(CorePlayer sender,
            @LiteralArg("list") String list) {
        sender.getMenu().setInventoryMenuItem(PartyMenu.getItem());
    }
    
}
