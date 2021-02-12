/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.coreapi.utils.packet.spigot.battle.PacketSpigotBattleRejoin;

/**
 * @author NickM13
 */
public class RejoinCommand extends CoreCommand {

    public RejoinCommand() {
        super("rejoin", CoreRank.DEFAULT);
    }

    @CommandAnnotation(description = "Attempts to rejoin the last game you were in")
    public void rejoin(CorePlayer sender) {
        Core.getInstance().sendPacket(new PacketSpigotBattleRejoin(sender.getUniqueId()));
    }

}
