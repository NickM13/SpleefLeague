/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.coreapi.utils.packet.spigot.battle.PacketSpigotBattleChallengeConfirm;

/**
 * @author NickM13
 */
public class ChallengeCommand extends CoreCommand {

    public ChallengeCommand() {
        super("challenge", CoreRank.DEFAULT);
    }

    @CommandAnnotation
    public void challengeAccept(CorePlayer sender,
                                @LiteralArg("accept") String l,
                                CorePlayer target) {
        Core.getInstance().sendPacket(new PacketSpigotBattleChallengeConfirm(
                sender.getUniqueId(), target.getUniqueId(),
                PacketSpigotBattleChallengeConfirm.Confirmation.ACCEPT));
    }

    @CommandAnnotation
    public void challengeDecline(CorePlayer sender,
                                 @LiteralArg("decline") String l,
                                 CorePlayer target) {
        Core.getInstance().sendPacket(new PacketSpigotBattleChallengeConfirm(
                sender.getUniqueId(), target.getUniqueId(),
                PacketSpigotBattleChallengeConfirm.Confirmation.DECLINE));
    }

}
