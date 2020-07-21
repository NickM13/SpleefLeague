/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;

/**
 * @author NickM13
 */
public class VanishCommand extends CoreCommand {
    
    public VanishCommand() {
        super("vanish", Rank.DEVELOPER);
    }
    
    @CommandAnnotation
    public void vanish(CorePlayer sender) {
        sender.setVanished(!sender.isVanished());
        if (sender.isVanished()) {
            success(sender, "Poof, you're gone!");
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
            packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            packet.getPlayerInfoDataLists().write(0, Lists.newArrayList(new PlayerInfoData(
                    WrappedGameProfile.fromPlayer(sender.getPlayer()),
                    1,
                    EnumWrappers.NativeGameMode.fromBukkit(sender.getPlayer().getGameMode()),
                    WrappedChatComponent.fromText(sender.getDisplayName()))));
            Core.sendPacketAll(packet);
        } else {
            success(sender, "Welcome to the real world!");
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
            packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
            packet.getPlayerInfoDataLists().write(0, Lists.newArrayList(new PlayerInfoData(
                    WrappedGameProfile.fromPlayer(sender.getPlayer()),
                    1,
                    EnumWrappers.NativeGameMode.fromBukkit(sender.getPlayer().getGameMode()),
                    WrappedChatComponent.fromText(sender.getDisplayName()))));
            Core.sendPacketAll(packet);
        }
        Core.getInstance().applyVisibilities(sender);
    }
    
}
