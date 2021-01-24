package com.spleefleague.core.command.commands;

import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.coreapi.utils.packet.spigot.PacketHub;
import com.spleefleague.coreapi.utils.packet.spigot.PacketServerConnect;

public class ServersCommand extends CoreCommand {

    public ServersCommand() {
        super("servers", Rank.MODERATOR);
        setOptions("serverNames", pi -> Core.getInstance().getServers());
    }

    @CommandAnnotation
    public void hub(CorePlayer sender,
                    @OptionArg(listName = "serverNames", force = false) String serverName) {
        Core.getInstance().sendPacket(new PacketServerConnect(sender.getUniqueId(), serverName));
    }

}
