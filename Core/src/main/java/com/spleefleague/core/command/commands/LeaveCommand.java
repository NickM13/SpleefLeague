/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.Core;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.world.build.BuildWorld;
import com.spleefleague.coreapi.utils.packet.spigot.queue.PacketSpigotQueueLeave;

/**
 * @author NickM13
 */
public class LeaveCommand extends CoreCommand {
    
    public LeaveCommand() {
        super("leave", CoreRank.DEFAULT);
        addAlias("l");
        setUsage("/leave");
        setDescription("Leave all queues");
    }
    
    @CommandAnnotation
    public void leave(CorePlayer sender) {
        if (sender.isInBattle()) {
            sender.getBattle().leavePlayer(sender);
        } else if (BuildWorld.removePlayerGlobal(sender)) {
            success(sender, "You have left the build world");
        } else if (Core.getInstance().unqueuePlayerGlobally(sender)) {
            success(sender, "You have left all queues");
        } else {
            Core.getInstance().sendPacket(new PacketSpigotQueueLeave(sender.getPlayer().getUniqueId()));
        }
    }
    
}
