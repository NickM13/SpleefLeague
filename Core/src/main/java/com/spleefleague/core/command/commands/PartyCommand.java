/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.CorePlayerArg;
import com.spleefleague.core.command.annotation.HelperArg;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.menu.hotbars.main.profile.PartyMenu;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.coreapi.party.PartyAction;
import com.spleefleague.coreapi.utils.packet.spigot.party.PacketSpigotParty;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author NickM13
 */
public class PartyCommand extends CoreCommand {

    public PartyCommand() {
        super("p", CoreRank.DEFAULT);
        addAlias("party");
        setDescription("Party commands");
    }

    @CommandAnnotation(description = "Invites a player to your party.")
    public void partyInvite(CorePlayer sender,
                            @LiteralArg("invite") String l,
                            @CorePlayerArg(allowSelf = false, allowCrossServer = true) CorePlayer target) {
        Core.getInstance().sendPacket(new PacketSpigotParty(PartyAction.INVITE, sender.getUniqueId(), target.getUniqueId()));
    }

    @CommandAnnotation
    public void partyKick(CorePlayer sender,
                          @LiteralArg("kick") String l,
                          @CorePlayerArg(allowSelf = false, allowCrossServer = true) CorePlayer target) {
        Core.getInstance().sendPacket(new PacketSpigotParty(PartyAction.KICK, sender.getUniqueId(), target.getUniqueId()));
    }

    @CommandAnnotation(description = "Leaves your current party.")
    public void partyLeave(CorePlayer sender,
                           @LiteralArg("leave") String l) {
        Core.getInstance().sendPacket(new PacketSpigotParty(PartyAction.LEAVE, sender.getUniqueId()));
    }

    @CommandAnnotation(description = "Transfers the ownership of your party to another player.")
    public void partyTransfer(CorePlayer sender,
                              @LiteralArg("transfer") String l,
                              @CorePlayerArg(allowSelf = false, allowCrossServer = true) CorePlayer receiver) {
        Core.getInstance().sendPacket(new PacketSpigotParty(PartyAction.TRANSFER, sender.getUniqueId(), receiver.getUniqueId()));
    }

    @CommandAnnotation(description = "Lists the players currently in your party.")
    public void partyList(CorePlayer sender,
                          @LiteralArg("list") String list) {
        sender.getMenu().setInventoryMenuItem(PartyMenu.getItem());
    }

    @CommandAnnotation(description = "Removes all players from your party.")
    public void partyDisband(CorePlayer sender,
                             @LiteralArg("disband") String l) {
        Core.getInstance().sendPacket(new PacketSpigotParty(PartyAction.DISBAND, sender.getUniqueId()));
    }

    @CommandAnnotation(description = "Enables all future messages to be sent in party chat.")
    public void partyChat(CorePlayer sender,
                          @LiteralArg("chat") String l,
                          @Nullable @HelperArg("message") String message) {
        if (message == null) {
            if (sender.getChatChannel().equals(ChatChannel.PARTY)) {
                sender.setChatChannel(ChatChannel.GLOBAL);
            } else {
                sender.setChatChannel(ChatChannel.PARTY);
            }
        } else {
            Chat.sendMessage(sender, ChatChannel.PARTY, message);
        }
    }

    @CommandAnnotation(hidden = true)
    public void partyAccept(CorePlayer sender,
                            @LiteralArg("accept") String l,
                            String uuid) {
        Core.getInstance().sendPacket(new PacketSpigotParty(PartyAction.ACCEPT, sender.getUniqueId(), UUID.fromString(uuid)));
    }

    @CommandAnnotation(hidden = true)
    public void partyDecline(CorePlayer sender,
                             @LiteralArg("decline") String l,
                             String uuid) {
        Core.getInstance().sendPacket(new PacketSpigotParty(PartyAction.DECLINE, sender.getUniqueId(), UUID.fromString(uuid)));
    }

}
